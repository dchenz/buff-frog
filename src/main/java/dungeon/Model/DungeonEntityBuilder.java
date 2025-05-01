package dungeon.Model;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import dungeon.Model.MapObjects.*;
import dungeon.Model.MapObjects.MapItems.*;

/**
 * DungeonEntityBuilder.java
 * 
 * Used to create Entity objects and image views
 * 
 *
 */

public class DungeonEntityBuilder extends DungeonBuilder {
	
	// image views for entities
	private ArrayList<ImageView> entities;
	
	/**
	 * 
	 * @param filename: JSON Filename
	 * @throws FileNotFoundException: no file exists
	 */
	public DungeonEntityBuilder(String filename) throws FileNotFoundException {
		super(filename);
		entities = new ArrayList<ImageView>();
	}
	
	// Load entities, register image view, return object
	//
	// Mostly from the starter code
	
    @Override
    public Player onLoad(Player player) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/player.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(player, view);
        //player.loadViews(); <- Didn't have time to put rotating player views
        return player;
    }

    @Override
    public BarrierEntity onLoad(Wall wall) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/wall.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(wall, view);
        return wall;
    }
    
    @Override
    public BarrierEntity onLoad(Door door) {
    	ImageView view = new ImageView(new Image(
    			"file:images/sprites/closed_door.png", TILE_SIZE, TILE_SIZE, true, true));
    	
    	// Doors will change their image when their "isOpen" boolean property changes to TRUE 
    	
    	door.getOpenProperty().addListener(new ChangeListener<Boolean>() {
    		@Override
    		public void changed(ObservableValue<? extends Boolean> observable,
    				Boolean oldValue, Boolean newValue) {
    			assert newValue.booleanValue() == true;
    			view.setImage(new Image("file:images/sprites/open_door.png", TILE_SIZE, TILE_SIZE, true, true));
    		}
    	});
        addEntity(door, view);
        return door;
    }
    
    @Override
    public BarrierEntity onLoad(Portal portal) {
    	ImageView view = new ImageView(new Image(
    			"file:images/sprites/portal.png", TILE_SIZE, TILE_SIZE, true, true));
    	addEntity(portal, view);
    	return portal;
    }
    
    @Override
    public Enemy onLoad(Enemy enemy) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/enemy.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(enemy, view);
        return enemy;
    }
    
    @Override
    public Boulder onLoad(Boulder boulder) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/boulder.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(boulder, view);
        return boulder;
    }
    
    @Override
    public FloorSwitch onLoad(FloorSwitch floor) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/switch.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(floor, view);
        return floor;
    }
    
    @Override
    public Exit onLoad(Exit exit) {
    	ImageView view = new ImageView(new Image(
    			"file:images/sprites/exit.png", TILE_SIZE, TILE_SIZE, true, true));
    	addEntity(exit, view);
    	return exit;
    }
    
    @Override
    public Item onLoad(Key key) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/key.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(key, view);
        return key;
    }
    
    @Override
    public Item onLoad(Sword sword) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/sword.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(sword, view);
        return sword;
    }
    
    @Override
    public Item onLoad(Potion potion) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/potion.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(potion, view);
        return potion;
    }
    
    @Override
    public Item onLoad(Treasure treasure) {
        ImageView view = new ImageView(new Image(
        		"file:images/sprites/treasure.png", TILE_SIZE, TILE_SIZE, true, true));
        addEntity(treasure, view);
        return treasure;
    }
    
    /**
     * From starter code
     * 
     * Add the entity to the GridPane and set their image view
     * 
     * @param entity: entity objetc
     * @param view: their image view
     */
    private void addEntity(Entity entity, ImageView view) {
        trackPosition(entity, view, entity.getXPosition(), entity.getYPosition());
        entity.setImageView(view);
        entities.add(view);
    }

}
