package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import Exceptions.NoOwnerException;
import Exceptions.SavingException;
import NewElementPack.NewElementController;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.TimeType;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import ToleranceParamPack.StrategyPack.percentStrategy;
import ToleranceParamPack.StrategyPack.upDownStrategy;
import VerificationPack.Gamma_Result;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;

public class Element implements Includable<Device>, dbStorable{

	public Element() {
		
	}
	
	private Device myDevice; 		
	private MeasResult nominal;		
	private ToleranceParametrs primaryModuleToleranceParams;	// 
	private ToleranceParametrs periodicModuleToleranceParams;
	private ToleranceParametrs primaryPhaseToleranceParams;
	private ToleranceParametrs periodicPhaseToleranceParams;
	private String type;
	private String serialNumber;
	private int poleCount;
	private int sParamsCount;
	private String measUnit;
	private int nominalIndex;
	
	private String periodicModuleParamTable;
	private String primaryModuleParamTable;
	private String periodicPhaseParamTable;
	private String primaryPhaseParamTable;	
	private String nominalTable;
	
	private String listOfVerificationsTable;
	private String moduleToleranceType;
	private String phaseToleranceType;
	public String getModuleToleranceType() {
		return moduleToleranceType;
	}
	public String getPhaseToleranceType() {
		return phaseToleranceType;
	}	
	public String getPeriodicModuleParamTable() {
		return periodicModuleParamTable;
	}
	public String getPrimaryModuleParamTable() {
		return primaryModuleParamTable;
	}
	public String getPeriodicPhaseParamTable() {
		return periodicPhaseParamTable;
	}
	public String getPrimaryPhaseParamTable() {
		return primaryPhaseParamTable;
	}	
	public String getNominalTable() {
		return nominalTable;
	}
	public String getListOfVerificationsTable() {
		return listOfVerificationsTable;
	}
	public String getType() {
		return type;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public int getPoleCount() {
		return poleCount;
	}
	public int getSParamsCout() {
		return sParamsCount;
	}
	public String getMeasUnit() {
		return measUnit;
	}

	public MeasResult getNominal() {
		return nominal;
	}
	public ToleranceParametrs getPrimaryModuleToleranceParams() {
		return primaryModuleToleranceParams;
	}
	public ToleranceParametrs getPeriodicModuleToleranceParams() {
		return periodicModuleToleranceParams;
	}
	public ToleranceParametrs getPrimaryPhaseToleranceParams() {
		return primaryPhaseToleranceParams;
	}
	public ToleranceParametrs getPeriodicPhaseToleranceParams() {
		return periodicPhaseToleranceParams;
	}
	public ToleranceParametrs getToleranceParametrs(TimeType time, MeasUnitPart unit) {		
		switch(time) {
			case PERIODIC:
				if(unit.equals(MeasUnitPart.MODULE)) return this.periodicModuleToleranceParams;
				else return this.periodicPhaseToleranceParams;
			case PRIMARY:
				if (unit.equals(MeasUnitPart.MODULE)) return this.primaryModuleToleranceParams;
				else return this.primaryPhaseToleranceParams;
			default:
				return null;
		}
	}	
	@Override
	public Device getMyOwner() {
		return this.myDevice;
	}
	@Override
	public void onAdding(Device Owner) {
		this.myDevice = Owner;		
		this.primaryModuleToleranceParams.setTableName();
		this.primaryPhaseToleranceParams.setTableName();
		this.periodicModuleToleranceParams.setTableName();
		this.periodicPhaseToleranceParams.setTableName();
		listOfVerificationsTable = "Проведенные поверки для " + this.myDevice.getName() + " " + this.myDevice.getType() + " " + this.myDevice.getSerialNumber() + " " + this.type + " " + this.serialNumber;
	}

//DataBase
	public Element(Device deviceOwner, int index) throws SQLException {		
		this.myDevice = deviceOwner;
		String elementTableName = myDevice.getElementsTableName();
		String sqlQuery = "SELECT ElementType, ElementSerNumber, PoleCount, MeasUnit, ModuleToleranceType, PhaseToleranceType, VerificationsTable, PrimaryModuleParamTable, PeriodicModuleParamTable, PrimaryPhaseParamTable, PeriodicPhaseParamTable, NominalIndex FROM [" + elementTableName + "] WHERE id='"+index+"'";
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
		if (poleCount == 2) {
			this.sParamsCount = 1;
		} else {
			this.sParamsCount = 4;
		}
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
		if (this.measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(this, nominalIndex);
		}
		else {
			this.nominal = new Gamma_Result(this, nominalIndex);
		}		 
		this.primaryModuleToleranceParams = new ToleranceParametrs(TimeType.PRIMARY,  MeasUnitPart.MODULE, this);
		this.periodicModuleToleranceParams = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.MODULE, this);
		this.primaryPhaseToleranceParams = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.PHASE, this);
		this.periodicPhaseToleranceParams = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.PHASE, this); 
		//Set strategies for suitability counting
		percentStrategy percent = new percentStrategy();
		upDownStrategy upDown = new upDownStrategy();
		if (this.moduleToleranceType.equals("percent")) {
			primaryModuleToleranceParams.setStrategy(percent);
			periodicModuleToleranceParams.setStrategy(percent);
		} else {
			primaryModuleToleranceParams.setStrategy(upDown);
			periodicModuleToleranceParams.setStrategy(upDown);
		}
		if (this.phaseToleranceType.equals("percent")) {
			primaryPhaseToleranceParams.setStrategy(percent);
			periodicPhaseToleranceParams.setStrategy(percent);			
		} else {
			primaryPhaseToleranceParams.setStrategy(upDown);
			periodicPhaseToleranceParams.setStrategy(upDown);
		}
	}
	
