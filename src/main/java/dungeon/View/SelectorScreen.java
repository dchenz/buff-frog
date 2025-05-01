package dungeon.View;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dungeon.Controller.SelectorScreenController;

public class SelectorScreen {

	private Stage stage;

	private Scene scene;

	private SelectorScreenController controller;

	public SelectorScreen(Stage stage) throws IOException {
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/select-level.fxml"));

		controller = new SelectorScreenController();
		loader.setController(controller);

		scene = new Scene(loader.load(), 600, 700);

	}

	public void start() {
		stage.setScene(scene);
		stage.show();
	}

	public SelectorScreenController getController() {
		return controller;
	}

}
