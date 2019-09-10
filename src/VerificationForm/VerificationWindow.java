package VerificationForm;

import java.io.IOException;
import GUIpack.guiWindow;

public class VerificationWindow extends guiWindow{

	private static VerificationWindow instanceVerificationWindow;
		
	private VerificationWindow() throws IOException{
		super("�������", "VerificationForm.fxml");
	}
		
	public static VerificationWindow getVerificationWindow() throws IOException {
		if (instanceVerificationWindow == null) {
			instanceVerificationWindow = new VerificationWindow();			
		}
		return instanceVerificationWindow;
	}

	
}