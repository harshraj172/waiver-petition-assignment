package intervals;

/**
 * Interface for interval expression trees with union and intersection operations.
 */
public interface Intervals {
  /**
   * Evaluates the interval expression and returns the result.
   *
   * @return the resulting interval after all operations
   */
  Interval evaluate();

  /**
   * Gets a text representation of the expression tree structure.
   *
   * <p>For instance, "(1,2) (3,4) U" produces:
   * <pre>
   *   U
   *   |
   *   |
   *   |___1,2
   *   |
   *   |___3,4
   * </pre>
   *
   * <p>A more complex example like "(1,2) (3,4) (4,6) U (3,5) I U" gives:
   * <pre>
   *   U
   *   |
   *   |
   *   |___1,2
   *   |
   *   |___I
   *       |
   *       |
   *       |___U
   *       |   |
   *       |   |
   *       |   |___3,4
   *       |   |
   *       |   |___4,6
   *       |
   *       |___3,5
   *
   *</pre>
   *
   * <p>Tree formatting rules:
   * - Left child starts 3 lines down, 3 spaces right from operator
   * - Right child starts 2 lines after left child ends
   * - Vertical lines (|) connect operators to operands
   * - Underscores (___) show horizontal connections
   * - Operators shown as U or I, intervals as start,end
   *
   * @return tree structure as a formatted string
   */
  String textTree();
}
