package expression;

/**
 * Interface for expression trees that support evaluation
 * and different string representations.
 */
public interface Expression {

  /**
   * Evaluate this expression and return its numeric value.
   *
   * @return the result of the expression as a double
   */
  double evaluate();

  /**
   * Return the infix form of this expression.
   *
   * <p>The infix form is space-separated and uses parentheses
   * to make the order of operations explicit.
   *
   * @return the infix form as a string
   */
  String infix();

  /**
   * Return a Scheme-style representation of this expression.
   *
   * <p>The output is a valid Scheme expression, where operators
   * precede their operands.
   *
   * @return the expression in Scheme syntax
   */
  String schemeExpression();

  /**
   * Return a tree-style textual representation of this expression.
   *
   * <p>For example, the postfix expression {@code "1 2 +"} would look like:
   * <pre>
   *   +
   *   |
   *   |___1.0
   *   |
   *   |___2.0
   * </pre>
   *
   * <p>A larger example such as {@code "1 4 6 - 5 + /"} becomes:
   * <pre>
   *   /
   *   |
   *   |___1.0
   *   |
   *   |___+
   *       |
   *       |___-
   *       |   |
   *       |   |___4.0
   *       |   |
   *       |   |___6.0
   *       |
   *       |___5.0
   * </pre>
   *
   * <p>Formatting rules:
   * <ul>
   *   <li>Left operand starts three lines down and three spaces right of the operator</li>
   *   <li>Right operand starts two lines after the left subtree</li>
   *   <li>Vertical bars (|) connect an operator to its children</li>
   *   <li>Underscores (___) mark horizontal connections</li>
   *   <li>Operators are shown as symbols (+, -, *, /) and operands as numbers</li>
   * </ul>
   *
   * @return the expression drawn as a text tree
   */
  String textTree();
}
