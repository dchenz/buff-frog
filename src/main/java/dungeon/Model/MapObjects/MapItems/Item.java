package dungeon.Model.MapObjects.MapItems;

import dungeon.Model.MapObjectives.Objective;
import dungeon.Model.MapObjects.Entity;
import dungeon.Model.Player;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 *
 */

public abstract class Item extends Entity {
	private IntegerProperty usesLeft; // bound by inventory durability counter

	// constructor used by sword and potion
	public Item(int durability, int x, int y) {
		super(x, y);
		usesLeft = new SimpleIntegerProperty(durability);
	}

	// constructor used by key (only one use)
	public Item(int x, int y) {
		this(1, x, y);
	}

	// use the item and deplete by 1
	public void deplete() {
		assert usesLeft.get() > 0 : "No uses left";
		usesLeft.set(usesLeft.get() - 1);
	}

	// getter
	public int getUsesLeft() {
		return usesLeft.get();
	}

	// getter
	public IntegerProperty getUsesLeftProperty() {
		return usesLeft;
	}

	// pick it up
	protected void collect(Player player, ItemTypes type) {
		player.pickupItem(type, this);
		setVisibility(false);
	}

	public abstract void trigger(Player player, Objective task);
}
