package VerificationPack;

import java.util.ArrayList;

import DevicePack.Device;
import DevicePack.Element;
import ProtocolCreatePack.ProtocolCreateController;
import StartVerificationPack.StartVerificationController;

public class VerificationProcedure {

//Primary information:
	private String typeByTime;
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
	 	
	public String getTypeByTime() {return this.typeByTime;}
	public String getTemperature() {return this.strTemperature;}
	public String getAtmPreasure() {return this.strAtmPreasure;}
	public String getAirHumidity() {return this.strAirHumidity;}
	
	public String getDeviceInfo() {return this.deviceMainInfo;}
	public String getDeviceSerNumber() {return deviceSerNumber;}
	public String getElementInfo(int index) {return elementsMainInfo.get(index);}
	
	public String getWorkerName() {return this.workerName;}
	public String getBossName() {return this.bossName;}
	public String getBossStatus() {return this.bossStatus;}
	public String getDecision() {return this.decision;}
	public String getProtocolNumber() {return this.protocolNumber;}
	public String getDocumentNumber() {return this.documentNumber;}
	
	public void setPrimaryInformation(StartVerificationController verCtrl) {
		typeByTime = verCtrl.getTypeBytime();
		strTemperature = verCtrl.getStrTemperatur();
		strAirHumidity = verCtrl.getStrAirHumidity();
		strAtmPreasure = verCtrl.getStrAtmPreasure();
	}
	
	public void setDeviceInformation(Device device) {
		deviceMainInfo = device.getName() + device.getType();
		deviceSerNumber = device.getSerialNumber();
		elementsMainInfo = new ArrayList<String>();
		for (Element elm : device.includedElements) {
			String item = elm.getType() + elm.getSerialNumber();
			this.elementsMainInfo.add(item);
		}
	}
	
	public void setFinallyInformation(ProtocolCreateController prtCreateCtrl) {
		bossName   = prtCreateCtrl.getBossName();
		bossStatus = prtCreateCtrl.getBossStatus();
		workerName = prtCreateCtrl.getWorkerName();
		decision   = prtCreateCtrl.getResultDecision();	
		protocolNumber = prtCreateCtrl.getProtocolNumber();
		documentNumber = prtCreateCtrl.getDocumentNumber();		
	}
	
}