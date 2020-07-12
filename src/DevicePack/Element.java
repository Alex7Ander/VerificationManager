package DevicePack;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private MeasResult lastVerificationResult;
	private ToleranceParametrs primaryModuleToleranceParams;
	private ToleranceParametrs periodicModuleToleranceParams;
	private ToleranceParametrs primaryPhaseToleranceParams;
	private ToleranceParametrs periodicPhaseToleranceParams;
	private String type;
	private String serNumber;
	private String measUnit;
	private String moduleToleranceType;
	private String phaseToleranceType;
		
	private int poleCount;
	private int sParamsCount;
	private int nominalId;
	private int lastVerificationId;
	
	/*
	private String periodicModuleParamTable;
	private String primaryModuleParamTable;
	private String periodicPhaseParamTable;
	private String primaryPhaseParamTable;	
	private String nominalTable;	
	private String listOfVerificationsTable;

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
	*/
	public int getId() {
		return this.id;
	}
	public String getModuleToleranceType() {
		return moduleToleranceType;
	}
	public String getPhaseToleranceType() {
		return phaseToleranceType;
	}	

	public String getType() {
		return type;
	}
	public String getSerialNumber() {
		return serNumber;
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
		//listOfVerificationsTable = "Проведенные поверки для " + myDevice.getName() + " " + myDevice.getType() + " " + myDevice.getSerialNumber() + " " + type + " " + serNumber;
	}

//DataBase
	public Element(Device deviceOwner, int index) throws SQLException {	
		System.out.println("\nБерем информацию на элемент с id = " + index);
		this.id = index;
		this.myDevice = deviceOwner;
		String sqlQuery = "SELECT type, serNumber, poleCount, measUnit, moduleToleranceType, phaseToleranceType, nominalId, lastVerificationId FROM [Elements] WHERE id='"+index+"'";
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("type");				
		fieldName.add("serNumber");			
		fieldName.add("poleCount");					
		fieldName.add("measUnit");					
		fieldName.add("moduleToleranceType");		
		fieldName.add("phaseToleranceType");		
		fieldName.add("nominalId");
		fieldName.add("lastVerificationId");
		List<List<String>> arrayResults = new ArrayList<List<String>>();
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		
		this.type = arrayResults.get(0).get(0);
		this.serNumber = arrayResults.get(0).get(1);
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
		this.lastVerificationId = Integer.parseInt(arrayResults.get(0).get(7));
		
		//Получим номинальные значения его характеристик для данного элемента и значения предыдущей поверки
		System.out.println("Получим номинальные значения его характеристик для данного элемента (они хранятся как результаты измерений с id = "+this.nominalId+")"
				+ "\nи значения предыдущей поверки (id="+this.lastVerificationId+")");
		if (measUnit.equals("vswr")) {
			this.nominal = new VSWR_Result(this, nominalId);
			if(this.lastVerificationId != 0)
				lastVerificationResult = new VSWR_Result(this, lastVerificationId); 
		}
		else {
			this.nominal = new Gamma_Result(this, nominalId);
			if(this.lastVerificationId != 0)
				lastVerificationResult = new Gamma_Result(this, lastVerificationId);
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
		this.serNumber = elCtrl.getSerNum();
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
				+ "type, " 				
				+ "serNumber, "  	
				+ "poleCount, "					
				+ "measUnit, "					
				+ "moduleToleranceType, "		
				+ "phaseToleranceType) VALUES ("		
					+ this.myDevice.getId() + ",'"
					+ type + "','" 												
					+ serNumber + "','" 										
					+ poleCount + "','" 										
					+ measUnit + "','" 											
					+ moduleToleranceType + "','" 								
					+ phaseToleranceType + "')";	
		System.out.println("Вносим запись в таблицу elements о сохраняемом элементе\n" + sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		//Getting id
		sqlQuery = "SELECT id FROM [Elements] WHERE type='" + type + "' AND  serNumber='" + serNumber + "' AND deviceId='" + this.myDevice.getId() + "'";
		System.out.println("Получаем id сохраненного элемента запросом:\n" + sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		System.out.println("id = " + this.id);

		//Nominal saving
		System.out.println("\nНачинаем сохранение номиналов");
		this.nominal.saveInDB();
		System.out.println("\nУстанавливаем статус номинала");
		this.nominal.setNominalStatus();
		this.setLastVerificationId(0);
				
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
		this.myDevice.removeElement(this);
	}
	
	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		String sqlQuery = "UPDATE Elements SET ";
		Class<? extends Element> elementClass = this.getClass();		
		Iterator<String> it = editingValues.keySet().iterator();
		boolean isEdited = false;
		do {
			String fieldName = it.next();
			try {
				Field anyField = elementClass.getDeclaredField(fieldName);
				String oldAnyFieldValue = (String) anyField.get(this);
				String newAnyFieldValue = editingValues.get(fieldName);
				if(!oldAnyFieldValue.equals(newAnyFieldValue)) {
					isEdited = true;
					sqlQuery += (fieldName + "='"+newAnyFieldValue+"', ");
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exp) {
				exp.printStackTrace();
			}
		} while(it.hasNext());
		
		if(!isEdited) { 
			return;
		}
		sqlQuery = sqlQuery.substring(0, sqlQuery.length()-2);
		sqlQuery += " WHERE id=" + this.id;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
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
	
	public List<List<String>> getMeasurementList() throws SQLException {		
		String sqlString = "SELECT id, MeasDate FROM [Results] WHERE ElementId=" + this.id + "";		
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("id");
		fieldName.add("MeasDate");
		List<List<String>> arrayResults = new ArrayList<List<String>>();		
		DataBaseManager.getDB().sqlQueryString(sqlString, fieldName, arrayResults);			
		return arrayResults;
	}
	
	public void changePoleCount(int newPoleCount) throws SQLException {
		String sqlQuery = "UPDATE Elements SET poleCount = '" + newPoleCount + "' WHERE id=" + this.id;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		this.poleCount = newPoleCount;
	}
	public int getLastVerificationId() {
		return lastVerificationId;
	}
	public void setLastVerificationId(int lastVerificationId) throws SQLException {
		String sqlQuery = "UPDATE [Elements] SET lastVerificationId='" + Integer.toString(lastVerificationId) + "' WHERE id=" + this.id;
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery); 
		this.lastVerificationId = lastVerificationId;
	}
	public MeasResult getLastVerificationResult() {
		return lastVerificationResult;
	}
	
}