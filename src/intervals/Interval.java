package intervals;

import java.util.Objects;

/**
 * Represents a 1D interval with integer start and end points.
 */
public class Interval {
  private int start;
  private int end;

  /**
   * Creates an interval from start to end.
   *
   * @param start starting point
   * @param end ending point
   * @throws IllegalArgumentException if start > end
   */
  public Interval(int start, int end) throws IllegalArgumentException {
    if (start > end) {
      throw new IllegalArgumentException("Invalid interval");
    }
    this.start = start;
    this.end = end;
  }

  /**
   * Finds the intersection with another interval.
   * Returns [MIN_VALUE, MIN_VALUE] if intervals don't overlap.
   *
   * @param other the interval to intersect with
   * @return intersection interval
   */
  public Interval intersect(Interval other) {
    int newStart = Math.max(this.start, other.start);
    int newEnd = Math.min(this.end, other.end);

    // No overlap case
    if (newStart > newEnd) {
      return new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    return new Interval(newStart, newEnd);
  }

  /**
   * Returns the union with another interval.
   *
   * @param other the interval to union with
   * @return union interval
   */
  public Interval union(Interval other) {
    return new Interval(
        Math.min(this.start, other.start),
        Math.max(this.end, other.end)
    );
  }

  /**
   * String representation as "start,end".
   *
   * @return interval as string
   */
  @Override
  public String toString() {
    return start + "," + end;
  }

  /**
   * Checks if two intervals are equal.
   *
   * @param obj object to compare
   * @return true if intervals have same start and end
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Interval)) {
      return false;
    }

    Interval that = (Interval) obj;
    return this.start == that.start && this.end == that.end;
  }

  /**
   * Hash code based on start and end values.
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }
}