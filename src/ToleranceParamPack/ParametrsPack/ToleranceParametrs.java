package ToleranceParamPack.ParametrsPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import Exceptions.SavingException;
import NewElementPack.NewElementController;
import ToleranceParamPack.StrategyPack.StrategyOfSuitability;
import VerificationPack.MeasResult;

public class ToleranceParametrs implements Includable<Element>, dbStorable {
	 private int id;
	 public int getId() {
		 return this.id;
	 }
	 public Map<String, Map<Double, Double>> values;
	 public ArrayList<Double> freqs;
	 private String tableName;
	 public String getTableName(){
		 return this.tableName;
	 }
	 
	 public void setTableName(){
		 String addrStr = myElement.getMyOwner().getName() + " " +
				 		  myElement.getMyOwner().getType() + " " +
				 		  myElement.getMyOwner().getSerialNumber() + " " +
				 		  myElement.getType() + " " + myElement.getSerialNumber();
		 tableName = "ѕараметры допуска " + timeType.getTableNamePart() + " поверки " + measUnitPart.getTableNamePart() + " S параметров дл€ " + addrStr;
	 }
	 
	 public TimeType timeType;
	 public MeasUnitPart measUnitPart;
//Constructors
	 ToleranceParametrs(){
		values = new LinkedHashMap<String, Map<Double, Double>>();
		freqs = new ArrayList<Double>();
	 }	 
//GUI
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, NewElementController elCtrl, Element ownerElement){		
		this();
		myElement = ownerElement;
		timeType = currentTimeType;
		measUnitPart = currentUnitPart;		
		freqs = elCtrl.getFreqsValues();
		values = elCtrl.getToleranceParamsValues(timeType, measUnitPart);
	}
//DataBase
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, Element ownerElement) throws SQLException {
		this();	
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;
		System.out.println("\n ритерий годности дл€ " + this.timeType + " " + this.measUnitPart);
		//Get id of this complex of tables
		String sqlQuery = "SELECT id FROM [Tolerance_params] WHERE ElementId=" + this.myElement.getId();
		System.out.println("ѕолучаем id данного критери€ годности запросом:\n" + sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		
		//Get table name for this type of tolernce (currentTimeType + currentUnitPart) 
		String fieldName = this.timeType + "_" + this.measUnitPart + "_table";
		sqlQuery = "SELECT " + fieldName + " FROM [Tolerance_params] WHERE id=" + this.id;
		System.out.println("ѕолучаем им€ таблицы, в которой хран€тс€ значени€ критери€ годности запросом:\n" + sqlQuery);
		List<String> arrayResults = new ArrayList<>();
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		this.tableName = arrayResults.get(0);
				
		ArrayList<String> fieldsNames = new ArrayList<String>();
		fieldsNames.add("freq");
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			fieldsNames.add("DOWN_" + measUnitPart + "_" + S_Parametr.values()[i]);
			fieldsNames.add("UP_" + measUnitPart + "_" + S_Parametr.values()[i]);
		}
		
		sqlQuery = "SELECT ";
		int stop = fieldsNames.size();
		for (int i = 0; i < stop; i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != stop - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM [" + this.tableName +"]";
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, values);
			
		sqlQuery = "SELECT freq FROM [" + this.tableName + "]";
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", freqs);		
	}
