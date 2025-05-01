package dungeon.Model.MapObjects;

import dungeon.Model.Player;

/**
 * Trigger will return TRUE if the player can move onto the tile
 * else it will return FALSE and be handled in the Map class
 *
 *
 */

public class Wall extends BarrierEntity {
	public Wall(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean trigger(Player player) {
		return false; // Always return false because cannot move through wall
	}

	// used in enemyMover
	@Override
	public boolean canMoveThrough() {
		return false; // enemy cannot move through wall
	}
}
