package ToleranceParamPack.ParametrsPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
		 tableName = "Параметры допуска " + timeType.getTableNamePart() + " поверки " + measUnitPart.getTableNamePart() + " S параметров для " + addrStr;
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
		myElement = ownerElement;
		timeType = currentTimeType;
		measUnitPart = currentUnitPart;
		setTableName();	
		
		ArrayList<String> fieldsNames = new ArrayList<String>();
		fieldsNames.add("freq");
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			fieldsNames.add("DOWN_" + measUnitPart + "_" + S_Parametr.values()[i]);
			fieldsNames.add("UP_" + measUnitPart + "_" + S_Parametr.values()[i]);
		}
		
		String sqlQuery = "SELECT ";
		int stop = fieldsNames.size();
		for (int i = 0; i < stop; i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != stop - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM [" + this.tableName +"]";
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, values);
		
		sqlQuery = "SELECT freq FROM [" + this.tableName + "]";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", freqs);		
	}
//interface dbStorable		    
	 @Override
	 public void saveInDB() throws SQLException, SavingException {
		String sqlQuery = "CREATE TABLE [" + this.tableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			sqlQuery += ("DOWN_" + measUnitPart + "_" + S_Parametr.values()[i] + " VARCHAR(20), ");
			sqlQuery += ("UP_" + measUnitPart + "_" + S_Parametr.values()[i] + " VARCHAR(20)");
			if (i != myElement.getSParamsCout() - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		 try {
			 DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		 } catch (SQLException sqlExp) {
			 throw new SavingException("Не удалось создать таблицу с параметрами для\n ." +
					 measUnitPart.getTableNamePart() + " S параметра при " +
					 timeType.getTableNamePart() + " поверке" );
		 }
		for (int i = 0; i < freqs.size(); i++) {			
			sqlQuery = "INSERT INTO [" + tableName + "] (freq, ";						
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
			} catch (SQLException sqlExp) {
				throw new SavingException("Не удалось сохранить значения параметров на частоте\n ." +
						freqs.get(i).toString() + " для " +
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
		 String newTableName = "Параметры допуска " + timeType.getTableNamePart() + " поверки " + measUnitPart.getTableNamePart() + " S параметров для " + myElement.getMyOwner().getName() + " " +
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