//interface dbStorable		    
	 @Override
	 public void saveInDB() throws SQLException, SavingException {
		 String identStr = this.myElement.getId() + "_" + (new Date().toString());
		 String sqlQuery = "UPDATE [Tolerance_params] SET "; 
		 String fieldName = this.timeType + "_" + this.measUnitPart + "_" + "table";
		 this.tableName = this.timeType + "_" + this.measUnitPart + "_table_" + identStr;
		 sqlQuery += (fieldName + "='" + this.tableName + "'");	
		 sqlQuery += " WHERE ElementId=" + this.myElement.getId() + "";
		 System.out.println(sqlQuery);
		 DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);		 
		 sqlQuery = "CREATE TABLE [" + this.tableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq REAL, ";
		 for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			 sqlQuery += ("DOWN_" + measUnitPart + "_" + S_Parametr.values()[i] + " REAL, ");
			 sqlQuery += ("UP_" + measUnitPart + "_" + S_Parametr.values()[i] + " REAL");
			 if (i != myElement.getSParamsCout() - 1) sqlQuery += ", ";
		 }
		 sqlQuery += ")";
		 DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		 
		 //Inserting values of parametrs
		 for (int i = 0; i < freqs.size(); i++) {			
			sqlQuery = "INSERT INTO [" + this.tableName + "] (freq, ";						
			for (int j = 0; j < myElement.getSParamsCout(); j++) {
				sqlQuery += ("DOWN_" + measUnitPart + "_" + S_Parametr.values()[j] + ", ");
				sqlQuery += ("UP_" + measUnitPart + "_" + S_Parametr.values()[j]);
				if (j !=  myElement.getSParamsCout() - 1) sqlQuery += ", ";
			}
			sqlQuery += ") values ('"+freqs.get(i)+"', ";		
			for (int j = 0; j < myElement.getSParamsCout(); j++) {
				String key = "DOWN_" + measUnitPart + "_" + S_Parametr.values()[j];
				sqlQuery += ("'"+values.get(key).get(freqs.get(i)).toString()+"', ");
				key = "UP_" + measUnitPart + "_" + S_Parametr.values()[j];
				sqlQuery += ("'"+values.get(key).get(freqs.get(i)).toString()+"'");
				if (j != myElement.getSParamsCout() - 1) sqlQuery += ", ";
			}				
			sqlQuery += ")";
			try {
				DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			} 
			catch (SQLException sqlExp) {
				throw new SavingException("Ќе удалось сохранить значени€ параметров на частоте\n ." +
					freqs.get(i).toString() + " дл€ " +
					measUnitPart.getTableNamePart() + " S параметра при " +
					timeType.getTableNamePart() + " поверке" );
			}
		 }		
	 }
	 	 
	 @Override
	 public void deleteFromDB() throws SQLException {
		String sqlQuery = "DROP TABLE [" + this.tableName + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	 }
	 @Override
	 public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		 // TODO Auto-generated method stub
	 }
	 
	 public void rewriteTableNames() throws SQLException {
		 String newTableName = "ѕараметры допуска " + timeType.getTableNamePart() + " поверки " + measUnitPart.getTableNamePart() + " S параметров дл€ " + myElement.getMyOwner().getName() + " " +
		 		  			   myElement.getMyOwner().getType() + " " + myElement.getMyOwner().getSerialNumber() + " " + myElement.getType() + " " + myElement.getSerialNumber();
		 
		 String elementsOfTableName = myElement.getMyOwner().getElementsTableName();
		 String sqlQuery = "UPDATE [" + elementsOfTableName + "] SET " + getFieldInElementsOfTable() + "='" + newTableName + "' "
		 				 + "WHERE ElementType='" + myElement.getType() + "' AND ElementSerNumber='" + myElement.getSerialNumber() + "'";
		 DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		 sqlQuery = "ALTER TABLE [" + tableName + "] RENAME TO [" + newTableName + "]";
		 DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		 tableName = newTableName;
	 }
	 
	 private Element myElement; 
	 @Override
	 public Element getMyOwner() {
	 	return myElement;
	 }
	 @Override
	 public void onAdding(Element Owner) {
	 	this.myElement = Owner;
	 	setTableName();
	 }
	 private StrategyOfSuitability strategy;
	 public void setStrategy(StrategyOfSuitability anyStrategy) {
		this.strategy = anyStrategy;
	 }
	 public boolean checkResult(MeasResult result) {
		return this.strategy.checkResult(result, this);
	 }
	 	 
	 private String getFieldInElementsOfTable() {
		 String result = null;
		 if (timeType.equals(TimeType.PRIMARY)) 
			 result = "Primary";
		 else
			 result = "Periodic";
		 
		 if (measUnitPart.equals(MeasUnitPart.MODULE))
			 result += "Module";
		 else
			 result += "Phase";
		 
		 result += "ParamTable";
		 return result;
	 }
}