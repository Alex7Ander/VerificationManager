package ProtocolCreatePack;

import java.io.File;
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
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ProtocolCreateService extends Service<Integer> {

	private VerificationProcedure verification;
	private ArrayList<MeasResult> protocoledResult;
	private String protocolName;
	private String pathFrom;
	private String pathTo;
	
	public ProtocolCreateService(String protocolFileName, ArrayList<MeasResult> result, VerificationProcedure verificationProc){
		this.verification = verificationProc;
		this.protocoledResult = result;
		this.protocolName = protocolFileName;
	}
	
	@Override
	protected Task<Integer> createTask() {
		return new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {
				pathTo = new File(".").getAbsolutePath() + "\\Protocols\\" + protocolName;
				try {
					fillFile();
					return 0;
				}
				catch(IOException ioExp) {
					ioExp.getStackTrace();
					return 1;
				}
			}			
		};
	}
	
	private void fillFile() throws IOException {
		String strDate = protocoledResult.get(0).getDateOfMeasByString();
		Workbook wb = new HSSFWorkbook();
		Sheet sh = wb.createSheet("поверка");
		
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

		rows.get(0).createCell(1).setCellValue("Протокол поверки № " + this.verification.getProtocolNumber());
		rows.get(1).createCell(0).setCellValue("к свидетельству о поверке (извещению о непригодности) №" + this.verification.getDocumentNumber());
		rows.get(2).createCell(0).setCellValue("Средство измерений: " + this.verification.getDeviceInfo());
		rows.get(4).createCell(0).setCellValue("Заводской номер (номера): " + this.verification.getDeviceSerNumber());
 
		rows.get(6).createCell(0).setCellValue("При следующих значениях влияющих факторов:");
		rows.get(7).createCell(0).setCellValue("температура окружающего воздуха " + this.verification.getTemperature() + " град. С");
		rows.get(8).createCell(0).setCellValue("относительная   влажность " + this.verification.getAirHumidity() + " %");
		rows.get(9).createCell(0).setCellValue("атмосферное давление " + this.verification.getAtmPreasure() + " мм рт. ст.");
		String s = null;
		if (this.verification.getTypeByTime().equals("primary")) {s = "первичной";}
		else {s = "перодической";}
		rows.get(11).createCell(0).setCellValue("и на основании результатов " + s + " поверки признано " + this.verification.getDecision());
		rows.get(12).createCell(0).setCellValue("1. Внешний осмотр");
		rows.get(13).createCell(0).setCellValue("Исправность средства измерений (внешний осмотр): Исправен.");
		rows.get(14).createCell(0).setCellValue("на эксплуатационные и метрологические характеристики: не обнаружено.");
		rows.get(15).createCell(0).setCellValue("Наличие маркировки согласно требованиям ЭД: маркировка присутствует.");
		rows.get(16).createCell(0).setCellValue("2. Опробование: Аппаратура работоспособна.");
		rows.get(17).createCell(0).setCellValue("3. Метрологические характеристики:");
		
		int dRow = 19;
		int countOfRes = protocoledResult.size();
		for (int i = 0; i < countOfRes; i++) {
			//Текущий результат
			MeasResult currentRes = protocoledResult.get(i);
			
			//Строка про элемент
			String head = currentRes.getMyOwner().getType() + " " + currentRes.getMyOwner().getSerialNumber();
			rows.get(dRow).createCell(0).setCellValue(head);
			dRow++;
			
			//Шапка для таблицы
			ArrayList<String> tableHeads = createtableHeads(currentRes);				
			for (int j = 0; j < currentRes.getCountOfParams(); j++) {
				rows.get(dRow).createCell(j).setCellValue(tableHeads.get(j));
			}
			dRow++;
			
			//Заполнение таблицы
			String keys[] = {"m_S11", "err_m_S11", "p_S11", "err_m_S11", 
					 		 "m_S12", "err_m_S12", "p_S12", "err_m_S12",
					 		 "m_S21", "err_m_S121", "p_S21", "err_m_S21", 
					 		 "m_S22", "err_m_S22", "p_S22", "err_m_S22"};
			
			for (int j = 0; j < currentRes.getCountOfFreq(); j++) {
				Double cFreq = currentRes.freqs.get(j);
				rows.get(dRow).createCell(0).setCellValue(cFreq.toString());
				for (int k = 0; k < currentRes.getCountOfParams(); k++) {
					String key = keys[k];					
					String text = currentRes.values.get(key).get(cFreq).toString();					
					rows.get(dRow).createCell(k+1).setCellValue(text);
				} //end k
				dRow++;
			} //end j			
		}//end i
		
		//Запись в файл
		FileOutputStream fout = new FileOutputStream(pathTo);
		wb.write(fout);
		fout.close();
	}
	
	private ArrayList<String> createtableHeads(MeasResult currentRes) {
		ArrayList<String> tableHeads = new ArrayList<String>();
		tableHeads.add("Частота");
		if (currentRes.getMyOwner().getMeasUnit().equals("vswr")) {
			tableHeads.add("КСВН 1-го порта");
		}
		else {
			tableHeads.add("Фаза");
		}
		tableHeads.add("СКО");
		tableHeads.add("Погрешность");
		tableHeads.add("Коэф. отр. S12");
		tableHeads.add("СКО");
		tableHeads.add("Погрешность");
		tableHeads.add("Коэф. отр. S21");
		tableHeads.add("СКО");
		tableHeads.add("Погрешность");
		if (currentRes.getMyOwner().getMeasUnit().equals("vswr")) {
			tableHeads.add("КСВН 2-го порта");
		}
		else {
			tableHeads.add("Фаза");
		}
		tableHeads.add("СКО");
		tableHeads.add("Погрешность");
		return tableHeads;		
	}

}