package dungeon.Model;

import dungeon.Model.MapObjectives.CompletableObjective;
import dungeon.Model.MapObjectives.ObjectiveTypes;
import dungeon.Model.MapObjects.BarrierEntity;
import dungeon.Model.MapObjects.Boulder;
import dungeon.Model.MapObjects.Enemy;
import dungeon.Model.MapObjects.Exit;
import dungeon.Model.MapObjects.FloorSwitch;
import dungeon.Model.MapObjects.MapItems.Item;
import dungeon.Model.MapObjects.MapItems.ItemTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Map (also referred to as Levels)
 *
 * Used to store the entities of every individual level
 *
 * mapID:      The ID of the level
 * height:     The height of the map grid
 * width:      The width of the map grid
 * enemies:    List of all enemies on the level
 * obstacles:  List of all barriers on the level (door, wall, portal)
 * boulders:   List of all boulders on the level
 * switches:   List of all floor switches on the level
 * items:      List of all items that can be collected on the level
 *
 *
 */
public class Map {
	private int mapID;
	private int height, width;
	private ArrayList<Enemy> enemies;
	private HashMap<Coordinate, BarrierEntity> obstacles;
	private HashMap<Coordinate, Boulder> boulders;
	private HashMap<Coordinate, FloorSwitch> switches;
	private HashMap<Coordinate, Exit> exits;
	private HashMap<Coordinate, Item> items;
	private ObjectiveManager tasks;
	private Player user;
	private IntegerProperty gameState;
	private EnemyMover mover;

	/**
	 * Constructor
	 *
	 * Creates a blank level of the given size
	 *
	 * @param id:      The map ID
	 * @param height:  Height of the map
	 * @param width:   Width of the map
	 */
	public Map(int id, int height, int width, ObjectiveManager tasks) {
		this.height = height;
		this.width = width;
		enemies = new ArrayList<Enemy>();
		obstacles = new HashMap<Coordinate, BarrierEntity>();
		boulders = new HashMap<Coordinate, Boulder>();
		switches = new HashMap<Coordinate, FloorSwitch>();
		exits = new HashMap<Coordinate, Exit>();
		items = new HashMap<Coordinate, Item>();
		mapID = id;
		gameState = new SimpleIntegerProperty(0);
		tasks.setStatusProperty(gameState);
		this.tasks = tasks;
	}

	/**
	 * Adds an Enemy object to the enemies list
	 *
	 * @param enemy: Enemy object
	 */
	public void add(Enemy enemy) {
		enemies.add(enemy);
	}

	/**
	 * Adds any BarrierEntity object to the obstacles list
	 *
	 * @param entity: A BarrierEntity which can be (Wall, Door, Portal)
	 */
	public void add(BarrierEntity entity, int x, int y) {
		obstacles.put(new Coordinate(x, y), entity);
	}

	/**
	 * Adds an Item object to the items list
	 *
	 * @param item: Item object
	 */
	public void add(Item item, int x, int y) {
		items.put(new Coordinate(x, y), item);
	}

	/**
	 * Adds a Boulder object to the boulders list
	 *
	 * @param boulder: Boulder object
	 */
	public void add(Boulder boulder, int x, int y) {
		Coordinate coordinate = new Coordinate(x, y);
		boulders.put(coordinate, boulder);

		if (switches.containsKey(coordinate)) {
			FloorSwitch fswitch = switches.get(coordinate);
			fswitch.trigger(true, tasks.getTask(ObjectiveTypes.FLOOR_SWITCH));
		}
	}

	/**
	 * Adds a FloorSwitch object to the switches list
	 *
	 * @param fswitch: FloorSwitch object
	 */
	public void add(FloorSwitch fswitch, int x, int y) {
		Coordinate coordinate = new Coordinate(x, y);
		switches.put(coordinate, fswitch);

		if (boulders.containsKey(coordinate)) {
			fswitch.trigger(true, tasks.getTask(ObjectiveTypes.FLOOR_SWITCH));
		}
	}

	/**
	 * Adds an Exit object to the exits list
	 *
	 * @param exit: Exit object
	 */
	public void add(Exit exit, int x, int y) {
		exits.put(new Coordinate(x, y), exit);
	}

