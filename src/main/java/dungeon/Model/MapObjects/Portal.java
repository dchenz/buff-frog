package dungeon.Model.MapObjects;

import dungeon.Model.Player;

/**
 * Trigger will return TRUE if the player can move onto the tile
 * else it will return FALSE and be handled in the Map class
 * 
 *
 */

public class Portal extends BarrierEntity {
	
	private Portal destination; // reference to other portal
	
	public Portal(int x, int y) {
		super(x, y);
	}
	
	@Override
	public boolean trigger(Player player) {
		teleportEntity(player); // teleport
		return false; // player cannot step onto a portal and be on it (because they will be teleported)
	}
	
	// used in enemyMover
	@Override
	public boolean canMoveThrough() {
		return true; // enemies can move through portals
	}
	
	// set player position to the destination portal's position
	private void teleportEntity(Entity entity) {
		assert destination != null : "Cannot teleport to nowhere";
		assert destination.destination == this : "Portals are not linked properly";
		
		entity.setPosition(destination.getXPosition(), destination.getYPosition());
	}
	
	// used in DungeonBuilder to connect portals to each other
	public void setConnection(Portal other) {
		assert this != other : "Portal cannot connect to itself";
		assert destination == null : "Portal is already connected";
		assert other.destination == null || other.destination == this : 
			"Other portal is already connected to a different portal";
		
		destination = other;
	}
	
	// getters for destination portal position
	
	public int getDestinationXPosition() {
		return destination.getXPosition();
	}
	
	public int getDestinationYPosition() {
		return destination.getYPosition();
	}

}
