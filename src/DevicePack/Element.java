package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import Exceptions.NoOwnerException;
import NewElementPack.NewElementController;
import ToleranceParamPack.PercentTolerance;
import ToleranceParamPack.ToleranceParametrs;
import ToleranceParamPack.UpDownTolerance;
import VerificationPack.Gamma_Result;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;

public class Element implements Includable<Device>, dbStorable{

	public Element() {
		
	}
	
	@Override
	public void onAdding(Device Owner) {
		this.myDevice = Owner;	
		String addrStr = this.myDevice.getName() + " " + this.myDevice.getType() + " " +this.myDevice.getSerialNumber() + " "+
				 this.type + " " + this.serialNumber;
		this.periodicParamTable = "Критерии периодической поверки для " + addrStr;
		this.primaryParamTable = "Критерии первичной поверки для " + addrStr;
		this.listOfVerificationsTable = "Список таблиц с результатами измерений для " + addrStr;
	}
	
	private String type;
	private String serialNumber;
	private int poleCount;
	private String measUnit;
	private String toleranceType;
	private int nominalIndex;
	
	private String periodicParamTable;
	private String primaryParamTable;
	private String nominalTable;
	private String listOfVerificationsTable;
	
	private Device myDevice; 		//Устройство, которому принадлежит элемент
	private MeasResult nominal;		//Номиналы значений
	private ToleranceParametrs primaryToleranceParams;	// 
	private ToleranceParametrs periodicToleranceParams;
	
	//Конструктор, инициализирующий объект информацией из БД
	//Вызывается при инициализации устройства перед поверкой
	public Element(Device deviceOwner, int index) throws SQLException{
		
		this.myDevice = deviceOwner;
		String elementTableName = myDevice.getElementsTableName();
		String sqlQuery = "SELECT ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, VerificationsTable, PeriodicParamTable, PrimaryParamTable, NominalIndex FROM [" + elementTableName + "] WHERE id='"+index+"'";
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("ElementType");
		fieldName.add("ElementSerNumber");
		fieldName.add("PoleCount");
		fieldName.add("MeasUnit");
		fieldName.add("ToleranceType");
		fieldName.add("VerificationsTable");
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
		this.listOfVerificationsTable = arrayResults.get(0).get(5);
		this.periodicParamTable = arrayResults.get(0).get(6);
		this.primaryParamTable = arrayResults.get(0).get(7);
		this.nominalIndex = Integer.parseInt(arrayResults.get(0).get(8));		
		//Получим номиналы на элемент 
		if (this.measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(this, nominalIndex);
		}
		else {
			this.nominal = new Gamma_Result(this, nominalIndex);
		}		 
		//Получим критерии годности на элемент
		if (toleranceType.equals("percent")) {
			this.periodicToleranceParams = new PercentTolerance("periodic", this);
			this.primaryToleranceParams = new PercentTolerance("primary", this);
		}
		else {
			this.periodicToleranceParams = new UpDownTolerance("periodic", this);
			this.primaryToleranceParams = new UpDownTolerance("primary", this);
		}
	}
	
	//Конструктор, инициализирующий объект информацией из Графического интерфейса для последующего сохранения в БД
	//Вызывается при инициализации устройства перед сохранением нового устройства в БД 
	//передаем в качестве парамерами ссылку на контроллер окна, из которого надо взять информацию
	public Element(NewElementController elCtrl) {			
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
	}

