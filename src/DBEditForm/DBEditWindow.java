package DBEditForm;

import java.io.IOException;
import GUIpack.guiWindow;
import VerificationForm.VerificationWindow;

public class DBEditWindow extends guiWindow {

	private static DBEditWindow instanceDBEditWindow;
	
	private DBEditWindow() throws IOException {
		super("ѕросмотр/редактирование базы данных", "DBEditForm.fxml");
	}

	public static DBEditWindow getDBEditWindow() throws IOException{
		if(instanceDBEditWindow == null) {
			instanceDBEditWindow = new DBEditWindow();			
		}
		return instanceDBEditWindow;
	}
	
}
