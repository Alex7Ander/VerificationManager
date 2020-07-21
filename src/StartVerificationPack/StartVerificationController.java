package StartVerificationPack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import AboutMessageForm.AboutMessageWindow;
import DBEditForm.DBEditController;
import DBEditForm.DBEditWindow;
import DevicePack.Element;
import FileManagePack.FileManager;
import ToleranceParamPack.ParametrsPack.TimeType;
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
	
	private static String manualParagraph;
	
	static {
		FileInputStream fis;
        Properties property = new Properties();
        String propPath = new File(".").getAbsolutePath() + "/files/aksol.properties";
        try {       	
            fis = new FileInputStream(propPath);
            property.load(fis);
            manualParagraph = property.getProperty("manual.paragraph.lastVerificatonIndex");           
            fis.close();
        } catch (IOException ioExp) {
        	manualParagraph = "";
        }		
	}
	
	
	@FXML
	private Button startBtn;
	
	private StartVerificationWindow myWindow;
	public void setWindow(StartVerificationWindow win) { this.myWindow = win;}
	
	@FXML
	private void initialize() {
		verGroup = new ToggleGroup();
		primaryVerRB.setToggleGroup(verGroup);
		periodicVerRB.setToggleGroup(verGroup);
		//primaryVerRB.setSelected(true);
		
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
	private void startBtnClick() throws IOException {		
		boolean envStatus;
		try {
			envStatus = chekEnvironment(); 
		}
		catch(NumberFormatException nfExp) {
			AboutMessageWindow.createWindow("������", nfExp.getMessage()).show();
			return;
		}
		if (!envStatus) {
			int answer = YesNoWindow.createYesNoWindow("��������", envStatusString + "\n������� ����������?").showAndWait();
			if (answer == 1) {
				return;
			}
		}
		if(!checkVerType()) {
			int answer = YesNoWindow.createYesNoWindow("����� ��� �������?", "����� ����������� ��������� �������?").showAndWait();
			if (answer == 0) {
				this.primaryVerRB.setSelected(true);
			}
			else {
				this.periodicVerRB.setSelected(true);
			}
		}
		if (!checkView()) {
			//��������� ������� �� ������ �� �������� ��������
			int answer = YesNoWindow.createYesNoWindow("������������� ��������� �������� �������?", "�� �������� ������ ��������\n �� ����������� ��������� �������?").showAndWait();
			if (answer == 0) {
				this.myWindow.close();
				VerificationWindow.getVerificationWindow().getController().FinishVerificationBeforeMeasurment();				
				return;
			}
			else {
				this.goodViewRB.setSelected(true);
			}
		}
		if (!checkWork()) {
			//��������� ������� �� ������ �� �����������
			int answer = YesNoWindow.createYesNoWindow("������������� ��������� �����������?", "�� �������� ������ ��������\n �� ����������� �����������?").showAndWait();
			if (answer == 0) {
				this.myWindow.close();
				VerificationWindow.getVerificationWindow().getController().FinishVerificationBeforeMeasurment();
				return;
			}
			else {
				this.goodWorkRB.setSelected(true);
			}
		}

		if(VerificationWindow.getVerificationWindow() != null) {
			VerificationController vCtrl = (VerificationController)VerificationWindow.getVerificationWindow().getControllerClass();
			
			if(this.periodicVerRB.isSelected()) {
				StringBuilder notMarkedElementsList = new StringBuilder();
				boolean hasNotMarkedElements = false;
				for (Element elm : vCtrl.verificatedDevice.includedElements) {
					if(elm.getLastVerificationId() == 0) {	
						notMarkedElementsList.append("- " + elm.getType() + " " + elm.getSerialNumber() + "\n");
						hasNotMarkedElements = true;
					}
				}
				if(hasNotMarkedElements) {
					DBEditController dbEditController = (DBEditController)DBEditWindow.getDBEditWindow().getController();
					dbEditController.setDevice(vCtrl.verificatedDevice);
					dbEditController.representRequestedInfo();
					DBEditWindow.getDBEditWindow().show();
					VerificationWindow.getVerificationWindow().close();
					VerificationWindow.getVerificationWindow().delete();
					String msg = "��� �������, ���������� ��� �������\n�� ������� ����������, ������� ������\n�������������� ��� ��������� � ��������\n����������� ���������� �������:\n��. ����� ����������� ��������� � " + manualParagraph;
					AboutMessageWindow.createWindow("������", msg).show();
					this.myWindow.close();
					return;
				}
			}
			
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
			if (wait) {
				vCtrl.waitResults();
			}
			this.myWindow.close();
		}
	}
	
	private boolean chekEnvironment() throws NumberFormatException {
		//replacing ',' to '.' in text fields
		this.temperatureTextField.setText(this.temperatureTextField.getText().replace(',', '.'));
		this.atmPreasureTextField.setText(this.atmPreasureTextField.getText().replace(',', '.'));
		this.airHumidityTextField.setText(this.airHumidityTextField.getText().replace(',', '.'));
		
		this.envStatusString = "��������� ���������� ����� ��� �������:\n";	
		try {
			this.currentTemparature = Double.parseDouble(this.temperatureTextField.getText());
			this.currentAtmPreasure = Double.parseDouble(this.atmPreasureTextField.getText());
			this.currentAirHumidity = Double.parseDouble(this.airHumidityTextField.getText());
			
			if (this.currentTemparature > this.upTemperature || this.currentTemparature < this.downTemperature) {
				if (this.currentTemparature > this.upTemperature) this.envStatusString += "����������� ���� �����\n";
				else this.envStatusString += "����������� ���� �����\n";
				return false;
			}
			if (this.currentAtmPreasure > this.upAtmPreasure || this.currentAtmPreasure < this.downAtmPreasure) {
				if (this.currentAtmPreasure > this.upAtmPreasure) this.envStatusString += "����������� �������� ���� �����\n";
				else this.envStatusString += "����������� �������� ���� �����\n";
				return false;
			}
			if (this.currentAirHumidity > this.upAirHumidity || this.currentAirHumidity < this.downAirHumidity) {
				if (this.currentAirHumidity > this.upAirHumidity) this.envStatusString += "��������� ���� �����\n";
				else this.envStatusString += "��������� ���� �����\n";
				return false;
			}
		}
		catch(NumberFormatException nfExp) {
			throw new NumberFormatException("��������� ���� �������� ����������\n���������� ����� �� �������� �������.");
			//this.envStatusString = "��������� ���� �������� ����������\n���������� ����� �� �������� ��������������� �������.";
			//return false;
		}
		return true;
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
	public TimeType getVerificationiTimeType() {
		if (this.primaryVerRB.isSelected())
			return TimeType.PRIMARY;
		else
			return TimeType.PERIODIC;
	}
	
}
