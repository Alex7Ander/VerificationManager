package NewElementPack;

import java.io.IOException;
import GUIpack.guiWindow;

public class NewElementWindow extends guiWindow {

	public NewElementWindow() throws IOException {		
		super("", "NewElementForm.fxml");
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}
	
}
