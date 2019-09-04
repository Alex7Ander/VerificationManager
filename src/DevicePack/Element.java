package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import NewElementPack.NewElementController;
import ToleranceParamPack.PercentTolerance;
import ToleranceParamPack.ToleranceParametrs;
import ToleranceParamPack.UpDownTolerance;
import VerificationPack.Gamma_Result;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;
import VerificationPack.Verificatable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Element implements Includable<Device>, Verificatable, dbStorable{

	public Element() {
		
	}
	
	private String type;
	private String serialNumber;
	private int poleCount;
	private String measUnit;
	private String toleranceType;
	private int nominalIndex;
	
	private String periodicParamTable;
	private String primaryParamTable;
	private String listOfVerificationsTable;
	
	private Device myDevice; 		//����������, �������� ����������� �������
	private MeasResult nominal;		//�������� ��������
	private ToleranceParametrs primaryToleranceParams;	// 
	private ToleranceParametrs periodicToleranceParams;
	
	//�����������, ���������������� ������ ����������� �� ��
	//���������� ��� ������������� ���������� ����� ��������
	public Element(Device deviceOwner, int index) throws SQLException{
		
		this.myDevice = deviceOwner;
		String elementTableName = myDevice.getElementsTableName();
		String sqlQuery = "SELECT ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, PeriodicParamTable, PrimaryParamTable, NominalIndex FROM [" + elementTableName + "] WHERE id='"+index+"'";
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("ElementType");
		fieldName.add("ElementSerNumber");
		fieldName.add("PoleCount");
		fieldName.add("MeasUnit");
		fieldName.add("ToleranceType");
		fieldName.add("PeriodicParamTable");
		fieldName.add("PrimaryParamTable");
		fieldName.add("NominalIndex");
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		
		
		this.type = arrayResults.get(0).get(0);
		this.serialNumber = arrayResults.get(0).get(1);
		this.poleCount = Integer.parseInt(arrayResults.get(0).get(2));
		this.measUnit = arrayResults.get(0).get(3);
		this.toleranceType = arrayResults.get(0).get(4);		
		this.periodicParamTable = arrayResults.get(0).get(5);
		this.primaryParamTable = arrayResults.get(0).get(6);
		this.nominalIndex = Integer.parseInt(arrayResults.get(0).get(7));
		String addrStr = this.myDevice.getName() + " " + this.myDevice.getType() + " " +this.myDevice.getSerialNumber() + " "+
				 this.type + " " + this.serialNumber;
		this.listOfVerificationsTable = "������ ������ � ������������ ��������� ��� " + addrStr;
		//������� �������� �� ������� 
		if (this.measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(this, nominalIndex);
		}
		else {
			this.nominal = new Gamma_Result(this, nominalIndex);
		}		 
		//������� �������� �������� �� �������
		if (toleranceType.equals("percent")) {
			this.periodicToleranceParams = new PercentTolerance("periodic", this);
			this.primaryToleranceParams = new PercentTolerance("primary", this);
		}
		else {
			this.periodicToleranceParams = new UpDownTolerance("periodic", this);
			this.primaryToleranceParams = new UpDownTolerance("primary", this);
		}
	}
	
	//�����������, ���������������� ������ ����������� �� ������������ ���������� ��� ������������ ���������� � ��
	//���������� ��� ������������� ���������� ����� ����������� ������ ���������� � �� 
	public Element(NewElementController elCtrl, Device device) {
		
		this.myDevice = device;
		
		this.type = elCtrl.getType();
		this.serialNumber = elCtrl.getSerNum();
		this.poleCount = elCtrl.getPoleCount();
		this.measUnit = elCtrl.getMeasUnit();
		this.toleranceType = elCtrl.getToleranceType();
		this.nominalIndex = 1;
		
		if (this.measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(elCtrl, this);
		}
		else {
			this.nominal = new Gamma_Result(elCtrl, this);
		}
		
		if (this.toleranceType.equals("percent")) {
			this.primaryToleranceParams = new PercentTolerance("primary", elCtrl, this);
			this.periodicToleranceParams = new PercentTolerance("periodic", elCtrl, this);
		}
		else {
			this.primaryToleranceParams = new UpDownTolerance("primary", elCtrl, this);
			this.periodicToleranceParams = new UpDownTolerance("periodic", elCtrl, this);
		}
		
		String addrStr = this.myDevice.getName() + " " + this.myDevice.getType() + " " +this.myDevice.getSerialNumber() + " "+
						 this.type + " " + this.serialNumber;
		this.periodicParamTable = "�������� ������������� ������� ��� " + addrStr;
		this.primaryParamTable = "�������� ��������� ������� ��� " + addrStr;
		this.listOfVerificationsTable = "������ ������ � ������������ ��������� ��� " + addrStr;
	}

	//getters
	public String getType() {return this.type;}
	public String getSerialNumber() {return this.serialNumber;}
	public int getPoleCount() {return this.poleCount;}
	public String getMeasUnit() {return this.measUnit;}
	public String getToleranceType() {return this.toleranceType;}
	public String getPeriodicParamTable() {return periodicParamTable;}
	public String getPrimaryParamTable() {return primaryParamTable;}
	public String getListOfVerificationsTable() {return listOfVerificationsTable;}
	public MeasResult getNominal() {return nominal;}
	public ToleranceParametrs getPrimaryToleranceParams() {return primaryToleranceParams;}	// 
	public ToleranceParametrs getPeriodicToleranceParams() {return periodicToleranceParams;}
	@Override
	public Device getMyOwner() {return this.myDevice;}
	
//dbStorable		
	@Override
	public void saveInDB() throws SQLException {
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();
		String strElementsTable = this.myDevice.getElementsTableName();
		// 3.1 ������ ������ �� ��������
		String sqlString = "INSERT INTO ["+strElementsTable+"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, PeriodicParamTable, PrimaryParamTable, NominalIndex) values ('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+toleranceType+"','"+periodicParamTable +"','"+primaryParamTable+"','"+Integer.toString(nominalIndex)+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		// 3.2 �������� ������� [������ ������ � ������������ ��������� ��� ...]
		sqlString = "CREATE TABLE [������ ������ � ������������ ��������� ��� " + (addStr + " " + this.type + " " + this.serialNumber) + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, dateOfVerification VARCHAR(60), resultsTableName VARCHAR(512))";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		// 3.3 ��������� �������� ��������
		this.primaryToleranceParams.saveInDB();	
		this.periodicToleranceParams.saveInDB();	
		// 3.4 ��������� ��������
		this.nominal.saveInDB();		
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();	
		String strElementsTable = this.myDevice.getElementsTableName();
		String measurementsOfTableName = "������ ������ � ������������ ��������� ��� " + addStr + " " + this.type + " " + this.serialNumber;
		
		//������� �������
		this.nominal.deleteFromDB();
		//������� �������� ��������
		this.periodicToleranceParams.deleteFromDB();
		this.primaryToleranceParams.deleteFromDB();		
		//������� ������� Measurements of 		
		String sqlString = "DROP TABLE ["+measurementsOfTableName+"]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		//������� ������ �� �������� �� ������� Elements of
		sqlString = "DELETE FROM ["+strElementsTable+"] WHERE ElementType='"+type+"' AND ElementSerNumber='"+serialNumber+"'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);		
	}
	
	@Override
	public void editInfoInDB() throws SQLException {
		
	}
	
	@Override
	public void getData() {
		
	}
//----------------------------------------	
	@Override
	public boolean isSuitable(MeasResult result, HashMap<Double, Double> report) {
		boolean resultOfVerification = false;
		//
		return resultOfVerification;
	}
	
	public ArrayList<ArrayList<String>> getListOfVerifications() throws SQLException {
		
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();	
		String strElementsTable = this.myDevice.getElementsTableName();
		String measurementsOfTableName = "������ ������ � ������������ ��������� ��� " + addStr + " " + this.type + " " + this.serialNumber;
		String sqlString = "SELECT id, dateOfVerification, resultsTableName FROM ["+measurementsOfTableName+"]";
		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("id");
		fieldName.add("dateOfVerification");
		fieldName.add("resultsTableName");
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();
		
		DataBaseManager.getDB().sqlQueryString(sqlString, fieldName, arrayResults);
			
		return arrayResults;
	}

}
