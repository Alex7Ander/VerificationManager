package VerificationPack;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import DataBasePack.DataBaseManager;
import DataBasePack.dbStorable;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.SavingException;
import ProtocolCreatePack.ProtocolCreateController;
import StartVerificationPack.StartVerificationController;
import ToleranceParamPack.ParametrsPack.TimeType;

public class VerificationProcedure implements dbStorable {

	private int id;
	
//Primary information:
	private TimeType verificationTimeType;
	private String strTemperature;
	private String strAtmPreasure;
	private String strAirHumidity;

//Device Information
	private int deviceId;
	private String deviceMainInfo;
	private String deviceSerNumber;	
	private ArrayList<String> elementsMainInfo;
	
//Finally information:
	private String bossName;
	private String bossStatus;
	private String standartGuardianName;
	private String standartGuardianStatus;
	private String workerName;
	private String decision;
	private String protocolNumber;
	private String documentNumber;

	private String docName;
	private String docType;
	private String verType;
	private String militaryBaseName;
	private String etalonString;
	private String deviceOwner;
	
	private String date;
	
	private String dateForDocuments;
	private String finishDate;
	private String verificatonMethodologyName;

	private String pathOfDoc;
	private String pathOfProtocol;
	
	private SecretKey secretKey;
	
	private boolean shouldBeSavedInDB;
	
	public boolean isShouldBeSavedInDB() {
		return this.shouldBeSavedInDB;
	}
	
	public void setShouldBeSavedInDBStatus(boolean status) {
		this.shouldBeSavedInDB = status;
	}
	
