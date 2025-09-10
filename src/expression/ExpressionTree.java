package expression;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Expression tree for algebraic expressions parsed from postfix.
 *
 * <p>Supports evaluation, infix/Scheme printing, and a simple text tree view.
 */
public class ExpressionTree implements Expression {

  /** Base node for the tree. */
  private abstract static class Node {
    abstract double evaluate();

    abstract String infix();

    abstract String schemeExpression();

    abstract String textTree(String prefix, boolean isLast);
  }

  /** Leaf node holding a number. */
  private static final class NumberNode extends Node {
    private final double value;

    NumberNode(double value) {
      this.value = value;
    }

    @Override
    double evaluate() {
      return value;
    }

    @Override
    String infix() {
      return fmt(value);
    }

    @Override
    String schemeExpression() {
      return fmt(value);
    }

    @Override
    String textTree(String prefix, boolean isLast) {
      return fmt(value);
    }
  }

  /** Internal node holding a binary operator. */
  private static final class OperatorNode extends Node {
    private final String op;
    private final Node left;
    private final Node right;

    OperatorNode(String op, Node left, Node right) {
      this.op = op;
      this.left = left;
      this.right = right;
    }

    @Override
    double evaluate() {
      final double a = left.evaluate();
      final double b = right.evaluate();
      switch (op) {
        case "+":
          return a + b;
        case "-":
          return a - b;
        case "*":
          return a * b;
        case "/":
          if (b == 0.0) {
            throw new ArithmeticException("division by zero");
          }
          return a / b;
        default:
          throw new IllegalArgumentException("unknown operator: " + op);
      }
    }

    @Override
    String infix() {
      return "( " + left.infix() + " " + op + " " + right.infix() + " )";
    }

    @Override
    String schemeExpression() {
      return "( " + op + " " + left.schemeExpression() + " " + right.schemeExpression() + " )";
    }

    @Override
    String textTree(String prefix, boolean isLast) {
      StringBuilder sb = new StringBuilder();
      sb.append(op).append('\n');

      // connector lines from operator to children
      sb.append(prefix).append("|\n");
      sb.append(prefix).append("|\n");

      // left child
      sb.append(prefix).append("|___");
      String leftStr = left.textTree(prefix + "|   ", false);
      sb.append(leftStr);
      if (!leftStr.endsWith("\n")) {
        sb.append('\n');
      }

      // spacer before right child
      sb.append(prefix).append("|\n");

      // right child
      sb.append(prefix).append("|___");
      sb.append(right.textTree(prefix + "    ", true));

      return sb.toString();
    }
  }

  private final Node root;

  /**
   * Build an expression tree from a space-separated postfix string.
   *
   * @param postfix space-separated tokens (numbers and + - * /)
   * @throws IllegalArgumentException if the expression is malformed
   */
  public ExpressionTree(String postfix) {
    if (postfix == null || postfix.trim().isEmpty()) {
      throw new IllegalArgumentException("expression must be non-empty");
    }
    this.root = parsePostfix(postfix.trim());
  }

  private Node parsePostfix(String s) {
    String[] tokens = s.split("\\s+");
    Deque<Node> st = new ArrayDeque<>();

    for (String tok : tokens) {
      if (isOperator(tok)) {
        if (st.size() < 2) {
          throw new IllegalArgumentException("insufficient operands for operator: " + tok);
        }
        Node right = st.pop();
        Node left = st.pop();
        st.push(new OperatorNode(tok, left, right));
      } else {
        try {
          st.push(new NumberNode(Double.parseDouble(tok)));
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("invalid token: " + tok);
        }
      }
    }

    if (st.size() != 1) {
      throw new IllegalArgumentException(st.isEmpty()
          ? "no result produced"
          : "too many operands");
    }
    return st.pop();
  }

  private static boolean isOperator(String t) {
    return "+".equals(t) || "-".equals(t) || "*".equals(t) || "/".equals(t);
  }

  /** Format numbers: show "3.0" for integers to match sample output. */
  private static String fmt(double v) {
    if (Double.isInfinite(v) || Double.isNaN(v)) {
      return String.valueOf(v);
    }
    if (v == Math.floor(v)) {
      return String.format("%.1f", v);
    }
    return String.valueOf(v);
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
