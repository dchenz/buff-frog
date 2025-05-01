package dungeon.Controller;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import dungeon.View.MenuScreen;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

public class EndScreenController {
	
	@FXML
	private StackPane screenPane;
	
	@FXML
	private AnchorPane fadeCover;
	
	private MenuScreen menuScreen;
	
	public void trigger() {
		fadeCover.setOpacity(1);
		fadeScreen(fadeCover, 2.5, true, null);

		ImageView player = createView("/images/end/end_frog.png", 0, 0);
		ImageView clouds = createView("/images/end/clouds.png", 0, 0);
		ImageView enemy  = createView("/images/end/enemy_frog.png", 350, 0);
		ImageView desert = createView("/images/end/desert.png", 0, 0);
		
		screenPane.getChildren().add(0, clouds);
		screenPane.getChildren().add(0, enemy);
		screenPane.getChildren().add(0, player);
		screenPane.getChildren().add(0, desert);
		
		Timeline move = new Timeline(new KeyFrame(Duration.millis(15), moveEnemy -> {
			double xPosition = enemy.getTranslateX();
			enemy.setTranslateX(xPosition - 1);
			enemy.setTranslateY(enemy.getTranslateY() + Math.sin(xPosition / 25));
			enemy.setFitHeight(enemy.getFitHeight() + Math.log(xPosition) / 50 + 2);
			enemy.setFitWidth(enemy.getFitWidth() + Math.log(xPosition) / 50 + 2);
		}));
		move.setOnFinished(buttons -> {
			revealButtons();
		});
		move.setCycleCount(350);
		move.setDelay(Duration.seconds(2));
		move.play();
	}
	
	private ImageView createView(String filename, double x, double y) {
		ImageView view = new ImageView(new Image(getClass().getResourceAsStream(filename), 600, 450, true, true));
		view.setTranslateX(x);
		view.setTranslateY(y);
		return view;
	}
	
	private void revealButtons() {
		Button menuButton = createExitButton("BACK TO MENU");
		Text displayText = createDisplayMessage("Levels completed");
		
		screenPane.getChildren().add(screenPane.getChildren().size() - 1, displayText);
		screenPane.getChildren().add(menuButton);
		
		fadeButton(menuButton);
		fadeText(displayText);
	}
	
	private Button createExitButton(String message) {
		Button menuButton = new Button(message);
		menuButton.resize(200, 150);
		menuButton.setTranslateX(-165);
		menuButton.setTranslateY(-75);
		menuButton.setOpacity(0);
		menuButton.setStyle("-fx-background-color: rgba(150, 150, 150, 0.5);"
						  + "-fx-text-fill: white;"
						  + "-fx-font-size: 16px;"
	    );
		
		menuButton.setOnAction(menu -> {
			menuButton.setVisible(false);
			fadeScreen(fadeCover, 2.5, false, exit -> {
				exitToMenu();
			});
		});
		
		return menuButton;
	}
	
	private Text createDisplayMessage(String message) {
		Text displayText = new Text(message);
		displayText.setTranslateX(-160);
		displayText.setTranslateY(-150);
		displayText.setOpacity(0);
		displayText.setStyle("-fx-fill: white;"
						   + "-fx-font-size: 24px;"
						   + "-fx-font-family: \"Poor Richard\";"
		);
		
		return displayText;
	}
	
	private void fadeScreen(Node object, double duration, boolean fadeOut, EventHandler<ActionEvent> ev) {
		Timeline fadeEffect = new Timeline(new KeyFrame(Duration.millis(duration * 10), fade -> {
			object.setOpacity(fadeCover.getOpacity() + 0.01 * (fadeOut ? -1 : 1));
		}));
		fadeEffect.setOnFinished(ev);
		fadeEffect.setCycleCount(100);
		fadeEffect.play();
	}
	
	private void fadeText(Text displayText) {
		Timeline fadeTextEffect = new Timeline(new KeyFrame(Duration.millis(25), fade -> {
			displayText.setOpacity(displayText.getOpacity() + 0.01);
		}));
		fadeTextEffect.setCycleCount(100);
		fadeTextEffect.play();
	}
	
	private void fadeButton(Button menuButton) {
		Timeline fadeButtonEffect = new Timeline(new KeyFrame(Duration.millis(25), fade -> {
			menuButton.setOpacity(menuButton.getOpacity() + 0.01);
		}));
		fadeButtonEffect.setCycleCount(100);
		fadeButtonEffect.setDelay(Duration.seconds(2));
		fadeButtonEffect.play();
	}
	
	public void exitToMenu() {
		resetScreen();
		menuScreen.start();
	}
	
	private void resetScreen() {
		fadeCover.setOpacity(0);
		screenPane.getChildren().clear();
		screenPane.getChildren().add(fadeCover);
	}

	public void setMenuScreen(MenuScreen screen) {
		menuScreen = screen;
	}
	
}
