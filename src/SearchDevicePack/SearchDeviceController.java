package SearchDevicePack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import AboutMessageForm.AboutMessageWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import FileManagePack.FileManager;
import GUIpack.InfoRequestable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SearchDeviceController {
	
	@FXML
	private Button chooseBtn;
	@FXML
	private ComboBox<String> nameComboBox;
	@FXML
	private ComboBox<String> typeComboBox;
	@FXML
	private TextField serialNumTextField;
	@FXML
	private TextField ownerTextField;
	@FXML
	private TextField gosNumTextField;	
	@FXML
	private ListView<String> devicesListView;
	@FXML
	private Label headLabel;
	
	ArrayList<ArrayList<String>> listOfDevicesInfo;
	private String filterName;
	private String filterType;
	private String filterSerNum;
	private String filterOwner;
	private String filterGosNum;
	
	private Device foundedDevice;
	public void setDevice(Device incomingDevice) {
		foundedDevice = incomingDevice;
	}
	
	private InfoRequestable myRequester;
	public void setRequester(InfoRequestable incomingRequester) {
		myRequester = incomingRequester;
	}
	
	@FXML
	private void initialize(){
		listOfDevicesInfo = new ArrayList<ArrayList<String>>();
		filterName = "";
		filterType = "";
		filterSerNum = "";
		filterOwner = "";
		filterGosNum = "";
		setDeviceItems();
		setNamesItems();
		setTypesItems();
	}
	
	@FXML
	private void nameComboBoxChange(ActionEvent event) {
		this.filterName = nameComboBox.getValue().toString();
		setDeviceItems();
	}	
	@FXML
	private void typeComboBoxChange(ActionEvent event) {
		this.filterType = typeComboBox.getValue().toString(); 
		setDeviceItems();
	}
	@FXML
	private void serialNumTextFieldEdit(ObservableValue<String> observable, String oldValue, String newValue){
		this.filterSerNum = serialNumTextField.getText();
		setDeviceItems();
	}
	@FXML
	private void ownerTextFieldEdit(ObservableValue<String> observable, String oldValue, String newValue) {
		this.filterOwner = ownerTextField.getText();
		setDeviceItems();
	}
	@FXML
	private void gosNumTextFieldEdit(ObservableValue<String> observable, String oldValue, String newValue) {
		this.filterGosNum = gosNumTextField.getText();
		setDeviceItems();
	}
	
	@FXML
	private void chooseBtnClick() {		
		int index =  this.devicesListView.getSelectionModel().getSelectedIndex();
		String cName = listOfDevicesInfo.get(index).get(0);
		String cType = listOfDevicesInfo.get(index).get(1);
		String cSerNum = listOfDevicesInfo.get(index).get(2);
		String text = cName + " " + cType + " " + cSerNum;	
		this.headLabel.setText(text);
		try {		
			foundedDevice = new Device(cName, cType, cSerNum);
			this.myRequester.setDevice(foundedDevice);
			this.myRequester.representRequestedInfo();									
		}
		catch(SQLException exp){			
			AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД.\nОбъект не может быть инициализирован").show();
		}
		Stage stage = (Stage) chooseBtn.getScene().getWindow();
	    stage.close();
	}
		
	private void setDeviceItems(){
		String sqlQuery = "SELECT NameOfDevice, TypeOfDevice,  SerialNumber, Owner FROM Devices" + addFilters();// WHERE "+filterString+"";
		ArrayList<String> fieldName = new ArrayList<>();
		fieldName.add("NameOfDevice");
		fieldName.add("TypeOfDevice");
		fieldName.add("SerialNumber");
		fieldName.add("Owner");
		ObservableList<String> items = FXCollections.observableArrayList();
		try {
			DataBaseManager.getDB().sqlQueryString(sqlQuery, fieldName, listOfDevicesInfo);
			System.out.println(listOfDevicesInfo.size());
			for (int i=0; i<listOfDevicesInfo.size(); i++) {
				String item = listOfDevicesInfo.get(i).get(0) + " " + listOfDevicesInfo.get(i).get(1) + " №" + listOfDevicesInfo.get(i).get(2) + ", принадлежащий: " + listOfDevicesInfo.get(i).get(3);				
				items.add(item);
			}			
			devicesListView.setItems(items);			
		}
		catch(SQLException sqlExp) {
			AboutMessageWindow.createWindow("Ошибка", "Ошибка доступа к БД. \nНевозможно инициализировать список устройств.").show();
		}
		catch(NullPointerException npExp) {
			System.out.println("Ошибка: " + npExp.getMessage());
			AboutMessageWindow.createWindow("Непредвиденная ошибка", "Место ошибки: \nБлок catch(Exception exp) \n Метод setDeviceItems() \nКласса SearchDeviceCintroller").show();
		}
	}
	
	private void setNamesItems(){
		ObservableList<String> listOfNames = FXCollections.observableArrayList();
		try {
			String absPath = new File(".").getAbsolutePath();
			FileManager.LinesToItems(absPath + "\\files\\sitypes.txt", listOfNames);
			listOfNames.add("");
		}
		catch(Exception exp) {
			System.out.println("Error: "+ exp.getMessage());
			listOfNames.clear();
			listOfNames.add("Рабочий эталон ККПиО");
			listOfNames.add("Набор нагрузок волноводных");
			listOfNames.add("Нагрузки волноводные согласованные");
			listOfNames.add("Комплект поверочный");
			listOfNames.add("Калибровочный и поверочный комплекты мер");
			listOfNames.add("Нагрузки волноводные КЗ подвижные");
			listOfNames.add("");
		}
		this.nameComboBox.setItems(listOfNames);
	}
	
	private void setTypesItems(){
		String sqlQuery = "SELECT DISTINCT TypeOfDevice FROM Devices";
		ObservableList<String> listOfTypes = FXCollections.observableArrayList();
		try {
			DataBaseManager.getDB().sqlQueryString(sqlQuery, "TypeOfDevice", listOfTypes);
			listOfTypes.add("");
			typeComboBox.setItems(listOfTypes);			
		}
		catch(SQLException sqlExp) {
			//Ошибка доступа к БД
		}
	}
	
	private String addFilters() {
		String filterString = " ";
		if (this.filterType.length()!=0 || this.filterName.length()!=0 || this.filterSerNum.length()!=0 || 
				this.filterOwner.length()!=0 || this.filterGosNum.length()!=0) {
			filterString += " WHERE";
			if (this.filterType.length()!=0) filterString += " TypeOfDevice LIKE '%" + filterType + "%' AND";
			if (this.filterName.length()!=0) filterString += " NameOfDevice LIKE '%" + filterName + "%' AND";
			if (this.filterSerNum.length()!=0) filterString += " SerialNumber LIKE '%" + filterSerNum + "%' AND";
			if (this.filterOwner.length()!=0) filterString += " Owner LIKE '%" + filterOwner + "%' AND";
			if (this.filterGosNum.length()!=0) filterString += " GosNumber LIKE '%" + filterGosNum + "%' AND";
			int lastAnd = filterString.lastIndexOf("AND");
			filterString = filterString.substring(0, lastAnd);
			filterString.trim();
		}
		return filterString;
	}
/*	
    private javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
        	SearchDeviceWindow.deleteWindow();
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler(){
    	return closeEventHandler;
    }
*/		
}
