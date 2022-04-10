package luxoft.ch.path;

import java.awt.Point;
import java.util.Objects;

public record Advance(Point from, Point to, int stepCount) {
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Advance a) {
			return Objects.equals(to, a.to);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(to);
	}

	@Override
	public String toString() {
		return new StringBuilder().append("from=").append(from.toString()).append(",").append("to=")
				.append(to.toString()).append(",").append("steps=").append(stepCount).toString();
	}

}