	/**
	 * Adds the player to the level
	 *
	 * @param player: The player object (only one per map)
	 */
	public void add(Player player) {
		user = player;
	}

	/**
	 * Load the enemy mover after all the entities are added
	 *
	 */
	public void loadEnemyMover() {
		mover = new EnemyMover(height, width);
		mover.getPlayerXProperty().bind(user.getXProperty()); // regsiter player position
		mover.getPlayerYProperty().bind(user.getYProperty());
		mover.setObstacles(obstacles).setBoulders(boulders); // set entities (builder pattern)
	}

	/**
	 * Moves the player (UP | DOWN | LEFT | RIGHT) based on a Direction enum value
	 *
	 * It checks the player's current position against the map dimensions
	 * to prevent the player from walking off the edge of the map
	 *
	 * activateTile() will stop the player from moving if they are blocked
	 * It will return TRUE iff the player can move into that tile
	 *
	 * @param where: An enum value of Direction.(UP | DOWN | LEFT | RIGHT)
	 */
	public void movePlayer(Direction where) {
		if (!user.isAlive() || gameState.get() != 0) {
			return;
		}

		Coordinate playerPosition = user.getPosition();

		switch (where) {
			case UP:
				if (!playerPosition.atTop()) { // Must not be at the first row
					if (activateTile(Direction.UP, playerPosition.up())) {
						user.moveUp();
						moveEnemies();
					}
				}
				break;
			case DOWN:
				if (!playerPosition.atBottom(height)) { // Must not be at the last row
					if (activateTile(Direction.DOWN, playerPosition.down())) {
						user.moveDown();
						moveEnemies();
					}
				}
				break;
			case LEFT:
				if (!playerPosition.atLeft()) { // Must not be at the first column
					if (activateTile(Direction.LEFT, playerPosition.left())) {
						user.moveLeft();
						moveEnemies();
					}
				}
				break;
			case RIGHT:
				if (!playerPosition.atRight(width)) { // Must not be at the last column
					if (activateTile(Direction.RIGHT, playerPosition.right())) {
						user.moveRight();
						moveEnemies();
					}
				}
				break;
			default:
				assert false : "Invalid move"; // Invalid move received
		}
	}

	/**
	 * Move the enemies one by one in the list of enemies
	 */
	private void moveEnemies() {
		Iterator<Enemy> enemyList = enemies.iterator();
		while (enemyList.hasNext()) {
			Enemy enemy = enemyList.next();

			boolean enemyMoved = mover.move(enemy); // has the enemy moved?

			// if the enemy moved and the user is in the same tile as enemy
			if (enemyMoved && user.getPosition().equals(enemy.getPosition())) {
				// trigger the enemy's kill player method and check for sword
				if (enemy.trigger(user, tasks.getTask(ObjectiveTypes.ENEMY))) {
					enemyList.remove(); // enemy trigger returns true if enemy dies
					// remove enemy from list if dead
				}
			}
		}
	}

	/**
	 * Triggers any entities that may be in the tile that the player wants to move
	 *
	 * It will trigger entities that can block the player movement first
	 * and it return FALSE if the player cannot move into that tile
	 *
	 * @param move: Enum value of direction (UP | DOWN | LEFT | RIGHT)
	 * @param x: X coordinate value of the tile the player wants to move
	 * @param y: Y coordinate value of the tile the player wants to move
	 *
	 * @return Boolean value, TRUE if player can move into the tile, FALSE otherwise
	 */
	private boolean activateTile(Direction move, Coordinate coordinate) {
		triggerExits(coordinate);

		boolean playerCanMove = true; // Initially can move

		// Unless blocked by an entity (Will always be false if any is false)
		playerCanMove &= triggerBarriers(coordinate);
		playerCanMove &= triggerBoulders(move, coordinate);

		// If player can't move, then no need to check for enemy or item in tile
		if (playerCanMove) {
			triggerEnemies(coordinate);

			if (user.hasItem(ItemTypes.POTION)) {
				user.useItem(ItemTypes.POTION);
			}

			triggerItems(coordinate);
		}

		return playerCanMove;
	}

