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
	//Конструктор, инициализирующий объект информацией из БД
	//Вызывается при инициализации устройства перед поверкой
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
	
	//Конструктор, инициализирующий объект информацией из Графического интерфейса для последующего сохранения в БД
	//Вызывается при инициализации устройства перед сохранением нового устройства в БД
	public Element(String Type, String SerialNumber, int PoleCount, String MeasUnit, String ToleranceType, Device MyDevice){
		this.type = Type;
		this.serialNumber = SerialNumber;
		this.poleCount = PoleCount;
		this.measUnit = MeasUnit;
		this.toleranceType = ToleranceType;
		this.myDevice = MyDevice; 
	}
	
	protected String type;
	protected String serialNumber;
	protected int poleCount;
	protected String measUnit;
	protected String toleranceType;
	protected String periodicParamTable;
	protected String primaryParamTable;
	
	protected Device myDevice;
	private MeasResult nominal;
		
	public String getType() {return this.type;}
	public String getSerialNumber() {return this.serialNumber;}
	public int getPoleCount() {return this.poleCount;}
	public String getMeasUnit() {return this.measUnit;}
	public String getToleranceType() {return this.toleranceType;}
	
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
		//Внесли запись об элементе
		sqlString = "INSERT INTO ["+strElementsTable+"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, PeriodicParamTable, PrimaryParamTable) values ('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+toleranceType+"','"+ePerParamTable+"','"+ePrimParamTable+"')";
		AksolDataBase.sqlQueryUpdate(sqlString);
		//Создание таблиц для первичных и переодических параметров годности
		sqlString = "CREATE TABLE ["+ePerParamTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq REAL, m_s11_d REAL, m_s11_n REAL, m_s11_u REAL, p_s11_d REAL, p_s11_n REAL,  p_s11_u REAL)";
		AksolDataBase.sqlQueryUpdate(sqlString);
		sqlString = "CREATE TABLE ["+ePrimParamTable+"] (id INTEGER PRIMARY KEY AUTOINCREMENT, freq REAL, m_s11_d REAL, m_s11_n REAL, m_s11_u REAL, p_s11_d REAL, p_s11_n REAL,  p_s11_u REAL)";
		AksolDataBase.sqlQueryUpdate(sqlString);
		//Заполняем таблицы с параметрами
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
	//критерии годности
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
