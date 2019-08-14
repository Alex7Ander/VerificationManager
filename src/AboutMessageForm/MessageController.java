package AboutMessageForm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessageController {
	
	@FXML
	private Button closeBtn;
	@FXML
	private Label infoLabel;
	
    public void setMessage(String anyMessage){
        infoLabel.setText(anyMessage);
    }
	
	@FXML
	private void closeBtnClick(){
		try {		
		    Stage stage = (Stage) closeBtn.getScene().getWindow();
		    stage.close();
		}
		catch(Exception exp){
			//System.exit(0);
		}
	}
}