# Java Program Compilation and Execution

This guide provides instructions on how to compile and run a Java program.

## Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK)
- A terminal or command prompt

## Compilation

To compile the Java program, use the following command:

```bash
javac run.java
```

This will generate a `run.class` file in the same directory.

## Execution

To run the compiled Java program, use the command:

```bash
java run
```

## Notes

- Ensure that `run.java` is in the current working directory.
- Do not include the `.class` extension when running the program.
- If your program contains a `package` declaration, navigate to the correct directory or use the `-cp` option.
- If your program requires additional libraries, include them using the `-classpath` (`-cp`) option.

For further troubleshooting, refer to the official [Java Documentation](https://docs.oracle.com/en/java/).
