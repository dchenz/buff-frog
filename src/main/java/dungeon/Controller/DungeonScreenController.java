package dungeon.Controller;

import dungeon.Model.Direction;
import dungeon.Model.DungeonEntityBuilder;
import dungeon.Model.Map;
import dungeon.Model.MapObjectives.*;
import dungeon.Model.MapObjects.MapItems.ItemTypes;
import dungeon.View.EndScreen;
import dungeon.View.MenuScreen;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class DungeonScreenController {
	@FXML private AnchorPane viewPane;

	@FXML private AnchorPane fadeCover;

	@FXML private AnchorPane mapScore;

	@FXML private Text mapMoves;

	@FXML private Button restartButton;

	@FXML private Button mainMenuButton;

	@FXML private AnchorPane keySlot;

	@FXML private AnchorPane swordSlot;

	@FXML private AnchorPane potionSlot;

	@FXML private ImageView openObjectivesButton;

	private GridPane mapGrid;
	private AnchorPane objectivePane;
	private DungeonEntityBuilder build;
	private ArrayList<String> dungeons;
	private Map currMap;
	private String currFile;
	private boolean overlayScreenShowing;

	private boolean customLevelPlaying;

	private MenuScreen menuScreen;

	private EndScreen endScreen;

	public DungeonScreenController() {
		dungeons = new ArrayList<String>();
	}

	public void addLevel(String filename) {
		dungeons.add(filename);
	}

	public void play(int id) {
		currFile = dungeons.get(id);
		setupDungeon(currFile);
		setupDisplay();
	}

	public void playCustom() {
		customLevelPlaying = true;
		play(levelQty() - 1);
	}

	@FXML
	public void restartMap(ActionEvent event) {
		setupDungeon(currFile);
		setupDisplay();
	}

	@FXML
	public void exitMap(ActionEvent event) {
		menuScreen.start();
	}

	public void setMenuScreenScreen(MenuScreen menu) {
		menuScreen = menu;
	}

	private void startEndScreen() {
		fadeCover.setOpacity(0);
		fadeCover.setVisible(false);
		endScreen.start();
	}

	public void setEndScreen(EndScreen scene) {
		endScreen = scene;
	}

	private void setupDungeon(String filename) {
		try {
			build = new DungeonEntityBuilder(filename);
			mapGrid = build.create(600.0);
			currMap = build.getDungeon();
			currMap.getGameStatus().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue) {
					switch (newValue.intValue()) {
						case 1: // game over
							levelFailed();
							break;
						case 2: // you win
							levelSuccess();
							break;
						default:
							assert false : "Bad input";
					}
				}
			});
			currFile = filename;
		} catch (FileNotFoundException e) {
			System.out.printf("ERROR: File \"%s\" could not be opened.\n", filename);
			System.exit(1);
		}
	}

	private void setupDisplay() {
		clearDisplay();

		viewPane.getChildren().add(mapGrid);
		viewPane.getChildren().add(createObjectivePreview());
		viewPane.getChildren().add(createObjectiveTracker());

		keySlot.getChildren().add(build.getItemSlotView(ItemTypes.KEY));
		swordSlot.getChildren().add(build.getItemSlotView(ItemTypes.SWORD));
		potionSlot.getChildren().add(build.getItemSlotView(ItemTypes.POTION));
		mapScore.getChildren().add(build.getScoreCounter());
	}

	private AnchorPane createObjectiveTracker() {
		objectivePane = new AnchorPane();

		AnchorPane taskPane = new AnchorPane();
		objectivePane.setPrefSize(300, 600);
		objectivePane.setLayoutX(-1 * objectivePane.getPrefWidth());
		objectivePane.setVisible(false);

		taskPane.setPrefSize(250, 550);
		taskPane.setLayoutX(25);
		taskPane.setLayoutY(25);

		objectivePane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

		buildObjectivePane(taskPane, currMap.getLevelObjective(), 0, 0);

		objectivePane.getChildren().add(taskPane);

		Button closePaneButton = new Button();
		closePaneButton.setPrefSize(25, 100);
		closePaneButton.setLayoutX(280);
		closePaneButton.setLayoutY(250);
		closePaneButton.setStyle("-fx-background-color: rgba(155, 155, 155, 0.75);");
		closePaneButton.visibleProperty().bind(objectivePane.visibleProperty());

		closePaneButton.setOnAction(ev -> {
			if (objectivePane.isVisible() && !overlayScreenShowing) {
				Timeline closePane = new Timeline(new KeyFrame(Duration.millis(2.5), x -> {
					double layoutX = objectivePane.getLayoutX();
					if (layoutX + objectivePane.getPrefWidth() > 0) {
						objectivePane.setLayoutX(layoutX - 1.75);
					} else {
						objectivePane.setVisible(false);
					}
				}));
				closePane.setCycleCount(300);
				closePane.play();
			}
		});

		objectivePane.getChildren().add(closePaneButton);

		return objectivePane;
	}

	private int buildObjectivePane(
		AnchorPane taskPane, CompletableObjective task, int indent, int n) {
		List<CompletableObjective> subTasks = task.getChildren();

		createTaskLine(taskPane, task, indent, n++);
		if (subTasks != null) {
			for (CompletableObjective subTask : subTasks) {
				n = buildObjectivePane(taskPane, subTask, indent + 1, n);
			}
		}
		return n;
	}

	private void createTaskLine(AnchorPane taskPane, CompletableObjective task, int indent, int n) {
		int checkboxSize = 25, indentSize = 10;

		AnchorPane taskLine = new AnchorPane();
		taskLine.setPrefSize(taskPane.getPrefWidth() - indent * indentSize, checkboxSize);
		taskLine.setLayoutX(indent * indentSize);
		taskLine.setLayoutY(n * checkboxSize);

		CheckBox taskCompletedBox = new CheckBox();
		taskCompletedBox.setPrefSize(checkboxSize, checkboxSize);
		taskCompletedBox.selectedProperty().bind(task.getCompletedProperty());
		taskCompletedBox.setDisable(true);
		taskCompletedBox.setStyle("-fx-opacity: 1");

		Text taskName = new Text(task.getObjectiveData());

		IntegerProperty counterProperty = task.getCompletedAmountProperty();

		if (counterProperty != null) {
			counterProperty.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue) {
					taskName.setText(task.getObjectiveData());
				}
			});
		}
		taskName.setLayoutX(indent * indentSize + checkboxSize);
		taskName.setLayoutY(checkboxSize / 1.5);
		taskName.setStyle("-fx-font-family: \"Lucida Console\"; "
			+ "-fx-fill: white;"
			+ "-fx-font-size: 14px;");

		taskLine.getChildren().add(taskCompletedBox);
		taskLine.getChildren().add(taskName);

		taskPane.getChildren().add(taskLine);
	}

	private AnchorPane createObjectivePreview() {
		Button okButton = new Button("START");

		AnchorPane objectiveScreen = createGameScreenOverlay("OBJECTIVES", okButton, false);

		okButton.setOnAction(closeScreen -> {
			overlayScreenShowing = false;
			viewPane.getChildren().remove(objectiveScreen);
			objectiveScreen.setVisible(false);
		});

		int nAddedPanes = 1;
		nAddedPanes +=
			createObjectivePreviewPane(objectiveScreen, ObjectiveTypes.TREASURE, nAddedPanes);
		nAddedPanes +=
			createObjectivePreviewPane(objectiveScreen, ObjectiveTypes.ENEMY, nAddedPanes);
		nAddedPanes +=
			createObjectivePreviewPane(objectiveScreen, ObjectiveTypes.FLOOR_SWITCH, nAddedPanes);
		nAddedPanes +=
			createObjectivePreviewPane(objectiveScreen, ObjectiveTypes.EXIT, nAddedPanes);

		return objectiveScreen;
	}

	private int createObjectivePreviewPane(AnchorPane objective, ObjectiveTypes type, int n) {
		int nObjectives = currMap.getObjectiveQuantity();
		int nRequired = currMap.getObjectiveRequirement(type);
		int iconWidth = 100;

		if (nRequired != 0) {
			String image = null;

			switch (type) {
				case TREASURE:
					image = "treasure.png";
					break;
				case ENEMY:
					image = "enemy.png";
					break;
				case FLOOR_SWITCH:
					image = "switch.png";
					break;
				case EXIT:
					image = "exit.png";
					break;
			}
			assert image != null;

			AnchorPane pane = new AnchorPane();

			pane.setPrefHeight(150);
			pane.setPrefWidth(100);
			pane.setLayoutX(
				n * ((600.0 - 100 * nObjectives) / (nObjectives + 1)) + (n - 1) * iconWidth);
			pane.setLayoutY(200);

			ImageView obj =
				new ImageView(new Image(getClass().getResourceAsStream("/images/sprites/" + image),
					iconWidth, iconWidth, true, true));

			pane.getChildren().add(obj);

			Text counter = new Text(Integer.toString(nRequired));
			counter.setFont(Font.font("Segoe UI", FontWeight.BOLD, 50));
			counter.setLayoutX(35);
			counter.setLayoutY(-10);

			pane.getChildren().add(counter);

			objective.getChildren().add(pane);
			return 1;
		}
		return 0;
	}

	private AnchorPane createGameScreenOverlay(String message, Button okButton, boolean darkTheme) {
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(600, 600);

		okButton.setPrefHeight(50);
		okButton.setPrefWidth(message.length() * 17.5);
		okButton.setLayoutX((pane.getPrefWidth() - okButton.getPrefWidth()) / 2.0);
		okButton.setLayoutY(400);
		okButton.setStyle("-fx-background-color: transparent; "
			+ "-fx-font-size: 30px;"
			+ "-fx-font-weight: bold;"
			+ String.format("-fx-border-color: %s;", darkTheme ? "white" : "black")
			+ String.format("-fx-text-fill: %s;", darkTheme ? "white" : "black"));
		okButton.visibleProperty().bind(pane.visibleProperty());

		pane.getChildren().add(okButton);

		Text screenMessage = new Text(message);
		screenMessage.setStyle("-fx-font-size: 30px;"
			+ "-fx-font-weight: bold;"
			+ String.format("-fx-fill: %s;", darkTheme ? "white" : "black"));
		screenMessage.setLayoutX((pane.getPrefWidth() - message.length() * 17) / 2.0);
		screenMessage.setLayoutY(75);

		pane.getChildren().add(screenMessage);

		pane.setStyle(String.format(
			"-fx-background-color: rgba(%s, 0.5);", darkTheme ? "0, 0, 0" : "255, 255, 255"));

		overlayScreenShowing = true;

		return pane;
	}

	private void clearDisplay() {
		viewPane.getChildren().clear();
		keySlot.getChildren().clear();
		swordSlot.getChildren().clear();
		potionSlot.getChildren().clear();
		mapScore.getChildren().clear();
	}

	private void levelSuccess() {
		Button nextLevelButton = new Button("NEXT LEVEL");

		AnchorPane screen = createGameScreenOverlay("LEVEL COMPLETE", nextLevelButton, false);

		nextLevelButton.setOnAction(next -> {
			if (customLevelPlaying) {
				exitMap(null);
			} else {
				overlayScreenShowing = false;

				int nextDungeon = dungeons.indexOf(currFile) + 1;
				if (nextDungeon < dungeons.size()) {
					screen.setVisible(false);
					currFile = dungeons.get(nextDungeon);
					setupDungeon(currFile);
					setupDisplay();
				} else {
					fadeCover.setVisible(true);
					Timeline fadeOut = new Timeline(new KeyFrame(Duration.millis(25),
						fade -> { fadeCover.setOpacity(fadeCover.getOpacity() + 0.01); }));
					fadeOut.setOnFinished(finish -> {
						viewPane.getChildren().remove(screen);
						startEndScreen();
					});
					fadeOut.setCycleCount(100);
					fadeOut.play();
				}
			}
		});

		viewPane.getChildren().add(screen);
	}

	private void levelFailed() {
		Button redoLevelButton = new Button("RESTART");

		AnchorPane screen = createGameScreenOverlay("LEVEL FAILED", redoLevelButton, true);

		redoLevelButton.setOnAction(next -> {
			overlayScreenShowing = false;
			screen.setVisible(false);
			restartMap(null);
		});

		screen.setOpacity(0);

		viewPane.getChildren().add(screen);

		Timeline fadeIn = new Timeline(new KeyFrame(
			Duration.millis(10), fade -> { screen.setOpacity(screen.getOpacity() + 0.01); }));
		fadeIn.setCycleCount(100);
		fadeIn.play();
	}

	@FXML
	public void initialize() {
		openObjectivesButton.setOnMouseClicked((MouseEvent event) -> {
			if (!objectivePane.isVisible() && !overlayScreenShowing) {
				Timeline openPane = new Timeline(new KeyFrame(Duration.millis(2.5), o -> {
					objectivePane.setVisible(true);
					double layoutX = objectivePane.getLayoutX();
					if (layoutX + objectivePane.getPrefWidth() < objectivePane.getPrefWidth()) {
						objectivePane.setLayoutX(layoutX + 1.75);
					}
				}));
				openPane.setCycleCount(300);
				openPane.play();
			}
		});
		fadeCover.setVisible(false);
		fadeCover.setStyle("-fx-background-color: white;");
		fadeCover.setOpacity(0);
	}

	@FXML
	public void handleMovement(KeyEvent event) {
		if (!overlayScreenShowing) {
			Direction move = null;
			switch (event.getCode()) {
				case UP:
					move = Direction.UP;
					break;
				case DOWN:
					move = Direction.DOWN;
					break;
				case LEFT:
					move = Direction.LEFT;
					break;
				case RIGHT:
					move = Direction.RIGHT;
					break;
				default:
					return;
			}
			currMap.movePlayer(move);
		}
	}

	public String getLevel(int id) {
		return dungeons.get(id);
	}

	public int levelQty() {
		return dungeons.size();
	}
}
