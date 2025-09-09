import static org.junit.Assert.assertEquals;

import intervals.Interval;
import intervals.IntervalTree;
import intervals.Intervals;
import org.junit.Test;

/**
 * Optimized JUnit tests for IntervalTree with minimal redundancy.
 */
public class IntervalTreeTest {

  @Test
  public void testSingleInterval() {
    Intervals tree = new IntervalTree("5,10");
    Interval result = tree.evaluate();
    assertEquals(new Interval(5, 10), result);
    assertEquals("5,10", tree.textTree());
  }

  @Test
  public void testSimpleUnion() {
    Intervals tree = new IntervalTree("1,4 2,5 U");
    Interval result = tree.evaluate();
    assertEquals(new Interval(1, 5), result);
    String expected = "U\n|\n|\n|___1,4\n|\n|___2,5";
    assertEquals(expected, tree.textTree());
  }

  @Test
  public void testSimpleIntersection() {
    Intervals tree = new IntervalTree("1,4 2,5 I");
    Interval result = tree.evaluate();
    assertEquals(new Interval(2, 4), result);
    String expected = "I\n|\n|\n|___1,4\n|\n|___2,5";
    assertEquals(expected, tree.textTree());
  }

  @Test
  public void testNoIntersection() {
    Intervals tree = new IntervalTree("1,3 5,7 I");
    Interval result = tree.evaluate();
    assertEquals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE), result);
  }

  @Test
  public void testTouchingIntervals() {
    // Intervals that touch at a point
    Intervals tree1 = new IntervalTree("1,4 4,7 U");
    assertEquals(new Interval(1, 7), tree1.evaluate());

    Intervals tree2 = new IntervalTree("1,4 4,7 I");
    assertEquals(new Interval(4, 4), tree2.evaluate());
  }

  @Test
  public void testNegativeIntervals() {
    Intervals tree1 = new IntervalTree("-5,-2 -3,1 U");
    assertEquals(new Interval(-5, 1), tree1.evaluate());

    Intervals tree2 = new IntervalTree("-5,-2 -3,1 I");
    assertEquals(new Interval(-3, -2), tree2.evaluate());
  }

  @Test
  public void testZeroLengthInterval() {
    Intervals tree1 = new IntervalTree("5,5");
    assertEquals(new Interval(5, 5), tree1.evaluate());

    Intervals tree2 = new IntervalTree("5,5 5,5 U");
    assertEquals(new Interval(5, 5), tree2.evaluate());
  }

  @Test
  public void testComplexExpression() {
    // Test "-4,4 2,5 U -1,4 I"
    Intervals tree = new IntervalTree("-4,4 2,5 U -1,4 I");
    // First: (-4,4) U (2,5) = (-4,5)
    // Then: (-4,5) I (-1,4) = (-1,4)
    assertEquals(new Interval(-1, 4), tree.evaluate());
  }

  @Test
  public void testChainedOperations() {
    // Test nested operations
    Intervals tree = new IntervalTree("1,5 2,6 I 3,7 4,8 I U");
    assertEquals(new Interval(2, 7), tree.evaluate());
  }

  @Test
  public void testWhitespaceHandling() {
    Intervals tree1 = new IntervalTree("  1,4   2,5   U  ");
    assertEquals(new Interval(1, 5), tree1.evaluate());

    Intervals tree2 = new IntervalTree("1,4\t2,5\tU");
    assertEquals(new Interval(1, 5), tree2.evaluate());
  }

  @Test
  public void testComplexTextTree() {
    Intervals tree = new IntervalTree("1,2 3,4 5,6 U I");
    String expected = "I\n"
        + "|\n"
        + "|\n"
        + "|___1,2\n"
        + "|\n"
        + "|___U\n"
        + "    |\n"
        + "    |\n"
        + "    |___3,4\n"
        + "    |\n"
        + "    |___5,6";
    assertEquals(expected, tree.textTree());
  }

  @Test
  public void testMathMinMaxMutations() {
    // Ensure Math.min/max aren't swapped
    Interval i1 = new Interval(1, 5);
    Interval i2 = new Interval(3, 7);

    Interval intersection = i1.intersect(i2);
    assertEquals("3,5", intersection.toString());

    Interval union = i1.union(i2);
    assertEquals("1,7", union.toString());
  }

  @Test
  public void testExactComparisonOperators() {
    boolean caught = false;
    try {
      new Interval(5, 4);
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals("Invalid interval", e.getMessage());
    }
    assertEquals(true, caught);

    Interval valid = new Interval(5, 5);
    assertEquals("5,5", valid.toString());
  }

  @Test
  public void testIntervalOperatorExactBehavior() {
    // Test U operator specifically
    Intervals unionTree = new IntervalTree("1,3 2,4 U");
    assertEquals("1,4", unionTree.evaluate().toString());

    // Test I operator specifically
    Intervals intersectTree = new IntervalTree("1,3 2,4 I");
    assertEquals("2,3", intersectTree.evaluate().toString());

    Intervals complexTree = new IntervalTree("1,5 3,7 I 2,6 U");
    assertEquals("2,6", complexTree.evaluate().toString());
  }

  @Test
  public void testMinValueHandling() {
    Intervals tree = new IntervalTree("1,2 5,6 I");
    Interval result = tree.evaluate();

    String[] parts = result.toString().split(",");
    assertEquals(String.valueOf(Integer.MIN_VALUE), parts[0]);
    assertEquals(String.valueOf(Integer.MIN_VALUE), parts[1]);
  }

  @Test
  public void testIntervalIntersectionCondition() {
    Interval i1 = new Interval(1, 2);
    Interval i2 = new Interval(3, 4);

    Interval result = i1.intersect(i2);
    // min = max(1,3) = 3, max = min(2,4) = 2
    // Since 3 > 2, should set to MIN_VALUE
    String str = result.toString();
    assertEquals("-2147483648,-2147483648", str);
  }

  @Test
  public void testIntervalUnionMathOperations() {
    Interval i1 = new Interval(5, 10);
    Interval i2 = new Interval(1, 7);

    Interval union = i1.union(i2);
    assertEquals("1,10", union.toString());

    Interval i3 = new Interval(1, 3);
    Interval i4 = new Interval(8, 10);
    Interval union2 = i3.union(i4);
    assertEquals("1,10", union2.toString());
  }

  @Test
  public void testEqualsExactBehavior() {
    Interval i1 = new Interval(1, 5);
    Interval i2 = new Interval(1, 5);

    assertEquals(true, i1.equals(i1));

    assertEquals(true, i1.equals(i2));
    assertEquals(true, i2.equals(i1));

    Interval i3 = new Interval(1, 6);
    assertEquals(false, i1.equals(i3));

    Interval i4 = new Interval(2, 5);
    assertEquals(false, i1.equals(i4));

    assertEquals(false, i1.equals(null));
    assertEquals(false, i1.equals("1,5"));
  }

  @Test
  public void testHashCodeConsistency() {
    Interval i1 = new Interval(5, 10);
    Interval i2 = new Interval(5, 10);

    assertEquals(i1.hashCode(), i2.hashCode());

    int hash1 = i1.hashCode();
    int hash2 = i1.hashCode();
    assertEquals(hash1, hash2);
  }

  @Test
  public void testParseIntervalCommaChecks() {
    try {
      new IntervalTree("12 3,4 U");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }

    try {
      new IntervalTree("1,2,3 4,5 U");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullExpression() {
    new IntervalTree(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyExpression() {
    new IntervalTree("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhitespaceOnlyExpression() {
    new IntervalTree("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalFormat1() {
    new IntervalTree("1-4 2,5 U"); // Using dash instead of comma
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalFormat2() {
    new IntervalTree("1 2,3 U"); // Missing comma
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalFormat3() {
    new IntervalTree(",2 3,4 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalValues1() {
    new IntervalTree("a,b 1,2 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalValues2() {
    new IntervalTree("1.5,2.5 3,4 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidOperator() {
    new IntervalTree("1,2 3,4 +");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooFewOperandsForOperator() {
    new IntervalTree("1,2 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooFewOperandsComplex() {
    new IntervalTree("1,2 3,4 U I");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooManyOperands() {
    new IntervalTree("1,2 3,4 5,6 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOnlyOperator() {
    new IntervalTree("U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalOrder() {
    new IntervalTree("5,2 3,4 U");
  }

  // Stack validation tests

  @Test
  public void testIntervalTreeEmptyChecks() {
    // Test trim().isEmpty() branches
    try {
      new IntervalTree(" ");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }

    try {
      new IntervalTree("\t\n");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }
  }
}