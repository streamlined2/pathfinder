package luxoft.ch.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public class Sleuth implements PathFinder {

	private static final int NEIGHBOR_COUNT = 8;

	private final Board board;
	private final Point start;
	private final Point target;
	private final double shortestPathKey;

	public Sleuth(int[][] terrain, Point start, Point target) {
		board = new Board(terrain);
		this.start = start;
		this.target = target;
		this.shortestPathKey = getPathKey(start);
	}

	enum Direction {
		LEFT, LEFT_UP, UP, UP_RIGHT, RIGHT, RIGHT_DOWN, DOWN, DOWN_LEFT
	}

	private Point getNeighbor(Point point, Direction direction) {
		return switch (direction) {
		case LEFT -> new Point(point.x - 1, point.y);
		case LEFT_UP -> new Point(point.x - 1, point.y - 1);
		case UP -> new Point(point.x, point.y - 1);
		case UP_RIGHT -> new Point(point.x + 1, point.y - 1);
		case RIGHT -> new Point(point.x + 1, point.y);
		case RIGHT_DOWN -> new Point(point.x + 1, point.y + 1);
		case DOWN -> new Point(point.x, point.y + 1);
		case DOWN_LEFT -> new Point(point.x - 1, point.y + 1);
		};
	}

	private void addPointToWaveFront(List<Advance> wave, Point center, Point candidate, Map<Point, Advance> inspected) {
		if (isAcceptable(candidate, inspected)) {
			wave.add(new Advance(center, candidate, getAdvanceKey(candidate)));
		}
	}

	private List<Advance> makeWaveFront(Point center, Map<Point, Advance> inspected) {
		List<Advance> wave = new ArrayList<>(NEIGHBOR_COUNT);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.LEFT), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.LEFT_UP), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.UP), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.UP_RIGHT), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.RIGHT), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.RIGHT_DOWN), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.DOWN), inspected);
		addPointToWaveFront(wave, center, getNeighbor(center, Direction.DOWN_LEFT), inspected);
		return wave;
	}

	private boolean isAcceptable(Point candidate, Map<Point, Advance> inspected) {
		return board.isValid(candidate) && !board.isObstructed(candidate) && !inspected.containsKey(candidate);
	}

	private Optional<Point> isTargetReached(List<Advance> wave) {
		for (var advance : wave) {
			Point point = advance.to();
			if (point.equals(target)) {
				return Optional.of(point);
			}
		}
		return Optional.empty();
	}

	private double getPathKey(Point point) {
		double width = target.x - point.x;
		double height = target.y - point.y;
		return (target.x == point.x) ? Double.MAX_VALUE : height / width;
	}

	private double getAdvanceKey(Point point) {
		return getPathKey(point) - shortestPathKey;
	}

	public List<Point> findPath() {
		Queue<Advance> toBeInspected = new PriorityQueue<>();
		Map<Point, Advance> inspected = new HashMap<>();
		toBeInspected.add(new Advance(null, start, getAdvanceKey(start)));
		do {
			Advance advance = toBeInspected.poll();
			if (advance == null) {
				break;
			}
			inspected.put(advance.to(), advance);
			List<Advance> wave = makeWaveFront(advance.to(), inspected);
			Optional<Point> targetPoint = isTargetReached(wave);
			if (targetPoint.isPresent()) {
				return constructPath(targetPoint.get(), inspected, wave);
			}
			toBeInspected.addAll(wave);
		} while (!toBeInspected.isEmpty());
		return null;
	}

	private List<Point> constructPath(Point target, Map<Point, Advance> inspected, List<Advance> wave) {
		List<Point> path = new LinkedList<>();
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

	public static void main(String... args) {

		Sleuth sleuth = new Sleuth(new int[13][13], new Point(0, 0), new Point(12, 12));
		List<Point> path = sleuth.findPath();
		printPath(path);

	}

	private static void printPath(List<Point> path) {
		if (path != null) {
			System.out.printf("found path: %n%s%n", path);
		} else {
			System.out.println("can't find path");
		}
	}

}
