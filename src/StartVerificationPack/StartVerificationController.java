package StartVerificationPack;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import AboutMessageForm.AboutMessageWindow;
import ProtocolCreatePack.ProtocolCreateWindow;
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
	
	private String currentVerificationType;
	private double currentTemparature;
	private double currentAtmPreasure;
	private double currentAirHumidity;
	
	private double upTemperature = 25.0;
	private double downTemperature = 20.0;
	
	private double upAtmPreasure = 800.0;
	private double downAtmPreasure = 600.0;
	
	private double upAirHumidity = 95.0;
	private double downAirHumidity = 35.0;
	
	private String envStatusString;
	
	@FXML
	private Button startBtn;
	
	@FXML
	private void initialize() {
		verGroup = new ToggleGroup();
		primaryVerRB.setToggleGroup(verGroup);
		periodicVerRB.setToggleGroup(verGroup);
		
		viewGroup = new ToggleGroup();
		goodViewRB.setToggleGroup(viewGroup);
		badViewRB.setToggleGroup(viewGroup);
				
		workGroup = new ToggleGroup();
		goodWorkRB.setToggleGroup(workGroup);
		badWorkRB.setToggleGroup(workGroup);
	}

	@FXML
	private void primaryVerRBClick() {
		currentVerificationType = "primary";
	}
	
	@FXML
	private void periodicVerRB() {
		currentVerificationType = "periodic";
	}
	
	@FXML
	private void startBtnClick() throws IOException {
		
		if (!chekEnvironment()) {
			AboutMessageWindow msgWin = new AboutMessageWindow("��������", envStatusString);
			msgWin.show();
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
				this.badViewRB.setSelected(true);
				String[] docTypes = {"��������� � �������������"};
				ProtocolCreateWindow.getProtocolCreateWindow(docTypes, null).show();
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
				ProtocolCreateWindow.getProtocolCreateWindow(docTypes, null).show();
				return;
			}
			else {
				this.goodWorkRB.setSelected(true);
			}
		}
		
		//������ ��������� measurment
		String absPath = new File(".").getAbsolutePath();
		File file =new File(absPath + "\\measurment\\Project1.exe");
		Desktop.getDesktop().open(file);
		
		//
		ServerSocket servS = new ServerSocket(5055);
		Socket s = null;
		while(true) {
			s = servS.accept();
			InputStream inS = s.getInputStream();
			byte[] buffer = new byte[1024];
			String str = new String(buffer);
			//int b = inS.read(buffer);
			if (true) {
				System.out.println("��������� ��������");
				//fileReadBtnClick();
				break;
			}
		}
		s.close();		
		
		/*
		Thread waitThr = new Thread(()->{
			try {
					
			}
			catch(UnknownHostException uhExp) {
				System.out.println("UnknownHostException " + uhExp.getMessage());
			}
			catch(IOException ioExp) {
				System.out.println("IOException " + ioExp.getMessage());
			}
		}, "waitThread");
		
		waitThr.start();
		//waitThr.join();
		*/
	}
	
	private boolean chekEnvironment() {
		boolean environmetnStatus = true;
		envStatusString = "��������� ���������� ����� ��� �������:\n";
		
		try {
			currentTemparature = Double.parseDouble(this.temperatureTextField.getText());
			currentAtmPreasure = Double.parseDouble(this.atmPreasureTextField.getText());
			currentAirHumidity = Double.parseDouble(this.airHumidityTextField.getText());
			
			if (currentTemparature > upTemperature || currentTemparature < downTemperature) {
				environmetnStatus = false;
				if (currentTemparature > upTemperature) envStatusString += "����������� ���� �����\n";
				else envStatusString += "����������� ���� �����\n";
			}
			
			if (currentAtmPreasure > upAtmPreasure || currentAtmPreasure < downAtmPreasure) {
				environmetnStatus = false;
				if (currentAtmPreasure > upAtmPreasure) envStatusString += "����������� �������� ���� �����\n";
				else envStatusString += "����������� �������� ���� �����\n";
			}
			
			if (currentAirHumidity > upAirHumidity || currentAirHumidity < downAirHumidity) {
				environmetnStatus = false;
				if (currentAirHumidity > upAirHumidity) envStatusString += "��������� ���� �����\n";
				else envStatusString += "��������� ���� �����\n";
			}
		}
		catch(NumberFormatException nfExp) {
			environmetnStatus = false;
			envStatusString = "��������� ���� �������� ����������\n���������� ����� �� �������� ��������������� �������.";
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
	
}
