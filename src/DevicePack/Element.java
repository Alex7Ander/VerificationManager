package DevicePack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

	private int id;
	private Device myDevice; 		
	private MeasResult nominal;		
	private ToleranceParametrs primaryModuleToleranceParams;
	private ToleranceParametrs periodicModuleToleranceParams;
	private ToleranceParametrs primaryPhaseToleranceParams;
	private ToleranceParametrs periodicPhaseToleranceParams;
	private String type;
	private String serialNumber;
	private int poleCount;
	private int sParamsCount;
	private String measUnit;
	private int nominalId;
	
	private String periodicModuleParamTable;
	private String primaryModuleParamTable;
	private String periodicPhaseParamTable;
	private String primaryPhaseParamTable;	
	private String nominalTable;
	
	private String listOfVerificationsTable;
	private String moduleToleranceType;
	private String phaseToleranceType;
	public int getId() {
		return this.id;
	}
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
	public int getNominalId() {
		return nominalId;
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
				else return periodicPhaseToleranceParams;
			case PRIMARY:
				if (unit.equals(MeasUnitPart.MODULE)) return this.primaryModuleToleranceParams;
				else return primaryPhaseToleranceParams;
			default:
				return null;
		}
	}	
	@Override
	public Device getMyOwner() {
		return myDevice;
	}
	@Override
	public void onAdding(Device Owner) {
		myDevice = Owner;		
		primaryModuleToleranceParams.setTableName();
		primaryPhaseToleranceParams.setTableName();
		periodicModuleToleranceParams.setTableName();
		periodicPhaseToleranceParams.setTableName();
		listOfVerificationsTable = "Проведенные поверки для " + myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber() + " " + type + " " + serialNumber;
	}

//DataBase
	public Element(Device deviceOwner, int index) throws SQLException {	
		System.out.println("\nБерем информацию на элемент с id = " + index);
		this.id = index;
		this.myDevice = deviceOwner;
		String elementTableName = myDevice.getElementsTableName();
		String sqlQuery = "SELECT ElementType, ElementSerNumber, PoleCount, MeasUnit, ModuleToleranceType, PhaseToleranceType, NominalId FROM [Elements] WHERE id='"+index+"'";
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("ElementType");				
		fieldName.add("ElementSerNumber");			
		fieldName.add("PoleCount");					
		fieldName.add("MeasUnit");					
		fieldName.add("ModuleToleranceType");		
		fieldName.add("PhaseToleranceType");		
		fieldName.add("NominalId");
		List<List<String>> arrayResults = new ArrayList<List<String>>();
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		
		this.type = arrayResults.get(0).get(0);
		this.serialNumber = arrayResults.get(0).get(1);
		this.poleCount = Integer.parseInt(arrayResults.get(0).get(2));
		if (this.poleCount == 2) {
			this.sParamsCount = 1;
		} 
		else {
			this.sParamsCount = 4;
		}
		this.measUnit = arrayResults.get(0).get(3);
		this.moduleToleranceType = arrayResults.get(0).get(4);
		this.phaseToleranceType = arrayResults.get(0).get(5);		
		this.nominalId = Integer.parseInt(arrayResults.get(0).get(6));			
		
		//Let's take nominals from data base
		System.out.println("Полуим номинальные значения его характеристик для данного элемента\n(они хранятся как результаты измерений)");
		if (measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(this, nominalId);
		}
		else {
			this.nominal = new Gamma_Result(this, nominalId);
		}	
			
		//And now it's time for tolerance parametrs
		this.primaryModuleToleranceParams = new ToleranceParametrs(TimeType.PRIMARY,  MeasUnitPart.MODULE, this);
		this.periodicModuleToleranceParams = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.MODULE, this);
		this.primaryPhaseToleranceParams = new ToleranceParametrs(TimeType.PRIMARY, MeasUnitPart.PHASE, this);
		this.periodicPhaseToleranceParams = new ToleranceParametrs(TimeType.PERIODIC, MeasUnitPart.PHASE, this); 
		//Set strategies for suitability counting
		percentStrategy percent = new percentStrategy();
		upDownStrategy upDown = new upDownStrategy();
		if (this.moduleToleranceType.equals("percent")) {
			this.primaryModuleToleranceParams.setStrategy(percent);
			this.periodicModuleToleranceParams.setStrategy(percent);
		} else {
			this.primaryModuleToleranceParams.setStrategy(upDown);
			this.periodicModuleToleranceParams.setStrategy(upDown);
		}
		if (this.phaseToleranceType.equals("percent")) {
			this.primaryPhaseToleranceParams.setStrategy(percent);
			this.periodicPhaseToleranceParams.setStrategy(percent);			
		} else {
			this.primaryPhaseToleranceParams.setStrategy(upDown);
			this.periodicPhaseToleranceParams.setStrategy(upDown);
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
		this.nominalId = 1;
		
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
			this.primaryModuleToleranceParams.setStrategy(udStr);
			this.periodicModuleToleranceParams.setStrategy(udStr);
		}
		else {
			this.primaryModuleToleranceParams.setStrategy(percentStr);
			this.periodicModuleToleranceParams.setStrategy(percentStr);
		}
		if (this.phaseToleranceType.equals("updown")) {
			this.primaryPhaseToleranceParams.setStrategy(udStr);
			this.periodicPhaseToleranceParams.setStrategy(udStr);
		}
		else {
			this.primaryPhaseToleranceParams.setStrategy(percentStr);
			this.periodicPhaseToleranceParams.setStrategy(percentStr);
		}
	}
	
