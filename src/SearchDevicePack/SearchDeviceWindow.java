package SearchDevicePack;

import java.io.IOException;

import DevicePack.Device;
import GUIpack.InfoRequestable;
import GUIpack.guiWindow;

public class SearchDeviceWindow extends guiWindow{
	
	private static SearchDeviceWindow instanceSearchDeviceWindow;
	
	private SearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) throws IOException{
		super("Поиск средства измерения", "SearchDeviceForm.fxml");
		SearchDeviceController controller = (SearchDeviceController) loader.getController();
		controller.setDevice(incomingDevice);
		controller.setRequester(incomingRequester);
		stage.setOnCloseRequest( event -> {
			instanceSearchDeviceWindow = null;
		});
	}
		
	public static SearchDeviceWindow getSearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) throws IOException {
		if (instanceSearchDeviceWindow == null) {
			instanceSearchDeviceWindow = new SearchDeviceWindow(incomingDevice, incomingRequester);				
		}
		return instanceSearchDeviceWindow;
	}
	
	@Override
	public void show() {
		stage.showAndWait();
	}
	
}