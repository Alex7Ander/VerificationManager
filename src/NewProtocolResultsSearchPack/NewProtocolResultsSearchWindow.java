package NewProtocolResultsSearchPack;

import java.io.IOException;

import DevicePack.Device;
import GUIpack.guiWindow;

public class NewProtocolResultsSearchWindow extends guiWindow{
	
	private static NewProtocolResultsSearchWindow instanceNewProtocolResultsSearchWindow;
	private static NewProtocolResultsSearchController ctrl;
	private NewProtocolResultsSearchWindow(Device device) throws IOException {
		super("", "NewProtocolResultsSearchForm.fxml");
		ctrl = (NewProtocolResultsSearchController) this.loader.getController();
	}

	public static NewProtocolResultsSearchWindow getProtocolCreateWindow(Device device) throws IOException {
		if (instanceNewProtocolResultsSearchWindow == null) {
			instanceNewProtocolResultsSearchWindow = new NewProtocolResultsSearchWindow(device);
		}
		ctrl.setDevice(device);
		return instanceNewProtocolResultsSearchWindow;
	}
	
	public static void delete() {
		instanceNewProtocolResultsSearchWindow = null;
	}
	
	public static void closeInstanceWindow() {
		instanceNewProtocolResultsSearchWindow.close();
	}
	
	public static NewProtocolResultsSearchController getController() {
		return ctrl;
	}
	
}
