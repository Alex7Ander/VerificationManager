package NewElementPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import DevicePack.Element;
import FileManagePack.FileManager;
import FreqTablesPack.FreqTablesWindow;
import GUIpack.StringGridFX;
import ToleranceParamPack.PercentTolerance;
import ToleranceParamPack.ToleranceParametrs;
import ToleranceParamPack.UpDownTolerance;
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

public class NewElementController {

	@FXML
	private Button addFreqBtn;
	@FXML
	private Button delFreqBtn;
	@FXML
	private Button freqTablesBtn;
	@FXML
	private Button cloneS11ToS22Btn;
	
//�������� ��� �������������� �������
	//������
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
	//��������� ����
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
	
	@FXML
	private Label moduleLabel;
	@FXML
	private Label phaseLable;
	
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
	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	
	private StringGridFX paramsTable;
	
	private ToggleGroup poleCountGroup;
	private ToggleGroup measUnitGroup;
	private ToggleGroup toleranceTypeGroup;
	private ToggleGroup verificationTypeGroup;
	private ToggleGroup phaseTolearnceTypeGroup;
//************************************************	
	//�������������� �������	
	private Element currentElement;
	public void setElement(Element Elm) { 
		this.currentElement = Elm;
		if (this.currentElement != null) {
			showElementInfo();
		}
	}
	//����� ���������������� �������
	public void initializeElement() {
		this.currentElement = new Element(this);
	}
	public Element getElement() {return this.currentElement;}
//************************************************	
		
	ObservableList<String> listOfParams;
	private int currentCountOfParams;
	private String currentTypeOfParams;
	//private int lastIndex;
	
	//��������� � ���������� ����������
	int timeIndex;
	int paramIndex;
	int savingIndex;
	String[] keys = {"primary_S11", "primary_S12", "primary_S21", "primary_S22", 
					 "periodic_S11", "periodic_S12", "periodic_S21", "periodic_S22"};
	private ArrayList<Double> freqs;
	
	private ArrayList<String> strFreqs;
	private HashMap<String, ArrayList<String>> m_s;
	private HashMap<String, ArrayList<String>> p_s;	
	private HashMap<String, ArrayList<String>> d_m_s; 	//������ ������ ������
	private HashMap<String, ArrayList<String>> u_m_s;	//������� ������ ������	
	private HashMap<String, ArrayList<String>> d_p_s;	//������ ������ ����
	private HashMap<String, ArrayList<String>> u_p_s;	//������� ������ ����
/*		
	private NewElementWindow myWindow;	
	public void setWindow(NewElementWindow window) {
		myWindow = window;
	}
*/	
	@FXML
	private void initialize() {			
		//������� �������
		int initialfreqCount = 10;
		ArrayList<String> paramTableHeads = new ArrayList<String>();
		paramTableHeads.add("�������, ���");
		paramTableHeads.add("������ ������");
		paramTableHeads.add("�������");
		paramTableHeads.add("������� ������");
		paramTableHeads.add("������ ������");
		paramTableHeads.add("�������");
		paramTableHeads.add("������� ������");
		paramsTable = new StringGridFX(7, initialfreqCount, 850, 100, scrollPane, tablePane, paramTableHeads);
		
		timeIndex = 0;
		paramIndex = 0;
		savingIndex = 0;
		
		//������� � ���� double
		freqs = new ArrayList<Double>();
		//������� � ���� ����� ��� ���������� ������
		strFreqs = new ArrayList<String>();
		m_s = new HashMap<String, ArrayList<String>>();
		p_s = new HashMap<String, ArrayList<String>>();
		d_m_s = new HashMap<String, ArrayList<String>>();		
		u_m_s = new HashMap<String, ArrayList<String>>();
		d_p_s = new HashMap<String, ArrayList<String>>();		
		u_p_s = new HashMap<String, ArrayList<String>>();
		
		for (int i = 0; i < keys.length; i++) {
			d_m_s.put(keys[i], new ArrayList<String>(10));
			m_s.put(keys[i], new ArrayList<String>(10));
			u_m_s.put(keys[i], new ArrayList<String>(10));
			
			d_p_s.put(keys[i], new ArrayList<String>(10));
			p_s.put(keys[i], new ArrayList<String>(10));
			u_p_s.put(keys[i], new ArrayList<String>(10));
		}
		
		listOfParams = FXCollections.observableArrayList();
		twoPoleTypesList = FXCollections.observableArrayList();
		fourPoleTypesList = FXCollections.observableArrayList();
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//twoPoleTypes.txt", twoPoleTypesList);
		} catch (Exception exp) {
			twoPoleTypesList.add("������������");			
		}
		try {
			FileManager.LinesToItems(new File(".").getAbsolutePath() + "//files//fourPoleTypes.txt", fourPoleTypesList);
		} catch (Exception exp) {
			fourPoleTypesList.add("���������������");
		}	
		this.elemTypesComboBox.setItems(twoPoleTypesList);
		elemTypesComboBox.setValue(twoPoleTypesList.get(0));
		
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
		
