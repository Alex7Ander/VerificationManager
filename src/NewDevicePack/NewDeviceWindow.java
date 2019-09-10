package NewDevicePack;

import java.io.IOException;

import GUIpack.guiWindow;

public class NewDeviceWindow extends guiWindow {

	private static NewDeviceWindow instanceNewDeviceWindow;
	
	private NewDeviceWindow() throws IOException {
		super("���������� ������ �������", "NewDeviceForm.fxml");
	}
	
	public static NewDeviceWindow getNewDeviceWindow() throws IOException {
		if (instanceNewDeviceWindow == null) {
			instanceNewDeviceWindow = new NewDeviceWindow();			
		}
		return instanceNewDeviceWindow;
	}
	
	public static void deleteNewDeviceWindow() {
		instanceNewDeviceWindow = null;
	}

}
