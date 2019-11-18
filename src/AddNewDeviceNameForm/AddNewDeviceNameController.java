package AddNewDeviceNameForm;

import java.io.File;
import java.io.IOException;
import AboutMessageForm.AboutMessageWindow;
import FileManagePack.FileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AddNewDeviceNameController {

    @FXML
    private Button addBtn;
    @FXML
    private Button delBtn;
    @FXML
    private Button saveBtn;

    @FXML
    private ListView<String> namesListView;

    @FXML
    private TextField newNameTextField;

    private ObservableList<String> listOfNames;

    @FXML
    private void initialize(){
        listOfNames = FXCollections.observableArrayList();
        try {
            String absPath = new File(".").getAbsolutePath();
            FileManager.LinesToItems(absPath + "\\files\\sitypes.txt", listOfNames);           
        }
        catch(Exception exp) {
        	listOfNames.add("������� ������ �����");
        	listOfNames.add("����� �������� �����������");
        	listOfNames.add("�������� ����������� �������������");
        	listOfNames.add("�������� ����������");
        	listOfNames.add("������������� � ���������� ��������� ���");
        	listOfNames.add("�������� ����������� �� ���������");
            System.out.println("�� ������� ��������� ������ ����� �������� �� ����. �������� ���������� � ����������: " + exp.getMessage());
        }
        finally {
        	namesListView.setItems(listOfNames);
        }
    }

    @FXML
    private void addBtnClick() {
        String itemText = this.newNameTextField.getText();
        ObservableList<String> tmpList = FXCollections.observableArrayList();
        tmpList = this.namesListView.getItems();
        tmpList.add(itemText);
        this.namesListView.setItems(tmpList);
        this.newNameTextField.setText("");
    }

    @FXML
    private void delBtnClick() {
        String itemText = this.namesListView.getSelectionModel().getSelectedItem().toString();
        int index = this.namesListView.getSelectionModel().getSelectedIndex();
        ObservableList<String> tmpList = FXCollections.observableArrayList();
        tmpList = this.namesListView.getItems();
        tmpList.remove(index);
        this.namesListView.setItems(tmpList);
        this.newNameTextField.setText(itemText);
    }

    @FXML
    private void saveBtnClick() {
    	AboutMessageWindow msgWin = null;
    	try {
            String absPath = new File(".").getAbsolutePath();
            FileManager.ItemsToLines(absPath + "\\files\\sitypes.txt", listOfNames);
            myWindow.getMyOwner().setItemsOfNames();
            msgWin = AboutMessageWindow.createWindow("�������", "����� ��� �� ��������");
            myWindow.close();
    	}
    	catch(IOException ioExp) {
            msgWin = AboutMessageWindow.createWindow("������", "�� ������� �������� ����� ��� ��\n��������� ������������� �����\n...\\files\\sitypes.txt");    
    	}
    	finally {
    		msgWin.show();
    	}
    }


    private AddNewDeviceNameWindow myWindow;
    public void setMyWindow(AddNewDeviceNameWindow MyWin) {
        myWindow = MyWin;
    }

}

