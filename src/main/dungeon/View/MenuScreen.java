package dungeon.View;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dungeon.Controller.MenuScreenController;

public class MenuScreen {

	private Stage stage;
	
	private Scene scene;
	
	private MenuScreenController controller;
	
	public MenuScreen(Stage stage) throws IOException {
		this.stage = stage;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main-menu.fxml"));
		
		controller = new MenuScreenController();
		
		loader.setController(controller);
		
		scene = new Scene(loader.load(), 600, 700);
		
	}
	
	public void start() {
		stage.setScene(scene);
		stage.show();
	}
	
	public MenuScreenController getController() {
		return controller;
	}
	
}
