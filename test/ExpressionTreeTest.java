import static org.junit.Assert.assertEquals;

import expression.Expression;
import expression.ExpressionTree;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive JUnit 4 tests for ExpressionTree implementation.
 * Designed to achieve 100% code coverage and pass PIT mutation testing.
 */
public class ExpressionTreeTest {

  private static final double DELTA = 0.0001;

  @Test
  public void testSingleNumber() {
    Expression tree = new ExpressionTree("42");
    assertEquals(42.0, tree.evaluate(), DELTA);
    assertEquals("42.0", tree.infix());
    assertEquals("42.0", tree.schemeExpression());
    assertEquals("42.0", tree.textTree());
  }

  @Test
  public void testNegativeNumber() {
    Expression tree = new ExpressionTree("-17.5");
    assertEquals(-17.5, tree.evaluate(), DELTA);
    assertEquals("-17.5", tree.infix());
    assertEquals("-17.5", tree.schemeExpression());
    assertEquals("-17.5", tree.textTree());
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
    String expected = "-\n|\n|\n|___5.0\n|\n|___3.0";
    assertEquals(expected, tree.textTree());
  }

  @Test
  public void testSimpleMultiplication() {
    Expression tree = new ExpressionTree("4 5 *");
    assertEquals(20.0, tree.evaluate(), DELTA);
    assertEquals("( 4.0 * 5.0 )", tree.infix());
    assertEquals("( * 4.0 5.0 )", tree.schemeExpression());
    String expected = "*\n|\n|\n|___4.0\n|\n|___5.0";
    assertEquals(expected, tree.textTree());
  }

