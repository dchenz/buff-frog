package dungeon.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.effect.BoxBlur;

import java.util.Random;

import dungeon.View.DungeonScreen;
import dungeon.View.SelectorScreen;
import dungeon.View.SandboxScreen;
import dungeon.View.InstructionScreen;

public class MenuScreenController {

	@FXML
	private StackPane backgroundImage;

	@FXML
	private AnchorPane mainImage;

	@FXML
	private AnchorPane gameTitle;

	@FXML
	private Button startButton;

	@FXML
	private Button levelSelectButton;

	@FXML
	private Button instructionsButton;
	
	@FXML
	private Button sandboxButton;

	private DungeonScreen dungeonScreen;
	private SelectorScreen selectorScreen;
	private InstructionScreen instructionScreen;
	private SandboxScreen sandboxScreen;

	private Timeline timeline;

	@FXML
	public void playCurrentLevel(ActionEvent event) {
		dungeonScreen.start();
	}

	@FXML
	public void selectLevelMap(ActionEvent event) {
		selectorScreen.start();
	}

	@FXML
	public void openInstructions(ActionEvent event) {
		instructionScreen.start();
	}
	
	@FXML
	public void openSandbox(ActionEvent event) {
		sandboxScreen.start();
	}

	@FXML
	public void initialize() {

		String backgroundFile = String.format("/images/menu-backgrounds/background%d.jpg", new Random().nextInt(3) + 1);

		mainImage.getChildren().add(0, new ImageView(new Image(getClass().getResourceAsStream("/images/app/main-image.png"), 550, 550, true, true)));
		gameTitle.getChildren().add(new ImageView(new Image(getClass().getResourceAsStream("/images/app/title.png"), 400, 250, true, true)));

		ImageView backgroundView = new ImageView(new Image(getClass().getResourceAsStream(backgroundFile), 2000, 2000, true, true));
		backgroundView.setEffect(new BoxBlur(12, 12, 1));

		backgroundImage.getChildren().add(0, backgroundView);

		EventHandler<ActionEvent> event = ev -> {
			backgroundView.setRotate(backgroundView.getRotate() + 0.25);
		};

		timeline = new Timeline(new KeyFrame(Duration.millis(100), event));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private void loadSandboxIcon() {
		AnchorPane icon = new AnchorPane();
		icon.setPrefHeight(100);
		icon.setPrefWidth(100);
		icon.setLayoutX(350);
		icon.setLayoutY(75);
		
		ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/images/app/hammer.png"), 100, 100, true, true));
		
		icon.getChildren().add(image);
		
		
	}
	
	public void setDungeonScreen(DungeonScreen screen) {
		dungeonScreen = screen;
	}

	public void setSelectorScreen(SelectorScreen screen) {
		selectorScreen = screen;
	}

	public void setInstructionsScreen(InstructionScreen screen) {
		instructionScreen = screen;
	}
	
	public void setSandboxScreen(SandboxScreen screen) {
		sandboxScreen = screen;
	}

}
