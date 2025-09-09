import static org.junit.Assert.assertEquals;

import expression.Expression;
import expression.ExpressionTree;
import org.junit.Test;

/**
 * Optimized JUnit 4 tests for ExpressionTree with minimal redundancy.
 */
public class ExpressionTreeTest {

  private static final double DELTA = 0.0001;

  // Core functionality tests

  @Test
  public void testSingleNumber() {
    Expression tree = new ExpressionTree("42");
    assertEquals(42.0, tree.evaluate(), DELTA);
    assertEquals("42.0", tree.infix());
    assertEquals("42.0", tree.schemeExpression());
    assertEquals("42.0", tree.textTree());
  }

  @Test
  public void testSimpleAddition() {
    Expression tree = new ExpressionTree("1 2 +");
    assertEquals(3.0, tree.evaluate(), DELTA);
    assertEquals("( 1.0 + 2.0 )", tree.infix());
    assertEquals("( + 1.0 2.0 )", tree.schemeExpression());
    String expected = "+\n|\n|\n|___1.0\n|\n|___2.0";
    assertEquals(expected, tree.textTree());
  }

  @Test
  public void testSimpleSubtraction() {
    Expression tree = new ExpressionTree("5 3 -");
    assertEquals(2.0, tree.evaluate(), DELTA);
    assertEquals("( 5.0 - 3.0 )", tree.infix());
    assertEquals("( - 5.0 3.0 )", tree.schemeExpression());
  }

  @Test
  public void testSimpleMultiplication() {
    Expression tree = new ExpressionTree("4 5 *");
    assertEquals(20.0, tree.evaluate(), DELTA);
    assertEquals("( 4.0 * 5.0 )", tree.infix());
    assertEquals("( * 4.0 5.0 )", tree.schemeExpression());
  }

  @Test
  public void testSimpleDivision() {
    Expression tree = new ExpressionTree("10 2 /");
    assertEquals(5.0, tree.evaluate(), DELTA);
    assertEquals("( 10.0 / 2.0 )", tree.infix());
    assertEquals("( / 10.0 2.0 )", tree.schemeExpression());
  }

  @Test
  public void testComplexExpression1() {
    // Test "1 2 + 3 *" which is (1 + 2) * 3
    Expression tree = new ExpressionTree("1 2 + 3 *");
    assertEquals(9.0, tree.evaluate(), DELTA);
    assertEquals("( ( 1.0 + 2.0 ) * 3.0 )", tree.infix());
    assertEquals("( * ( + 1.0 2.0 ) 3.0 )", tree.schemeExpression());
  }

  @Test
  public void testComplexExpression4() {
    // Test "1 4 6 - 5 + /" which is 1 / ((4 - 6) + 5)
    Expression tree = new ExpressionTree("1 4 6 - 5 + /");
    assertEquals(1.0 / 3.0, tree.evaluate(), DELTA);
    assertEquals("( 1.0 / ( ( 4.0 - 6.0 ) + 5.0 ) )", tree.infix());
    assertEquals("( / 1.0 ( + ( - 4.0 6.0 ) 5.0 ) )", tree.schemeExpression());
  }

  @Test
  public void testWhitespaceHandling() {
    // Test with extra spaces and tabs
    Expression tree1 = new ExpressionTree("  1   2   +  ");
    assertEquals(3.0, tree1.evaluate(), DELTA);

    Expression tree2 = new ExpressionTree("1\t2\t+");
    assertEquals(3.0, tree2.evaluate(), DELTA);
  }

  @Test
  public void testComplexTextTree() {
    Expression tree = new ExpressionTree("1 4 6 - 5 + /");
    String expected = "/\n"
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
    assertEquals(expected, tree.textTree());
  }

  // Number formatting tests (covers Math.floor condition)

  @Test
  public void testIntegerFormatting() {
    // Test that integers get .0 appended
    Expression tree = new ExpressionTree("5");
    assertEquals("5.0", tree.infix());
    assertEquals("5.0", tree.schemeExpression());
    assertEquals("5.0", tree.textTree());

    // Test negative integer
    Expression negTree = new ExpressionTree("-10");
    assertEquals("-10.0", negTree.infix());
  }

  @Test
  public void testFloatFormatting() {
    // Test that floats keep exact representation
    Expression tree = new ExpressionTree("5.5");
    assertEquals("5.5", tree.infix());
    assertEquals("5.5", tree.schemeExpression());
    assertEquals("5.5", tree.textTree());

    // Test that it's not formatted as integer
    Expression tree2 = new ExpressionTree("3.00000000001");
    assertEquals("3.00000000001", tree2.infix());
  }

  @Test
  public void testNegativeAndZero() {
    Expression negTree = new ExpressionTree("3 -2 +");
    assertEquals(1.0, negTree.evaluate(), DELTA);
    assertEquals("( 3.0 + -2.0 )", negTree.infix());

    Expression zeroTree = new ExpressionTree("0 5 +");
    assertEquals(5.0, zeroTree.evaluate(), DELTA);
  }

  @Test
  public void testScientificNotation() {
    Expression tree = new ExpressionTree("1e2 2e1 +");
    assertEquals(120.0, tree.evaluate(), DELTA);
  }

  // Error cases

  @Test(expected = ArithmeticException.class)
  public void testDivisionByZero() {
    Expression tree = new ExpressionTree("5 0 /");
    tree.evaluate();
  }

  @Test(expected = ArithmeticException.class)
  public void testDivisionByZeroComplex() {
    Expression tree = new ExpressionTree("10 5 5 - /");
    tree.evaluate(); // 10 / (5 - 5) = 10 / 0
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullExpression() {
    new ExpressionTree(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyExpression() {
    new ExpressionTree("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceOnlyExpression() {
    new ExpressionTree("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidToken() {
    new ExpressionTree("1 a +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidOperator() {
    new ExpressionTree("1 2 %");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooFewOperandsForOperator() {
    new ExpressionTree("1 +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooFewOperandsComplex() {
    new ExpressionTree("1 2 + +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooManyOperands() {
    new ExpressionTree("1 2 3 +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOnlyOperator() {
    new ExpressionTree("+");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMultipleOperatorsNoOperands() {
    new ExpressionTree("+ - *");
  }

  // Stack validation edge cases

  @Test
  public void testStackSizeValidation() {
    // Test exact error messages for insufficient operands
    try {
      new ExpressionTree("+ +");
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("insufficient operands"));
    }

    // Test too many operands message
    try {
      new ExpressionTree("1 2 3 4 +");
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("too many operands"));
    }
  }
}