//GUI	
	public Element(NewElementController elCtrl) {			
		this.type = elCtrl.getType();
		this.serialNumber = elCtrl.getSerNum();
		this.poleCount = elCtrl.getPoleCount();
		if (poleCount == 2) {
			this.sParamsCount = 1;
		} else {
			this.sParamsCount = 4;
		}
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
		this.primaryModuleToleranceParams = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.MODULE, elCtrl, this);
		this.primaryPhaseToleranceParams = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.PHASE, elCtrl, this);		
		this.periodicModuleToleranceParams = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.MODULE, elCtrl, this);
		this.periodicPhaseToleranceParams = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.PHASE, elCtrl, this);		
		percentStrategy percentStr = new percentStrategy();
		upDownStrategy udStr = new upDownStrategy();
		if (this.moduleToleranceType.equals("updown")) {
			primaryModuleToleranceParams.setStrategy(udStr);
			periodicModuleToleranceParams.setStrategy(udStr);
		}
		else {
			primaryModuleToleranceParams.setStrategy(percentStr);
			periodicModuleToleranceParams.setStrategy(percentStr);
		}
		if (this.phaseToleranceType.equals("updown")) {
			primaryPhaseToleranceParams.setStrategy(udStr);
			periodicPhaseToleranceParams.setStrategy(udStr);
		}
		else {
			primaryPhaseToleranceParams.setStrategy(percentStr);
			periodicPhaseToleranceParams.setStrategy(percentStr);
		}
	}
	
