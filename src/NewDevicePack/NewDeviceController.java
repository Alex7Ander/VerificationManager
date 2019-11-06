package NewDevicePack;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import AboutMessageForm.AboutMessageWindow;
import AddNewDeviceNameForm.AddNewDeviceNameWindow;
import DataBasePack.DataBaseManager;
import DevicePack.Device;
import DevicePack.Element;
import Exceptions.SavingException;
import FileManagePack.FileManager;
import NewElementPack.NewElementController;
import NewElementPack.NewElementWindow;
import YesNoDialogPack.YesNoWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class NewDeviceController  {

	@FXML
	private Button createElmentsBtn;
	@FXML
	private Button saveBtn;
	@FXML
	private Button addNameBtn;
	
	@FXML
	private ComboBox<String>  namesComboBox;
	@FXML
	private TextField typeTextField;
	@FXML
	private TextField serialNumberTextField;
	@FXML
	private TextField ownerTextField;
	@FXML
	private TextField gosNumberTextField;
	@FXML
	private TextField countOfElementsTextField;
	
	@FXML
	private VBox elementsButtonBox;
	
	//������������ ������
	private Device newDevice;
	//��������� �������� ������� �������
	//private ArrayList<Element> elements;
	
	public ArrayList<NewElementWindow> elementsWindow;
	public ArrayList<Button> elementsButton;
	
	private ObservableList<String> listOfNames;
	int countOfElements;

	@FXML
	private void initialize(){
		listOfNames = FXCollections.observableArrayList();
		elementsButton = new ArrayList<Button>();
		elementsWindow = new ArrayList<NewElementWindow>();
		//elements = new ArrayList<Element>();
		setItemsOfNames();
	}
	
	@FXML
	public void createElementsBtnClick(ActionEvent event) {
		for (int i=0; i<elementsButton.size(); i++) {
			elementsButtonBox.getChildren().remove(elementsButton.get(i));
		}
		elementsButton.clear();
		elementsWindow.clear();
		try {			
			countOfElements = Integer.parseInt(countOfElementsTextField.getText());
			for (int i = 0; i < countOfElements; i++) {								
				final int index = i;
				String item = Integer.toString(i+1);		
				//C������ ����, ������ ����� ��������� ���������� ��� ��������
				NewElementWindow elementWin = new NewElementWindow();
				elementWin.setTitle(item);
				elementsWindow.add(elementWin);	
				
				Button btn = new Button(item);
				btn.setPrefWidth(600);
				btn.setPrefHeight(30);				
				btn.setOnAction(new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent event) {
							elementsWindow.get(index).show();
						}
				});
				elementsButton.add(btn);
			}
			elementsButtonBox.getChildren().addAll(elementsButton);
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
		}
	}	
	
	@FXML
	public void saveBtnClick(ActionEvent event) throws IOException {		
		String tName = null;
		try{
			tName = this.namesComboBox.getValue().toString();		
		}
		catch(NullPointerException npExp) {
			AboutMessageWindow errorMessage = new AboutMessageWindow("������", "�� ������� ������������ ���� ������������ ��!");
			errorMessage.show();
			return;
		}
		
		String tType = this.typeTextField.getText();
		String tSerN = this.serialNumberTextField.getText();
		String tOwner = this.ownerTextField.getText();
		String tGosN = this.gosNumberTextField.getText();
			
		//���� ���������� ������ ��� ���������� ���� �����, �� ��������� ���������
		if (tName.length()==0 || tType.length()==0 || tSerN.length()==0){
			AboutMessageWindow errorMessage = new AboutMessageWindow("������", "��������� �� ��� ��������� ����");
			errorMessage.show();
			return;
		}
		if (tOwner.length() == 0) tOwner = "-"; //�� ���������� ������ ��������� ������� ���������
		if (tGosN.length() == 0) tGosN = "-";
			
		//������� ����� ����������
		newDevice = new Device(tName, tType, tSerN, tOwner, tGosN);			
		//��������, �� ���������� �� ����������� � ��
		try {
			if (newDevice.isExist()) {
				AboutMessageWindow errorMessage = new AboutMessageWindow("������", "������ ������� ���� � ����������� �������� �������\n"
						+ "����������������� � ���� ������!");
				errorMessage.show();
				return;
			}
		} catch (SQLException sqlExp) {
			AboutMessageWindow errorMessage = new AboutMessageWindow("������", "���� ������ ����������� ��� ����������");
			errorMessage.show();
			return;
		}
		
		//Create elements
		//int notInitializedElementsCount = 0;
		for (int i = 0; i < this.elementsWindow.size(); i++) {			
			NewElementController ctrl = (NewElementController)this.elementsWindow.get(i).getControllerClass();
			Element elm = new Element(ctrl); //ctrl.getElement();
			newDevice.addElement(elm);	
		}
		//� �������, ������� ��������� ��� � ��
		try{
			DataBaseManager.getDB().BeginTransaction();
			newDevice.saveInDB();
			DataBaseManager.getDB().Commit();
			AboutMessageWindow sucsessMessage = new AboutMessageWindow("�������", "�������� ����������");
			sucsessMessage.show();
			Stage stage = (Stage) saveBtn.getScene().getWindow();
			stage.close();
			NewDeviceWindow.deleteNewDeviceWindow();
		}
		catch(SQLException sqlExp){
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow errorMessage = new AboutMessageWindow("������", "������: " + sqlExp.getMessage());
			errorMessage.show();
		}
		catch(SavingException noExp) {
			DataBaseManager.getDB().RollBack();
			AboutMessageWindow errorMessage = new AboutMessageWindow("������", "������: " + noExp.getMessage());
			errorMessage.show();
		}
	}
	
	@FXML
	public void addNameBtnClick(ActionEvent event) {		
		try {
			AddNewDeviceNameWindow.getNewDeviceWindow(this).show();
		}
		catch(IOException ioExp) {
			//
		}		
	}
	
	public void setItemsOfNames() {
		try {
			String absPath = new File(".").getAbsolutePath();
			FileManager.LinesToItems(absPath + "\\files\\sitypes.txt", listOfNames);
		}
		catch(Exception exp) {
			System.out.println("Error: "+ exp.getMessage());
			listOfNames.clear();
			listOfNames.add("������� ������ �����");
			listOfNames.add("����� �������� �����������");
			listOfNames.add("�������� ����������� �������������");
			listOfNames.add("�������� ����������");
			listOfNames.add("������������� � ���������� ��������� ���");
			listOfNames.add("�������� ����������� �� ���������");
		}
		this.namesComboBox.setItems(listOfNames);
	}

}