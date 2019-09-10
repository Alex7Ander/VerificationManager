package AddNewDeviceNameForm;

import java.io.IOException;

import DevicePack.Includable;
import GUIpack.guiWindow;
import NewDevicePack.NewDeviceController;

public class AddNewDeviceNameWindow extends guiWindow implements Includable<NewDeviceController> {

	private NewDeviceController myOwner;
	private AddNewDeviceNameController myController;
	
	private static AddNewDeviceNameWindow instanceNewDeviceNameWindow;
	
	protected AddNewDeviceNameWindow(NewDeviceController winOwnerController) throws IOException {
		super("Добавление нового наименования типа СИ", "AddNewDeviceNameForm.fxml");
		this.myOwner = winOwnerController;
		myController = (AddNewDeviceNameController) loader.getController();
		myController.setMyWindow(this);
	}
	
	public static AddNewDeviceNameWindow getNewDeviceWindow(NewDeviceController winOwnerController) throws IOException {
		if (instanceNewDeviceNameWindow == null) {
			instanceNewDeviceNameWindow = new AddNewDeviceNameWindow(winOwnerController);
		}
		return instanceNewDeviceNameWindow;
	}

	@Override
	public NewDeviceController getMyOwner() {
		return myOwner;
	}

}