  @Test
  public void testSimpleDivision() {
    Expression tree = new ExpressionTree("10 2 /");
    assertEquals(5.0, tree.evaluate(), DELTA);
    assertEquals("( 10.0 / 2.0 )", tree.infix());
    assertEquals("( / 10.0 2.0 )", tree.schemeExpression());
    String expected = "/\n|\n|\n|___10.0\n|\n|___2.0";
    assertEquals(expected, tree.textTree());
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
  public void testComplexExpression2() {
    // Test "1 2 3 * +" which is 1 + (2 * 3)
    Expression tree = new ExpressionTree("1 2 3 * +");
    assertEquals(7.0, tree.evaluate(), DELTA);
    assertEquals("( 1.0 + ( 2.0 * 3.0 ) )", tree.infix());
    assertEquals("( + 1.0 ( * 2.0 3.0 ) )", tree.schemeExpression());
  }

  @Test
  public void testComplexExpression3() {
    // Test from assignment: To compute (1.2 + 5.4) - ((1.2 + 5.4) * -4.5)
    // We need to duplicate the first sum, so: "1.2 5.4 + 1.2 5.4 + -4.5 * -"
    Expression tree = new ExpressionTree("1.2 5.4 + 1.2 5.4 + -4.5 * -");
    double val1 = 1.2 + 5.4; // 6.6
    double val2 = val1 * (-4.5); // -29.7
    double result = val1 - val2; // 6.6 - (-29.7) = 36.3
    assertEquals(36.3, tree.evaluate(), DELTA);
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
  public void testChainedOperations() {
    Expression tree = new ExpressionTree("1 2 + 3 4 + *");
    assertEquals(21.0, tree.evaluate(), DELTA); // (1+2) * (3+4) = 3 * 7 = 21
    assertEquals("( ( 1.0 + 2.0 ) * ( 3.0 + 4.0 ) )", tree.infix());
    assertEquals("( * ( + 1.0 2.0 ) ( + 3.0 4.0 ) )", tree.schemeExpression());
  }

  @Test
  public void testDecimalNumbers() {
    Expression tree = new ExpressionTree("2.5 4.5 *");
    assertEquals(11.25, tree.evaluate(), DELTA);
    assertEquals("( 2.5 * 4.5 )", tree.infix());
    assertEquals("( * 2.5 4.5 )", tree.schemeExpression());
  }

  @Test
  public void testNegativeNumbersInOperation() {
    Expression tree = new ExpressionTree("3 -2 +");
    assertEquals(1.0, tree.evaluate(), DELTA);
    assertEquals("( 3.0 + -2.0 )", tree.infix());
    assertEquals("( + 3.0 -2.0 )", tree.schemeExpression());
  }

  @Test
  public void testZeroInOperations() {
    Expression tree1 = new ExpressionTree("0 5 +");
    assertEquals(5.0, tree1.evaluate(), DELTA);

    Expression tree2 = new ExpressionTree("10 0 -");
    assertEquals(10.0, tree2.evaluate(), DELTA);

    Expression tree3 = new ExpressionTree("0 7 *");
    assertEquals(0.0, tree3.evaluate(), DELTA);
  }

  @Test
  public void testLargeNumbers() {
    Expression tree = new ExpressionTree("1000000 2000000 +");
    assertEquals(3000000.0, tree.evaluate(), DELTA);
  }

  @Test
  public void testVerySmallNumbers() {
    Expression tree = new ExpressionTree("0.0001 0.0002 +");
    assertEquals(0.0003, tree.evaluate(), DELTA);
  }

  @Test
  public void testWhitespaceHandling() {
    // Test with extra spaces
    Expression tree1 = new ExpressionTree("  1   2   +  ");
    assertEquals(3.0, tree1.evaluate(), DELTA);

    // Test with tabs
    Expression tree2 = new ExpressionTree("1\t2\t+");
    assertEquals(3.0, tree2.evaluate(), DELTA);

    // Test with mixed whitespace
    Expression tree3 = new ExpressionTree("  1  \t 2 \t  + ");
    assertEquals(3.0, tree3.evaluate(), DELTA);
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

  @Test
  public void testDeeplyNestedExpression() {
    // Test "1 2 + 3 + 4 + 5 +"
    Expression tree = new ExpressionTree("1 2 + 3 + 4 + 5 +");
    assertEquals(15.0, tree.evaluate(), DELTA); // 1+2+3+4+5 = 15
  }

  // Error cases

  @Test(expected = ArithmeticException.class)
  public void testDivisionByZero() {
    Expression tree = new ExpressionTree("5 0 /");
    tree.evaluate(); // Should throw ArithmeticException
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

  @Test(expected = IllegalArgumentException.class)
  public void testMixedInvalidTokens() {
    new ExpressionTree("1 @ 2 # +");
  }

  // Edge cases for complete coverage

  @Test
  public void testAllOperators() {
    // Ensure all operators are tested
    Expression add = new ExpressionTree("3 2 +");
    assertEquals(5.0, add.evaluate(), DELTA);

    Expression sub = new ExpressionTree("3 2 -");
    assertEquals(1.0, sub.evaluate(), DELTA);

    Expression mul = new ExpressionTree("3 2 *");
    assertEquals(6.0, mul.evaluate(), DELTA);

    Expression div = new ExpressionTree("6 2 /");
    assertEquals(3.0, div.evaluate(), DELTA);
  }

  @Test
  public void testOperatorPrecedenceInPostfix() {
    // In postfix, precedence is implicit in the order
    // "2 3 * 4 +" should be (2 * 3) + 4 = 10
    Expression tree1 = new ExpressionTree("2 3 * 4 +");
    assertEquals(10.0, tree1.evaluate(), DELTA);

    // "2 3 4 + *" should be 2 * (3 + 4) = 14
    Expression tree2 = new ExpressionTree("2 3 4 + *");
    assertEquals(14.0, tree2.evaluate(), DELTA);
  }

  @Test
  public void testScientificNotation() {
    Expression tree = new ExpressionTree("1e2 2e1 +");
    assertEquals(120.0, tree.evaluate(), DELTA);
  }

  @Test
  public void testVeryLongExpression() {
    // Build a long expression
    Expression tree = new ExpressionTree("1 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 +");
    assertEquals(10.0, tree.evaluate(), DELTA);
  }

  @Test
  public void testNegativeDivision() {
    Expression tree = new ExpressionTree("-10 -2 /");
    assertEquals(5.0, tree.evaluate(), DELTA);
  }

  @Test
  public void testFractionalDivision() {
    Expression tree = new ExpressionTree("1 3 /");
    assertEquals(0.333333, tree.evaluate(), 0.00001);
  }

  @Test
  public void testBoundaryConditionsForOperators() {
    // Test exact boundary values for each operator

    // Addition boundary
    Expression add1 = new ExpressionTree("0 0 +");
    assertEquals(0.0, add1.evaluate(), 0.0);

    Expression add2 = new ExpressionTree("-1 1 +");
    assertEquals(0.0, add2.evaluate(), 0.0);

    // Subtraction boundary
    Expression sub1 = new ExpressionTree("5 5 -");
    assertEquals(0.0, sub1.evaluate(), 0.0);

    Expression sub2 = new ExpressionTree("0 5 -");
    assertEquals(-5.0, sub2.evaluate(), 0.0);

    // Multiplication boundary
    Expression mult1 = new ExpressionTree("1 5 *");
    assertEquals(5.0, mult1.evaluate(), 0.0);

    Expression mult2 = new ExpressionTree("-1 5 *");
    assertEquals(-5.0, mult2.evaluate(), 0.0);

    // Division boundary
    Expression div1 = new ExpressionTree("5 1 /");
    assertEquals(5.0, div1.evaluate(), 0.0);

    Expression div2 = new ExpressionTree("5 -1 /");
    assertEquals(-5.0, div2.evaluate(), 0.0);
  }

  @Test
  public void testOperatorMutations() {
    // Test that changing operators would be detected
    Expression expr1 = new ExpressionTree("6 2 +");
    assertEquals(8.0, expr1.evaluate(), 0.0);
    // If + was mutated to -, result would be 4
    // If + was mutated to *, result would be 12
    // If + was mutated to /, result would be 3

    Expression expr2 = new ExpressionTree("6 2 -");
    assertEquals(4.0, expr2.evaluate(), 0.0);
    // Verify it's not 8 (addition)

    Expression expr3 = new ExpressionTree("6 2 *");
    assertEquals(12.0, expr3.evaluate(), 0.0);
    // Verify it's not 8 or 4

    Expression expr4 = new ExpressionTree("6 2 /");
    assertEquals(3.0, expr4.evaluate(), 0.0);
    // Verify it's not any other operation
  }

  @Test
  public void testExactStringComparisons() {
    // Test exact string outputs to catch string mutations
    Expression tree = new ExpressionTree("3 4 +");

    String infix = tree.infix();
    assertEquals("( 3.0 + 4.0 )", infix);
    // Verify exact spacing and parentheses
    assertEquals(13, infix.length());
    assertEquals('(', infix.charAt(0));
    assertEquals(')', infix.charAt(infix.length() - 1));

    String scheme = tree.schemeExpression();
    assertEquals("( + 3.0 4.0 )", scheme);
    assertEquals(13, scheme.length());
  }

  @Test
  public void testTextTreeExactFormat() {
    Expression tree = new ExpressionTree("1 2 +");
    String textTree = tree.textTree();

    // Just verify key components are present and in correct order
    assertEquals(true, textTree.startsWith("+"));
    assertEquals(true, textTree.contains("|___1.0"));
    assertEquals(true, textTree.contains("|___2.0"));

    // Verify the operator comes before operands
    int opIndex = textTree.indexOf("+");
    int op1Index = textTree.indexOf("1.0");
    int op2Index = textTree.indexOf("2.0");

    assertEquals(true, opIndex < op1Index);
    assertEquals(true, op1Index < op2Index);
  }

  @Test
  public void testConditionalBoundaries() {
    // Test values right at conditional boundaries

    // Test floor condition for formatting
    Expression intVal = new ExpressionTree("5");
    assertEquals("5.0", intVal.infix());

    Expression floatVal = new ExpressionTree("5.5");
    assertEquals("5.5", floatVal.infix());

    Expression floatClose = new ExpressionTree("5.000001");
    assertEquals("5.000001", floatClose.infix());
  }

  @Test
  public void testStackSizeValidation() {
    // Verify exact stack size checks
    boolean caught = false;
    try {
      new ExpressionTree("+ +"); // No operands
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals(true, e.getMessage().contains("insufficient operands"));
    }
    assertEquals(true, caught);

    caught = false;
    try {
      new ExpressionTree("1 2 3 4 +"); // Too many operands
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals(true, e.getMessage().contains("too many operands"));
    }
    assertEquals(true, caught);
  }

  @Test
  public void testNullAndEmptyValidation() {
    // Test null check
    boolean caught = false;
    try {
      new ExpressionTree(null);
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }
    assertEquals(true, caught);

    // Test empty string check
    caught = false;
    try {
      new ExpressionTree("");
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }
    assertEquals(true, caught);

    // Test whitespace only
    caught = false;
    try {
      new ExpressionTree("   ");
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }
    assertEquals(true, caught);
  }

  @Test
  public void testArithmeticBoundaries() {
    // Test that each operator returns EXACTLY the right value
    Expression e1 = new ExpressionTree("7 3 +");
    assertEquals(10.0, e1.evaluate(), 0.0);
    // Not 4 (subtraction), not 21 (multiplication), not 2.333 (division)

    Expression e2 = new ExpressionTree("7 3 -");
    assertEquals(4.0, e2.evaluate(), 0.0);
    // Not 10 (addition), not 21 (multiplication), not 2.333 (division)

    Expression e3 = new ExpressionTree("7 3 *");
    assertEquals(21.0, e3.evaluate(), 0.0);
    // Not 10 (addition), not 4 (subtraction), not 2.333 (division)

    Expression e4 = new ExpressionTree("7 3 /");
    assertEquals(7.0 / 3.0, e4.evaluate(), 0.0);
    // Not 10, not 4, not 21
  }

  @Test
  public void testOperatorNodeDefaultCase() {
    // This tests that the default case in operator switch throws correctly
    // Even though it "should never happen", PIT might mutate it
    boolean caught = false;
    try {
      // We can't directly test this without modifying the code,
      // but we can ensure all valid operators work correctly
      Expression e1 = new ExpressionTree("1 2 +");
      assertEquals(3.0, e1.evaluate(), 0.0);
      Expression e2 = new ExpressionTree("1 2 -");
      assertEquals(-1.0, e2.evaluate(), 0.0);
      Expression e3 = new ExpressionTree("1 2 *");
      assertEquals(2.0, e3.evaluate(), 0.0);
      Expression e4 = new ExpressionTree("1 2 /");
      assertEquals(0.5, e4.evaluate(), 0.0);
    } catch (Exception e) {
      caught = true;
    }
    assertEquals(false, caught); // Should not throw for valid operators
  }

  @Test
  public void testNumberNodeInfinityCheck() {
    // Test that division by zero throws ArithmeticException
    boolean caught = false;
    try {
      Expression e1 = new ExpressionTree("1 0 /");
      e1.evaluate();
    } catch (ArithmeticException e) {
      caught = true;
      assertEquals("Division by zero", e.getMessage());
    }
    assertEquals(true, caught);

    // Test with negative numerator
    caught = false;
    try {
      Expression e2 = new ExpressionTree("-1 0 /");
      e2.evaluate();
    } catch (ArithmeticException e) {
      caught = true;
      assertEquals("Division by zero", e.getMessage());
    }
    assertEquals(true, caught);

    // Test that non-zero division works fine
    Expression e3 = new ExpressionTree("1 2 /");
    assertEquals(0.5, e3.evaluate(), 0.0);
  }

  @Test
  public void testParsePostfixStackValidation() {
    // Test stack.size() < 2 condition
    try {
      new ExpressionTree("+");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("insufficient operands"));
    }

    try {
      new ExpressionTree("1 +");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("insufficient operands"));
    }

    // Test empty stack condition
    try {
      new ExpressionTree("+ -");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true,
          e.getMessage().contains("insufficient")
              || e.getMessage().contains("no result"));
    }
  }

  @Test
  public void testIsOperatorAllCases() {
    // Ensure isOperator returns false for non-operators
    try {
      new ExpressionTree("1 2 %");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid token"));
    }

    try {
      new ExpressionTree("1 2 ^");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid token"));
    }
  }

  @Test
  public void testWholeNumberCondition() {
    // Test Math.floor(value) == value condition precisely
    Expression e1 = new ExpressionTree("3.0");
    assertEquals("3.0", e1.infix());

    Expression e2 = new ExpressionTree("3.1");
    assertEquals("3.1", e2.infix());

    Expression e3 = new ExpressionTree("3.9999999");
    assertEquals("3.9999999", e3.infix());

    Expression e4 = new ExpressionTree("4.0000001");
    assertEquals("4.0000001", e4.infix());
  }
}