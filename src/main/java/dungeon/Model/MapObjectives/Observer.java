package dungeon.Model.MapObjectives;

/**
 *
 * Objective observer
 *
 * The objective must listen for changes in the completed amount
 *
 * These implemented methods will be called in the relevant entity trigger() methods
 *
 *
 */

public interface Observer {
	public void increment(); // increase in completed amount
	public void decrement(); // decrease in completed amount (eg, floor switches with no boulder)
}
