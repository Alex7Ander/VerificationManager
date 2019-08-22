package NewElementPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import FileManagePack.FileManager;
import FreqTablesPack.FreqTablesWindow;
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
	
	//Коллекции с введенными значениями
	int timeIndex;
	int paramIndex;
	int savingIndex;
	String[] keys = {"primary_s11", "primary_s12", "primary_s21", "primary_s22", 
					 "periodical_s11", "periodical_s12", "periodical_s21", "periodical_s22"};
	private ArrayList<Double> freqs;
	
	private HashMap<String, ArrayList<String>> d_m_s; 	//нижний предел
	private HashMap<String, ArrayList<String>> m_s; 	//Модуль
	private HashMap<String, ArrayList<String>> u_m_s;	//верхний предел
	
	private HashMap<String, ArrayList<String>> d_p_s;	//нижний предел
	private HashMap<String, ArrayList<String>> p_s; 	//Фаза
	private HashMap<String, ArrayList<String>> u_p_s;	//верхний предел
	
	//Randomizer
	
	@FXML
	private void initialize() {
		timeIndex = 0;
		paramIndex = 0;
		savingIndex = 0;
		freqs = new ArrayList<Double>();
		d_m_s = new HashMap<String, ArrayList<String>>();
		m_s = new HashMap<String, ArrayList<String>>();
		u_m_s = new HashMap<String, ArrayList<String>>();
		d_p_s = new HashMap<String, ArrayList<String>>();
		p_s = new HashMap<String, ArrayList<String>>();
		u_p_s = new HashMap<String, ArrayList<String>>();
		for (int i=0; i<8; i++) {
			d_m_s.put(keys[i], new ArrayList<String>());
			m_s.put(keys[i], new ArrayList<String>());
			u_m_s.put(keys[i], new ArrayList<String>());
			
			d_p_s.put(keys[i], new ArrayList<String>());
			p_s.put(keys[i], new ArrayList<String>());
			u_p_s.put(keys[i], new ArrayList<String>());
		}
	
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
		paramsComboBox.setItems(listOfParams);
	}
	
	@FXML
	private void gammaClick(ActionEvent event) {
		currentTypeOfParams = "gamma";
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setItems(listOfParams);
	}
	
	@FXML
	private void twoPoleRBClick(ActionEvent event) {
		currentCountOfParams = 1;
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setItems(listOfParams);
		paramsComboBox.setValue(listOfParams.get(0));
		elemTypesComboBox.setItems(twoPoleTypesList);
		elemTypesComboBox.setValue("Нагрузка согласованная");
	}
	
	@FXML 
	private void fourPoleRBClick(ActionEvent event) {
		currentCountOfParams = 4;
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setItems(listOfParams);
		paramsComboBox.setValue(listOfParams.get(0));
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
		if (primaryVerificationRB.isSelected()){
			timeIndex -= 4;
			int showIndex = paramIndex + timeIndex;		
			refreshTable(savingIndex, showIndex);
			savingIndex = showIndex;
		}
	}
	
	@FXML
	private void periodicRBClick() {		
		if (periodicVerificationRB.isSelected()) {
			timeIndex += 4;
			int showIndex = paramIndex + timeIndex;		
			refreshTable(savingIndex, showIndex);
			savingIndex = showIndex;
		}
	}
				
	@FXML
	private void paramsComboBoxClick() {		
		paramIndex = this.paramsComboBox.getSelectionModel().getSelectedIndex();
		int showIndex = paramIndex + timeIndex;		
		refreshTable(savingIndex, showIndex);
		savingIndex = showIndex;
	}
	
	@FXML 
	private void agreeBtnClick(ActionEvent event) {
		//
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
	private void freaTablesBtnClick() throws IOException {
		FreqTablesWindow.getFreqTablesWindow(this).show();
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
	
	public void setFreqTable(ArrayList<Double> freqTable) {
		freqs = freqTable;
		ArrayList<String> columnFreqValues = new ArrayList<String>();
		for (Double fr : freqTable) {
			columnFreqValues.add(fr.toString());
		}
		
		int currentRowCount = this.paramsTable.getRowCount();
		if (currentRowCount < freqTable.size()) {
			while (this.paramsTable.getRowCount() < freqTable.size()) {
				this.paramsTable.addRow();
			}				
		}
		else if (currentRowCount > freqTable.size()) {
			while (this.paramsTable.getRowCount() > freqTable.size()) {
				this.paramsTable.deleteRow(this.paramsTable.getRowCount());
			}
		}
		
		this.paramsTable.setColumn(0, columnFreqValues);
	}
	
	public void refreshTable(int savingIndex, int showIndex) {
		if(savingIndex >= 0 && showIndex >= 0) {
			this.paramsTable.getColumnToDouble(0, freqs);		
			this.paramsTable.getColumn(1, d_m_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(2, m_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(3, u_m_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(4, d_p_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(5, p_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(6, u_p_s.get(keys[savingIndex]));
		
			this.paramsTable.clear();
		
			this.paramsTable.setColumnFromDouble(0, freqs);
			this.paramsTable.setColumn(1, d_m_s.get(keys[showIndex]));
			this.paramsTable.setColumn(2, m_s.get(keys[showIndex]));
			this.paramsTable.setColumn(3, u_m_s.get(keys[showIndex]));
			this.paramsTable.setColumn(4, d_p_s.get(keys[showIndex]));
			this.paramsTable.setColumn(5, p_s.get(keys[showIndex]));
			this.paramsTable.setColumn(6, u_p_s.get(keys[showIndex]));
		}
	}
	
}
