package NewElementPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DevicePack.Element;
import FileManagePack.FileManager;
import FreqTablesPack.FreqTablesWindow;
import GUIpack.Tables.NewElementNominalsTable;
import GUIpack.Tables.NewElementToleranceParamsTable;
import GUIpack.Tables.Table;
import ToleranceParamPack.ParametrsPack.MeasUnitPart;
import ToleranceParamPack.ParametrsPack.S_Parametr;
import ToleranceParamPack.ParametrsPack.TimeType;
import VerificationPack.Gamma_Result;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class NewElementController {

	@FXML
	private Button addFreqBtn;
	@FXML
	private Button delFreqBtn;
	@FXML
	private Button freqTablesBtn;
	@FXML
	private Button cloneS11ToS22Btn;
	
//Элементы для автозаполнения таблицы
	//кнопки
	@FXML
	private Button autoDownModuleBtn;
	@FXML
	private Button autoModuleBtn;
	@FXML
	private Button autoUpModuleBtn;
	@FXML
	private Button autoDownPhaseBtn;
	@FXML
	private Button autoPhaseBtn;
	@FXML
	private Button autoUpPhaseBtn;
	//текстовые поля
	@FXML
	private TextField moduleTextField;
	@FXML
	private TextField downModuleTextField;
	@FXML
	private TextField upModuleTextField;
	@FXML
	private TextField phaseTextField;
	@FXML
	private TextField downPhaseTextField;
	@FXML
	private TextField upPhaseTextField;
	
	/*
	@FXML
	private Label moduleLabel;
	@FXML
	private Label phaseLable;
	*/
	@FXML
	private Label tolParamsNameLabel;
		
	@FXML 
	private RadioButton primaryVerificationRB;	
	@FXML 
	private RadioButton periodicVerificationRB;	
	@FXML
	private RadioButton twoPoleRB;
	public RadioButton getTwoPoleRB() {
		return twoPoleRB;
	}
	@FXML
	private RadioButton fourPoleRB;
	public RadioButton getFourPoleRB() {
		return fourPoleRB;
	}
	@FXML
	private RadioButton vswrRB;	
	@FXML
	private RadioButton gammaRB;	
	@FXML
	private RadioButton percentToleranceRB;	
	@FXML
	private RadioButton upDownToleranceRB;
	@FXML
	private RadioButton percentPhaseToleranceRB;
	@FXML
	private RadioButton upDownPhaseToleranceRB;
	
	@FXML
	private ComboBox<String> paramsComboBox;
	@FXML
	private ComboBox<String> elemTypesComboBox;
	@FXML
	private TextField serNumberTextField;
	
	private ObservableList<String> twoPoleTypesList;
	private ObservableList<String> fourPoleTypesList;

//Tables
	@FXML
	private ScrollPane nominalsScrollPane;
	@FXML
	private AnchorPane nominalsTablePane;
	@FXML
	private StackPane tablesStack;
	@FXML
	private ScrollPane primaryScrollPane;
	@FXML
	private AnchorPane primaryTablePane;
	@FXML
	private ScrollPane periodicScrollPane;
	@FXML
	private AnchorPane periodicTablePane;
	
	private NewElementNominalsTable nominalsTable; //NewElementNominalsTable
	private NewElementToleranceParamsTable primaryToleranceParamsTable;
	private NewElementToleranceParamsTable periodicToleranceParamsTable; 
	
	private NewElementToleranceParamsTable visibleParamsTable; //ссылка на таблицу, отображаемую в данный момент времени
	
	public NewElementNominalsTable getNominalsTable() {
		return nominalsTable;
	}	
	public NewElementToleranceParamsTable getPrimaryToleranceParamsTable() {
		return primaryToleranceParamsTable;
	}
	public NewElementToleranceParamsTable getPeriodicToleranceParamsTable() {
		return periodicToleranceParamsTable;
	}
	
	private ToggleGroup poleCountGroup;
	private ToggleGroup measUnitGroup;
	private ToggleGroup toleranceTypeGroup;
	private ToggleGroup verificationTypeGroup;
	private ToggleGroup phaseTolearnceTypeGroup;
	
	//представляемый элемент	
	private Element currentElement;
	public void setElement(Element element) { 
		currentElement = element;
		if (currentElement != null) {
			serNumberTextField.setText(currentElement.getSerialNumber());
			elemTypesComboBox.setValue(currentElement.getType());
			
			//Уcтановим poleCount
			if(element.getPoleCount() == 4) {
				this.fourPoleRB.setSelected(true);
				this.currentCountOfParams = 4;
			}
			else {
				this.twoPoleRB.setSelected(true);
				this.currentCountOfParams = 1;
			}
			//Установим типы допусков
			if(element.getModuleToleranceType().equals("percent")) {
				this.percentToleranceRB.setSelected(true);
			}
			else {
				this.upDownToleranceRB.setSelected(true);
			}
			if(element.getMeasUnit().equals("vswr")) {
				this.vswrRB.setSelected(true);
			}
			else {
				this.gammaRB.setSelected(true);
			}
			
			setParams(currentTypeOfParams, currentElement.getSParamsCout());
			
			while(currentElement.getNominal().freqs.size() > nominalsTable.getRowCount()) {
				nominalsTable.addRow();
				periodicToleranceParamsTable.addRow();
				primaryToleranceParamsTable.addRow();
			}
			
			nominalsTable.setValuesFromElement(currentElement);
			nominalsTable.showParametr(currentS);
			
			periodicToleranceParamsTable.setValuesFromElement(currentElement);
			periodicToleranceParamsTable.showParametr(currentS);
			
			primaryToleranceParamsTable.setValuesFromElement(currentElement);			
			primaryToleranceParamsTable.showParametr(currentS);
			
			if (primaryVerificationRB.isSelected()) {
				periodicScrollPane.toBack();
			} else {
				primaryScrollPane.toBack();
			}
		}
	}
	public Element getElement() {
		return currentElement;
	}
//************************************************	
		
	ObservableList<String> listOfParams;
	private int currentCountOfParams;
	private String currentTypeOfParams;
	//private int lastIndex;
	
	//Коллекции с введенными значениями
	int timeIndex;
	int paramIndex;
	int savingIndex;
	String[] keys = {"primary_S11", "primary_S12", "primary_S21", "primary_S22", 
					 "periodic_S11", "periodic_S12", "periodic_S21", "periodic_S22"};
	private S_Parametr currentS;
	
	@FXML
	private void initialize() {			
		//Создаем таблицы
		nominalsTable = new NewElementNominalsTable(nominalsTablePane);
		primaryToleranceParamsTable = new NewElementToleranceParamsTable(primaryTablePane, TimeType.PRIMARY);
		periodicToleranceParamsTable = new NewElementToleranceParamsTable(periodicTablePane, TimeType.PERIODIC); 
		
		visibleParamsTable = primaryToleranceParamsTable;
		periodicScrollPane.toBack();
		
		currentS = S_Parametr.S11;
		listOfParams = FXCollections.observableArrayList();
		twoPoleTypesList = FXCollections.observableArrayList();
		fourPoleTypesList = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//twoPoleTypes.txt", twoPoleTypesList);
		} catch (Exception exp) {
			twoPoleTypesList.add("Двухполюсник");			
		}
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//fourPoleTypes.txt", fourPoleTypesList);
		} catch (Exception exp) {
			fourPoleTypesList.add("Четырехполюсник");
		}	
		elemTypesComboBox.setItems(twoPoleTypesList);
		elemTypesComboBox.setValue(twoPoleTypesList.get(0));
		
		poleCountGroup = new ToggleGroup();
		twoPoleRB.setSelected(true);
		twoPoleRB.setToggleGroup(poleCountGroup);
		fourPoleRB.setToggleGroup(poleCountGroup);
		
		measUnitGroup = new ToggleGroup();
		vswrRB.setSelected(true);
		vswrRB.setToggleGroup(measUnitGroup);
		gammaRB.setToggleGroup(measUnitGroup);
		
		toleranceTypeGroup = new ToggleGroup();
		percentToleranceRB.setSelected(true);
		percentToleranceRB.setToggleGroup(toleranceTypeGroup);
		upDownToleranceRB.setToggleGroup(toleranceTypeGroup);
		
		verificationTypeGroup = new ToggleGroup(); 
		primaryVerificationRB.setSelected(true);
		primaryVerificationRB.setToggleGroup(verificationTypeGroup);
		periodicVerificationRB.setToggleGroup(verificationTypeGroup);
		
		phaseTolearnceTypeGroup = new ToggleGroup();
		upDownPhaseToleranceRB.setSelected(true);
		upDownPhaseToleranceRB.setToggleGroup(phaseTolearnceTypeGroup);
		percentPhaseToleranceRB.setToggleGroup(phaseTolearnceTypeGroup);
		
		currentCountOfParams = 1;
		currentTypeOfParams = "vswr";		
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setItems(listOfParams);
		paramsComboBox.setValue(listOfParams.get(0));		
	}
	
	private boolean isCorrectValueForInputing(String value) {
		try {
			Double.parseDouble(value);
			return true;
		}
		catch(NumberFormatException nfExp) {
			return false;
		}
	}	
	@FXML
	private void autoModuleBtnClick() {
		int stop = visibleParamsTable.getRowCount();
		String text = this.moduleTextField.getText();
		text = text.replace(",", ".");
		if(isCorrectValueForInputing(text)) {
			for (int i=0; i<stop; i++) {
				this.nominalsTable.setCellValue(1, i, text);
			}
		}
	}
	@FXML
	private void autoPhaseBtnClick() {
		int stop = visibleParamsTable.getRowCount();
		String text = this.phaseTextField.getText();
		text = text.replace(",", ".");
		if(isCorrectValueForInputing(text)) {
			for (int i=0; i<stop; i++) {
				this.nominalsTable.setCellValue(2, i, text);
			}
		}

	}	
	@FXML
	private void autoDownModuleBtnClick() {
		int stop = visibleParamsTable.getRowCount();
		String text = this.downModuleTextField.getText();
		text = text.replace(",", ".");
		if(isCorrectValueForInputing(text)) {
			for (int i=0; i<stop; i++) {
				visibleParamsTable.setCellValue(1, i, text);
			}
		}

	}	
	@FXML
	private void autoUpModuleBtnClick() {
		int stop = visibleParamsTable.getRowCount();
		String text = this.upModuleTextField.getText();
		text = text.replace(",", ".");
		if(isCorrectValueForInputing(text)) {
			for (int i=0; i<stop; i++) {
				visibleParamsTable.setCellValue(2, i, text);
			}			
		}

	}
	@FXML
	private void autoDownPhaseBtnClick() {
		int stop = visibleParamsTable.getRowCount();
		String text = this.downPhaseTextField.getText();
		text = text.replace(",", ".");
		if(isCorrectValueForInputing(text)) {
			for (int i=0; i<stop; i++) {
				visibleParamsTable.setCellValue(3, i, text);
			}
		}
	}
	@FXML
	private void autoUpPhaseBtnClick() {
		int stop = visibleParamsTable.getRowCount();
		String text = this.upPhaseTextField.getText();
		text = text.replace(",", ".");
		if(isCorrectValueForInputing(text)) {
			for (int i=0; i<stop; i++) {
				visibleParamsTable.setCellValue(4, i, text);
			}
		}
	}
	
	@FXML
	private void vswrRBClick(ActionEvent event) {
		currentTypeOfParams = "vswr";
		setParams(currentTypeOfParams, currentCountOfParams);
		//paramsComboBox.setItems(listOfParams);
	}
	
	@FXML
	private void gammaClick(ActionEvent event) {
		currentTypeOfParams = "gamma";
		setParams(currentTypeOfParams, currentCountOfParams);
		//paramsComboBox.setItems(listOfParams);
	}
	
	@FXML
	private void twoPoleRBClick(ActionEvent event) {
		currentCountOfParams = 1;		
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setValue(listOfParams.get(0));
		elemTypesComboBox.setItems(twoPoleTypesList);
		elemTypesComboBox.setValue(twoPoleTypesList.get(0));
	}
	
	@FXML 
	private void fourPoleRBClick(ActionEvent event) {
		currentCountOfParams = 4;
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setValue(listOfParams.get(0));
		elemTypesComboBox.setItems(fourPoleTypesList);
		elemTypesComboBox.setValue(fourPoleTypesList.get(0));
	}
	
	@FXML
	private void primaryRBClick() {
		visibleParamsTable = this.primaryToleranceParamsTable;
		periodicScrollPane.toBack();
		if (!visibleParamsTable.getCurrentS().equals(currentS)) {
			visibleParamsTable.changeSParametr(currentS);
		}
		tolParamsNameLabel.setText("Таблица допусков для первичной поверки");
	}
	
	@FXML
	private void periodicRBClick() {
		visibleParamsTable = this.periodicToleranceParamsTable;	
		primaryScrollPane.toBack();
		if (!visibleParamsTable.getCurrentS().equals(currentS)) {
			visibleParamsTable.changeSParametr(currentS);
		}
		tolParamsNameLabel.setText("Таблица допусков для периодической поверки");
	}
				
	@FXML
	private void paramsComboBoxClick() {		
	    paramIndex = this.paramsComboBox.getSelectionModel().getSelectedIndex();
	    if (paramIndex >= 0) {
			currentS = S_Parametr.values()[paramIndex];
			this.nominalsTable.changeSParametr(currentS);
			this.primaryToleranceParamsTable.changeSParametr(currentS);			
			this.periodicToleranceParamsTable.changeSParametr(currentS);	
			
			String sParamHeadText = this.paramsComboBox.getSelectionModel().getSelectedItem();
			
			this.nominalsTable.setHead(1, "Модуль\n" + sParamHeadText);
			this.nominalsTable.setHead(2, "Фаза\n" + sParamHeadText);
			
			this.primaryToleranceParamsTable.setHead(1, "Нижний допуск модуля\n" + sParamHeadText);
			this.primaryToleranceParamsTable.setHead(2, "Верхний допуск модуля\n" + sParamHeadText);
			this.primaryToleranceParamsTable.setHead(3, "Нижний допуск фазы\n" + sParamHeadText);
			this.primaryToleranceParamsTable.setHead(4, "Верхний допуск фазы\n" + sParamHeadText);
			
			this.periodicToleranceParamsTable.setHead(1, "Нижний допуск модуля\n" + sParamHeadText);
			this.periodicToleranceParamsTable.setHead(2, "Верхний допуск модуля\n" + sParamHeadText);
			this.periodicToleranceParamsTable.setHead(3, "Нижний допуск фазы\n" + sParamHeadText);
			this.periodicToleranceParamsTable.setHead(4, "Верхний допуск фазы\n" + sParamHeadText);
	    }
	}
	
	@FXML 
	private void addFreqBtnClick(ActionEvent event) {
		this.nominalsTable.addRow();
		this.primaryToleranceParamsTable.addRow();
		this.periodicToleranceParamsTable.addRow();
	}
	
	@FXML
	private void delFreqBtnClick(ActionEvent event) {
		this.nominalsTable.deleteRow(nominalsTable.getRowCount());
		this.primaryToleranceParamsTable.deleteRow(primaryToleranceParamsTable.getRowCount());
		this.periodicToleranceParamsTable.deleteRow(periodicToleranceParamsTable.getRowCount());
	}
	
	@FXML
	private void freqTablesBtnClick() throws IOException {
		FreqTablesWindow.getFreqTablesWindow(this).show();
	}

	@FXML
	private void cloneS11ToS22BtnClick() {
		String[] upDownArray = new String[] {"DOWN_", "UP_"};
		for (String prefix : upDownArray) {
			for (int i = 0; i < MeasUnitPart.values().length; i++) {
				String key1 = prefix + MeasUnitPart.values()[i] + "_" + S_Parametr.S11;
				String key2 = prefix + MeasUnitPart.values()[i] + "_" + S_Parametr.S22;
				
				ArrayList<String> primaryValuesArray = this.primaryToleranceParamsTable.values.get(key1);
				ArrayList<String> periodicValuesArray = this.periodicToleranceParamsTable.values.get(key1);
				
				this.primaryToleranceParamsTable.values.get(key2).clear();
				this.periodicToleranceParamsTable.values.get(key2).clear();
				
				for (String val : primaryValuesArray) {
					this.primaryToleranceParamsTable.values.get(key2).add(val);				
				}
				for (String val : periodicValuesArray) {
					this.primaryToleranceParamsTable.values.get(key2).add(val);				
				}
			}
		}		
	}
	
