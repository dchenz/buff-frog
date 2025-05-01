package dungeon.Model;

import java.awt.Dimension;

/**
 * Coordinate.java
 * 
 * Used in Map and EnemyMover to get tiles
 * 
 *
 */

public class Coordinate {
	private Dimension coordinate;
	
	public Coordinate(int x, int y) {
		coordinate = new Dimension(x, y);
	}
	
	public Coordinate(Coordinate coord) {
		this(coord.X(), coord.Y());
	}
	
	// get positions
	
	public int X() {
		return (int) coordinate.getWidth();
	}
	
	public int Y() {
		return (int) coordinate.getHeight();
	}
	
	// get directions
	
	public Coordinate up() {
		return new Coordinate(X(), Y() - 1);
	}
	
	public Coordinate down() {
		return new Coordinate(X(), Y() + 1);
	}
	
	public Coordinate left() {
		return new Coordinate(X() - 1, Y());
	}
	
	public Coordinate right() {
		return new Coordinate(X() + 1, Y());
	}
	
	// is at the boundaries
	
	public boolean atTop() {
		return Y() == 0;
	}
	
	public boolean atBottom(int height) {
		return Y() == height - 1;
	}
	
	public boolean atLeft() {
		return X() == 0;
	}
	
	public boolean atRight(int width) {
		return X() == width - 1;
	}
	
	// out of bounds
	
	public boolean outside(int height, int width) {
		return (X() < 0 || X() >= width || Y() < 0 || Y() >= height);
	}
	
	// equals method
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (getClass().equals(o.getClass())) {
			Coordinate c = (Coordinate) o;
			return hashCode() == c.hashCode();
		}
		return false;
	}
	
	// hash code is unique for all integer ordered pairs
	// hence why Dimension is used
	
	@Override
	public int hashCode() {
		return coordinate.hashCode();
	}
}
