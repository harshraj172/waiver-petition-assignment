import static org.junit.Assert.assertEquals;

import intervals.Interval;
import intervals.IntervalTree;
import intervals.Intervals;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for IntervalTree implementation.
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
  public void testIdenticalIntervals() {
    Intervals tree1 = new IntervalTree("3,7 3,7 U");
    assertEquals(new Interval(3, 7), tree1.evaluate());

    Intervals tree2 = new IntervalTree("3,7 3,7 I");
    assertEquals(new Interval(3, 7), tree2.evaluate());
  }

  @Test
  public void testAdjacentIntervals() {
    // Adjacent but not overlapping
    Intervals tree1 = new IntervalTree("1,3 4,6 U");
    assertEquals(new Interval(1, 6), tree1.evaluate());

    Intervals tree2 = new IntervalTree("1,3 4,6 I");
    assertEquals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE), tree2.evaluate());
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
  public void testCompletelyContainedIntervals() {
    // One interval completely contains the other
    Intervals tree1 = new IntervalTree("1,10 3,7 U");
    assertEquals(new Interval(1, 10), tree1.evaluate());

    Intervals tree2 = new IntervalTree("1,10 3,7 I");
    assertEquals(new Interval(3, 7), tree2.evaluate());
  }

  @Test
  public void testNegativeIntervals() {
    Intervals tree1 = new IntervalTree("-5,-2 -3,1 U");
    assertEquals(new Interval(-5, 1), tree1.evaluate());

    Intervals tree2 = new IntervalTree("-5,-2 -3,1 I");
    assertEquals(new Interval(-3, -2), tree2.evaluate());
  }

  @Test
  public void testMixedSignIntervals() {
    Intervals tree1 = new IntervalTree("-3,2 1,5 U");
    assertEquals(new Interval(-3, 5), tree1.evaluate());

    Intervals tree2 = new IntervalTree("-3,2 1,5 I");
    assertEquals(new Interval(1, 2), tree2.evaluate());
  }

  @Test
  public void testZeroLengthInterval() {
    Intervals tree1 = new IntervalTree("5,5");
    assertEquals(new Interval(5, 5), tree1.evaluate());

    Intervals tree2 = new IntervalTree("5,5 5,5 U");
    assertEquals(new Interval(5, 5), tree2.evaluate());

    Intervals tree3 = new IntervalTree("5,5 5,5 I");
    assertEquals(new Interval(5, 5), tree3.evaluate());
  }

  @Test
  public void testZeroInterval() {
    Intervals tree = new IntervalTree("0,0 1,1 I");
    assertEquals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE), tree.evaluate());
  }

  @Test
  public void testLargeIntervals() {
    Intervals tree = new IntervalTree("1000000,2000000 1500000,2500000 U");
    assertEquals(new Interval(1000000, 2500000), tree.evaluate());
  }

  @Test
  public void testExamplesFromAssignment() {
    // Test "1,4 2,5 U"
    Intervals tree1 = new IntervalTree("1,4 2,5 U");
    assertEquals(new Interval(1, 5), tree1.evaluate());

    // Test "-4,4 2,5 U -1,4 I"
    Intervals tree2 = new IntervalTree("-4,4 2,5 U -1,4 I");
    // First: (-4,4) U (2,5) = (-4,5)
    // Then: (-4,5) I (-1,4) = (-1,4)
    assertEquals(new Interval(-1, 4), tree2.evaluate());

    // Test "3,7 2,6 4,10 I U"
    Intervals tree3 = new IntervalTree("3,7 2,6 4,10 I U");
    // First: (2,6) I (4,10) = (4,6)
    // Then: (3,7) U (4,6) = (3,7)
    assertEquals(new Interval(3, 7), tree3.evaluate());

    // Test "3,10 5,12 U 4,4 I"
    Intervals tree4 = new IntervalTree("3,10 5,12 U 4,4 I");
    // First: (3,10) U (5,12) = (3,12)
    // Then: (3,12) I (4,4) = (4,4)
    assertEquals(new Interval(4, 4), tree4.evaluate());
  }

  @Test
  public void testComplexExpression1() {
    Intervals tree = new IntervalTree("1,2 3,4 5,6 U I");
    // First: (3,4) U (5,6) = (3,6)
    // Then: (1,2) I (3,6) = no intersection
    assertEquals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE), tree.evaluate());
  }

  @Test
  public void testComplexExpression2() {
    // Test nested operations
    Intervals tree = new IntervalTree("1,5 2,6 I 3,7 4,8 I U");
    // First: (1,5) I (2,6) = (2,5)
    // Second: (3,7) I (4,8) = (4,7)
    // Finally: (2,5) U (4,7) = (2,7)
    assertEquals(new Interval(2, 7), tree.evaluate());
  }

  @Test
  public void testChainedUnions() {
    Intervals tree = new IntervalTree("1,2 3,4 U 5,6 U");
    // First: (1,2) U (3,4) = (1,4)
    // Then: (1,4) U (5,6) = (1,6)
    assertEquals(new Interval(1, 6), tree.evaluate());
  }

  @Test
  public void testChainedIntersections() {
    Intervals tree = new IntervalTree("1,10 2,8 I 3,7 I");
    // First: (1,10) I (2,8) = (2,8)
    // Then: (2,8) I (3,7) = (3,7)
    assertEquals(new Interval(3, 7), tree.evaluate());
  }

  @Test
  public void testMixedOperations() {
    Intervals tree = new IntervalTree("1,3 2,4 U 5,7 6,8 U I");
    // First: (1,3) U (2,4) = (1,4)
    // Second: (5,7) U (6,8) = (5,8)
    // Finally: (1,4) I (5,8) = no intersection
    assertEquals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE), tree.evaluate());
  }

  @Test
  public void testWhitespaceHandling() {
    // Test with extra spaces
    Intervals tree1 = new IntervalTree("  1,4   2,5   U  ");
    assertEquals(new Interval(1, 5), tree1.evaluate());

    // Test with tabs
    Intervals tree2 = new IntervalTree("1,4\t2,5\tU");
    assertEquals(new Interval(1, 5), tree2.evaluate());

    // Test with mixed whitespace
    Intervals tree3 = new IntervalTree("  1,4  \t 2,5 \t  U ");
    assertEquals(new Interval(1, 5), tree3.evaluate());
  }

  @Test
  public void testIntervalFormatVariations() {
    // Test that intervals must be in the format "start,end" without spaces
    // The implementation requires intervals to be single tokens
    Intervals tree1 = new IntervalTree("1,4 2,5 U");
    assertEquals(new Interval(1, 5), tree1.evaluate());

    // Test with negative numbers
    Intervals tree2 = new IntervalTree("-1,4 -2,5 U");
    assertEquals(new Interval(-2, 5), tree2.evaluate());
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
  public void testVeryLargeNegativeInterval() {
    Intervals tree = new IntervalTree("-1000000,-500000 -750000,-250000 U");
    assertEquals(new Interval(-1000000, -250000), tree.evaluate());
  }

  @Test
  public void testBoundaryValues() {
    // Test with Integer.MAX_VALUE (not quite, to avoid overflow in Interval)
    Intervals tree1 = new IntervalTree("2147483640,2147483645 2147483642,2147483646 U");
    assertEquals(new Interval(2147483640, 2147483646), tree1.evaluate());

    // Test with very negative values
    Intervals tree2 = new IntervalTree("-2147483640,-2147483635 -2147483638,-2147483633 U");
    assertEquals(new Interval(-2147483640, -2147483633), tree2.evaluate());
  }

  // Error cases

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
    new IntervalTree("1,2,3 4,5 U"); // Too many commas
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalFormat3() {
    new IntervalTree("1 2,3 U"); // Missing comma
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalFormat4() {
    new IntervalTree(",2 3,4 U"); // Empty start
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalFormat5() {
    new IntervalTree("1, 3,4 U"); // Empty end
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalValues1() {
    new IntervalTree("a,b 1,2 U"); // Non-numeric values
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalValues2() {
    new IntervalTree("1.5,2.5 3,4 U"); // Decimal values (should be integers)
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidOperator() {
    new IntervalTree("1,2 3,4 +"); // Invalid operator
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTooFewOperandsForOperator() {
    new IntervalTree("1,2 U"); // Only one operand for union
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
  public void testMultipleOperatorsNoOperands() {
    new IntervalTree("U I U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidIntervalOrder() {
    new IntervalTree("5,2 3,4 U"); // Start > End should fail in Interval constructor
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMixedInvalidTokens() {
    new IntervalTree("1,2 @ 3,4 # U");
  }


  @Test
  public void testAllOperators() {
    Intervals union = new IntervalTree("1,3 2,4 U");
    assertEquals(new Interval(1, 4), union.evaluate());

    Intervals intersect = new IntervalTree("1,3 2,4 I");
    assertEquals(new Interval(2, 3), intersect.evaluate());
  }

  @Test
  public void testVeryLongExpression() {
    // Build a long expression with many operations
    Intervals tree = new IntervalTree("1,2 2,3 U 3,4 U 4,5 U 5,6 U 6,7 U 7,8 U 8,9 U 9,10 U");
    assertEquals(new Interval(1, 10), tree.evaluate());
  }

  @Test
  public void testAlternatingOperations() {
    Intervals tree = new IntervalTree("0,10 2,8 I 1,5 U 3,7 I");
    assertEquals(new Interval(3, 7), tree.evaluate());
  }

  @Test
  public void testDeeplyNestedExpression() {
    Intervals tree = new IntervalTree("1,2 3,4 U 5,6 7,8 U U");
    assertEquals(new Interval(1, 8), tree.evaluate());
  }

  @Test
  public void testMathMinMaxMutations() {
    // Ensure Math.min/max aren't swapped
    Interval i1 = new Interval(1, 5);
    Interval i2 = new Interval(3, 7);

    Interval intersection = i1.intersect(i2);
    assertEquals("3,5", intersection.toString());
    // If Math.max and Math.min were swapped, would get different result

    Interval union = i1.union(i2);
    assertEquals("1,7", union.toString());
    // If Math.min and Math.max were swapped, would get different result
  }

  @Test
  public void testExactComparisonOperators() {
    // Test > vs >= mutations
    boolean caught = false;
    try {
      new Interval(5, 4); // start > end
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals("Invalid interval", e.getMessage());
    }
    assertEquals(true, caught);

    // Test that equal values are allowed (not caught)
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

    // Ensure operators aren't swapped
    Intervals complexTree = new IntervalTree("1,5 3,7 I 2,6 U");
    // (1,5) I (3,7) = (3,5), then (3,5) U (2,6) = (2,6)
    assertEquals("2,6", complexTree.evaluate().toString());
  }

  @Test
  public void testParseIntervalExactValidation() {
    // Test comma position check
    boolean caught = false;
    try {
      new IntervalTree("12 3,4 U"); // No comma in first token
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }
    assertEquals(true, caught);

    // Test multiple comma check
    caught = false;
    try {
      new IntervalTree("1,2,3 4,5 U"); // Multiple commas
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }
    assertEquals(true, caught);
  }

  @Test
  public void testMinValueHandling() {
    // Test Integer.MIN_VALUE assignment for no intersection
    Intervals tree = new IntervalTree("1,2 5,6 I");
    Interval result = tree.evaluate();

    // Both should be MIN_VALUE
    String[] parts = result.toString().split(",");
    assertEquals(String.valueOf(Integer.MIN_VALUE), parts[0]);
    assertEquals(String.valueOf(Integer.MIN_VALUE), parts[1]);
  }

  @Test
  public void testExactTextTreeFormat() {
    Intervals tree = new IntervalTree("1,2 3,4 U");
    String textTree = tree.textTree();

    String[] lines = textTree.split("\n");
    assertEquals("U", lines[0]);
    assertEquals("|", lines[1]);
    assertEquals("|", lines[2]);
    assertEquals("|___1,2", lines[3]);
    assertEquals("|", lines[4]);
    assertEquals("|___3,4", lines[5]);
  }

  @Test
  public void testEqualsExactBehavior() {
    Interval i1 = new Interval(1, 5);
    Interval i2 = new Interval(1, 5);

    // Test reflexivity
    assertEquals(true, i1.equals(i1));

    // Test symmetry
    assertEquals(true, i1.equals(i2));
    assertEquals(true, i2.equals(i1));

    // Test different end
    final Interval i3 = new Interval(1, 6);
    assertEquals(false, i1.equals(i3));

    // Test different start
    final Interval i4 = new Interval(2, 5);
    assertEquals(false, i1.equals(i4));

    // Test null
    assertEquals(false, i1.equals(null));

    // Test different type
    assertEquals(false, i1.equals("1,5"));
  }

  @Test
  public void testHashCodeConsistency() {
    Interval i1 = new Interval(5, 10);
    Interval i2 = new Interval(5, 10);

    // Equal objects must have equal hash codes
    assertEquals(i1.hashCode(), i2.hashCode());

    // Test that hash code is consistent
    int hash1 = i1.hashCode();
    int hash2 = i1.hashCode();
    assertEquals(hash1, hash2);
  }

  @Test
  public void testIntervalIntersectionCondition() {
    // Test the min > max condition precisely
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
    // Ensure Math.min and Math.max are used correctly
    Interval i1 = new Interval(5, 10);
    Interval i2 = new Interval(1, 7);

    Interval union = i1.union(i2);
    assertEquals("1,10", union.toString());
    // If Math.min/max were swapped, would get "5,7"

    Interval i3 = new Interval(1, 3);
    Interval i4 = new Interval(8, 10);
    Interval union2 = i3.union(i4);
    assertEquals("1,10", union2.toString());
  }

  @Test
  public void testIntervalStartEndComparison() {
    // Test the start > end check
    boolean caught = false;
    try {
      new Interval(10, 5);
    } catch (IllegalArgumentException e) {
      caught = true;
      assertEquals("Invalid interval", e.getMessage());
    }
    assertEquals(true, caught);

    // Test that start == end is allowed
    Interval i = new Interval(5, 5);
    assertEquals("5,5", i.toString());
  }

  @Test
  public void testIntervalTreeOperatorSwitch() {
    // Ensure U and I operators work correctly and aren't swapped
    Intervals t1 = new IntervalTree("1,5 3,7 U");
    assertEquals("1,7", t1.evaluate().toString());

    Intervals t2 = new IntervalTree("1,5 3,7 I");
    assertEquals("3,5", t2.evaluate().toString());

    // Test that they're different
    Intervals t3 = new IntervalTree("1,3 5,7 U");
    Intervals t4 = new IntervalTree("1,3 5,7 I");
    String r3 = t3.evaluate().toString();
    String r4 = t4.evaluate().toString();
    assertEquals(false, r3.equals(r4));
  }

  @Test
  public void testParseIntervalCommaChecks() {
    // Test indexOf returning -1
    try {
      new IntervalTree("12 3,4 U");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }

    // Test second indexOf check
    try {
      new IntervalTree("1,2,3 4,5 U");
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }
  }

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

  @Test
  public void testIntervalEqualsAllBranches() {
    Interval i1 = new Interval(5, 10);

    // Test this == other
    assertEquals(true, i1.equals(i1));

    // Test instanceof false
    assertEquals(false, i1.equals("not an interval"));
    assertEquals(false, i1.equals(null));
    assertEquals(false, i1.equals(Integer.valueOf(5)));

    // Test start mismatch
    Interval i2 = new Interval(6, 10);
    assertEquals(false, i1.equals(i2));

    // Test end mismatch
    Interval i3 = new Interval(5, 11);
    assertEquals(false, i1.equals(i3));

    // Test both match
    Interval i4 = new Interval(5, 10);
    assertEquals(true, i1.equals(i4));
  }
}