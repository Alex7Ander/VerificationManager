package StartVerificationPack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import FileManagePack.FileManager;
import VerificationForm.VerificationController;
import VerificationForm.VerificationWindow;
import YesNoDialogPack.YesNoWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class StartVerificationController {

	@FXML
	private TextField  temperatureTextField;
	@FXML
	private TextField atmPreasureTextField;
	@FXML
	private TextField airHumidityTextField;
	
	@FXML
	private RadioButton primaryVerRB;
	@FXML
	private RadioButton periodicVerRB;
	private ToggleGroup verGroup;
	
	@FXML
	private RadioButton goodViewRB;
	@FXML
	private RadioButton badViewRB;
	private ToggleGroup viewGroup;
	
	@FXML
	private RadioButton goodWorkRB;
	@FXML
	private RadioButton badWorkRB;
	private ToggleGroup workGroup;
	
	private double currentTemparature;
	private double currentAtmPreasure;
	private double currentAirHumidity;
	
	private double upTemperature = 24.0;
	private double downTemperature = 22.0;
	
	private double upAtmPreasure = 1800.0;
	private double downAtmPreasure = 450.0;
	
	private double upAirHumidity = 80.0;
	private double downAirHumidity = 50.0;
	
	private String envStatusString;
	
	@FXML
	private Button startBtn;
	
	private StartVerificationWindow myWindow;
	public void setWindow(StartVerificationWindow win) { this.myWindow = win;}
	
	@FXML
	private void initialize() {
		verGroup = new ToggleGroup();
		primaryVerRB.setToggleGroup(verGroup);
		periodicVerRB.setToggleGroup(verGroup);
		primaryVerRB.setSelected(true);
		
		viewGroup = new ToggleGroup();
		goodViewRB.setToggleGroup(viewGroup);
		badViewRB.setToggleGroup(viewGroup);
		goodViewRB.setSelected(true);
				
		workGroup = new ToggleGroup();
		goodWorkRB.setToggleGroup(workGroup);
		badWorkRB.setToggleGroup(workGroup);
		goodWorkRB.setSelected(true);
	}

	@FXML
	private void primaryVerRBClick() {
		//
	}
	
	@FXML
	private void periodicVerRB() {
		//
	}
	
	@FXML
	private void startBtnClick() throws Exception {
		
		if (!chekEnvironment()) {
			YesNoWindow ynWin = new YesNoWindow("��������", envStatusString + "\n������� ����������?");
			int answer = ynWin.showAndWait();
			if (answer == 1) {
				return;
			}
		}
		
		if(!checkVerType()) {
			YesNoWindow ynWin = new YesNoWindow("����� ��� �������?", "����� ����������� ��������� �������?");
			int answer = ynWin.showAndWait();
			if (answer == 0) {
				this.primaryVerRB.setSelected(true);
			}
			else {
				this.periodicVerRB.setSelected(true);
			}
		}
		
		if (!checkView()) {
			//��������� ������� �� ������ �� �������� ��������
			YesNoWindow ynWin = new YesNoWindow("������������� ��������� �������� �������?", "�� �������� ������ ��������\n �� ����������� ��������� �������?");
			int answer = ynWin.showAndWait();
			if (answer == 0) {
				String[] docTypes = {"��������� � �������������"};
				VerificationWindow.getVerificationWindow().getController().createProtocol(docTypes);
				return;
			}
			else {
				this.goodViewRB.setSelected(true);
			}
		}
		
		if (!checkWork()) {
			//��������� ������� �� ������ �� �����������
			YesNoWindow ynWin = new YesNoWindow("������������� ��������� �����������?", "�� �������� ������ ��������\n �� ����������� �����������?");
			int answer = ynWin.showAndWait();
			if (answer == 0) {
				this.badWorkRB.setSelected(true);
				String[] docTypes = {"��������� � �������������"};
				VerificationWindow.getVerificationWindow().getController().createProtocol(docTypes);
				return;
			}
			else {
				this.goodWorkRB.setSelected(true);
			}
		}
		
		if(VerificationWindow.getVerificationWindow() != null) {
			VerificationController vCtrl = (VerificationController)VerificationWindow.getVerificationWindow().getControllerClass();
			vCtrl.StartVerification();
			boolean wait = false;
			try {
				ArrayList<String> array = new ArrayList<String>();
				FileManager.LinesToItems(new File(".").getAbsolutePath() +  "//files//startserver.txt", array);
				if (array.get(0).equals("true")) {
					wait = true;
				}
			} catch (IOException ioExp) {
				ioExp.getStackTrace();
			}
			if (wait) {vCtrl.waitResults();}
			this.myWindow.close();
		}
	}
	
	private boolean chekEnvironment() {
		boolean environmentStatus = true;
		envStatusString = "��������� ���������� ����� ��� �������:\n";		
		try {
			currentTemparature = Double.parseDouble(this.temperatureTextField.getText());
			currentAtmPreasure = Double.parseDouble(this.atmPreasureTextField.getText());
			currentAirHumidity = Double.parseDouble(this.airHumidityTextField.getText());
			
			if (currentTemparature > upTemperature || currentTemparature < downTemperature) {
				environmentStatus = false;
				if (currentTemparature > upTemperature) envStatusString += "����������� ���� �����\n";
				else envStatusString += "����������� ���� �����\n";
			}
			
			if (currentAtmPreasure > upAtmPreasure || currentAtmPreasure < downAtmPreasure) {
				environmentStatus = false;
				if (currentAtmPreasure > upAtmPreasure) envStatusString += "����������� �������� ���� �����\n";
				else envStatusString += "����������� �������� ���� �����\n";
			}
			
			if (currentAirHumidity > upAirHumidity || currentAirHumidity < downAirHumidity) {
				environmentStatus = false;
				if (currentAirHumidity > upAirHumidity) envStatusString += "��������� ���� �����\n";
				else envStatusString += "��������� ���� �����\n";
			}
		}
		catch(NumberFormatException nfExp) {
			environmentStatus = false;
			envStatusString = "��������� ���� �������� ����������\n���������� ����� �� �������� ��������������� �������.";
		}				
		return environmentStatus;
	}
	
	private boolean checkVerType() {
		if(this.primaryVerRB.isSelected() || this.periodicVerRB.isSelected()) return true;
		else return false;
	}
	
	private boolean checkView() {
		if(this.goodViewRB.isSelected()) return true;
		else return false;
	}
	
	private boolean checkWork() {
		if (this.goodWorkRB.isSelected()) return true;
		else return false;
	}
	
	public String getStrTemperatur() {return this.temperatureTextField.getText();}
	public String getStrAirHumidity() {return this.airHumidityTextField.getText();}
	public String getStrAtmPreasure() {return this.atmPreasureTextField.getText();}
	public String getTypeBytime() {
		if (this.primaryVerRB.isSelected()) return "primary";
		else return "periodic";
	}
	
}
