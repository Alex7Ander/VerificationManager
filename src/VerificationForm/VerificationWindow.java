package VerificationForm;

import java.io.IOException;
import GUIpack.guiWindow;
import SearchDevicePack.SearchDeviceWindow;

public class VerificationWindow extends guiWindow{

	private static VerificationWindow instanceVerificationWindow;
		
	private VerificationWindow() throws IOException{
		super("Поверка", "VerificationForm.fxml");
	}
		
	public static VerificationWindow getVerificationWindow() throws IOException {
		if (instanceVerificationWindow == null) {
			instanceVerificationWindow = new VerificationWindow();			
		}
		return instanceVerificationWindow;
	}

	
}