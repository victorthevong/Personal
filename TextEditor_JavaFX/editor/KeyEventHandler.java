package editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import OpenAndSave.Open;
import Texts.Point;
import Texts.TextBox;
import Texts.TextBoxLinkedListDeque;
import Texts.TextBoxLinkedListDeque.Node;
import BlinkingCursor.BlinkingCursor;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyEventHandler implements EventHandler<KeyEvent>{

	private ArrayList<Node> lines;
	private TextBoxLinkedListDeque textdisplay;
	private Open open;
	private Group root;
	private BlinkingCursor cursor;
	private  ScrollBar scrollbar;
	private UndoRedoDeque undo;
	private Stack<TextBox> redo;
	public static int textHeight = 12;
	public static int sceneWidth;
	public static int sceneHeight;

	public KeyEventHandler(Group root, BlinkingCursor cursor, TextBoxLinkedListDeque textdisplay, ArrayList<Node> lines,  ScrollBar scrollbar, Open open){


		this.root = root;

		this.cursor = cursor;

		this.textdisplay = textdisplay;

		this.scrollbar = scrollbar;

		this.lines = lines;
		this.open = open;

		this.root.getChildren().add(cursor);

		this.undo = new UndoRedoDeque(100);
		this.redo = new Stack<TextBox>();

	}

	@Override
	public void handle(KeyEvent keyEvent) {
		
		if (this.cursor.getXY().getYpos() + Editor.scrollbarShiftValue < 0) {
			this.scrollbar.setValue((this.cursor.getXY().getYpos() + KeyEventHandler.textHeight + Editor.scrollbarShiftValue));
		} else if (this.cursor.getXY().getYpos() + Editor.scrollbarShiftValue > KeyEventHandler.sceneHeight) {
			this.scrollbar.setValue(this.cursor.getXY().getYpos() - KeyEventHandler.sceneHeight - Editor.scrollbarShiftValue - KeyEventHandler.textHeight);
		}

		KeyCode code = keyEvent.getCode();

		if (keyEvent.isShortcutDown() && keyEvent.getCharacter().compareTo("s") == 0) {
			this.open.save();

		} else if (keyEvent.isShortcutDown() && keyEvent.getCharacter().compareTo("v") == 0) { 
			
			Clipboard clipboard = Clipboard.getSystemClipboard();
			String content = clipboard.getString();
			
			for(int i = 0; i < content.length(); i++) {
				TextBox temp = new TextBox(content.substring(i, i+1));
				this.textdisplay.addToCursorPosition(temp);
				this.root.getChildren().add(temp);
			}
			
			this.rerender();
			
		}else if (keyEvent.isShortcutDown() && keyEvent.getCharacter().compareTo("z") == 0) {


			if (this.undo.size() != 0) {
				Print.printLine("undo stack size : " + undo.size());

				TextBox box = this.undo.pollLast();
				String action  = box.getActionPerformedOnIt();

				if (action.compareTo("backspace") == 0) {
					this.cursor.resetXYForClick(box.getXYpoint().getXpos(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					this.textdisplay.addToCursorPosition(box);
					this.root.getChildren().add(box);
					this.cursor.resetXYForClick(box.getXYpoint().getXpos(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					box.setActionPerformedOnIt("add"); // necessary to prevent duplicate elements
					this.redo.push(box);
					this.rerender();
					
					Print.printLine("undid backspace");

				} else if (action.compareTo("add") == 0) {
					this.cursor.resetXYForClick(box.getXYpoint().getXpos(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					box = this.textdisplay.getClosestNodeTextBox();
					this.textdisplay.removeNodeClosestToCursor();
					this.root.getChildren().remove(box);
					this.cursor.resetXYForClick(box.getXYpoint().getXpos() - box.getLayoutBounds().getWidth(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					box.setActionPerformedOnIt("backspace"); // necessary to prevent duplicate elements
					this.redo.push(box);
					this.rerender();
					
					Print.printLine("undid add");
				}

			} else {
				Print.printLine("Size of Undo Stack is : " + this.undo.size());
			}


		} else if (keyEvent.isShortcutDown() && keyEvent.getCharacter().compareTo("y") == 0) {
			if (this.redo.size() != 0) {
				Print.printLine("redodo stack size : " + redo.size());

				TextBox box = this.redo.pop();
				String action  = box.getActionPerformedOnIt();

				if (action.compareTo("add") == 0) {
					this.cursor.resetXYForClick(box.getXYpoint().getXpos(), box.getXYpoint().getYpos());		
					this.textdisplay.setAndFindClosestNodeToCursor();
					box = this.textdisplay.getClosestNodeTextBox();	
					this.textdisplay.removeNodeClosestToCursor();
					this.root.getChildren().remove(box);
					this.cursor.resetXYForClick(box.getXYpoint().getXpos(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					box.setActionPerformedOnIt("backspace");
					this.undo.addLast(box);
					this.rerender();


				} else if (action.compareTo("backspace") == 0) {

					this.cursor.resetXYForClick(box.getXYpoint().getXpos(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					this.textdisplay.addToCursorPosition(box);
					this.root.getChildren().add(box);
					this.cursor.resetXYForClick(box.getXYpoint().getXpos() + box.getLayoutBounds().getWidth(), box.getXYpoint().getYpos());
					this.textdisplay.setAndFindClosestNodeToCursor();
					box.setActionPerformedOnIt("add");
					this.undo.addLast(box);
					this.rerender();

				}

				//				System.out.println(this.redo.size());
			} else {
				Print.printLine("Size of redo stack is " + this.redo.size());
			}

		} else if (keyEvent.isShortcutDown() && code == KeyCode.EQUALS){

			KeyEventHandler.textHeight += 4;

			Iterator<Node> it = this.textdisplay.iterator();

			while(it.hasNext()) {
				it.next().item.increaseFontSize();
			}

			this.cursor.increaseFontSize();

			this.rerender();


		} else if (keyEvent.isShortcutDown() && code == KeyCode.MINUS){

			if(KeyEventHandler.textHeight - 4 > 0){

				KeyEventHandler.textHeight -= 4;

				Iterator<Node> it = this.textdisplay.iterator();

				while(it.hasNext()) {
					it.next().item.decreaseFontSize();
				}

				this.cursor.decreaseFontSize();

				this.rerender();

			}


		} else if(keyEvent.isShortcutDown() && keyEvent.getCharacter().compareTo("p") == 0){

			System.out.println(this.cursor.getXY().getXpos() + ", " + this.cursor.getXY().getYpos());

		} else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {

			String characterTyped = keyEvent.getCharacter();

			if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8 && !characterTyped.equals("\r")) {
				// Ignore control keys, which have non-zero length, as well as the backspace
				// key, which is represented as a character of value = 8 on Windows.

				TextBox temp = new TextBox(this.cursor.getXY().getXpos(),this.cursor.getXY().getYpos(),characterTyped, "add");

				TextBox undoAdd;

				if (this.textdisplay.getClosestNodeToCursor() != null) {

					Node nextNode = this.textdisplay.getClosestNodeToCursor().next;

					undoAdd = new TextBox(this.cursor.getXY().getXpos(),this.cursor.getXY().getYpos(),characterTyped, "add");

					if (nextNode != null){
						undoAdd = new TextBox(nextNode.item.getXYpoint().getXpos(),nextNode.item.getXYpoint().getYpos(),characterTyped, "add");
					}

				} else {
					undoAdd = new TextBox(this.cursor.getXY().getXpos(),this.cursor.getXY().getYpos(),characterTyped, "add");
				}

				KeyEventHandler.textHeight = temp.getFontSize();

				//				System.out.println(temp.getText());
				this.textdisplay.addToCursorPosition(temp);
				//				System.out.println("\n " + textdisplay.toString() + "\n" );

				this.undo.addLast(undoAdd);

				this.redo.clear();

				this.root.getChildren().add(temp);

				this.cursor.resetXY((int) Math.ceil(temp.getLayoutBounds().getWidth()), 0); //must change 0 to something else later

				keyEvent.consume();

				this.rerender();


			} else if (characterTyped.equals("\r") && this.textdisplay.size() != 0) {

				//				System.out.println(this.cursor.getXY().getYpos());

				this.cursor.resetXYForNewLine(Editor.margin, KeyEventHandler.textHeight); // will probably have to change later

				TextBox temp = new TextBox(Editor.margin ,this.cursor.getXY().getYpos(),"\n" , "add");
				//				System.out.println(temp.getLayoutBounds().getWidth());

				this.undo.addLast(temp);

				this.textdisplay.addToCursorPosition(temp);
				this.root.getChildren().add(temp);

				this.rerender();

				keyEvent.consume();
			}

			/*
			 * I have this.rerender in 2 different places. If i after rerender after both cases then for enter 
			 * key it wont go to the correct place since I use the adjustCursorXY method
			 */


		} else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !keyEvent.isShortcutDown()) {

			if (code == KeyCode.BACK_SPACE) {

				Node closestTextBox = this.textdisplay.getClosestNodeToCursor();

				if (closestTextBox != null) {

					this.cursor.resetXYForBackSpace(closestTextBox.item.getXYpoint().getXpos(), (closestTextBox.item.getXYpoint().getYpos()));
					this.root.getChildren().remove(closestTextBox.item);

					closestTextBox.item.setActionPerformedOnIt("backspace");

					//					System.out.println(this.textdisplay.removeNodeClosestToCursor());

					this.textdisplay.removeNodeClosestToCursor();

					Node temp = this.textdisplay.getClosestNodeToCursor();

					if (temp != null) {

						int x = (int)Math.ceil(temp.item.getXYpoint().getXpos());
						int y = (int) Math.ceil(temp.item.getXYpoint().getYpos());

						closestTextBox.item.getXYpoint().setXpos(x);
						closestTextBox.item.getXYpoint().setYpos(y);

						if (temp.prev != null) {
							closestTextBox.item.getXYpoint().setXpos(temp.prev.item.getXYpoint().getXpos()
									+ (int) Math.ceil(temp.prev.item.getLayoutBounds().getWidth()));
							closestTextBox.item.getXYpoint().setYpos(temp.prev.item.getXYpoint().getYpos());
						} 

					}


					this.undo.addLast(closestTextBox.item);

					this.redo.clear();

					this.rerender();


					//					System.out.println( "removed");

				} 

			} else if (code == KeyCode.UP) {
				
				if(lines.size() != 0 && this.cursor.getXY().getYpos() != 0) {

					this.cursor.resetXY(0, -KeyEventHandler.textHeight);

					Point mouseClickPoint = new Point(this.cursor.getXY().getXpos(),this.cursor.getXY().getYpos());

					int linesStart = this.cursor.getXY().getYpos() / KeyEventHandler.textHeight;


					Node start;
					Node closestToClick;

					start = lines.get(linesStart);

					closestToClick = start;

					while (start != null) {

						if (start.item.getXYpoint().compare(start.item.getXYpoint(), mouseClickPoint) <= 0) {
							closestToClick = start;
						}

						start = start.next;
					}

					this.textdisplay.setClosestNodeToCursor(closestToClick);

					int newx = closestToClick.item.getXYpoint().getXpos() + (int)Math.ceil(closestToClick.item.getLayoutBounds().getWidth());
					int newy = closestToClick.item.getXYpoint().getYpos();

					this.cursor.resetXYForClick(newx, newy);
				}

			} else if (code == KeyCode.DOWN) {

				if(lines.size() != 0 && this.cursor.getXY().getYpos()/KeyEventHandler.textHeight != lines.size()-1) {

					this.cursor.resetXY(0, KeyEventHandler.textHeight);

					Point mouseClickPoint = new Point(this.cursor.getXY().getXpos(),this.cursor.getXY().getYpos());

					int linesStart = this.cursor.getXY().getYpos() / KeyEventHandler.textHeight;


					Node start;
					Node closestToClick;

					start = lines.get(linesStart);

					closestToClick = start;

					while (start != null) {

						if (start.item.getXYpoint().compare(start.item.getXYpoint(), mouseClickPoint) <= 0) {
							closestToClick = start;
						}

						start = start.next;
					}

					this.textdisplay.setClosestNodeToCursor(closestToClick);

					int newx = closestToClick.item.getXYpoint().getXpos() + (int)Math.ceil(closestToClick.item.getLayoutBounds().getWidth());
					int newy = closestToClick.item.getXYpoint().getYpos();

					this.cursor.resetXYForClick(newx, newy);
				}

			} else if (code == KeyCode.RIGHT) {


				Node temp = this.textdisplay.getClosestNodeToCursor();

				if (temp != null) {

					if (temp.next != null) {

						int x = (int) Math.ceil(temp.next.item.getXYpoint().getXpos()) + (int)Math.ceil(temp.next.item.getLayoutBounds().getWidth());
						int y = temp.next.item.getXYpoint().getYpos();

						this.cursor.resetXYForClick(x, y);

						this.textdisplay.setClosestNodeToCursor(temp.next);
					}

				}


			} else if (code == KeyCode.LEFT) {


				Node temp = this.textdisplay.getClosestNodeToCursor();

				if (temp != null) {

					int x = (int)Math.ceil(temp.item.getXYpoint().getXpos());
					int y = (int) Math.ceil(temp.item.getXYpoint().getYpos());

					this.cursor.resetXYForClick(x, y);

					if (temp.prev != null) {
						this.textdisplay.setClosestNodeToCursor(temp.prev);
					} 

				}

			}

		}

	}

	public void setSceneWidth(int sceneWidth) {
		KeyEventHandler.sceneWidth = sceneWidth;
	}

	public void setSceneHeight(int sceneHeight) {
		KeyEventHandler.sceneHeight = sceneHeight;
	}

	public void getAdjustedSceneWidth() {


	}

	public void rerender() {

		Iterator<Node> it = this.textdisplay.iterator();

		this.lines.clear(); // clears all lines

		Node textbox = null;
		Node lastspace = null;

		int xpos = Editor.margin;
		int ypos = 0;
		
		int count = 0;

		while (it.hasNext()) {

			textbox = it.next();
			

			if (textbox.item.getText().compareTo("\n") == 0) {

				xpos = Editor.margin;
				ypos += KeyEventHandler.textHeight;
				this.lines.add(textbox);
				lastspace = null; // only care about spaces on current line

			} else if (textbox.item.getText().compareTo(" ") == 0) {
				lastspace = textbox;
			} else if ((xpos + (int)Math.ceil(textbox.item.getLayoutBounds().getWidth())) 
					> (KeyEventHandler.sceneWidth - Editor.margin - this.scrollbar.getWidth())) {

				xpos = Editor.margin;
				ypos += KeyEventHandler.textHeight;

				if (lastspace != null) {

					Node locationAfterSpace = lastspace.next; // will always have a last elemenet based on iterator

					this.lines.add(locationAfterSpace); // start of new line

					for(Node temp = locationAfterSpace; temp != textbox; temp = temp.next) {

						temp.item.resetXYForRendering(xpos, ypos);
						xpos += (int)Math.ceil(temp.item.getLayoutBounds().getWidth());

					}

					lastspace = null;

				} else {
					//lastspace = null	
					this.lines.add(textbox);
				}
				

			}
			
			
			if (count == 0) {
				this.lines.add(textbox);
				count += 1;
			}

			textbox.item.resetXYForRendering(xpos, ypos);

			xpos += (int)Math.ceil(textbox.item.getLayoutBounds().getWidth());

		}

		this.textdisplay.adjustCursorXY();

		if((this.lines.size()*KeyEventHandler.textHeight) - KeyEventHandler.sceneHeight > 0) {
			this.scrollbar.setMax((lines.size()*KeyEventHandler.textHeight) - KeyEventHandler.sceneHeight);
		} else {
			this.scrollbar.setMax(0);
		}

	}



}
