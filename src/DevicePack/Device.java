package DevicePack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import Exceptions.SavingException;
import FileManagePack.FileManager;

public class Device implements dbStorable {
	
	private String name;
	private String type;
	private String serialNumber;
	private String owner;
	private String gosNumber;	
	private int countOfElements;
	private String elementsTableName;
	
	public ArrayList<Element> includedElements;
	
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public String getOwner() {
		return owner;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public String getGosNumber() {
		return gosNumber;
	}
	public String getElementsTableName() { 
		return elementsTableName;
	}
	public int getCountOfElements() {
		return countOfElements;
	}
		
	//Конструктор для извлечения из БД
	public Device(String Name, String Type, String SerialNumber) throws SQLException {		
		includedElements = new ArrayList<Element>();		
		name = Name;
		type = Type;
		serialNumber = SerialNumber;
		String sqlQuery = "SELECT Owner, GosNumber, CountOfElements, ElementsTable FROM Devices WHERE TypeOfDevice='"+ Type +"' AND NameOfDevice='"+ Name +"' AND SerialNumber='"+ SerialNumber +"'";		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("Owner"); 
		fieldName.add("GosNumber");
		fieldName.add("CountOfElements");
		fieldName.add("ElementsTable");		
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();					
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);	
		if (arrayResults.size() > 0) {
			this.owner = arrayResults.get(0).get(0);
			this.gosNumber = arrayResults.get(0).get(1);						
			this.elementsTableName = arrayResults.get(0).get(3);			
			this.countOfElements = 0;			
			List<String> indices = new ArrayList<String>();
			sqlQuery = "SELECT id FROM [" + elementsTableName + "]";
			DataBaseManager.getDB().sqlQueryString(sqlQuery, "id", indices);
			for (int i = 0; i < indices.size(); i++) {
				Element element = new Element(this, Integer.parseInt(indices.get(i)));
				includedElements.add(element);
				countOfElements++;		
			}
		}
	}
	
	//Конструктор перед сохранение в БД
	public Device(String Name, String Type, String SerialNumber, String Owner, String GosNumber){
		includedElements = new ArrayList<Element>();
		this.name = Name;
		this.type = Type;
		this.serialNumber = SerialNumber;
		this.owner = Owner;
		this.gosNumber = GosNumber;
		this.elementsTableName = "";
	}
	
	public boolean isExist() throws SQLException {
		String sqlString = null;
		sqlString = "SELECT COUNT(*) FROM Devices WHERE TypeOfDevice='" + this.type + "' AND NameOfDevice='" + this.name + "' AND SerialNumber='" + this.serialNumber + "'";
		int isExist = DataBaseManager.getDB().sqlQueryCount(sqlString);
		if (isExist == 0) {
			return false;
		}
		else {
			return true;
		}				 
	}
//dbStorable	
	@Override
	public void saveInDB() throws SQLException, SavingException{
		String sqlString = null;
		String addStr = name + " " + type + " " + serialNumber;
		elementsTableName = "Элементы для " + addStr;		
		sqlString = "INSERT INTO [Devices] (NameOfDevice, TypeOfDevice, SerialNumber, Owner, GosNumber, CountOfElements, ElementsTable) values ('"+name+"','"+type+"','"+serialNumber+"','"+owner+"','"+gosNumber+"','"+Integer.toString(this.countOfElements)+"','"+elementsTableName+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("Внесена запись о новом приборе в таблицу Devices");
		//Создание таблицы со списком включенных элементов
		sqlString = "CREATE TABLE [" + elementsTableName + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, ElementType VARCHAR(256), ElementSerNumber VARCHAR(256), PoleCount VARCHAR(256), MeasUnit VARCHAR(256), ModuleToleranceType VARCHAR(256), PhaseToleranceType VARCHAR(256), VerificationsTable VARCHAR(256), PrimaryModuleParamTable VARCHAR(256), PeriodicModuleParamTable VARCHAR(256), PrimaryPhaseParamTable VARCHAR(256), PeriodicPhaseParamTable VARCHAR(256), NominalIndex VARCHAR(10))";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("Создана таблица со списком элементов данного устройства");
		for (int i=0; i < this.countOfElements; i++){
			System.out.println("Начато сохранение элемента №" + i + ": ");
			//Сохранение в БД данных о составных элементах
			includedElements.get(i).saveInDB(); 		
		}
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		for (Element elm : this.includedElements) {
			elm.deleteFromDB();
		}
		this.includedElements.clear();
		String sqlQuery = "DROP TABLE [" + elementsTableName + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "DELETE FROM [Devices] WHERE NameOfDevice='"+this.name+"' AND TypeOfDevice='"+this.type+"' AND SerialNumber='"+this.serialNumber+"'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		String sqlQuery = "UPDATE Devices SET ";
		int count = 1;
		for (String str: editingValues.keySet()) {
			sqlQuery += (str + "='"+editingValues.get(str)+"'");
			if (count != editingValues.size()) {
				sqlQuery += ", ";
			} 
			else {
				sqlQuery += " ";
			}
			count++;
		}
		sqlQuery += "WHERE NameOfDevice='" + name + "' AND TypeOfDevice='" + type + "' AND SerialNumber='" + serialNumber + "'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		boolean tableNamesMustBeRewrited = false;
		for (String field : editingValues.keySet()) {
			if (field.equals("NameOfDevice")) {
				name = editingValues.get(field);
				tableNamesMustBeRewrited = true;
			} 
			else if (field.equals("TypeOfDevice")) {
				type = editingValues.get(field);
				tableNamesMustBeRewrited = true;
			}
			else if (field.equals("SerialNumber")) {
				serialNumber = editingValues.get(field);
				tableNamesMustBeRewrited = true;
			}
			else if(field.equals("Owner")) {
				owner = editingValues.get(field);
			}
			else if (field.equals("GosNumber")) {
				gosNumber = editingValues.get(field);
			}
		}
		
