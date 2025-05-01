package dungeon.Model.MapObjectives;

/**
 *
 *
 */

public class EnemyObjective extends Objective {
	public EnemyObjective(int nRequired) {
		super(nRequired);
	}

	@Override
	protected String getObjectiveName() {
		return "Defeat enemies";
	}
}
