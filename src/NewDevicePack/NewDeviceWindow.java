package NewDevicePack;

import java.io.IOException;

import GUIpack.guiWindow;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class NewDeviceWindow extends guiWindow {

	private static NewDeviceWindow instanceNewDeviceWindow;
	
	private NewDeviceWindow() throws IOException {
		super("Добавление нового прибора", "NewDeviceForm.fxml");
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				instanceNewDeviceWindow = null;				
			}			
		});
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
