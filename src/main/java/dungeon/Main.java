package dungeon;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import dungeon.View.*;

/**
 * Main.java
 * 
 *
 */

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("ADVENTURES OF BUFF FROG"); // Set name
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app/frog.png"))); // Set app icon
		primaryStage.setResizable(false); // Set not resizable

		// Prevent Spacebar andEnter from activating buttons
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
	        if (key.getCode() == KeyCode.SPACE || key.getCode() == KeyCode.ENTER){
	            key.consume();
	        }
	    });

		try {
			MenuScreen menuScreen = new MenuScreen(primaryStage); 				// Main menu
			DungeonScreen dungeonScreen = new DungeonScreen(primaryStage); 		// Dungeon 
			SelectorScreen selectorScreen = new SelectorScreen(primaryStage);	// Select a level
			EndScreen endScreen = new EndScreen(primaryStage); 					// After all levels completed
			InstructionScreen helpScreen = new InstructionScreen(primaryStage); // How to play
			SandboxScreen sandboxScreen = new SandboxScreen(primaryStage); 		// Build your own map

			// Add levels to the game
			dungeonScreen.getController().addLevel("boulders.json");
			dungeonScreen.getController().addLevel("template.json");
			dungeonScreen.getController().addLevel("template1.json");
			dungeonScreen.getController().addLevel("advanced.json");
			dungeonScreen.getController().addLevel("maze.json");

			// Link all the screens and their controllers
			setControllers(menuScreen, dungeonScreen, selectorScreen, endScreen, helpScreen, sandboxScreen);

			// Play the menu screen
			menuScreen.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Link the controllers and screens to their relevant screens
	 * 
	 * @param screen1: Menu (start)
	 * @param screen2: Dungeon 
	 * @param screen3: Select level
	 * @param screen4: Ending scene
	 * @param screen5: How to play
	 * @param screen6: Sandbox mode
	 */
	private void setControllers(MenuScreen screen1, DungeonScreen screen2, SelectorScreen screen3, 
			EndScreen screen4, InstructionScreen screen5, SandboxScreen screen6) {
		
		// Menu links with dungeon, level select, instructions, and sandbox screens
		screen1.getController().setDungeonScreen(screen2);
		screen1.getController().setSelectorScreen(screen3);
		screen1.getController().setInstructionsScreen(screen5);
		screen1.getController().setSandboxScreen(screen6);
		
		// Dungeon links with menu screen and ending screen
		screen2.getController().setMenuScreenScreen(screen1);
		screen2.getController().setEndScreen(screen4);
		
		// Level select screen links with menu screen and dungeon screen
		screen3.getController().setDungeonScreen(screen2);
		screen3.getController().setMenuScreen(screen1);
		
		// Ending screen links back to the menu
		screen4.getController().setMenuScreen(screen1);
		
		// Instructions screen links back to the menu
		screen5.getController().setMenuScreen(screen1);
		
		// Sandbox screen links back to the menu and also the dungeon screen
		screen6.getController().setMenuScreen(screen1);
		screen6.getController().setDungeonScreen(screen2);
		
		// Add the dungeon controller to the sandbox controller to play dungeons from sandbox screen
		screen6.getController().setDungeonController(screen2.getController());
	}

	/**
	 * Launch the app
	 * 
	 * @param args: arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
