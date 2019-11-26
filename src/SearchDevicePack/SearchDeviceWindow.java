package SearchDevicePack;

import java.io.IOException;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import GUIpack.InfoRequestable;
import GUIpack.guiWindow;

public class SearchDeviceWindow extends guiWindow{
		
	private SearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) throws IOException{
		super("Поиск средства измерения", "SearchDeviceForm.fxml");
		SearchDeviceController controller = (SearchDeviceController) loader.getController();
		controller.setDevice(incomingDevice);
		controller.setRequester(incomingRequester);
	}
		
	public static SearchDeviceWindow getSearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) {
		try {
			return new SearchDeviceWindow(incomingDevice, incomingRequester);
		} 
		catch(IOException ioExp) {
			AboutMessageWindow.createWindow("Ошибка", "Ошибка при создании окна поиска СИ\nВозможно файл SearchDeviceForm.fxml отсутсвует или поврежден.").show();
			System.out.println("Ошибка при создании окна поиска прибора. " + ioExp.getStackTrace());
			return null;
		}
	}
	
	@Override
	public void show() {
		stage.showAndWait();
	}
	
}