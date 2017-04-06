package BlinkingCursor;

import editor.Editor;
import editor.KeyEventHandler;
import Texts.Point;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BlinkingCursor extends Rectangle {

	private Timeline blinkingAnimation = new Timeline();
	private boolean isBlinking = false;
	private Point xy;
	private BlinkingCursor sameinstance;

	public BlinkingCursor(){
		super();
		this.xy = new Point(Editor.margin,0);
		this.setWidth(1);
		this.setHeight(KeyEventHandler.textHeight);
		this.setFill(Color.BLACK);

		/*
		 * I was unable to use the same instance to make
		 * blinking happening unless I had something else pointing to it
		 */
		this.sameinstance = this;

	}


	public void increaseFontSize(){
		this.setHeight(KeyEventHandler.textHeight);
	}

	public void decreaseFontSize(){
		this.setHeight(KeyEventHandler.textHeight);
	}

	public void resetXY(int x, int y) {
		this.xy.appendXpos(x);
		this.xy.appendYpos(y);

		if (this.xy.getXpos() < Editor.margin && this.xy.getYpos() <= 0) {
			this.xy.setXpos(Editor.margin);
			this.xy.setYpos(0);
		} else if (this.xy.getXpos() < Editor.margin && this.xy.getYpos() > 0) {
			this.xy.setXpos(KeyEventHandler.sceneWidth - Editor.margin);
			this.xy.appendYpos(-((int) Math.ceil(this.getHeight())));
		} else if (this.xy.getXpos() > KeyEventHandler.sceneWidth - Editor.margin) {
			this.xy.setXpos(Editor.margin);
			this.xy.appendYpos(((int) Math.ceil(this.getHeight())));
		} else if (this.xy.getYpos() < 0) {
			this.xy.setYpos(0);
		}

		this.setX(this.xy.getXpos());
		this.setY(this.xy.getYpos());
	}

	public void resetXYForClick(double x, double y) {
		
		if ( x < 5) {
			x = Editor.margin;
		}
		
		this.xy.setXpos((int) Math.ceil(x));
		this.xy.setYpos((int) Math.ceil(y));
		this.setX(this.xy.getXpos());
		this.setY(this.xy.getYpos());
	}

	public void resetXYForBackSpace(int x, int y) {	
		
		if ( x < 5) {
			x = Editor.margin;
		}
		
		this.xy.setXpos(x);
		this.xy.setYpos(y);
		this.setX(x);
		this.setY(y);

	}

	public void resetXYForNewLine(int x, int y) {
		
		if ( x < 5) {
			x = Editor.margin;
		}
		
		this.xy.setXpos(x);
		this.xy.appendYpos(y);
		this.setX(this.xy.getXpos());
		this.setY(this.xy.getYpos());

	}


	public Point getXY() {
		return this.xy;
	}

	public void makeBlink(){

		if(!isBlinking){
			this.blinkingAnimation.setCycleCount(Timeline.INDEFINITE);

			KeyFrame keyframe1 = new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent event) {                                                              
					sameinstance.setFill( Color.TRANSPARENT );                    
				}
			});

			KeyFrame keyframe2 = new KeyFrame(Duration.seconds(.5), new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent event) {
					sameinstance.setFill(Color.BLACK);
				}
			});

			KeyFrame keyframe3 = new KeyFrame(Duration.seconds(1));

			this.blinkingAnimation.getKeyFrames().addAll(keyframe1, keyframe2, keyframe3);
			this.blinkingAnimation.play();
			this.isBlinking = true;
		}

	}

	public void stopBlinking(){

		if(this.isBlinking){
			this.blinkingAnimation.stop();
			this.isBlinking = false;
		}

	}


}
