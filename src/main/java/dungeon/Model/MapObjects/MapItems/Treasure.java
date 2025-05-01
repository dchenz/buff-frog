package dungeon.Model.MapObjects.MapItems;

import dungeon.Model.MapObjectives.Objective;
import dungeon.Model.Player;

/**
 *
 * Can only hold one KEY/SWORD at a time
 * and cannot swap with another one
 *
 * Picking up another ption will replace current effect
 *
 *
 */

public class Treasure extends Item {
	private int value = 100; // fixed value of 100

	public Treasure(int x, int y) {
		super(x, y);
	}

	@Override
	public void trigger(Player player, Objective task) {
		if (isVisible()) { // if not picked up
			// pick up and update observer
			player.pickupTreasure(this);
			setVisibility(false);
			if (task != null) { // update objective if exists
				task.increment();
			}
		}
	}

	// getter for its value
	public int value() {
		return value;
	}
}
