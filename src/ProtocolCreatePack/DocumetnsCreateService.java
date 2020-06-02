package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DocumetnsCreateService extends Service<Integer> {

	private VerificationProcedure verification;
	private List<MeasResult> protocoledResult;
	private List<MeasResult> nominals;
	private List<ToleranceParametrs> protocoledModuleToleranceParams;
	private List<ToleranceParametrs> protocoledPhaseToleranceParams;
		
	private String protocolName;
	private String documentName;
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
	
	public DocumetnsCreateService(String protocolFileName, 
								String documetFileName, 
								List<MeasResult> result, 
								List<MeasResult> nominals, 
								List<ToleranceParametrs> protocoledModuleToleranceParams,
								List<ToleranceParametrs> protocoledPhaseToleranceParams,
								VerificationProcedure verificationProc){
		this.verification = verificationProc;
		this.protocoledResult = result;
		this.nominals = nominals;
		this.protocoledModuleToleranceParams = protocoledModuleToleranceParams;
		this.protocoledPhaseToleranceParams = protocoledPhaseToleranceParams;
		
		this.protocolName = protocolFileName;
		this.documentName = documetFileName;
		this.wb = new HSSFWorkbook();
		String strDate = protocoledResult.get(0).getDateOfMeasByString();
		this.sh = wb.createSheet(WorkbookUtil.createSafeSheetName(strDate));
		this.rows = new ArrayList<Row>();
		//Создадим стиль "граница ячеек"
		this.borderCellStyle = wb.createCellStyle();
		this.borderCellStyle.setBorderBottom(BorderStyle.THIN);
		this.borderCellStyle.setBorderLeft(BorderStyle.THIN);
		this.borderCellStyle.setBorderRight(BorderStyle.THIN);
		this.borderCellStyle.setBorderTop(BorderStyle.THIN);
		Font ordinaryfont = wb.createFont();
		ordinaryfont.setFontName("Times New Roman");
		ordinaryfont.setFontHeightInPoints((short)12);
		this.borderCellStyle.setFont(ordinaryfont);				
		//Стиль заголовок документа
		this.headCellStyle = this.wb.createCellStyle();
		Font headFont = this.wb.createFont();
		headFont.setFontName("Times New Roman");
		headFont.setFontHeightInPoints((short)14);
		headFont.setBold(true);
		this.headCellStyle.setFont(headFont);
		this.headCellStyle.setAlignment(HorizontalAlignment.CENTER);		
		//Простой стиль
		this.ordinaryCellStyle = this.wb.createCellStyle();
		this.ordinaryCellStyle.setFont(ordinaryfont);		
		//Стиль - Жирный шрифт
		this.boldCellStyle = this.wb.createCellStyle(); 
		Font boldFont = this.wb.createFont();
		boldFont.setFontName("Times New Roman");
		boldFont.setFontHeightInPoints((short)12);
		this.boldCellStyle.setFont(boldFont);
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
			writer.write(documentName + "\n");								//1
			writer.write(this.verification.getDocType() + "\n");			//2
			if (this.verification.isPrimary()) {
				writer.write("первичной \n");								//3
			} else {
				writer.write("периодической \n");							//3
			}
			writer.write(verification.getMilitaryBasename() + "\n");		//4
			writer.write(verification.getDocumentNumber() + "\n");			//5
			writer.write(verification.getDeviceInfo() + "\n");				//6
			writer.write(verification.getEtalonString() + "\n");			//7
			writer.write(verification.getDeviceSerNumber() + "\n");			//8
			writer.write(verification.getDeviceOwner() + "\n");				//9
			writer.write(verification.getWorkerName() + "\n");				//10
			writer.write(verification.getBossStatus() + " " + verification.getBossName() + "\n");		//11
			writer.write(verification.getDecision() + "\n");				//12
			writer.write(verification.getDateOfCreation() + "\n");			//13
			writer.write(verification.getFinishDate() + "\n");				//14
		} catch (IOException ioExp) {
			ioExp.getStackTrace();
		} finally {
			writer.close();
		}	
		String absPath = new File(".").getAbsolutePath();		
		String scriptPath = null;
		String doc = verification.getDocType();
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
			rows.add(row);
		}
	}
	
	private void fillSheet() {
		setCellValue(0, 0, "Протокол поверки № " + verification.getProtocolNumber(), headCellStyle);
		setCellValue(1, 0, "к свидетельству о поверке (извещению о непригодности) № " + verification.getDocumentNumber(), headCellStyle);		
		setCellValue(2, 0, "Средство измерений: " + verification.getDeviceInfo(), ordinaryCellStyle);
		setCellValue(4, 0, "Заводской номер (номера): " + verification.getDeviceSerNumber(), ordinaryCellStyle);
		setCellValue(6, 0, "Поверка проведена при следующих значениях влияющих факторов:", boldCellStyle);
		setCellValue(7, 0, "температура окружающего воздуха " + verification.getTemperature() + " град. С", ordinaryCellStyle);
		setCellValue(8, 0, "относительная   влажность " + verification.getAirHumidity() + " %", ordinaryCellStyle);
		setCellValue(9, 0, "атмосферное давление " + verification.getAtmPreasure() + " мм рт. ст.", ordinaryCellStyle);
		String s = null;
		if (this.verification.isPrimary()) {
			s = "первичной";
		}
		else {
			s = "перодической";
		}
		setCellValue(11, 0, "И на основании результатов " + s + " поверки признано " + verification.getDecision(), ordinaryCellStyle);
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
			//Вносимые данные
			MeasResult currentRes = this.protocoledResult.get(i);
			MeasResult currentNominal = this.nominals.get(i);
			ToleranceParametrs currentModuleTolerance = this.protocoledModuleToleranceParams.get(i);
			ToleranceParametrs currentPhaseTolerance = this.protocoledPhaseToleranceParams.get(i);
			
			//Строка про элемент
			String head = currentRes.getMyOwner().getType() + " " + currentRes.getMyOwner().getSerialNumber();
			setCellValue(dRow, 0, head, headCellStyle);
			rowMerging(dRow, 0, 10);
			dRow++;
			
			//Заполнение таблицы
			String keys[] = {"S11", "S12", "S21", "S22"};
			int stop = 1;
			if (currentRes.getMyOwner().getPoleCount()==4) stop = 4;
			for(int k = 0; k < stop; k++) {				
				//Шапка для таблицы
				String resType = currentRes.getMyOwner().getMeasUnit();
				List<String> tableHeads = createtableHeads(resType, keys[k]);
				for (int j = 0; j < tableHeads.size(); j++)
					setCellValue(dRow, j, tableHeads.get(j), borderCellStyle);
				dRow++;
				
				for (int j = 0; j < currentRes.getCountOfFreq(); j++) {
					//Частота
					Double cFreq = currentRes.freqs.get(j);
					setCellValue(dRow, 0, cFreq.toString(), borderCellStyle);
					
					//КСВН/Г
					String vswr = currentRes.values.get("MODULE_" + keys[k]).get(cFreq).toString();
					setCellValue(dRow, 1, vswr.replace('.', ','), borderCellStyle);
					
					//Номинал КСВН/Г
					String vswrNominal = currentNominal.values.get("MODULE_" + keys[k]).get(cFreq).toString();
					setCellValue(dRow, 2, vswrNominal.replace('.', ','), borderCellStyle);
					
					//Допуск КСВН/Г
					String vswrTolerance = currentModuleTolerance.values.get("DOWN_MODULE_" + keys[k]).get(cFreq).toString();
					vswrTolerance += ("/" + currentModuleTolerance.values.get("UP_MODULE_" + keys[k]).get(cFreq).toString());
					setCellValue(dRow, 3, vswrTolerance, borderCellStyle);
					
					//Погрешность КСВН/Г
					String vswrError = currentRes.values.get("ERROR_MODULE_" + keys[k]).get(cFreq).toString();
					setCellValue(dRow, 4, vswrError.replace('.', ','), borderCellStyle);
					
					//Годен/не годен по КСВН
					String moduleDecision = currentRes.suitabilityDecision.get("MODULE_" + keys[k]).get(cFreq);
					setCellValue(dRow, 5, moduleDecision, borderCellStyle);
					
					//Фаза
					String phase = currentRes.values.get("PHASE_" + keys[k]).get(cFreq).toString();
					setCellValue(dRow, 6, phase.replace('.', ','), borderCellStyle);
					
					//Номинал Фазы
					String phaseNominal = currentNominal.values.get("PHASE_" + keys[k]).get(cFreq).toString();
					setCellValue(dRow, 7, phaseNominal.replace('.', ','), borderCellStyle);
					
					//Допуск Фазы
					String phaseTolerance = currentPhaseTolerance.values.get("DOWN_PHASE_" + keys[k]).get(cFreq).toString();
					phaseTolerance += ("/" + currentPhaseTolerance.values.get("UP_PHASE_" + keys[k]).get(cFreq).toString());
					setCellValue(dRow, 8, phaseTolerance, borderCellStyle);
					
					//Погрешность Фазы
					String phaseError = currentRes.values.get("ERROR_PHASE_" + keys[k]).get(cFreq).toString();
					setCellValue(dRow, 9, phaseError.replace('.', ','), borderCellStyle);

					//Годен/не годен по фазе
					String phaseDecision = currentRes.suitabilityDecision.get("PHASE_" + keys[k]).get(cFreq);
					setCellValue(dRow, 10, phaseDecision, borderCellStyle);
					
					dRow++;
				}//end j			 
			} //end k		
		}//end i
	}
	
	private List<String> createtableHeads(String resType, String key) {
		List<String> tableHeads = new ArrayList<>();
		tableHeads.add("F, ГГц");
		if(key.equals("S11")){
			if (resType.equals("vswr")) {
				tableHeads.add("КСВН 1-го порта");
			} else {
				tableHeads.add("|Г| 1-го порта");
			}
		}
		else if(key.equals("S12")) {
			tableHeads.add("Коэф. перед. S12");
		}
		else if(key.equals("S21")) {
			tableHeads.add("Коэф. перед. S21");
		}
		else if(key.equals("S22")) {
			if (resType.equals("vswr")) {
				tableHeads.add("КСВН 2-го порта");
			} else {
				tableHeads.add("|Г| 2-го порта");
			}
		}
		tableHeads.add("Пред.пов.");
		tableHeads.add("Допуск");
		tableHeads.add("Погрешность");
		tableHeads.add("Соответсвие НТД");
		
		tableHeads.add("Фаза, \u00B0");
		tableHeads.add("Пред.пов.");
		tableHeads.add("Допуск");
		tableHeads.add("Погрешность");
		tableHeads.add("Соответсвие НТД");
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
			sh.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
		}
	}
	
	private void rowMerging(int row, int from, int to) {
		sh.addMergedRegion(new CellRangeAddress(row, row, from, to));
	}
	
	private void writeSheet() throws IOException {		
		//Запись в файл
		FileOutputStream fout = new FileOutputStream(protoPathTo);
		wb.write(fout);
		fout.close();
	}

}