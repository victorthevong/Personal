package Texts;

public class Point extends PointComparator {
	
	private int xpos;
	private int ypos;
	
	public Point(int x, int y) {
		this.xpos = x;
		this.ypos = y;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	
	public void appendXpos(int x) {
		this.xpos += x;
	}
	
	public void appendYpos(int y) {
		this.ypos += y;
	}
	
	public void printPoint() {
		System.out.println( "x: "+ this.xpos + " y: " + this.ypos);
	}

}
