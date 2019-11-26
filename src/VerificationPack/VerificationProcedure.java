package VerificationPack;

import java.util.ArrayList;
import DevicePack.Device;
import DevicePack.Element;
import ProtocolCreatePack.ProtocolCreateController;
import StartVerificationPack.StartVerificationController;
import ToleranceParamPack.ParametrsPack.TimeType;

public class VerificationProcedure {

//Primary information:
	private TimeType verificationTimeType;
	private String strTemperature;
	private String strAtmPreasure;
	private String strAirHumidity;

//Device Information
	private String deviceMainInfo;
	private String deviceSerNumber;
	private ArrayList<String> elementsMainInfo;
	
//Finally information:
	private String bossName;
	private String bossStatus;
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
	private String dateOfCreation;
	private String finishDate;

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
	public String getDateOfCreation() {
		return dateOfCreation;
	}
	public String getFinishDate() {
		return finishDate;
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
		verificationTimeType = verCtrl.getVerificationiTimeType();
		strTemperature = verCtrl.getStrTemperatur();
		strAirHumidity = verCtrl.getStrAirHumidity();
		strAtmPreasure = verCtrl.getStrAtmPreasure();
	}
	
	public void setDeviceInformation(Device device) {
		deviceMainInfo = device.getName() + " " + device.getType();
		deviceSerNumber = device.getSerialNumber();
		elementsMainInfo = new ArrayList<String>();
		for (Element elm : device.includedElements) {
			String item = elm.getType() + elm.getSerialNumber();
			elementsMainInfo.add(item);
		}
		deviceOwner = device.getOwner();
	}
	
	public void setFinallyInformation(ProtocolCreateController prtCreateCtrl) {
		bossName   = prtCreateCtrl.getBossName();
		bossStatus = prtCreateCtrl.getBossStatus();
		workerName = prtCreateCtrl.getWorkerName();
		decision   = prtCreateCtrl.getResultDecision();	
		protocolNumber = prtCreateCtrl.getProtocolNumber();
		documentNumber = prtCreateCtrl.getDocumentNumber();	
		etalonString = prtCreateCtrl.getEtalonString();
		docType = prtCreateCtrl.getDocType();
		dateOfCreation = prtCreateCtrl.getDateOfCreation();
		finishDate = "Годен до " + prtCreateCtrl.getFinishDate();
		militaryBaseName = prtCreateCtrl.getMilitryBaseName();
	}
	
}