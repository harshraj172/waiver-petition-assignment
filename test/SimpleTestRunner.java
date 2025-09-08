import expression.Expression;
import expression.ExpressionTree;
import intervals.Interval;
import intervals.IntervalTree;
import intervals.Intervals;

/**
 * Simple test runner for the tree implementations without JUnit dependency.
 * This provides basic testing capability when JUnit is not available.
 */
public class SimpleTestRunner {

  private static int testsRun = 0;
  private static int testsPassed = 0;

  /**
   * Main method to run all tests for ExpressionTree and IntervalTree.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    System.out.println("Running tests for ExpressionTree and IntervalTree...\n");
    System.out.println("=" + repeat("=", 49));

    // Test ExpressionTree
    testExpressionTree();

    // Test IntervalTree
    testIntervalTree();

    // Print summary
    System.out.println("\n" + repeat("=", 50));
    System.out.println("FINAL RESULTS:");
    System.out.println(repeat("=", 50));
    System.out.println("Tests completed: " + testsRun);
    System.out.println("Tests passed: " + testsPassed);
    System.out.println("Tests failed: " + (testsRun - testsPassed));
    System.out.println("Success rate: "
        + String.format("%.1f", (100.0 * testsPassed / testsRun)) + "%");

    if (testsPassed == testsRun) {
      System.out.println("\nALL TESTS PASSED! ✓");
    } else {
      System.out.println("\nSOME TESTS FAILED. Please review the output above.");
    }
  }

  private static void testExpressionTree() {
    System.out.println("\nTesting ExpressionTree:");
    System.out.println(repeat("-", 40));

    // Test simple operations
    test("Simple addition (1 2 +)", () -> {
      Expression tree = new ExpressionTree("1 2 +");
      return Math.abs(tree.evaluate() - 3.0) < 0.001
          && tree.infix().equals("( 1.0 + 2.0 )")
          && tree.schemeExpression().equals("( + 1.0 2.0 )");
    });

    test("Simple subtraction (5 3 -)", () -> {
      Expression tree = new ExpressionTree("5 3 -");
      return Math.abs(tree.evaluate() - 2.0) < 0.001
          && tree.infix().equals("( 5.0 - 3.0 )")
          && tree.schemeExpression().equals("( - 5.0 3.0 )");
    });

    test("Simple multiplication (4 5 *)", () -> {
      Expression tree = new ExpressionTree("4 5 *");
      return Math.abs(tree.evaluate() - 20.0) < 0.001
          && tree.infix().equals("( 4.0 * 5.0 )")
          && tree.schemeExpression().equals("( * 4.0 5.0 )");
    });

    test("Simple division (10 2 /)", () -> {
      Expression tree = new ExpressionTree("10 2 /");
      return Math.abs(tree.evaluate() - 5.0) < 0.001
          && tree.infix().equals("( 10.0 / 2.0 )")
          && tree.schemeExpression().equals("( / 10.0 2.0 )");
    });

    // Test complex expressions
    test("Complex expression (1.2 5.4 + -4.5 * -)", () -> {
      Expression tree = new ExpressionTree("1.2 5.4 + -4.5 * -");
      double val1 = 1.2 + 5.4; // 6.6
      double val2 = val1 * (-4.5); // -29.7
      double expected = val1 - val2; // 36.3
      return Math.abs(tree.evaluate() - expected) < 0.001;
    });

    test("Complex expression (1 4 6 - 5 + /)", () -> {
      Expression tree = new ExpressionTree("1 4 6 - 5 + /");
      double expected = 1.0 / 3.0;
      return Math.abs(tree.evaluate() - expected) < 0.001;
    });

    // Test single number
    test("Single number (42)", () -> {
      Expression tree = new ExpressionTree("42");
      return Math.abs(tree.evaluate() - 42.0) < 0.001
          && tree.infix().equals("42.0")
          && tree.schemeExpression().equals("42.0");
    });

    // Test negative numbers
    test("Negative numbers (3 -2 +)", () -> {
      Expression tree = new ExpressionTree("3 -2 +");
      return Math.abs(tree.evaluate() - 1.0) < 0.001;
    });

    // Test text tree formatting
    test("Text tree formatting", () -> {
      Expression tree = new ExpressionTree("1 2 +");
      String expected = "+\n|\n|\n|___1.0\n|\n|___2.0";
      return tree.textTree().equals(expected);
    });

    // Test error handling
    test("Division by zero handling", () -> {
      try {
        Expression tree = new ExpressionTree("5 0 /");
        tree.evaluate();
        return false; // Should have thrown exception
      } catch (ArithmeticException e) {
        return true;
      }
    });

    test("Invalid expression handling (too few operands)", () -> {
      try {
        new ExpressionTree("1 +");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Invalid expression handling (too many operands)", () -> {
      try {
        new ExpressionTree("1 2 3 +");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Invalid token handling", () -> {
      try {
        new ExpressionTree("1 a +");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Null expression handling", () -> {
      try {
        new ExpressionTree(null);
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Empty expression handling", () -> {
      try {
        new ExpressionTree("");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });
  }

  private static void testIntervalTree() {
    System.out.println("\n\nTesting IntervalTree:");
    System.out.println(repeat("-", 40));

    // Test simple operations
    test("Simple union (1,4 2,5 U)", () -> {
      Intervals tree = new IntervalTree("1,4 2,5 U");
      Interval result = tree.evaluate();
      return result.equals(new Interval(1, 5));
    });

    test("Simple intersection (1,4 2,5 I)", () -> {
      Intervals tree = new IntervalTree("1,4 2,5 I");
      Interval result = tree.evaluate();
      return result.equals(new Interval(2, 4));
    });

    test("No intersection (1,3 5,7 I)", () -> {
      Intervals tree = new IntervalTree("1,3 5,7 I");
      Interval result = tree.evaluate();
      return result.equals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE));
    });

    // Test complex expressions from assignment
    test("Complex: -4,4 2,5 U -1,4 I", () -> {
      Intervals tree = new IntervalTree("-4,4 2,5 U -1,4 I");
      Interval result = tree.evaluate();
      return result.equals(new Interval(-1, 4));
    });

    test("Complex: 3,7 2,6 4,10 I U", () -> {
      Intervals tree = new IntervalTree("3,7 2,6 4,10 I U");
      Interval result = tree.evaluate();
      return result.equals(new Interval(3, 7));
    });

    test("Complex: 3,10 5,12 U 4,4 I", () -> {
      Intervals tree = new IntervalTree("3,10 5,12 U 4,4 I");
      Interval result = tree.evaluate();
      return result.equals(new Interval(4, 4));
    });

    // Test single interval
    test("Single interval (5,10)", () -> {
      Intervals tree = new IntervalTree("5,10");
      return tree.evaluate().equals(new Interval(5, 10));
    });

    // Test negative intervals
    test("Negative intervals (-5,-2 -3,1 U)", () -> {
      Intervals tree = new IntervalTree("-5,-2 -3,1 U");
      return tree.evaluate().equals(new Interval(-5, 1));
    });

    // Test text tree formatting
    test("Text tree formatting", () -> {
      Intervals tree = new IntervalTree("1,2 3,4 U");
      String expected = "U\n|\n|\n|___1,2\n|\n|___3,4";
      return tree.textTree().equals(expected);
    });

    // Test error handling
    test("Invalid interval format (1-2 3,4 U)", () -> {
      try {
        new IntervalTree("1-2 3,4 U");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Invalid interval values (a,b 1,2 U)", () -> {
      try {
        new IntervalTree("a,b 1,2 U");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Too few operands (1,2 U)", () -> {
      try {
        new IntervalTree("1,2 U");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Too many operands (1,2 3,4 5,6 U)", () -> {
      try {
        new IntervalTree("1,2 3,4 5,6 U");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Invalid operator (1,2 3,4 +)", () -> {
      try {
        new IntervalTree("1,2 3,4 +");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Null expression handling", () -> {
      try {
        new IntervalTree(null);
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Empty expression handling", () -> {
      try {
        new IntervalTree("");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("Invalid interval order (5,2 3,4 U)", () -> {
      try {
        new IntervalTree("5,2 3,4 U");
        return false; // Should have thrown exception
      } catch (IllegalArgumentException e) {
        return true;
      }
    });
  }

  private static void test(String testName, TestCase testCase) {
    testsRun++;
    try {
      if (testCase.run()) {
        testsPassed++;
        System.out.println("✓ " + testName);
      } else {
        System.out.println("✗ " + testName + " - FAILED");
      }
    } catch (Exception e) {
      System.out.println("✗ " + testName + " - ERROR: " + e.getMessage());
    }
  }

  private static String repeat(String str, int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(str);
    }
    return sb.toString();
  }

  @FunctionalInterface
  private interface TestCase {
    boolean run() throws Exception;
  }
}