package NewElementPack;

import java.io.IOException;
import GUIpack.guiWindow;

public class NewElementWindow extends guiWindow {

	public NewElementWindow() throws IOException {		
		super("", "NewElementForm.fxml");
		NewElementController ctrl = loader.getController();
		stage.setOnCloseRequest(ctrl.getCloseEventHandler());
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}
	
}