		if (tableNamesMustBeRewrited) {
			String oldElementsTableName = elementsTableName;
			elementsTableName = "Элементы для " + name + " " + type + " " + serialNumber;
			sqlQuery = "SELECT SerialNumber FROM [Devices] WHERE NameOfDevice='" + name + "' AND TypeOfDevice='" + type + "' AND SerialNumber='" + serialNumber + "'";
			List<String> arrayResults = new ArrayList<String>();
			DataBaseManager.getDB().sqlQueryString(sqlQuery, "SerialNumber", arrayResults);
			sqlQuery = "UPDATE Devices SET ElementsTable='" + elementsTableName + "' WHERE NameOfDevice='" + name + "' AND TypeOfDevice='" + type + "' AND SerialNumber='" + serialNumber + "'";
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			sqlQuery = "ALTER TABLE [" + oldElementsTableName + "] RENAME TO ["+ elementsTableName + "]";	
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
			for (Element element : includedElements) {
				element.rewriteTableNames();
			}
		}
	}
		
	public void addElement(Element element) {
		includedElements.add(element);
		element.onAdding(this);
		countOfElements = includedElements.size();	
	}
	
	public void removeElement(int index) {
		includedElements.remove(index);
		countOfElements--;	
	}
	
	public void createIniFile(String psiFilePath) throws IOException {
		ArrayList<String> fileStrings = new ArrayList<String>();
		fileStrings.add("Параметры поверки:\n");
		fileStrings.add("1.Наименование типа средства измерения <" + name + ">\n");
		fileStrings.add("2.Тип поверяемого средства измерения <" + type + ">\n");
		fileStrings.add("3.Заводской номер <" + serialNumber + ">\n");
		fileStrings.add("4.В состав входят элементов в количестве <" + Integer.toString(countOfElements) + ">\n");
		fileStrings.add("------------------------------------------------------------------\n");
		for (int i = 0; i < countOfElements; i++) {
			fileStrings.add("Элемент № " + Integer.toString(i) + ": " + includedElements.get(i).getType() + "\n");
			fileStrings.add("Серийный номер: " + includedElements.get(i).getSerialNumber() + "\n");
			String ruTextOfMeasUnit;
			if (this.includedElements.get(i).getMeasUnit().equals("vswr")) {
				ruTextOfMeasUnit = "КСВН";
			}
			else {
				ruTextOfMeasUnit = "Модуль";
			}
			fileStrings.add("Измеряемая величина: " + ruTextOfMeasUnit + "\n");
			String paramsStr = "S11";
			if (includedElements.get(i).getPoleCount() == 4) {
				paramsStr += ", S12, S21, S22";
			}				
			fileStrings.add("Параметры: " + paramsStr + "\n");
			int currentCountOfFreq = includedElements.get(i).getNominal().getCountOfFreq();
			for (int j=0; j<currentCountOfFreq; j++) {
				double currentFreq = includedElements.get(i).getNominal().freqs.get(j);
				fileStrings.add(Double.toString(currentFreq) + "\n");
			}			
			if (i < countOfElements - 1) {
				fileStrings.add("------------------------------------------------------------------\n");
			}
			else {
				fileStrings.add("------------------------------------------------------------------");
			}
		}
		String coding = "ansi-1251";
		FileManager.WriteFile(psiFilePath, coding, fileStrings);
	}
	
}