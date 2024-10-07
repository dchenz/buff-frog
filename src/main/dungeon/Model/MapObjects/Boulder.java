package dungeon.Model.MapObjects;

/**
 * Trigger will return TRUE if the player can move onto the tile
 * else it will return FALSE and be handled in the Map class
 * 
 *
 */

public class Boulder extends Entity implements MoveableEntity {
	
	public Boulder(int x, int y) {
		super(x, y);
	}
	
	public boolean trigger() {
		// player movement is handled in Map, so return true for now
		return true;
	}
	
	// move boulder
	
	@Override
	public void moveUp() {
        getYProperty().set(getYPosition() - 1);
    }
    
	@Override
    public void moveDown() {
        getYProperty().set(getYPosition() + 1);
    }

	@Override
    public void moveLeft() {
        getXProperty().set(getXPosition() - 1);
    }
	
	@Override
    public void moveRight() {
        getXProperty().set(getXPosition() + 1);
    }
	
}
