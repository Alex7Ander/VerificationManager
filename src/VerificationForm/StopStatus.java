package VerificationForm;

public class StopStatus {

	private boolean status;
	
	public StopStatus()  {
		status = false;
	}
	
	public synchronized void waitMonitor() throws InterruptedException {
		wait();
	}
	
	public synchronized void setStopStatus() {
		status = true;
		notify();
	}
	
	public synchronized boolean getStopStatus() throws InterruptedException {
		return status;
	}
	
}