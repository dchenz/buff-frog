package dungeon.Model.MapObjects;

import dungeon.Model.MapObjectives.Objective;

/**

 *
 */

public class FloorSwitch extends Entity {
	boolean isSwitchedOn;
	
	public FloorSwitch(int x, int y) {
		super(x, y);
	}
	
	public void trigger(boolean hasBoulder, Objective task) {
		if (isVisible()) { // if visible
			if (task != null) { // if objective exists
				if (hasBoulder && !isSwitchedOn) { // has a boulder and is not switched on yet
					task.increment(); // switch it on and update
					isSwitchedOn = true;
				} else if (!hasBoulder && isSwitchedOn) { // does not have a boulder and was switched on
					task.decrement(); // switch it off and update
					isSwitchedOn = false;
				}
			}
		}
	}
}
