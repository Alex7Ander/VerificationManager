package DevicePack;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import Exceptions.SavingException;
import FileManagePack.FileManager;

public class Device implements dbStorable {
	
	private int id;
	private String name;
	private String type;
	private String serialNumber;
	private String owner;
	private String gosNumber;	
	private int countOfElements;
	//private String elementsTableName;
	
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
	/*public String getElementsTableName() { 
		return elementsTableName;
	}*/
	public int getCountOfElements() {
		return countOfElements;
	}
		
	//����������� ��� ���������� �� ��
	public Device(int id) throws SQLException {	
		System.out.println("�������� ��������� ������ �� ���������� � id=" + id);
		this.id = id;
		this.includedElements = new ArrayList<Element>();		
		String sqlQuery = "SELECT name, type, serialNumber, owner, gosNumber FROM Devices WHERE id="+ Integer.toString(id) +"";		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("name");
		fieldName.add("type");
		fieldName.add("serialNumber");
		fieldName.add("owner"); 
		fieldName.add("gosNumber");	
		List<List<String>> arrayResults = new ArrayList<List<String>>();	
		System.out.println("�������� �������� ���������� �� ���������� ��������: " + sqlQuery);
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
			System.out.println("��� ���������� � id = " + this.id + " ������� " + indices.size() + " ��������� ��������");
			for (int i = 0; i < indices.size(); i++) {
				Element element = new Element(this, indices.get(i));
				this.includedElements.add(element);
				this.countOfElements++;		
			}
		}
		System.out.println("\n��������� ��������� ���������� ������� ���������");
	}
	
	//����������� ����� ���������� � ��
	public Device(String name, String type, String serialNumber, String owner, String gosNumber){
		this.includedElements = new ArrayList<Element>();
		this.name = name;
		this.type = type;
		this.serialNumber = serialNumber;
		this.owner = owner;
		this.gosNumber = gosNumber;
	}
	
	public boolean isExist() throws SQLException {
		String sqlString = "SELECT COUNT(*) FROM Devices WHERE type='" + this.type + "' AND name='" + this.name + "' AND serialNumber='" + this.serialNumber + "'";
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
		String sqlString = "INSERT INTO [Devices] (name, type, serialNumber, owner, gosNumber) values ('"+name+"','"+type+"','"+serialNumber+"','"+owner+"','"+gosNumber+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println(sqlString);
		System.out.println("������ ���������� ���������� "+name+", ��� "+type+", � "+serialNumber+", �������� "+owner+", � �� ��� "+gosNumber);
		System.out.println("������� ������ � ����� ������� � ������� Devices");
		sqlString = "SELECT id FROM [Devices] WHERE name='" + name + "' AND type='" + type + "' AND serialNumber='" + serialNumber + "'";
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlString);
		System.out.println(sqlString);
		System.out.println("������� id ��� ������� �������� ������: " + this.id);
		for (int i=0; i < this.countOfElements; i++){
			System.out.println("������ ���������� �������� �" + i + ": ");
			//���������� � �� ������ � ��������� ���������
			includedElements.get(i).saveInDB(); 		
		}		
	}
	
	@Override
	public void deleteFromDB() throws SQLException {	
		String sqlQuery = "DELETE FROM [Devices] WHERE id="+this.id;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		this.includedElements.clear();
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		String sqlQuery = "UPDATE Devices SET ";
		Class<? extends Device> deviceClass = this.getClass();		
		Iterator<String> it = editingValues.keySet().iterator();
		do {
			String fieldName = it.next();
			try {
				Field anyField = deviceClass.getDeclaredField(fieldName);
				String oldAnyFieldValue = (String) anyField.get(this);
				String newAnyFieldValue = editingValues.get(fieldName);
				if(!oldAnyFieldValue.equals(newAnyFieldValue)) {
					sqlQuery += (fieldName + "='"+newAnyFieldValue+"', ");
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exp) {
				exp.printStackTrace();
			}
		} while(it.hasNext());
		sqlQuery = sqlQuery.substring(0, sqlQuery.length()-2);
		sqlQuery += " WHERE id=" + this.id;
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
	
	public void removeElement(Element element) {
		int elementIndex = this.includedElements.indexOf(element);
		this.includedElements.remove(elementIndex);
		this.countOfElements--;
	}
	
	public void createIniFile(String psiFilePath) throws IOException {
		ArrayList<String> fileStrings = new ArrayList<String>();
		fileStrings.add("��������� �������:\n");
		fileStrings.add("1.������������ ���� �������� ��������� <" + name + ">\n");
		fileStrings.add("2.��� ����������� �������� ��������� <" + type + ">\n");
		fileStrings.add("3.��������� ����� <" + serialNumber + ">\n");
		fileStrings.add("4.� ������ ������ ��������� � ���������� <" + Integer.toString(countOfElements) + ">\n");
		fileStrings.add("------------------------------------------------------------------\n");
		for (int i = 0; i < countOfElements; i++) {
			fileStrings.add("������� � " + Integer.toString(i) + ": " + includedElements.get(i).getType() + "\n");
			fileStrings.add("�������� �����: " + includedElements.get(i).getSerialNumber() + "\n");
			String ruTextOfMeasUnit;
			if (this.includedElements.get(i).getMeasUnit().equals("vswr")) {
				ruTextOfMeasUnit = "����";
			}
			else {
				ruTextOfMeasUnit = "������";
			}
			fileStrings.add("���������� ��������: " + ruTextOfMeasUnit + "\n");
			String paramsStr = "S11";
			if (includedElements.get(i).getPoleCount() == 4) {
				paramsStr += ", S12, S21, S22";
			}				
			fileStrings.add("���������: " + paramsStr + "\n");
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