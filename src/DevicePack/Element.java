package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import ToleranceParamPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import VerificationPack.Verificatable;

public class Element implements Includable<Device>, Verificatable, dbStorable{

	public Element() {
		
	}
	
	//�����������, ���������������� ������ ����������� �� ��
	//���������� ��� ������������� ���������� ����� ��������
	public Element(String ElementTableName, int index) throws SQLException{
		String sqlQuery = "SELECT ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, PeriodicParamTable, PrimaryParamTable FROM " + ElementTableName + " WHERE id="+index+"";
		ArrayList<String> fieldName = new ArrayList<String>();
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		this.type = arrayResults.get(0).get(0);
		this.serialNumber = arrayResults.get(0).get(1);
		this.poleCount = Integer.parseInt(arrayResults.get(0).get(2));
		this.measUnit = arrayResults.get(0).get(3);
		this.toleranceType = arrayResults.get(0).get(4);
		this.periodicParamTable = arrayResults.get(0).get(5);
		this.primaryParamTable = arrayResults.get(0).get(6);
	}
	
	//�����������, ���������������� ������ ����������� �� ������������ ���������� ��� ������������ ���������� � ��
	//���������� ��� ������������� ���������� ����� ����������� ������ ���������� � ��
	public Element(String Type, String SerialNumber, int PoleCount, String MeasUnit, String ToleranceType, Device MyDevice, MeasResult Nominal, ToleranceParametrs PrimaryTolParams, ToleranceParametrs PeriodicTolParams){
		this.type = Type;
		this.serialNumber = SerialNumber;
		this.poleCount = PoleCount;
		this.measUnit = MeasUnit;
		this.toleranceType = ToleranceType;
		this.myDevice = MyDevice; 
		this.listOfVerificationsTable = "Verifications of " + this.myDevice.getName() + " " +
				this.myDevice.getType() + " " + this.myDevice.getSerialNumber() + " "+
				this.type + " " + this.serialNumber;
			
		String addrStr = this.myDevice.getName() + " " + this.myDevice.getType() + " " +this.myDevice.getSerialNumber() + " "+
				this.type + " " + this.serialNumber;
		this.primaryParamTable = "Primary for " + addrStr;
		this.periodicParamTable = "Periodic for " + addrStr;
		
		this.nominal = Nominal;
		this.primaryToleranceParams =  PrimaryTolParams;	
		this.periodicToleranceParams = PeriodicTolParams;
	}
	
	protected String type;
	protected String serialNumber;
	protected int poleCount;
	protected String measUnit;
	protected String toleranceType;
	
	protected String periodicParamTable;
	protected String primaryParamTable;
	protected String listOfVerificationsTable;
	
	protected Device myDevice; 		//����������, �������� ����������� �������
	private MeasResult nominal;		//�������� ��������
	private ToleranceParametrs primaryToleranceParams;	// 
	private ToleranceParametrs periodicToleranceParams;
	
	//getters
	public String getType() {return this.type;}
	public String getSerialNumber() {return this.serialNumber;}
	public int getPoleCount() {return this.poleCount;}
	public String getMeasUnit() {return this.measUnit;}
	public String getToleranceType() {return this.toleranceType;}
	public String getPeriodicParamTable() {return periodicParamTable;}
	public String getPrimaryParamTable() {return primaryParamTable;}
	public String getListOfVerificationsTable() {return listOfVerificationsTable;}
	@Override
	public Device getMyOwner() {return this.myDevice;}
	
//dbStorable		
	@Override
	public void saveInDB() throws SQLException {
		String sqlString;
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();
		String strElementsTable = "Elements of " + addStr;
		String ePerParamTable  = "Periodic for " + addStr + " " + this.type + " " + this.serialNumber;
		String ePrimParamTable = "Primary for " + addStr + " " + this.type + " " + this.serialNumber;
		// 3.1 ������ ������ �� ��������
		sqlString = "INSERT INTO ["+strElementsTable+"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, PeriodicParamTable, PrimaryParamTable) values ('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+toleranceType+"','"+ePerParamTable+"','"+ePrimParamTable+"')";
		AksolDataBase.sqlQueryUpdate(sqlString);
		// 3.2 �������� ������� [Verification of ...]
		sqlString = "CREATE TABLE Verification of " + (addStr + " " + this.type + " " + this.serialNumber) + "(id INTEGER PRIMARY KEY AUTOINCREMENT, dateOfVerification VARCHAR(20), resultsTableName (512))";
		// 3.3 ��������� �������� ��������
		//this.toleranceParams.saveInDB();	
		// 3.4 ��������� ��������
		this.nominal.saveInDB();		
	}
	@Override
	public void deleteFromDB() {
		
	}
	@Override
	public void editInfoInDB() {
		
	}
	@Override
	public void getData() {
		
	}
//----------------------------------------	
	//�������� ��������
	public ToleranceParametrs periodicParametrs;
	public ToleranceParametrs primaryParametrs;
	//-----------------	
	@Override
	public boolean isSuitable(MeasResult result, HashMap<Double, Double> report) {
		boolean resultOfVerification = false;
		if (result.getPeriodType().equals("periodic")) resultOfVerification = periodicParametrs.checkResult(result, report);
		else if (result.getPeriodType().equals("primary")) resultOfVerification = primaryParametrs.checkResult(result, report);
		return resultOfVerification;
	}

}
