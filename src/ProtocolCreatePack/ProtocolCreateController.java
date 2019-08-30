package ProtocolCreatePack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import VerificationPack.MeasResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class ProtocolCreateController {

	@FXML
	private ComboBox<String> docTypeComboBox;
	@FXML 
	private ComboBox<String> militaryStatusComboBox;
	
	@FXML
	private TextField docNumberTextField;	
	@FXML
	private TextField militaryBaseName;
	@FXML 
	private TextField bossNameTextFiled;
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
	private Label verTypeLabel;
	@FXML
	private Label decisionLabel;
	
	@FXML
	private RadioButton etalonRB;
	@FXML
	private RadioButton siRB;
	private ToggleGroup siGroup;
	@FXML
	private CheckBox printCheckBox;
	
	@FXML
	private Button createBtn;
			
	private ObservableList militaryRanks;
	private ObservableList docTypes;
	private String newProtocolName;
	private String newDocumentName;
	private ArrayList<MeasResult> protocoledResult;
		
	@FXML
	private void initialize() {
		militaryRanks = FXCollections.observableArrayList();
		docTypes = FXCollections.observableArrayList();	
		
		siGroup = new ToggleGroup();
		etalonRB.setToggleGroup(siGroup);
		siRB.setToggleGroup(siGroup);
		
	}
	
	public void setDocTypes(String[] types) {
		for (String str : types) docTypes.add(str);
		docTypeComboBox.setItems(docTypes);
	}
	
	public void setResults(ArrayList<MeasResult> results) {
		this.protocoledResult = results;
	}
	
	@FXML
	private void createBtnClick() {
		
		if (!checkDocType()) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Вы не выбрали тип создаваемого документа");
				msgWin.show();
			}
			finally {
				return;
			}
		}
		
		Date dt = protocoledResult.get(0).getDateOfMeas();
		String strDt = new SimpleDateFormat("DD/mm/yy HH/mm/ss").format(dt);
		String addStr = protocoledResult.get(0).getMyOwner().getMyOwner().getName() + " " +
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getType() + " " +
				   		protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber() + " от " + strDt;
		newProtocolName = "Протокол поверки для " + addStr + ".xlsx";
		newDocumentName = docTypeComboBox.getSelectionModel().getSelectedItem().toString() + " " + addStr + ".docx";
		
		//Запускаем поток создания протокола, если были проведены измерения
		if (protocoledResult != null) {
			ProtocolCreateThread crtThread = new ProtocolCreateThread("crtThread", newProtocolName, protocoledResult);
			crtThread.start();
			try {
				crtThread.join();
			}
			catch(InterruptedException iExp) {
				
			}
		}
		
		//Запускаем поток создания документа (извещения о непригодности или свидетельства о поверке)
		/*
		while(!crtThread.isInterrupted()) {
			
		}
		*/
		try {
			makeRecordInDB();
		}
		catch(SQLException sqlExp) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка внесения данных о созданном протоколе в БД");
				msgWin.show();
			}
			catch(IOException ioExp) {
				//
			}
		}
		
	}
	
	private void makeRecordInDB() throws SQLException {
		String date = protocoledResult.get(0).getDateOfMeasByString();
		String devName = protocoledResult.get(0).getMyOwner().getMyOwner().getName();
		String devType = protocoledResult.get(0).getMyOwner().getMyOwner().getType();
		String devSerN = protocoledResult.get(0).getMyOwner().getMyOwner().getSerialNumber();
		String pathToProtocol = new File(".").getAbsolutePath() + "//Protocols//" + newProtocolName;
		String pathToDocument = new File(".").getAbsolutePath() + "//Protocols//" + newDocumentName;
		String docT = docTypeComboBox.getSelectionModel().getSelectedItem().toString();
		String sqlQuery = "INSERT INTO Verifications (data, deviceName, deviceType, deviceSerNum, pathOfDoc, pathOfProtocol, typeOFDoc) VALUES "
											  + "('"+date+"','"+devName+"','"+devType+"','"+devSerN+"','"+pathToDocument+"','"+pathToProtocol+"','"+docT+"')";
		DataBaseManager.getDB().sqlQueryUpdate(sqlQuery);
	}
	
	private boolean checkDocType() {
		if (this.docTypeComboBox.getSelectionModel().getSelectedIndex() < 0) {
			return false;
		}
		else {
			return true;
		}
	}
}
