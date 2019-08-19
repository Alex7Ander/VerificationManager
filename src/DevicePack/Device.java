package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;

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
	public String getSerialNumber() {return serialNumber;}
	public String getGosNumber() {return gosNumber;}
	public String Owner() {return owner;}
	public int getCountOfElements() {return this.countOfElements;}
		
	public Device(){
		//
	}
	
	public Device(String Name, String Type, String SerialNumber) throws SQLException {
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
				this.includedElements.add(new Element());
				//this.includedElements.add(new Element(this.elementsTableName, i+1));
				this.countOfElements++;		
			}
		}
		else {
			//
		}
	}
	
	public Device(String Name, String Type, String SerialNumber, String Owner, String GosNumber){
		this.name = Name;
		this.type = Type;
		this.serialNumber = SerialNumber;
		this.owner = Owner;
		this.gosNumber = GosNumber;
	}
	
	public boolean isExist() {
		String sqlString = null;
		String addStr = name + " " + type + " " + serialNumber;
		String strElementsTable = "Elements of " + addStr;
		sqlString = "SELECT COUNT(*) FROM Devices WHERE TypeOfDevice='"+this.type+"' AND NameOfDevice='"+ this.name +"' AND SerialNumber='" + this.serialNumber + "'";
		int isExist = 0;
		try {
			isExist = AksolDataBase.sqlQueryCount(sqlString);
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
		String strElementsTable = "Elements of " + addStr;
		
		sqlString = "INSERT INTO [Devices] (NameOfDevice, TypeOfDevice, SerialNumber, Owner, GosNumber, CountOfElements, ElementsTable) values ('"+name+"','"+type+"','"+serialNumber+"','"+owner+"','"+gosNumber+"','"+Integer.toString(this.countOfElements)+"','"+strElementsTable+"')";
		AksolDataBase.sqlQueryUpdate(sqlString);
		//Создание таблицы со списком включенных элементов
		sqlString = "CREATE TABLE ["+strElementsTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, ElementType VARCHAR(256), ElementSerNumber VARCHAR(256), PoleCount VARCHAR(256), MeasUnit VARCHAR(256), ToleranceType VARCHAR(256), PeriodicParamTable VARCHAR(256), PrimaryParamTable VARCHAR(256))";
		AksolDataBase.sqlQueryUpdate(sqlString);
		for (int i=0; i < this.countOfElements; i++){
			//Сохранение в БД данных о составных элементах
			this.includedElements.get(i).saveInDB(); 		
		}
		sqlString = "COMMIT";
		AksolDataBase.sqlQueryUpdate(sqlString);
	}
	
	@Override
	public void deleteFromDB() {
		// TODO Auto-generated method stub
	}
	@Override
	public void editInfoInDB() {
		// TODO Auto-generated method stub
	}
	@Override
	public void getData() {
		// TODO Auto-generated method stub		
	}
	
	public void addElement(Element element) {
		this.includedElements.add(element);
	}
	
	public void removeElement(int index) {
		Iterator<Element> it = this.includedElements.iterator();
		for (int i=0; i<index; i++) {
			it.next();
		}
		it.remove();
	}
	
	
}
