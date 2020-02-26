package DevicePack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import Exceptions.SavingException;
import FileManagePack.FileManager;

public class Device implements dbStorable {
	
	private static final Logger logger = Logger.getLogger(Device.class);
	
	private int id;
	private String name;
	private String type;
	private String serialNumber;
	private String owner;
	private String gosNumber;	
	private int countOfElements;
	private String elementsTableName;
	
	public ArrayList<Element> includedElements;
	
	public int getId() {
		return this.id;
	}
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
	public Device(int id) throws SQLException {	
		System.out.println("Начинаем получение данных на устройство с id=" + id);
		this.id = id;
		this.includedElements = new ArrayList<Element>();		
		String sqlQuery = "SELECT NameOfDevice, TypeOfDevice, SerialNumber, Owner, GosNumber FROM Devices WHERE id="+ Integer.toString(id) +"";		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("NameOfDevice");
		fieldName.add("TypeOfDevice");
		fieldName.add("SerialNumber");
		fieldName.add("Owner"); 
		fieldName.add("GosNumber");	
		List<List<String>> arrayResults = new ArrayList<List<String>>();	
		System.out.println("Получаем основную информацию на устройство запросом: " + sqlQuery);
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);	
		if (arrayResults.size() > 0) {
			this.name = arrayResults.get(0).get(0);
			this.type = arrayResults.get(0).get(1);
			this.serialNumber = arrayResults.get(0).get(2);
			this.owner = arrayResults.get(0).get(3);
			this.gosNumber = arrayResults.get(0).get(4);									
			this.countOfElements = 0;			
			List<Integer> indices = new ArrayList<Integer>();
			sqlQuery = "SELECT (id) FROM [Elements] WHERE DeviceId='" + this.id + "'";
			DataBaseManager.getDB().sqlQueryInteger(sqlQuery, "id", indices);
			System.out.println("Для устройства с id = " + this.id + " найдено " + indices.size() + " составных элемента");
			for (int i = 0; i < indices.size(); i++) {
				Element element = new Element(this, indices.get(i));
				this.includedElements.add(element);
				this.countOfElements++;		
			}
		}
		System.out.println("\nПроцедура получения информации успешно завершена");
	}
	
	//Конструктор перед сохранение в БД
	public Device(String name, String type, String serialNumber, String owner, String gosNumber){
		this.includedElements = new ArrayList<Element>();
		this.name = name;
		this.type = type;
		this.serialNumber = serialNumber;
		this.owner = owner;
		this.gosNumber = gosNumber;
	}
	
	public boolean isExist() throws SQLException {
		String sqlString = "SELECT COUNT(*) FROM Devices WHERE TypeOfDevice='" + this.type + "' AND NameOfDevice='" + this.name + "' AND SerialNumber='" + this.serialNumber + "'";
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
		String sqlString = "INSERT INTO [Devices] (NameOfDevice, TypeOfDevice, SerialNumber, Owner, GosNumber) values ('"+name+"','"+type+"','"+serialNumber+"','"+owner+"','"+gosNumber+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("Начато сохранение устройства "+name+", тип "+type+", № "+serialNumber+", владелец "+owner+", № по ФИФ "+gosNumber);
		System.out.println("Внесена запись о новом приборе в таблицу Devices");
		sqlString = "SELECT id FROM [Devices] WHERE NameOfDevice='" + name + "' AND TypeOfDevice='" + type + "' AND SerialNumber='" + serialNumber + "'";
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlString);
		System.out.println("Получен id под которым сохранен прибор: " + this.id);
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
		//String sqlQuery = "DROP TABLE [" + elementsTableName + "]";
		//DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		String sqlQuery = "DELETE FROM [Devices] WHERE NameOfDevice='"+this.name+"' AND TypeOfDevice='"+this.type+"' AND SerialNumber='"+this.serialNumber+"'";
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
		
		//Rewriting info in table Verification
		sqlQuery = "UPDATE [Verifications] SET ";
		for (String str: editingValues.keySet()) {
			if (str.equals("NameOfDevice") || str.equals("TypeOfDevice") || str.equals("SerialNumber")) {
				sqlQuery += (str + "='"+editingValues.get(str)+"', ");
			}
		}
		int lastCommaIndex = sqlQuery.lastIndexOf(",");
		sqlQuery = sqlQuery.substring(0, lastCommaIndex);
		sqlQuery += " WHERE NameOfDevice='" + name + "' AND TypeOfDevice='" + type + "' AND SerialNumber='" + serialNumber + "'";
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
		/*
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
		}*/
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