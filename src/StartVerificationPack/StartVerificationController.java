package StartVerificationPack;


import java.io.IOException;

import DevicePack.DeviceException;
import ProtocolCreatePack.ProtocolCreateWindow;
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
			YesNoWindow ynWin = new YesNoWindow("Внимание", envStatusString + "\nЖелаете продолжить?");
			int answer = ynWin.showAndWait();
			if (answer == 1) {
				return;
			}
		}
		
		if(!checkVerType()) {
			YesNoWindow ynWin = new YesNoWindow("Какой тип поверки?", "Будет проводиться первичная поверка?");
			int answer = ynWin.showAndWait();
			if (answer == 0) {
				this.primaryVerRB.setSelected(true);
			}
			else {
				this.periodicVerRB.setSelected(true);
			}
		}
		
		if (!checkView()) {
			//Признание прибора не годным по внешнему остмотру
			YesNoWindow ynWin = new YesNoWindow("Отрицательный результат внешнего осмотра?", "Вы признали прибор негодным\n по результатам внешнеого осмотра?");
			int answer = ynWin.showAndWait();
			if (answer == 0) {
				String[] docTypes = {"Извещение о непригодности"};
				VerificationWindow.getVerificationWindow().getController().createProtocol(docTypes);
				return;
			}
			else {
				this.goodViewRB.setSelected(true);
			}
		}
		
		if (!checkWork()) {
			//Признание прибора не годным по опробованию
			YesNoWindow ynWin = new YesNoWindow("Отрицательный результат опробования?", "Вы признали прибор негодным\n по результатам опробования?");
			int answer = ynWin.showAndWait();
			if (answer == 0) {
				this.badWorkRB.setSelected(true);
				String[] docTypes = {"Извещение о непригодности"};
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
			this.myWindow.close();
		}
	}
	
	private boolean chekEnvironment() {
		boolean environmetnStatus = true;
		envStatusString = "Параметры окружающей среды вне допуска:\n";
		
		try {
			currentTemparature = Double.parseDouble(this.temperatureTextField.getText());
			currentAtmPreasure = Double.parseDouble(this.atmPreasureTextField.getText());
			currentAirHumidity = Double.parseDouble(this.airHumidityTextField.getText());
			
			if (currentTemparature > upTemperature || currentTemparature < downTemperature) {
				environmetnStatus = false;
				if (currentTemparature > upTemperature) envStatusString += "температура выше нормы\n";
				else envStatusString += "температура ниже нормы\n";
			}
			
			if (currentAtmPreasure > upAtmPreasure || currentAtmPreasure < downAtmPreasure) {
				environmetnStatus = false;
				if (currentAtmPreasure > upAtmPreasure) envStatusString += "атмосферное давление выше нормы\n";
				else envStatusString += "атмосферное давление ниже нормы\n";
			}
			
			if (currentAirHumidity > upAirHumidity || currentAirHumidity < downAirHumidity) {
				environmetnStatus = false;
				if (currentAirHumidity > upAirHumidity) envStatusString += "влажность выше нормы\n";
				else envStatusString += "влажность ниже нормы\n";
			}
		}
		catch(NumberFormatException nfExp) {
			environmetnStatus = false;
			envStatusString = "Введенные Вами значения параметров\nокружающей среды не являются действительными числами.";
		}				
		return environmetnStatus;
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
