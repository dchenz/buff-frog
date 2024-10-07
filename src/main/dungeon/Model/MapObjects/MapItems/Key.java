package dungeon.Model.MapObjects.MapItems;

import dungeon.Model.Player;
import dungeon.Model.MapObjectives.Objective;

/**
 * 
 * Can only hold one KEY/SWORD at a time
 * and cannot swap with another one
 * 
 * Picking up another ption will replace current effect
 * 
 *
 */

public class Key extends Item {
	private int id;
	
	public Key(int keyID, int x, int y) {
		super(x, y);
		id = keyID;
	}
	
	@Override
	public void trigger(Player player, Objective task) {
		if (isVisible()) { // if visible (not picked up)
			// If player is holding a key, do not pick it up
			if (!player.hasItem(ItemTypes.KEY)) {
				collect(player, ItemTypes.KEY);
			}
		}
	}
	
	public int getID() {
		return id;
	}
	
}
