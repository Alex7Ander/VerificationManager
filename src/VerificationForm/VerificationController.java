package VerificationForm;

import java.io.IOException;
import java.util.ArrayList;

import DevicePack.Device;
import GUIpack.InfoRequestable;
import GUIpack.StringGridFX;
import SearchDevicePack.SearchDeviceWindow;
import VerificationPack.Gamma_Result;
import VerificationPack.MeasResult;
import VerificationPack.VSWR_Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
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
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private AnchorPane tablePane;
	private StringGridFX resultTable;
	//Результат поверки
	private ArrayList<MeasResult> verificationResult;	
	//Ссылка на поверяемое СИ
	public Device verificatedDevice;
		
	@FXML
	private void initialize(){
		ArrayList<String> paramTableHeads = new ArrayList<String>();
		resultTable = new StringGridFX(7, 10, 1110, 100, scrollPane, tablePane);		 
	}
	
	@FXML
	private void searchDeviceBtnClick(ActionEvent event) {
		try {
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
		System.out.println("Start representing");
		this.nameLabel.setText(this.verificatedDevice.getName());
		this.typeLabel.setText(this.verificatedDevice.getType());
		this.serNumLabel.setText(this.verificatedDevice.getSerialNumber());
		int countOfElements = this.verificatedDevice.getCountOfElements();	
		try {
			if (countOfElements > 0) {
				for(int i = 0; i < this.verificatedDevice.getCountOfElements(); i++) {
					int countOfParams = this.verificatedDevice.includedElements.get(i).getPoleCount();
					if (countOfParams == 2) { countOfParams = 1;}
					if (this.verificatedDevice.includedElements.get(i).getMeasUnit().equals("VSWR")) {
						//this.verificationResult.add(new VSWR_Result(countOfParams));
					}
					else if (this.verificatedDevice.includedElements.get(i).getMeasUnit().equals("Gamma")) {
						//this.verificationResult.add(new Gamma_Result(countOfParams));
					}
				}
			}
			else {
		
			}
		}
		catch(Exception exp) {
			
		}
		
	}
	
	@Override
	public void setDevice(Device device) {
		verificatedDevice = device;
		System.out.println("In setDevice " + verificatedDevice.getName());
	}
}
