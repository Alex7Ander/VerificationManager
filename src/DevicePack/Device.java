package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;

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
	public int getCountOfElements() {return this.includedElements.size();}
	
	
	public Device(){
		//
	}
	
	public Device(String Name, String Type, String SerialNumber) throws SQLException {
		this.name = Name;
		this.type = Type;
		this.serialNumber = SerialNumber;
		String sqlQuery = "SELECT Owner, GosNumber, CountOfElements, ElementsTable FROM Devices WHERE TypeOfDevice='"+ Name +"' AND NameOfDevice='"+ Type +"' AND SerialNumber='"+ SerialNumber +"'";
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
			this.countOfElements = Integer.parseInt(arrayResults.get(0).get(2));
			this.elementsTableName = arrayResults.get(0).get(3);
			for (int i=0; i<this.countOfElements; i++) {
				/*
				Element incElement = new Element(this.elementsTableName, i);
				this.includedElements.add(incElement);
				*/
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
	
	
//dbStorable	
	@Override
	public void saveInDB() throws SavingException{
		String sqlString = null;
		String addStr = name + " " + type + " " + serialNumber;
		String strElementsTable = "Elements of " + addStr;
		sqlString = "SELECT COUNT(*) FROM Devices WHERE TypeOfDevice='"+this.type+"' AND NameOfDevice='"+ this.name +"' AND SerialNumber='" + this.serialNumber + "'";
		int isExist = 0;
		try {
			isExist = AksolDataBase.sqlQueryCount(sqlString);
		}
		catch(SQLException exp){
			throw new SavingException("������ � �������� [ "+sqlString+" ] ��� �������� ���������� �������� � ����� ������.\n���������� � ������������� �� ��� ���������� ��������.");
		}
		if (isExist > 0) {
			throw new SavingException("������ ������� ������������ � ���� \n� ����� �������� ������� ��� ����������!");
		}
		else {
			try {
				sqlString = "BEGIN TRANSACTION";
				AksolDataBase.sqlQueryUpdate(sqlString);
				sqlString = "INSERT INTO [Devices] (TypeOfDevice, NameOfDevice, SerialNumber, Owner, GosNumber, CountOfElements, ElementsTable) values ('"+type+"','"+name+"','"+serialNumber+"','"+owner+"','"+gosNumber+"','"+Integer.toString(this.countOfElements)+"','"+strElementsTable+"')";
				AksolDataBase.sqlQueryUpdate(sqlString);
				//�������� ������� �� ������� ���������� ���������
				sqlString = "CREATE TABLE ["+strElementsTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, ElementType VARCHAR(256), ElementSerNumber VARCHAR(256), PoleCount VARCHAR(256), MeasUnit VARCHAR(256), ToleranceType VARCHAR(256), PeriodicParamTable VARCHAR(256), PrimaryParamTable VARCHAR(256))";
				AksolDataBase.sqlQueryUpdate(sqlString);
				for (int i=0; i < this.countOfElements; i++){
					//���������� � �� ������ � ��������� ���������
					this.includedElements.get(i).saveInDB(); 		
				}
				sqlString = "COMMIT";
				AksolDataBase.sqlQueryUpdate(sqlString);
			}
			catch(Exception exp){
		      new SavingException("��� ���������� ��������� ������! �� �������� ������ " + sqlString);
			}
		}
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
	
	
}
