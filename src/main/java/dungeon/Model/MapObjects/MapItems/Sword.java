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

public class Sword extends Item {
	private final static int DURABILITY = 5;

	public Sword(int x, int y) {
		super(DURABILITY, x, y); // fixed durability of 5
	}

	@Override
	public void trigger(Player player, Objective task) {
		if (isVisible()) { // if not yet picked up
			// If not holding a sword, pick up sword
			if (!player.hasItem(ItemTypes.SWORD)) {
				collect(player, ItemTypes.SWORD);
			}
		}
	}
}
