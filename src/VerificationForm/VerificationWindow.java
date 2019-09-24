package VerificationForm;

import java.io.IOException;
import GUIpack.guiWindow;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class VerificationWindow extends guiWindow{

	private static VerificationWindow instanceVerificationWindow;
		
	private VerificationWindow() throws IOException{
		super("Поверка", "VerificationForm.fxml");
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				instanceVerificationWindow = null;				
			}			
		});
	}
		
	public static VerificationWindow getVerificationWindow() throws IOException {
		if (instanceVerificationWindow == null) {
			instanceVerificationWindow = new VerificationWindow();			
		}
		return instanceVerificationWindow;
	}
	
	public VerificationController getController() {
		return this.loader.getController();
	}

	public static void closeinstancewindow() {
		instanceVerificationWindow.close();
	}
	
}