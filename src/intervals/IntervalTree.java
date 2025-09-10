package intervals;

import java.util.Stack;

/**
 * Interval expression tree implementation for union, intersection operations.
 * Parses postfix notation and builds a tree structure.
 */
public class IntervalTree implements Intervals {

  /**
   * Base node class for the tree.
   */
  private abstract static class Node {
    /**
     * Evaluate the node and get its interval.
     *
     * @return resulting interval
     */
    abstract Interval evaluate();

    /**
     * Get text representation of subtree.
     *
     * @param prefix spacing prefix
     * @param isLast is this the last child
     * @return formatted tree string
     */
    abstract String textTree(String prefix, boolean isLast);

    /**
     * Get tree height.
     *
     * @return height
     */
    abstract int getHeight();
  }

  /**
   * Leaf node containing an interval.
   */
  private static class IntervalNode extends Node {
    private final Interval interval;

    /**
     * Create leaf node.
     *
     * @param interval the interval to store
     */
    public IntervalNode(Interval interval) {
      this.interval = interval;
    }

    @Override
    Interval evaluate() {
      return interval;
    }

    @Override
    String textTree(String prefix, boolean isLast) {
      return interval.toString();
    }

    @Override
    int getHeight() {
      return 1;
    }
  }

  /**
   * Internal node for operators (U or I).
   */
  private static class OperatorNode extends Node {
    private final String operator;
    private final Node left;
    private final Node right;

    /**
     * Create operator node.
     *
     * @param operator U for union, I for intersection
     * @param left left subtree
     * @param right right subtree
     */
    public OperatorNode(String operator, Node left, Node right) {
      this.operator = operator;
      this.left = left;
      this.right = right;
    }

    @Override
    Interval evaluate() {
      Interval leftInterval = left.evaluate();
      Interval rightInterval = right.evaluate();

      switch (operator) {
        case "U":
          return leftInterval.union(rightInterval);
        case "I":
          return leftInterval.intersect(rightInterval);
        default:
          // shouldn't happen with valid input
          throw new IllegalArgumentException("Unknown operator: " + operator);
      }
    }

    @Override
    String textTree(String prefix, boolean isLast) {
      StringBuilder result = new StringBuilder();

      // Add operator at top
      result.append(operator).append("\n");

      // Vertical lines
      result.append(prefix).append("|\n");
      result.append(prefix).append("|\n");

      // Left child
      result.append(prefix).append("|___");
      String leftResult = left.textTree(prefix + "|   ", false);
      result.append(leftResult);

      // Add newline if needed
      if (!leftResult.endsWith("\n")) {
        result.append("\n");
      }

      // Spacing before right child
      result.append(prefix).append("|\n");

      // Right child
      result.append(prefix).append("|___");
      String rightResult = right.textTree(prefix + "    ", true);
      result.append(rightResult);

      return result.toString();
    }

    @Override
    int getHeight() {
      return 1 + Math.max(left.getHeight(), right.getHeight());
    }
  }

  private final Node root;

  /**
   * Build tree from postfix expression.
   *
   * @param postfixExpression space-separated postfix string
   * @throws IllegalArgumentException for invalid expressions
   */
  public IntervalTree(String postfixExpression) throws IllegalArgumentException {
    if (postfixExpression == null || postfixExpression.trim().isEmpty()) {
      throw new IllegalArgumentException("Expression cannot be null or empty");
    }

    this.root = parsePostfix(postfixExpression.trim());
  }

  /**
   * Parse postfix and build tree using stack.
   *
   * @param expression postfix string
   * @return root of tree
   * @throws IllegalArgumentException if malformed
   */
  private Node parsePostfix(String expression) throws IllegalArgumentException {
    String[] tokens = expression.split("\\s+");
    Stack<Node> stack = new Stack<>();

    for (String token : tokens) {
      if (isOperator(token)) {
        // Need two operands for binary operator
        if (stack.size() < 2) {
          throw new IllegalArgumentException(
              "Invalid expression: insufficient operands for operator " + token);
        }
        // Pop right then left (reverse order)
        Node right = stack.pop();
        Node left = stack.pop();
        stack.push(new OperatorNode(token, left, right));
      } else {
        // Must be an interval
        try {
          Interval interval = parseInterval(token);
          stack.push(new IntervalNode(interval));
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid interval: " + token);
        }
      }
    }

    // Should have exactly one node left
    if (stack.size() != 1) {
      if (stack.isEmpty()) {
        throw new IllegalArgumentException("Invalid expression: no result");
      } else {
        throw new IllegalArgumentException(
            "Invalid expression: too many operands");
      }
    }

    return stack.pop();
  }

  /**
   * Parse "start,end" format into Interval.
   * Handles negative numbers.
   *
   * @param intervalStr string to parse
   * @return new Interval
   * @throws IllegalArgumentException if bad format
   */
  private Interval parseInterval(String intervalStr) throws IllegalArgumentException {
    // Find comma separator
    int commaIndex = intervalStr.indexOf(',');
    if (commaIndex == -1) {
      throw new IllegalArgumentException("Invalid interval format: " + intervalStr);
    }

    // Check for multiple commas
    if (intervalStr.indexOf(',', commaIndex + 1) != -1) {
      throw new IllegalArgumentException("Invalid interval format: " + intervalStr);
    }

    // Split at comma
    String startStr = intervalStr.substring(0, commaIndex).trim();
    String endStr = intervalStr.substring(commaIndex + 1).trim();

    // Both parts must exist
    if (startStr.isEmpty() || endStr.isEmpty()) {
      throw new IllegalArgumentException("Invalid interval format: " + intervalStr);
    }

    try {
      int start = Integer.parseInt(startStr);
      int end = Integer.parseInt(endStr);
      return new Interval(start, end);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid interval values: " + intervalStr);
    }
  }

  /**
   * Check if token is U or I operator.
   *
   * @param token string to check
   * @return true if operator
   */
  private boolean isOperator(String token) {
    return token.equals("U") || token.equals("I");
  }

  @Override
  public Interval evaluate() {
    return root.evaluate();
  }

  @Override
  public String textTree() {
    return root.textTree("", true);
  }
}