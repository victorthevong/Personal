package OpenAndSave;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import editor.Editor;
import editor.KeyEventHandler;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import BlinkingCursor.BlinkingCursor;
import Texts.TextBox;
import Texts.TextBoxLinkedListDeque;
import Texts.TextBoxLinkedListDeque.Node;

public class Open {

	private static String filename;
	private TextBoxLinkedListDeque textdisplay;
	private Group root;
	private EventHandler<KeyEvent> keyEventHandler;
	private BlinkingCursor cursor;


	public Open(String filename) {

		Open.filename = filename;

		this.textdisplay = null;
		this.root = null;
		this.cursor = null;
		this.keyEventHandler = null;

	}
	
	public void setKeyEventHandler(EventHandler<KeyEvent> keyEventHandler) {
		this.keyEventHandler = keyEventHandler;
	}
	public void setCursor(BlinkingCursor cursor) {
		this.cursor = cursor;
	}
	public void setGroupRoot(Group root) {
		this.root = root;
	}
	public void setTextDisplay(TextBoxLinkedListDeque textdisplay) {
		this.textdisplay = textdisplay;
	}


	public void save() {

		try {

			File file = new File(Open.filename);

			if (file.exists()) {

				FileWriter writer = new FileWriter(Open.filename);

				Iterator<Node> it = this.textdisplay.iterator();

				while(it.hasNext()) {
					writer.write(it.next().item.getText());
				}

				writer.close();

			} 

		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("File not found! Exception was: " + fileNotFoundException);
		} catch (IOException ioException) {
			System.out.println("Error when copying; exception was: " + ioException);
		}

	}

	public void tryToOpen() {

		try {

			File file = new File(Open.filename);

			if (file.exists()) {

				FileReader reader = new FileReader(Open.filename);
				// It's good practice to read files using a buffered reader.  A buffered reader reads
				// big chunks of the file from the disk, and then buffers them in memory.  Otherwise,
				// if you read one character at a time from the file using FileReader, each character
				// read causes a separate read from disk.  You'll learn more about this if you take more
				// CS classes, but for now, take our word for it!
				BufferedReader bufferedReader = new BufferedReader(reader);

				int intRead = -1;
				// Keep reading from the file input read() returns -1, which means the end of the file
				// was reached.
				while ((intRead = bufferedReader.read()) != -1) {
					// The integer read can be cast to a char, because we're assuming ASCII.
					String characterTyped = Character.toString((char) intRead); 

					if (characterTyped.length() > 0 && !characterTyped.equals("\r") && characterTyped.charAt(0) != 8) {
						// Ignore control keys, which have non-zero length, as well as the backspace
						// key, which is represented as a character of value = 8 on Windows.

						TextBox temp = new TextBox(this.cursor.getXY().getXpos(),this.cursor.getXY().getYpos(),characterTyped, "none");

						//						System.out.println(temp.getText());
						this.textdisplay.addToCursorPosition(temp);
						//						System.out.println("\n " + textdisplay.toString() + "\n" );

						this.root.getChildren().add(temp);

						this.cursor.resetXY((int) Math.ceil(temp.getLayoutBounds().getWidth()), 0); //must change 0 to something else later

						((KeyEventHandler) keyEventHandler).rerender();

					} else if (characterTyped.equals("\r") && this.textdisplay.size() != 0) {

						//						System.out.println(this.cursor.getXY().getYpos());

						this.cursor.resetXYForNewLine(Editor.margin, KeyEventHandler.textHeight); // will probably have to change later

						TextBox temp = new TextBox(Editor.margin ,this.cursor.getXY().getYpos(),"\n" , "none");
						//						System.out.println(temp.getLayoutBounds().getWidth());


						this.textdisplay.addToCursorPosition(temp);
						this.root.getChildren().add(temp);

						((KeyEventHandler) keyEventHandler).rerender();

					}		

					// need to use keyevent.handle here
				}


				bufferedReader.close();

			} 

		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("File not found! Exception was: " + fileNotFoundException);
		} catch (IOException ioException) {
			System.out.println("Error when copying; exception was: " + ioException);
		}

	}


}
