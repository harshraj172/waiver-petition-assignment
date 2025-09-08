import expression.ExpressionTree;
import expression.Expression;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

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
        String expected = "/\n" +
                "|\n" +
                "|\n" +
                "|___1.0\n" +
                "|\n" +
                "|___+\n" +
                "    |\n" +
                "    |\n" +
                "    |___-\n" +
                "    |   |\n" +
                "    |   |\n" +
                "    |   |___4.0\n" +
                "    |   |\n" +
                "    |   |___6.0\n" +
                "    |\n" +
                "    |___5.0";
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
}