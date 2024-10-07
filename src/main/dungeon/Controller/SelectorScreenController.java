package dungeon.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import dungeon.Model.DungeonEntityBuilder;
import dungeon.View.DungeonScreen;
import dungeon.View.MenuScreen;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.effect.BoxBlur;

import java.io.FileNotFoundException;

public class SelectorScreenController {

	private MenuScreen menuScreen;

	private DungeonScreen dungeonScreen;

	@FXML
	private AnchorPane backgroundImage;

	@FXML
	private Button mainMenuButton;

	@FXML
	private Button prevLevelButton;

	@FXML
	private Button nextLevelButton;

	@FXML
	private Button playLevelButton;

	@FXML
	private AnchorPane previewPane;

	private int currentLevel;
	private int maxLevel;

	public SelectorScreenController() {

	}

	@FXML
	public void exitSelector(ActionEvent event) {
		menuScreen.start();
		resetLevelSelector();
	}

	@FXML
	public void viewPrevious(ActionEvent event) {
		if (currentLevel > 0) {
			setPreview(loadMapPreview(--currentLevel));
			if (currentLevel == 0) {
				prevLevelButton.setVisible(false);
			}
			nextLevelButton.setVisible(true);
		}
	}

	@FXML
	public void viewNext(ActionEvent event) {
		if (currentLevel < maxLevel) {
			setPreview(loadMapPreview(++currentLevel));
			if (currentLevel == maxLevel) {
				nextLevelButton.setVisible(false);
			}
			prevLevelButton.setVisible(true);
		}
	}

	@FXML
	public void playSelectedLevel(ActionEvent event) {
		dungeonScreen.start(currentLevel);
		resetLevelSelector();
	}

	private void resetLevelSelector() {
		while (currentLevel != 0) {
			viewPrevious(null);
		}
	}

	private void initialisePreview() {
		setPreview(loadMapPreview(currentLevel));
		prevLevelButton.setVisible(false);
		maxLevel = dungeonScreen.getController().levelQty() - 1;
	}

	private void setPreview(GridPane view) {
		previewPane.getChildren().clear();
		previewPane.getChildren().add(view);
	}

	private GridPane loadMapPreview(int id) {
		String mapName = dungeonScreen.getController().getLevel(id);

		DungeonEntityBuilder builder = null;
		try {
			builder = new DungeonEntityBuilder(mapName);
		} catch (FileNotFoundException e) {
			System.out.printf("Preview could not be loaded for map: \"%s\"", mapName);
			System.exit(1);
		}
		GridPane view = builder.create(450.0);
		return view;
	}

	public void setDungeonScreen(DungeonScreen screen) {
		dungeonScreen = screen;
		initialisePreview();
	}

	public void setMenuScreen(MenuScreen screen) {
		menuScreen = screen;
	}

	@FXML
	public void initialize() {
		ImageView backgroundView = new ImageView(new Image("file:images/app/selector-screen-bg.jpg", 1500, 1500, true, true));
		backgroundView.setEffect(new BoxBlur(12, 12, 1));
		backgroundImage.getChildren().add(0, backgroundView);
	}

}
