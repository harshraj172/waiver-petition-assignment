package intervals;

import java.util.Stack;

/**
 * This class represents an expression tree for interval operations.
 * It can parse postfix interval expressions and evaluate them, as well as
 * provide tree visualization.
 */
public class IntervalTree implements Intervals {

  /**
   * Abstract base class for interval expression tree nodes.
   */
  private abstract static class Node {
    /**
     * Evaluates this node and returns the resulting interval.
     *
     * @return the evaluated interval
     */
    abstract Interval evaluate();

    /**
     * Returns the text tree representation of this node.
     *
     * @param prefix the prefix for formatting
     * @param isLast whether this is the last child
     * @return the text tree string
     */
    abstract String textTree(String prefix, boolean isLast);

    /**
     * Returns the height of this node in the tree.
     *
     * @return the height as an integer
     */
    abstract int getHeight();
  }

  /**
   * Node representing an interval operand.
   */
  private static class IntervalNode extends Node {
    private final Interval interval;

    /**
     * Constructs an interval node with the given interval.
     *
     * @param interval the interval value
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
   * Node representing a binary operator (Union or Intersection).
   */
  private static class OperatorNode extends Node {
    private final String operator;
    private final Node left;
    private final Node right;

    /**
     * Constructs an operator node.
     *
     * @param operator the operator symbol (U or I)
     * @param left the left child node
     * @param right the right child node
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
          // should never happen if isOperator is correct
          throw new IllegalArgumentException("Unknown operator: " + operator);
      }
    }

    @Override
    String textTree(String prefix, boolean isLast) {
      StringBuilder result = new StringBuilder();

      result.append(operator).append("\n");

      result.append(prefix).append("|\n");
      result.append(prefix).append("|\n");

      result.append(prefix).append("|___");
      String leftResult = left.textTree(prefix + "|   ", false);
      result.append(leftResult);

      if (!leftResult.endsWith("\n")) {
        result.append("\n");
      }

      result.append(prefix).append("|\n");

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
   * Constructs an IntervalTree from a postfix interval expression string.
   *
   * @param postfixExpression the postfix expression as a space-separated string
   * @throws IllegalArgumentException if the expression is invalid
   */
  public IntervalTree(String postfixExpression) throws IllegalArgumentException {
    if (postfixExpression == null || postfixExpression.trim().isEmpty()) {
      throw new IllegalArgumentException("Expression cannot be null or empty");
    }

    this.root = parsePostfix(postfixExpression.trim());
  }

  /**
   * Parses a postfix interval expression and builds the expression tree.
   *
   * @param expression the postfix expression
   * @return the root node of the expression tree
   * @throws IllegalArgumentException if the expression is invalid
   */
  private Node parsePostfix(String expression) throws IllegalArgumentException {
    String[] tokens = expression.split("\\s+");
    Stack<Node> stack = new Stack<>();

    for (String token : tokens) {
      if (isOperator(token)) {
        if (stack.size() < 2) {
          throw new IllegalArgumentException(
              "Invalid expression: insufficient operands for operator " + token);
        }
        Node right = stack.pop();
        Node left = stack.pop();
        stack.push(new OperatorNode(token, left, right));
      } else {
        // Prse as an interval
        try {
          Interval interval = parseInterval(token);
          stack.push(new IntervalNode(interval));
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid interval: " + token);
        }
      }
    }

    // Check that we have exactly one element left (the root)
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
   * Parses an interval string in the format "start,end".
   * Supports both positive and negative integers.
   *
   * @param intervalStr the interval string
   * @return the parsed Interval object
   * @throws IllegalArgumentException if the format is invalid
   */
  private Interval parseInterval(String intervalStr) throws IllegalArgumentException {
    int commaIndex = intervalStr.indexOf(',');
    if (commaIndex == -1) {
      throw new IllegalArgumentException("Invalid interval format: " + intervalStr);
    }

    if (intervalStr.indexOf(',', commaIndex + 1) != -1) {
      throw new IllegalArgumentException("Invalid interval format: " + intervalStr);
    }

    String startStr = intervalStr.substring(0, commaIndex).trim();
    String endStr = intervalStr.substring(commaIndex + 1).trim();

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
   * Checks if a token is a valid operator.
   *
   * @param token the token to check
   * @return true if the token is an operator, false otherwise
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