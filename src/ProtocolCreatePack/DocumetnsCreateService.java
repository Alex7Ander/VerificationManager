package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import SecurityPack.FileEncrypterDecrypter;
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
	//Лист
	private Sheet sh;
	//Строки
	private List<Row> rows;
	
	//Стили шрифтов
	private CellStyle borderCellStyle;
	private CellStyle boldCellStyle;
	private CellStyle headCellStyle;
	private CellStyle ordinaryCellStyle;
	private CellStyle tableHeadCellStyle;
	private CellStyle elementNameStyle;
	
	private CellStyle borderModuleCellStyle;
	private CellStyle borderPhaseCellStyle;
	
	@SuppressWarnings("unused")
	private final String moduleFormatPattern = "0.000";
	@SuppressWarnings("unused")
	private final String phaseFormatPattern = "0.00";
	
	//Шифрование
	private SecretKey secretKey;
	
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
		this.sh.setAutobreaks(true);
		this.sh.setMargin(Sheet.LeftMargin, 1);
		this.sh.setMargin(Sheet.RightMargin, 1);
		this.sh.setMargin(Sheet.BottomMargin, 0.4);
		this.sh.setMargin(Sheet.TopMargin, 0.4);
		
		PrintSetup ps = sh.getPrintSetup();
		ps.setFitWidth((short)1);
		ps.setFitHeight((short)255);

		this.rows = new ArrayList<Row>();
		
		//Шрифты
		Font ordinaryfont = wb.createFont();
		ordinaryfont.setFontName("Times New Roman");
		ordinaryfont.setFontHeightInPoints((short)10);
		Font boldFont = this.wb.createFont();
		boldFont.setFontName("Times New Roman");
		boldFont.setFontHeightInPoints((short)12);
		boldFont.setBold(true);
		
		//Создадим стиль "ячейки с границами"		
		this.borderCellStyle = wb.createCellStyle();
		this.borderModuleCellStyle = wb.createCellStyle();
		this.borderPhaseCellStyle = wb.createCellStyle();
		
		this.borderCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		this.borderModuleCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		this.borderPhaseCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		
		this.borderCellStyle.setBorderTop(BorderStyle.THIN);
		this.borderModuleCellStyle.setBorderTop(BorderStyle.THIN);
		this.borderPhaseCellStyle.setBorderTop(BorderStyle.THIN);
		
		this.borderCellStyle.setBorderBottom(BorderStyle.THIN);
		this.borderModuleCellStyle.setBorderBottom(BorderStyle.THIN);
		this.borderPhaseCellStyle.setBorderBottom(BorderStyle.THIN);
		
		this.borderCellStyle.setBorderLeft(BorderStyle.THIN);
		this.borderModuleCellStyle.setBorderLeft(BorderStyle.THIN);
		this.borderPhaseCellStyle.setBorderLeft(BorderStyle.THIN);
		
		this.borderCellStyle.setBorderRight(BorderStyle.THIN);
		this.borderModuleCellStyle.setBorderRight(BorderStyle.THIN);
		this.borderPhaseCellStyle.setBorderRight(BorderStyle.THIN);
				
		this.borderModuleCellStyle.setDataFormat(wb.createDataFormat().getFormat("0.000"));
		this.borderPhaseCellStyle.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		
		this.borderCellStyle.setFont(ordinaryfont);	
		this.borderModuleCellStyle.setFont(ordinaryfont);
		this.borderPhaseCellStyle.setFont(ordinaryfont);
		
		this.borderCellStyle.setWrapText(true);
		this.borderModuleCellStyle.setWrapText(true);
		this.borderPhaseCellStyle.setWrapText(true);
		
		//Стиль для названия нагрузки
		this.elementNameStyle = this.wb.createCellStyle();
		this.elementNameStyle.setFont(boldFont);
		this.elementNameStyle.setAlignment(HorizontalAlignment.LEFT);
		
		//Стиль заголовок документа
		this.headCellStyle = this.wb.createCellStyle();
		this.headCellStyle.setFont(boldFont);
		this.headCellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		//Простой стиль
		this.ordinaryCellStyle = this.wb.createCellStyle();
		this.ordinaryCellStyle.setFont(ordinaryfont);	
		
		//Стиль заголовок таблиц
		this.tableHeadCellStyle = this.wb.createCellStyle();
		this.tableHeadCellStyle.setFont(ordinaryfont);
		this.tableHeadCellStyle.setAlignment(HorizontalAlignment.CENTER);
		this.tableHeadCellStyle.setBorderTop(BorderStyle.THIN);
		this.tableHeadCellStyle.setBorderBottom(BorderStyle.THIN);
		this.tableHeadCellStyle.setBorderRight(BorderStyle.THIN);
		this.tableHeadCellStyle.setBorderLeft(BorderStyle.THIN);
		this.tableHeadCellStyle.setWrapText(true);
		
		//Стиль - Жирный шрифт
		this.boldCellStyle = this.wb.createCellStyle(); 
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
		//prepareSheet();
		fillSheet();
		topCellsMerging();
		colScaling();
		writeSheet(this.verification.getSecretKey());
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
			writer.write(verification.getDateForDocuments() + "\n");			//13
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
		int countOfRows = 14;
		for (int i=0; i < protocoledResult.size(); i++) {
			int currentParamsCount = 1;
			if(protocoledResult.get(i).getMyOwner().getPoleCount()==4)
				currentParamsCount = 4;
			int freqCount = protocoledResult.get(i).getCountOfFreq();
			countOfRows += ((freqCount + 3) * currentParamsCount);
		}
		for (int i=0; i < countOfRows; i++) {
			Row row = sh.createRow(i); 
			rows.add(row);
		}
	}
	
	private void fillSheet() {
		System.out.println("Процедура заполнения файла Excel:");
		System.out.print("Основная информация: ");
		setCellValue(0, 0, "Протокол № ________________________", headCellStyle);		
		setCellValue(1, 0, "от " + verification.getDateForDocuments(), headCellStyle);		
		setCellValue(2, 0, verification.getEtalonString().trim() + ": " + verification.getDeviceInfo().toLowerCase(), ordinaryCellStyle);
		setCellValue(3, 0, "Заводской номер (номера): " + verification.getDeviceSerNumber(), ordinaryCellStyle);
		setCellValue(4, 0, "Принадлежащего: " + verification.getDeviceOwner(), ordinaryCellStyle);
		
		String weatherStr1 = "Условия поверки: температура " + verification.getTemperature() + " \u00B0C, относительная   влажность " + 
								verification.getAirHumidity() + " %, давление " + verification.getAtmPreasure() + " мм рт. ст.";
		setCellValue(6, 0, weatherStr1, ordinaryCellStyle);

		setCellValue(7, 0, "Средства измерений: военный эталон ВЭ-24-20", ordinaryCellStyle);
		
		setCellValue(8, 0, "Нормативно-техническая документация поверки:", ordinaryCellStyle);
		setCellValue(9, 0, "Методика поверки рабочих эталонов и средств измерений военного назначения на военном эталоне.", ordinaryCellStyle);
				
		setCellValue(11, 0, "1. Внешний осмотр: замечаний нет", ordinaryCellStyle);
		setCellValue(12, 0, "2. Опробование: аппаратура работоспособна", ordinaryCellStyle);
		setCellValue(13, 0, "3. Метрологические характеристики: представлены в таблицах", ordinaryCellStyle);
		System.out.print("\tуспешно\n");
					
		int dRow = 14;
		int countOfRes = protocoledResult.size();
		for (int i = 0; i < countOfRes; i++) {
			//Вносимые данные
			MeasResult currentRes = this.protocoledResult.get(i);
			MeasResult currentNominal = this.nominals.get(i);
			ToleranceParametrs currentModuleTolerance = this.protocoledModuleToleranceParams.get(i);
			ToleranceParametrs currentPhaseTolerance = this.protocoledPhaseToleranceParams.get(i);
			
			//Строка про элемент
			String head = currentRes.getMyOwner().getType() + " " + currentRes.getMyOwner().getSerialNumber();
			setCellValue(dRow, 0, head, ordinaryCellStyle);
			rowMerging(dRow, 0, 12);
			System.out.println(head);
			dRow++;
			
			//Заполнение таблицы
			String keys[] = {"S11", "S12", "S21", "S22"};
			int stop = 1;
			if (currentRes.getMyOwner().getPoleCount() == 4) stop = 4;
			for(int k = 0; k < stop; k++) {				
				//Шапка для таблицы
				List<String> tableHeads = createtableHeads(currentRes, keys[k]);
				for (int th = 0; th < tableHeads.size(); th++) {
					setCellValue(dRow, th, tableHeads.get(th), tableHeadCellStyle);
					System.out.print("\t" + tableHeads.get(th));
				}	
				System.out.print("\n");
				dRow++;
				
				for (int j = 0; j < currentRes.getCountOfFreq(); j++) {
					//Частота
					Double cFreq = currentRes.freqs.get(j);
					setCellValue(dRow, 0, cFreq, borderPhaseCellStyle);				
					System.out.print("\t" + cFreq);
					
					//КСВН/Г
					Double vswr = currentRes.values.get("MODULE_" + keys[k]).get(cFreq); //.toString();
					setCellValue(dRow, 1, vswr, borderModuleCellStyle);		
					System.out.print("\t" + vswr);
					
					//Погрешность КСВН/Г
					Double vswrError = currentRes.values.get("ERROR_MODULE_" + keys[k]).get(cFreq);
					setCellValue(dRow, 2, vswrError, borderModuleCellStyle);
					System.out.print("\t" + vswrError);
					
					//Номинал КСВН/Г
					Double vswrNominal = currentNominal.values.get("MODULE_" + keys[k]).get(cFreq);
					setCellValue(dRow, 3, vswrNominal, borderModuleCellStyle);
					System.out.print("\t" + vswrNominal);
					
					//Дельты по модулю
					Double moduleDelta = currentRes.differenceBetweenNominal.get("MODULE_" + keys[k]).get(cFreq);
					setCellValue(dRow, 4, moduleDelta, borderModuleCellStyle);
					System.out.print("\t" + moduleDelta);
					
					//Допуск КСВН/Г
					String vswrTolerance = currentModuleTolerance.values.get("DOWN_MODULE_" + keys[k]).get(cFreq).toString();
					vswrTolerance += ("/" + currentModuleTolerance.values.get("UP_MODULE_" + keys[k]).get(cFreq).toString());
					setCellValue(dRow, 5, vswrTolerance.replace('.', ','), borderCellStyle);
					System.out.print("\t" + vswrTolerance);
					
					//Годен/не годен по КСВН
					String moduleDecision = currentRes.suitabilityDecision.get("MODULE_" + keys[k]).get(cFreq);
					setCellValue(dRow, 6, moduleDecision, borderCellStyle);
					System.out.print("\t" + moduleDecision);
					
					if(!currentRes.getMyOwner().getType().contains("Нагрузка согласованная")) {
						//Фаза
						Double phase = currentRes.values.get("PHASE_" + keys[k]).get(cFreq);
						setCellValue(dRow, 7, phase, borderPhaseCellStyle);
						System.out.print("\t" + phase);
											
						//Погрешность Фазы
						Double phaseError = currentRes.values.get("ERROR_PHASE_" + keys[k]).get(cFreq);
						setCellValue(dRow, 8, phaseError, borderPhaseCellStyle);
						System.out.print("\t" + phaseError);
						
						//Номинал Фазы
						Double phaseNominal = currentNominal.values.get("PHASE_" + keys[k]).get(cFreq);
						setCellValue(dRow, 9, phaseNominal, borderPhaseCellStyle);
						System.out.print("\t" + phaseNominal);
						
						//Дельты по модулю
						Double phaseDelta = currentRes.differenceBetweenNominal.get("PHASE_" + keys[k]).get(cFreq);
						setCellValue(dRow, 10, phaseDelta, borderPhaseCellStyle);
						System.out.print("\t" + phaseDelta);
						
						//Допуск Фазы
						String phaseTolerance = currentPhaseTolerance.values.get("DOWN_PHASE_" + keys[k]).get(cFreq).toString();
						phaseTolerance += ("/" + currentPhaseTolerance.values.get("UP_PHASE_" + keys[k]).get(cFreq).toString());
						setCellValue(dRow, 11, phaseTolerance.replace('.', ','), borderCellStyle);
						System.out.print("\t" + phaseTolerance);
						
						//Годен/не годен по фазе
						String phaseDecision = currentRes.suitabilityDecision.get("PHASE_" + keys[k]).get(cFreq);
						setCellValue(dRow, 12, phaseDecision, borderCellStyle);	
						System.out.print("\t" + phaseDecision);
					}										
					System.out.print("\n");					
					dRow++;
				}//end j
				
				String currentElementDecision = "годен";
				for(int freqIndex = 0; freqIndex < currentRes.getCountOfFreq(); freqIndex++) {
					Double cFreq = currentRes.freqs.get(freqIndex);
					if (currentRes.suitabilityDecision.get("MODULE_" + keys[k]).get(cFreq).contains("Не соотв")) {
						currentElementDecision = "не годен";
						break;
					}
					if(!currentRes.getMyOwner().getType().contains("Нагрузка согласованная") && currentRes.suitabilityDecision.get("PHASE_" + keys[k]).get(cFreq).contains("Не соотв")) {
						currentElementDecision = "не годен";
						break;
					}
				}
				setCellValue(dRow, 0, "Результаты поверки: " + currentElementDecision, ordinaryCellStyle);
				rowMerging(dRow, 0, 12);
				dRow++;
				System.out.print("\n\n");
			} //end k		
		}//end i
		
		dRow++;
		
		setCellValue(dRow, 0, "Измерения выполнил: 	", ordinaryCellStyle);
		rowMerging(dRow, 0, 5);
		setCellValue(dRow, 6, "", ordinaryCellStyle);
		rowMerging(dRow, 6, 7);
		setCellValue(dRow, 8, this.verification.getWorkerName(), ordinaryCellStyle);
		rowMerging(dRow, 8, 12);
		dRow++;
		
		setCellValue(dRow, 0, "", ordinaryCellStyle);
		rowMerging(dRow, 0, 12);		
		dRow++;
		
		setCellValue(dRow, 0, "Ответственный за экплуатацию (хранитель эталона): ", ordinaryCellStyle);
		rowMerging(dRow, 0, 5);
		setCellValue(dRow, 6, "", ordinaryCellStyle);
		rowMerging(dRow, 6, 7);
		setCellValue(dRow, 8, this.verification.getStandartGuardianStatus() + " " + this.verification.getStandartGuardianName(), ordinaryCellStyle);
		rowMerging(dRow, 8, 12);
		dRow++;
		
		setCellValue(dRow, 0, "", ordinaryCellStyle);
		rowMerging(dRow, 0, 12);		
	}
	
	private List<String> createtableHeads(MeasResult currentRes, String key) {
		String resType = currentRes.getMyOwner().getMeasUnit();
		String currentModuleToleranceType = currentRes.getMyOwner().getModuleToleranceType();
		
		List<String> tableHeads = new ArrayList<>();
		
		String paramStr = null;
		String errorModuleHead = null;
		String moduleDifference = null;
		String nominalHead = null;
		String tolleranceHead = null;
		
		if(key.equals("S11")){
			if (resType.equals("vswr")) {
				paramStr = "КСВН\r\n(1 порт)";
				nominalHead = "Пред.пов.\nКСВН";
			} 
			else {
				paramStr = "|Г|\r\n(1 порт)";
				nominalHead = "Пред.пов.\n|Г|";
			}			
			if(currentModuleToleranceType.equals("percent")) {
				errorModuleHead = "Погр-ть,\n%";
				moduleDifference = "\u03B4, %";
				tolleranceHead = "Допуск, %";
			} 
			else {
				errorModuleHead = "Погр-ть";
				moduleDifference = "\u0394";	
				tolleranceHead = "Допуск";
			}			
		}
		else if(key.equals("S12")) {
			paramStr = "Коэф.\r\nS12, дБ";
			errorModuleHead = "Погр-ть, дБ";
			moduleDifference = "\u0394, дБ";
			nominalHead = "Пред.пов.\nкоэф. S12, дБ";
			tolleranceHead = "Допуск, дБ";
		}
		else if(key.equals("S21")) {
			paramStr = "Коэф.\r\nS21, дБ";
			errorModuleHead = "Погр-ть, дБ";
			moduleDifference = "\u0394, дБ";
			nominalHead = "Пред.пов.\nкоэф. S21, дБ";
			tolleranceHead = "Допуск, дБ";
		}
		else if(key.equals("S22")) {
			if (resType.equals("vswr")) {
				paramStr = "КСВН\r\n(2 порт)";
				nominalHead = "Пред.пов.\nКСВН";
			} 
			else {
				paramStr = "|Г|\r\n(2 порт)";
				nominalHead = "Пред.пов.\n|Г|";
			}
			
			if(currentModuleToleranceType.equals("percent")) {
				errorModuleHead = "Погр-ть,\n%";
				moduleDifference = "\u03B4, %";
				tolleranceHead = "Допуск, %";
			} 
			else {
				errorModuleHead = "Погр-ть";
				moduleDifference = "\u0394";
				tolleranceHead = "Допуск";
			}			
		}
				
		tableHeads.add("F, ГГц");
		tableHeads.add(paramStr);
		tableHeads.add(errorModuleHead);
		tableHeads.add(nominalHead); 		
		tableHeads.add(moduleDifference);
		tableHeads.add(tolleranceHead);		
		tableHeads.add("Соответсвие\r\nНТД");
		
		if(!currentRes.getMyOwner().getType().contains("Нагрузка согласованная")){
			tableHeads.add("Фаза, \u00B0");
			tableHeads.add("Погр-ть, \u00B0");
			tableHeads.add("Пред.пов., \u00B0");		
			tableHeads.add("\u0394, \u00B0");
			tableHeads.add("Допуск, \u00B0");
			tableHeads.add("Соответсвие\r\nНТД");
		}

		return tableHeads;		
	}
	
	private void setCellValue(int rowIndex, int colIndex, String value, CellStyle style) {
		if(rowIndex >= this.rows.size()) {
			while(this.rows.size()-1 != rowIndex) {
				Row newRow = sh.createRow(rowIndex); 
				rows.add(newRow);
			}
		}
		Cell cell = rows.get(rowIndex).createCell(colIndex);
		cell.setCellValue(value);		
		cell.setCellStyle(style);		
	}	
	
	private void setCellValue(int rowIndex, int colIndex, Double value, CellStyle style) {
		if(rowIndex >= this.rows.size()) {
			while(this.rows.size()-1 != rowIndex) {
				Row newRow = sh.createRow(rowIndex); 
				rows.add(newRow);
			}
		}
		Cell cell = rows.get(rowIndex).createCell(colIndex);		
		cell.setCellValue(value);
		try {
			cell.setCellType(CellType.NUMERIC);
		} catch (Exception exp) {
			exp.printStackTrace();
		}		
		cell.setCellStyle(style);
	}
	
	private void colScaling() {
		//установим автоподгонку ширины для 17 столбцов
		for (int i=0; i<17; i++) {
			sh.autoSizeColumn(i);
		}
	}
	
	private void topCellsMerging() {
		for (int i = 0; i < 14; i++) {
			sh.addMergedRegion(new CellRangeAddress(i, i, 0, 12));
		}
	}
	
	private void rowMerging(int row, int from, int to) {
		sh.addMergedRegion(new CellRangeAddress(row, row, from, to));
	}
	
	private void writeSheet(SecretKey secretKey) throws IOException {		
		//Запись в файл
		FileOutputStream fout = new FileOutputStream(protoPathTo);
		wb.write(fout);
					
		String encProtoFileName = protocolName.substring(0, protocolName.lastIndexOf('.')) + ".enc"; 
		String encProtoFilePathTo = new File(".").getAbsolutePath() + "\\files\\resurect\\" + encProtoFileName;

		FileInputStream fis= new FileInputStream(protoPathTo);
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		
				
		FileEncrypterDecrypter fileEncrypterDecrypter = null;
		try {			
			fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey);		//transformation: "AES/ECB/PKCS5Padding"
			fileEncrypterDecrypter.encrypt(buffer, encProtoFilePathTo);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException mExp) {
			mExp.printStackTrace();
		}	
					        
		fis.close();
		fout.close();
		
		verification.setPathOfProtocol("\\files\\resurect\\" + encProtoFileName);
	}

}