package DBEditForm;

import java.io.IOException;

import DevicePack.Device;
import ErrorParamsPack.ErrorParamsWindow;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFX;
import SearchDevicePack.SearchDeviceWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class DBEditController implements InfoRequestable {
	
	@FXML
	private Button passBtn;	
	@FXML
	private Button errorParamsBtn;	
	@FXML
	private Button searchDeviceBtn;
	@FXML
	private Button deleteDeviceBtn;
	@FXML
	private Button deletResBtn;
	@FXML
	private Button saveDeviceModificationBtn;
	@FXML
	private Button saveResModificationBtn;
	
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField typeTextFiel;
	@FXML
	private TextField serNumTextField;
	@FXML
	private TextField ownerTextField;
	@FXML
	private TextField gosNumTextField;
	
	@FXML
	private ListView elementsListView;
	
	@FXML
	private ComboBox verificationDataComboBox;
	
	@FXML
	private RadioButton resultsRB;
	@FXML
	private RadioButton paramsRB;
	private ToggleGroup indicationGroup;
	
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	private StringGridFX resultTable;
	
	private Device modDevice;
	private int currentElementIndex;
//---------------------------------------------------------
	@FXML
	private void initialize() {
		resultTable = new StringGridFX(7, 10, 800, 100, scrollPane, tablePane);
		
		indicationGroup = new ToggleGroup();		
		this.resultsRB.setToggleGroup(indicationGroup);
		this.paramsRB.setToggleGroup(indicationGroup);
		this.resultsRB.setSelected(true);
	}
//---------------------------------------------------------	
	@FXML
	private void passBtnClick() {
		
	}
	
	@FXML
	private void errorParamsBtnClick() throws IOException {
		ErrorParamsWindow.getErrorParamsWindow().show();
	}
	
	@FXML
	private void searchDeviceBtnClick() {
		try {
			SearchDeviceWindow.getSearchDeviceWindow(modDevice, this).show();
		}
		catch(IOException exp) {
			//
		}
	}
	
	@FXML
	private void deleteDeviceBtnClick() {
		
	}
	
	@FXML
	private void deletResBtnClick() {
		
	}
	
	@FXML
	private void saveDeviceModificationBtnClick() {
		
	}
	
	@FXML
	private void saveResModificationBtn() {
		
	}
	
	@Override
	public void setDevice(Device device) {
		modDevice = device;	
	}

	@Override
	public void representRequestedInfo() {
		this.nameTextField.setText(modDevice.getName());
		this.typeTextFiel.setText(modDevice.getType());
		this.ownerTextField.setText(modDevice.getOwner());
		this.serNumTextField.setText(modDevice.getSerialNumber());
		this.gosNumTextField.setText(modDevice.getGosNumber());		
	}

}
