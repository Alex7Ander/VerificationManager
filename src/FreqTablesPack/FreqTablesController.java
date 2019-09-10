package FreqTablesPack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import DevicePack.Includable;
import FileManagePack.FileManager;
import NewElementPack.NewElementController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class FreqTablesController implements Includable<NewElementController> {

	@FXML
	private Button continueBtn;	
	@FXML
	private Button exitBtn;
	@FXML
	private ComboBox<String> freqTableComboBox;
	
	private NewElementController myOwner;
	private ArrayList<Double> freqs;

	
	public void setMyOwner(NewElementController elemntController){
		myOwner = elemntController;
	}
	
	
	@FXML
	private void initialize() {		
		freqTableComboBox.setItems(FXCollections.observableArrayList(new String("5,2"),
				new String("3,6"), new String("2,4"), new String("1,2")));
		freqTableComboBox.setValue("5,2");
	}
		
	@FXML
	private void continueBtnClick() throws FileNotFoundException, IOException {
		freqs = new ArrayList<Double>();
		String selItem = this.freqTableComboBox.getSelectionModel().getSelectedItem().toString();
		String absPath = new File(".").getAbsolutePath();
		FileManager.LinesToDouble(absPath + "\\files\\"+selItem+".txt", freqs);
		myOwner.setFreqTable(freqs);
		
	    Stage stage = (Stage) exitBtn.getScene().getWindow();
	    stage.close();
	    FreqTablesWindow.deleteFreqTablesWindow();
	}
	
	@FXML
	private void exitBtnClick() {
	    Stage stage = (Stage) exitBtn.getScene().getWindow();
	    stage.close();
	    FreqTablesWindow.deleteFreqTablesWindow();
	}

	@Override
	public NewElementController getMyOwner() {
		return myOwner;
	}
}