		phaseTolearnceTypeGroup = new ToggleGroup();
		this.upDownPhaseToleranceRB.setSelected(true);
		this.upDownPhaseToleranceRB.setToggleGroup(phaseTolearnceTypeGroup);
		this.percentPhaseToleranceRB.setToggleGroup(phaseTolearnceTypeGroup);
		
		currentCountOfParams = 1;
		currentTypeOfParams = "vswr";		
		setParams(currentTypeOfParams, currentCountOfParams);
		this.paramsComboBox.setItems(listOfParams);
		this.paramsComboBox.setValue(listOfParams.get(0));		
	}
	
	@FXML
	private void autoDownModuleBtnClick() {
		int stop = this.paramsTable.getRowCount();
		String text = this.downModuleTextField.getText();
		for (int i=0; i<stop; i++) {
			this.paramsTable.setCellValue(1, i, text);
		}
	}
	@FXML
	private void autoModuleBtnClick() {
		int stop = this.paramsTable.getRowCount();
		String text = this.moduleTextField.getText();
		for (int i=0; i<stop; i++) {
			this.paramsTable.setCellValue(2, i, text);
		}
	}
	@FXML
	private void autoUpModuleBtnClick() {
		int stop = this.paramsTable.getRowCount();
		String text = this.upModuleTextField.getText();
		for (int i=0; i<stop; i++) {
			this.paramsTable.setCellValue(3, i, text);
		}
	}
	@FXML
	private void autoDownPhaseBtnClick() {
		int stop = this.paramsTable.getRowCount();
		String text = this.downPhaseTextField.getText();
		for (int i=0; i<stop; i++) {
			this.paramsTable.setCellValue(4, i, text);
		}
	}
	@FXML
	private void autoPhaseBtnClick() {
		int stop = this.paramsTable.getRowCount();
		String text = this.phaseTextField.getText();
		for (int i=0; i<stop; i++) {
			this.paramsTable.setCellValue(5, i, text);
		}
	}
	@FXML
	private void autoUpPhaseBtnClick() {
		int stop = this.paramsTable.getRowCount();
		String text = this.upPhaseTextField.getText();
		for (int i=0; i<stop; i++) {
			this.paramsTable.setCellValue(6, i, text);
		}
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
		elemTypesComboBox.setValue(twoPoleTypesList.get(0));
	}
	
	@FXML 
	private void fourPoleRBClick(ActionEvent event) {
		currentCountOfParams = 4;
		setParams(currentTypeOfParams, currentCountOfParams);
		paramsComboBox.setItems(listOfParams);
		paramsComboBox.setValue(listOfParams.get(0));
		elemTypesComboBox.setItems(fourPoleTypesList);
		elemTypesComboBox.setValue(fourPoleTypesList.get(0));
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
		String item = paramsComboBox.getSelectionModel().getSelectedItem().toString();
		this.moduleLabel.setText(item);
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
	private void freqTablesBtnClick() throws IOException {
		FreqTablesWindow.getFreqTablesWindow(this).show();
	}

	@FXML
	private void cloneS11ToS22BtnClick() {	
		try {
			//������ ��� ���� ���� � ���� ������, ��� �� �� ���������� � ��� �� �����������
			ArrayList<HashMap<String, ArrayList<String>>> hashMaps = new ArrayList<HashMap<String, ArrayList<String>>>();
			hashMaps.add(m_s);
			hashMaps.add(p_s);
			hashMaps.add(d_m_s);
			hashMaps.add(u_m_s);
			hashMaps.add(d_p_s);
			hashMaps.add(u_p_s);
			for (HashMap<String, ArrayList<String>> hashMap : hashMaps) {
				hashMap.remove("primary_S22");
				ArrayList<String> cloneHMPrim = hashMap.get("primary_S11");
				hashMap.put("primary_S22", cloneHMPrim);
			
				hashMap.remove("periodic_S22");
				ArrayList<String> cloneHMPeriod = hashMap.get("periodic_S11");
				hashMap.put("periodic_S22", cloneHMPeriod);
			}
		}
		catch(Exception exp) {
			//
		}
	}
//end @FXML methods
	
	private void setParams(String paramsIndex, int countOfParams) {
		try {
			String path = new File(".").getAbsolutePath();
			if (paramsIndex.equals("vswr")) path += "\\files\\vswr.txt";
			else if (paramsIndex.equals("gamma")) path += "\\files\\gamma.txt";
			listOfParams.clear();
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
			this.paramsTable.getColumn(0, strFreqs);		
			this.paramsTable.getColumn(1, d_m_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(2, m_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(3, u_m_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(4, d_p_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(5, p_s.get(keys[savingIndex]));
			this.paramsTable.getColumn(6, u_p_s.get(keys[savingIndex]));
		
			this.paramsTable.clear();
		
			this.paramsTable.setColumn(0, strFreqs);
			this.paramsTable.setColumn(1, d_m_s.get(keys[showIndex]));
			this.paramsTable.setColumn(2, m_s.get(keys[showIndex]));
			this.paramsTable.setColumn(3, u_m_s.get(keys[showIndex]));
			this.paramsTable.setColumn(4, d_p_s.get(keys[showIndex]));
			this.paramsTable.setColumn(5, p_s.get(keys[showIndex]));
			this.paramsTable.setColumn(6, u_p_s.get(keys[showIndex]));
		}
	}
//������ ��� ��������� ���������� �� ��������
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
	
	public int checkInputedValues() {
		int returnedvalue = 0;		
		ArrayList<HashMap<String, ArrayList<String>>> hashMaps = new ArrayList<HashMap<String, ArrayList<String>>>();
		hashMaps.add(d_m_s);
		hashMaps.add(m_s);
		hashMaps.add(u_m_s);
		hashMaps.add(d_p_s);
		hashMaps.add(p_s);
		hashMaps.add(u_p_s);		
		for (int i=0; i<hashMaps.size(); i++) {			
			for (String key : keys) {
				for (int j=0; j<this.strFreqs.size(); j++) {
					String cValue = hashMaps.get(i).get(key).get(j);
					if (cValue.length()==0) {
						returnedvalue = -1;
						break;
					}
				}
			}
		}		
		return returnedvalue;
	}
	
//�������� �� �������� ����		
	public void remeberTables() {
		try {
    		int showIndex = paramIndex + timeIndex;		
    		refreshTable(savingIndex, showIndex);
    		savingIndex = showIndex;   		
    		freqs.clear();
    		for (int i=0; i<paramsTable.getRowCount(); i++) {
    			try {
    				double val = Double.parseDouble(paramsTable.getCellValue(0, i));
    				freqs.add(val);
    			}
    			catch(NumberFormatException nfExp) {
    				freqs.add(0.0);
    			}
    		}
    	}
    	catch(Exception exp) {
    		//
    	}
	}
	public boolean checkfreqTable() {
		boolean result = true;
		for (int i = 0; i < this.strFreqs.size(); i++) {		
			try {
				double value = Double.parseDouble(this.strFreqs.get(i));
				if (value <=0) {
					result = false;
					break;
				}
			} catch(Exception exp) {
				result = false;
				break;
			}			
		}	
		return result;
	}
	public int checkInfo() {
		int result = 0;
		ArrayList<HashMap<String, ArrayList<String>>> maps = new ArrayList<HashMap<String, ArrayList<String>>>();
		maps.add(m_s);
		maps.add(p_s);
		maps.add(d_m_s);
		maps.add(u_m_s);
		maps.add(d_p_s);
		maps.add(u_p_s);
		
		String[] currentKeys;
		if (this.twoPoleRB.isSelected()) {
			currentKeys = new String[2];
			currentKeys[0] = this.keys[0];
			currentKeys[1] = this.keys[4];
		}
		else {
			currentKeys = this.keys;
		}
		
		for (int cMap = 0; cMap < maps.size(); cMap++) { //��� ������ ���� �� ����������
			for (String key: currentKeys) {				 //��� ������� S ��������� � ��� ���������, ��� � ������������� �������
				for (int i = 0; i < maps.get(cMap).get(key).size(); i++) {
					String strValue = maps.get(cMap).get(key).get(i);
					try {
						@SuppressWarnings("unused")
						double value = Double.parseDouble(strValue);
					} catch(NumberFormatException nfExp) {
						result++;
					}
				}
			}
		}
		return result;		
	}
//--------------------------------------------------------------------------------------------------	
	private void showElementInfo() {
		this.elemTypesComboBox.setValue(this.currentElement.getType());
		this.serNumberTextField.setText(this.currentElement.getSerialNumber());
		
		if (this.currentElement.getPoleCount() == 4) {
			this.fourPoleRB.setSelected(true);
			this.fourPoleRBClick(null);
		}
		else {
			this.twoPoleRB.setSelected(true);
			this.twoPoleRBClick(null);
		}
		
		this.paramsTable.clear();	
		for (String key : keys) {
			m_s.get(key).clear();
		}

		Double fr[] = new Double[this.currentElement.getNominal().freqs.size()];
		this.currentElement.getNominal().freqs.toArray(fr);
		for (Double freq : fr) {
			this.freqs.add(freq);
			strFreqs.add(freq.toString());
			m_s.get("primary_S11").add(this.currentElement.getNominal().values.get("m_S11").get(freq).toString());
			m_s.get("periodic_S11").add(this.currentElement.getNominal().values.get("m_S11").get(freq).toString());
			p_s.get("primary_S11").add(this.currentElement.getNominal().values.get("p_S11").get(freq).toString());
			p_s.get("periodic_S11").add(this.currentElement.getNominal().values.get("p_S11").get(freq).toString());
			
			d_m_s.get("primary_S11").add(this.currentElement.getPrimaryToleranceParams().values.get("d_m_S11").get(freq).toString());
			d_m_s.get("periodic_S11").add(this.currentElement.getPeriodicToleranceParams().values.get("d_m_S11").get(freq).toString());
			u_m_s.get("primary_S11").add(this.currentElement.getPrimaryToleranceParams().values.get("u_m_S11").get(freq).toString());
			u_m_s.get("periodic_S11").add(this.currentElement.getPeriodicToleranceParams().values.get("u_m_S11").get(freq).toString());
			
			d_p_s.get("primary_S11").add(this.currentElement.getPrimaryToleranceParams().values.get("d_p_S11").get(freq).toString());
			d_p_s.get("periodic_S11").add(this.currentElement.getPeriodicToleranceParams().values.get("d_p_S11").get(freq).toString());
			u_p_s.get("primary_S11").add(this.currentElement.getPrimaryToleranceParams().values.get("u_p_S11").get(freq).toString());
			u_p_s.get("periodic_S11").add(this.currentElement.getPeriodicToleranceParams().values.get("u_p_S11").get(freq).toString());
			
			if (this.currentElement.getPoleCount() == 4) {
				m_s.get("primary_S12").add(this.currentElement.getNominal().values.get("m_S12").get(freq).toString());
				m_s.get("periodic_S12").add(this.currentElement.getNominal().values.get("m_S12").get(freq).toString());
				p_s.get("primary_S12").add(this.currentElement.getNominal().values.get("p_S12").get(freq).toString());
				p_s.get("periodic_S12").add(this.currentElement.getNominal().values.get("p_S12").get(freq).toString());
				
				d_m_s.get("primary_S12").add(this.currentElement.getPrimaryToleranceParams().values.get("d_m_S12").get(freq).toString());
				d_m_s.get("periodic_S12").add(this.currentElement.getPeriodicToleranceParams().values.get("d_m_S12").get(freq).toString());
				u_m_s.get("primary_S12").add(this.currentElement.getPrimaryToleranceParams().values.get("u_m_S12").get(freq).toString());
				u_m_s.get("periodic_S12").add(this.currentElement.getPeriodicToleranceParams().values.get("u_m_S12").get(freq).toString());
				
				d_p_s.get("primary_S12").add(this.currentElement.getPrimaryToleranceParams().values.get("d_p_S12").get(freq).toString());
				d_p_s.get("periodic_S12").add(this.currentElement.getPeriodicToleranceParams().values.get("d_p_S12").get(freq).toString());
				u_p_s.get("primary_S12").add(this.currentElement.getPrimaryToleranceParams().values.get("u_p_S12").get(freq).toString());
				u_p_s.get("periodic_S12").add(this.currentElement.getPeriodicToleranceParams().values.get("u_p_S12").get(freq).toString());
				//--------------------------------------------------------------------------------------------------------------------------
				m_s.get("primary_S21").add(this.currentElement.getNominal().values.get("m_S21").get(freq).toString());
				m_s.get("periodic_S21").add(this.currentElement.getNominal().values.get("m_S21").get(freq).toString());
				p_s.get("primary_S21").add(this.currentElement.getNominal().values.get("p_S21").get(freq).toString());
				p_s.get("periodic_S21").add(this.currentElement.getNominal().values.get("p_S21").get(freq).toString());
				
				d_m_s.get("primary_S21").add(this.currentElement.getPrimaryToleranceParams().values.get("d_m_S21").get(freq).toString());
				d_m_s.get("periodic_S21").add(this.currentElement.getPeriodicToleranceParams().values.get("d_m_S21").get(freq).toString());
				u_m_s.get("primary_S21").add(this.currentElement.getPrimaryToleranceParams().values.get("u_m_S21").get(freq).toString());
				u_m_s.get("periodic_S21").add(this.currentElement.getPeriodicToleranceParams().values.get("u_m_S21").get(freq).toString());
				
				d_p_s.get("primary_S21").add(this.currentElement.getPrimaryToleranceParams().values.get("d_p_S21").get(freq).toString());
				d_p_s.get("periodic_S21").add(this.currentElement.getPeriodicToleranceParams().values.get("d_p_S21").get(freq).toString());
				u_p_s.get("primary_S21").add(this.currentElement.getPrimaryToleranceParams().values.get("u_p_S21").get(freq).toString());
				u_p_s.get("periodic_S21").add(this.currentElement.getPeriodicToleranceParams().values.get("u_p_S21").get(freq).toString());
				//--------------------------------------------------------------------------------------------------------------------------
				m_s.get("primary_S22").add(this.currentElement.getNominal().values.get("m_S22").get(freq).toString());
				m_s.get("periodic_S22").add(this.currentElement.getNominal().values.get("m_S22").get(freq).toString());
				p_s.get("primary_S22").add(this.currentElement.getNominal().values.get("p_S22").get(freq).toString());
				p_s.get("periodic_S22").add(this.currentElement.getNominal().values.get("p_S22").get(freq).toString());
				
				d_m_s.get("primary_S22").add(this.currentElement.getPrimaryToleranceParams().values.get("d_m_S22").get(freq).toString());
				d_m_s.get("periodic_S22").add(this.currentElement.getPeriodicToleranceParams().values.get("d_m_S22").get(freq).toString());
				u_m_s.get("primary_S22").add(this.currentElement.getPrimaryToleranceParams().values.get("u_m_S22").get(freq).toString());
				u_m_s.get("periodic_S22").add(this.currentElement.getPeriodicToleranceParams().values.get("u_m_S22").get(freq).toString());
				
				d_p_s.get("primary_S22").add(this.currentElement.getPrimaryToleranceParams().values.get("d_p_S22").get(freq).toString());
				d_p_s.get("periodic_S22").add(this.currentElement.getPeriodicToleranceParams().values.get("d_p_S22").get(freq).toString());
				u_p_s.get("primary_S22").add(this.currentElement.getPrimaryToleranceParams().values.get("u_p_S22").get(freq).toString());
				u_p_s.get("periodic_S22").add(this.currentElement.getPeriodicToleranceParams().values.get("u_p_S22").get(freq).toString());
			}
			int freqCount = this.currentElement.getNominal().freqs.size();
			if (freqCount > this.paramsTable.getRowCount()) {
				while(freqCount != this.paramsTable.getRowCount()) 
					this.paramsTable.addRow();
			}
			else if (freqCount < this.paramsTable.getRowCount()) {
				while(freqCount != this.paramsTable.getRowCount()) 
					this.paramsTable.deleteRow(this.paramsTable.getRowCount());
			}
			this.paramsTable.setColumn(0, strFreqs);			
			this.paramsTable.setColumn(1, d_m_s.get(keys[this.savingIndex]));
			this.paramsTable.setColumn(2, m_s.get(keys[this.savingIndex]));
			this.paramsTable.setColumn(3, u_m_s.get(keys[this.savingIndex]));
			this.paramsTable.setColumn(4, d_p_s.get(keys[this.savingIndex]));
			this.paramsTable.setColumn(5, p_s.get(keys[this.savingIndex]));
			this.paramsTable.setColumn(6, u_p_s.get(keys[this.savingIndex]));
		}
	}
	
	public ToleranceParametrs getPrimaryTP() {
		if (this.percentToleranceRB.isSelected()) {
			PercentTolerance ptp = new PercentTolerance("primary", this, this.currentElement);
			return ptp;
		}
		else {
			UpDownTolerance udt = new UpDownTolerance("primary", this, this.currentElement);
			return udt;
		}
	}
	
	public ToleranceParametrs getPeriodicTP() {
		if (this.percentToleranceRB.isSelected()) {
			PercentTolerance ptp = new PercentTolerance("periodic", this, this.currentElement);
			return ptp;
		}
		else {
			UpDownTolerance udt = new UpDownTolerance("periodic", this, this.currentElement);
			return udt;
		}
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
	
}