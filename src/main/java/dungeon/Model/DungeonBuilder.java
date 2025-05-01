package dungeon.Model;

import dungeon.Model.Map;
import dungeon.Model.MapObjects.*;
import dungeon.Model.MapObjects.MapItems.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * DungeonBuilder.java
 *
 * Used to create the GridPane for the UI
 *
 * Creation process:
 * JSONObject >> Map object >> GridPane
 *
 *
 */

public abstract class DungeonBuilder {
	// Size of the inventory slot icons
	private final static int ICON_SIZE = 50;

	// Used to know what size to create all tiles
	// Set inside initialiseGrid()
	protected double TILE_SIZE;

	// Map ID
	private static int totalMaps = 0;

	// Number of rows and columns in the map, respectively
	protected int height, width;

	// The map container
	private GridPane mapGrid;

	// Map data
	private JSONObject map;
	private JSONArray entities;

	// The map model to store everything
	private Map dungeon;

	// Item slots on the UI
	private HashMap<ItemTypes, AnchorPane> itemViews;

	// Item images for the inventory slots on the UI
	private HashMap<ItemTypes, ImageView> itemImages;

	// Used to link portals to each other
	private HashMap<Integer, Portal> portals;

	// In-game score
	private Text scoreCounter;

	/**
	 * Constructor
	 *
	 * @param filename					: name of the json dungeon file
	 * @throws FileNotFoundException    : File not found
	 */
	public DungeonBuilder(String filename) throws FileNotFoundException {
		InputStream input = getClass().getResourceAsStream("/maps/" + filename);
		map = new JSONObject(new JSONTokener(input));

		mapGrid = new GridPane();
		itemViews = new HashMap<ItemTypes, AnchorPane>();
		portals = new HashMap<Integer, Portal>();
		itemImages = new HashMap<ItemTypes, ImageView>();

		// Store image views for the inventory item slots on the UI
		// Will toggle between visibility when player picks them up
		itemImages.put(ItemTypes.KEY,
			new ImageView(new Image(getClass().getResourceAsStream("/images/sprites/key.png"),
				ICON_SIZE, ICON_SIZE, true, true)));
		itemImages.put(ItemTypes.SWORD,
			new ImageView(new Image(getClass().getResourceAsStream("/images/sprites/sword.png"),
				ICON_SIZE, ICON_SIZE, true, true)));
		itemImages.put(ItemTypes.POTION,
			new ImageView(new Image(getClass().getResourceAsStream("/images/sprites/potion.png"),
				ICON_SIZE, ICON_SIZE, true, true)));
	}

	/**
	 * Parses the JSON to create a dungeon.
	 * @return Map object
	 */
	private Map loadDungeon() {
		// dimensions
		height = map.getInt("height");
		width = map.getInt("width");

		// entities
		entities = map.getJSONArray("entities");

		// building the objectives using recursion
		// Must count the number of each objective's entity
		// so the objective knows how many are required to complete it
		ObjectiveBuilder taskBuilder = new ObjectiveBuilder(countEntityType(entities, "treasure"),
			countEntityType(entities, "switch"), countEntityType(entities, "enemy"),
			countEntityType(entities, "exit"));

		// Set it to an objective manager
		//
		// Stores the root objective (if complete, level complete)
		// Stores the leaf objectives (enemies, treasure, boulders, exit)
		ObjectiveManager taskManager = new ObjectiveManager(
			taskBuilder.build(map.getJSONObject("goal-condition")), // build recursively
			taskBuilder.getObjectives());

		// Create Map and return it
		return new Map(totalMaps++, height, width, taskManager);
	}

	/**
	 * Loops through the JSONArray and creates an entity object for each one
	 * and adds it to the Map object
	 *
	 * @param dungeonMap: Map object
	 */
	private void createEntities(Map dungeonMap) {
		// loop through json array
		for (int x = 0; x < entities.length(); x++) {
			// Get entity and load it
			JSONObject mapEntity = entities.getJSONObject(x);
			loadEntity(dungeonMap, mapEntity);
		}

		// Load and create the enemy mover after creating all entities
		// It registers all entity positions and bind with the user's position on the Map
		dungeonMap.loadEnemyMover();
	}

