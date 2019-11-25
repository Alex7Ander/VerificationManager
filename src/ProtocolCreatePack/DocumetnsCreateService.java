package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DocumetnsCreateService extends Service<Integer> {

	private VerificationProcedure verification;
	private ArrayList<MeasResult> protocoledResult;
	private String protocolName;
	private String protoPathTo;
	//Книга
	private Workbook wb;
	//лист
	private Sheet sh;
	//Строки
	private ArrayList<Row> rows;
	//Стили шрифтов
	private CellStyle borderCellStyle;
	private CellStyle headCellStyle;
	private CellStyle ordinaryCellStyle;
	private CellStyle boldCellStyle;
	
	public DocumetnsCreateService(String protocolFileName, ArrayList<MeasResult> result, VerificationProcedure verificationProc){
		this.verification = verificationProc;
		this.protocoledResult = result;
		this.protocolName = protocolFileName;		
		wb = new HSSFWorkbook();
		String strDate = protocoledResult.get(0).getDateOfMeasByString();
		sh = wb.createSheet(WorkbookUtil.createSafeSheetName(strDate));
		rows = new ArrayList<Row>();
		//Создадим стиль "граница ячеек"
		borderCellStyle = wb.createCellStyle();
		borderCellStyle.setBorderBottom(BorderStyle.THIN);
		borderCellStyle.setBorderLeft(BorderStyle.THIN);
		borderCellStyle.setBorderRight(BorderStyle.THIN);
		borderCellStyle.setBorderTop(BorderStyle.THIN);
		Font ordinaryfont = wb.createFont();
		ordinaryfont.setFontName("Times New Roman");
		ordinaryfont.setFontHeightInPoints((short)12);
		borderCellStyle.setFont(ordinaryfont);				
		//Стиль заголовок документа
		headCellStyle = wb.createCellStyle();
		Font headFont = wb.createFont();
		headFont.setFontName("Times New Roman");
		headFont.setFontHeightInPoints((short)14);
		headFont.setBold(true);
		headCellStyle.setFont(headFont);
		headCellStyle.setAlignment(HorizontalAlignment.CENTER);		
		//Простой стиль
		ordinaryCellStyle = wb.createCellStyle();
		ordinaryCellStyle.setFont(ordinaryfont);		
		//Стиль - Жирный шрифт
		boldCellStyle = wb.createCellStyle(); 
		Font boldFont = wb.createFont();
		boldFont.setFontName("Times New Roman");
		boldFont.setFontHeightInPoints((short)12);
		boldCellStyle.setFont(boldFont);
	}
	
	@Override
	protected Task<Integer> createTask() {
		return new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {
				protoPathTo = new File(".").getAbsolutePath() + "\\Protocols\\" + protocolName;
				try {					
					if (protocoledResult != null) {
						createProtocol();
					}	
					createDocument();
					return 0;
				}
				catch(IOException ioExp) {
					ioExp.getStackTrace();
					return 1;
				}
			}			
		};
	}
	
	private void createProtocol() throws IOException {
		prepareSheet();
		fillSheet();
		topCellsMerging();
		colScaling();
		writeSheet();
	}
	
	private void createDocument() throws IOException {
		FileWriter writer = new FileWriter("proto.txt");
		try {
			String docName = this.verification.getDocType() + " для " + this.verification.getDeviceInfo() + " №" + this.verification.getDeviceSerNumber() +
					" проведенной " + this.verification.getDateOfCreation() + ".doc";			
			writer.write(docName + "\n");										//1
			writer.write(this.verification.getDocType() + "\n");				//2
			if (this.verification.isPrimary()) {
				writer.write("первичной \n");									//3
			} else {
				writer.write("периодической \n");								//3
			}
			writer.write(this.verification.getMilitaryBasename() + "\n");		//4
			writer.write(this.verification.getDocumentNumber() + "\n");			//5
			writer.write(this.verification.getDeviceInfo() + "\n");				//6
			writer.write(this.verification.getEtalonString() + "\n");			//7
			writer.write(this.verification.getDeviceSerNumber() + "\n");		//8
			writer.write(this.verification.getDeviceOwner() + "\n");			//9
			writer.write(this.verification.getWorkerName() + "\n");				//10
			writer.write(this.verification.getBossStatus() + " " + this.verification.getBossName() + "\n");		//11
			writer.write(this.verification.getDecision() + "\n");				//12
			writer.write(this.verification.getDateOfCreation() + "\n");			//13
			writer.write(this.verification.getFinishDate() + "\n");				//14
		} catch (IOException ioExp) {
			ioExp.getStackTrace();
		} finally {
			writer.close();
		}	
		String absPath = new File(".").getAbsolutePath();		
		String scriptPath = null;
		String doc = this.verification.getDocType();
		if (doc.equals("Cвидетельство о поверке")) {
			scriptPath = absPath + "\\cert.exe";
		} else {
			scriptPath = absPath + "\\notif.exe";
		}
		File file = new File(scriptPath);
		Desktop.getDesktop().open(file);
	}
	
	private void prepareSheet() {	
		//Создадим строки
		int countOfRows = 21;
		for (int i=0; i < protocoledResult.size(); i++) {
			int freqCount = protocoledResult.get(i).getCountOfFreq();
			countOfRows += (freqCount + 3);
		}
		for (int i=0; i < countOfRows; i++) {
			Row row = sh.createRow(i); 
			this.rows.add(row);
		}
	}
	
	private void fillSheet() {
		setCellValue(0, 0, "Протокол поверки № " + this.verification.getProtocolNumber(), headCellStyle);
		setCellValue(1, 0, "к свидетельству о поверке (извещению о непригодности) № " + this.verification.getDocumentNumber(), headCellStyle);		
		setCellValue(2, 0, "Средство измерений: " + this.verification.getDeviceInfo(), ordinaryCellStyle);
		setCellValue(4, 0, "Заводской номер (номера): " + this.verification.getDeviceSerNumber(), ordinaryCellStyle);
		setCellValue(6, 0, "Поверка проведена при следующих значениях влияющих факторов:", boldCellStyle);
		setCellValue(7, 0, "температура окружающего воздуха " + this.verification.getTemperature() + " град. С", ordinaryCellStyle);
		setCellValue(8, 0, "относительная   влажность " + this.verification.getAirHumidity() + " %", ordinaryCellStyle);
		setCellValue(9, 0, "атмосферное давление " + this.verification.getAtmPreasure() + " мм рт. ст.", ordinaryCellStyle);
		String s = null;
		if (this.verification.isPrimary()) {
			s = "первичной";
		}
		else {
			s = "перодической";
		}
		setCellValue(11, 0, "И на основании результатов " + s + " поверки признано " + this.verification.getDecision(), ordinaryCellStyle);
		setCellValue(12, 0, "1. Внешний осмотр: ", boldCellStyle);
		setCellValue(13, 0, "Исправность средства измерений (внешний осмотр): Исправен.", ordinaryCellStyle);
		setCellValue(14, 0, "на эксплуатационные и метрологические характеристики: не обнаружено.", ordinaryCellStyle);
		setCellValue(15, 0, "Наличие маркировки согласно требованиям ЭД: маркировка присутствует.", ordinaryCellStyle);
		setCellValue(16, 0, "2. Опробование: ", boldCellStyle);
		setCellValue(17, 0, "Аппаратура работоспособна.", ordinaryCellStyle);
		setCellValue(18, 0, "3. Метрологические характеристики: ", boldCellStyle);
			
		int dRow = 19;
		int countOfRes = protocoledResult.size();
		for (int i = 0; i < countOfRes; i++) {
			//Текущий результат
			MeasResult currentRes = protocoledResult.get(i);
			//Текущее количество параметров
			int paramsCount = currentRes.getCountOfParams();
			
			//Строка про элемент
			String head = currentRes.getMyOwner().getType() + " " + currentRes.getMyOwner().getSerialNumber();
			setCellValue(dRow, 0, head, headCellStyle);
			rowMerging(dRow, 0, 3);
			dRow++;
			
			//Шапка для таблицы
			ArrayList<String> tableHeads = createtableHeads(currentRes);				
			for (int j = 0; j < paramsCount + 1 ; j++) { // +1 поскольку частота не относится к параметрам
				String value = tableHeads.get(j);
				setCellValue(dRow, j, value, borderCellStyle);
			}
			dRow++;
			
			//Заполнение таблицы
			 String keys[] = {"MODULE_S11", "ERROR_MODULE_S11", "PHASE_S11", "ERROR_PHASE_S11",
					"MODULE_S12", "ERROR_MODULE_S12", "PHASE_S12", "ERROR_PHASE_S12",
					"MODULE_S21", "ERROR_MODULE_S21", "PHASE_S21", "ERROR_PHASE_S21",
					"MODULE_S22", "ERROR_MODULE_S22", "PHASE_S22", "ERROR_PHASE_S22"};
			
			for (int j = 0; j < currentRes.getCountOfFreq(); j++) {
				Double cFreq = currentRes.freqs.get(j);
				setCellValue(dRow, 0, cFreq.toString(), borderCellStyle);
				
				for (int k = 0; k < paramsCount; k++) {
					String key = keys[k];					
					String text = currentRes.values.get(key).get(cFreq).toString();					
					setCellValue(dRow, k+1, text, borderCellStyle);
				} //end k
				dRow++;
			} //end j		
		}//end i
	}
	
	private ArrayList<String> createtableHeads(MeasResult currentRes) {
		ArrayList<String> tableHeads = new ArrayList<String>();
		//Частота
		tableHeads.add("Частота");
		//S11
		if (currentRes.getMyOwner().getMeasUnit().equals("vswr")) {
			tableHeads.add("|КСВН| 1-го порта");
		}
		else {
			tableHeads.add("|Г|");
		}
		tableHeads.add("Погрешность");
		tableHeads.add("Фаза");
		tableHeads.add("Погрешность");
		//S12
		tableHeads.add("Коэф. отр. S12");
		tableHeads.add("Погрешность");
		tableHeads.add("Фаза");
		tableHeads.add("Погрешность");
		//S21
		tableHeads.add("Коэф. отр. S21");
		tableHeads.add("Погрешность");
		tableHeads.add("Фаза");
		tableHeads.add("Погрешность");
		//S22
		if (currentRes.getMyOwner().getMeasUnit().equals("vswr")) {
			tableHeads.add("|КСВН| 2-го порта");
		}
		else {
			tableHeads.add("|Г|");
		}
		tableHeads.add("Погрешность");
		tableHeads.add("Фаза");
		tableHeads.add("Погрешность");
		return tableHeads;		
	}
	
	private void setCellValue(int row, int col, String value, CellStyle style) {
		Cell cell = rows.get(row).createCell(col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}
	
	private void colScaling() {
		//установим автоподгонку ширины для 17 столбцов
		for (int i=0; i<17; i++) {
			sh.autoSizeColumn(i);
		}
	}
	
	private void topCellsMerging() {
		for (int i=0; i<19; i++) {
			this.sh.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
		}
	}
	
	private void rowMerging(int row, int from, int to) {
		this.sh.addMergedRegion(new CellRangeAddress(row, row, from, to));
	}
	
	private void writeSheet() throws IOException {		
		//Запись в файл
		FileOutputStream fout = new FileOutputStream(protoPathTo);
		wb.write(fout);
		fout.close();
	}

}