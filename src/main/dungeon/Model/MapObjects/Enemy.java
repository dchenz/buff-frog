package dungeon.Model.MapObjects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import dungeon.Model.MapObjectives.Objective;
import dungeon.Model.MapObjects.MapItems.ItemTypes;
import dungeon.Model.Player;

/**
 * Trigger will return TRUE if the player has killed the enemy
 * else it will return FALSE if the player dies and be handled in Map class
 * 
 *
 */

public class Enemy extends RotatableEntity implements Killable {
	
	private BooleanProperty isFollowing;
	
	public Enemy(int x, int y) {
		super(x, y);
		isFollowing = new SimpleBooleanProperty(true);
	}
	
	public boolean trigger(Player player, Objective task) {
		if (isVisible()) { // enemy is visible
			
			// potion kills do not deplete sword if player has both at the same time
			
			// player has potion
			if (player.hasItem(ItemTypes.POTION)) {
				if (task != null) { // update objective if exists
					task.increment();
				}
				this.die(); // enemy dies, do not deplete sword if it has it too
			} else if (player.hasItem(ItemTypes.SWORD)) { // player has sword
				player.useItem(ItemTypes.SWORD); // use sword
				if (task != null) { // update objective if exists
					task.increment();
				}
				this.die(); // enemy dies
			} else {
				player.die(); // if not potion or sword, player dies and level ends
			}
		}
		return isVisible(); // needed for when the enemy dies and needs to be removed from Map
	}

	// kill enemy and set invisible
	@Override
	public void die() {
		setVisibility(false);
	}
	
	// getter used in DungeonBuilder to bidn following behaviour to whether player has a potion
	public BooleanProperty getFollowingProperty() {
		return isFollowing;
	}
	
	// getter used in enemyMover
	public boolean isFollowing() {
		return getFollowingProperty().get();
	}
	
}
