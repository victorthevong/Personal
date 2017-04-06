package editor;

import java.util.ArrayList;

import Texts.Point;
import Texts.TextBoxLinkedListDeque;
import Texts.TextBoxLinkedListDeque.Node;
import BlinkingCursor.BlinkingCursor;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseEventHandler implements EventHandler<MouseEvent> {

	private BlinkingCursor cursor;
	private TextBoxLinkedListDeque textdisplay;
	private ArrayList<Node> lines;

	MouseEventHandler(BlinkingCursor cursor, TextBoxLinkedListDeque textdisplay, ArrayList<Node> lines) {

		this.cursor = cursor;

		this.textdisplay = textdisplay;

		this.lines = lines;

	}


	@Override
	public void handle(MouseEvent mouseEvent) {

		if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {

			int ypos = (int) Math.ceil(mouseEvent.getY()) - (int)Math.ceil(mouseEvent.getY() % KeyEventHandler.textHeight);
			int xpos = (int) Math.ceil(mouseEvent.getX());

			Point mouseClickPoint = new Point(xpos,ypos);

			Print.printLine("Mouse clicked x :" + xpos + " Mouse Clicked y :" + ypos);

			int linesStart = ypos/KeyEventHandler.textHeight;
			Node start;
			Node closestToClick;
			int newx = 0;
			int newy = 0;

			if(lines.size() != 0) {


				if (linesStart > lines.size()-1) {

					linesStart = lines.size() - 1;

				}


				start = lines.get(linesStart);

				closestToClick = start;

				while (start != null) {

					if (start.item.getXYpoint().compare(start.item.getXYpoint(), mouseClickPoint) <= 0) {
						closestToClick = start;
					}

					start = start.next;
				}

				newx = (int) Math.ceil(closestToClick.item.getXYpoint().getXpos()) + (int) Math.ceil(closestToClick.item.getLayoutBounds().getWidth());

				newy = (int) Math.ceil(closestToClick.item.getXYpoint().getYpos());

				this.textdisplay.setClosestNodeToCursor(closestToClick);

				this.cursor.resetXYForClick(newx, newy);
				
				if (newx > xpos && ypos == newy) {
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

	}

}




