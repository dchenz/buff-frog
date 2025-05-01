package dungeon.Model.MapObjectives;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;

/**
 * ConditionalObjective.java
 *
 * AND / OR objectives
 *
 * Uses the composite pattern
 *
 *
 */

public abstract class ConditionalObjective extends CompletableObjective {
	protected ArrayList<CompletableObjective> objectives;

	public ConditionalObjective() {
		super();
		objectives = new ArrayList<CompletableObjective>();
	}

	// add component to its list of components
	public void addComponent(CompletableObjective task) {
		task.parent = this;
		objectives.add(task);
	}

	// If one of its components is completed or became incomplete
	// check if AND / OR has completed or incompleted
	// and update its status
	@Override
	protected void changedStatus(boolean newValue) {
		newValue = isCompleted();

		setCompletedProperty(newValue);
		if (parent != null) {
			parent.changedStatus(newValue); // update its parent too
		}
	}

	// getters for controller

	@Override
	public IntegerProperty getCompletedAmountProperty() {
		return null;
	}

	@Override
	public List<CompletableObjective> getChildren() {
		return objectives;
	}

	@Override
	public String getObjectiveData() {
		return getObjectiveName();
	}
}
