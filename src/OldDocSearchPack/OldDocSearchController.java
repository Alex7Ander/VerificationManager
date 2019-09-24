  package OldDocSearchPack;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import GUIpack.InfoRequestable;
import SearchDevicePack.SearchDeviceWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
		
	@FXML
	private ListView verificationListView;
	
	@FXML
	private ToggleButton deviceSearchTB;
	
	@FXML
	private DatePicker fromDTP;
	@FXML
	private DatePicker tillDTP;

	private Device checkDevice;
	private ObservableList<String> listOfVerifications;
	ArrayList<ArrayList<String>> resultOfSearch; 
	
	private String typeOfDoc;
	private Date from;
	private Date till;
	
	@FXML
	private void initialize() {	
		checkDevice = null;
		
		docTypeGroup = new ToggleGroup();
		gDocRB.setSelected(true);
		gDocRB.setToggleGroup(docTypeGroup);
		bDocBtn.setToggleGroup(docTypeGroup);
		typeOfDoc = "Certificate";
		
		listOfVerifications = FXCollections.observableArrayList();
		resultOfSearch = new ArrayList<ArrayList<String>>();
		
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
		 typeOfDoc = "Certificate";
	}
	
	@FXML
	private void bDocRBClick() {
		typeOfDoc = "Notice";
	}
	
	@FXML
	private void searchBtnClick() {		
		setVerificationItems();
	}
	
	@FXML
	private void openBtnClick()  {
		try {
			int index = this.verificationListView.getSelectionModel().getSelectedIndex();
			String pathOfDoc = new File(".").getAbsolutePath() + resultOfSearch.get(index).get(1);
			String pathOfProtocol = new File(".").getAbsolutePath() + resultOfSearch.get(index).get(2);
			
			File docFile = new File(pathOfDoc);
			File protocolFile = new File(pathOfProtocol);
			//Desktop.getDesktop().open(docFile);
			Desktop.getDesktop().open(protocolFile);
		}
		catch(IOException ioExp) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Файл протокола не найден");
				msgWin.show();
			}
			catch(IOException exp) {
				System.out.println("Ошибка создания информациооного окна для сообщения об отсутствии файла");
			}
		}
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
		listOfVerifications.clear();
		
		LocalDate lcFrom = this.fromDTP.getValue();
		if (lcFrom != null) {
			this.from = Date.from(lcFrom.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		else {
			this.from = new Date(1);
		}
		
		LocalDate lcTill = this.tillDTP.getValue();
		if (lcTill != null) { 
			this.till = Date.from(lcTill.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		else {
			this.till = new Date();
		}

		String addFilters = "";
		ArrayList<String> fieldsNames = new ArrayList<String>();
		
		
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
		String sqlQuery = "SELECT ";
		for (int i=0; i<fieldsNames.size(); i++) {
			sqlQuery += fieldsNames.get(i);
			if (i != fieldsNames.size() - 1) { sqlQuery += ", ";}
		}
		sqlQuery += (" FROM Verifications WHERE typeOFDoc='" + typeOfDoc + "'" + addFilters);
		try {
			DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, resultOfSearch);
			for (int i=0; i<resultOfSearch.size(); i++) {
				
				Date dateOfVer;
				String strDateOfVer = resultOfSearch.get(i).get(0);
				try {
					dateOfVer = new SimpleDateFormat("DD/MM/yyyy HH:mm:ss").parse(strDateOfVer);
				}
				catch(ParseException pExp) {
					dateOfVer = Calendar.getInstance().getTime();
				}
				
				if (dateOfVer.after(this.from) && dateOfVer.before(this.till)) {
					String item = resultOfSearch.get(i).get(0) + " ";
					if (checkDevice != null) {
						item += (checkDevice.getName() + " " + checkDevice.getType() + " " + checkDevice.getSerialNumber());				
					}
					else {
						item += (resultOfSearch.get(i).get(3) + " " + resultOfSearch.get(i).get(4) + " " + resultOfSearch.get(i).get(5));
					}
					listOfVerifications.add(item);	
				}
			}
			this.verificationListView.setItems(listOfVerifications);
		}
		catch(SQLException sqlExp) {
			try {
				AboutMessageWindow msgWin = new AboutMessageWindow("Ошибка", "Ошибка доступа к БД");
				msgWin.show();
			}
			catch(IOException ioExp) {
				System.out.println("Ошибка создания информациооного окна для сообщения");
			}
		}		
	}
	
	
}