	/**
	 * Counts how many of an entity type are on a Map
	 *
	 * @param entities: all entities
	 * @param type:     type of entity being queried
	 *
	 * @return    Integer of how many of that entity are on the Map
	 */
	private int countEntityType(JSONArray entities, String type) {
		int count = 0, total = entities.length();
		for (int row = 0; row < total; row++) {
			JSONObject mapEntity = entities.getJSONObject(row);
			if (mapEntity.getString("type").equals(type)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Creates an entity object from the JSON object
	 * then adds the entity to the Map
	 *
	 * @param dungeon: Map object
	 * @param json  : JSON object
	 */
	private void loadEntity(Map dungeon, JSONObject json) {
		// Get entity type and coordinates
		String type = json.getString("type");
		int x = json.getInt("x");
		int y = json.getInt("y");

		switch (type) {
			case "player":

				// Create inventory
				Inventory inventory = new Inventory();

				// Bind the item slots on the UI to the inventory internal item slots
				bindItemSlots(inventory, ItemTypes.KEY);
				bindItemSlots(inventory, ItemTypes.SWORD);
				bindItemSlots(inventory, ItemTypes.POTION);

				// Bind the score counter to how many treasures are collected
				bindScoreCounter(inventory);

				// Create the player and give it the inventory
				Player player = onLoad(new Player(inventory, x, y));

				// Add listener to end the level if the player dies
				// Player has a boolean property where TRUE if alive, else FALSE
				player.getStateProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
						if (newValue == false) {
							dungeon.levelFailed(); // Fail level and end it
						} else
							assert false; // Assert: cannot die yet still be alive
					}
				});

				player.loadViews(); // load invincible image view

				dungeon.add(player); // Add to Map
				break;
			case "enemy":
				// Create enemy object
				//
				// It has a boolean prperty for whether it is following the player or not

				// If the player is holding a Potion, the slot will be TRUE and
				// it will be NOT FOLLOWING (false)

				// If the player is not holding Potion, the slot will be FALSE and
				// it will be FOLLOWING (true)
				Enemy enemy = new Enemy(x, y);
				enemy.getFollowingProperty().bind(
					itemImages.get(ItemTypes.POTION).visibleProperty().not());

				dungeon.add(onLoad(enemy)); // Add to Map
				break;
			case "wall":
				dungeon.add(onLoad(new Wall(x, y)), x, y); // Add to Map
				break;
			case "exit":
				dungeon.add(onLoad(new Exit(x, y)), x, y); // Add to Map
				break;
			case "door":
				dungeon.add(onLoad(new Door(json.getInt("id"), x, y)), x, y); // Add to Map with ID
				break;
			case "boulder":
				dungeon.add(onLoad(new Boulder(x, y)), x, y); // Add to Map
				break;
			case "switch":
				dungeon.add(onLoad(new FloorSwitch(x, y)), x, y); // Add to Map
				break;
			case "portal":
				// Must find the connecting portal with the same ID and link it before adding
				dungeon.add(
					onLoad(getConnectedPortal(json.getInt("id"), x, y)), x, y); // Add to Map
				break;
			case "key":
				dungeon.add(onLoad(new Key(json.getInt("id"), x, y)), x, y); // Add to Map with ID
				break;
			case "sword":
				dungeon.add(onLoad(new Sword(x, y)), x, y); // Add to Map
				break;
			case "potion":
				dungeon.add(onLoad(new Potion(x, y)), x, y); // Add to Map
				break;
			case "treasure":
				dungeon.add(onLoad(new Treasure(x, y)), x, y); // Add to Map
				break;
		}
	}

