package DBEditForm;

import java.io.IOException;
import GUIpack.guiWindow;

public class DBEditWindow extends guiWindow {

	private static DBEditWindow instanceDBEditWindow;
	
	private DBEditWindow() throws IOException {
		super("ѕросмотр/редактирование базы данных", "DBEditForm.fxml");
		stage.setOnCloseRequest(event -> {
			delete();
		});
	}

	public static DBEditWindow getDBEditWindow() throws IOException{
		if(instanceDBEditWindow == null) {
			instanceDBEditWindow = new DBEditWindow();			
		}
		return instanceDBEditWindow;
	}
	
	public DBEditController getController() {
		return this.loader.getController();
	}
	
	public void delete() {
		instanceDBEditWindow = null;
	}
	
}