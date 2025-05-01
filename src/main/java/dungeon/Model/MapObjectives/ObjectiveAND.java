package dungeon.Model.MapObjectives;

/**
 * AND
 *
 * only complete when all complete
 *
 * Uses composite pattern
 *
 *
 */

public class ObjectiveAND extends ConditionalObjective {
	public ObjectiveAND() {
		super();
	}

	// all must be complete
	@Override
	public boolean isCompleted() {
		for (CompletableObjective task : objectives) {
			if (!task.isCompleted()) {
				return false;
			}
		}
		return true;
	}

	// used for objective panel in controller
	@Override
	protected String getObjectiveName() {
		return "AND";
	}
}
