package dungeon.Model.MapObjectives;

/**
 * 
 *
 */

public class TreasureObjective extends Objective {
	public TreasureObjective(int nRequired) {
		super(nRequired);
	}
	
	@Override
	protected String getObjectiveName() {
		return "Collect treasure";
	}
	
}
