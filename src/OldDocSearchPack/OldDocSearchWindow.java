package OldDocSearchPack;

import java.io.IOException;
import GUIpack.guiWindow;


public class OldDocSearchWindow extends guiWindow {

	private static OldDocSearchWindow instanceOldDocSearchWindow;
	
	private OldDocSearchWindow() throws IOException {
		super("Поиск протоколов поверки", "OldDocSearchForm.fxml");
	}
	
	public static OldDocSearchWindow getOldDocSearchWindow() throws IOException{
		if(instanceOldDocSearchWindow == null) {
			instanceOldDocSearchWindow = new OldDocSearchWindow();
		}
		return instanceOldDocSearchWindow;
	}

}
