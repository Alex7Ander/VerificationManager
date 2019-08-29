package StartVerificationPack;

import java.io.IOException;

import GUIpack.guiWindow;

public class StartVerificationWindow  extends guiWindow {

	private static StartVerificationWindow instanceStartVerificationWindow;
	
	private StartVerificationWindow() throws IOException{
		super("���������� � �������", "StartVerificationForm.fxml");
	}
		
	public static StartVerificationWindow getVerificationWindow() throws IOException {
		if (instanceStartVerificationWindow == null) {
			instanceStartVerificationWindow = new StartVerificationWindow();			
		}
		return instanceStartVerificationWindow;
	}
	
}