	public VerificationProcedure() {
		try {
			secretKey = KeyGenerator.getInstance("AES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		shouldBeSavedInDB = true;
	}
	
	public VerificationProcedure(int id) throws SQLException {
		System.out.println("\nФормирование объекта класса VerificationProcedure из БД");
		this.id = id;
		String sqlQuery = "SELECT deviceId, "
				+ "verificationDate, "
				+ "pathOfDoc, "
				+ "pathOfProtocol, "
				+ "typeOfDoc, "
				+ "verificationTimeType, "
				+ "temperature, "
				+ "preasure, "
				+ "humidity, "
				+ "workerName, "
				+ "bossStatus, "
				+ "bossName, "
				+ "militaryBaseName, "
				+ "verificationMetodologyName, "
				+ "rejectionReason, "
				+ "tillTime, "
				+ "isStandard, "
				+ "secretKeyString FROM [Verifications] WHERE id='"+id+"'";
		ArrayList<String> fieldName = new ArrayList<String>();
		fieldName.add("deviceId");				
		fieldName.add("verificationDate");			
		fieldName.add("pathOfDoc");					
		fieldName.add("pathOfProtocol");					
		fieldName.add("typeOfDoc");		
		fieldName.add("verificationTimeType");		
		fieldName.add("temperature");
		fieldName.add("preasure");
		fieldName.add("humidity");		
		fieldName.add("workerName");
		fieldName.add("bossStatus");
		fieldName.add("bossName");
		fieldName.add("militaryBaseName");
		fieldName.add("verificationMetodologyName");
		fieldName.add("rejectionReason");		
		fieldName.add("tillTime");
		fieldName.add("isStandard");
		fieldName.add("secretKeyString");
		List<List<String>> arrayResults = new ArrayList<List<String>>();
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, arrayResults);
		
		try {
			this.deviceId = Integer.parseInt(arrayResults.get(0).get(0));
		}
		catch(NumberFormatException nfExp) {
			System.err.println("Ошибка преобразования deviceId в число из строки (Конструктор класса VerificationProcedure)");
			this.deviceId = 0;
		}
		
		this.date = arrayResults.get(0).get(1);
		this.pathOfDoc = arrayResults.get(0).get(2);
		this.pathOfProtocol = arrayResults.get(0).get(3);
		this.docType = arrayResults.get(0).get(4);
		if (arrayResults.get(0).get(5).equals("PRIMARY")) {
			this.verificationTimeType = TimeType.PRIMARY;
		}
		else {
			this.verificationTimeType = TimeType.PERIODIC;
		}
		this.strTemperature = arrayResults.get(0).get(6);
		this.strAtmPreasure = arrayResults.get(0).get(7);
		this.strAirHumidity = arrayResults.get(0).get(8);
		this.workerName = arrayResults.get(0).get(9);
		this.bossStatus = arrayResults.get(0).get(10);
		this.bossName = arrayResults.get(0).get(11);
		this.militaryBaseName = arrayResults.get(0).get(12);
		this.verificatonMethodologyName = arrayResults.get(0).get(13);
		
		if (this.verificatonMethodologyName.equals("null")) 
			this.verificatonMethodologyName = "Методика поверки рабочих эталонов и средств измерений военного назначения на военном эталоне.";
		
		this.decision = arrayResults.get(0).get(14);		
		this.finishDate = arrayResults.get(0).get(15);
		this.etalonString = arrayResults.get(0).get(16);
		
		String secretKeyString = arrayResults.get(0).get(17);
		if(secretKeyString != null) {
			byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
			this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		}
		
		elementsMainInfo = new ArrayList<String>();
		if(this.deviceId != 0) {
			Device device = new Device(this.deviceId);
			this.deviceMainInfo = device.getName() + " " + device.getType();
			this.deviceSerNumber = device.getSerialNumber();
			for (Element elm : device.includedElements) {
				String item = elm.getType() + elm.getSerialNumber();
				this.elementsMainInfo.add(item);
			}
		}		
		System.out.println("Формирование объекта класса VerificationProcedure из БД завершено успешно");
	}
	
	public int getId() {
		return id;
	}	
	
	public String getTemperature() {
		return strTemperature;
	}
	public String getAtmPreasure() {
		return strAtmPreasure;
	}
	public String getAirHumidity() {
		return strAirHumidity;
	}
	
	public String getDeviceInfo() {
		return deviceMainInfo;
	}
	public String getDeviceSerNumber() {
		return deviceSerNumber;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public String getElementInfo(int index) {
		return elementsMainInfo.get(index);
	}
	
	public String getWorkerName() {
		return workerName;
	}
	public String getBossName() {
		return bossName;
	}
	public String getBossStatus() {
		return bossStatus;
	}
	public String getDecision() {
		return decision;
	}
	public String getProtocolNumber() {
		return protocolNumber;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	
	public String getDocName() {
		return docName;
	}
	public String getDocType() {
		return docType;
	}
	public String getVerType() {
		return verType;
	}
	public String getMilitaryBasename() {
		return militaryBaseName;
	}
	public String getEtalonString() {
		return etalonString;
	}
	public String getDeviceOwner() {
		return deviceOwner;
	}
	public String getDateForDocuments() {
		return dateForDocuments;
	}
	public String getFinishDate() {
		return finishDate;
	}
	public String getVerificatonMethodologyName() {
		return verificatonMethodologyName;
	}

	public boolean isPrimary(){
		if (verificationTimeType.equals(TimeType.PRIMARY)){
			return true;
		}
		else {
			return false;
		}
	}

	public void setPrimaryInformation(StartVerificationController verCtrl) {
		this.verificationTimeType = verCtrl.getVerificationiTimeType();
		this.strTemperature = verCtrl.getStrTemperatur();
		this.strAirHumidity = verCtrl.getStrAirHumidity();
		this.strAtmPreasure = verCtrl.getStrAtmPreasure();
	}
	
	public void setDeviceInformation(Device device) {
		this.deviceId = device.getId();
		this.deviceMainInfo = device.getName() + " " + device.getType();
		this.deviceSerNumber = device.getSerialNumber();
		this.elementsMainInfo = new ArrayList<String>();
		for (Element elm : device.includedElements) {
			String item = elm.getType() + elm.getSerialNumber();
			this.elementsMainInfo.add(item);
		}
		this.deviceOwner = device.getOwner();
	}
	
	public void setFinallyInformation(ProtocolCreateController prtCreateCtrl) {
		this.bossName   = prtCreateCtrl.getBossName();
		this.bossStatus = prtCreateCtrl.getBossStatus();
		this.standartGuardianName = prtCreateCtrl.getStandartGuardianName();
		this.standartGuardianStatus = prtCreateCtrl.getStandartGuardianStatus();
		this.workerName = prtCreateCtrl.getWorkerName();
		this.decision   = prtCreateCtrl.getResultDecision();	
		this.protocolNumber = prtCreateCtrl.getProtocolNumber();
		this.documentNumber = prtCreateCtrl.getDocumentNumber();	
		this.etalonString = prtCreateCtrl.getEtalonString();
		this.docType = prtCreateCtrl.getDocType();
		
		this.date = prtCreateCtrl.getDate();		
		this.dateForDocuments = this.date.substring(0, date.indexOf(" ")).replace('/', '.');
		
		this.finishDate = prtCreateCtrl.getFinishDate();
		this.militaryBaseName = prtCreateCtrl.getMilitryBaseName();
				
		this.pathOfDoc = prtCreateCtrl.getPathOfDoc();
		this.pathOfProtocol = prtCreateCtrl.getPathOfProtocol();
	}

	public String getPathOfProtocol() {
		return pathOfProtocol;
	}

	public String getPathOfDoc() {
		return pathOfDoc;
	}

	public void setPrimary() {
		this.verificationTimeType = TimeType.PRIMARY;
	}
	
	public void setPeriodic() {
		this.verificationTimeType = TimeType.PERIODIC;
	}
	
	@Override
	public void saveInDB() throws SQLException, SavingException {
		String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		
		String sqlQuery = "INSERT INTO Verifications (DeviceId, "
				+ "VerificationDate, "
				+ "PathOfDoc, "
				+ "PathOfProtocol, "
				+ "TypeOfDoc, "
				+ "verificationTimeType, "
				+ "temperature, "
				+ "preasure, "
				+ "humidity, "
				+ "workerName, "
				+ "bossStatus, "
				+ "bossName, "
				+ "militaryBaseName, "
				+ "verificationMetodologyName, "
				+ "rejectionReason, "
				+ "tillTime, "
				+ "isStandard, "
				+ "secretKeyString) VALUES "
					+"('"+this.deviceId+"',"
					+ "'"+this.date+"',"
					+ "'"+this.pathOfDoc+"',"
					+ "'"+this.pathOfProtocol+"',"
					+ "'"+this.docType+"',"
					+ "'"+this.verificationTimeType+"',"
					+ "'"+this.strTemperature+"',"
					+ "'"+this.strAtmPreasure+"',"
					+ "'"+this.strAirHumidity+"',"
					+ "'"+this.workerName+"',"
					+ "'"+this.bossStatus+"',"
					+ "'"+this.bossName+"',"
					+ "'"+this.militaryBaseName+"',"
					+ "'"+this.verificatonMethodologyName+"',"
					+ "'"+this.decision+"',"
					+ "'"+this.finishDate+"',"
					+ "'"+this.etalonString+"',"
					+ "'"+secretKeyString+"')";		
		System.out.println(sqlQuery);
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
		
		sqlQuery = "SELECT id FROM [Verifications] WHERE verificationDate='" + this.date + "' AND deviceId=" + this.deviceId;
		System.out.println(sqlQuery);
		this.id = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
		if (this.id == 0) {
			throw new SavingException("Не присвоен id для данной процедуры поверки");
		}
		else {
			System.out.println("Информация о поверке сохранена. Присвоен id = " + this.id);
		}
	}
	
	public List<MeasResult> getMeasResults() {
		List<MeasResult> results = new ArrayList<>();
		if(deviceId != 0) {
			try {
				Device device = new Device(deviceId);				
				List<Integer> resultsId = new ArrayList<>();
				String sqlQuery = "SELECT id FROM [Results] WHERE verificationId=" + this.id;
				DataBaseManager.getDB().sqlQueryInteger(sqlQuery, "id", resultsId);
				
				for(int i = 0; i < resultsId.size();  i++) {
					int currentResultId = resultsId.get(i); 
					sqlQuery = "SELECT elementId FROM [Results] WHERE id=" + currentResultId;
					int currentElementId = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
					for (Element elm : device.includedElements) {
						if (elm.getId() == currentElementId) {
							results.add(new MeasResult(elm, currentResultId));
						}
					}
				}				 				
			} catch (SQLException sqlExp) {
				sqlExp.printStackTrace();
			}
		}
		return results;
	}
	
	public MeasResult getMeasResultForElement(Element elm) {
		MeasResult result = null;		
		try {
			String sqlQuery = "SELECT id FROM [Results] WHERE verificationId=" + this.id + " AND elementId=" + elm.getId();
			int currentResultId = DataBaseManager.getDB().sqlQueryCount(sqlQuery);
			result = new MeasResult(elm, currentResultId);
		} catch (SQLException sqlExp) {
			System.err.println("Не найден результат поверки для устройства с id = " + elm.getId() + " соответствовавший бы поверке с id = " + this.id + ". " + sqlExp.getMessage());
		}
		return result;
	}

	@Override
	public void deleteFromDB() throws SQLException {
		// TODO Auto-generated method stub		
	}

	@Override
	public void editInfoInDB(HashMap<String, String> editingValues) throws SQLException {
		// TODO Auto-generated method stub		
	}

	public String getDate() {
		return date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStrTemperature(String strTemperature) {
		this.strTemperature = strTemperature;
	}

	public void setStrAtmPreasure(String strAtmPreasure) {
		this.strAtmPreasure = strAtmPreasure;
	}

	public void setStrAirHumidity(String strAirHumidity) {
		this.strAirHumidity = strAirHumidity;
	}

	public void setDeviceMainInfo(String deviceMainInfo) {
		this.deviceMainInfo = deviceMainInfo;
	}

	public void setDeviceSerNumber(String deviceSerNumber) {
		this.deviceSerNumber = deviceSerNumber;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public void setElementsMainInfo(ArrayList<String> elementsMainInfo) {
		this.elementsMainInfo = elementsMainInfo;
	}

	public void setBossName(String bossName) {
		this.bossName = bossName;
	}

	public void setBossStatus(String bossStatus) {
		this.bossStatus = bossStatus;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

	public void setProtocolNumber(String protocolNumber) {
		this.protocolNumber = protocolNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public void setVerType(String verType) {
		this.verType = verType;
	}

	public void setMilitaryBaseName(String militaryBaseName) {
		this.militaryBaseName = militaryBaseName;
	}

	public void setEtalonString(String etalonString) {
		this.etalonString = etalonString;
	}

	public void setDeviceOwner(String deviceOwner) {
		this.deviceOwner = deviceOwner;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDateForDocuments(String dateForDocuments) {
		this.dateForDocuments = dateForDocuments;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public void setVerificatonMethodologyName(String verificatonMethodologyName) {
		this.verificatonMethodologyName = verificatonMethodologyName;
	}

	public void setPathOfDoc(String pathOfDoc) {
		this.pathOfDoc = pathOfDoc;
	}

	public void setPathOfProtocol(String pathOfProtocol) {
		this.pathOfProtocol = pathOfProtocol;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	
	public static List<VerificationProcedure> getAllVerificationsProcedures() throws SQLException{
		List<VerificationProcedure> procedures = new ArrayList<>();
		List<Integer> arrayResults = new ArrayList<>();
		String sqlQuery = "SELECT id FROM [Verifications]";
		DataBaseManager.getDB().sqlQueryInteger(sqlQuery, "id", arrayResults);
		
		for (Integer id : arrayResults) {
			procedures.add(new VerificationProcedure(id));
		}
		return procedures;	
	}
	
	public static List<VerificationProcedure> getAllVerificationsProcedures(Device device) throws SQLException{
		List<VerificationProcedure> procedures = new ArrayList<>();
		List<Integer> arrayResults = new ArrayList<>();
		String sqlQuery = "SELECT id FROM [Verifications] WHERE deviceId=" + device.getId();
		DataBaseManager.getDB().sqlQueryInteger(sqlQuery, "id", arrayResults);
		
		for (Integer id : arrayResults) {
			procedures.add(new VerificationProcedure(id));
		}
		return procedures;	
	}

	public String getStandartGuardianName() {
		return standartGuardianName;
	}

	public void setStandartGuardianName(String standartGuardianName) {
		this.standartGuardianName = standartGuardianName;
	}

	public String getStandartGuardianStatus() {
		return standartGuardianStatus;
	}

	public void setStandartGuardianStatus(String standartGuardianStatus) {
		this.standartGuardianStatus = standartGuardianStatus;
	}
		
}