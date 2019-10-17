package ToleranceParamPack.ParametrsPack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Element;
import DevicePack.Includable;
import ErrorParamsPack.ErrorParams;
import Exceptions.SavingException;
import NewElementPack.NewElementController;
import ToleranceParamPack.StrategyPack.StrategyOfSuitability;
import VerificationPack.MeasResult;

public class ToleranceParametrs implements Includable<Element>, dbStorable {
	 protected String keys[] = 	{"S11", "S12", "S21", "S22"}; 
	 public HashMap<String, HashMap<Double, Double>> values; //Значения критериев годности
	 public ArrayList<Double> freqs;						 //Частоты
	
	 private String tableName;
	 public String getTableName(){
		 return this.tableName;
	 }

	 public TimeType timeType;
	 public MeasUnitPart measUnitPart;

	 public void setTableName(){
		 tableName = "�������� �������� " + this.timeType.getTableNamePart() + " ������� ��� " + this.measUnitPart.getTableNamePart() + " S ����������";
	 }
		
//Конструкторы
	 ToleranceParametrs(){
		this.values = new HashMap<String, HashMap<Double, Double>>();
		this.freqs = new ArrayList<Double>();
	 }
	//Конструктор для получения параметров из GUI
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, NewElementController elCtrl, Element ownerElement){		
		this();
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;
		this.freqs = elCtrl.getFreqsValues();
		this.values = elCtrl.getToleranceParamsValues("");
		this.tableName = "Критерии годности " + this.timeType.getTableNamePart() + " поверки для " + this.measUnitPart.getTableNamePart() + " S параметров";
	}
	//Конструктор для получения данных из БД
	public ToleranceParametrs(TimeType currentTimeType, MeasUnitPart currentUnitPart, Element ownerElement) throws SQLException {
		this();		
		this.myElement = ownerElement;
		this.timeType = currentTimeType;
		this.measUnitPart = currentUnitPart;		
		this.tableName = "Критерии годности " + this.timeType.getTableNamePart() + " поверки для " + this.measUnitPart.getTableNamePart() + " S параметров";		
		
		ArrayList<String> fieldsNames = new ArrayList<String>();
		fieldsNames.add("freq");
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			fieldsNames.add("DOWN_" + this.measUnitPart + "_" + this.keys[i]);
			fieldsNames.add("UP_" + this.measUnitPart + "_" + this.keys[i]);
		}
		
		String sqlQuery = "SELECT ";
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != this.myElement.getSParamsCout() - 1) sqlQuery += ", ";
		}
		sqlQuery += " FROM [" + this.tableName +"]";
		DataBaseManager.getDB().sqlQueryMapOfDouble(sqlQuery, fieldsNames, this.values);
		
		sqlQuery = "SELECT freq FROM [" + this.tableName + "]";
		DataBaseManager.getDB().sqlQueryDouble(sqlQuery, "freq", this.freqs);		
	}
 
//������, ����������� ������ � �� (������������� �� dbStorable)		    
	 @Override
	 public void saveInDB() throws SQLException, SavingException {
		String sqlQuery = "CREATE TABLE [" + this.tableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq VARCHAR(20), ";
		for (int i = 0; i < this.myElement.getSParamsCout(); i++) {
			sqlQuery += ("DOWN_" + this.measUnitPart + "_" + this.keys[i] + " VARCHAR(20)");
			sqlQuery += ("UP_" + this.measUnitPart + "_" + this.keys[i] + " VARCHAR(20)");
			if (i != this.myElement.getSParamsCout()) sqlQuery += ", ";
		}
		sqlQuery += ")";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		System.out.println("\t\t������� ������� � ����������� ");
			
		//��������� ������� paramsTableName � ������������ ���������
		for (int i = 0; i < this.freqs.size(); i++) {			
			sqlQuery = "INSERT INTO [" + this.tableName + "] (freq, ";						
			for (int j = 0; j < this.values.size(); j++) {
				sqlQuery += (keys[j]);
				if (j != this.values.size()) sqlQuery += ", ";
			}
			sqlQuery += ") values ('"+freqs.get(i)+"', ";		
			for (int j = 0; j < this.values.size(); j++) {
				sqlQuery += ("'"+values.get(this.timeType + "_" + keys[j]).get(freqs.get(i)).toString()+"'");
				if (j != this.values.size()) sqlQuery += ", ";
			}				
			sqlQuery += ")";						
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			System.out.println("\t\t������� �������� ��� ������� " + freqs.get(i) + ": ");
			System.out.println("\t\t" + sqlQuery + "");
		}			
	 }
	
	 @Override
	 public void deleteFromDB() throws SQLException {
		String sqlQuery = "DROP TABLE [" + this.tableName + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		//Добавить код по удалению записи таблицы в таблице элемента о таблице данного критерия (ну вы поняли)
	 }
	
	 @Override
	 public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		 // TODO Auto-generated method stub
	 }

//������, ������������ �������������� ������� �������� �������� � ����������� �������� (������������ �� Includable<Element>)
	 private Element myElement; //��� �������
	 @Override
	 public Element getMyOwner() {
		 return myElement;
	 }
	
	 @Override
	 public void onAdding(Element Owner) {
		 this.myElement = Owner;				
	 }
	 
	//Стратегия определения пригодности элемента
	private StrategyOfSuitability strategy;
	public void setStratege(StrategyOfSuitability anyStrategy) {
		this.strategy = anyStrategy;
	}
	public boolean checkResult(MeasResult result) {
		return this.strategy.checkResult(result, this);		
	}			
}