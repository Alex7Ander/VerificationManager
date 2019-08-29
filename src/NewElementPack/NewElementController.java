package NewElementPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import FileManagePack.FileManager;
import FreqTablesPack.FreqTablesWindow;
import GUIpack.StringGridFX;
import _tempHelpers.Randomizer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

public class NewElementController {

	Randomizer randManager = new Randomizer();
	@FXML
	private Button fillTableRand;
	@FXML
	private void fillTableRandClick() {
		Randomizer.fillStringGrid(this.paramsTable, 1);
	}
	
	
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
	private ComboBox<String> paramsComboBox;
	@FXML
	private ComboBox<String> elemTypesComboBox;
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
	
	//Коллекции с введенными значениями
	int timeIndex;
	int paramIndex;
	int savingIndex;
	String[] keys = {"primary_S11", "primary_S12", "primary_S21", "primary_S22", 
					 "periodic_S11", "periodic_S12", "periodic_S21", "periodic_S22"};
	private ArrayList<Double> freqs;
	
	private HashMap<String, ArrayList<String>> d_m_s; 	//нижний предел
	private HashMap<String, ArrayList<String>> m_s; 	//Модуль
	private HashMap<String, ArrayList<String>> u_m_s;	//верхний предел
	
	private HashMap<String, ArrayList<String>> d_p_s;	//нижний предел
	private HashMap<String, ArrayList<String>> p_s; 	//Фаза
	private HashMap<String, ArrayList<String>> u_p_s;	//верхний предел
	
	
	
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
	
	@FXML 
	private void agreeBtnClick(ActionEvent event) {
		//
	}
//end @FXML methods
	
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
		
//Методы для получения информации об элементе
	public String getType() {return elemTypesComboBox.getSelectionModel().getSelectedItem().toString();}
	public String getSerNum() {return serNumberTextField.getText();}
	public int getPoleCount() {
		if (this.fourPoleRB.isSelected()) return 4;
		else return 2;
	}
	public String getMeasUnit() {
		if (this.vswrRB.isSelected()) return "vswr";
		else return "gamma"; 
	}
	
	public String getToleranceType() {
		if (this.upDownToleranceRB.isSelected()) return "updown";
		else return "percent";
	}

	public ArrayList<Double> getFreqsValues(){
		return freqs;
	}		
	public HashMap<String, HashMap<Double, Double>> getNominalValues(){
		HashMap<String, HashMap<Double, Double>> nominalValues = new HashMap<String, HashMap<Double, Double>>();
				
		HashMap<Double, Double> m_S11 = new HashMap<Double, Double>();
		HashMap<Double, Double> p_S11 = new HashMap<Double, Double>();
		
		HashMap<Double, Double> m_S12 = new HashMap<Double, Double>();
		HashMap<Double, Double> p_S12 = new HashMap<Double, Double>();
		
		HashMap<Double, Double> m_S21 = new HashMap<Double, Double>();
		HashMap<Double, Double> p_S21 = new HashMap<Double, Double>();
		
		HashMap<Double, Double> m_S22 = new HashMap<Double, Double>();
		HashMap<Double, Double> p_S22 = new HashMap<Double, Double>();
		int s = freqs.size();		
		for (int j = 0; j < freqs.size(); j++) {
			m_S11.put(this.freqs.get(j), Double.parseDouble(this.m_s.get("primary_S11").get(j)));
			p_S11.put(this.freqs.get(j), Double.parseDouble(this.p_s.get("primary_S11").get(j)));
			
			if (this.getPoleCount() == 4) {
				m_S12.put(this.freqs.get(j), Double.parseDouble(this.m_s.get("primary_S12").get(j)));
				p_S12.put(this.freqs.get(j), Double.parseDouble(this.p_s.get("primary_S12").get(j)));
			
				m_S21.put(this.freqs.get(j), Double.parseDouble(this.m_s.get("primary_S21").get(j)));
				p_S21.put(this.freqs.get(j), Double.parseDouble(this.p_s.get("primary_S21").get(j)));
			
				m_S22.put(this.freqs.get(j), Double.parseDouble(this.m_s.get("primary_S22").get(j)));
				p_S22.put(this.freqs.get(j), Double.parseDouble(this.p_s.get("primary_S22").get(j)));
			}
		}		
		
		nominalValues.put("m_S11", m_S11);
		nominalValues.put("p_S11", p_S11);
		
		if (this.getPoleCount() == 4) {
			nominalValues.put("m_S12", m_S12);
			nominalValues.put("p_S12", m_S12);
			nominalValues.put("m_S21", m_S21);
			nominalValues.put("p_S21", m_S21);
			nominalValues.put("m_S22", m_S22);
			nominalValues.put("p_S22", m_S22);
		}
				
		return nominalValues;
	}
	
