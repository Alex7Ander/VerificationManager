package DevicePack;

public class SavingException extends Exception {
	private String savingStatus;
	public String getDeviceSatausMsg() {return savingStatus;}
	SavingException(String text){savingStatus = text;}
}
