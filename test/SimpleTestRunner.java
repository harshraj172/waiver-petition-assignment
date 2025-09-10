import expression.Expression;
import expression.ExpressionTree;
import intervals.Interval;
import intervals.IntervalTree;
import intervals.Intervals;

/**
 * Test runner without JUnit.
 */
public class SimpleTestRunner {

  private static int testsRun = 0;
  private static int testsPassed = 0;

  /**
   * Main method to run tests.
   *
   * @param args command line arguments (unused)
   */
  public static void main(String[] args) {
    System.out.println("Running tests...\n");
    System.out.println("==================================================");

    // run expression tests
    testExpressionTree();

    // run interval tests
    testIntervalTree();

    // print results
    System.out.println("\n==================================================");
    System.out.println("Results:");
    System.out.println("Total: " + testsRun);
    System.out.println("Passed: " + testsPassed);
    System.out.println("Failed: " + (testsRun - testsPassed));

    double percent = (100.0 * testsPassed / testsRun);
    System.out.println("Score: " + String.format("%.1f", percent) + "%");

    if (testsPassed == testsRun) {
      System.out.println("\nAll tests passed!");
    } else {
      System.out.println("\nSome tests failed - check output");
    }
  }

  private static void testExpressionTree() {
    System.out.println("\nExpression Tree Tests:");
    System.out.println("----------------------------------------");

    // basic ops
    test("add test", () -> {
      Expression e = new ExpressionTree("1 2 +");
      boolean evalOk = Math.abs(e.evaluate() - 3.0) < 0.001;
      boolean infixOk = e.infix().equals("( 1.0 + 2.0 )");
      boolean schemeOk = e.schemeExpression().equals("( + 1.0 2.0 )");
      return evalOk && infixOk && schemeOk;
    });

    test("subtract test", () -> {
      Expression e = new ExpressionTree("5 3 -");
      return Math.abs(e.evaluate() - 2.0) < 0.001
          && e.infix().equals("( 5.0 - 3.0 )")
          && e.schemeExpression().equals("( - 5.0 3.0 )");
    });

    test("multiply test", () -> {
      Expression expr = new ExpressionTree("4 5 *");
      double result = expr.evaluate();
      return Math.abs(result - 20.0) < 0.001
          && expr.infix().equals("( 4.0 * 5.0 )")
          && expr.schemeExpression().equals("( * 4.0 5.0 )");
    });

    test("divide test", () -> {
      Expression tree = new ExpressionTree("10 2 /");
      return Math.abs(tree.evaluate() - 5.0) < 0.001
          && tree.infix().equals("( 10.0 / 2.0 )")
          && tree.schemeExpression().equals("( / 10.0 2.0 )");
    });

    // complex stuff
    test("complex 1", () -> {
      Expression tree = new ExpressionTree("1.2 5.4 + -4.5 * -");
      double v1 = 6.6;  // 1.2 + 5.4
      double v2 = -29.7; // 6.6 * -4.5
      double expected = 36.3; // 6.6 - (-29.7)
      return Math.abs(tree.evaluate() - expected) < 0.001;
    });

    test("complex 2", () -> {
      Expression tree = new ExpressionTree("1 4 6 - 5 + /");
      // this is 1 / ((4-6) + 5) = 1/3
      double expected = 1.0 / 3.0;
      return Math.abs(tree.evaluate() - expected) < 0.001;
    });

    test("single number", () -> {
      Expression tree = new ExpressionTree("42");
      boolean ok = Math.abs(tree.evaluate() - 42.0) < 0.001;
      ok = ok && tree.infix().equals("42.0");
      ok = ok && tree.schemeExpression().equals("42.0");
      return ok;
    });

    test("negative nums", () -> {
      Expression tree = new ExpressionTree("3 -2 +");
      return Math.abs(tree.evaluate() - 1.0) < 0.001;
    });

    test("tree format", () -> {
      Expression tree = new ExpressionTree("1 2 +");
      String exp = "+\n|\n|\n|___1.0\n|\n|___2.0";
      return tree.textTree().equals(exp);
    });

    // error cases
    test("div by zero", () -> {
      try {
        Expression tree = new ExpressionTree("5 0 /");
        tree.evaluate();
        return false;
      } catch (ArithmeticException e) {
        return true;  // expected
      }
    });

    test("not enough operands", () -> {
      try {
        new ExpressionTree("1 +");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("too many operands", () -> {
      try {
        new ExpressionTree("1 2 3 +");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("bad token", () -> {
      try {
        new ExpressionTree("1 a +");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("null input", () -> {
      try {
        new ExpressionTree(null);
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("empty string", () -> {
      try {
        new ExpressionTree("");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });
  }

  private static void testIntervalTree() {
    System.out.println("\n\nInterval Tree Tests:");
    System.out.println("----------------------------------------");

    test("union", () -> {
      Intervals t = new IntervalTree("1,4 2,5 U");
      Interval res = t.evaluate();
      return res.equals(new Interval(1, 5));
    });

    test("intersection", () -> {
      Intervals tree = new IntervalTree("1,4 2,5 I");
      return tree.evaluate().equals(new Interval(2, 4));
    });

    test("no overlap", () -> {
      Intervals tree = new IntervalTree("1,3 5,7 I");
      Interval result = tree.evaluate();
      // should be MIN_VALUE when no intersection
      return result.equals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE));
    });

    // assignment examples
    test("example 1", () -> {
      Intervals tree = new IntervalTree("-4,4 2,5 U -1,4 I");
      return tree.evaluate().equals(new Interval(-1, 4));
    });

    test("example 2", () -> {
      Intervals tree = new IntervalTree("3,7 2,6 4,10 I U");
      Interval r = tree.evaluate();
      return r.equals(new Interval(3, 7));
    });

    test("example 3", () -> {
      Intervals tree = new IntervalTree("3,10 5,12 U 4,4 I");
      return tree.evaluate().equals(new Interval(4, 4));
    });

    test("single interval", () -> {
      Intervals t = new IntervalTree("5,10");
      return t.evaluate().equals(new Interval(5, 10));
    });

    test("negative intervals", () -> {
      Intervals tree = new IntervalTree("-5,-2 -3,1 U");
      return tree.evaluate().equals(new Interval(-5, 1));
    });

    test("tree output", () -> {
      Intervals tree = new IntervalTree("1,2 3,4 U");
      String exp = "U\n|\n|\n|___1,2\n|\n|___3,4";
      return tree.textTree().equals(exp);
    });

    // errors
    test("bad format 1", () -> {
      try {
        new IntervalTree("1-2 3,4 U");  // dash not comma
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("bad format 2", () -> {
      try {
        new IntervalTree("a,b 1,2 U");  // not numbers
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("missing operand", () -> {
      try {
        new IntervalTree("1,2 U");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("extra operand", () -> {
      try {
        new IntervalTree("1,2 3,4 5,6 U");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("wrong operator", () -> {
      try {
        new IntervalTree("1,2 3,4 +");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("null", () -> {
      try {
        new IntervalTree(null);
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("empty", () -> {
      try {
        new IntervalTree("");
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });

    test("backwards interval", () -> {
      try {
        new IntervalTree("5,2 3,4 U");  // 5 > 2
        return false;
      } catch (IllegalArgumentException e) {
        return true;
      }
    });
  }

  private static void test(String name, TestCase tc) {
    testsRun++;
    try {
      if (tc.run()) {
        testsPassed++;
        System.out.println("[PASS] " + name);
      } else {
        System.out.println("[FAIL] " + name);
      }
    } catch (Exception e) {
      System.out.println("[ERROR] " + name + ": " + e.getMessage());
    }
  }

  // helper to make line separators
  private static String repeat(String s, int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  @FunctionalInterface
  private interface TestCase {
    boolean run() throws Exception;
  }
}