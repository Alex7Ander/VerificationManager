package application;
	
import java.sql.SQLException;

import DataBasePack.DataBaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
 
public class Main extends Application {  
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			primaryStage.setOnCloseRequest(event -> {				
				try {
					DataBaseManager.getDB().zeroingAllElements();
				} catch (SQLException sqlExp) {
					System.out.println("Ошибка при попытке обнулить поле lastVerificationId в таблице Elements по закрытию программы. " + sqlExp.getMessage());
					sqlExp.printStackTrace();
				}
				System.exit(0);
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}