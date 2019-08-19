package NewDeviceForm;

import java.io.IOException;
import GUIpack.guiWindow;

public class NewDeviceWindow extends guiWindow{

	private static NewDeviceWindow instanceNewDeviceWindow;
	
	private NewDeviceWindow() throws IOException {
		super("Добавление нового прибора", "NewDeviceForm.fxml");
	}
	
	public static NewDeviceWindow getNewDeviceWindow() throws IOException {
		if (instanceNewDeviceWindow == null) {
			instanceNewDeviceWindow = new NewDeviceWindow();			
		}
		return instanceNewDeviceWindow;
	}
	
}
