package SearchDevicePack;

import java.io.IOException;

import DevicePack.Device;
import GUIpack.InfoRequestable;
import GUIpack.guiWindow;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class SearchDeviceWindow extends guiWindow{
	
	private static SearchDeviceWindow instanceSearchDeviceWindow;
	
	private SearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) throws IOException{
		super("Поиск средства измерения", "SearchDeviceForm.fxml");
		SearchDeviceController controller = (SearchDeviceController) loader.getController();
		controller.setDevice(incomingDevice);
		controller.setRequester(incomingRequester);
		this.stage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				deleteWindow();				
			}			
		});
	}
		
	public static SearchDeviceWindow getSearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) throws IOException {
		if (instanceSearchDeviceWindow == null) {
			instanceSearchDeviceWindow = new SearchDeviceWindow(incomingDevice, incomingRequester);				
		}
		return instanceSearchDeviceWindow;
	}
	
	public static void deleteWindow() {
		instanceSearchDeviceWindow = null;
	}
	
}