//dbStorable		
	@Override
	public void saveInDB() throws SQLException, SavingException {
		if (this.myDevice == null) {
			throw new NoOwnerException(this);
		}
		String sqlQuery = "INSERT INTO ["+ myDevice.getElementsTableName() +"] (ElementType, ElementSerNumber, PoleCount, MeasUnit, ModuleToleranceType, PhaseToleranceType, " +
				"VerificationsTable, PrimaryModuleParamTable, PeriodicModuleParamTable, PrimaryPhaseParamTable, PeriodicPhaseParamTable, NominalIndex) values "
				+ "('"+type+"','"+serialNumber+"','"+poleCount+"','"+measUnit+"','"+this.moduleToleranceType+"','"+this.phaseToleranceType+"','" +listOfVerificationsTable+"','"
				+ this.primaryModuleToleranceParams.getTableName() + "','" + this.periodicModuleToleranceParams.getTableName() + "','"
				+ this.primaryPhaseToleranceParams.getTableName() + "','" + this.periodicPhaseToleranceParams.getTableName() + "','" + Integer.toString(nominalIndex) + "')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "CREATE TABLE [" + listOfVerificationsTable + "] (id INTEGER PRIMARY KEY AUTOINCREMENT, dateOfVerification VARCHAR(60), resultsTableName VARCHAR(512))";
		try {
			DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		} catch (SQLException sqlExp) {
			throw new SavingException("Не удалось создать таблицу\nсо списком проведенных поверок.");
		}
		this.primaryModuleToleranceParams.saveInDB();	
		this.primaryPhaseToleranceParams.saveInDB();
		this.periodicModuleToleranceParams.saveInDB();	
		this.periodicPhaseToleranceParams.saveInDB();
		this.nominal.saveInDB();	
	}
	
	@Override
	public void deleteFromDB() throws SQLException {		
		String addStr = myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber();	
		String strElementsTable = this.myDevice.getElementsTableName();
		String measurementsOfTableName = " " + addStr + " " + this.type + " " + this.serialNumber;		
		this.nominal.deleteFromDB();
		this.primaryModuleToleranceParams.deleteFromDB();
		this.primaryPhaseToleranceParams.deleteFromDB();
		this.periodicModuleToleranceParams.deleteFromDB();
		this.periodicPhaseToleranceParams.deleteFromDB();		
		String sqlString = "DROP TABLE ["+measurementsOfTableName+"]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
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
	
	public void rewriteTableNames() throws SQLException {
		//First of all we must rewrite table names for results of measurements
		String sqlQuery = "SELECT COUNT(*) FROM [" + listOfVerificationsTable + "]";
		int countOfMeas = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		for (int i = 0; i < countOfMeas; i++) {
			MeasResult measRes = new MeasResult(this, i);
			measRes.rewriteTableNames();
		}
		//Now we are rewriting name of the table with the list of verifications
		String newListOfVerificationsTable = "Проведенные поверки для " + myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber() + " " + type + " " + serialNumber;
		sqlQuery = "ALTER TABLE " + listOfVerificationsTable + " RENAME TO " + newListOfVerificationsTable;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		// Field listOfVerificationsTable takes new value only after successful query to DB
		listOfVerificationsTable = newListOfVerificationsTable;
		//And finally we are rewriting tables names of tolerance parameters
		this.primaryModuleToleranceParams.rewriteTableNames();
		this.primaryPhaseToleranceParams.rewriteTableNames();
		this.periodicModuleToleranceParams.rewriteTableNames();
		this.periodicPhaseToleranceParams.rewriteTableNames();	
	}
	
	public void rewriteParams(ToleranceParametrs newModulePrimaryParams, ToleranceParametrs newModulePeriodicParams,
							  ToleranceParametrs newPhasePrimaryParams, ToleranceParametrs newPhasePeriodicparams, MeasResult newNominals) throws SQLException{
		this.primaryModuleToleranceParams.deleteFromDB();
		this.primaryPhaseToleranceParams.deleteFromDB();
		this.periodicModuleToleranceParams.deleteFromDB();
		this.periodicPhaseToleranceParams.deleteFromDB();
		this.nominal.deleteFromDB();
		/*
		newModulePrimaryParams.saveInDB();
		newModulePeriodicParams.saveInDB();
		newPhasePrimaryParams.saveInDB();
		newPhasePeriodicparams.saveInDB();
		newNominals.saveInDB();*/
	}
	
	public ArrayList<ArrayList<String>> getListOfVerifications() throws SQLException {		
		String sqlString = "SELECT id, dateOfVerification, resultsTableName FROM [" + listOfVerificationsTable + "]";		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("id");
		fieldName.add("dateOfVerification");
		fieldName.add("resultsTableName");
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();		
		DataBaseManager.getDB().sqlQueryString(sqlString, fieldName, arrayResults);			
		return arrayResults;
	}
	
	
}