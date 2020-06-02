package GUIpack;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class guiWindow {

	protected Parent root;
	protected Scene scene;
	protected Stage stage;
	protected String title;
	protected String fxmlFile;
	protected FXMLLoader loader;
	
	protected guiWindow(String titleText, String FXMLFile) throws IOException {
		this.title = titleText; 
		this.fxmlFile = FXMLFile;
		this.loader = new FXMLLoader(getClass().getResource(fxmlFile));	
		this.root = loader.load();
		this.scene = new Scene(root);
		this.stage = new Stage();
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.setTitle(title);		
	}
	
	public void show() {
		this.stage.show();
	}
	
	public void close() {
		this.stage.close();
	}
	
	public Object getControllerClass() {
		return this.loader.getController();
	}
	
}