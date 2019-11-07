package ToleranceParamPack.ParametrsPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import Exceptions.SavingException;
import NewElementPack.NewElementController;
import ToleranceParamPack.StrategyPack.StrategyOfSuitability;
import VerificationPack.MeasResult;

public class ToleranceParametrs implements Includable<Element>, dbStorable {
	 public HashMap<String, HashMap<Double, Double>> values; 
	 public ArrayList<Double> freqs;						 
	
	 private String tableName;
	 public String getTableName(){
		 return this.tableName;
	 }
	 public void setTableName(){
		 String addrStr = this.myElement.getMyOwner().getName() + " " +
				 		  this.myElement.getMyOwner().getType() + " " +
				 		  this.myElement.getMyOwner().getSerialNumber() + " " +
				 		  this.myElement.getType() + " " + this.myElement.getSerialNumber();
		 tableName = "Параметры допуска " + this.timeType.getTableNamePart() + " поверки " + this.measUnitPart.getTableNamePart() + " S параметров для " + addrStr;
	 }
	 
	 public TimeType timeType;
	 public MeasUnitPart measUnitPart;
	 
//Constructors
	 ToleranceParametrs(){
		this.values = new HashMap<String, HashMap<Double, Double>>();
		this.freqs = new ArrayList<Double>();
	 }	 
//GUI
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, NewElementController elCtrl, Element ownerElement){		
		this();
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;		
		this.freqs = elCtrl.getFreqsValues();
		this.values = elCtrl.getToleranceParamsValues(this.timeType, this.measUnitPart);
	}
//DataBase
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, Element ownerElement) throws SQLException {
		this();		
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;
		setTableName();
		//this.tableName = "Параметры допуска " + this.timeType.getTableNamePart() + " поверки для " + this.measUnitPart.getTableNamePart() + " S параметров";		
		
		ArrayList<String> fieldsNames = new ArrayList<String>();
		fieldsNames.add("freq");
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			fieldsNames.add("DOWN_" + this.measUnitPart + "_" + S_Parametr.values()[i]);
			fieldsNames.add("UP_" + this.measUnitPart + "_" + S_Parametr.values()[i]);
		}
		
		String sqlQuery = "SELECT ";
		int stop = fieldsNames.size();
		for (int i = 0; i < stop; i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != stop - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM [" + this.tableName +"]";
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, this.values);
		
		sqlQuery = "SELECT freq FROM [" + this.tableName + "]";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);		
	}
 
//interface dbStorable		    
	 @Override
	 public void saveInDB() throws SQLException, SavingException {
		String sqlQuery = "CREATE TABLE [" + this.tableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			sqlQuery += ("DOWN_" + this.measUnitPart + "_" + S_Parametr.values()[i] + " VARCHAR(20), ");
			sqlQuery += ("UP_" + this.measUnitPart + "_" + S_Parametr.values()[i] + " VARCHAR(20)");
			if (i != this.myElement.getSParamsCout() - 1) sqlQuery += ", ";
		}
		sqlQuery += ")";
		 try {
			 DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		 } catch (SQLException sqlExp) {
			 throw new SavingException("Не удалось создать таблицу с параметрами для\n ." +
					 this.measUnitPart.getTableNamePart() + " S параметра при " +
					 this.timeType.getTableNamePart() + " поверке" );
		 }
		for (int i = 0; i < this.freqs.size(); i++) {			
			sqlQuery = "INSERT INTO [" + this.tableName + "] (freq, ";						
			for (int j = 0; j < this.myElement.getSParamsCout(); j++) {
				sqlQuery += ("DOWN_" + this.measUnitPart + "_" + S_Parametr.values()[j] + ", ");
				sqlQuery += ("UP_" + this.measUnitPart + "_" + S_Parametr.values()[j]);
				if (j !=  this.myElement.getSParamsCout() - 1) sqlQuery += ", ";
			}
			sqlQuery += ") values ('"+freqs.get(i)+"', ";		
			for (int j = 0; j < this.myElement.getSParamsCout(); j++) {
				String key = "DOWN_" + this.measUnitPart + "_" + S_Parametr.values()[j];
				sqlQuery += ("'"+values.get(key).get(freqs.get(i)).toString()+"', ");
				key = "UP_" + this.measUnitPart + "_" + S_Parametr.values()[j];
				sqlQuery += ("'"+values.get(key).get(freqs.get(i)).toString()+"'");
				if (j != this.myElement.getSParamsCout() - 1) sqlQuery += ", ";
			}				
			sqlQuery += ")";
			try {
				DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			} catch (SQLException sqlExp) {
				throw new SavingException("Не удалось сохранить значения параметров на частоте\n ." +
						freqs.get(i).toString() + " для " +
						this.measUnitPart.getTableNamePart() + " S параметра при " +
						this.timeType.getTableNamePart() + " поверке" );
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

	 private Element myElement; 
	 @Override
	 public Element getMyOwner() {
		 return myElement;
	 }
	
	 @Override
	 public void onAdding(Element Owner) {
		 this.myElement = Owner;				
	 }
	 
	private StrategyOfSuitability strategy;
	public void setStrategy(StrategyOfSuitability anyStrategy) {
		this.strategy = anyStrategy;
	}
	public boolean checkResult(MeasResult result) {
		return this.strategy.checkResult(result, this);		
	}			
}