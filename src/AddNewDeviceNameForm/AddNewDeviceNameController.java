package AddNewDeviceNameForm;

import java.io.File;
import java.util.Iterator;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Includable;
import FileManagePack.FileManager;
import NewDevicePack.NewDeviceController;
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
    private ListView namesListView;

    @FXML
    private TextField newNameTextField;

    private ObservableList<String> listOfNames;

    @FXML
    private void initialize(){
        listOfNames = FXCollections.observableArrayList();
        try {
            String absPath = new File(".").getAbsolutePath();
            FileManager.LinesToItems(absPath + "\\files\\sitypes.txt", listOfNames);
            this.namesListView.setItems(listOfNames);
        }
        catch(Exception exp) {
            System.out.println("Error: " + exp.getMessage());
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
        this.listOfNames = this.namesListView.getItems();
        try {
            String absPath = new File(".").getAbsolutePath();
            FileManager.ItemsToLines(absPath + "\\files\\sitypes.txt", listOfNames);
            myWindow.getMyOwner().setItemsOfNames();
            AboutMessageWindow msgWin = new AboutMessageWindow("Успешно", "Изменения списка наименований типов СИ\nуспешно сохранены.");
            msgWin.show();
            myWindow.close();
        }
        catch(Exception exp) {
            //
        }

    }


    private AddNewDeviceNameWindow myWindow;
    public void setMyWindow(AddNewDeviceNameWindow MyWin) {
        myWindow = MyWin;
    }

}