//dbStorable		
	@Override
	public void saveInDB() throws SQLException, SavingException {
		if (this.myDevice == null) {
			throw new NoOwnerException(this);
		}			
		String sqlQuery = "INSERT INTO [Elements] (DeviceId, "
				+ "ElementType, " 				
				+ "ElementSerNumber, "  	
				+ "PoleCount, "					
				+ "MeasUnit, "					
				+ "ModuleToleranceType, "		
				+ "PhaseToleranceType) VALUES ("		
					+ this.myDevice.getId() + ",'"
					+ type + "','" 												
					+ serialNumber + "','" 										
					+ poleCount + "','" 										
					+ measUnit + "','" 											
					+ moduleToleranceType + "','" 								
					+ phaseToleranceType + "')";	
		System.out.println("Вносим запись в таблицу elements о сохраняемом элементе\n" + sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//Getting id
		sqlQuery = "SELECT id FROM [Elements] WHERE ElementType='" + type + "' AND  ElementSerNumber='" + serialNumber + "' AND DeviceId='" + this.myDevice.getId() + "'";
		System.out.println("Получаем id сохраненного элемента запросом:\n" + sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		System.out.println("id = " + this.id);
		/*
		String identStr = id + "_" + (new Date().toString());
		sqlQuery = "INSERT INTO [Tolerance_params] (ElementId) VALUES (" + this.id + ")";
		System.out.println("Вносим информацию о таблицах, в которых будут содаржаться критерии пригодности запросом:\n" + sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		*/
		//Nominal saving
		System.out.println("\nНачинаем сохранение номиналов");
		this.nominal.saveInDB();
		System.out.println("\nУстанавливаем статус номинала");
		this.nominal.setNominalStatus();
		
		//Tolerance parametrs saving
		System.out.println("\nНачинаем сохранение критериев пригодности");
		this.primaryModuleToleranceParams.saveInDB();		
		this.primaryPhaseToleranceParams.saveInDB();
		this.periodicModuleToleranceParams.saveInDB();	
		this.periodicPhaseToleranceParams.saveInDB();			
	}
	
	@Override
	public void deleteFromDB() throws SQLException {
		String sqlString = "DELETE FROM [Elements] WHERE id=" + this.id + "";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		
		/*
		this.nominal.deleteFromDB();
		this.primaryModuleToleranceParams.deleteFromDB();
		this.primaryPhaseToleranceParams.deleteFromDB();
		this.periodicModuleToleranceParams.deleteFromDB();
		this.periodicPhaseToleranceParams.deleteFromDB();		
		String sqlString = "DROP TABLE [" + listOfVerificationsTable + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		sqlString = "DELETE FROM [" + myDevice.getElementsTableName() + "] WHERE ElementType='" + type + "' AND ElementSerNumber='" + serialNumber + "'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);
		int val = myDevice.getCountOfElements();
		--val;
		sqlString = "UPDATE [Devices] SET CountOfElements = '" + Integer.toString(val) + "' WHERE NameOfDevice='" + myDevice.getName()
		+"' AND TypeOfDevice='" + myDevice.getType() + "' AND SerialNumber='" + myDevice.getSerialNumber() + "'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlString);	
		*/	
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
		List<String> measIndices = new ArrayList<String>();
		String sqlQuery = "SELECT id FROM [" + listOfVerificationsTable + "]";
		DataBaseManager.getDB().sqlQueryString(sqlQuery, "id", measIndices);		
		for (int i = 0; i < measIndices.size(); i++) {
			MeasResult measRes = new MeasResult(this, Integer.parseInt(measIndices.get(i)));
			measRes.rewriteTableNames();
		}
		//Now we are rewriting name of the table with the list of verifications
		String newListOfVerificationsTable = "Проведенные поверки для " + myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber() + " " + type + " " + serialNumber;
		sqlQuery = "ALTER TABLE [" + listOfVerificationsTable + "] RENAME TO [" + newListOfVerificationsTable + "]";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		sqlQuery = "UPDATE [" + myDevice.getElementsTableName() + "] SET VerificationsTable='" + newListOfVerificationsTable + "' ";
		sqlQuery += "WHERE ElementType = '" + type + "' AND ElementSerNumber='" + serialNumber + "'";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		// Field listOfVerificationsTable takes new value only after successful query to DB
		listOfVerificationsTable = newListOfVerificationsTable;
		//And finally we are rewriting table names of tolerance parameters
		primaryModuleToleranceParams.rewriteTableNames();
		primaryPhaseToleranceParams.rewriteTableNames();
		periodicModuleToleranceParams.rewriteTableNames();
		periodicPhaseToleranceParams.rewriteTableNames();	
	}
	
	public void rewriteParams(ToleranceParametrs newModulePrimaryParams, ToleranceParametrs newModulePeriodicParams,
							  ToleranceParametrs newPhasePrimaryParams,  ToleranceParametrs newPhasePeriodicparams) throws SQLException, SavingException {
		primaryModuleToleranceParams.deleteFromDB();
		primaryPhaseToleranceParams.deleteFromDB();
		periodicModuleToleranceParams.deleteFromDB();
		periodicPhaseToleranceParams.deleteFromDB();
		
		newModulePrimaryParams.onAdding(this);
		newModulePeriodicParams.onAdding(this);
		newPhasePrimaryParams.onAdding(this);
		newPhasePeriodicparams.onAdding(this);
		
		newModulePrimaryParams.saveInDB();
		newModulePeriodicParams.saveInDB();
		newPhasePrimaryParams.saveInDB();
		newPhasePeriodicparams.saveInDB();
	}
	
	public List<List<String>> getListOfVerifications() throws SQLException {		
		String sqlString = "SELECT id, MeasDate FROM [Results] WHERE ElementId=" + this.id + "";		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("id");
		fieldName.add("MeasDate");
		List<List<String>> arrayResults = new ArrayList<List<String>>();		
		DataBaseManager.getDB().sqlQueryString(sqlString, fieldName, arrayResults);			
		return arrayResults;
	}

	
}