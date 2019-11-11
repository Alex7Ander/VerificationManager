package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

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
	private TextArea helpTextArea;
				
//Verification button
	@FXML
	public void mouseOnVerificationBtn(MouseEvent event) {
		helpTextArea.setText("����� ���������� ������� ������������������ ��������.");
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
		helpTextArea.setText("�������� � �������������� ���������� ������������������ ��������.");
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
		helpTextArea.setText("��������������� ������ �������.");
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
		helpTextArea.setText("����� ���������� (�����������/���������) ����������� ��� ����������� ��������.");
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
	
//About button
	@FXML
	public void mouseOnAboutBtn(MouseEvent event) {
		helpTextArea.setText("���������� � ������ ���������.");
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
		helpTextArea.setText("������ �� 0.0.1");
		try {
			AboutMessageWindow versionWindow = new AboutMessageWindow("� ���������", "������ ��: 0.0.1");
			versionWindow.show();
		}
		catch(Exception exp) {
			System.out.println(exp.getStackTrace());
			helpTextArea.setText(exp.getMessage());
		}
	}
	
//Exit button
	@FXML
	public void mouseOnExitBtn(MouseEvent event) {
		helpTextArea.setText("��� ������������");
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
