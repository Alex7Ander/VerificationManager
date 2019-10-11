package DevicePack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
	
	//����������� ��� ���������� �� ��
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
	
	//����������� ����� ���������� � ��
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
		sqlString = "SELECT COUNT(*) FROM Devices WHERE TypeOfDevice='" + this.type + "' AND NameOfDevice='" + this.name + "' AND SerialNumber='" + this.serialNumber + "'";
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
	public void saveInDB() throws SQLException, SavingException{
		String sqlString = null;
		String addStr = name + " " + type + " " + serialNumber;
		String strElementsTable = "�������� ��� " + addStr;
		
		sqlString = "INSERT INTO [Devices] (NameOfDevice, TypeOfDevice, SerialNumber, Owner, GosNumber, CountOfElements, ElementsTable) values ('"+name+"','"+type+"','"+serialNumber+"','"+owner+"','"+gosNumber+"','"+Integer.toString(this.countOfElements)+"','"+strElementsTable+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("������� ������ � ����� ������� � ������� Devices");
		//�������� ������� �� ������� ���������� ���������
		sqlString = "CREATE TABLE ["+strElementsTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, ElementType VARCHAR(256), ElementSerNumber VARCHAR(256), PoleCount VARCHAR(256), MeasUnit VARCHAR(256), ModuleToleranceType VARCHAR(256), PhaseToleranceType VARCHAR(256), VerificationsTable VARCHAR(256), PrimaryModuleParamTable VARCHAR(256), PeriodicModuleParamTable VARCHAR(256), PrimaryPhaseParamTable VARCHAR(256), PeriodicPhaseParamTable VARCHAR(256), NominalIndex VARCHAR(10))";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("������� ������� �� ������� ��������� ������� ����������");
		this.elementsTableName = strElementsTable;
		for (int i=0; i < this.countOfElements; i++){
			System.out.println("������ ���������� �������� �" + i + ": ");
			//���������� � �� ������ � ��������� ���������
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
		sqlQuery += "WHERE NameOfDevice='"+this.name+"' AND TypeOfDevice='"+this.type+"' AND SerialNumber='"+this.serialNumber+"'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);		
	}
		
	public void addElement(Element element) {
		this.includedElements.add(element);
		element.onAdding(this);
		this.countOfElements = this.includedElements.size();	
	}
	
	public void removeElement(int index) {
		this.includedElements.remove(index);
		this.countOfElements--;	
	}
	
	public void createIniFile(String psiFilePath) throws IOException {
		ArrayList<String> fileStrings = new ArrayList<String>();
		fileStrings.add("��������� �������:\n");
		fileStrings.add("1.������������ ���� �������� ��������� <" + this.name + ">\n");
		fileStrings.add("2.��� ����������� �������� ��������� <" + this.type + ">\n");
		fileStrings.add("3.��������� ����� <" + this.serialNumber + ">\n");
		fileStrings.add("4.� ������ ������ ��������� � ���������� <" + Integer.toString(this.countOfElements) + ">\n");
		fileStrings.add("------------------------------------------------------------------\n");
		for (int i=0; i<this.countOfElements; i++) {
			fileStrings.add("������� � " + Integer.toString(i) + ": " + this.includedElements.get(i).getType() + "\n");
			fileStrings.add("�������� �����: " + this.includedElements.get(i).getSerialNumber() + "\n");
			String ruTextOfMeasUnit;
			if (this.includedElements.get(i).getMeasUnit().equals("vswr")) {
				ruTextOfMeasUnit = "����";
			}
			else {
				ruTextOfMeasUnit = "������";
			}
			fileStrings.add("���������� ��������: " + ruTextOfMeasUnit + "\n");
			String paramsStr = "S11";
			if (this.includedElements.get(i).getPoleCount() == 4) {
				paramsStr += ", S12, S21, S22";
			}				
			fileStrings.add("���������: " + paramsStr + "\n");
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
		FileManager.WriteFile(psiFilePath, coding, fileStrings);
	}
	
}