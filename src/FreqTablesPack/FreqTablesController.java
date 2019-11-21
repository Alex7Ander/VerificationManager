package FreqTablesPack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import FileManagePack.FileManager;
import NewElementPack.NewElementController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class FreqTablesController {

	@FXML
	private Button continueBtn;	
	@FXML
	private Button exitBtn;
	@FXML
	private ComboBox<String> freqTableComboBox;
	private ObservableList<String> freqTables;
	
	private NewElementController myOwner;
	private ArrayList<Double> freqs;

	
	public void setMyOwner(NewElementController elemntController){
		myOwner = elemntController;
	}
	
	
	@FXML
	private void initialize() {	
		freqTables = FXCollections.observableArrayList();
		try {
			String filePath = new File(".").getAbsolutePath() + "\\files\\freqTables.txt";
			FileManager.LinesToItems(filePath, freqTables);
		}
		catch(IOException ioExp) {
			freqTables.add("5,2");
			freqTables.add("3,6");
			freqTables.add("2,4");
			freqTables.add("1,2");
		}
		freqTableComboBox.setItems(freqTables);
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

}