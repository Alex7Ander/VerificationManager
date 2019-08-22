package OldDocSearchPack;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import DataBasePack.DataBaseManager;
import DevicePack.Device;
import GUIpack.InfoRequestable;
import SearchDevicePack.SearchDeviceWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class OldDocSearchController implements InfoRequestable {

	@FXML
	private Button searchBtn;
	@FXML
	private Button openBtn;
	
	@FXML
	private RadioButton gDocRB;
	@FXML
	private RadioButton bDocBtn;
	private ToggleGroup docTypeGroup;
	private String typeOfDoc;
	
	@FXML
	private ListView verificationListView;
	
	@FXML
	private ToggleButton deviceSearchTB;
	
	private Device checkDevice;
	private ObservableList<String> listOfVerifications;
	
	@FXML
	private void initialize() {	
		checkDevice = null;
		
		docTypeGroup = new ToggleGroup();
		gDocRB.setSelected(true);
		gDocRB.setToggleGroup(docTypeGroup);
		bDocBtn.setToggleGroup(docTypeGroup);
		typeOfDoc = "свидетельство о поверке";
		
		listOfVerifications = FXCollections.observableArrayList();
		
		setVerificationItems();
	}
	
	@FXML
	private void deviceSearchTBClick() {
		if(!deviceSearchTB.isSelected()) {
			checkDevice = null;
			deviceSearchTB.setText("Выбор средства измерения");
		}
		else {
			try {
				SearchDeviceWindow.getSearchDeviceWindow(checkDevice, this).show();
			}
			catch(IOException exp) {
				//
			}
		}
	}
	
	@FXML
	private void gDocRBClick() {
		typeOfDoc = "свидетельство о поверке";
	}
	
	@FXML
	private void bDocRBClick() {
		typeOfDoc = "извещение о непригодности";
	}
	
	@FXML
	private void searchBtnClick() {
		setVerificationItems();
	}
	
	@FXML
	private void openBtnClick() {
		//
	}
	
	

//InfoRequestable
	@Override
	public void setDevice(DevicePack.Device device) {
		checkDevice = device;	
		if (checkDevice == null) {
			deviceSearchTB.setSelected(false);
		}
	}
	@Override
	public void representRequestedInfo() {
		String caption = checkDevice.getName() + " " + checkDevice.getType() + " №" + checkDevice.getSerialNumber();
		deviceSearchTB.setText(caption);		
	}
	
	private void setVerificationItems() {
		String addFilters = "";
		ArrayList<String> fieldsNames = new ArrayList<String>();
		ArrayList<ArrayList<String>> arrayResults = new ArrayList<ArrayList<String>>();
		fieldsNames.add("data");
		fieldsNames.add("pathOfDoc");
		fieldsNames.add("pathOfProtocol");
		if (checkDevice != null) {
			addFilters = (" AND deviceName='" + checkDevice.getName() + "' AND deviceType='" + checkDevice.getType() +"' AND deviceSerNum='" + checkDevice.getSerialNumber() + "'");
		}	
		else {
			fieldsNames.add("deviceName");
			fieldsNames.add("deviceType");
			fieldsNames.add("deviceSerNum");
		}
		String sqlQuery = "SELECT (";
		for (int i=0; i<fieldsNames.size(); i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != fieldsNames.size() - 1) { sqlQuery += " ,";}
		}
		sqlQuery += (" FROM Verifications WHERE typeOFDoc='" + typeOfDoc + "'" + addFilters);
		try {
			DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, arrayResults);
			for (int i=0; i<arrayResults.size(); i++) {
				String item = arrayResults.get(i).get(0) + " ";
				if (checkDevice != null) {
					item += (checkDevice.getName() + " " + checkDevice.getType() + " " + checkDevice.getSerialNumber());				
				}
				else {
					item += (arrayResults.get(i).get(3) + " " + arrayResults.get(i).get(4) + " " + arrayResults.get(i).get(5));
				}
				listOfVerifications.add(item);				
			}
			this.verificationListView.setItems(listOfVerifications);
		}
		catch(SQLException sqlExp) {
			//
		}		
	}
	
	
}
