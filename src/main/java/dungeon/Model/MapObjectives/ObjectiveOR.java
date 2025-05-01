package dungeon.Model.MapObjectives;

/**
 * OR
 *
 * Complete only when at least one is complete
 *
 * Uses composite pattern
 *
 *
 */

public class ObjectiveOR extends ConditionalObjective {
	public ObjectiveOR() {
		super();
	}

	// check if at least one is complete
	@Override
	public boolean isCompleted() {
		for (CompletableObjective task : objectives) {
			if (task.isCompleted()) {
				return true;
			}
		}
		return false;
	}

	// used for objective panel in controller
	@Override
	protected String getObjectiveName() {
		return "OR";
	}
}
