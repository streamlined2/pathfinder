package luxoft.ch.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Stream;

public class Sleuth implements PathFinder {

	private static final Function<Point, Point> POINT_KEY_EXTRACTOR = Function.<Point>identity();
	private static final Function<Point, Integer> ROW_KEY_EXTRACTOR = (Point p) -> p.y;

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

	private List<Advance> makeWave(Advance advance, Map<Point, Advance> inspected) {
		Point center = advance.to();
		return Stream
				.of(getNeighbor(center, Direction.LEFT), getNeighbor(center, Direction.UP),
						getNeighbor(center, Direction.RIGHT), getNeighbor(center, Direction.DOWN))
				.filter(candidate -> isAcceptable(candidate, inspected))
				.map(candidate -> new Advance(center, candidate, advance.stepCount() + 1)).toList();
	}

	private boolean isAcceptable(Point candidate, Map<Point, Advance> inspected) {
		return board.isValid(candidate) && !board.isObstructed(candidate) && !inspected.containsKey(candidate);
	}

	private <R> Optional<Point> isTargetReached(List<Advance> wave, R value, Function<Point, R> keyExtractor) {
		return wave.stream().map(Advance::to).filter(point -> keyExtractor.apply(point).equals(value)).findFirst();
	}

	private <R> List<Point> findPath(Point start, R target, Function<Point, R> keyExtractor) {
		Queue<Advance> toBeInspected = new LinkedList<>();
		Map<Point, Advance> inspected = new HashMap<>();
		toBeInspected.add(new Advance(null, start, 0));
		do {
			Advance advance = toBeInspected.poll();
			if (advance == null) {
				break;
			}
			inspected.put(advance.to(), advance);
			List<Advance> wave = makeWave(advance, inspected);
			Optional<Point> targetPoint = isTargetReached(wave, target, keyExtractor);
			if (targetPoint.isPresent()) {
				return constructPath(targetPoint.get(), inspected, wave);
			}
			toBeInspected.addAll(wave);
		} while (!toBeInspected.isEmpty());
		return null;
	}

	private List<Point> constructPath(Point target, Map<Point, Advance> inspected, List<Advance> wave) {
		List<Point> path = new ArrayList<>();
		path.add(0, target);
		Optional<Advance> step = wave.stream().filter(advance -> advance.to().equals(target)).findFirst();
		while (step.isPresent()) {
			Point previous = step.get().from();
			if (previous == null) {
				break;
			}
			path.add(0, previous);
			step = Optional.ofNullable(inspected.get(previous));
		}
		return path;
	}

	@Override
	public List<Point> findPath(int[][] board, int startX, int startY, int destY) {
		setBoard(new Board(board));
		return findPath(new Point(startX, startY), Integer.valueOf(destY), ROW_KEY_EXTRACTOR);
	}

	@Override
	public List<Point> findPath(int[][] board, int startX, int startY, int destX, int destY) {
		setBoard(new Board(board));
		return findPath(new Point(startX, startY), new Point(destX, destY), POINT_KEY_EXTRACTOR);
	}

	public static void main(String... args) {
		int[][] board = new int[][] { { 0, 1, 0, 0, 0 }, { 0, 0, 0, 1, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 0 },
				{ 1, 1, 0, 0, 0 } };

		Sleuth sleuth = new Sleuth();
		List<Point> path = sleuth.findPath(board, 2, 0, 4, 4);
		printPath(path);

		path = sleuth.findPath(board, 2, 0, 2);
		printPath(path);
	}

	private static void printPath(List<Point> path) {
		if (path != null) {
			System.out.printf("found path: %n%s%n", path);
		} else {
			System.out.println(String.format("can't find path"));
		}
	}

}
