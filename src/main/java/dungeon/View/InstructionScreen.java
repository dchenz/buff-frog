package dungeon.View;

import dungeon.Controller.InstructionScreenController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InstructionScreen {
	private Stage stage;

	private Scene scene;

	private InstructionScreenController controller;

	public InstructionScreen(Stage stage) throws IOException {
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/instructions.fxml"));

		controller = new InstructionScreenController();
		loader.setController(controller);

		scene = new Scene(loader.load(), 600, 700);
	}

	public void start() {
		stage.setScene(scene);
		stage.show();
	}

	public InstructionScreenController getController() {
		return controller;
	}
}
