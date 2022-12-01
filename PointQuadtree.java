import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * @author Nicolas Schaievitch
 * @author Shaamil Shaw Alem
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		if(p2.getX()<=point.getX()&&p2.getY()<=point.getY())
		{
			if(hasChild(2))
			{
				c2.insert(p2);
			}
			else {
				c2 = new PointQuadtree<>(p2, x1, y1, (int) point.getX(), (int) point.getY());
			}
		}
		if(p2.getX()>point.getX()&&p2.getY()<=point.getY())
		{
			if(hasChild(1))
			{
				c1.insert(p2);
			}
			else
			{
				c1 = new PointQuadtree<>(p2, (int)point.getX(), y1, x2, (int) point.getY());
			}
		}
		if(p2.getX()<=point.getX()&&p2.getY()>point.getY())
		{
			if(hasChild(3))
			{
				c3.insert(p2);
			}
			else
			{
				c3 = new PointQuadtree<>(p2, x1, (int) point.getY(), (int) point.getX(), y2);
			}
		}
		if(p2.getX()>point.getX()&&p2.getY()>point.getY())
		{
			if(hasChild(4))
			{
				c4.insert(p2);
			}
			else {
				c4 = new PointQuadtree<>(p2, (int)point.getX(),(int)point.getY(),x2,y2);
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int num =1;
		if(hasChild(1)) num += c1.size();
		if(hasChild(2)) num += c2.size();
		if(hasChild(3)) num += c3.size();
		if(hasChild(4)) num += c4.size();
        return num;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		List<E> points = new ArrayList<E>();
		addToList(points);
		return points;
	}

	/**
	 * Recursive helper function for allPoints()
	 * @param points accumulative list of points
	 */
	public void addToList(List<E> points)
	{
		points.add(point);
		if(hasChild(1)) c1.addToList(points);
		if(hasChild(2)) c2.addToList(points);
		if(hasChild(3)) c3.addToList(points);
		if(hasChild(4)) c4.addToList(points);
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		List<E> points = new ArrayList<E>();
		addToFinds(cx, cy, cr, points);
		return points;
	}

	public void addToFinds(double cx, double cy, double cr, List<E> points) {
		if(Geometry.circleIntersectsRectangle(cx,cy,cr,x1,y1,x2,y2))
		{
			if(Geometry.pointInCircle(point.getX(),point.getY(),cx,cy,cr))
			{
				points.add(point);
			}
			for (int i = 1; i <= 4; i++) {
				if (hasChild(i)) getChild(i).addToFinds(cx, cy, cr, points);
			}

		}
	}


}
