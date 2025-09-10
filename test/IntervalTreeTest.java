import static org.junit.Assert.assertEquals;

import intervals.Interval;
import intervals.IntervalTree;
import intervals.Intervals;
import org.junit.Test;

/**
 * Tests for interval tree implementation.
 */
public class IntervalTreeTest {

  @Test
  public void testSingleInterval() {
    Intervals t = new IntervalTree("5,10");
    Interval res = t.evaluate();
    assertEquals(new Interval(5, 10), res);
    assertEquals("5,10", t.textTree());
  }

  @Test
  public void testBasicUnion() {
    Intervals tree = new IntervalTree("1,4 2,5 U");
    Interval result = tree.evaluate();
    assertEquals(new Interval(1, 5), result);

    // check the tree output
    String expectedTree = "U\n|\n|\n|___1,4\n|\n|___2,5";
    assertEquals(expectedTree, tree.textTree());
  }

  @Test
  public void testBasicIntersect() {
    Intervals tree = new IntervalTree("1,4 2,5 I");
    Interval r = tree.evaluate();
    assertEquals(new Interval(2, 4), r);

    String exp = "I\n|\n|\n|___1,4\n|\n|___2,5";
    assertEquals(exp, tree.textTree());
  }

  @Test
  public void testNoOverlap() {
    // these don't overlap so intersection should be empty
    Intervals t = new IntervalTree("1,3 5,7 I");
    Interval result = t.evaluate();
    assertEquals(new Interval(Integer.MIN_VALUE, Integer.MIN_VALUE), result);
  }

  @Test
  public void testTouching() {
    // what happens when intervals touch at one point
    Intervals unionTree = new IntervalTree("1,4 4,7 U");
    assertEquals(new Interval(1, 7), unionTree.evaluate());

    Intervals intTree = new IntervalTree("1,4 4,7 I");
    Interval res = intTree.evaluate();
    assertEquals(new Interval(4, 4), res);  // just the point 4
  }

  @Test
  public void testNegativeNumbers() {
    Intervals t1 = new IntervalTree("-5,-2 -3,1 U");
    assertEquals(new Interval(-5, 1), t1.evaluate());

    Intervals t2 = new IntervalTree("-5,-2 -3,1 I");
    Interval intersection = t2.evaluate();
    assertEquals(new Interval(-3, -2), intersection);
  }

  @Test
  public void testPointInterval() {
    // interval with same start and end
    Intervals tree = new IntervalTree("5,5");
    assertEquals(new Interval(5, 5), tree.evaluate());

    Intervals tree2 = new IntervalTree("5,5 5,5 U");
    assertEquals(new Interval(5, 5), tree2.evaluate());
  }

  @Test
  public void testComplexOps() {
    // from assignment: "-4,4 2,5 U -1,4 I"
    Intervals tree = new IntervalTree("-4,4 2,5 U -1,4 I");
    // (-4,4) U (2,5) = (-4,5)
    // (-4,5) I (-1,4) = (-1,4)
    Interval expected = new Interval(-1, 4);
    assertEquals(expected, tree.evaluate());
  }

  @Test
  public void testChaining() {
    // multiple operations
    Intervals tree = new IntervalTree("1,5 2,6 I 3,7 4,8 I U");
    assertEquals(new Interval(2, 7), tree.evaluate());
  }

  @Test
  public void testWhitespace() {
    // extra spaces shouldn't matter
    Intervals t1 = new IntervalTree("  1,4   2,5   U  ");
    assertEquals(new Interval(1, 5), t1.evaluate());

    // tabs too
    Intervals t2 = new IntervalTree("1,4\t2,5\tU");
    assertEquals(new Interval(1, 5), t2.evaluate());
  }

  @Test
  public void testTreeOutput() {
    Intervals tree = new IntervalTree("1,2 3,4 5,6 U I");
    String treeStr = "I\n"
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
    assertEquals(treeStr, tree.textTree());
  }

  // Testing interval operations directly

  @Test
  public void testIntervalMethods() {
    Interval i1 = new Interval(1, 5);
    Interval i2 = new Interval(3, 7);

    // intersection should be (3,5)
    Interval inter = i1.intersect(i2);
    assertEquals("3,5", inter.toString());

    // union should be (1,7)
    Interval uni = i1.union(i2);
    assertEquals("1,7", uni.toString());
  }

  @Test
  public void testIntervalValidation() {
    boolean exceptionThrown = false;
    try {
      new Interval(5, 4);  // start > end, should fail
    } catch (IllegalArgumentException e) {
      exceptionThrown = true;
      assertEquals("Invalid interval", e.getMessage());
    }
    assertEquals(true, exceptionThrown);

    // but equal is fine
    Interval i = new Interval(5, 5);
    assertEquals("5,5", i.toString());
  }

