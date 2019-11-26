package SearchDevicePack;

import java.io.IOException;

import AboutMessageForm.AboutMessageWindow;
import DevicePack.Device;
import GUIpack.InfoRequestable;
import GUIpack.guiWindow;

public class SearchDeviceWindow extends guiWindow{
		
	private SearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) throws IOException{
		super("����� �������� ���������", "SearchDeviceForm.fxml");
		SearchDeviceController controller = (SearchDeviceController) loader.getController();
		controller.setDevice(incomingDevice);
		controller.setRequester(incomingRequester);
	}
		
	public static SearchDeviceWindow getSearchDeviceWindow(Device incomingDevice, InfoRequestable incomingRequester) {
		try {
			return new SearchDeviceWindow(incomingDevice, incomingRequester);
		} 
		catch(IOException ioExp) {
			AboutMessageWindow.createWindow("������", "������ ��� �������� ���� ������ ��\n�������� ���� SearchDeviceForm.fxml ���������� ��� ���������.").show();
			System.out.println("������ ��� �������� ���� ������ �������. " + ioExp.getStackTrace());
			return null;
		}
	}
	
	@Override
	public void show() {
		stage.showAndWait();
	}
	
}