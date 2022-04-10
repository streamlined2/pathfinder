package luxoft.ch.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public class Sleuth implements PathFinder {

	private Board board;

	public void setBoard(Board board) {
		this.board = board;
	}

	enum Direction {
		LEFT, UP, RIGHT, DOWN
	}

	private Point getNeighbor(Point point, Direction direction) {
		return switch (direction) {
		case LEFT -> new Point(point.x - 1, point.y);
		case UP -> new Point(point.x, point.y - 1);
		case RIGHT -> new Point(point.x + 1, point.y);
		case DOWN -> new Point(point.x, point.y + 1);
		};
	}

	private List<Advance> makeWave(Advance advance, Set<Advance> inspected) {
		Point center = advance.to();
		return Stream
				.of(getNeighbor(center, Direction.LEFT), getNeighbor(center, Direction.UP),
						getNeighbor(center, Direction.RIGHT), getNeighbor(center, Direction.DOWN))
				.filter(candidate -> isAcceptable(candidate, inspected))
				.map(candidate -> new Advance(center, candidate, advance.stepCount() + 1)).toList();
	}

	private boolean isAcceptable(Point candidate, Set<Advance> inspected) {
		return board.isValid(candidate) && !board.isObstructed(candidate)
				&& !isAlreadyInspected(candidate, inspected);
	}

	private boolean isAlreadyInspected(Point candidate, Set<Advance> inspected) {
		return inspected.stream().map(Advance::to).anyMatch(candidate::equals);
	}

	private Optional<Point> isTargetReached(List<Advance> wave, Point target) {
		return wave.stream().map(Advance::to).filter(target::equals).findFirst();
	}

	private Optional<Point> isTargetReached(List<Advance> wave, int y) {
		return wave.stream().map(Advance::to).filter(point -> point.y == y).findFirst();
	}

	private List<Point> findPath(Point start, Point target) {
		Queue<Advance> toBeInspected = new LinkedList<>();
		Set<Advance> inspected = new HashSet<>();
		toBeInspected.add(new Advance(null, start, 0));
		do {
			Advance advance = toBeInspected.poll();
			if (advance == null) {
				break;
			}
			inspected.add(advance);
			List<Advance> wave = makeWave(advance, inspected);
			Optional<Point> targetPoint = isTargetReached(wave, target);
			if (targetPoint.isPresent()) {
				return constructPath(targetPoint.get(), inspected, wave);
			}
			toBeInspected.addAll(wave);
		} while (!toBeInspected.isEmpty());
		return null;
	}

	private List<Point> constructPath(Point target, Set<Advance> inspected, List<Advance> wave) {
		List<Point> path = new ArrayList<>();
		path.add(0, target);
		Optional<Advance> step = wave.stream().filter(advance -> advance.to().equals(target)).findFirst();
		while (step.isPresent()) {
			Point previous = step.get().from();
			if (previous == null) {
				break;
			}
			path.add(0, previous);
			step = inspected.stream().filter(advance -> advance.to().equals(previous)).findFirst();
		}
		return path;
	}

	private List<Point> findPath(Point start, int targetY) {
		Queue<Advance> toBeInspected = new LinkedList<>();
		Set<Advance> inspected = new HashSet<>();
		toBeInspected.add(new Advance(null, start, 0));
		do {
			Advance advance = toBeInspected.poll();
			if (advance == null) {
				break;
			}
			inspected.add(advance);
			List<Advance> wave = makeWave(advance, inspected);
			Optional<Point> targetPoint = isTargetReached(wave, targetY);
			if (targetPoint.isPresent()) {
				return constructPath(targetPoint.get(), inspected, wave);
			}
			toBeInspected.addAll(wave);
		} while (!toBeInspected.isEmpty());
		return null;
	}

	@Override
	public List<Point> findPath(int[][] board, int startX, int startY, int destY) {
		setBoard(new Board(board));

		Point from = new Point(startX, startY);

		return findPath(from, destY);
	}

	@Override
	public List<Point> findPath(int[][] board, int startX, int startY, int destX, int destY) {
		setBoard(new Board(board));

		Point from = new Point(startX, startY);
		Point to = new Point(destX, destY);

		return findPath(from, to);
	}

	public static void main(String... args) {
		int[][] board = new int[][] { { 0, 1, 0, 0, 0 }, { 0, 0, 0, 1, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 0 },
				{ 1, 1, 0, 0, 0 } };

		Sleuth sleuth = new Sleuth();
		List<Point> path = sleuth.findPath(board, 2, 0, 4, 4);
		if (path != null) {
			System.out.printf("found path: %n%s%n", path);
		} else {
			System.out.println(String.format("can't find path"));
		}

		path = sleuth.findPath(board, 2, 0, 2);
		if (path != null) {
			System.out.printf("found path: %n%s%n", path);
		} else {
			System.out.println(String.format("can't find path"));
		}
	}

}
