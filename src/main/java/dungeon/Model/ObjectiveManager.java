package dungeon.Model;

import dungeon.Model.MapObjectives.CompletableObjective;
import dungeon.Model.MapObjectives.Objective;
import dungeon.Model.MapObjectives.ObjectiveTypes;
import java.util.HashMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * ObjectiveManager.java
 * 
 * Holds the root goal and leaf objectives
 * 
 * Also determines if the level is complete
 * (Map status listens to the goal status property)
 * 
 *
 */

public class ObjectiveManager {
	private CompletableObjective goal; // root goal
	private BooleanProperty objectiveCompleted; // is goal completed
	private HashMap<ObjectiveTypes, Objective> tasks; // tasks by enum type
	
	private IntegerProperty status; // status of the Map (0 none, 1 lose, 2 win)
	
	public ObjectiveManager(CompletableObjective goal, HashMap<ObjectiveTypes, Objective> tasks) {
		this.goal = goal;
		this.tasks = tasks;
		objectiveCompleted = new SimpleBooleanProperty(goal.isCompleted());
		objectiveCompleted.bind(goal.getCompletedProperty());
		
		// listen for the goal being completed, then set game status to 2 and WIN the level
		objectiveCompleted.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					status.set(2);
				}
			}
		});
	}
	
	// getter for the Map exit method
	public CompletableObjective getRootGoal() {
		return goal;
	}
	
	// getter for Map trigger methods
	public Objective getTask(ObjectiveTypes taskType) {
		return tasks.get(taskType);
	}
	
	// getter for controller (how many needed to complete goal)
	public int getRequirement(ObjectiveTypes taskType) {
		Objective task = getTask(taskType);
		if (task == null) {
			return 0;
		}
		return task.getRequiredAmount();
	}
	
	// getter for controller (how many leaf objectives) (int >= 1 && int <= 4)
	public int getObjectiveQty() {
		int required = 0;
		for (ObjectiveTypes type : tasks.keySet()) {
			required += getRequirement(type) != 0 ? 1 : 0; // count if requirement is not 0
			// requirement is only 0 if objective does not exist
			// or the objective exists BUT it does not have the releavnt entities
			// (eg. enemy objective with no enemies on the map) 
			// ^ These are completed by default
		}
		return required;
	}
	
	// setter for status
	
	public void setStatusProperty(IntegerProperty state) {
		status = state;
	}
	
}
