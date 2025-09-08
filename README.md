# Assignment 5: Composites - Tree Structures Implementation

This project implements two tree data structures for handling composite operations:

1. **ExpressionTree** - For algebraic expressions with basic arithmetic operations
2. **IntervalTree** - For interval operations (union and intersection)

## Project Structure

```
├── src/
│   ├── expression/
│   │   ├── Expression.java        # Interface for expression operations
│   │   └── ExpressionTree.java    # Implementation of expression tree
│   └── intervals/
│       ├── Interval.java          # Interval class with union/intersect operations
│       ├── Intervals.java         # Interface for interval tree operations
│       └── IntervalTree.java      # Implementation of interval tree
├── test/
│   ├── ExpressionTreeTest.java    # JUnit tests for ExpressionTree
│   ├── IntervalTreeTest.java      # JUnit tests for IntervalTree
│   └── SimpleTestRunner.java     # Simple test runner without JUnit dependency
└── README.md
```

## Implementation Details

### ExpressionTree

The `ExpressionTree` class implements the `Expression` interface and provides:

- **Postfix Expression Parsing**: Constructs expression trees from postfix notation
- **Evaluation**: Evaluates the expression to a double value
- **Multiple Formats**: 
  - Infix notation with full parentheses
  - Scheme expression format
  - Visual tree representation

**Supported Operations**: `+`, `-`, `*`, `/`

**Example Usage**:
```java
ExpressionTree tree = new ExpressionTree("1 2 +");
System.out.println(tree.evaluate());           // 3.0
System.out.println(tree.infix());             // ( 1.0 + 2.0 )
System.out.println(tree.schemeExpression());   // ( + 1.0 2.0 )
```

### IntervalTree

The `IntervalTree` class implements the `Intervals` interface and provides:

- **Postfix Interval Expression Parsing**: Constructs trees from postfix interval notation
- **Interval Evaluation**: Returns the resulting interval after operations
- **Visual Tree Representation**: Shows the tree structure

**Supported Operations**: 
- `U` (Union)
- `I` (Intersection)

**Example Usage**:
```java
IntervalTree tree = new IntervalTree("1,4 2,5 U");
Interval result = tree.evaluate();  // Results in interval (1,5)
```

## Key Features

### Expression Tree Features
- Handles negative numbers and decimals
- Comprehensive error handling for invalid expressions
- Division by zero protection
- Full parenthesization in infix output
- Scheme syntax support

### Interval Tree Features
- Proper handling of non-overlapping intervals
- Union and intersection operations following mathematical definitions
- Support for negative intervals
- Error handling for malformed interval syntax

### Tree Visualization
Both implementations provide `textTree()` methods that create visual representations:

```
+
|
|
|___1.0
|
|___2.0
```

## Testing

### Running Tests

**With JUnit** (if available):
```bash
javac -cp .:junit-platform-console-standalone.jar src/**/*.java test/*.java
java -cp .:junit-platform-console-standalone.jar org.junit.platform.console.ConsoleLauncher --scan-classpath
```

**Without JUnit** (using SimpleTestRunner):
```bash
javac -cp src src/**/*.java test/SimpleTestRunner.java
java -cp src:test SimpleTestRunner
```

### Test Coverage

The test suite covers:
- Basic operations (arithmetic/interval operations)
- Complex nested expressions
- Edge cases (division by zero, non-overlapping intervals)
- Error conditions (invalid syntax, insufficient operands)
- Format validation (infix, scheme, tree visualization)

## Algorithm Details

### Expression Tree Construction
1. Parse postfix expression tokens
2. Use stack-based algorithm:
   - Push operands onto stack
   - For operators: pop two operands, create node, push back
3. Final stack should contain exactly one node (the root)

### Interval Tree Construction
1. Parse postfix interval expression tokens
2. Parse intervals in "start,end" format
3. Use same stack-based approach as expression tree
4. Support Union (U) and Intersection (I) operations

### Tree Traversal for Output Formats
- **Infix**: In-order traversal with parentheses
- **Scheme**: Pre-order traversal with Scheme syntax
- **Tree Visualization**: Custom recursive formatting

## Error Handling

Both implementations include comprehensive error handling:
- Malformed input expressions
- Invalid tokens/operators
- Insufficient or excess operands
- Mathematical errors (division by zero)
- Null/empty input validation

## Assignment Requirements Compliance

✅ **Interface Implementation**: Both classes implement their respective interfaces  
✅ **Postfix Parsing**: Constructor takes postfix expression strings  
✅ **Tree Structure**: Internal tree representation using composite pattern  
✅ **All Required Methods**: evaluate(), infix(), schemeExpression(), textTree()  
✅ **Exception Handling**: IllegalArgumentException for invalid expressions  
✅ **Package Organization**: Code in expression/intervals packages, tests in default  
✅ **No Additional Public Methods**: Only interface methods and constructor are public  

## Design Patterns Used

- **Composite Pattern**: Tree nodes (OperatorNode, NumberNode/IntervalNode)
- **Strategy Pattern**: Different node types implement evaluation differently  
- **Template Method**: Common tree traversal patterns with specific implementations

## Compilation Instructions

```bash
# Compile all source files
javac -cp src src/expression/*.java src/intervals/*.java

# Compile tests (requires test files in classpath)
javac -cp src test/*.java

# Run simple test runner
java -cp src:test SimpleTestRunner
```