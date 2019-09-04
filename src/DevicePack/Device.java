package DevicePack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import FileManagePack.FileManager;
import VerificationPack.MeasResult;

public class Device implements dbStorable {
	
	private String name;
	private String type;
	private String serialNumber;
	private String owner;
	private String gosNumber;	
	private int countOfElements;
	private String elementsTableName;
	
	public ArrayList<Element> includedElements;
	
	public String getType() {return type;}
	public String getName() {return name;}
	public String getOwner() {return this.owner;}
	public String getSerialNumber() {return serialNumber;}
	public String getGosNumber() {return gosNumber;}
	public String getElementsTableName() { return this.elementsTableName;}
	public int getCountOfElements() {return this.countOfElements;}
		
	public Device(){
		//
	}
	
	//Конструктор для извлечения из БД
	public Device(String Name, String Type, String SerialNumber) throws SQLException {
		
		includedElements = new ArrayList<Element>();
		
		this.name = Name;
		this.type = Type;
		this.serialNumber = SerialNumber;
		String sqlQuery = "SELECT Owner, GosNumber, CountOfElements, ElementsTable FROM Devices WHERE TypeOfDevice='"+ Type +"' AND NameOfDevice='"+ Name +"' AND SerialNumber='"+ SerialNumber +"'";
		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("Owner"); 
		fieldName.add("GosNumber");
		fieldName.add("CountOfElements");
		fieldName.add("ElementsTable");
		
		ArrayList<Element> includedElements = new ArrayList<Element>();
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();	
				
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
	
		if (arrayResults.size() > 0) {
			this.owner = arrayResults.get(0).get(0);
			this.gosNumber = arrayResults.get(0).get(1);						
			this.elementsTableName = arrayResults.get(0).get(3);			
			this.countOfElements = 0;			
			int predictableCount = Integer.parseInt(arrayResults.get(0).get(2));
			for (int i=0; i<predictableCount; i++) {			
				this.includedElements.add(new Element(this, i+1));
				this.countOfElements++;		
			}
		}
		else {
			//
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
	
	public boolean isExist() {
		String sqlString = null;
		String addStr = name + " " + type + " " + serialNumber;
		String strElementsTable = "Элементы для " + addStr;
		sqlString = "SELECT COUNT(*) FROM Devices WHERE TypeOfDevice='"+this.type+"' AND NameOfDevice='"+ this.name +"' AND SerialNumber='" + this.serialNumber + "'";
		int isExist = 0;
		try {
			isExist = DataBaseManager.getDB().sqlQueryCount(sqlString);
			if (isExist == 0) {
				return false;
			}
			else {
				return true;
			}
		}
		catch(SQLException exp){
			return true;
		}		 
	}
//dbStorable	
	@Override
	public void saveInDB() throws SQLException{
		String sqlString = null;
		String addStr = name + " " + type + " " + serialNumber;
		String strElementsTable = "Элементы для " + addStr;
		
		sqlString = "INSERT INTO [Devices] (NameOfDevice, TypeOfDevice, SerialNumber, Owner, GosNumber, CountOfElements, ElementsTable) values ('"+name+"','"+type+"','"+serialNumber+"','"+owner+"','"+gosNumber+"','"+Integer.toString(this.countOfElements)+"','"+strElementsTable+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		//Создание таблицы со списком включенных элементов
		sqlString = "CREATE TABLE ["+strElementsTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, ElementType VARCHAR(256), ElementSerNumber VARCHAR(256), PoleCount VARCHAR(256), MeasUnit VARCHAR(256), ToleranceType VARCHAR(256), PeriodicParamTable VARCHAR(256), PrimaryParamTable VARCHAR(256), NominalIndex VARCHAR(10))";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		this.elementsTableName = strElementsTable;
		for (int i=0; i < this.countOfElements; i++){
			System.out.println("this.countOfElements = " + this.countOfElements);
			//Сохранение в БД данных о составных элементах
			this.includedElements.get(i).saveInDB(); 		
		}
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		for (Element elm : this.includedElements) {
			elm.deleteFromDB();
		}
		this.includedElements.clear();
		String sqlQuery = "DROP TABLE [" + this.elementsTableName + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "DELETE FROM [Devices] WHERE NameOfDevice='"+this.name+"' AND TypeOfDevice='"+this.type+"' AND SerialNumber='"+this.serialNumber+"'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	@Override
	public void editInfoInDB() throws SQLException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void getData() {
		// TODO Auto-generated method stub		
	}
	
	public void addElement(Element element) {
		this.includedElements.add(element);
		this.countOfElements = this.includedElements.size();	
	}
	
	public void removeElement(int index) {
		this.includedElements.remove(index);
		this.countOfElements--;	
	}
	
	public void createIniFile(String psiFilePAth) throws IOException {
		ArrayList<String> fileStrings = new ArrayList<String>();
		fileStrings.add("Параметры поверки:\n");
		fileStrings.add("1.Наименование типа средства измерения <" + this.name + ">\n");
		fileStrings.add("2.Тип поверяемого средства измерения <" + this.type + ">\n");
		fileStrings.add("3.Заводской номер <" + this.serialNumber + ">\n");
		fileStrings.add("4.В состав входят элементов в количестве <" + Integer.toString(this.countOfElements) + ">\n");
		fileStrings.add("------------------------------------------------------------------\n");
		for (int i=0; i<this.countOfElements; i++) {
			fileStrings.add("Элемент № " + Integer.toString(i) + ": " + this.includedElements.get(i).getType() + "\n");
			fileStrings.add("Серийный номер: " + this.includedElements.get(i).getSerialNumber() + "\n");
			String ruTextOfMeasUnit;
			if (this.includedElements.get(i).getMeasUnit().equals("vswr")) {
				ruTextOfMeasUnit = "КСВН";
			}
			else {
				ruTextOfMeasUnit = "модуль";
			}
			fileStrings.add("Измеряемая величина: " + ruTextOfMeasUnit + "\n");
			int currentCountOfFreq = this.includedElements.get(i).getNominal().getCountOfFreq();
			for (int j=0; j<currentCountOfFreq; j++) {
				double currentFreq = this.includedElements.get(i).getNominal().freqs.get(j);
				fileStrings.add(Double.toString(currentFreq) + "\n");
			}			
			if (i < this.countOfElements-1) {
				fileStrings.add("------------------------------------------------------------------\n");
			}
			else {
				fileStrings.add("------------------------------------------------------------------");
			}
		}
		String coding = "ansi-1251";
		FileManager.WriteFile(psiFilePAth, coding, fileStrings);
	}
	
}