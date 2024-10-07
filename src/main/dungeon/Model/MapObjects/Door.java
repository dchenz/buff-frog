package dungeon.Model.MapObjects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import dungeon.Model.Player;
import dungeon.Model.MapObjects.MapItems.ItemTypes;

/**
 * Trigger will return TRUE if the player can move onto the tile
 * else it will return FALSE and be handled in the Map class
 * 
 *
 */

public class Door extends BarrierEntity {
	private int doorID; // door ID to maych with key
	private BooleanProperty isOpen; // isOpen prperty listened to by enemyMover and its image view
	
	public Door(int id, int x, int y) {
		super(x, y);
		doorID = id;
		isOpen = new SimpleBooleanProperty(false); // initially closed
	}
	
	@Override
	public boolean trigger(Player player) { 
		// if the door is closed
		if (isOpen.get() == false) {
			// and the player has a key of the same ID
			if (player.hasItem(ItemTypes.KEY) && player.getKeyID() == doorID) {
				isOpen.set(true); // open the door
				player.useItem(ItemTypes.KEY); // use the key
				
				return true; // can move through now open door
			}
			return false; // closed and no key
		}
		return true; // door is already open
	}
	
	// getter used in enemyMover
	@Override
	public boolean canMoveThrough() {
		return isOpen.get(); 
	}
	
	// getter used to bind door with image view
	public BooleanProperty getOpenProperty() {
		return isOpen;
	}
	
}
