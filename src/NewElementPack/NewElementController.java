package NewElementPack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import FileManagePack.FileManager;
import GUIpack.StringGridFX;
import ToleranceParamPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class NewElementController {

	@FXML
	private Button agreeBtn; 
	@FXML
	private Button addFreqBtn;
	@FXML
	private Button delFreqBtn;
	@FXML
	private Button freqTablesBtn;
		
	@FXML 
	private RadioButton primaryVerificationRB;	
	@FXML 
	private RadioButton periodicVerificationRB;	
	@FXML
	private RadioButton twoPoleRB;
	@FXML
	private RadioButton fourPoleRB;
	@FXML
	private RadioButton vswrRB;	
	@FXML
	private RadioButton gammaRB;	
	@FXML
	private RadioButton percentToleranceRB;	
	@FXML
	private RadioButton upDownToleranceRB;
	
	@FXML
	private ComboBox paramsComboBox;
	@FXML
	private ComboBox elemTypesComboBox;
	@FXML
	private TextField serNumberTextField;
	
	private ObservableList<String> twoPoleTypesList;
	private ObservableList<String> fourPoleTypesList;
	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	
	private StringGridFX paramsTable;
	private StringGridFX paramsTable1;
	private StringGridFX paramsTable2;
	private StringGridFX paramsTable3;
	private StringGridFX paramsTable4;
	private ArrayList<StringGridFX> tables;
	
	private ToggleGroup poleCountGroup;
	private ToggleGroup measUnitGroup;
	private ToggleGroup toleranceTypeGroup;
	private ToggleGroup verificationTypeGroup;
	
	ObservableList<String> listOfParams;
	private int currentCountOfParams;
	private String currentTypeOfParams;
	private String currentTypeOfTolerance;
	private int lastIndex;
	
	private ArrayList<Double> Freqs;
	private HashMap<Integer, ArrayList<ArrayList<Double>>> tableValues;
	
	private MeasResult Nominal;
	private ToleranceParametrs PrimaryTolParams;
	private ToleranceParametrs PeriodicTolParams;
	//Randomizer
	
	@FXML
	private void initialize() {

		listOfParams = FXCollections.observableArrayList();
		twoPoleTypesList = FXCollections.observableArrayList();
		fourPoleTypesList = FXCollections.observableArrayList();		
		twoPoleTypesList.add("Нагрузка согласованная");
		twoPoleTypesList.add("Нагрузка рассогласованная");
		twoPoleTypesList.add("Нагрузка короткозамкнутая");
		twoPoleTypesList.add("Нагрузка холостоходная");
		twoPoleTypesList.add("Нагрузка подвижная согласованная");
		twoPoleTypesList.add("Нагрузка подвижная рассогласованная");
		fourPoleTypesList.add("Отрезок линии");
		fourPoleTypesList.add("Вентиль");
		fourPoleTypesList.add("Аттенюатор");
		twoPoleTypesList.add("Преобразователь мощности");
		elemTypesComboBox.setItems(twoPoleTypesList);
		elemTypesComboBox.setValue("Нагрузка согласованная");		
		
		poleCountGroup = new ToggleGroup();
		this.twoPoleRB.setSelected(true);
		this.twoPoleRB.setToggleGroup(poleCountGroup);
		this.fourPoleRB.setToggleGroup(poleCountGroup);
		
		measUnitGroup = new ToggleGroup();
		this.vswrRB.setSelected(true);
		this.vswrRB.setToggleGroup(measUnitGroup);
		this.gammaRB.setToggleGroup(measUnitGroup);
		
		toleranceTypeGroup = new ToggleGroup();
		this.percentToleranceRB.setSelected(true);
		this.percentToleranceRB.setToggleGroup(toleranceTypeGroup);
		this.upDownToleranceRB.setToggleGroup(toleranceTypeGroup);
		
		verificationTypeGroup = new ToggleGroup(); 
		this.primaryVerificationRB.setSelected(true);
		this.primaryVerificationRB.setToggleGroup(verificationTypeGroup);
		this.periodicVerificationRB.setToggleGroup(verificationTypeGroup);
		
		currentCountOfParams = 1;
		currentTypeOfParams = "vswr";		
		setParams(currentTypeOfParams, currentCountOfParams);
		this.paramsComboBox.setItems(listOfParams);
		this.paramsComboBox.setValue(listOfParams.get(0));
		lastIndex = 0;
		
		ArrayList<String> paramTableHeads = new ArrayList<String>();
		paramTableHeads.add("Частота, ГГц");
		paramTableHeads.add("Нижний допуск");
		paramTableHeads.add("Номинал");
		paramTableHeads.add("Верхний допуск");
		paramTableHeads.add("Нижний допуск");
		paramTableHeads.add("Номинал");
		paramTableHeads.add("Верхний допуск");
		
		paramsTable = new StringGridFX(7, 10, 850, 100, scrollPane, tablePane, paramTableHeads);		
		Freqs = new ArrayList<Double>();
		tableValues = new HashMap<Integer, ArrayList<ArrayList<Double>>>();

	}
	
	@FXML
	private void vswrRBClick(ActionEvent event) {
		currentTypeOfParams = "vswr";
		setParams(currentTypeOfParams, currentCountOfParams);
		this.paramsComboBox.setItems(listOfParams);
	}
	
	@FXML
	private void gammaClick(ActionEvent event) {
		currentTypeOfParams = "gamma";
		setParams(currentTypeOfParams, currentCountOfParams);
		this.paramsComboBox.setItems(listOfParams);
	}
	
	@FXML
	private void twoPoleRBClick(ActionEvent event) {
		currentCountOfParams = 1;
		setParams(currentTypeOfParams, currentCountOfParams);
		this.paramsComboBox.setItems(listOfParams);
		elemTypesComboBox.setItems(twoPoleTypesList);
		elemTypesComboBox.setValue("Нагрузка согласованная");
	}
	
	@FXML 
	private void fourPoleRBClick(ActionEvent event) {
		currentCountOfParams = 4;
		setParams(currentTypeOfParams, currentCountOfParams);
		this.paramsComboBox.setItems(listOfParams);
		elemTypesComboBox.setItems(fourPoleTypesList);
		elemTypesComboBox.setValue("Отрезок линии");
	}
	
	@FXML
	private void upDownToleranceRBClick() {
		
	}
	
	@FXML
	private void percentToleranceRBClick() {
		
	}
	
	@FXML
	private void primaryRBClick() {
		
	}
	
	@FXML
	private void periodicRBClick() {
		
	}
		
	@FXML 
	private void agreeBtnClick(ActionEvent event) {
		ArrayList<Double> Freqs = new ArrayList<Double>();
		/*
		if (this.vswrRB.isCache()) {
			
			Nominal = new VSWR_Result();
		}
		else {
			Nominal = new Gamma_Result();
		}
		*/
	}
	
	@FXML 
	private void addFreqBtnClick(ActionEvent event) {
		paramsTable.addRow();
	}
	
	@FXML
	private void delFreqBtnClick(ActionEvent event) {
		paramsTable.deleteRow(paramsTable.getRowCount());
	}
	
	@FXML
	private void paramsComboBoxClick() {
		/*
		int index = this.paramsComboBox.getSelectionModel().getSelectedIndex();
		for (int i=0; i<tables.size(); i++) {
			tables.get(i).setVisible(false);
		}
		tables.get(index).setVisible(true);
		*/
	}
	
	@FXML
	private void freaTablesBtnClick() {
		
	}
	
	private void setParams(String paramsIndex, int countOfParams) {
		try {
			String path = new File(".").getAbsolutePath();
			if (paramsIndex.equals("vswr")) path += "\\files\\vswr.txt";
			else if (paramsIndex.equals("gamma")) path += "\\files\\gamma.txt";
			FileManager.LinesToItems(path, countOfParams, listOfParams);			
		}
		catch(Exception exp) {
			listOfParams.clear();
			listOfParams.add("S11");
			listOfParams.add("S12");
			listOfParams.add("S21");
			listOfParams.add("S22");
		}
	}
	
}
