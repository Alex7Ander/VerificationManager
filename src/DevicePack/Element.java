package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import Exceptions.NoOwnerException;
import NewElementPack.NewElementController;
import ToleranceParamPack.Parametrspack.ToleranceParametrs;
import ToleranceParamPack.StrategyPack.percentStrategy;
import ToleranceParamPack.StrategyPack.upDownStrategy;
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
		this.periodicModuleParamTable = "Критерии годности Модуля периодической поверки для " + addrStr;
		this.primaryModuleParamTable = "Критерии первичной поверки для " + addrStr;
		this.periodicPhaseParamTable = "Критерии годности Фазы периодической поверки для " + addrStr;
		this.primaryPhaseParamTable = "Критерии годности Фазы первичной поверки " + addrStr;		
		this.listOfVerificationsTable = "Список таблиц с результатами измерений для " + addrStr;
	}
	
	private String type;
	private String serialNumber;
	private int poleCount;
	private String measUnit;
	private int nominalIndex;
	
	//Маркеры типов критериев годнсоти
	private String moduleToleranceType;
	private String phaseToleranceType;
	public String getModuleToleranceType() {return this.moduleToleranceType;}
	public String getPhaseToleranceType() {return this.phaseToleranceType;}
	
	//Названия таблиц для критериев годности, номиналов и списка проведенных поверок
	private String periodicModuleParamTable;
	private String primaryModuleParamTable;
	private String periodicPhaseParamTable;
	private String primaryPhaseParamTable;	
	private String nominalTable;
	private String listOfVerificationsTable;
	public String getPeriodicModuleParamTable() {return this.periodicModuleParamTable;}
	public String getPrimaryModuleParamTable() {return this.primaryModuleParamTable;}
	public String getPeriodicPhaseParamTable() {return this.periodicPhaseParamTable;}
	public String getPrimaryPhaseParamTable() {return this.primaryPhaseParamTable;}	
	public String getNominalTable() {return this.nominalTable;}
	public String getListOfVerificationsTable() {return this.listOfVerificationsTable;}
	
	private Device myDevice; 		//Устройство, которому принадлежит элемент
	private MeasResult nominal;		//Номиналы значений
	private ToleranceParametrs primaryModuleToleranceParams;	// 
	private ToleranceParametrs periodicModuleToleranceParams;
	private ToleranceParametrs primaryPhaseToleranceParams;
	private ToleranceParametrs periodicPhaseToleranceParams;
	
	//Конструктор, инициализирующий объект информацией из БД
	//Вызывается при инициализации устройства перед поверкой или редактированием
	public Element(Device deviceOwner, int index) throws SQLException{		
		this.myDevice = deviceOwner;
		String elementTableName = myDevice.getElementsTableName();
		String sqlQuery = "SELECT ElementType, ElementSerNumber, PoleCount, MeasUnit, ModuleToleranceType, PhaseToleranceType, VerificationsTable, vv NominalIndex FROM [" + elementTableName + "] WHERE id='"+index+"'";
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("ElementType");				//0
		fieldName.add("ElementSerNumber");			//1
		fieldName.add("PoleCount");					//2
		fieldName.add("MeasUnit");					//3
		fieldName.add("ModuleToleranceType");		//4
		fieldName.add("PhaseToleranceType");		//5
		fieldName.add("VerificationsTable");		//6		
		fieldName.add("PrimaryModuleParamTable");	//7
		fieldName.add("PeriodicModuleParamTable");	//8
		fieldName.add("PrimaryPhaseParamTable");	//9
		fieldName.add("PeriodicPhaseParamTable");	//10		
		fieldName.add("NominalIndex");				//11
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);				
		this.type = arrayResults.get(0).get(0);
		this.serialNumber = arrayResults.get(0).get(1);
		this.poleCount = Integer.parseInt(arrayResults.get(0).get(2));
		this.measUnit = arrayResults.get(0).get(3);
		this.moduleToleranceType = arrayResults.get(0).get(4);
		this.phaseToleranceType = arrayResults.get(0).get(5);		
		this.listOfVerificationsTable = arrayResults.get(0).get(6);
		this.primaryModuleParamTable = arrayResults.get(0).get(7);
		this.periodicModuleParamTable = arrayResults.get(0).get(8);
		this.primaryPhaseParamTable = arrayResults.get(0).get(9);
		this.periodicPhaseParamTable = arrayResults.get(0).get(10);
		this.nominalIndex = Integer.parseInt(arrayResults.get(0).get(11));			
		arrayResults.clear();
		fieldName.clear();
		fieldName.add("resultsTableName");
		sqlQuery = "SELECT resultsTableName FROM ["+ this.listOfVerificationsTable +"] WHERE id = "+Integer.toString(this.nominalIndex)+"";
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		this.nominalTable = arrayResults.get(0).get(0);
		
		//Получим номиналы на элемент 
		if (this.measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(this, nominalIndex);
		}
		else {
			this.nominal = new Gamma_Result(this, nominalIndex);
		}		 
		//Получим критерии годности на элемент
		this.primaryModuleToleranceParams = new ToleranceParametrs("primary", this, "m_");
		this.periodicModuleToleranceParams = new ToleranceParametrs("periodic", this, "m_");
		this.primaryPhaseToleranceParams = new ToleranceParametrs("primary", this, "p_");
		this.periodicPhaseToleranceParams = new ToleranceParametrs("periodic", this, "p_");
		/*
		if (toleranceType.equals("percent")) {
			this.periodicToleranceParams = new PercentTolerance("periodic", this);
			this.primaryToleranceParams = new PercentTolerance("primary", this);
		}
		else {
			this.periodicToleranceParams = new UpDownTolerance("periodic", this);
			this.primaryToleranceParams = new UpDownTolerance("primary", this);
		}
		*/
	}
	
	//Конструктор, инициализирующий объект информацией из Графического интерфейса для последующего сохранения в БД
	//Вызывается при инициализации устройства перед сохранением нового устройства в БД 
	//передаем в качестве парамерами ссылку на контроллер окна, из которого надо взять информацию
	public Element(NewElementController elCtrl) {			
		this.type = elCtrl.getType();
		this.serialNumber = elCtrl.getSerNum();
		this.poleCount = elCtrl.getPoleCount();
		this.measUnit = elCtrl.getMeasUnit();
		this.moduleToleranceType = elCtrl.getModuleToleranceType();
		this.phaseToleranceType = elCtrl.getPhaseToleranceType();
		this.nominalIndex = 1;
		
		if (this.measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(elCtrl, this);
		}
		else {
			this.nominal = new Gamma_Result(elCtrl, this);
		}
		
		this.primaryModuleToleranceParams = new ToleranceParametrs("primary", elCtrl, this, "m_");
		this.primaryPhaseToleranceParams = new ToleranceParametrs("primary", elCtrl, this, "p_");		
		this.periodicModuleToleranceParams = new ToleranceParametrs("periodic", elCtrl, this, "m_");
		this.periodicPhaseToleranceParams = new ToleranceParametrs("periodic", elCtrl, this, "p_");		
		percentStrategy percentStr = new percentStrategy();
		upDownStrategy udStr = new upDownStrategy();
		if (this.moduleToleranceType.equals("updown")) {
			primaryModuleToleranceParams.setStratege(udStr);
			periodicModuleToleranceParams.setStratege(udStr);
		} else {			
			primaryModuleToleranceParams.setStratege(percentStr);
			periodicModuleToleranceParams.setStratege(percentStr);
		}
		if (this.phaseToleranceType.equals("updown")) {
			primaryPhaseToleranceParams.setStratege(udStr);
			periodicPhaseToleranceParams.setStratege(udStr);
		} else {
			primaryPhaseToleranceParams.setStratege(percentStr);
			periodicPhaseToleranceParams.setStratege(percentStr);
		}
	}

	//getters
	public String getType() {return this.type;}
	public String getSerialNumber() {return this.serialNumber;}
	public int getPoleCount() {return this.poleCount;}
	public String getMeasUnit() {return this.measUnit;}
	public String getNominaltable() {return nominalTable;}
	public MeasResult getNominal() {return nominal;}
	public ToleranceParametrs getPrimaryModuleToleranceParams() {return primaryModuleToleranceParams;}	// 
	public ToleranceParametrs getPeriodicModuleToleranceParams() {return periodicModuleToleranceParams;}
	public ToleranceParametrs getPrimaryPhaseToleranceParams() {return primaryPhaseToleranceParams;}	// 
	public ToleranceParametrs getPeriodicPhaseToleranceParams() {return periodicPhaseToleranceParams;}
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
		// 3.1 Внесли запись об элементе в таблицу Devices
		String sqlString = "INSERT INTO ["+strElementsTable+"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ModuleToleranceType, PhaseToleranceType, VerificationsTable, PrimaryModuleParamTable, PeriodicModuleParamTable, PrimaryPhaseParamTable, PeriodicPhaseParamTable, NominalIndex) values "
				+ "('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+this.moduleToleranceType+"','"+this.phaseToleranceType+"','"+listOfVerificationsTable+"','" + this.primaryModuleParamTable + "','"+this.periodicModuleParamTable+"','"+this.primaryPhaseParamTable+"','"+this.periodicPhaseParamTable+"','"+Integer.toString(nominalIndex)+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("\tЭлемент зарегистрирован");
		// 3.2 Создание таблицы [Список таблиц с результатами измерений для ...]
		sqlString = "CREATE TABLE [Список таблиц с результатами измерений для " + (addStr + " " + this.type + " " + this.serialNumber) + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, dateOfVerification VARCHAR(60), resultsTableName VARCHAR(512))";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		System.out.println("\tСоздана таблица для списков проведенных измерений");
		// 3.3 Сохранить критерии годности
		System.out.println("\tНачато сохранение критериев годности:");
		System.out.println("\t---------------первичной поверки-----------------------:");
		this.primaryModuleToleranceParams.saveInDB();	
		this.primaryPhaseToleranceParams.saveInDB();
		System.out.println("\t---------------периодическойй поверки-----------------------:");
		this.periodicModuleToleranceParams.saveInDB();	
		this.periodicPhaseToleranceParams.saveInDB();
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
		this.primaryModuleToleranceParams.deleteFromDB();
		this.primaryPhaseToleranceParams.deleteFromDB();
		this.periodicModuleToleranceParams.deleteFromDB();
		this.periodicPhaseToleranceParams.deleteFromDB();
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

	public void rewriteParams(ToleranceParametrs newModulePrimaryParams, ToleranceParametrs newModulePeriodicParams,
							  ToleranceParametrs newPhasePrimaryParams, ToleranceParametrs newPhasePeriodicparams, MeasResult newNominals) throws SQLException{
		this.primaryModuleToleranceParams.deleteFromDB();
		this.primaryPhaseToleranceParams.deleteFromDB();
		this.periodicModuleToleranceParams.deleteFromDB();
		this.periodicPhaseToleranceParams.deleteFromDB();
		this.nominal.deleteFromDB();
		
		newModulePrimaryParams.saveInDB();
		newModulePeriodicParams.saveInDB();
		newPhasePrimaryParams.saveInDB();
		newPhasePeriodicparams.saveInDB();
		newNominals.saveInDB();
	}
}
