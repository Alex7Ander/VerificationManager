package YesNoDialogPack;

import java.io.IOException;
import GUIpack.guiWindow;

public class YesNoWindow extends guiWindow {

	int result;
	YesNoController ctrl;
	
	public YesNoWindow(String titleText, String message) throws IOException {
		super(titleText, "YesNoForm.fxml");
		ctrl = (YesNoController) this.loader.getController();
		ctrl.setMessage(message);
		ctrl.setMyWin(this);
	}
	
	public int showAndWait() {
		this.stage.showAndWait();		
		return ctrl.getResult();
	}

}