	public HashMap<String, HashMap<Double, Double>> getToleranceParamsValues(String TypeByTime){
		HashMap<String, HashMap<Double, Double>> tolParamsValues = new HashMap<String, HashMap<Double, Double>>();
		
		HashMap<Double, Double> d_m_S11 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_m_S11 = new HashMap<Double, Double>();
		HashMap<Double, Double> d_p_S11 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_p_S11 = new HashMap<Double, Double>();
		
		HashMap<Double, Double> d_m_S12 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_m_S12 = new HashMap<Double, Double>();
		HashMap<Double, Double> d_p_S12 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_p_S12 = new HashMap<Double, Double>();
		
		HashMap<Double, Double> d_m_S21 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_m_S21 = new HashMap<Double, Double>();
		HashMap<Double, Double> d_p_S21 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_p_S21 = new HashMap<Double, Double>();
		
		HashMap<Double, Double> d_m_S22 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_m_S22 = new HashMap<Double, Double>();
		HashMap<Double, Double> d_p_S22 = new HashMap<Double, Double>();
		HashMap<Double, Double> u_p_S22 = new HashMap<Double, Double>();
		
		for (int j = 0; j < freqs.size(); j++) {
			d_m_S11.put(this.freqs.get(j), Double.parseDouble(this.d_m_s.get(TypeByTime+"_S11").get(j)));
			u_m_S11.put(this.freqs.get(j), Double.parseDouble(this.u_m_s.get(TypeByTime+"_S11").get(j)));
			d_p_S11.put(this.freqs.get(j), Double.parseDouble(this.d_p_s.get(TypeByTime+"_S11").get(j)));
			u_p_S11.put(this.freqs.get(j), Double.parseDouble(this.u_p_s.get(TypeByTime+"_S11").get(j)));
				
			if (this.getPoleCount() == 4) {
				d_m_S12.put(this.freqs.get(j), Double.parseDouble(this.d_m_s.get(TypeByTime+"_S11").get(j)));
				u_m_S12.put(this.freqs.get(j), Double.parseDouble(this.u_m_s.get(TypeByTime+"_S11").get(j)));
				d_p_S12.put(this.freqs.get(j), Double.parseDouble(this.d_p_s.get(TypeByTime+"_S11").get(j)));
				u_p_S12.put(this.freqs.get(j), Double.parseDouble(this.u_p_s.get(TypeByTime+"_S11").get(j)));
					
				d_m_S21.put(this.freqs.get(j), Double.parseDouble(this.d_m_s.get(TypeByTime+"_S11").get(j)));
				u_m_S21.put(this.freqs.get(j), Double.parseDouble(this.u_m_s.get(TypeByTime+"_S11").get(j)));
				d_p_S21.put(this.freqs.get(j), Double.parseDouble(this.d_p_s.get(TypeByTime+"_S11").get(j)));
				u_p_S21.put(this.freqs.get(j), Double.parseDouble(this.u_p_s.get(TypeByTime+"_S11").get(j)));
					
				d_m_S22.put(this.freqs.get(j), Double.parseDouble(this.d_m_s.get(TypeByTime+"_S11").get(j)));
				u_m_S22.put(this.freqs.get(j), Double.parseDouble(this.u_m_s.get(TypeByTime+"_S11").get(j)));
				d_p_S22.put(this.freqs.get(j), Double.parseDouble(this.d_p_s.get(TypeByTime+"_S11").get(j)));
				u_p_S22.put(this.freqs.get(j), Double.parseDouble(this.u_p_s.get(TypeByTime+"_S11").get(j)));
			}
		}		
		
		tolParamsValues.put("d_m_S11", d_m_S11);
		tolParamsValues.put("u_m_S11", u_m_S11);
		tolParamsValues.put("d_p_S11", d_p_S11);
		tolParamsValues.put("u_p_S11", u_p_S11);
		
		if (this.getPoleCount() == 4) {
			tolParamsValues.put("d_m_S12", d_m_S12);
			tolParamsValues.put("u_m_S12", u_m_S12);
			tolParamsValues.put("d_p_S12", d_p_S12);
			tolParamsValues.put("u_p_S12", u_p_S12);
			
			tolParamsValues.put("d_m_S21", d_m_S21);
			tolParamsValues.put("u_m_S21", u_m_S21);
			tolParamsValues.put("d_p_S21", d_p_S21);
			tolParamsValues.put("u_p_S21", u_p_S21);
			
			tolParamsValues.put("d_m_S22", d_m_S22);
			tolParamsValues.put("u_m_S22", u_m_S22);
			tolParamsValues.put("d_p_S22", d_p_S22);
			tolParamsValues.put("u_p_S22", u_p_S22);
		}
		
		return tolParamsValues;
	}
	
//Действие по закрытию окна
	private EventHandler<WindowEvent> closeEventHandler = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
        	try {
        		int showIndex = paramIndex + timeIndex;		
        		refreshTable(savingIndex, showIndex);
        		savingIndex = showIndex;
        	}
        	catch(Exception exp) {
        		//
        	}
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler(){
    	return closeEventHandler;
    }
}
