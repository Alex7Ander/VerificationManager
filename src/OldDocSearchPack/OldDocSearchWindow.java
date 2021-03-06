package OldDocSearchPack;

import java.io.IOException;
import GUIpack.guiWindow;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class OldDocSearchWindow extends guiWindow {

	private static OldDocSearchWindow instanceOldDocSearchWindow;
	
	private OldDocSearchWindow() throws IOException {
		super("����� ���������� �������", "OldDocForm.fxml");
		this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				instanceOldDocSearchWindow = null;				
			}			
		});
	}
	
	public static OldDocSearchWindow getOldDocSearchWindow() throws IOException{
		if(instanceOldDocSearchWindow == null) {
			instanceOldDocSearchWindow = new OldDocSearchWindow();
		}
		return instanceOldDocSearchWindow;
	}
	
	public static void deleteOldDocSearchWindow() {
		instanceOldDocSearchWindow = null;
	}

}