	/**
	 * Triggers enemies in the tile the player wants to move
	 *
	 * Tiles can contain at most one enemy
	 *
	 * Enemy Trigger:
	 * - Checks if the player has a sword
	 *   - Kills enemy if yes
	 *   - Kills player if no
	 * - Updates the ENEMY objective upon enemy death
	 * - Returns TRUE if the enemy dies, otherwise FALSE
	 *
	 * @param coordinate: Coordinate of the tile the player wants to move
	 */
	private void triggerEnemies(Coordinate coordinate) {
		Enemy enemy = getEnemyAtPosition(coordinate);
		if (enemy != null) {
			if (enemy.trigger(user, tasks.getTask(ObjectiveTypes.ENEMY))) {
				enemies.remove(enemy);
			}
		}
	}

	/**
	 * Triggers items in the tile the player wants to move
	 *
	 * Tiles can contain at most one item
	 *
	 * Item Trigger:
	 * - KEY / SWORD:
	 *   - Checks if the player is already carrying the item
	 *     - Pick up item if no
	 *     - Otherwise, don't
	 * - POTION:
	 *   - Always pick up and replace the current potion
	 * - TREASURE:
	 *   - Collect points
	 *   - Update TREASURE objective
	 *
	 * @param coordinate: Coordinate of the tile the player wants to move
	 */
	private void triggerItems(Coordinate coordinate) {
		if (items.containsKey(coordinate)) {
			Item item = items.get(coordinate);
			item.trigger(user, tasks.getTask(ObjectiveTypes.TREASURE));
		}
	}

	/**
	 * Triggers walls, doors, portals in the tile the player wnats to move
	 *
	 * Tiles can contain at most one of either walls, doors or portals
	 *
	 * Barrier Trigger:
	 * - Returns TRUE iff the player can move onto that tile, otherwise FALSE
	 *  - WALL:
	 *    - Always returns FALSE
	 *  - DOOR:
	 *    - Checks if the player has a KEY of the same ID
	 *      - Return TRUE if they do
	 *      - Otherwise return FALSE
	 *  - PORTAL:
	 *    - Always returns FALSE (player cannot be on a portal tile)
	 *    - Moves the player to the connecting portal tile
	 *
	 * @param coordinate: Coordinate of the tile the player wants to move
	 *
	 * @return Boolean TRUE iff the player can move onto that tile, else FALSE
	 */
	private boolean triggerBarriers(Coordinate coordinate) {
		if (obstacles.containsKey(coordinate)) {
			BarrierEntity obstacle = obstacles.get(coordinate);
			return obstacle.trigger(user);
		}
		return true;
	}

	/**
	 * Triggers boulders in the tile the player wants to move
	 *
	 * Boulder Trigger:
	 *  - No internal trigger
	 *
	 * Mechanism:
	 *   1. Gets the coordinate of the tile where the boulder is to move
	 *   2. Check if the boulder can move into it
	 *     - Boulders cannot move outside of the map
	 *     - Boulders cannot move onto walls, through doors, into portals
	 *     - Boulders cannot move onto enemies
	 *     - Cannot push more than one boulder at a time
	 *   3. Return FALSE (block player) if any of the above is present
	 *   4. Otherwise, move the boulder in the respective direction
	 *   5. Remove the boulder from the list of boulders
	 *   6. Add the boulder with a new position key to the list
	 *
	 * @param move: An enum value of Direction.(UP | DOWN | LEFT | RIGHT)
	 * @param coordinate: Coordinate of the tile the player wants to move
	 *
	 * @return Boolean TRUE iff the player can push the boulder onto that tile, else FALSE
	 */
	private boolean triggerBoulders(Direction move, Coordinate coordinate) {
		if (boulders.containsKey(coordinate)) {
			Coordinate targetPosition = null;

			Boulder boulder = boulders.get(coordinate);
			switch (move) {
				case UP:
					targetPosition = new Coordinate(coordinate.up());
					if (boulderBlocked(targetPosition)) {
						return false;
					}
					boulder.moveUp();
					break;
				case DOWN:
					targetPosition = new Coordinate(coordinate.down());
					if (boulderBlocked(targetPosition)) {
						return false;
					}
					boulder.moveDown();
					break;
				case LEFT:
					targetPosition = new Coordinate(coordinate.left());
					if (boulderBlocked(targetPosition)) {
						return false;
					}
					boulder.moveLeft();
					break;
				case RIGHT:
					targetPosition = new Coordinate(coordinate.right());
					if (boulderBlocked(targetPosition)) {
						return false;
					}
					boulder.moveRight();
					break;
			}
			boulders.remove(coordinate);
			boulders.put(targetPosition, boulder);
			triggerSwitches(coordinate);
			triggerSwitches(targetPosition);
		}
		return true;
	}

