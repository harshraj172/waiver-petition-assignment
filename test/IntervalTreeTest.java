import intervals.IntervalTree;
import intervals.Intervals;
import intervals.Interval;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Comprehensive JUnit 4 tests for IntervalTree implementation.
 * Designed to achieve 100% code coverage and pass PIT mutation testing.
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
        String expected = "I\n" +
                "|\n" +
                "|\n" +
                "|___1,2\n" +
                "|\n" +
                "|___U\n" +
                "    |\n" +
                "    |\n" +
                "    |___3,4\n" +
                "    |\n" +
                "    |___5,6";
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
        new IntervalTree("1,2 3,4 U I"); // Not enough operands for second I
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooManyOperands() {
        new IntervalTree("1,2 3,4 5,6 U"); // Three operands, one operator
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnlyOperator() {
        new IntervalTree("U"); // No operands
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleOperatorsNoOperands() {
        new IntervalTree("U I U"); // Only operators
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIntervalOrder() {
        new IntervalTree("5,2 3,4 U"); // Start > End should fail in Interval constructor
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMixedInvalidTokens() {
        new IntervalTree("1,2 @ 3,4 # U");
    }

    // Edge cases for complete coverage

    @Test
    public void testAllOperators() {
        // Ensure both operators are tested
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
        // Test alternating U and I operations
        Intervals tree = new IntervalTree("0,10 2,8 I 1,5 U 3,7 I");
        // First: (0,10) I (2,8) = (2,8)
        // Second: (2,8) U (1,5) = (1,8)
        // Third: (1,8) I (3,7) = (3,7)
        assertEquals(new Interval(3, 7), tree.evaluate());
    }

    @Test
    public void testDeeplyNestedExpression() {
        // Test deeply nested structure
        Intervals tree = new IntervalTree("1,2 3,4 U 5,6 7,8 U U");
        // First: (1,2) U (3,4) = (1,4)
        // Second: (5,6) U (7,8) = (5,8)
        // Finally: (1,4) U (5,8) = (1,8)
        assertEquals(new Interval(1, 8), tree.evaluate());
    }
}