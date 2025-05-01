package dungeon.Model.MapObjects;

/**
 * Entities that can move:
 * 
 * PLAYER
 * BOULDER
 * ENEMY
 * 
 *
 */

public interface MoveableEntity {
	
	public void moveUp();
	
	public void moveDown();
	
	public void moveLeft();
	
	public void moveRight();
	
}
