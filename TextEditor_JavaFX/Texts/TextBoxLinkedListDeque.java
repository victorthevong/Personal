package Texts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import editor.Editor;
import BlinkingCursor.BlinkingCursor;

public class TextBoxLinkedListDeque {

	public class Node {

		public TextBox item;
		public Node next;
		public Node prev;
		public boolean isLastNode;

		public Node(TextBox item) {
			this.item = item;
			this.next = null;
			this.prev = null;
			this.isLastNode = false;
		}

	}

	private ArrayList<Point> points;
	private Node frontman;
	private Node backman;
	private Node deletingFromToNull;
	private Node closestNodeToCursor;
	private BlinkingCursor cursor;
	private int size = 0;


	public TextBoxLinkedListDeque() {
		this.frontman = new Node(null);
		this.backman = new Node(null);
		this.closestNodeToCursor = null;
		this.deletingFromToNull = new Node(new TextBox(""));
		this.points = new ArrayList<Point>();
	}

	public void setCursor(BlinkingCursor node) {
		this.cursor = node;
	}

	public void adjustCursorXY() {
		TextBox temp = this.getClosestNodeTextBox();

		if (temp != null) {
			int newx = temp.getXYpoint().getXpos() + (int)Math.ceil(temp.getLayoutBounds().getWidth());
			this.cursor.resetXYForClick(newx, temp.getXYpoint().getYpos());
		}
	}

	public TextBox getClosestNodeTextBox() {

		if(this.closestNodeToCursor != null) {
			return closestNodeToCursor.item;
		}
		return null;
	}

	public void setClosestNodeToCursor(Node item) {
		this.closestNodeToCursor = item;
	}

	public Node getClosestNodeToCursor() {
		return this.closestNodeToCursor;
	}

	public Node getDeletingFromCursorToNullNode(){
		return this.deletingFromToNull;
	}

	public void setDeletingFromCursorToNullNodeNext(Node item) {
		this.deletingFromToNull.next = item;
	}

	public void setFrontmanNext(Node item) {
		this.frontman.next = item;
	}

	public void setBackmanNext(Node item) {
		this.backman.next = item;
	}

	public void addToCursorPosition(TextBox item) {
//		System.out.println("addLast added " + item.getText());

		Node itemBeforeCursor = new Node(item);
		this.points.add(itemBeforeCursor.item.getXYpoint());

		if (this.backman.next == null && this.frontman.next == null && this.closestNodeToCursor == null ) {
			this.frontman.next = itemBeforeCursor;
			this.backman.next = itemBeforeCursor;
			itemBeforeCursor.isLastNode = true;
			this.closestNodeToCursor = itemBeforeCursor;
			this.closestNodeToCursor.prev = null;
			this.closestNodeToCursor.next = null;

		} else if (this.closestNodeToCursor != null) {

			itemBeforeCursor.prev = this.closestNodeToCursor;

			if (this.closestNodeToCursor.next != null) {
				itemBeforeCursor.next = this.closestNodeToCursor.next;
				this.closestNodeToCursor.next.prev = itemBeforeCursor;
				this.closestNodeToCursor.next = itemBeforeCursor;
				this.closestNodeToCursor = itemBeforeCursor;
			} else {
				itemBeforeCursor.isLastNode = true;
				this.backman.next = itemBeforeCursor;
				this.closestNodeToCursor.next = itemBeforeCursor;
				this.closestNodeToCursor.isLastNode = false;
				this.closestNodeToCursor = itemBeforeCursor;
			}

		}

		this.size += 1;

	} 

	public boolean removeNodeClosestToCursor() {

		Node tempNode = this.closestNodeToCursor;

		if (tempNode != null) {
//			System.out.println("Found node with item to remove");

			this.points.remove(tempNode.item.getXYpoint()); // removes xy point

			if (tempNode.prev != null) {

				this.closestNodeToCursor = tempNode.prev;

				tempNode.prev.next = tempNode.next;

				if (tempNode.next != null) {
					tempNode.next.prev = tempNode.prev;
					tempNode = null;
				} else if (tempNode.next == null) {
					this.backman.next = tempNode.prev;
					tempNode.prev.isLastNode = true;
					tempNode.isLastNode = false;
					tempNode.prev.next = null;
					tempNode = null;
				}

			} else if (tempNode.prev == null) {

				if (tempNode.next != null) {

					this.closestNodeToCursor = deletingFromToNull;
					this.frontman.next = deletingFromToNull;
					deletingFromToNull.next = tempNode.next;
					tempNode.next.prev = deletingFromToNull;
					tempNode = null;
					this.cursor.resetXYForClick(Editor.margin, 0);

				} else {
					this.closestNodeToCursor = null;
					this.frontman.next = null;
					this.backman.next = null;
					this.deletingFromToNull.next = null;
					tempNode = null;
					this.cursor.resetXYForClick(Editor.margin, 0);
				}

			}

			if(this.size > 0) {
				this.size -= 1;
			}

			return true;

		}

		return false;
	}

	public boolean setAndFindClosestNodeToCursor() {

		Collections.sort(points, new PointComparator()); // sorts points

		Iterator<Node> it = this.iterator();

		Point temppoint = null;

		Point cursorPoint = this.cursor.getXY();

		for (Point p : points) {

			if (p.compare(p, cursorPoint) <= 0) {
				temppoint = p;
			} 

		}

		if (temppoint != null) {
			while (it.hasNext()) {

				Node tempnode = it.next();

				if (temppoint.compare(tempnode.item.getXYpoint(), temppoint) == 0) {
					this.closestNodeToCursor = tempnode; // sets closest node to cursor	

					if (tempnode.prev != null) {
						this.closestNodeToCursor.prev = tempnode.prev;
					} else {
						
						if (tempnode.next != null) {

							this.closestNodeToCursor = deletingFromToNull;
							this.frontman.next = deletingFromToNull;
							deletingFromToNull.next = tempnode.next;
							tempnode.next.prev = deletingFromToNull;
							tempnode = null;
							this.cursor.resetXYForClick(Editor.margin, 0);

						} else {
							this.closestNodeToCursor = deletingFromToNull;
							this.frontman.next = null;
							this.backman.next = null;
							this.deletingFromToNull.next = null;
							tempnode = null;
							this.cursor.resetXYForClick(Editor.margin, 0);
						}
						
					}

				}
			}

			return true; // found closests textobject exists

		}

		return false; // unable to find closest point. i.e no characters
	}

	public boolean isEmpty() {

		if (this.size == 0) {
			return true;
		}

		return false;

	}

	public int size() {
		return this.size;
	}

	public void printDeque() {

		Node front = this.frontman;

		while (front.next != null) {
			Node item = front.next;
			System.out.println(item.item.getText());
			front = front.next;
		}

		System.out.println("LinkedList size = " + this.size);

	}

	public String toString() {

		String sent = "";

		Node front = this.frontman;

		while (front.next != null) {
			Node item = front.next;
			sent += item.item.getText();
			front = front.next;
		}

		return sent;

	}
	
	public void clear() {
		this.frontman = new Node(null);
		this.backman = new Node(null);
		this.closestNodeToCursor = null;
		this.deletingFromToNull = new Node(new TextBox(""));
		this.points = new ArrayList<Point>();
	}

	private class ListIterator implements Iterator<Node> {

		public Node front = frontman;

		@Override
		public boolean hasNext() {
			return front.next != null;
		}

		@Override
		public Node next() {
			Node item = front.next;
			front = front.next;
			return item;
		}

	}

	public Iterator<Node> iterator() {
		return new ListIterator();
	}

}