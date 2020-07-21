package ProtocolCreatePack;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationForm.VerificationWindow;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProtocolCreateController {

	@FXML
	private ComboBox<String> docTypeComboBox;
	@FXML 
	private ComboBox<String> bossMilitaryStatusComboBox;
	@FXML 
	private ComboBox<String> standartGuardianMilitaryStatusComboBox;
	
	/*
	@FXML
	private TextField docNumberTextField;
	*/	
	@FXML
	private TextField militaryBaseName;
	@FXML 
	private TextField bossNameTextFiled;
	@FXML
	private TextField standartGuardianNameTextField;
	@FXML
	private TextField workerNameTextField;

	@FXML
	private DatePicker verificationDate;
	
	@FXML
	private TextArea reasonTextArea;
	
	@FXML
	private Label devNameLabel;
	@FXML
	private Label devSerNLabel;
	@FXML
	private Label devOwnerLabel;
	@FXML
	private Label decisionLabel;
	@FXML
	private Label workTillLabel;
	
	@FXML
	private RadioButton etalonRB;
	@FXML
	private RadioButton siRB;
	private ToggleGroup siGroup;
	@FXML
	private RadioButton printRB;
	
	@FXML
	private Button createBtn;
		
	//------------------------------------
	@FXML
	private StackPane stackPane;
	@FXML
	private ProgressIndicator progress;
	@FXML
	private Label infoLabel;
	@FXML
	private VBox infoBox;
	@FXML
	private AnchorPane progressPane;	
	//------------------------------------
	
	private ObservableList<String> militaryRanks;
	private ObservableList<String> docTypes;
	private String newProtocolName;
	private String newDocumentName;
	private List<MeasResult> protocoledResult;
	private List<MeasResult> nominals;
	private List<ToleranceParametrs> protocoledModuleToleranceParams;
	private List<ToleranceParametrs> protocoledPhaseToleranceParams;
	private VerificationProcedure verification;
	
	@FXML
	private void initialize() throws IOException {
		this.infoBox.toFront();
		this.infoBox.setOpacity(1.0);
		this.progressPane.setVisible(false);
		
		this.militaryRanks = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//ranks.txt", militaryRanks);
		}
		catch(IOException ioExp) {
			this.militaryRanks.add("Полковник");
			this.militaryRanks.add("Подполковник");
			this.militaryRanks.add("Майор");
		}
		this.bossMilitaryStatusComboBox.setItems(militaryRanks);
		this.standartGuardianMilitaryStatusComboBox.setItems(militaryRanks);
		
		this.docTypes = FXCollections.observableArrayList();	
		this.reasonTextArea.setVisible(false);
		this.decisionLabel.setVisible(false);
		
		this.siGroup = new ToggleGroup();
		this.etalonRB.setToggleGroup(siGroup);
		this.siRB.setToggleGroup(siGroup);
		this.siRB.setSelected(true);
	}
	
	public void setDevice(Device device) {
		this.devNameLabel.setText(device.getName() + " " + device.getType());
		this.devSerNLabel.setText(device.getSerialNumber());
		this.devOwnerLabel.setText(device.getOwner());
	}
	
	@FXML
	private void docTypeComboBoxChange() {
		if (!this.decisionLabel.isVisible()) {
			this.decisionLabel.setVisible(true);
		}
		if (this.docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("Cвидетельство о поверке")) {
			this.decisionLabel.setText("признан пригодным к применению");
			this.reasonTextArea.setVisible(false);
			this.verificationDate.setVisible(true);
			this.workTillLabel.setVisible(true);
		}
		else {
			this.decisionLabel.setText("признан не пригодным к применению по следующим причинам");
			this.reasonTextArea.setVisible(true);
			this.verificationDate.setVisible(false);
			this.workTillLabel.setVisible(false);
		}
	}
	
	@FXML
	private void createBtnClick() throws IOException{
		if (!checkDocType()) {
			AboutMessageWindow.createWindow("Ошибка", "Вы не выбрали тип создаваемого документа").show();
			return;
		}
		
		String addStr = "";
		
		if(this.protocoledResult != null) {
			Date dt = this.protocoledResult.get(0).getDateOfMeas();
			String strDt = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(dt); //
			addStr = this.protocoledResult.get(0).getMyOwner().getMyOwner().getName() + " " +
					this.protocoledResult.get(0).getMyOwner().getMyOwner().getType() + " " +
					this.protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber() + " от " + strDt;
			this.newProtocolName = "Протокол поверки для " + addStr + ".xls";	
		}
		else {
			Date dateOfCreation = Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
			addStr = dateFormat.format(dateOfCreation);
			verification.setPathOfProtocol("-");
		}
		this.newDocumentName = docTypeComboBox.getSelectionModel().getSelectedItem().toString() + " " + addStr + ".docx";
		
		//Скрываем информационные поля и показываем прогресс индикатор
		this.infoBox.toBack();
		this.infoBox.setOpacity(0.1);
		this.progressPane.setVisible(true);	
		
		this.verification.setBossName(getBossName());
		this.verification.setBossStatus(getBossStatus());
		this.verification.setStandartGuardianName(getStandartGuardianName());
		this.verification.setStandartGuardianStatus(getStandartGuardianStatus());
		this.verification.setWorkerName(getWorkerName());
		this.verification.setDecision(getResultDecision());	
		
		//this.verification.setProtocolNumber(getProtocolNumber());
		//this.verification.setDocumentNumber(getDocumentNumber());
		
		this.verification.setEtalonString(getEtalonString());
		this.verification.setDocType(getDocType());		
		this.verification.setDate(getDate());		
		this.verification.setFinishDate(getFinishDate());
		this.verification.setMilitaryBaseName(getMilitryBaseName());			
		this.verification.setPathOfDoc(getPathOfDoc());
		this.verification.setPathOfProtocol(getPathOfProtocol());
				
		//Создаем документы
		creteDocuments();	
	}
	
	private void creteDocuments() {	
		//Создаем поток создания протокола
		DocumetnsCreateService docService = new DocumetnsCreateService();		
		docService.setProtocolName(this.newProtocolName);
		docService.setDocumentName(this.newDocumentName);
		docService.setProtocoledResult(this.protocoledResult);
		docService.setNominals(this.nominals);
		docService.setProtocoledModuleToleranceParams(this.protocoledModuleToleranceParams);
		docService.setProtocoledPhaseToleranceParams(this.protocoledPhaseToleranceParams);
		docService.setVerificationProcedure(this.verification);
		
		//Устанавливаем действие при успешном завершении
		docService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				infoBox.toFront();
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false);
				try {
					//Вносим запись в БД о созданных протоколе и документе если необходимо
					if(verification.isShouldBeSavedInDB()) {
						try {
							verification.saveInDB();
						}
						catch(SQLException sqlExp) {
							AboutMessageWindow.createWindow("Ошибка", "Данные о созданном протоколе\n в БД не внесены.\n").show();
						}
						catch(SavingException exp) {
							AboutMessageWindow.createWindow("Ошибка", exp.getMessage()).show();
						}
					}
					
					//Показываем сообщение об успешном создании
					String docType = docTypeComboBox.getSelectionModel().getSelectedItem();
					AboutMessageWindow.createWindow("Успешно", "Документы успешно созданы.").show();
					
					//Соотносим результаты измерений с самой поверкой
					if(protocoledResult != null) {
						for(MeasResult result : protocoledResult) {
							try {
								int currentVerId = verification.getId();
								result.setVerificationId(currentVerId);
							} catch (SQLException sqlExp) {
								System.err.println("Не удалось установить id поверки для сохраненных результатов поверки (с id = " + result.getId() + ")");
								sqlExp.printStackTrace();
							}
						}
					}
										
					//Открываем созданный файл, если требуется
					if (printRB.isSelected()) {
						String protocolPath = new File(".").getAbsoluteFile() + "//Protocols//" + newProtocolName;
						String documentPath = new File(".").getAbsoluteFile() + "//Documents//" + newDocumentName;
						File protocol = new File(protocolPath);
						File document = new File(documentPath);
						try {
							Desktop.getDesktop().open(document);
							if(protocoledResult != null) {
								Desktop.getDesktop().open(protocol);
							}														
						}
						catch(IOException ioExp) {
							ioExp.getStackTrace();
						}			
					}
					//И закрываем окно поверки
					try {
						VerificationWindow.getVerificationWindow().getController().zeroingLastVerificationsId();
						VerificationWindow.getVerificationWindow().close();
						VerificationWindow.getVerificationWindow().delete();
					} catch (IOException ioExp) {
						System.err.println("Не удалось автоматически закрыть окно поверки. " + ioExp.getMessage());
					}					
				}
				finally {
					ProtocolCreateWindow.closeInstanceWindow();
					ProtocolCreateWindow.delete();
				}
			}				
		});
		//Устанавливаем действие при провале
		docService.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				infoBox.setOpacity(1.0);
				progressPane.setVisible(false); 
				AboutMessageWindow.createWindow("Ошибка", "Произошла ошибка при\nсоздании документов.\nПовторите попытку.").show();
				return;
			}
		});
		//Запускаем поток создания документов
		docService.start();
	}
	
	public void setDocTypes(String[] types) {
		for (String str : types) docTypes.add(str);
		this.docTypeComboBox.setItems(docTypes);
	}
	
	public void setResults(List<MeasResult> results) {
		this.protocoledResult = results;
	}
	
	public void setNominals(List<MeasResult> nominals) {
		this.nominals = nominals;
	}
	
	public void setModuleToleranceParams(List<ToleranceParametrs> protocoledModuleToleranceParams) {
		this.protocoledModuleToleranceParams = protocoledModuleToleranceParams;		
	}
	
	public void setPhaseToleranceParams(List<ToleranceParametrs> protocoledPhaseToleranceParams) {
		this.protocoledPhaseToleranceParams = protocoledPhaseToleranceParams;
	}
	
	private boolean checkDocType() {
		if (this.docTypeComboBox.getSelectionModel().getSelectedIndex() < 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void setVerificationProcedure(VerificationProcedure verificationProc) {
		this.verification = verificationProc;
		//setFieldsValues();
	}
	
	public String getWorkerName() {
		String name = "";
		if(this.workerNameTextField.getText() == null) {
			name = "Фамилия Имя Отчество";
		}
		else {
			name = this.workerNameTextField.getText();
		}
		return name;
	}
	public String getBossName() {
		String name = "";
		if(this.bossNameTextFiled.getText() == null) {
			name = "Фамилия Имя Отчество";
		}
		else {
			name = this.bossNameTextFiled.getText();
		}
		return name;
	}
	
	public String getBossStatus() {
		String status = "";
		try {
			status = this.bossMilitaryStatusComboBox.getSelectionModel().getSelectedItem().toString();
		}
		catch (NullPointerException npExp) {
			status = "Полковник ";
		}
		return status;
	}
	
	public String getStandartGuardianName() {
		String name = "";
		if(this.standartGuardianNameTextField.getText() == null) {
			name = "Фамилия Имя Отчество";
		}
		else {
			name = this.standartGuardianNameTextField.getText();
		}
		return name;
	}
	
	public String getStandartGuardianStatus() {
		String status = "";
		try {
			status = this.standartGuardianMilitaryStatusComboBox.getSelectionModel().getSelectedItem().toString();
		}
		catch (NullPointerException npExp) {
			status = "Майор ";
		}
		return status;
	}
	
	public String getResultDecision() {
		if (docTypeComboBox.getSelectionModel().getSelectedItem().toString().equals("Cвидетельство о поверке")) {
			return "признано годным";
		}
		else {
			return this.reasonTextArea.getText();
		}
	}
	/*
	public String getProtocolNumber() {
		return docNumberTextField.getText();
	}
	public String getDocumentNumber() {
		return docNumberTextField.getText();
	}
	*/
	public String getEtalonString() { 
		if (etalonRB.isSelected()) {
			return "Эталон";
		} else {
			return "Средство измерения ";
		}
	}

	public String getDocType() { 
		return this.docTypeComboBox.getSelectionModel().getSelectedItem().toString();
	}
	public String getDateOfCreation() {		
		Date dt = new Date();
		String strDate = new SimpleDateFormat("dd.MM.yyyy").format(dt);
		return strDate;
	}
	public String getFinishDate() {
		Date dt = new Date();
		String sdt = new SimpleDateFormat("dd.MM.yyyy").format(dt);
		Calendar calend = Calendar.getInstance();
		try {
			calend.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(sdt));
		}
		catch (ParseException pExp) {
			pExp.getStackTrace();
			calend.setTime(new Date());
		}
		calend.add(Calendar.YEAR, 1);
		String strDate = new SimpleDateFormat("dd.MM.yyyy").format(calend.getTime());		
		return strDate;
	}
	public String getMilitryBaseName() {
		String name = "";
		if(this.militaryBaseName.getText() != null) {
			name = this.militaryBaseName.getText();
		}
		return name;
	}

	/*
	public void setFieldsValues() {		
		this.devNameLabel.setText(verification.getDeviceInfo());
		this.devSerNLabel.setText(verification.getDeviceSerNumber());
		this.devOwnerLabel.setText(verification.getDeviceOwner());		
		this.militaryBaseName.setText(verification.getMilitaryBasename());
		this.reasonTextArea.setText(verification.getDecision());		
		this.workerNameTextField.setText(verification.getWorkerName());
		this.militaryStatusComboBox.setValue(verification.getBossStatus());
		this.bossNameTextFiled.setText(verification.getBossName());
		try {
			if(verification.getEtalonString().equals("Эталон")) {
				etalonRB.setSelected(true);
			}
			else {
				siRB.setSelected(true);
			}
		} 
		catch(NullPointerException npExp) {
			System.err.println("Ошибка: etalonString = null");
		}		
	}
	*/
	public String getPathOfDoc() {
		return "//Documents//" + newDocumentName;
	}
	
	public String getPathOfProtocol() {
		return "//Protocols//" + newProtocolName;
	}

	public String getDate() {
		String dateStr = null;
		if(this.protocoledResult != null) {
			dateStr = this.protocoledResult.get(0).getDateOfMeasByString();
		}
		else {
			Date dateOfCreation = Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			dateStr = dateFormat.format(dateOfCreation);
		}
		return dateStr;
	}


}