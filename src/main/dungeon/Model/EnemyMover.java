package dungeon.Model;

import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.Random;

import dungeon.Model.MapObjects.*;

/**
 * EnemyMover.java
 * 
 * Builds a directed graph of the Map
 * and finds the Dijkstra shortest path
 * from the enemy to the player
 * 
 * If enemy is not following, move randomly
 * 
 * Design Pattern: Builder pattern
 * 
 *
 */

public class EnemyMover {
	
	// map entities
	private HashMap<Coordinate, BarrierEntity> obstacles;
	private HashMap<Coordinate, Portal> portals;
	private HashMap<Coordinate, Boulder> boulders;
	
	// graph
	private SimpleDirectedGraph<Coordinate, DefaultEdge> graph;
	
	// map dimensions and player's coordinates (updates as player moves)
	private int mapHeight, mapWidth;
	private IntegerProperty playerX, playerY;
	
	// constructor with map dimensions
	public EnemyMover(int height, int width) {
		mapHeight = height;
		mapWidth = width;
		playerX = new SimpleIntegerProperty();
		playerY = new SimpleIntegerProperty();
	}
	
	/**
	 * Sets obstacles, Portal/Wall/Doors
	 * 
	 * Separates Portals into its own HashMap
	 * 
	 * @param barriers: Portal/Wall/Door
	 * @return
	 */
	public EnemyMover setObstacles(HashMap<Coordinate, BarrierEntity> barriers) {
		obstacles = new HashMap<Coordinate, BarrierEntity>(barriers);
		portals = new HashMap<Coordinate, Portal>();
		for (Coordinate coordinate : barriers.keySet()) {
			if (obstacles.get(coordinate) instanceof Portal) {
				portals.put(coordinate, (Portal) obstacles.remove(coordinate));
			}
		}
		return this;
	}
	
	/**
	 * Sets boulders for the enemy to avoid
	 * 
	 * @param boulders: boulders on the map
	 * @return
	 */
	public EnemyMover setBoulders(HashMap<Coordinate, Boulder> boulders) {
		this.boulders = boulders;
		return this;
	}

	/**
	 * Master method to move the enemy
	 * 
	 * Checks if enemy is following the player
	 * 
	 * @param enemy: Enemy object to move
	 * 
	 * @return TRUE if enemy has moved, FALSE if didn't move
	 */
	public boolean move(Enemy enemy) {
		if (!enemy.isFollowing()) {
			return moveAwayFromPlayer(enemy);
		} else {
			return moveTowardsPlayer(enemy);
		}
	}
	
	/**
	 * Finds a move towards the player that is shortest path
	 * 
	 * @param enemy: enemy object
	 * @return TRUE if moved, else FALSE
	 */
	private boolean moveTowardsPlayer(Enemy enemy) {
		
		// build the directed graph for the map using the obstacles registered and player position
		List<Coordinate> path = buildGraph(enemy.getPosition(), new Coordinate(playerX.get(), playerY.get()));
		
		// If a path exists and is greater than 1 vertex long (Path contains enemy current position)
		if (path != null && path.size() > 1) {
			
			// Get coordinates for the tiles
			Coordinate enemyCurrentTile = path.get(0), enemyTargetTile = path.get(1);
			
			// Find which direction it should move based on their relative positions from each other
			if (enemyTargetTile.equals(enemyCurrentTile.right())) {
				enemy.moveRight();
			} else if (enemyTargetTile.equals(enemyCurrentTile.left())) {
				enemy.moveLeft();
			} else if (enemyTargetTile.equals(enemyCurrentTile.down())) {
				enemy.moveDown();
			} else if (enemyTargetTile.equals(enemyCurrentTile.up())) {
				enemy.moveUp();
			} else {
				// If the path is greater than 1 vertex but is not up, down, left nor right,
				// then the enemy must have entered a portal
				//
				// Set enemy position to the tile of the destination portal
				enemy.setPosition(enemyTargetTile); 
			}
			return true; // Enemy moved
		}
		return false; // No path, enemy didn't move
	}
	
