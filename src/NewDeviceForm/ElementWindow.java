package NewDeviceForm;

import java.io.IOException;

import GUIpack.guiWindow;

public class ElementWindow extends guiWindow {

	public ElementWindow() throws IOException {
		super("", "ElementForm.fxml");
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}

}
