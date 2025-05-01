package dungeon.Model;

import java.util.HashMap;

import dungeon.Model.MapObjects.MapItems.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Inventory.java
 * 
 * Holds items for the player
 * 
 *
 */

public class Inventory {
	// The item object
	private HashMap<ItemTypes, Item> items;
	
	// "hasItem" property (used for UI)
	private HashMap<ItemTypes, BooleanProperty> hasItem;
	
	// item durability property (used for UI)
	private HashMap<ItemTypes, IntegerProperty> itemDurability;
	
	// score (used for UI)
	private IntegerProperty points;

	public Inventory() {
		items = new HashMap<ItemTypes, Item>();
		hasItem = new HashMap<ItemTypes, BooleanProperty>();
		itemDurability = new HashMap<ItemTypes, IntegerProperty>();
		// set for all types of items
		for (ItemTypes type : ItemTypes.values()) {
			items.put(type, null);
			hasItem.put(type, new SimpleBooleanProperty(false));// does not have item initially
			itemDurability.put(type, new SimpleIntegerProperty(0)); // no durability
		}
		points = new SimpleIntegerProperty(0); // start at 0 points
	}
	
	/**
	 * Pick up item that isn't treasure
	 * 
	 * @param type: enum item type
	 * @param i: item object
	 */
	public void addItem(ItemTypes type, Item i) {
		items.put(type, i); // add item
		getItemProperty(type).set(true); // set has item to true
		getDurabilityProperty(type).set(i.getUsesLeft()); // set durability counter
		getDurabilityProperty(type).bind(i.getUsesLeftProperty()); // bind it to the item's durability
	}
	
	/**
	 * Pick up treasure (not stored in inventory)
	 * 
	 * @param treasure: treasure object
	 */
	public void addItem(Treasure treasure) {
		// Increase points by the treasure's value
		points.set(points.get() + treasure.value());
	}
	
	/**
	 * Remove item from inventory
	 * 
	 * @param type: enum itme type
	 */
	public void removeItem(ItemTypes type) {
		items.put(type, null); // set to null 
		getItemProperty(type).set(false); // does not have item, FALSE
		getDurabilityProperty(type).unbind(); // unbind the durability counter
		getDurabilityProperty(type).set(0); // set counter to zero
	}
	
	/**
	 * Has item type
	 * 
	 * @param type: enum item type
	 * @return TRUE iff has item, else FALSE
	 */
	public boolean hasItem(ItemTypes type) {
		return getItemProperty(type).get(); // return "has item"
	}
	
	/**
	 * Uses the item 
	 * 
	 * @param type: enum item type
	 * @return Number of uses remaining after using the item (int >= 0)
	 */
	public int useItem(ItemTypes type) {
		Item item = items.get(type); // get the item 
		item.deplete(); // deplete its uses by 1
		if (item.getUsesLeft() == 0) { // if no uses left
			removeItem(type); // remove the item from inventory
			assert hasItem(type) == false : "Item not actually removed"; // Assert: item is gone
		}
		// return number of uses left after using it (int >= 0)
		return item.getUsesLeft();
	}
	
	/**
	 * Getter: key ID
	 * 
	 * This method is only called after checking if inventory has a key
	 * by the caller inside Door's trigger() method
	 * 
	 * @return the integer ID of the key if it holds it
	 */
	public int getKeyID() {
		Key key = (Key) items.get(ItemTypes.KEY);
		return key.getID();
	}
	
	// Property getter to bind inside DungeonBuilder for the UI
	
	public BooleanProperty getItemProperty(ItemTypes type) {
		return hasItem.get(type);
	}
	
	public IntegerProperty getDurabilityProperty(ItemTypes type) {
		return itemDurability.get(type);
	}
	
	public IntegerProperty getPointsProperty() {
		return points;
	}
	
}