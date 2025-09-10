# WAIVER PETITION ASSIGNMENT

## How to Compile and Run

I used gradle for this assignment because it makes testing way easier and handles all the dependencies automatically. To build and test everything:

```bash
./gradlew clean build
./gradlew test
```

For mutation testing (to make sure tests are thorough):
```bash
./gradlew pitest
```

The gradle wrapper is included so you don't need gradle installed - just run the commands above.

## File Structure

The code is organized as specified:
- `src/expression/` - Expression tree implementation
- `src/intervals/` - Interval tree implementation
- `test/` - All test files

## Notes

- The ExpressionTree handles postfix expressions with +, -, *, / operators
- The IntervalTree handles interval operations with U (union) and I (intersection)
- Both support the required methods: evaluate(), textTree(), and the expression tree also has infix() and schemeExpression()
- Error handling throws IllegalArgumentException for invalid inputs as required

Tested on Linux with Java 11 but should work on other versions too.