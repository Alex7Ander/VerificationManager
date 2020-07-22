  package OldDocSearchPack;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import GUIpack.InfoRequestable;
import SearchDevicePack.SearchDeviceWindow;
import SecurityPack.FileEncrypterDecrypter;
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
	/*
	 * 
	 * КАК ТОЛЬКО БУДЕТ ВОЗМОЖНОСЬ, НЕОБХОДИМО ПЕРЕПИСАТ МЕТОД setVerificationItems()
	 * Необходимо убрать запрос к БД непосредственно из тела этого метода
	 * Поиск процедур поверки делать через статический метод VerificationProcedure.getAllVerificationsProcedures()
	 * А для поиска протоколов конкретного устройства написать метод VerificationProcedure.getAllVerificationsProceduresForDevice(Device device) 
	 * 
	 * */
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
	private ObservableList<String> listOfVerificationsItems;
	
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
		this.typeOfDoc = "Cвидетельство о поверке";		
		this.listOfVerificationsItems = FXCollections.observableArrayList();
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
		this.typeOfDoc = "Cвидетельство о поверке";
	}
	
	@FXML
	private void bDocRBClick() {
		this.typeOfDoc = "Извещение о непригодности";
	}
	
	@FXML
	private void searchBtnClick() {		
		setVerificationItems();
	}
	
	@FXML
	private void openBtnClick()  {
		int index = this.verificationListView.getSelectionModel().getSelectedIndex();
		try {
			String pathOfDoc = new File(".").getAbsolutePath() + resultOfSearch.get(index).get(1);
			String pathOfProtocol = new File(".").getAbsolutePath() + resultOfSearch.get(index).get(2);
			byte[] decodedKey = Base64.getDecoder().decode(resultOfSearch.get(index).get(3));
			SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
			
			FileEncrypterDecrypter fileEncrypterDecrypter = null;
			try {			
				fileEncrypterDecrypter = new FileEncrypterDecrypter(originalKey);	//, "AES/CBC/PKCS5Padding"		
			} catch (NoSuchAlgorithmException | NoSuchPaddingException mExp) {
				mExp.printStackTrace();
			}	
			byte[] protBuffer = null;
			byte[] docBuffer = null;
			
			int ind1 = pathOfProtocol.lastIndexOf('\\');
			int ind2 = pathOfDoc.lastIndexOf('\\');
			String nameOfNewProtocolFile = pathOfProtocol.substring(ind1, pathOfProtocol.lastIndexOf('.'));
	        String pathToNewProtocolFile = new File(".").getAbsolutePath() + nameOfNewProtocolFile + ".xls";
	        
	        String nameOfNewDocFile = pathOfDoc.substring(ind2, pathOfDoc.lastIndexOf('.'));
	        String pathToNewDocFile = new File(".").getAbsolutePath() + nameOfNewDocFile + ".docx";
	        
	        FileOutputStream protfout = new FileOutputStream(pathToNewProtocolFile);
	        FileOutputStream docfout = new FileOutputStream(pathToNewDocFile);
	        
			try {
				docBuffer = fileEncrypterDecrypter.decrypt(pathOfDoc);
				docfout.write(docBuffer);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} 
			docfout.close();
			Desktop.getDesktop().open(new File(pathToNewDocFile));
			
			if(pathOfProtocol.contains("null")) {
				protfout.close();
				AboutMessageWindow.createWindow("Протокол отсутвует", "Протокол для данной поверки не создавался.").show();
				return;
			}
			try {
				protBuffer = fileEncrypterDecrypter.decrypt(pathOfProtocol);
				protfout.write(protBuffer);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			protfout.close();
			Desktop.getDesktop().open(new File(pathToNewProtocolFile));
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
		String caption = checkDevice.getName() + " " + checkDevice.getType() + " №" + checkDevice.getSerialNumber();
		this.deviceSearchTB.setText(caption);		
	}
	
	private void setVerificationItems() {	
		this.listOfVerificationsItems.clear();
		
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
		fieldsNames.add("secretKeyString");
		if (checkDevice != null) {
			addFilters = (" AND DeviceId='" + checkDevice.getId() + "'");
		}	
		else {
			fieldsNames.add("name");
			fieldsNames.add("type");
			fieldsNames.add("serialNumber");
		}
		String sqlQuery = "SELECT Verifications.verificationDate, Verifications.pathOfDoc, Verifications.pathOfProtocol, Verifications.secretKeyString, Devices.name, Devices.type, Devices.serialNumber";
		sqlQuery += (" FROM Verifications INNER JOIN Devices ON Verifications.DeviceId = Devices.id WHERE Verifications.typeOfDoc='" + typeOfDoc + "'" + addFilters); 
		try {
			resultOfSearch.clear();
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
						item += (currentResultOfSerach.get(4) + " " + currentResultOfSerach.get(5) + " " + currentResultOfSerach.get(6));
					}
					listOfVerificationsItems.add(item);	
					next++;
				}
				else {
					resultOfSearch.remove(currentResultOfSerach);
				}
			}
			this.verificationListView.setItems(listOfVerificationsItems);
		}
		catch(SQLException sqlExp) {
			System.out.println(sqlExp.getMessage() + "\n\n" + sqlExp.getStackTrace());
			AboutMessageWindow.createWindow("Ошибка", "База данных отсутствует или повреждена").show();
		}	
		
	}	
}