package luxoft.ch.path;

import java.awt.Point;
import java.io.PrintStream;

public class Board {

	public enum Cell {
		UNOBSTRUCTED, OBSTACLE;
	}

	private Cell[][] cells;

	public Board(int width, int height, Point... obstacles) {
		checkDimension(width);
		checkDimension(height);
		cells = new Cell[height][width];
		setup(width, height);
		placeObstacles(obstacles);
	}

	public Board(int[][] board) {
		checkDimension(board.length);
		checkDimension(board[0].length);
		cells = new Cell[board.length][board[0].length];
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				cells[y][x] = board[y][x] == 0 ? Cell.UNOBSTRUCTED : Cell.OBSTACLE;
			}
		}
	}

	private void checkDimension(int dimension) {
		if (dimension <= 0) {
			throw new IllegalArgumentException("dimension %d should be positive".formatted(dimension));
		}
	}

	private void setup(int width, int height) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				cells[i][j] = Cell.UNOBSTRUCTED;
			}
		}
	}

	public void placeObstacles(Point... obstacles) {
		for (var obstacle : obstacles) {
			placeObstacle(obstacle);
		}
	}

	public void placeObstacle(Point position) {
		cells[position.y][position.x] = Cell.OBSTACLE;
	}

	public boolean isObstructed(Point position) {
		return cells[position.y][position.x] == Cell.OBSTACLE;
	}

	public boolean isObstructed(int y, int x) {
		return cells[y][x] == Cell.OBSTACLE;
	}

	public boolean isValid(Point position) {
		return position.y >= 0 && position.y < cells.length && position.x >= 0 && position.x < cells[position.y].length;
	}

	public boolean isValid(int y, int x) {
		return y >= 0 && y < cells.length && x >= 0 && x < cells[y].length;
	}

	public void print(PrintStream os) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				os.print("|" + (cells[i][j] == Cell.OBSTACLE ? "X" : " "));
			}
			os.print("|");
			os.println();
		}
	}

}
