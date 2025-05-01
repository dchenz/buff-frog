package dungeon.Controller;

import dungeon.View.MenuScreen;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class InstructionScreenController {
	@FXML private AnchorPane backgroundImage;

	@FXML private AnchorPane arrowKeysView;

	@FXML private ImageView leftButton;

	@FXML private ImageView rightButton;

	@FXML private Button mainMenuButton;

	@FXML private AnchorPane itemView;

	private ArrayList<JSONObject> descriptions;
	private int currDescription;

	private Timeline enemyViewHover;

	private MenuScreen menuScreen;

	public InstructionScreenController() throws IOException {
		descriptions = new ArrayList<JSONObject>();

		InputStream input = getClass().getResourceAsStream("/screens/description.json");
		JSONArray entities = new JSONArray(new JSONTokener(input));
		for (int i = 0; i < entities.length(); i++) {
			descriptions.add(entities.getJSONObject(i));
		}
		currDescription = 0;
	}

	@FXML
	public void exitToMenu(ActionEvent event) {
		resetDescriptionPane();
		menuScreen.start();
	}

	private void prevEntityDescription() {
		if (currDescription > 0) {
			if (enemyViewHover.getStatus() == Status.RUNNING) {
				enemyViewHover.stop();
			}
			loadEntityPane(--currDescription);
			rightButton.setVisible(true);
			if (currDescription == 0) {
				leftButton.setVisible(false);
			}
		}
	}

	private void nextEntityDescription() {
		if (currDescription < descriptions.size() - 1) {
			if (enemyViewHover.getStatus() == Status.RUNNING) {
				enemyViewHover.stop();
			}
			loadEntityPane(++currDescription);
			leftButton.setVisible(true);
			if (currDescription == descriptions.size() - 1) {
				rightButton.setVisible(false);
			}
		}
	}

	@FXML
	public void initialize() {
		ImageView background = new ImageView(
			new Image(getClass().getResourceAsStream("/images/help-screen/help-screen-bg.jpg"), 700,
				700, true, true));
		background.setEffect(new BoxBlur(12, 12, 1));

		backgroundImage.getChildren().add(background);

		ImageView arrows = new ImageView(
			new Image(getClass().getResourceAsStream("/images/help-screen/arrows.gif"), 225, 150,
				true, true));

		arrowKeysView.getChildren().add(arrows);

		loadEntityPane(0);
		leftButton.setVisible(false);

		leftButton.setOnMouseClicked(prevPane -> { prevEntityDescription(); });

		rightButton.setOnMouseClicked(nextPane -> { nextEntityDescription(); });
	}

	private void loadEntityPane(int id) {
		resetDescription();

		JSONObject object = descriptions.get(id);

		String entityName = object.getString("object");

		Text entityType = loadEntityTitle(entityName);
		ImageView entityImage = loadEntityImage(entityName);
		Text entityDescription = loadEntityDescription(object.getString("info"));

		itemView.getChildren().add(0, entityType);
		itemView.getChildren().add(0, entityImage);
		itemView.getChildren().add(0, entityDescription);
	}

	private Text loadEntityTitle(String text) {
		Text entityTitle =
			new Text(text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase());

		entityTitle.resize(150, 50);
		entityTitle.setLayoutX(25);
		entityTitle.setLayoutY(25);
		entityTitle.setStyle("-fx-fill: white; "
			+ "-fx-font-size: 24px;"
			+ "-fx-background-color: transparent;");
		entityTitle.setWrappingWidth(275);

		return entityTitle;
	}

	private ImageView loadEntityImage(String entityName) {
		ImageView image = null;

		if (entityName.equals("door")) {
			image = new ImageView(
				new Image(getClass().getResourceAsStream("/images/help-screen/door.gif"), 150, 150,
					true, true));
		} else {
			image = new ImageView(new Image(
				getClass().getResourceAsStream(String.format("/images/sprites/%s.png", entityName)),
				150, 150, true, true));
		}

		if (entityName.equals("enemy")) {
			ImageView hoveringEntity = image;
			hoveringEntity.setTranslateZ(0);
			enemyViewHover = new Timeline(new KeyFrame(Duration.millis(25), move -> {
				double hover = hoveringEntity.getTranslateZ();
				hoveringEntity.setLayoutY(hoveringEntity.getLayoutY() + Math.sin(hover) / 2.5);
				hoveringEntity.setTranslateZ(hover + 0.05);
			}));
			enemyViewHover.setCycleCount(Animation.INDEFINITE);
			enemyViewHover.play();
		}

		image.resize(150, 150);
		image.setLayoutX(25);
		image.setLayoutY(24);

		return image;
	}

	private Text loadEntityDescription(String text) {
		Text entityDescription = new Text(text);

		entityDescription.resize(250, 150);
		entityDescription.setLayoutX(200);
		entityDescription.setLayoutY(35);
		entityDescription.setStyle("-fx-fill: white; "
			+ "-fx-font-size: 16px;"
			+ "-fx-background-color: transparent;");
		entityDescription.setWrappingWidth(250);

		return entityDescription;
	}

	private void resetDescription() {
		if (itemView.getChildren().size() > 3) {
			itemView.getChildren().remove(0, 3);
		}
	}

	private void resetDescriptionPane() {
		while (currDescription > 0) {
			prevEntityDescription();
		}
	}

	public void setMenuScreen(MenuScreen screen) {
		menuScreen = screen;
	}
}
