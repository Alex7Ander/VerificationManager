package NewElementPack;

import java.io.IOException;
import GUIpack.guiWindow;

public class NewElementWindow extends guiWindow {

	public NewElementWindow() throws IOException {		
		super("", "NewElementForm.fxml");
		NewElementController ctrl = (NewElementController) loader.getController();
		stage.setOnCloseRequest(ctrl.getCloseEventHandler());
		ctrl.setWindow(this);
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}
		
	
}