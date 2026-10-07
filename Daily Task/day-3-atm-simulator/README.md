# ATM Simulator

## Overview
The **ATM Simulator** is a console-based Core Java application built with **Java 21** and **Apache Maven**. It simulates essential ATM functionalities including PIN authentication with security lockout, balance inquiry, money deposit, withdrawal with limit checks, input validation, mini statement generation using transaction history, and environment profile switching via Maven properties (`dev` vs `prod`).

---

## Objectives
- Demonstrate core Java control flow structures (`do-while`, `switch`, `if-else`, `break`, `continue`).
- Implement collection handling (`ArrayList<Transaction>`) and iteration via enhanced-for loops.
- Apply robust user input validation using Java `Scanner` to prevent application crashes.
- Structure a Java application using standard Maven directory conventions and lifecycle management.
- Configure and filter environment-specific application properties using Maven build profiles (`dev` and `prod`).
- Write unit tests using **JUnit 5** to verify core financial transactions.

---

## Features
- **PIN Authentication & Lockout:** Secure 3-attempt PIN validation (`1234`). Automatically locks the account after 3 failed attempts using a `break` statement.
- **ATM Interactive Menu:** Driven by a `do-while` loop and `switch` statement for continuous operation until exit.
- **Balance Inquiry:** Displays current account balance in Indian Rupees (`₹`).
- **Money Deposit:** Validates positive numeric entry, updates balance, and logs deposit transactions.
- **Money Withdrawal:** Checks for positive amounts, available balance, and profile-based maximum withdrawal limits per transaction.
- **Mini Statement:** Displays formatted transaction history rendered using an **enhanced-for loop**.
- **Input Validation:** Catches non-numeric inputs, negative amounts, out-of-range menu selections, and empty inputs gracefully without crashing.
- **Maven Profiles:** `dev` profile sets `max.withdrawal.limit=50000.0`, while `prod` profile enforces `max.withdrawal.limit=20000.0`.

---

## Java Concepts Demonstrated
- `do-while` loop: Guarantees menu display at least once and keeps session active until user selects option 5 (Exit).
- `switch` statement: Routes menu choices (1-5) to corresponding ATM actions cleanly.
- `break` statement: Exits authentication loop upon successful PIN entry or after reaching 3 failed attempts.
- `continue` statement: Jumps to the next iteration of the `do-while` loop when an invalid menu choice or transaction amount is detected.
- **Enhanced-for loop:** Iterates over the `List<Transaction>` collection to output the mini-statement.
- `ArrayList`: Dynamically records transaction logs (`Transaction` objects).
- **Exception Handling:** Catches `NumberFormatException` during input parsing to prevent application crashes.

---

## Maven Concepts
- **`pom.xml`:** Defines project metadata, Java 21 compiler configuration, JUnit 5 dependencies, and Maven build plugins.
- **Maven Build Lifecycle:**
  - `clean`: Removes previously built build artifacts in the `target/` directory.
  - `compile`: Compiles Java source files in `src/main/java`.
  - `test`: Executes JUnit 5 unit tests in `src/test/java`.
  - `package`: Packs compiled binaries and filtered resource files into an executable JAR (`target/atm-simulator-1.0-SNAPSHOT.jar`).

---

## Maven Profiles

### Development Profile (`dev`)
Activated by default or explicitly via:
```bash
mvn clean package -Pdev
```
- Sets `environment=development`
- Sets `max.withdrawal.limit=50000.0`

### Production Profile (`prod`)
Activated explicitly via:
```bash
mvn clean package -Pprod
```
- Sets `environment=production`
- Sets `max.withdrawal.limit=20000.0`

> **Property Difference Explanation:** Maven resource filtering injects build profile properties into `application.properties` during packaging. The ATM runtime reads these properties at startup to enforce profile-specific withdrawal limits and display the active environment mode.

---

## Project Structure
```text
day-3-atm-simulator/
│
├── pom.xml
├── README.md
├── .gitignore
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── atm/
│   │   │           └── simulator/
│   │   │               ├── Main.java
│   │   │               ├── ATM.java
│   │   │               └── Transaction.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── atm/
│                   └── simulator/
│                       └── ATMTest.java
│
└── target/  (Generated build directory - excluded from version control)
```

---

## How to Test
Execute all JUnit 5 unit tests with:
```bash
mvn test
```

---

## How to Build
Build executable JAR with default (`dev`) profile:
```bash
mvn clean package
```

Build executable JAR with production (`prod`) profile:
```bash
mvn clean package -Pprod
```

---

## How to Run
After building the package, launch the compiled JAR using Java:
```bash
java -jar target/atm-simulator-1.0-SNAPSHOT.jar
```

Or run directly using Maven:
```bash
mvn exec:java
```

---

## Sample Output

```text
Enter PIN: 1234
Access granted.

========================================
              ATM SIMULATOR
========================================
1. Check Balance
2. Deposit Money
3. Withdraw Money
4. Mini Statement
5. Exit
========================================
Enter your choice: 2
Enter deposit amount: 5000

Deposit successful.
Current Balance: ₹20000.00

========================================
              ATM SIMULATOR
========================================
1. Check Balance
2. Deposit Money
3. Withdraw Money
4. Mini Statement
5. Exit
========================================
Enter your choice: 3
Enter withdrawal amount: 3000

Withdrawal successful.
Current Balance: ₹17000.00

========================================
              ATM SIMULATOR
========================================
1. Check Balance
2. Deposit Money
3. Withdraw Money
4. Mini Statement
5. Exit
========================================
Enter your choice: 4

========================================
             MINI STATEMENT
========================================
Deposit      +₹5000.00  (Balance: ₹20000.00)
Withdrawal   -₹3000.00  (Balance: ₹17000.00)
========================================
Current Balance: ₹17000.00

========================================
              ATM SIMULATOR
========================================
1. Check Balance
2. Deposit Money
3. Withdraw Money
4. Mini Statement
5. Exit
========================================
Enter your choice: 5

Thank you for using the ATM.
Please collect your card.
```

---

## Learning Outcomes
1. Structured core Java applications using object-oriented principles.
2. Mastered `do-while`, `switch`, `break`, and `continue` statements for robust user interaction loops.
3. Utilized Java `ArrayList` and enhanced-for loops to track state history.
4. Learned Maven dependency management, build phases, and resource filtering across `dev` and `prod` profiles.
5. Implemented unit testing best practices using JUnit 5.
