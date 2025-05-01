package dungeon.Model.MapObjectives;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * CompletableObjective.java
 *
 * The root abstract class for objectives
 *
 * Subclasses into composite objectives and individual objectives
 *
 * Uses composite pattern.
 *  If a lead objective (enemies, boulders, treasure, exit) is completed or becomes incomplete,
 *  it tells its parent objective that its status has changed
 *  Then the parent (always either AND/OR) will decide if the change has changed their own
 * completeness It wil update its own status to reflect the change and tell its parent too
 *
 *  This goes all the way up the composite tree until the root objective for the level
 *
 *
 */

public abstract class CompletableObjective {
	// parent objective, null if the root objective
	protected CompletableObjective parent;

	private BooleanProperty completed; // completed or not

	public CompletableObjective() {
		completed = new SimpleBooleanProperty(false);
	}

	// getter for completed or not
	public BooleanProperty getCompletedProperty() {
		return completed;
	}

	// setter to complete or incomplete it
	protected void setCompletedProperty(boolean newValue) {
		completed.set(newValue);
	}

	// abstract methods for the subclass objective types

	public abstract IntegerProperty getCompletedAmountProperty();

	protected abstract void changedStatus(boolean newValue);

	public abstract boolean isCompleted();

	public abstract List<CompletableObjective> getChildren();

	public abstract String getObjectiveData();

	protected abstract String getObjectiveName();
}
