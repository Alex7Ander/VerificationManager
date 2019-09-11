package ProtocolCreatePack;

import VerificationPack.VerificationProcedure;

public class DocumentCreateThread implements Runnable{

	private Thread myThread;
	
	VerificationProcedure verification;
	private String pathFrom;
	private String pathTo;
	
	public DocumentCreateThread(VerificationProcedure verificationProc) {
		myThread = new Thread(this, "DocumentCreateThread");
		verification = verificationProc;
	}
	
	public void start() {
		this.myThread.start();
	}
	
	public void join() throws InterruptedException {
		this.myThread.join();
	}
	
	@Override
	public void run() {
				
	}
	
}