	//getters
	public String getType() {return this.type;}
	public String getSerialNumber() {return this.serialNumber;}
	public int getPoleCount() {return this.poleCount;}
	public String getMeasUnit() {return this.measUnit;}
	public String getToleranceType() {return this.toleranceType;}
	public String getPeriodicParamTable() {return periodicParamTable;}
	public String getPrimaryParamTable() {return primaryParamTable;}
	public String getNominaltable() {return nominalTable;}
	public String getListOfVerificationsTable() {return listOfVerificationsTable;}
	public MeasResult getNominal() {return nominal;}
	public ToleranceParametrs getPrimaryToleranceParams() {return primaryToleranceParams;}	// 
	public ToleranceParametrs getPeriodicToleranceParams() {return periodicToleranceParams;}
	@Override
	public Device getMyOwner() {return this.myDevice;}
	
//dbStorable		
	@Override
	public void saveInDB() throws SQLException, NoOwnerException {
		if (this.myDevice == null) {
			throw new NoOwnerException(this);
		}
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();
		String strElementsTable = this.myDevice.getElementsTableName();
		// 3.1 Внесли запись об элементе
		String sqlString = "INSERT INTO ["+strElementsTable+"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ToleranceType, VerificationsTable, PeriodicParamTable, PrimaryParamTable, NominalIndex) values ('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+toleranceType+"','"+listOfVerificationsTable+"','"+periodicParamTable +"','"+primaryParamTable+"','"+Integer.toString(nominalIndex)+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("\tЭлемент зарегистрирован");
		// 3.2 Создание таблицы [Список таблиц с результатами измерений для ...]
		sqlString = "CREATE TABLE [Список таблиц с результатами измерений для " + (addStr + " " + this.type + " " + this.serialNumber) + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, dateOfVerification VARCHAR(60), resultsTableName VARCHAR(512))";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("\tСоздана таблица для списков проведенных измерений");
		// 3.3 Сохранить критерии годности
		System.out.println("\tНачато сохранение критериев годности:");
		System.out.println("\t---------------первичной поверки-----------------------:");
		this.primaryToleranceParams.saveInDB();	
		System.out.println("\t---------------периодическойй поверки-----------------------:");
		this.periodicToleranceParams.saveInDB();		
		System.out.println("\tНачато сохранение номинала:");
		// 3.4 Сохраняем номиналы
		this.nominal.saveInDB();	
		System.out.println("\tПроцедура сохранения завершена");
	}
	
	@Override
	public void deleteFromDB() throws SQLException {		
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();	
		String strElementsTable = this.myDevice.getElementsTableName();
		String measurementsOfTableName = "Список таблиц с результатами измерений для " + addStr + " " + this.type + " " + this.serialNumber;		
		//Удалить номинал
		this.nominal.deleteFromDB();
		//Удалить критерии годности
		this.periodicToleranceParams.deleteFromDB();
		this.primaryToleranceParams.deleteFromDB();		
		//Удалить таблицу Measurements of 		
		String sqlString = "DROP TABLE ["+measurementsOfTableName+"]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		//Удалить запись об элементе из таблицы Elements of
		sqlString = "DELETE FROM ["+strElementsTable+"] WHERE ElementType='"+type+"' AND ElementSerNumber='"+serialNumber+"'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);		
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		String tableName = this.myDevice.getElementsTableName();
		String sqlQuery = "UPDATE " + tableName + " SET ";
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
		sqlQuery += "WHERE ElementType='"+this.type+"' AND ElementSerNumber='"+this.serialNumber+"'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);	
	}
	
	public ArrayList<ArrayList<String>> getListOfVerifications() throws SQLException {		
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();	
		String measurementsOfTableName = "Список таблиц с результатами измерений для " + addStr + " " + this.type + " " + this.serialNumber;
		String sqlString = "SELECT id, dateOfVerification, resultsTableName FROM ["+measurementsOfTableName+"]";
		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("id");
		fieldName.add("dateOfVerification");
		fieldName.add("resultsTableName");
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();
		
		DataBaseManager.getDB().sqlQueryString(sqlString, fieldName, arrayResults);
			
		return arrayResults;
	}

	public void rewriteParams(ToleranceParametrs newPrimaryparams,
							  ToleranceParametrs newPeriodicalParams,
							  MeasResult newNominals) throws SQLException{
		this.primaryToleranceParams.deleteFromDB();
		this.periodicToleranceParams.deleteFromDB();
		this.nominal.deleteFromDB();
		
		newPrimaryparams.saveInDB();
		newPeriodicalParams.saveInDB();
		newNominals.saveInDB();
	}
}
