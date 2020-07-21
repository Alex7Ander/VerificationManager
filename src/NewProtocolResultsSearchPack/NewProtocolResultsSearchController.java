package NewProtocolResultsSearchPack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import DevicePack.Element;
import ProtocolCreatePack.ProtocolCreateWindow;
import ToleranceParamPack.ParametrsPack.ToleranceParametrs;
import VerificationPack.MeasResult;
import VerificationPack.VerificationProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class NewProtocolResultsSearchController {

	private Device protocoledDevice;
	private List<VerificationProcedure> protocoledDeviceProcedures;
	private VerificationProcedure nominalsVerificationProcedure;
	private VerificationProcedure protocoledVerificationProcedure;
	
	@FXML
	private Label deviceLabel;
	@FXML
	private Label nominalsResultCheckedLabel;
	@FXML
	private Label protocoledResultCheckLabel;
	
	@FXML
	private RadioButton primaryRB;
	@FXML
	private RadioButton periodicRB;
	private ToggleGroup workGroup;
	
	@FXML
	private ListView<String> nominalsResultListView;
	@FXML
	private ListView<String> protocoledResultListView;
	
	private ObservableList<String> verificationsItemsList = FXCollections.observableArrayList();
	
	@FXML
	private Button createBtn;
	
	@FXML
	private void initialize() {
		workGroup = new ToggleGroup();
		primaryRB.setToggleGroup(workGroup);
		periodicRB.setToggleGroup(workGroup);
	}
	
	@FXML
	private void createBtnClick() {	
		if(nominalsVerificationProcedure == null || protocoledVerificationProcedure == null) {
			AboutMessageWindow.createWindow("Ошибка", "Вы не выбрали процедуры поверок\nдля выписки протокола").show();
			return;
		}
		
		protocoledVerificationProcedure.setShouldBeSavedInDBStatus(false);
		
		if(!primaryRB.isSelected() && !periodicRB.isSelected()) {
			AboutMessageWindow.createWindow("Ошибка", "Выберите тип поверки").show();
			return;
		}
		
		String[] docTypes = {"Cвидетельство о поверке", "Извещение о непригодности"};
		
		if(primaryRB.isSelected()) {
			protocoledVerificationProcedure.setPrimary();
		}
		else {
			protocoledVerificationProcedure.setPeriodic();
		}
		
		List<MeasResult> results = protocoledVerificationProcedure.getMeasResults();
		List<MeasResult> nominals = null;
		if(protocoledVerificationProcedure.isPrimary()) {
			nominals = new ArrayList<>();
			for (Element elm : this.protocoledDevice.includedElements) {
				nominals.add(elm.getNominal());
			}
		}
		else {
			nominals = nominalsVerificationProcedure.getMeasResults();
		}
		
		List<ToleranceParametrs> protocoledModuleToleranceParams = new ArrayList<>();
		List<ToleranceParametrs> protocoledPhaseToleranceParams = new ArrayList<>();

		for(Element elm : this.protocoledDevice.includedElements) {
			if(protocoledVerificationProcedure.isPrimary()) {
				protocoledModuleToleranceParams.add(elm.getPrimaryModuleToleranceParams());
				protocoledPhaseToleranceParams.add(elm.getPrimaryPhaseToleranceParams());
			}
			else {
				protocoledModuleToleranceParams.add(elm.getPeriodicModuleToleranceParams());
				protocoledPhaseToleranceParams.add(elm.getPeriodicPhaseToleranceParams());
			}
		}
		
		for(int i = 0; i<results.size(); i++) {
			protocoledModuleToleranceParams.get(i).checkResult(nominals.get(i), results.get(i));
			protocoledPhaseToleranceParams.get(i).checkResult(nominals.get(i), results.get(i));
		}
		
		try {
			ProtocolCreateWindow.getProtocolCreateWindow(protocoledDevice, docTypes, results, nominals, protocoledModuleToleranceParams, protocoledPhaseToleranceParams, protocoledVerificationProcedure).show();
		} catch (IOException ioExp) {
			System.err.println("Ошибка при открытии окна для создания протокола. " + ioExp.getMessage());
			ioExp.printStackTrace();
		}
	}
	
	@FXML
	private void protocoledResultListViewClick() {
		int index = protocoledResultListView.getSelectionModel().getSelectedIndex();
		protocoledVerificationProcedure = protocoledDeviceProcedures.get(index);
				
		String labelText = "Поверка от " + protocoledVerificationProcedure.getDate();
		protocoledResultCheckLabel.setText(labelText);
	}
	
	@FXML
	private void nominalsResultListViewClick() {
		int index = nominalsResultListView.getSelectionModel().getSelectedIndex();
		nominalsVerificationProcedure = protocoledDeviceProcedures.get(index);
				
		String labelText = "Поверка от " + nominalsVerificationProcedure.getDate();
		nominalsResultCheckedLabel.setText(labelText);
	}
		
	public void setDevice(Device device) {
		this.protocoledDevice = device;		
		
		String labelText = protocoledDevice.getName() + " " + protocoledDevice.getType() + " №" + protocoledDevice.getSerialNumber(); 
		deviceLabel.setText(labelText);
		
		try {
			protocoledDeviceProcedures = VerificationProcedure.getVerificationsProceduresWithMeasResults(protocoledDevice);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(VerificationProcedure procedure : this.protocoledDeviceProcedures) {
			String item = "Поверка (" + procedure.getDate() + ")";
			verificationsItemsList.add(item);
		}		
		nominalsResultListView.setItems(verificationsItemsList);
		protocoledResultListView.setItems(verificationsItemsList);
	}
	
}