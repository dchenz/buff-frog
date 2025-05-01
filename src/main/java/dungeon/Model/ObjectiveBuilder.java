package dungeon.Model;

import dungeon.Model.MapObjectives.*;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ObjectiveBuilder.java
 *
 * Recursively builds objectives
 *
 * Design pattern: Composite pattern
 *
 *
 */

public class ObjectiveBuilder {
	private int nTreasure, nSwitches, nEnemies, nExits;
	private HashMap<ObjectiveTypes, Objective> individualTasks;

	// constructor, set how many of the entities are in the map
	public ObjectiveBuilder(int nTreasure, int nSwitches, int nEnemies, int nExits) {
		this.nTreasure = nTreasure;
		this.nSwitches = nSwitches;
		this.nEnemies = nEnemies;
		this.nExits = nExits;
		individualTasks = new HashMap<ObjectiveTypes, Objective>();
	}

	// create objective from json object
	public CompletableObjective build(JSONObject objective) {
		CompletableObjective obj = null;
		switch (objective.getString("goal")) {
			// conjunctin and disjuction Composite parts
			case "AND":
				obj = buildSubObjective(new ObjectiveAND(), objective.getJSONArray("subgoals"));
				break;
			case "OR":
				obj = buildSubObjective(new ObjectiveOR(), objective.getJSONArray("subgoals"));
				break;
			// single objective leaf components
			case "treasure":
				obj = retrieveObjective(ObjectiveTypes.TREASURE);
				break;
			case "boulders":
				obj = retrieveObjective(ObjectiveTypes.FLOOR_SWITCH);
				break;
			case "enemies":
				obj = retrieveObjective(ObjectiveTypes.ENEMY);
				break;
			case "exit":
				obj = retrieveObjective(ObjectiveTypes.EXIT);
				break;
		}
		assert obj != null;
		return obj;
	}

	// for every objective in the composite AND/OR objective, build that recursively
	private CompletableObjective buildSubObjective(
		ConditionalObjective composite, JSONArray components) {
		int length = components.length();
		for (int x = 0; x < length; x++) {
			composite.addComponent(build(components.getJSONObject(x))); // add it as a component
		}
		return composite; // return AND/OR when finished
	}

	// There must only be ONE object instance of the objective
	// if there are multiple objective leafs of the same type (eg. contained in more than one
	// composite
	//
	// This method ensures that the same reference is used for all leafs of the same objective
	// so all observer updates are passed to all occurences of it in the Composite tree
	private Objective retrieveObjective(ObjectiveTypes type) {
		if (individualTasks.get(type) == null) { // not yet added
			Objective task = null;
			switch (type) {
				case ENEMY:
					task = new EnemyObjective(nEnemies);
					break;
				case FLOOR_SWITCH:
					task = new FloorSwitchObjective(nSwitches);
					break;
				case TREASURE:
					task = new TreasureObjective(nTreasure);
					break;
				case EXIT:
					task = new ExitObjective(nExits);
					break;
				default:
					assert false : "Invalid objective type";
			}
			individualTasks.put(type, task); // add it
		}
		return individualTasks.get(type); // if added, retrieve it
	}

	// leaf objective getter

	public HashMap<ObjectiveTypes, Objective> getObjectives() {
		return individualTasks;
	}
}
