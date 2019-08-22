package GUIpack;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class guiWindow {
	//protected UniqueWindow instanceWindow;
	protected Parent root;
	protected Scene scene;
	protected Stage stage;
	protected String title;
	protected String fxmlFile;
	protected FXMLLoader loader;
	
	protected guiWindow(String titleText, String FXMLFile) throws IOException {
		title = titleText; 
		fxmlFile = FXMLFile;
		loader = new FXMLLoader(getClass().getResource(fxmlFile));	
		root = loader.load();
		scene = new Scene(root);
		stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle(title);		
		
	}
	
	public void show() {
		stage.show();
	}
	
	public void close() {
		stage.close();
	}
	

}
