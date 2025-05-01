package dungeon.View;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dungeon.Controller.EndScreenController;

public class EndScreen {

	private Stage stage;

	private Scene scene;

	private EndScreenController controller;

	public EndScreen(Stage stage) throws IOException {
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/end-scene.fxml"));

		controller = new EndScreenController();
		loader.setController(controller);

		scene = new Scene(loader.load(), 600, 700);

	}

	public void start() {
		controller.trigger();
		stage.setScene(scene);
		stage.show();
	}

	public EndScreenController getController() {
		return controller;
	}

}
