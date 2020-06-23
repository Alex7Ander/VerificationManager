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
	private int deviceId;
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
	private String verificatonMethodologyName;

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
	public String getDateOfCreation() {
		return dateOfCreation;
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
		this.workerName = prtCreateCtrl.getWorkerName();
		this.decision   = prtCreateCtrl.getResultDecision();	
		this.protocolNumber = prtCreateCtrl.getProtocolNumber();
		this.documentNumber = prtCreateCtrl.getDocumentNumber();	
		this.etalonString = prtCreateCtrl.getEtalonString();
		this.docType = prtCreateCtrl.getDocType();
		this.dateOfCreation = prtCreateCtrl.getDateOfCreation();
		this.finishDate = "Годен до " + prtCreateCtrl.getFinishDate();
		this.militaryBaseName = prtCreateCtrl.getMilitryBaseName();
		this.verificatonMethodologyName = prtCreateCtrl.getVerificatonMethodologyName();
	}	
}