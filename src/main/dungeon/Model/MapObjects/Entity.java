package dungeon.Model.MapObjects;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import dungeon.Model.Coordinate;

/**
 * Controls the coordinates and image view of all entities on the map
 * 
 * Extends into RotatableEntity, Item, BarrierEntity
 * 
 *
 */

public abstract class Entity {
	private IntegerProperty xCoord, yCoord;
	private ImageView image;
	
	public Entity(int x, int y) {
		xCoord = new SimpleIntegerProperty(x);
		yCoord = new SimpleIntegerProperty(y);
	}
	
	// getters to bind with grid pane
	
	public IntegerProperty getXProperty() {
		return xCoord;
	}
	
	public IntegerProperty getYProperty() {
		return yCoord;
	}
	
	// getters for coordinate position
	
	public int getXPosition() {
		return xCoord.get();
	}
	
	public int getYPosition() {
		return yCoord.get();
	}
	
	// getter for actual coordinate used in Map and enemyMover
	
	public Coordinate getPosition() {
		return new Coordinate(getXPosition(), getYPosition());
	}
	
	// setter for entity position
	
	public void setPosition(int x, int y) {
		getXProperty().set(x);
		getYProperty().set(y);
	}
	
	public void setPosition(Coordinate coordinate) {
		setPosition(coordinate.X(), coordinate.Y());
	}
	
	// get image
	
	protected Image getImage() {
		return image.getImage();
	}
	
	// set image
	
	public void setImageView(ImageView entityImage) {
		image = entityImage;
	}
	
	// change image (used in rotatableEntity but never got to implement)
	
	protected void changeImage(Image view) {
		image.setImage(view);
	}
	
	// is visible getter (used in trigger methods)
	
	protected boolean isVisible() {
		return image.isVisible();
	}
	
	// set visibility (used to kill entities or remove them)
	
	protected void setVisibility(boolean onMap) {
		image.setVisible(onMap);
	}
	
}
