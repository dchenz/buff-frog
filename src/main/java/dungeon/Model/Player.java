package dungeon.Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

import dungeon.Model.MapObjects.RotatableEntity;
import dungeon.Model.MapObjects.MapItems.ItemTypes;
import dungeon.Model.MapObjects.Killable;
import dungeon.Model.MapObjects.MapItems.Item;
import dungeon.Model.MapObjects.MapItems.Treasure;

/**
 * Player.java
 * 
 * The player object
 * 
 * Supposed to be rotatable with multiple views
 * but never had a chance to complete that 
 * 
 *
 */

public class Player extends RotatableEntity implements Killable {
	private BooleanProperty isAlive; // is alive (listened by the Map)
	private Inventory items;
	
	// normal and invincible views
	private Image normalView, invincibleView;
	
	public Player(Inventory inventory, int x, int y) {
		super(x, y);
		isAlive = new SimpleBooleanProperty(true);
		items = inventory;
	}
	
	// inventory methods to get items, use them and check if holding them
	
	public void pickupItem(ItemTypes type, Item item) {
		items.addItem(type, item);
	}
	
	public void pickupTreasure(Treasure treasure) {
		items.addItem(treasure);
	}
	
	public void useItem(ItemTypes type) {
		items.useItem(type);
	}
	
	public boolean hasItem(ItemTypes type) {
		return items.hasItem(type);
	}
	
	public void removeItem(ItemTypes type) {
		items.removeItem(type);
	}
	
	// This method is only called by the Door trigger() method
	// after checking if the player is holding the key is TRUE
	public int getKeyID() {
		return items.getKeyID();
	}


	// player dies
	@Override
	public void die() {
		isAlive.set(false);
		setVisibility(false);
	}
	
	// getters
	
	public boolean isAlive() {
		return isAlive.get();
	}
	
	public Inventory getInventory() {
		return items;
	}
	
	public BooleanProperty getStateProperty() {
		return isAlive;
	}
	
	
	/**
	 * Load views and rotate the player when they move
	 * 
	 * However didn't get to implement this feature
	 * 
	 */
	public void loadViews() {
		normalView = getImage();
		invincibleView = new Image("file:images/sprites/invincible_player.gif", 
				normalView.getWidth(), normalView.getHeight(), true, true);
		
		items.getItemProperty(ItemTypes.POTION).addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue.booleanValue() == true) {
					changeImage(invincibleView);
				} else {
					changeImage(normalView);
				}
			}
		});
	}
	
}