	/**
	 * Enemy is not following the player and will move randomly
	 * 
	 * @param enemy: enemy object
	 * 
	 * @return Did the enemy move, TRUE if yes else FALSE
	 */
	private boolean moveAwayFromPlayer(Enemy enemy) {
		Coordinate curr = enemy.getPosition();
		
		// Randomly select a move and repeat until:
			// It finds a tile it can move into (attempts will increment tile key)
			// If no tile (attemppts == 4), return false because it cannot move
		
		int move = new Random().nextInt(4), attempts = 0;
		boolean moved = true;
		
		do {
			moved = true;
			
			if (move == 0 && graph.containsEdge(curr, curr.up())) {
				enemy.moveUp();
			} else if (move == 1 && graph.containsEdge(curr, curr.down())) {
				enemy.moveDown();
			} else if (move == 2 && graph.containsEdge(curr, curr.left())) {
				enemy.moveLeft();
			} else if (move == 3 && graph.containsEdge(curr, curr.right())) {
				enemy.moveRight();
			} else {
				move = (move + 1) % 4;
				attempts++;
				moved = false;
			}
		} while (!moved && attempts < 4); // while has not moved and attempts < 4 (increment tile key)
		
		return attempts != 4; // did the enemy break from the loop with all attempts exhausted? 
	}
	
	
	/**
	 * Build graph from obstacles on the map
	 * 
	 * @param enemyPosition: enemy coordinate
	 * @param playerCoordinate: player coordinate
	 * 
	 * @return List of coordinate vertices of the shortest dijkstra path from enemy to player
	 */
	private List<Coordinate> buildGraph(Coordinate enemyPosition, Coordinate playerCoordinate) {
		graph = new SimpleDirectedGraph<Coordinate, DefaultEdge>(DefaultEdge.class);
		
		// add vertices, edges
		createGraphVertices();
		createGraphEdges();
		
		// get shortest path
		GraphPath<Coordinate, DefaultEdge> shortest = DijkstraShortestPath
				.findPathBetween(graph, enemyPosition, playerCoordinate
		);
		
		// no path exists
		if (shortest == null) {
			return null;
		}
		return shortest.getVertexList(); // get list of vertices in the path
	}
	
	/**
	 * Create vertices
	 * 
	 * All tiles are a vertex in the graph
	 * 
	 */
	private void createGraphVertices() {
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				graph.addVertex(new Coordinate(x, y));
			}
		}
	}
	
	/**
	 * Create edges
	 * 
	 * For every tile (vertex) check the 4 neighbouring tiles
	 * and add an edge iff the enemy can move onto that tile
	 * from its current position
	 * 
	 */
	private void createGraphEdges() {
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				Coordinate coordinate = new Coordinate(x, y);
				// Check if the current tile can have an enemy on it
				if (tileAccessible(coordinate)) { 
					// Check and join it to its neighbour tiles
					joinNeighbourTiles(coordinate);
				}
				
			}
		}
	}
	
	/**
	 * Check neighbour tiles and create edges
	 * 
	 * Only add the edge if it does not go out of bounds of the map
	 * 
	 * @param src: tile to add all its neighbours as edges
	 */
	private void joinNeighbourTiles(Coordinate src) {
		
		if (!src.atLeft()) {
			createEdge(src, new Coordinate(src.left()));
		}
		if (!src.atRight(mapWidth)) {
			createEdge(src, new Coordinate(src.right()));
		}
		if (!src.atTop()) {
			createEdge(src, new Coordinate(src.up()));
		}
		if (!src.atBottom(mapHeight)) {
			createEdge(src, new Coordinate(src.down()));
		}
		
	}
	
	/**
	 * Create the edge between current and neighbour tile
	 * 
	 * @param src: current tile
	 * @param dest: neighbour tile
	 */
	private void createEdge(Coordinate src, Coordinate dest) {
		if (!tileAccessible(dest)) {
			return; // If the neighbour tile is cannot have an enemy on it, return
		}
		// If the neighbour tile is a portal,
		// then the current tile actually has an edge to the corresponding portal instead
		//
		// Hence enemies will instantly travel to the other portal when stepping into one
		if (portals.containsKey(dest)) {
			Portal portal = portals.get(dest);
			dest = new Coordinate(
					portal.getDestinationXPosition(), 
					portal.getDestinationYPosition()
			);
		}
		graph.addEdge(src, dest); // add edge to graph
	}
	
	/**
	 * Check if the tile can have an enemy on it
	 * 
	 * @param coordinate: tile position (vertex)
	 * 
	 * @return TRUE iff enemies can go on it, else FALSE
	 */
	private boolean tileAccessible(Coordinate coordinate) {
		// Check if there is a WALL or DOOR
		if (obstacles.containsKey(coordinate)) {
			BarrierEntity obj = obstacles.get(coordinate);
			
			// WALL will always return FALSE, DOOR will return "isOpen"
			return obj.canMoveThrough();
		}
		// If no WALL or DOOR, check if there is a boulder (enemies cannot push boulders)
		return !boulders.containsKey(coordinate);
	}
	
	// getters for player position 
	
	public IntegerProperty getPlayerXProperty() {
		return playerX;
	}
	
	public IntegerProperty getPlayerYProperty() {
		return playerY;
	}
	
}
