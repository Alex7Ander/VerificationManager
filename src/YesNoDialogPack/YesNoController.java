package YesNoDialogPack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class YesNoController {

	@FXML
	private Label msgLabel;
	
	@FXML
	private Button yesBtn;
	@FXML
	private Button noBtn;
	
	private YesNoWindow myWin;
	private int result;
	
	@FXML
	private void initialize() {
		result = 1;
	}
	
	@FXML
	private void yesBtnClick() {
		result = 0;
		myWin.close();
	}
	
	@FXML
	private void noBtnClick() {
		result = 1;
		myWin.close();
	}
	
	public void setMyWin(YesNoWindow MyWindow) {myWin = MyWindow;}
	public void setMessage(String message) { 
		this.msgLabel.setText(message);
	}
	public int getResult() { return result;}
	
}
