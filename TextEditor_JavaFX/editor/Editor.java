package editor;

import java.util.ArrayList;
import java.util.List;

import OpenAndSave.Open;
import Texts.TextBoxLinkedListDeque;
import Texts.TextBoxLinkedListDeque.Node;
import BlinkingCursor.BlinkingCursor;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Editor extends Application {

	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 500;
	public static final int margin = 5;
	public static int scrollbarShiftValue = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		List<String> args = getParameters().getRaw(); // parameters passed into command line
		
		Open open = new Open(args.get(0));

		Group masterroot = new Group();
		
		Group root = new Group();
		
		masterroot.getChildren().add(root);
		
        // Make a vertical scroll bar on the right side of the screen.
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        // Set the height of the scroll bar so that it fills the whole window.
        scrollBar.setPrefHeight(WINDOW_HEIGHT);

        // Set the range of the scroll bar.
        scrollBar.setMin(0.0);
        scrollBar.setMax(WINDOW_HEIGHT);

        // Add the scroll bar to the scene graph, so that it appears on the screen.
        masterroot.getChildren().add(scrollBar);

        double usableScreenWidth = WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth();
        scrollBar.setLayoutX(usableScreenWidth);

		Scene scene = new Scene(masterroot, WINDOW_WIDTH , WINDOW_HEIGHT, Color.WHITE);

		BlinkingCursor cursor = new BlinkingCursor();
		cursor.makeBlink();

		TextBoxLinkedListDeque textdisplay = new TextBoxLinkedListDeque();
		textdisplay.setCursor(cursor);
		
		ArrayList<Node> lines = new ArrayList<Node>();

		EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(root,cursor, textdisplay, lines, scrollBar, open);
		// the open passed into keyEventHandler is repointed to a different open at bottom;
		
		KeyEventHandler.sceneWidth = (int) Math.ceil(usableScreenWidth);

		// sets scene width/height first
	
		KeyEventHandler.sceneWidth = (int) Math.ceil(scene.getWidth()) - (int)Math.ceil(scrollBar.getLayoutBounds().getWidth());
		KeyEventHandler.sceneHeight = (int) Math.ceil(scene.getHeight());

		// Register the event handler to be called for all KEY_PRESSED and
		// KEY_TYPED events.
		scene.setOnKeyTyped(keyEventHandler);
		scene.setOnKeyPressed(keyEventHandler);
		scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
			
				KeyEventHandler.sceneWidth = (int) Math.ceil((double) newValue) - (int)Math.ceil(scrollBar.getLayoutBounds().getWidth());
				((KeyEventHandler) keyEventHandler).rerender();
				
		        double usableScreenWidth = newValue.doubleValue() - scrollBar.getLayoutBounds().getWidth();
		        scrollBar.setLayoutX(usableScreenWidth);

			}
		});

		scene.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {

				KeyEventHandler.sceneHeight = (int) Math.ceil((double) newValue);
				((KeyEventHandler) keyEventHandler).rerender();
				
		        scrollBar.setPrefHeight(newValue.doubleValue());
		        
		        scrollBar.setMax(newValue.doubleValue());

			}
		});

		EventHandler<MouseEvent> mouseEventHandler = new MouseEventHandler(cursor, textdisplay, lines);

		scene.setOnMouseClicked(mouseEventHandler);
		
        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            	
            	Editor.scrollbarShiftValue = (int) -Math.round(newValue.doubleValue());
               root.setLayoutY(Editor.scrollbarShiftValue);
               
            }
        });

		primaryStage.setTitle("Text Editor");

		// This is boilerplate, necessary to setup the window where things are
		// displayed.
		primaryStage.setScene(scene);
		primaryStage.setResizable(true); // allows window to be resizeable
		primaryStage.show();
		
		if (args.size() == 1) {
			open.setTextDisplay(textdisplay);
			open.setGroupRoot(root);
			open.setCursor(cursor);
			open.setKeyEventHandler(keyEventHandler);
			open.tryToOpen();
			cursor.resetXYForClick(Editor.margin, 0);
			textdisplay.setAndFindClosestNodeToCursor();
			
		} else if (args.size() == 2) {
			
			if (args.get(1).compareTo("debug") == 0) {
				Print.print = true;
				Print.printLine("Debug Mode initialized:\n\n");
			}
			
			open.setTextDisplay(textdisplay);
			open.setGroupRoot(root);
			open.setCursor(cursor);
			open.setKeyEventHandler(keyEventHandler);
			open.tryToOpen();
			
			cursor.resetXYForClick(Editor.margin, 0);
			textdisplay.setAndFindClosestNodeToCursor();
			
		} else {
			System.out.println("Expected usage: <source filename> <Optional debug status : type debug >");
			System.exit(1);
		}

	}

	public static void main(String[] args) {
		Application.launch(args);

	}

}