  @Test
  public void testOperatorBehavior() {
    // make sure U and I work correctly
    Intervals u = new IntervalTree("1,3 2,4 U");
    assertEquals("1,4", u.evaluate().toString());

    Intervals i = new IntervalTree("1,3 2,4 I");
    assertEquals("2,3", i.evaluate().toString());

    // complex one
    Intervals c = new IntervalTree("1,5 3,7 I 2,6 U");
    assertEquals("2,6", c.evaluate().toString());
  }

  @Test
  public void testEmptyIntersection() {
    Intervals tree = new IntervalTree("1,2 5,6 I");
    Interval res = tree.evaluate();

    // should be MIN_VALUE for both
    String[] parts = res.toString().split(",");
    assertEquals(String.valueOf(Integer.MIN_VALUE), parts[0]);
    assertEquals(String.valueOf(Integer.MIN_VALUE), parts[1]);
  }

  @Test
  public void testNoIntersectDetails() {
    Interval i1 = new Interval(1, 2);
    Interval i2 = new Interval(3, 4);

    Interval result = i1.intersect(i2);
    // max(1,3) = 3, min(2,4) = 2, but 3 > 2 so no intersection
    assertEquals("-2147483648,-2147483648", result.toString());
  }

  @Test
  public void testUnionLogic() {
    Interval int1 = new Interval(5, 10);
    Interval int2 = new Interval(1, 7);
    Interval u = int1.union(int2);
    assertEquals("1,10", u.toString());

    // non-overlapping
    Interval int3 = new Interval(1, 3);
    Interval int4 = new Interval(8, 10);
    Interval u2 = int3.union(int4);
    assertEquals("1,10", u2.toString());
  }

  @Test
  public void testEquals() {
    Interval i1 = new Interval(1, 5);
    Interval i2 = new Interval(1, 5);

    // reflexive
    assertEquals(true, i1.equals(i1));

    // symmetric
    assertEquals(true, i1.equals(i2));
    assertEquals(true, i2.equals(i1));

    // different intervals
    Interval i3 = new Interval(1, 6);
    assertEquals(false, i1.equals(i3));

    Interval i4 = new Interval(2, 5);
    assertEquals(false, i1.equals(i4));

    // null and wrong type
    assertEquals(false, i1.equals(null));
    assertEquals(false, i1.equals("1,5"));
  }

  @Test
  public void testHashCode() {
    Interval i1 = new Interval(5, 10);
    Interval i2 = new Interval(5, 10);

    // equal objects should have same hash
    assertEquals(i1.hashCode(), i2.hashCode());

    // consistent
    int h1 = i1.hashCode();
    int h2 = i1.hashCode();
    assertEquals(h1, h2);
  }

  @Test
  public void testBadCommaFormat() {
    try {
      new IntervalTree("12 3,4 U");  // no comma in first
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }

    try {
      new IntervalTree("1,2,3 4,5 U");  // too many commas
      assertEquals("Should throw", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals(true, e.getMessage().contains("Invalid interval"));
    }
  }

  // Error cases

  @Test(expected = IllegalArgumentException.class)
  public void nullTest() {
    new IntervalTree(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyTest() {
    new IntervalTree("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void spacesOnly() {
    new IntervalTree("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void dashNotComma() {
    new IntervalTree("1-4 2,5 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingComma() {
    new IntervalTree("1 2,3 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyStart() {
    new IntervalTree(",2 3,4 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void notNumbers() {
    new IntervalTree("a,b 1,2 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void decimalsNotAllowed() {
    new IntervalTree("1.5,2.5 3,4 U");  // we only support integers
  }

  @Test(expected = IllegalArgumentException.class)
  public void wrongOperator() {
    new IntervalTree("1,2 3,4 +");  // + is not valid
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEnoughArgs() {
    new IntervalTree("1,2 U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEnoughArgs2() {
    new IntervalTree("1,2 3,4 U I");  // I needs another operand
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooManyArgs() {
    new IntervalTree("1,2 3,4 5,6 U");  // U only takes 2
  }

  @Test(expected = IllegalArgumentException.class)
  public void operatorOnly() {
    new IntervalTree("U");
  }

  @Test(expected = IllegalArgumentException.class)
  public void badOrder() {
    new IntervalTree("5,2 3,4 U");  // 5 > 2, invalid interval
  }

  @Test
  public void testEmptyStringVariations() {
    // different types of whitespace
    try {
      new IntervalTree(" ");
      assertEquals("Should fail", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }

    try {
      new IntervalTree("\t\n");
      assertEquals("Should fail", true, false);
    } catch (IllegalArgumentException e) {
      assertEquals("Expression cannot be null or empty", e.getMessage());
    }
  }
}