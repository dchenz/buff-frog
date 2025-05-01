package dungeon.Model.MapObjects.MapItems;

import dungeon.Model.MapObjectives.Objective;
import dungeon.Model.Player;
import java.util.Random;

/**
 *
 * Can only hold one KEY/SWORD at a time
 * and cannot swap with another one
 *
 * Picking up another ption will replace current effect
 *
 *
 */

public class Potion extends Item {
	public Potion(int x, int y) {
		// set effect durability to random amount between 3 and 8
		// super(new Random().nextInt(6) + 3, x, y);
		super(8, x, y);
		// removed random effect time
	}

	@Override
	public void trigger(Player player, Objective task) {
		if (isVisible()) { // if not yet picked up
			if (player.hasItem(ItemTypes.POTION)) {
				player.removeItem(ItemTypes.POTION);
			}
			// apply the effect to replace any current effect
			collect(player, ItemTypes.POTION);
		}
	}
}
