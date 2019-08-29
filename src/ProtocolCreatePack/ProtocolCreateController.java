package ProtocolCreatePack;

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
	
	@FXML
	private void createBtnClick() {

	}
}
