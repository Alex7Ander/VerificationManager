package application;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
	
	@FXML
	private Label infoLabel;
	
	@FXML
	private void initialize(){
		Sender sender = new Sender();
		try {
			sender.send();
			Platform.exit();
		} catch (IOException ioExp) {
			ioExp.getStackTrace();
			Platform.exit();		
		} catch (NullPointerException npExp) {
			Platform.exit();
		} 
	}
	
}
