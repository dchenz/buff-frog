package dungeon.Model.MapObjectives;

import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Objective.java
 *
 * Abstract parent class for the leaf objectives
 *
 * Uses observer pattern
 *
 *
 */

public abstract class Objective extends CompletableObjective implements Observer {
	// how many required
	private IntegerProperty completedCounter;
	private int nRequired;

	// some objectives may initially be part completed, eg when a boulder spawns on a switch
	public Objective(int nRequired) {
		completedCounter = new SimpleIntegerProperty(0);

		this.nRequired = nRequired;

		if (getCompletedAmount() == nRequired) {
			changedStatus(true);
		}
	}

	// part completed
	@Override
	public void increment() {
		completedCounter.set(getCompletedAmount() + 1);
		if (getCompletedAmount() == nRequired) {
			changedStatus(true);
		}
	}

	// completed part reversed (eg, floor switch)
	@Override
	public void decrement() {
		completedCounter.set(getCompletedAmount() - 1);
		if (getCompletedAmount() == nRequired - 1) {
			changedStatus(false);
		}
	}

	@Override
	public boolean isCompleted() {
		assert (getCompletedAmount() >= nRequired)
			== getCompletedProperty().get() : "Not completed";

		return getCompletedAmount() >= nRequired;
	}

	// getters

	public int getCompletedAmount() {
		return completedCounter.get();
	}

	public int getRequiredAmount() {
		return nRequired;
	}

	@Override
	public IntegerProperty getCompletedAmountProperty() {
		return completedCounter;
	}

	@Override
	protected void changedStatus(boolean newValue) {
		setCompletedProperty(newValue);
		if (parent != null) {
			parent.changedStatus(newValue);
		}
	}

	@Override
	public List<CompletableObjective> getChildren() {
		return null;
	}

	// used to set the objective panel data in-game

	@Override
	public String getObjectiveData() {
		return String.format(
			"%2d /%2d %s", getCompletedAmount(), getRequiredAmount(), getObjectiveName());
	}
}
