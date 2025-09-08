package expression;

import java.util.Stack;

/**
 * This class represents an expression tree for algebraic expressions.
 * It can parse postfix expressions and evaluate them, as well as convert
 * to various formats including infix, Scheme, and tree visualization.
 */
public class ExpressionTree implements Expression {

    /**
     * Abstract base class for expression tree nodes.
     */
    private abstract static class Node {
        abstract double evaluate();
        abstract String infix();
        abstract String schemeExpression();
        abstract String textTree(String prefix, boolean isLast);
        abstract int getHeight();
    }

    /**
     * Node representing a numeric operand.
     */
    private static class NumberNode extends Node {
        private final double value;

        public NumberNode(double value) {
            this.value = value;
        }

        @Override
        double evaluate() {
            return value;
        }

        @Override
        String infix() {
            // Format the number appropriately
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                return String.format("%.1f", value);
            }
            return String.valueOf(value);
        }

        @Override
        String schemeExpression() {
            // Format the number appropriately for Scheme
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                return String.format("%.1f", value);
            }
            return String.valueOf(value);
        }

        @Override
        String textTree(String prefix, boolean isLast) {
            // Just return the value for leaf nodes
            if (value == Math.floor(value) && !Double.isInfinite(value)) {
                return String.format("%.1f", value);
            }
            return String.valueOf(value);
        }

        @Override
        int getHeight() {
            return 1;
        }
    }

    /**
     * Node representing a binary operator.
     */
    private static class OperatorNode extends Node {
        private final String operator;
        private final Node left;
        private final Node right;

        public OperatorNode(String operator, Node left, Node right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        @Override
        double evaluate() {
            double leftVal = left.evaluate();
            double rightVal = right.evaluate();

            switch (operator) {
                case "+":
                    return leftVal + rightVal;
                case "-":
                    return leftVal - rightVal;
                case "*":
                    return leftVal * rightVal;
                case "/":
                    if (rightVal == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return leftVal / rightVal;
                default:
                    // This should never happen if isOperator is correct
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }

        @Override
        String infix() {
            return "( " + left.infix() + " " + operator + " " + right.infix() + " )";
        }

        @Override
        String schemeExpression() {
            return "( " + operator + " " + left.schemeExpression() + " " +
                    right.schemeExpression() + " )";
        }

        @Override
        String textTree(String prefix, boolean isLast) {
            StringBuilder result = new StringBuilder();

            // Add the operator
            result.append(operator).append("\n");

            // Add the connecting lines
            result.append(prefix).append("|\n");
            result.append(prefix).append("|\n");

            // Add the left child with its prefix
            result.append(prefix).append("|___");
            String leftResult = left.textTree(prefix + "|   ", false);
            result.append(leftResult);

            // Always add newline after left child
            if (!leftResult.endsWith("\n")) {
                result.append("\n");
            }

            // Add spacing line before right child
            result.append(prefix).append("|\n");

            // Add the right child
            result.append(prefix).append("|___");
            String rightResult = right.textTree(prefix + "    ", true);
            result.append(rightResult);

            // Don't add trailing newline - parent will handle it

            return result.toString();
        }

        @Override
        int getHeight() {
            return 1 + Math.max(left.getHeight(), right.getHeight());
        }
    }

    private final Node root;

    /**
     * Constructs an ExpressionTree from a postfix expression string.
     * @param postfixExpression the postfix expression as a space-separated string
     * @throws IllegalArgumentException if the expression is invalid
     */
    public ExpressionTree(String postfixExpression) throws IllegalArgumentException {
        if (postfixExpression == null || postfixExpression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        this.root = parsePostfix(postfixExpression.trim());
    }

    /**
     * Parses a postfix expression and builds the expression tree.
     * @param expression the postfix expression
     * @return the root node of the expression tree
     * @throws IllegalArgumentException if the expression is invalid
     */
    private Node parsePostfix(String expression) throws IllegalArgumentException {
        String[] tokens = expression.split("\\s+");
        Stack<Node> stack = new Stack<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                // Check if we have enough operands
                if (stack.size() < 2) {
                    throw new IllegalArgumentException(
                            "Invalid expression: insufficient operands for operator " + token);
                }
                // Pop in reverse order (right first, then left)
                Node right = stack.pop();
                Node left = stack.pop();
                stack.push(new OperatorNode(token, left, right));
            } else {
                // Try to parse as a number
                try {
                    double value = Double.parseDouble(token);
                    stack.push(new NumberNode(value));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid token: " + token);
                }
            }
        }

        // Check that we have exactly one element left (the root)
        if (stack.size() != 1) {
            if (stack.isEmpty()) {
                throw new IllegalArgumentException("Invalid expression: no result");
            } else {
                throw new IllegalArgumentException(
                        "Invalid expression: too many operands");
            }
        }

        return stack.pop();
    }

    /**
     * Checks if a token is a valid operator.
     * @param token the token to check
     * @return true if the token is an operator, false otherwise
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") ||
                token.equals("*") || token.equals("/");
    }

    @Override
    public double evaluate() {
        return root.evaluate();
    }

    @Override
    public String infix() {
        return root.infix();
    }

    @Override
    public String schemeExpression() {
        return root.schemeExpression();
    }

    @Override
    public String textTree() {
        return root.textTree("", true);
    }
}