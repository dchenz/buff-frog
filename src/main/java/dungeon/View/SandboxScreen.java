package dungeon.View;

import dungeon.Controller.SandboxScreenController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SandboxScreen {
	private Stage stage;

	private Scene scene;

	private SandboxScreenController controller;

	public SandboxScreen(Stage stage) throws IOException {
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/sandbox-frame.fxml"));

		controller = new SandboxScreenController();
		loader.setController(controller);

		scene = new Scene(loader.load(), 600, 700);
	}

	public void start() {
		controller.reset();
		controller.initialize();
		stage.setScene(scene);
		stage.show();
	}

	public SandboxScreenController getController() {
		return controller;
	}
}
