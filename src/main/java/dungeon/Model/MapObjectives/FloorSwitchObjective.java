package dungeon.Model.MapObjectives;

/**
 * 
 *
 */

public class FloorSwitchObjective extends Objective {
	public FloorSwitchObjective(int nRequired) {
		super(nRequired);
	}
	
	@Override
	protected String getObjectiveName() {
		return "Activate switches";
	}
	
}
