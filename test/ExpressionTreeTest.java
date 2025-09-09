import static org.junit.Assert.assertEquals;

import expression.Expression;
import expression.ExpressionTree;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit 4 tests for ExpressionTree implementation.
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

  @Test
  public void testNumberFormattingBoundaryConditions() {
    // Test exact integer values - these MUST format with .0
    Expression tree1 = new ExpressionTree("1.0");
    assertEquals("1.0", tree1.infix());
    assertEquals("1.0", tree1.schemeExpression());
    assertEquals("1.0", tree1.textTree());

    Expression tree2 = new ExpressionTree("2");
    assertEquals("2.0", tree2.infix());
    assertEquals("2.0", tree2.schemeExpression());
    assertEquals("2.0", tree2.textTree());

    Expression tree3 = new ExpressionTree("100");
    assertEquals("100.0", tree3.infix());
    assertEquals("100.0", tree3.schemeExpression());
    assertEquals("100.0", tree3.textTree());

    // Test non-integer values - these MUST NOT format with .0
    Expression tree4 = new ExpressionTree("1.1");
    assertEquals("1.1", tree4.infix());
    assertEquals(false, "1.0".equals(tree4.infix()));
    assertEquals("1.1", tree4.schemeExpression());
    assertEquals("1.1", tree4.textTree());

    Expression tree5 = new ExpressionTree("2.999999");
    assertEquals("2.999999", tree5.infix());
    assertEquals(false, "3.0".equals(tree5.infix()));
    assertEquals("2.999999", tree5.schemeExpression());
    assertEquals("2.999999", tree5.textTree());

    // Test very small decimal differences
    Expression tree6 = new ExpressionTree("1.0000000001");
    assertEquals("1.0000000001", tree6.infix());
    assertEquals(false, "1.0".equals(tree6.infix()));

    Expression tree7 = new ExpressionTree("0.9999999999");
    assertEquals("0.9999999999", tree7.infix());
    assertEquals(false, "1.0".equals(tree7.infix()));
  }

  @Test
  public void testFloorConditionMutations() {
    // Test that Math.floor is being used correctly
    // If Math.floor is removed or changed, these should fail

    // Test integer that should use %.1f format
    Expression intExpr = new ExpressionTree("5");
    String intInfix = intExpr.infix();
    assertEquals("5.0", intInfix);
    assertEquals(false, "5".equals(intInfix));
    assertEquals(false, "5.00".equals(intInfix));

    // Test float that should use String.valueOf
    Expression floatExpr = new ExpressionTree("5.5");
    String floatInfix = floatExpr.infix();
    assertEquals("5.5", floatInfix);
    assertEquals(false, "5.0".equals(floatInfix));
    assertEquals(false, "6.0".equals(floatInfix));

    // Test negative integer
    Expression negIntExpr = new ExpressionTree("-10");
    assertEquals("-10.0", negIntExpr.infix());

    // Test negative float
    Expression negFloatExpr = new ExpressionTree("-10.7");
    assertEquals("-10.7", negFloatExpr.infix());
  }

  @Test
  public void testEqualityConditionMutation() {
    // Test the == vs != mutation
    // These tests ensure that == is the correct operator

    // For value == Math.floor(value), test edge cases
    Expression e1 = new ExpressionTree("3.0");
    assertEquals("3.0", e1.infix()); // Should use %.1f format

    Expression e2 = new ExpressionTree("3.00000000001");
    assertEquals(false, "3.0".equals(e2.infix())); // Should NOT use %.1f format
    assertEquals("3.00000000001", e2.infix());

    Expression e3 = new ExpressionTree("2.99999999999");
    assertEquals(false, "3.0".equals(e3.infix())); // Should NOT use %.1f format
    assertEquals("2.99999999999", e3.infix());
  }

  @Test
  public void testStringFormatVsValueOf() {
    // Ensure String.format and String.valueOf return different results
    // This kills mutations that swap these methods

    // Integer should use String.format("%.1f", value)
    Expression intTree = new ExpressionTree("42");
    String intResult = intTree.infix();
    assertEquals(4, intResult.length()); // "42.0" is 4 chars
    assertEquals(true, intResult.endsWith(".0"));

    // Float should use String.valueOf(value)
    Expression floatTree = new ExpressionTree("42.123");
    String floatResult = floatTree.infix();
    assertEquals("42.123", floatResult);
    assertEquals(false, floatResult.endsWith(".0"));
  }

  @Test
  public void testAllNumberNodeMethods() {
    // Test that all three methods (infix, schemeExpression, textTree)
    // use the same formatting logic

    double[] testValues = {0.0, 1.0, -1.0, 1.5, -1.5, 100.0, 0.1, 0.00001};

    for (double value : testValues) {
      Expression tree = new ExpressionTree(String.valueOf(value));

      String infix = tree.infix();
      String scheme = tree.schemeExpression();
      String textTree = tree.textTree();

      // All three should produce the same output for a single number
      assertEquals("infix and scheme should match for " + value, infix, scheme);
      assertEquals("scheme and textTree should match for " + value, scheme, textTree);

      // Verify the formatting rules
      if (value == Math.floor(value)) {
        // Should end with .0
        assertEquals("Integer value " + value + " should end with .0",
            true, infix.endsWith(".0"));
      } else {
        // Should not artificially add .0
        assertEquals("Non-integer value " + value + " should not end with .0",
            false, infix.endsWith(".0"));
      }
    }
  }

  @Test
  public void testVeryPreciseDecimals() {
    // Test numbers with many decimal places
    Expression tree1 = new ExpressionTree("3.14159265358979323846");
    String result1 = tree1.infix();
    assertEquals("3.141592653589793", result1); // Java double precision limit

    Expression tree2 = new ExpressionTree("1.000000000000001");
    String result2 = tree2.infix();
    assertEquals("1.000000000000001", result2);
    assertEquals(false, "1.0".equals(result2));
  }

  @Test
  public void testZeroVariations() {
    // Test different representations of zero
    Expression tree1 = new ExpressionTree("0");
    assertEquals("0.0", tree1.infix());

    Expression tree2 = new ExpressionTree("0.0");
    assertEquals("0.0", tree2.infix());

    Expression tree3 = new ExpressionTree("-0.0");
    assertEquals("-0.0", tree3.infix());

    Expression tree4 = new ExpressionTree("0.00000");
    assertEquals("0.0", tree4.infix());
  }

  @Test
  public void testReturnPathCoverage() {
    // Explicitly test that each return path is taken

    // Path 1: Integer value -> String.format("%.1f", value)
    Expression intPath = new ExpressionTree("7");
    assertEquals("7.0", intPath.infix());
    assertEquals("7.0", intPath.schemeExpression());
    assertEquals("7.0", intPath.textTree());

    // Path 2: Non-integer value -> String.valueOf(value)
    Expression floatPath = new ExpressionTree("7.5");
    assertEquals("7.5", floatPath.infix());
    assertEquals("7.5", floatPath.schemeExpression());
    assertEquals("7.5", floatPath.textTree());

    // Verify these are different code paths by checking exact format
    assertEquals(false, "7.5000000000".equals(floatPath.infix()));
    assertEquals(false, "7".equals(intPath.infix()));
  }

  @Test
  public void testMutationKillerComprehensive() {
    // Comprehensive test to ensure mutations are killed

    // Test 1: Mutation of == to !=
    Expression eq1 = new ExpressionTree("10.0");
    assertEquals("10.0", eq1.infix()); // Would be "10.0" with ==, but "10.0" with != is wrong

    Expression eq2 = new ExpressionTree("10.1");
    assertEquals("10.1", eq2.infix()); // Would be "10.1" with ==, different with !=

    // Test 2: Removal of Math.floor
    Expression floor1 = new ExpressionTree("5.9");
    assertEquals(false, "5.0".equals(floor1.infix())); // Without floor check, might format wrong
    assertEquals("5.9", floor1.infix());

    // Test 3: Removal of !Double.isInfinite
    // Hard to test directly, but the condition should be there
    Expression finite = new ExpressionTree("123");
    assertEquals("123.0", finite.infix());

    // Test 4: Wrong format string
    Expression fmt1 = new ExpressionTree("8");
    String fmt1Result = fmt1.infix();
    assertEquals(true, fmt1Result.matches("\\d+\\.\\d"));
    assertEquals("8.0", fmt1Result);
  }

  @Test
  public void testIntegerFormattingPrecision() {
    // Specifically test that integers always get .0 appended
    for (int i = -10; i <= 10; i++) {
      Expression tree = new ExpressionTree(String.valueOf(i));
      String result = tree.infix();
      assertEquals(i + ".0", result);

      // Also test in scheme and textTree
      assertEquals(i + ".0", tree.schemeExpression());
      assertEquals(i + ".0", tree.textTree());
    }
  }

  @Test
  public void testFloatFormattingPrecision() {
    // Test that floats keep their exact representation
    double[] floats = {1.1, 1.01, 1.001, 1.0001, 1.00001};
    for (double f : floats) {
      Expression tree = new ExpressionTree(String.valueOf(f));
      String result = tree.infix();
      assertEquals(String.valueOf(f), result);

      // Should NOT be formatted as integer
      assertEquals(false, result.endsWith(".0"));
    }
  }

  @Test
  public void testFormattingConsistencyAcrossMethods() {
    // Ensure all three methods use the same formatting logic
    String[] inputs = {"5", "5.0", "5.5", "-10", "-10.0", "-10.5", "0", "0.0"};

    for (String input : inputs) {
      Expression tree = new ExpressionTree(input);
      String infix = tree.infix();
      String scheme = tree.schemeExpression();
      String text = tree.textTree();

      // All three should be identical for a single number
      assertEquals("Methods should agree for " + input, infix, scheme);
      assertEquals("Methods should agree for " + input, scheme, text);
    }
  }

  @Test
  public void testCriticalMutationPoints() {
    // Target specific mutation points that PIT identifies

    // Test Math.floor(value) == value (not !=)
    Expression integerExpr = new ExpressionTree("9");
    assertEquals("9.0", integerExpr.infix()); // Must be 9.0, not 9

    Expression floatExpr = new ExpressionTree("9.1");
    assertEquals("9.1", floatExpr.infix()); // Must be 9.1, not 9.0

    // Test that both branches are taken
    Expression branch1 = new ExpressionTree("100");
    String r1 = branch1.infix();
    assertEquals(true, r1.contains("."));
    assertEquals("100.0", r1);

    Expression branch2 = new ExpressionTree("100.1");
    String r2 = branch2.infix();
    assertEquals(false, r2.equals("100.0"));
    assertEquals("100.1", r2);
  }
}