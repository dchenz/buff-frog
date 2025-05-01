package dungeon.Model.MapObjects;

import dungeon.Model.Map;
import dungeon.Model.MapObjectives.CompletableObjective;
import dungeon.Model.MapObjectives.Objective;

/**

 *
 */

public class Exit extends Entity {
	public Exit(int x, int y) {
		super(x, y);
	}

	public void trigger(Map level, CompletableObjective levelTask, Objective task) {
		if (isVisible()) { // if visible
			if (task != null) { // if objective exists
				task.increment();
			}
			// finish level
			if (levelTask.isCompleted()) { // completed, success
				level.levelSuccess();
			} else { // task not completed, fail
				level.levelFailed();
			}
		}
	}
}
