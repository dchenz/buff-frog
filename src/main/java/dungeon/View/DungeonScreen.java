package dungeon.View;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dungeon.Controller.DungeonScreenController;

public class DungeonScreen {

	private Stage stage;

	private Scene scene;

	private DungeonScreenController controller;

	public DungeonScreen(Stage stage) throws IOException {
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/dungeon-frame.fxml"));

		controller = new DungeonScreenController();

		loader.setController(controller);

		scene = new Scene(loader.load(), 600, 700);

	}

	public void start() {
		start(0);
	}

	public void start(int id) {
		controller.play(id);
		startScreen();
	}

	public void startCustom() {
		controller.playCustom();
		startScreen();
	}

	private void startScreen() {
		stage.setScene(scene);
		stage.show();
	}

	public DungeonScreenController getController() {
		return controller;
	}

}
