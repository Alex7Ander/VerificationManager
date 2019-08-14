package VerificationForm;

import java.io.IOException;

import DevicePack.Device;
import GUIpack.InfoRequestable;
import SearchDevicePack.SearchDeviceWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class VerificationController implements InfoRequestable {
	@FXML
	private Button searchDeviceBtn;
	@FXML
	private Button startBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button closeBtn;
	@FXML
	private Button createProtocolBtn;
	@FXML
	private Label nameLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private Label serNumLabel;
	
	//Ссылка на поверяемое СИ
	public Device verificatedDevice;
	
	@FXML
	private void searchDeviceBtnClick(ActionEvent event) {
		try {
			//verificatedDevice = new Device();
			SearchDeviceWindow.getSearchDeviceWindow(verificatedDevice, this).show();
		}
		catch(IOException exp) {
			//
		}
	}
	@FXML
	private void startBtnClick(ActionEvent event) {
		//
	}
	@FXML
	private void saveBtnClick(ActionEvent event) {
		//
	}
	@FXML
	private void createProtocolBtnClick(ActionEvent event) {
		//
	}
	@FXML
	private void closeBtnClick(ActionEvent event) {
		try {
			Stage stage = (Stage) closeBtn.getScene().getWindow();
			stage.close();
		}
		catch(Exception exp){
			//
		}
	}

	@Override
	public void representRequestedInfo() {
		this.nameLabel.setText(this.verificatedDevice.getName());
		this.typeLabel.setText(this.verificatedDevice.getType());
		this.serNumLabel.setText(this.verificatedDevice.getSerialNumber());		
	}
	@Override
	public void setDevice(Device device) {
		verificatedDevice = device;
	}
}
