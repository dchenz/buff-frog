package dungeon.Controller;

import dungeon.View.DungeonScreen;
import dungeon.View.MenuScreen;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SandboxScreenController {
	private ColorAdjust tileEffect;
	private ColorAdjust entityEffect;

	// flags

	private AnchorPane selectedTile;
	private ImageView selectedEntity;

	// constants

	private final int ROWS_MIN = 5, ROWS_MAX = 20;
	private final int COLS_MIN = 5, COLS_MAX = 20;

	// current tile size properties

	private DoubleProperty tileSize;

	private int nRows, nCols;

	// image

	private Image defaultTile;

	// screens

	private MenuScreen menuScreen;

	private DungeonScreen dungeonScreen;

	// map loader

	private DungeonScreenController dungeonController;

	// arrow buttons

	@FXML private ImageView rowUpButton;

	@FXML private ImageView rowDownButton;

	@FXML private ImageView colUpButton;

	@FXML private ImageView colDownButton;

	// counters

	@FXML private Text rowCounter;

	@FXML private Text colCounter;

	// scene buttons

	@FXML private Button createDungeonButton;

	@FXML private Button mainMenuButton;

	// panes

	@FXML private AnchorPane viewPane;

	@FXML private AnchorPane entitiesPane;

	@FXML private AnchorPane itemsPane;

	// gridpane

	private GridPane mapGrid;

	private GridPane tmpGrid;

	// entities grid for building

	private ArrayList<String[]> entities;

	// CONSTRUCTOR
	public SandboxScreenController() {
		tileSize = new SimpleDoubleProperty();
		defaultTile = new Image(
			getClass().getResourceAsStream("/images/sprites/grass.png"), 32, 32, true, true);
		nRows = ROWS_MIN;
		nCols = COLS_MIN;
		tileEffect = new ColorAdjust();
		entityEffect = new ColorAdjust();
		tileEffect.setBrightness(0.5);
		entityEffect.setBrightness(1);

		entities = new ArrayList<String[]>();
	}

	// button handlers

	@FXML
	public void buildCustomDungeon(ActionEvent event) throws IOException {
		if (dungeonEntitiesOK()) {
			final String saveFile = "build-data.json";

			FileWriter writer = new FileWriter("maps/" + saveFile);

			writer.write(String.format("{\"width\":%d,\"height\":%d,\"entities\":[", nCols, nRows));

			int n = 0;
			for (String[] entity : entities) {
				if (entity[3] != null) {
					writer.write(String.format("{\"type\":\"%s\",\"x\":%s,\"y\":%s,\"id\":%s}",
						entity[0], entity[1], entity[2], entity[3]));
				} else {
					writer.write(String.format(
						"{\"type\":\"%s\",\"x\":%s,\"y\":%s}", entity[0], entity[1], entity[2]));
				}
				if (n != entities.size() - 1) {
					writer.write(",");
				}
				n++;
			}

			writer.write("],\"goal-condition\":{\"goal\":\"enemies\"}}");

			writer.close();

			dungeonController.addLevel(saveFile);
			dungeonScreen.startCustom();
		} else {
			createDungeonButton.setStyle("-fx-background-color: rgba(200, 50, 55, 0.6); "
				+ "-fx-border-color: rgba(190, 60, 55, 0.6);");
			Timeline revert = new Timeline(new KeyFrame(Duration.millis(1), x -> {
				createDungeonButton.setStyle("-fx-background-color: rgba(50, 200, 55, 0.6);"
					+ "-fx-border-color: rgba(40, 170, 45, 0.7);");
			}));
			revert.setCycleCount(1);
			revert.setDelay(Duration.seconds(1));
			revert.play();
		}
	}

	private boolean dungeonEntitiesOK() {
		int nPlayers = 0, nKeys = 0, nDoors = 0, nPortals = 0;

		for (String[] entity : entities) {
			switch (entity[0]) {
				case "player":
					nPlayers++;
					break;
				case "key":
					nKeys++;
					break;
				case "door":
					nDoors++;
					break;
				case "portal":
					nPortals++;
					break;
			}
		}

		return (nPlayers == 1 && nKeys == nDoors && nPortals % 2 == 0);
	}

	@FXML
	public void exitToMenu(ActionEvent event) {
		menuScreen.start();
	}

	@FXML
	public void initialize() {
		rowCounter.setText(Integer.toString(ROWS_MIN));
		colCounter.setText(Integer.toString(COLS_MIN));

		setButtonHandlers();

		mapGrid = createGridPane(ROWS_MIN, COLS_MIN);
		loadGridPane(mapGrid, ROWS_MIN, COLS_MIN);

		setCounterChangeListeners();

		loadEntities();
	}

	public void reset() {
		entities.clear();
		viewPane.getChildren().clear();
		mapGrid = null;
		tmpGrid = null;
		nRows = ROWS_MIN;
		nCols = COLS_MIN;
		selectedTile = null;
		selectedEntity = null;
	}

	private void setButtonHandlers() {
		rowUpButton.setOnMouseClicked(increment -> {
			if (nRows < ROWS_MAX) {
				rowCounter.setText(Integer.toString(nRows + 1));
			}
		});
		rowDownButton.setOnMouseClicked(decrement -> {
			if (nRows > ROWS_MIN) {
				rowCounter.setText(Integer.toString(nRows - 1));
			}
		});

		colUpButton.setOnMouseClicked(increment -> {
			if (nCols < COLS_MAX) {
				colCounter.setText(Integer.toString(nCols + 1));
			}
		});
		colDownButton.setOnMouseClicked(decrement -> {
			if (nCols > COLS_MIN) {
				colCounter.setText(Integer.toString(nCols - 1));
			}
		});
	}

	private void setCounterChangeListeners() {
		rowCounter.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
				ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int oldRows = Integer.parseInt(oldValue), newRows = Integer.parseInt(newValue);

				nRows = newRows;

				if (newRows > oldRows) {
					tileSize.set(viewPane.getPrefHeight() / (newRows < nCols ? nCols : newRows));
					addGridRow();
				} else if (newRows < oldRows) {
					tmpGrid = createGridPane(nRows, nCols);
					copyGridPane(mapGrid, tmpGrid, nRows, nCols);
					checkForDeletion();
				} else {
					assert false : "Rows did not change";
				}
			}
		});
		colCounter.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
				ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int oldCols = Integer.parseInt(oldValue), newCols = Integer.parseInt(newValue);

				nCols = newCols;

				if (newCols > oldCols) {
					tileSize.set(viewPane.getPrefHeight() / (newCols < nRows ? nRows : newCols));
					addGridColumn();
				} else if (newCols < oldCols) {
					tmpGrid = createGridPane(nRows, nCols);
					copyGridPane(mapGrid, tmpGrid, nRows, nCols);
					checkForDeletion();
				} else {
					assert false : "Columns did not change";
				}
			}
		});
	}

	private GridPane createGridPane(int initRows, int initCols) {
		GridPane pane = new GridPane();

		double size = viewPane.getPrefHeight() / (initRows < initCols ? initCols : initRows);

		tileSize.set(size);

		for (int col = 0; col < initCols; col++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.prefWidthProperty().bind(tileSize);
			pane.getColumnConstraints().add(cc);
		}

		for (int row = 0; row < initRows; row++) {
			RowConstraints rc = new RowConstraints();
			rc.prefHeightProperty().bind(tileSize);
			pane.getRowConstraints().add(rc);
		}
		return pane;
	}

	private void loadGridPane(GridPane grid, int initRows, int initCols) {
		for (int row = 0; row < initRows; row++) {
			for (int col = 0; col < initCols; col++) {
				grid.add(createDefaultTile(), col, row);
			}
		}
		adjustGridPosition();
		viewPane.getChildren().add(grid);
	}

	private void copyGridPane(GridPane src, GridPane dest, int initRows, int initCols) {
		for (int row = 0; row < initRows; row++) {
			for (int col = 0; col < initCols; col++) {
				dest.add(getGridCell(src, row, col), col, row);
			}
		}
		viewPane.getChildren().add(dest);
		mapGrid.getChildren().clear();
		viewPane.getChildren().remove(mapGrid);
		mapGrid = dest;
		adjustGridPosition();
	}

	private AnchorPane getGridCell(GridPane grid, int row, int col) {
		for (Node n : grid.getChildren()) {
			if (GridPane.getRowIndex(n) == row && GridPane.getColumnIndex(n) == col) {
				return (AnchorPane) n;
			}
		}
		return null;
	}

	private void addGridRow() {
		RowConstraints rc = new RowConstraints();
		rc.prefHeightProperty().bind(tileSize);
		mapGrid.getRowConstraints().add(rc);

		for (int col = 0; col < nCols; col++) {
			mapGrid.add(createDefaultTile(), col, nRows - 1);
		}

		adjustGridPosition();
	}

	private void addGridColumn() {
		ColumnConstraints cc = new ColumnConstraints();
		cc.prefWidthProperty().bind(tileSize);
		mapGrid.getColumnConstraints().add(cc);

		for (int row = 0; row < nRows; row++) {
			mapGrid.add(createDefaultTile(), nCols - 1, row);
		}

		adjustGridPosition();
	}

	private AnchorPane createDefaultTile() {
		AnchorPane pane = new AnchorPane();

		ImageView icon = new ImageView(defaultTile);
		icon.fitWidthProperty().bind(tileSize);
		icon.fitHeightProperty().bind(tileSize);

		pane.getChildren().add(icon);

		pane.setOnMouseClicked(highlight -> {
			if (selectedTile != null) {
				// entity must not be selected
				if (pane == selectedTile) {
					unselectTile(pane);
				} else {
					mapGrid.getChildren().forEach(tile -> { tile.setEffect(null); });
					selectedTile = null;
					selectTile(pane);
				}
			} else {
				if (selectedEntity != null) {
					// add entity
					putEntity(pane, selectedEntity);
				} else {
					// select tile
					selectTile(pane);
				}
			}
		});
		return pane;
	}

	private void adjustGridPosition() {
		mapGrid.setLayoutX((viewPane.getPrefWidth() - tileSize.doubleValue() * nCols) / 2);
		mapGrid.setLayoutY((viewPane.getPrefHeight() - tileSize.doubleValue() * nRows) / 2);
	}

	private void loadEntities() {
		entitiesPane.getChildren().add(0, createEntityIcon(0, "enemy"));
		entitiesPane.getChildren().add(0, createEntityIcon(1, "boulder"));
		entitiesPane.getChildren().add(0, createEntityIcon(2, "portal"));
		entitiesPane.getChildren().add(0, createEntityIcon(3, "exit"));
		entitiesPane.getChildren().add(0, createEntityIcon(4, "closed_door"));
		entitiesPane.getChildren().add(0, createEntityIcon(5, "treasure"));
		entitiesPane.getChildren().add(0, createEntityIcon(6, "player"));
		entitiesPane.getChildren().add(0, createEntityIcon(7, "switch"));
		entitiesPane.getChildren().add(0, createEntityIcon(8, "wall"));
		itemsPane.getChildren().add(0, createItemIcon(0, "key"));
		itemsPane.getChildren().add(0, createItemIcon(1, "sword"));
		itemsPane.getChildren().add(0, createItemIcon(2, "potion"));
		itemsPane.getChildren().add(0, createItemIcon(3, "treasure"));
	}

	private AnchorPane createEntityIcon(int n, String name) {
		ImageView image = new ImageView(
			new Image(getClass().getResourceAsStream(String.format("/images/sprites/%s.png", name)),
				32, 32, true, true));

		image.setFitHeight(45);
		image.setFitWidth(45);

		AnchorPane pane = new AnchorPane();

		pane.getChildren().add(image);

		pane.setPrefWidth(45);
		pane.setPrefHeight(45);
		pane.setLayoutX(n * 45 + (n + 1) * 4.5);
		pane.setLayoutY(20);

		pane.setOnMouseClicked(highlight -> {
			if (selectedEntity != null) {
				if (selectedEntity == image) {
					unselectEntity(image);
				} else {
					selectedEntity = null;
					selectEntity(image);
				}
			} else {
				if (selectedTile != null) {
					// add entity
					putEntity(selectedTile, image);
				} else {
					selectEntity(image);
				}
			}
		});

		if (name.equals("closed_door")) {
			image.setId("door");
		} else {
			image.setId(name);
		}

		return pane;
	}

	private AnchorPane createItemIcon(int n, String name) {
		return createEntityIcon(n, name);
	}

	private void putEntity(AnchorPane tile, ImageView entity) {
		if (tile.getChildren().size() > 1) {
			tile.getChildren().remove(1);
		}
		tile.setEffect(null);

		ImageView view = new ImageView(entity.getImage());
		tile.getChildren().add(view);
		view.fitHeightProperty().bind(tileSize);
		view.fitWidthProperty().bind(tileSize);

		selectedTile = null;
		selectedEntity = null;

		String[] mapEntity = new String[4];

		String name = entity.getId();

		mapEntity[0] = new String(name);
		mapEntity[1] = Integer.toString(GridPane.getColumnIndex((Node) tile));
		mapEntity[2] = Integer.toString(GridPane.getRowIndex((Node) tile));
		mapEntity[3] = null;

		if (name.equals("key") || name.equals("door")) {
			mapEntity[3] = Integer.toString(countEntityType(name));
		} else if (name.equals("portal")) {
			mapEntity[3] = Integer.toString(countEntityType(name) / 2);
		}

		entities.add(mapEntity);
	}

	private void selectTile(AnchorPane tile) {
		assert tile.getEffect() == null;
		assert selectedTile == null;

		tile.setEffect(tileEffect);
		selectedTile = tile;
	}

	private void unselectTile(AnchorPane tile) {
		assert tile.getEffect() != null;
		assert selectedTile != null;

		tile.setEffect(null);
		selectedTile = null;
	}

	private void selectEntity(ImageView entity) {
		assert selectedEntity == null;

		selectedEntity = entity;
	}

	private void unselectEntity(ImageView entity) {
		assert selectedEntity != null;

		selectedEntity = null;
	}

	private void checkForDeletion() {
		Iterator<String[]> entityList = entities.iterator();
		while (entityList.hasNext()) {
			String[] entity = entityList.next();
			int x = Integer.parseInt(entity[1]);
			int y = Integer.parseInt(entity[2]);

			if (x >= nCols || y >= nRows) {
				entityList.remove();
			}
		}
	}

	private int countEntityType(String name) {
		int n = 0;
		for (String[] entity : entities) {
			if (entity[0].equals(name)) {
				n++;
			}
		}
		return n;
	}

	public void setMenuScreen(MenuScreen screen) {
		menuScreen = screen;
	}

	public void setDungeonScreen(DungeonScreen screen) {
		dungeonScreen = screen;
	}

	public void setDungeonController(DungeonScreenController control) {
		dungeonController = control;
	}
}
