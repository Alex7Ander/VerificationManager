package ProtocolCreatePack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import VerificationPack.MeasResult;

public class ProtocolCreateThread extends Thread {

	private ArrayList<MeasResult> protocoledResult;
	private String protocolName;
	
	public ProtocolCreateThread(String threadName, String protocolFileName, ArrayList<MeasResult> result){
		super(threadName);	
		this.protocoledResult = result;
		this.protocolName = protocolFileName;
	}
	
	public void run() {
		//Скопировать файл 
		String absPath = new File(".").getAbsolutePath();
		String pathFrom = new File(".").getAbsolutePath() + "\\files\\protocolsTemplates\\PRT.xlsx";
		String pathTo = new File(".").getAbsolutePath() + "\\Protocols\\" + this.protocolName;
		File source = new File(pathFrom);
		File dist = new File(pathTo);
		try {
			Files.copy(source.toPath(), dist.toPath());
			//Заполнение файла
			fillFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void fillFile() {
		
		//
		
	}
	
}