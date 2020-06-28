package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import AboutMessageForm.AboutMessageWindow;
import DBEditForm.DBEditWindow;
import NewDevicePack.NewDeviceWindow;
import OldDocSearchPack.OldDocSearchWindow;
import VerificationForm.*;

public class MainController {
	@FXML
	private Button exitBtn;
	
	@FXML
	private Button aboutBtn;
	
	@FXML
	private Button verificationBtn;
	
	@FXML
	private Button dbEditBtn;
	
	@FXML
	private Button newDeviceBtn;
	
	@FXML
	private Button searchOldDocBtn;
	
	@FXML
	private Button imcButton;
	
	@FXML
	private TextArea helpTextArea;
			
	@FXML
	private ImageView logoView;
	
//Verification button
	@FXML
	public void mouseOnVerificationBtn(MouseEvent event) {
		helpTextArea.setText("Режим проведения поверки зарегистрированных приборов.");
		try {
			verificationBtn.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void mouseOffVerificationBtn() {
		helpTextArea.setText("");
		try {
			verificationBtn.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}	
	@FXML
	public void verificationBtnClick(ActionEvent event) {
		try {
			VerificationWindow vW = VerificationWindow.getVerificationWindow();
			vW.show();
		}
		catch(Exception exp) {
			helpTextArea.setText("Error: " + exp.getMessage());
		}
	}
	
//DB Edit button
	@FXML
	public void mouseOnDBEditBtn(MouseEvent event) {
		helpTextArea.setText("Работа с базой данных измерительной установки: просмотр результатов измерений,\n"
				+ "редактирование критериев годности.");
		try {
			dbEditBtn.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void mouseOffDBEditBtn() {
		helpTextArea.setText("");
		try {
			dbEditBtn.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void dbEditBtnClick(ActionEvent event) {
		try {
			DBEditWindow dbEditWin = DBEditWindow.getDBEditWindow();
			dbEditWin.show();
		}
		catch(Exception exp) {
			helpTextArea.setText("Error: " + exp.getMessage());
		}
	}
	
//New Deivce button
	@FXML
	public void mouseOnNewDeviceBtn(MouseEvent event) {
		helpTextArea.setText("Регистрирование нового прибора.");
		try {
			newDeviceBtn.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void mouseOffNewDeviceBtn() {
		helpTextArea.setText("");
		try {
			newDeviceBtn.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void newDeviceBtnClick(ActionEvent event) {
		try {
			NewDeviceWindow newDevWindow = NewDeviceWindow.getNewDeviceWindow();
			newDevWindow.show();
		}
		catch(Exception exp) {
			helpTextArea.setText("Error: " + exp.getMessage());
		}
	}
	
//Old documents search button
	@FXML
	public void mouseOnOldDocSearchBtn(MouseEvent event) {
		helpTextArea.setText("Поиск документов (свдетельств/извещений) оформленных при проведенных поверках.");
		try {
			searchOldDocBtn.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void mouseOffOldDocSearchBtn() {
		helpTextArea.setText("");
		try {
			searchOldDocBtn.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void searchOldDocBtnClick(ActionEvent event) {
		try {
			OldDocSearchWindow oldDocWindow = OldDocSearchWindow.getOldDocSearchWindow();
			oldDocWindow.show();
		}
		catch (Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}

// IMC button
	@FXML
	private void mouseOnIMCBtn() {
		helpTextArea.setText("Проведение независимой метрологической аттестации");
		try {
			imcButton.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	private void mouseOffIMCBtn() {
		helpTextArea.setText("");
		try {
			imcButton.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	private void IMCBtnClick() {
		String absPath = new File(".").getAbsolutePath();
		String path = absPath;
		try(FileReader reader =  new FileReader(absPath + "\\files\\imcexe.txt")){
			int c;
			while((c = reader.read()) != -1) {
				path += (char)c;
			}
		}
		catch (IOException ioExp) {
			System.out.println("Отсутствует файл imcexe.txt");
			path += "\\NMA\\Project1.exe";
		}		
		//Start program of independent meteorology certification
		File imcExe = new File(path);
		try {
			Desktop.getDesktop().open(imcExe);
		}
		catch (IOException ioExp) {
			System.out.println("Отсутствует файл " + path);
		}		
	}
	
//About button
	@FXML
	public void mouseOnAboutBtn(MouseEvent event) {
		helpTextArea.setText("Информация о версии программы.");
		try {
			aboutBtn.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void mouseOffAboutBtn() {
		helpTextArea.setText("");
		try {
			aboutBtn.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML 
	public void aboutBtnClick(ActionEvent event) {
		helpTextArea.setText("Версия ПО 1.0.1");
		AboutMessageWindow.createWindow("О программе", "Версия ПО: 1.0.1").show();
	}
	
//Exit button
	@FXML
	public void mouseOnExitBtn(MouseEvent event) {
		helpTextArea.setText("Закрыть программу");
		try {
			exitBtn.setOpacity(1.0);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}	
	@FXML
	public void mouseOffExitBtn() {
		helpTextArea.setText("");
		try {
			exitBtn.setOpacity(0.8);
		}
		catch(Exception exp) {
			helpTextArea.setText(exp.getMessage());
		}
	}
	@FXML
	public void exitBtnClick(ActionEvent event) {
		System.exit(0);
	}

}
