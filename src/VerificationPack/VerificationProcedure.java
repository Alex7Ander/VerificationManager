package VerificationPack;

import StartVerificationPack.StartVerificationController;

public class VerificationProcedure {

//Primary information:
	private String typeByTime;
	private String strTemperature;
	private String strAtmPreasure;
	private String strAirHumidity;
	
//Finally information:
	private String bossName;
	private String bossStatus;
	private String workerName;
	
	public String getTypeByTime() {return this.typeByTime;}
	public String getTemperature() {return this.strTemperature;}
	public String getAtmPreasure() {return this.strAtmPreasure;}
	public String getAirHumidity() {return this.strAirHumidity;}
	
	public String getWorkerName() {return this.workerName;}
	public String getBossName() {return this.bossName;}
	public String getBossStatus() {return this.bossStatus;}
	
	public void setPrimaryInformation(StartVerificationController verCtrl) {
		this.typeByTime = verCtrl.getTypeBytime();
		this.strTemperature = verCtrl.getStrTemperatur();
		this.strAirHumidity = verCtrl.getStrAirHumidity();
		this.strAtmPreasure = verCtrl.getStrAtmPreasure();
	}
	
	public void getFinallyInformation() {
		
	}
}