	/**
	 * Checks if the boulder can move onto the given coordinate
	 *
	 * 1. Not outside the map
	 * 2. Not onto another boulder
	 * 3. Not onto a wall, through a door, or into a portal
	 * 4. Not onto an enemy
	 *
	 * @param targetPosition: Coordinate of the tile the player is to push the boulder onto
	 *
	 * @return Boolean TRUE iff the boulder is not obstructed by the above entities, else FALSE
	 */
	private boolean boulderBlocked(Coordinate targetPosition) {
		if (targetPosition.outside(height, width)) {
			return true;
		}
		if (boulders.containsKey(targetPosition)) {
			return true;
		}
		if (obstacles.containsKey(targetPosition)) {
			return true;
		}
		if (getEnemyAtPosition(targetPosition) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Fetch an enemy object using its coordinate
	 *
	 * @param coordinate: position of enemy
	 * @return
	 */
	private Enemy getEnemyAtPosition(Coordinate coordinate) {
		for (Enemy enemy : enemies) {
			if (coordinate.equals(enemy.getPosition())) {
				return enemy;
			}
		}
		return null;
	}

	/**
	 * Check if boulder was pushed off switch
	 *
	 * @param coordinate: tile the player wants to move into
	 */
	private void triggerSwitches(Coordinate coordinate) {
		if (switches.containsKey(coordinate)) {
			FloorSwitch fswitch = switches.get(coordinate);
			fswitch.trigger(
				boulders.containsKey(coordinate), tasks.getTask(ObjectiveTypes.FLOOR_SWITCH));
		}
	}

	/**
	 * Trigger exit
	 *
	 * Checks if root goal is complete
	 * after the Exit objective (if it exists) is completed
	 *
	 * If the root goal is not completed,
	 * then the player must not have completed another conjunction objective
	 * and will fail the level
	 *
	 * @param coordinate: position the player wants to move
	 */
	private void triggerExits(Coordinate coordinate) {
		if (exits.containsKey(coordinate)) {
			// check objectives, end level
			Exit exit = exits.get(coordinate);
			exit.trigger(this, tasks.getRootGoal(), tasks.getTask(ObjectiveTypes.EXIT));
		}
	}

	/**
	 * Getter - Map ID
	 *
	 * @return The map ID integer value
	 */
	public int getID() {
		return mapID;
	}

	/**
	 * Getter - Map Height
	 *
	 * @return The height dimension of the map
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Getter - Map Width
	 *
	 * @return The width dimension of the map
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the game state to 2 to successfully complete a level
	 *
	 * Game States:
	 *   0: Nothing, currently playing the level
	 *   1: Level failed
	 *   2: Level success
	 *
	 */
	public void levelSuccess() {
		gameState.set(2);
	}

	/**
	 * Sets the game state to 1 to fail the level
	 *
	 * Game States:
	 *   0: Nothing, currently playing the level
	 *   1: Level failed
	 *   2: Level success
	 *
	 */
	public void levelFailed() {
		gameState.set(1);
	}

	// getters

	public IntegerProperty getGameStatus() {
		return gameState;
	}

	public CompletableObjective getLevelObjective() {
		return tasks.getRootGoal();
	}

	public int getObjectiveRequirement(ObjectiveTypes type) {
		return tasks.getRequirement(type); // get how many required to complete the objective
	}

	public int getObjectiveQuantity() {
		return tasks.getObjectiveQty(); // get how many leaf objectives (int >= 1 && int <= 4)
	}
}
