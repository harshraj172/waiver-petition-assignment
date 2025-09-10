import static org.junit.Assert.assertEquals;

import expression.Expression;
import expression.ExpressionTree;
import org.junit.Test;

/**
 * Tests for ExpressionTree class.
 */
public class ExpressionTreeTest {

  private static final double DELTA = 0.0001;

  // Basic tests

  @Test
  public void testSingleValue() {
    Expression expr = new ExpressionTree("42");
    assertEquals(42.0, expr.evaluate(), DELTA);
    assertEquals("42.0", expr.infix());
    assertEquals("42.0", expr.schemeExpression());
    assertEquals("42.0", expr.textTree());
  }

  @Test
  public void testAddTwoNumbers() {
    Expression expr = new ExpressionTree("1 2 +");
    assertEquals(3.0, expr.evaluate(), DELTA);
    assertEquals("( 1.0 + 2.0 )", expr.infix());
    assertEquals("( + 1.0 2.0 )", expr.schemeExpression());

    // check tree format
    String tree = "+\n|\n|\n|___1.0\n|\n|___2.0";
    assertEquals(tree, expr.textTree());
  }

  @Test
  public void testSubtract() {
    Expression e = new ExpressionTree("5 3 -");
    assertEquals(2.0, e.evaluate(), DELTA);
    assertEquals("( 5.0 - 3.0 )", e.infix());
    assertEquals("( - 5.0 3.0 )", e.schemeExpression());
  }

  @Test
  public void testMultiply() {
    Expression e = new ExpressionTree("4 5 *");
    double result = e.evaluate();
    assertEquals(20.0, result, DELTA);
    assertEquals("( 4.0 * 5.0 )", e.infix());
    assertEquals("( * 4.0 5.0 )", e.schemeExpression());
  }

  @Test
  public void testDivide() {
    Expression e = new ExpressionTree("10 2 /");
    assertEquals(5.0, e.evaluate(), DELTA);
    String infix = e.infix();
    assertEquals("( 10.0 / 2.0 )", infix);
    assertEquals("( / 10.0 2.0 )", e.schemeExpression());
  }

  // Complex expressions

  @Test
  public void testNestedOps() {
    // (1 + 2) * 3 = 9
    Expression e = new ExpressionTree("1 2 + 3 *");
    assertEquals(9.0, e.evaluate(), DELTA);
    assertEquals("( ( 1.0 + 2.0 ) * 3.0 )", e.infix());
    assertEquals("( * ( + 1.0 2.0 ) 3.0 )", e.schemeExpression());
  }

  @Test
  public void testComplexNesting() {
    // 1 / ((4 - 6) + 5) = 1/3
    Expression expr = new ExpressionTree("1 4 6 - 5 + /");
    double val = expr.evaluate();
    assertEquals(0.33333, val, 0.00001);

    String infixStr = expr.infix();
    assertEquals("( 1.0 / ( ( 4.0 - 6.0 ) + 5.0 ) )", infixStr);

    String scheme = expr.schemeExpression();
    assertEquals("( / 1.0 ( + ( - 4.0 6.0 ) 5.0 ) )", scheme);
  }

  @Test
  public void testSpacesAndTabs() {
    // should handle extra whitespace
    Expression e1 = new ExpressionTree("  1   2   +  ");
    assertEquals(3.0, e1.evaluate(), DELTA);

    Expression e2 = new ExpressionTree("1\t2\t+");
    assertEquals(3.0, e2.evaluate(), DELTA);
  }

  @Test
  public void testTreeVisualization() {
    Expression expr = new ExpressionTree("1 4 6 - 5 + /");
    String tree = "/\n"
        + "|\n"
        + "|\n"
        + "|___1.0\n"
        + "|\n"
        + "|___+\n"
        + "    |\n"
        + "    |\n"
        + "    |___-\n"
        + "    |   |\n"
        + "    |   |\n"
        + "    |   |___4.0\n"
        + "    |   |\n"
        + "    |   |___6.0\n"
        + "    |\n"
        + "    |___5.0";
    assertEquals(tree, expr.textTree());
  }

  // Test number formatting

  @Test
  public void testIntegerFormat() {
    Expression e1 = new ExpressionTree("5");
    // integers should show as X.0
    assertEquals("5.0", e1.infix());
    assertEquals("5.0", e1.schemeExpression());
    assertEquals("5.0", e1.textTree());

    Expression e2 = new ExpressionTree("-10");
    assertEquals("-10.0", e2.infix());
  }

  @Test
  public void testDecimalFormat() {
    Expression e1 = new ExpressionTree("5.5");
    // decimals stay as-is
    assertEquals("5.5", e1.infix());
    assertEquals("5.5", e1.schemeExpression());
    assertEquals("5.5", e1.textTree());

    // very small difference from integer
    Expression e2 = new ExpressionTree("3.00000000001");
    assertEquals("3.00000000001", e2.infix());
  }

  @Test
  public void testNegativeNumbers() {
    Expression e = new ExpressionTree("3 -2 +");
    assertEquals(1.0, e.evaluate(), DELTA);
    String inf = e.infix();
    assertEquals("( 3.0 + -2.0 )", inf);
  }

  @Test
  public void testWithZero() {
    Expression e = new ExpressionTree("0 5 +");
    double res = e.evaluate();
    assertEquals(5.0, res, DELTA);
  }

  @Test
  public void testScientificNumbers() {
    Expression e = new ExpressionTree("1e2 2e1 +");
    assertEquals(120.0, e.evaluate(), DELTA);
  }

  // Error handling tests

  @Test(expected = ArithmeticException.class)
  public void divByZero() {
    Expression e = new ExpressionTree("5 0 /");
    e.evaluate();  // boom
  }

  @Test(expected = ArithmeticException.class)
  public void divByZeroIndirect() {
    // 10 / (5 - 5) should fail
    Expression e = new ExpressionTree("10 5 5 - /");
    e.evaluate();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullInput() {
    new ExpressionTree(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyString() {
    new ExpressionTree("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void justSpaces() {
    new ExpressionTree("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void badToken() {
    new ExpressionTree("1 a +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownOperator() {
    new ExpressionTree("1 2 %");  // we don't support modulo
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEnoughOperands() {
    new ExpressionTree("1 +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEnoughOperands2() {
    new ExpressionTree("1 2 + +");  // second + has no operands
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooManyNumbers() {
    new ExpressionTree("1 2 3 +");  // leaves 1 and result on stack
  }

  @Test(expected = IllegalArgumentException.class)
  public void justOperator() {
    new ExpressionTree("+");
  }

  @Test(expected = IllegalArgumentException.class)
  public void operatorsOnly() {
    new ExpressionTree("+ - *");
  }

  // Check error messages

  @Test
  public void checkErrorMessages() {
    try {
      new ExpressionTree("+ +");
    } catch (IllegalArgumentException e) {
      // should mention insufficient operands
      assertEquals(true, e.getMessage().contains("insufficient operands"));
    }

    try {
      new ExpressionTree("1 2 3 4 +");
    } catch (IllegalArgumentException e) {
      // should mention too many
      assertEquals(true, e.getMessage().contains("too many operands"));
    }
  }
}