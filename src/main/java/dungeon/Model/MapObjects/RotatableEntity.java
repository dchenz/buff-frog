package dungeon.Model.MapObjects;

import javafx.scene.image.Image;

/**
 * RotatableEntity
 * 
 * Never had a chance to implement player and entity rotation
 * when they move and change their sprites 
 * 
 *
 */

public abstract class RotatableEntity extends Entity implements MoveableEntity {
	private Image[] views;
	private int facing;
	
	public RotatableEntity(int x, int y) {
		super(x, y);
		views = new Image[4];
		facing = 0;
	}
	
	public void rotateLeft() {
		if (facing == 0) {
			facing = 3;
		} else {
			facing--;
		}
		changeImage(views[facing]);
	}
	
	public void rotateRight() {
		if (facing == 3) {
			facing = 0;
		} else {
			facing++;
		}
		changeImage(views[facing]);
	}
	
	public void setRotationalViews(int where, Image view) {
		assert where >= 0 && where < 4 : "Invalid view direction";
		views[where] = view;
	}
	
	public int getFacing() {
		return facing;
	}
	
	// Implements the methods in MoveableEntity for PLAYER and ENEMY
	
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
