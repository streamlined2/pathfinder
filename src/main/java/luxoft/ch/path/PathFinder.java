package luxoft.ch.path;

import java.awt.Point;
import java.util.List;

public interface PathFinder {

	List<Point> findPath(int[][] board, int startX, int startY, int destY);

	List<Point> findPath(int[][] board, int startX, int startY, int destX, int destY);

}
