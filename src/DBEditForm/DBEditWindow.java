package DBEditForm;

import java.io.IOException;
import GUIpack.guiWindow;

public class DBEditWindow extends guiWindow {

	private static DBEditWindow instanceDBEditWindow;
	
	private DBEditWindow() throws IOException {
		super("��������/�������������� ���� ������", "DBEditForm.fxml");
	}

	public static DBEditWindow getDBEditWindow() throws IOException{
		if(instanceDBEditWindow == null) {
			instanceDBEditWindow = new DBEditWindow();			
		}
		return instanceDBEditWindow;
	}
	
}