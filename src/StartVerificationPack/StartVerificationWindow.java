package StartVerificationPack;

import java.io.IOException;

import GUIpack.guiWindow;

public class StartVerificationWindow  extends guiWindow {

	private static StartVerificationWindow instanceStartVerificationWindow;
	
	private StartVerificationWindow() throws IOException{
		super("���������� � �������", "StartVerificationForm.fxml");
		StartVerificationController ctrl = (StartVerificationController) this.loader.getController();
		ctrl.setWindow(this);
	}
		
	public static StartVerificationWindow getStartVerificationWindow() throws IOException {
		if (instanceStartVerificationWindow == null) {
			instanceStartVerificationWindow = new StartVerificationWindow();			
		}
		return instanceStartVerificationWindow;
	}
	
	
}
