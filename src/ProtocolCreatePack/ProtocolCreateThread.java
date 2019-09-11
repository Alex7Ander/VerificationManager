package ProtocolCreatePack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;

public class ProtocolCreateThread implements Runnable {

	private Thread myThr;
	
	private VerificationProcedure verification;
	private ArrayList<MeasResult> protocoledResult;
	private String protocolName;
	private String pathFrom;
	private String pathTo;
	
	public ProtocolCreateThread(String threadName, String protocolFileName, ArrayList<MeasResult> result, VerificationProcedure verificationProc){
		myThr = new Thread(this, "ProtocolCreateThread");
		this.verification = verificationProc;
		this.protocoledResult = result;
		this.protocolName = protocolFileName;
	}
	
	public void start() {
		this.myThr.start();
	}
	
	public void join() throws InterruptedException {
		this.myThr.join();
	}
	
	public void run() {
		//Скопировать файл 
		String absPath = new File(".").getAbsolutePath();
		pathFrom = new File(".").getAbsolutePath() + "\\files\\protocolsTemplates\\PRT.xlsx";
		pathTo = new File(".").getAbsolutePath() + "\\Protocols\\" + this.protocolName;
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
	
	private void fillFile() throws IOException {
		String strDate = protocoledResult.get(0).getDateOfMeasByString();
		Workbook wb = new HSSFWorkbook();
		Sheet sh = wb.createSheet(strDate);
		
		ArrayList<Row> rows = new ArrayList<Row>();
		int countOfRows = 20;
		for (int i=0; i < protocoledResult.size(); i++) {
			int freqCount = protocoledResult.get(i).getCountOfFreq();
			countOfRows += (freqCount + 3);
		}
		for (int i=0; i < countOfRows; i++) {
			Row row = sh.createRow(i); 
			rows.add(row);
		}
		
		//Начинаем заполнять
		rows.get(0).createCell(1).setCellValue("Протокол поверки № " + this.verification.getProtocolNumber());//Номер протокола поверки
		rows.get(1).createCell(5).setCellValue(this.verification.getDocumentNumber());//номер свидетельства о поверке
		rows.get(2).createCell(2).setCellValue(this.verification.getDeviceInfo());//Средство измерения
		rows.get(4).createCell(3).setCellValue(this.verification.getDeviceSerNumber());//Заводской номер
		//Параметры окружающей среды
		rows.get(7).createCell(4).setCellValue(this.verification.getTemperature());//Температура
		rows.get(8).createCell(4).setCellValue(this.verification.getAirHumidity());//влажность
		rows.get(9).createCell(4).setCellValue(this.verification.getAtmPreasure());//атмосферное давление
		//И на основании результатов ... поверки признано ...
		rows.get(11).createCell(3).setCellValue(this.verification.getTypeByTime());
		rows.get(11).createCell(6).setCellValue(this.verification.getDecision());
		
		FileOutputStream fout = new FileOutputStream(pathTo);
		wb.write(fout);
		fout.close();
	}
	
}