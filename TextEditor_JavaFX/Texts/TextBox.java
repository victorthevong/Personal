package Texts;

import editor.Editor;
import editor.KeyEventHandler;
import BlinkingCursor.BlinkingCursor;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextBox extends Text {

	private String fontName = "Verdana";
	private String actionPerformedOnIt;
	private Point xy;

	public TextBox() {
		super();
		this.xy = new Point(Editor.margin,0);
		this.setX(Editor.margin);
		this.setY(0);
		this.setFont(Font.font(fontName, KeyEventHandler.textHeight));
		this.setTextOrigin(VPos.TOP);
		this.actionPerformedOnIt = "none";
	}

	public TextBox(int x , int y ,String value, String actionPerformedOnIt) {
		super(x, y, value);
		this.xy = new Point(x,y);
		this.setX(x);
		this.setY(y);
		this.setFont(Font.font(fontName, KeyEventHandler.textHeight));
		this.setTextOrigin(VPos.TOP);
		this.actionPerformedOnIt = actionPerformedOnIt;
	}

	public TextBox(String value) {
		super(value);
		this.xy = new Point(Editor.margin,0);
		this.setX(Editor.margin);
		this.setY(0);
		this.setFont(Font.font(fontName, KeyEventHandler.textHeight));
		this.setTextOrigin(VPos.TOP);
		this.actionPerformedOnIt = "none";
	}

	public int getFontSize() {
		return KeyEventHandler.textHeight;
	}
	
	public void setActionPerformedOnIt(String actionPerformedOnIt) {
		this.actionPerformedOnIt = actionPerformedOnIt;
	}
	
	public String getActionPerformedOnIt() {
		return this.actionPerformedOnIt;
	}

	public void increaseFontSize() {
		this.setFont(Font.font(fontName, KeyEventHandler.textHeight));
	}
	public void decreaseFontSize() {
		this.setFont(Font.font(fontName, KeyEventHandler.textHeight));
	}

	public Point getXYpoint() {
		return this.xy;
	}

	public void resetXYForRendering(int x, int y) {

		if (x < Editor.margin && y < 0) {
			this.xy.setXpos(Editor.margin);
			this.xy.setYpos(0);
		} else if (x > KeyEventHandler.sceneWidth) {
			this.xy.setXpos(Editor.margin);
			this.xy.appendYpos(KeyEventHandler.textHeight);;
		} else {
			this.xy.setXpos(x);
			this.xy.setYpos(y);
		}

		this.setX(this.xy.getXpos());
		this.setY(this.xy.getYpos());

	}

	public String toString() {
		return super.getText();
	}

	public boolean compareToAnotherTextBox(TextBox box) {
		return ((this.getXYpoint().getXpos() == box.getXYpoint().getXpos()) && (this.getXYpoint().getYpos() == box.getXYpoint().getYpos())
				&& (this.getText().compareTo(box.getText()) == 0));
	}

	public static boolean compareTextBoxPosAndCursorPos(TextBox box, BlinkingCursor cursor) {

		if (box.xy.getYpos() == cursor.getXY().getYpos()) {
			return true;
		}

		return false;

	}




}
