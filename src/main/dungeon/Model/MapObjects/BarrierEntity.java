package dungeon.Model.MapObjects;

import dungeon.Model.Player;

/**
 * Walls, Doors, Portals abstract class
 * 
 *
 */

public abstract class BarrierEntity extends Entity {
	public BarrierEntity(int x, int y) {
		super(x, y);
	}
	
	public abstract boolean trigger(Player player);
	
	// used in enemyMover
	public abstract boolean canMoveThrough();
}
