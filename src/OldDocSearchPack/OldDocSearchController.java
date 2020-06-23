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
import java.util.List;

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
	private ListView<String> verificationListView;
	
	@FXML
	private ToggleButton deviceSearchTB;
	
	@FXML
	private DatePicker fromDTP;
	@FXML
	private DatePicker tillDTP;

	private Device checkDevice;
	private ObservableList<String> listOfVerifications;
	List<List<String>> resultOfSearch; 
	
	private String typeOfDoc;
	private Date from;
	private Date till;
	
	@FXML
	private void initialize() {	
		this.checkDevice = null;		
		this.docTypeGroup = new ToggleGroup();
		this.gDocRB.setSelected(true);
		this.gDocRB.setToggleGroup(docTypeGroup);
		this.bDocBtn.setToggleGroup(docTypeGroup);
		this.typeOfDoc = "Certificate";		
		this.listOfVerifications = FXCollections.observableArrayList();
		this.resultOfSearch = new ArrayList<List<String>>();		
		setVerificationItems();
	}
	
	@FXML
	private void deviceSearchTBClick() {
		if(!deviceSearchTB.isSelected()) {
			this.checkDevice = null;
			this.deviceSearchTB.setText("Выбор средства измерения");
		}
		else {
			SearchDeviceWindow.getSearchDeviceWindow(checkDevice, this).show();
		}
	}
	
	@FXML
	private void gDocRBClick() {
		this.typeOfDoc = "Certificate";
	}
	
	@FXML
	private void bDocRBClick() {
		this.typeOfDoc = "Notice";
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
			Desktop.getDesktop().open(docFile);
			Desktop.getDesktop().open(protocolFile);
		}
		catch(IOException ioExp) {
			AboutMessageWindow.createWindow("Ошибка", "Файл отсутствует").show();
		}
	}
	
//InfoRequestable
	@Override
	public void setDevice(Device device) {
		this.checkDevice = device;	
		if (checkDevice == null) {
			this.deviceSearchTB.setSelected(false);
		}
	}
	@Override
	public void representRequestedInfo() {
		String caption = checkDevice.getName() + " " + checkDevice.getType() + " пїЅ" + checkDevice.getSerialNumber();
		this.deviceSearchTB.setText(caption);		
	}
	
	private void setVerificationItems() {
		this.listOfVerifications.clear();
		LocalDate lcFrom = fromDTP.getValue();
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
		Calendar calendarInstance = Calendar.getInstance();
		calendarInstance.setTime(this.till); 
		calendarInstance.add(Calendar.DAY_OF_MONTH, 1);
		this.till = calendarInstance.getTime();
		
		String addFilters = "";
		List<String> fieldsNames = new ArrayList<String>();
				
		fieldsNames.add("verificationDate");
		fieldsNames.add("pathOfDoc");
		fieldsNames.add("pathOfProtocol");				
		if (checkDevice != null) {
			addFilters = (" AND DeviceId='" + checkDevice.getId() + "'");
		}	
		else {
			fieldsNames.add("name");
			fieldsNames.add("type");
			fieldsNames.add("serialNumber");
		}
		String sqlQuery = "SELECT Verifications.verificationDate, Verifications.pathOfDoc, Verifications.pathOfProtocol, Devices.name, Devices.type, Devices.serialNumber";
		sqlQuery += (" FROM Verifications INNER JOIN Devices ON Verifications.DeviceId = Devices.id WHERE Verifications.typeOfDoc='" + typeOfDoc + "'"); 
		try {
			DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldsNames, resultOfSearch);
			
			int next = 0;
			int stop = resultOfSearch.size();
			for (int i = 0; i < stop; i++) {
				//Этот объект будет удален из результатов поиска, если дата поверки не будет между значениями from и till
				List<String> currentResultOfSerach = resultOfSearch.get(next);				
				Date dateOfVer;
				String strDateOfVer = currentResultOfSerach.get(0);
				try {
					dateOfVer = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDateOfVer); 
				}
				catch(ParseException pExp) {
					dateOfVer = Calendar.getInstance().getTime();
				}
				if (dateOfVer.after(this.from) && dateOfVer.before(this.till)) { 
					String item = currentResultOfSerach.get(0) + " ";					
					if (checkDevice != null) {
						item += (checkDevice.getName() + " " + checkDevice.getType() + " " + checkDevice.getSerialNumber());				
					}
					else {				
						item += (currentResultOfSerach.get(3) + " " + currentResultOfSerach.get(4) + " " + currentResultOfSerach.get(5));
					}
					listOfVerifications.add(item);	
					next++;
				}
				else {
					resultOfSearch.remove(currentResultOfSerach);
				}
			}
			this.verificationListView.setItems(listOfVerifications);
		}
		catch(SQLException sqlExp) {
			System.out.println(sqlExp.getMessage() + "\n\n" + sqlExp.getStackTrace());
			AboutMessageWindow.createWindow("Ошибка", "База данных отсутствует или повреждена").show();
		}		
	}	
}