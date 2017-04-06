package Texts;

import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

	@Override
	public int compare(Point p1, Point p2) {
		
		if(p1.getXpos() == p2.getXpos() && p1.getYpos() == p2.getYpos()) {
			return 0; // p1 is enitrely equal to p2
		} else if ( p1.getYpos() == p2.getYpos()) { // p1.y = p2.y
			
			if(p1.getXpos() < p2.getXpos()) {
				return -1; // p1 is less than p2 with same y values
			} else {
				return 1; // p1 is greater than p2 with same y values
			}
			
		} else if ( p1.getYpos() > p2.getYpos()) {
			return 1; // p1 is postion lower in editor then p2
		} else {
			return -1; // p1 is position higher in editor than p2
		}
		
	}

}