	/**
	 * Find the other portal for a portal's ID
	 * and link them together.
	 *
	 * Portals contain a reference to the other Portal object
	 *
	 * @param id: portal ID
	 * @param x: x coordinate
	 * @param y: y coordinate
	 *
	 * @return Portal entity object
	 */
	private Portal getConnectedPortal(int id, int x, int y) {
		Portal portal = null;

		// The "portals" attribute will store the other Portal object
		// when the first portal of that ID is created
		//
		// The subsequent creation of that portal's ID
		// will fetch the existing reference to the other portal
		// instead of creating a new portal object

		// Portal already exists (2nd creation of the Portal ID)
		if (portals.containsKey(id)) {
			portal = portals.get(id);

			// (1st creation of the Portal ID) Loop through the entities and find the other portal
		} else
			for (int i = 0; i < entities.length(); i++) {
				JSONObject entity = entities.getJSONObject(i);

				if (entity.getString("type").equals("portal")) {
					int xCoord = entity.getInt("x"), yCoord = entity.getInt("y");

					// Must NOT be the same Portal entity (needs to be different positions)
					// And must have the same portal ID
					if (!(xCoord == x && yCoord == y) && entity.getInt("id") == id) {
						// Create portal
						portal = new Portal(x, y);

						// Create its corresponding portal
						Portal destinationPortal = new Portal(xCoord, yCoord);

						// Connect the portals
						portal.setConnection(destinationPortal);
						destinationPortal.setConnection(portal);

						// Store the corresponding portal so it can be fetched
						// at a subsequent call to portal for that ID
						portals.put(id, destinationPortal);
					}
				}
			}

		// Return portal, must not be null
		assert portal != null : "Portal could not be loaded";
		return portal;
	}