//end @FXML methods	
	private void setParams(String paramsIndex, int countOfParams) {
		try {
			String path = new File(".").getAbsolutePath() + "\\files\\" + paramsIndex + ".txt";			
			listOfParams.clear();
			FileManager.LinesToItems(path, countOfParams, listOfParams);
			this.paramsComboBox.setValue(listOfParams.get(this.currentS.ordinal()));
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
		List<String> columnFreqValues = new ArrayList<>();
		for (Double fr : freqTable) {
			columnFreqValues.add(fr.toString());
		}
		
		int currentRowCount = this.primaryToleranceParamsTable.getRowCount();
		if (currentRowCount < freqTable.size()) {
			while (this.primaryToleranceParamsTable.getRowCount() < freqTable.size()) {
				this.nominalsTable.addRow();
				this.primaryToleranceParamsTable.addRow();
				this.periodicToleranceParamsTable.addRow();
			}				
		}
		else if (currentRowCount > freqTable.size()) {
			while (this.primaryToleranceParamsTable.getRowCount() > freqTable.size()) {
				this.nominalsTable.deleteRow(this.nominalsTable.getRowCount());
				this.primaryToleranceParamsTable.deleteRow(this.primaryToleranceParamsTable.getRowCount());
				this.periodicToleranceParamsTable.deleteRow(this.periodicToleranceParamsTable.getRowCount());
			}
		}
		
		this.nominalsTable.setColumn(0, columnFreqValues);
		this.primaryToleranceParamsTable.setColumn(0, columnFreqValues);
		this.periodicToleranceParamsTable.setColumn(0, columnFreqValues);
	}
	
//Methods for getting information about element
	public String getType() {
		return elemTypesComboBox.getSelectionModel().getSelectedItem().toString();
	}
	public String getSerNum() {
		return serNumberTextField.getText();
	}
	public int getPoleCount() {
		if (this.fourPoleRB.isSelected()) return 4;
		else return 2;
	}
	public String getMeasUnit() {
		if (this.vswrRB.isSelected()) return "vswr";
		else return "gamma"; 
	}
	
	public String getModuleToleranceType() {
		if (this.upDownToleranceRB.isSelected()) return "updown";
		else return "percent";
	}
	public String getPhaseToleranceType() {
		if (this.upDownPhaseToleranceRB.isSelected()) return "updown";
		else return "percent";
	}

//Method for getting collections of parametrs	
	public List<Double> getFreqsValues(){
		return this.nominalsTable.getFreqs();
	}
	
	public Map<String, Map<Double, Double>> getNominalValues(){
		Map<String, Map<Double, Double>> nominals = new LinkedHashMap<String, Map<Double, Double>>();
		for (int i = 0; i < currentCountOfParams; i++) {
			String key = MeasUnitPart.MODULE + "_" + S_Parametr.values()[i];
			nominals.put(key, this.nominalsTable.getParametr(key));
			key = MeasUnitPart.PHASE + "_" + S_Parametr.values()[i];
			nominals.put(key, this.nominalsTable.getParametr(key));
		}
		return nominals;
	}
		
	public Map<String, Map<Double, Double>> getToleranceParamsValues(TimeType timeType, MeasUnitPart unit){
		LinkedHashMap<String, Map<Double, Double>> params = new LinkedHashMap<String, Map<Double, Double>>();
		NewElementToleranceParamsTable currentTable = null;
		if (timeType.equals(TimeType.PERIODIC)) {
			currentTable = this.periodicToleranceParamsTable;
		} else if (timeType.equals(TimeType.PRIMARY)) {
			currentTable = this.primaryToleranceParamsTable;
		}
		else {
			return null;
		}
		for (int i = 0; i < currentCountOfParams; i++) {
			String key = "DOWN_" + unit + "_" + S_Parametr.values()[i];
			params.put(key, currentTable.getParametr(key));
			key = "UP_" + unit + "_" + S_Parametr.values()[i];
			params.put(key, currentTable.getParametr(key));
		}
		return params;
	}
	
	//Действия по закрытию окна	
	public void saveValues() {
		this.nominalsTable.changeSParametr(currentS);
		this.periodicToleranceParamsTable.changeSParametr(this.currentS);
		this.primaryToleranceParamsTable.changeSParametr(this.currentS);
	}
	private boolean checkTable(Table table) {
		/*
		table.changeSParametr(this.currentS);
		return table.isFull(this.currentCountOfParams);
		*/
		return true;
	}
	public boolean checkPrimaryTable() {
		return checkTable(this.primaryToleranceParamsTable);
	}
	public boolean checkPeriodicTable() {
		return checkTable(this.periodicToleranceParamsTable);
	}		
	public MeasResult getNominals() {
		if (this.gammaRB.isSelected()) {
			Gamma_Result gr = new Gamma_Result(this, this.currentElement);
			return gr;
		}
		else{
			VSWR_Result vr = new VSWR_Result(this, this.currentElement);
			return vr;
		}
	}
	
	public void setNominalTableEditable(boolean editable) {
		this.nominalsTable.setEditable(editable);
		this.moduleTextField.setDisable(!editable); 
		this.phaseTextField.setDisable(!editable);
		
		this.autoModuleBtn.setDisable(!editable);
		this.autoPhaseBtn.setDisable(!editable);
	}

}