package editor;

import java.util.ArrayDeque;

import Texts.TextBox;

public class UndoRedoDeque extends ArrayDeque<TextBox> {
	
	private int limit;
	
	public UndoRedoDeque(int limit) {
		super(limit);
		
		this.limit = limit;
	}
	
	@Override
	public void addLast(TextBox e) {
		
		if(super.size() == this.limit) {
			super.removeFirst();
		}
		
		super.addLast(e);
		
	}

}