	/**
	 * Bind the inventory slots to the image view on the UI
	 *
	 * @param items: inventory object
	 * @param type : Item type to be binded
	 */
	private void bindItemSlots(Inventory items, ItemTypes type) {
		ImageView view = itemImages.get(type); // get image view for that item

		AnchorPane itemSlot = new AnchorPane();

		// set dimensions and add to item slots
		view.setVisible(false);
		view.setLayoutX(12.5);
		view.setLayoutY(12.5);
		itemSlot.getChildren().add(view);

		// durability counter on item slot
		Text durabilityCounter = new Text("0");
		durabilityCounter.setLayoutX(32.5);
		durabilityCounter.setLayoutY(47.5);
		durabilityCounter.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1.5px;");
		durabilityCounter.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
		durabilityCounter.setVisible(false);
		itemSlot.getChildren().add(durabilityCounter);

		// Bind the inventory "hasItem" boolean property to the visibility property
		// If the inventory doesn't have the item anymore, set invisible on item slot
		items.getItemProperty(type).bindBidirectional(view.visibleProperty());

		// Remove the durability counter on the item slot if the item durability reaches ZERO
		// Item slot should appear blank when an item is depleted and removed
		items.getDurabilityProperty(type).addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
				ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				assert newValue.intValue() >= 0 : "Bad durability value";
				if (newValue.intValue() == 0) {
					durabilityCounter.setVisible(false); // If zero, set invisible
				} else {
					durabilityCounter.setVisible(true); // Else set visible
				}
				// Change the counter to reflect the new durability
				durabilityCounter.setText(Integer.toString(newValue.intValue()));
			}
		});
		itemViews.put(type, itemSlot); // Add the anchorpane to the attribute so it can be set on UI
	}

	/**
	 * Bind the score counter to track how many treasures are picked up
	 *
	 * @param items: inventory object
	 */
	private void bindScoreCounter(Inventory items) {
		// style
		scoreCounter = new Text();
		scoreCounter.setLayoutX(20);
		scoreCounter.setLayoutY(20);
		scoreCounter.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		// When the score counter property in Inventory changes, convert it to a string and update
		// Text
		scoreCounter.textProperty().bindBidirectional(
			items.getPointsProperty(), new NumberStringConverter());
	}

	/**
	 * Getter: score counter
	 *
	 * used to add to the UI in the controller
	 *
	 * @return score counter Text object
	 */
	public Text getScoreCounter() {
		return scoreCounter;
	}

	/**
	 * From starter code
	 *
	 * Registers the entity to move on the GridPane
	 * when their coordinates are changed
	 *
	 * @param entity: entity object
	 * @param node : the gridpane imageview object
	 * @param x    : x coordinate
	 * @param y    : y coordinate
	 */
	protected void trackPosition(Entity entity, Node node, int x, int y) {
		mapGrid.add(node, x, y);
		entity.getXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
				ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				GridPane.setColumnIndex(node, newValue.intValue());
			}
		});
		entity.getYProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
				ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				GridPane.setRowIndex(node, newValue.intValue());
			}
		});
	}

	/**
	 * Create grid pane from the dungeon's size and sets it to the attribute
	 * so it can be returned to the dungeon controller
	 *
	 * @param dungeon: Map object
	 * @param size: Size of the screen (always 600)
	 */
	private void initialiseGrid(Map dungeon, double size) {
		int mapHeight = dungeon.getHeight(), mapWidth = dungeon.getWidth();

		// Size of the grid pane tiles is the window size divided by longest side
		// Then add a small offset to remove empty lines inbetween the tiles
		TILE_SIZE = size / (mapHeight < mapWidth ? mapWidth : mapHeight) + 0.5;

		// Center the gridpane in the middle of the 600 x 600 view
		if (mapHeight > mapWidth) {
			mapGrid.setLayoutX((size - TILE_SIZE * mapWidth) / 2);
		} else if (mapWidth > mapHeight) {
			mapGrid.setLayoutY((size - TILE_SIZE * mapHeight) / 2);
		}

		// add empty columns
		for (int col = 0; col < mapWidth; col++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPrefWidth(TILE_SIZE - 0.5);
			mapGrid.getColumnConstraints().add(cc);
		}

		// add empty rows
		for (int row = 0; row < mapHeight; row++) {
			RowConstraints rc = new RowConstraints();
			rc.setPrefHeight(TILE_SIZE - 0.5);
			mapGrid.getRowConstraints().add(rc);
		}

		// set default grass tiles on the entire grid pane
		Image ground = new Image(getClass().getResourceAsStream("/images/sprites/grass.png"),
			TILE_SIZE, TILE_SIZE, true, true);
		for (int row = 0; row < mapHeight; row++) {
			for (int col = 0; col < mapWidth; col++) {
				mapGrid.add(new ImageView(ground), col, row);
			}
		}
	}

	// Abstract methods used in DungeonEntityBuilder

	public abstract Player onLoad(Player player);

	public abstract Enemy onLoad(Enemy enemy);

	public abstract Boulder onLoad(Boulder boulder);

	public abstract FloorSwitch onLoad(FloorSwitch floor);

	public abstract Exit onLoad(Exit exit);

	public abstract BarrierEntity onLoad(Portal portal);

	public abstract BarrierEntity onLoad(Wall wall);

	public abstract BarrierEntity onLoad(Door door);

	public abstract Item onLoad(Key key);

	public abstract Item onLoad(Sword sword);

	public abstract Item onLoad(Potion potion);

	public abstract Item onLoad(Treasure treasure);

	/**
	 * Master command called from the Dungeon Controller to set up everything
	 *
	 * @param size: size of the window (600)
	 *
	 * @return The initialised grid pane with all entities registered
	 */
	public GridPane create(double size) {
		dungeon = loadDungeon();
		initialiseGrid(dungeon, size);
		createEntities(dungeon);
		return mapGrid;
	}

	/**
	 * Getter: Map object of the level
	 *
	 * @return Map
	 */
	public Map getDungeon() {
		return dungeon;
	}

	/**
	 * Getter: returns the item slots to put on the UI
	 *
	 * @param type: which item (key, sword, potion)
	 * @return The pane to add to the UI
	 */
	public AnchorPane getItemSlotView(ItemTypes type) {
		return itemViews.get(type);
	}
}
