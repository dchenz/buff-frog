package dungeon.Model.MapObjectives;

/**
 * 
 *
 */

public class ExitObjective extends Objective {
	public ExitObjective(int nRequired) {
		super(nRequired);
	}
	
	@Override
	protected String getObjectiveName() {
		return "Enter the exit";
	}
	
	@Override
	public String getObjectiveData() {
		return getObjectiveName();
	}
}
