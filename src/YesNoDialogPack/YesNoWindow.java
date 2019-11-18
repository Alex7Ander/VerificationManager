package YesNoDialogPack;

import java.io.IOException;
import GUIpack.guiWindow;

public class YesNoWindow extends guiWindow {

	int result;
	YesNoController ctrl;
	
	private YesNoWindow(String title, String message) throws IOException {
		super(title, "YesNoForm.fxml");
		ctrl = (YesNoController) this.loader.getController();
		ctrl.setMessage(message);
		ctrl.setMyWin(this);
	}
	
	public int showAndWait() {
		stage.showAndWait();		
		return ctrl.getResult();
	}
	
	public static YesNoWindow createYesNoWindow(String title, String message) {
		YesNoWindow yesNoWindow = null;
		try {
			yesNoWindow = new YesNoWindow(title, message);
		}
		catch(IOException ioExp) {
			System.out.println("Возникла ошибка при создании диалогового окна (" + message + ")" + ioExp.getStackTrace());
		}
		return yesNoWindow;
	}

}
