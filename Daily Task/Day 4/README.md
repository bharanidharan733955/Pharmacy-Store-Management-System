# Day 4: Bank Account System & Hot Code Replace Debugging Lab

## Overview
The **Bank Account Management System** is a core Object-Oriented Java application built with **Java 21** and **Apache Maven**. It demonstrates essential Object-Oriented Programming (OOP) concepts including encapsulation, class design with private fields, a static account counter, 3-level constructor chaining (`this(...)`), input validation for money deposit and withdrawal operations, and custom overriding of `equals()` and `hashCode()`.

Additionally, this project includes a **Debug Lab** designed to practice interactive Java debugging techniques using **Conditional Breakpoints**, **Watch Expressions**, and **Hot Code Replace (HCR)**.

---

## Objectives & Core Requirements

1. **Encapsulation & Private Fields**: Hide internal state (`accountNumber`, `accountHolderName`, `balance`, `accountType`).
2. **Static Counter**: Track total `BankAccount` instances created across the JVM lifecycle (`totalAccounts`).
3. **Constructor Chaining (3 Constructors)**:
   - **Constructor 1 (`String name`)**: Delegates to Constructor 2 with default initial balance `₹1000.00`.
   - **Constructor 2 (`String name, double initialBalance`)**: Delegates to Constructor 3 with default account type `"SAVINGS"`.
   - **Constructor 3 (`String name, double initialBalance, String accountType`)**: Primary constructor that performs validation, increments `totalAccounts`, and generates a unique auto-incremented account number (e.g., `ACC-0001`).
4. **Validated Operations**:
   - `deposit(double amount)`: Validates positive amounts before modifying balance.
   - `withdraw(double amount)`: Validates positive amounts and sufficient funds.
5. **Equals & HashCode Contract**: Overridden `equals(Object obj)` and `hashCode()` based on unique `accountNumber`.
6. **Package Modularization**: Structured cleanly across `model`, `service`, and `app` packages.
7. **Debug Lab**: Identify, isolate, and fix a planted bug in `withdraw()` using IDE debugging tools (Conditional Breakpoint, Watch, Hot Code Replace).

---

## Package Architecture

```text
Day 4/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── bank/
    │               ├── model/
    │               │   └── BankAccount.java           # Encapsulated model, constructor chaining, equals/hashCode
    │               ├── service/
    │               │   ├── BankAccountService.java    # Business logic, transfer atomic logic, lookup
    │               │   ├── AccountNotFoundException.java
    │               │   └── InsufficientBalanceException.java
    │               └── app/
    │                   └── BankAccountApp.java        # Main entry point & interactive UI
    └── test/
        └── java/
            └── com/
                └── bank/
                    ├── model/
                    │   └── BankAccountTest.java      # JUnit 5 tests for model logic
                    └── service/
                        └── BankAccountServiceTest.java # JUnit 5 tests for service logic
```

---

## Key OOP Concepts Explained

### 1. Constructor Chaining (`this(...)`)
Constructor chaining reduces code duplication by allowing one constructor to invoke another within the same class using `this(...)`.

```java
// Constructor 1 -> Calls Constructor 2
public BankAccount(String accountHolderName) {
    this(accountHolderName, 1000.00);
}

// Constructor 2 -> Calls Constructor 3
public BankAccount(String accountHolderName, double initialBalance) {
    this(accountHolderName, initialBalance, "SAVINGS");
}

// Constructor 3 -> Master Constructor
public BankAccount(String accountHolderName, double initialBalance, String accountType) {
    // Parameter validation, balance initialization, and static counter increment
}
```

### 2. Static Field & Counter
The `totalAccounts` field is declared as `private static int totalAccounts = 0;`. Because it is static, a single memory copy is shared across all `BankAccount` objects, enabling auto-generation of unique account IDs (`ACC-0001`, `ACC-0002`).

### 3. `equals()` and `hashCode()` Overriding
Java's contract requires that if `equals()` returns `true` for two objects, their `hashCode()` values must be identical:
- `equals()` verifies equality based on unique `accountNumber`.
- `hashCode()` hashes `accountNumber` via `Objects.hash(accountNumber)`.

---

## Debugging Lab: Planted Bug & Hot Code Replace (HCR)

### The Planted Bug
In `BankAccount.java`, the `withdrawWithPlantedBug(double amount)` method (or uncorrected `withdraw()`) erroneously adds funds instead of deducting them:

```java
// PLANTED BUG: Addition instead of subtraction!
this.balance += amount; 
```

### Step-by-Step Debugging Walkthrough

#### Step 1: Set a Conditional Breakpoint
1. Open [`BankAccount.java`](file:///c:/Users/bhara/OneDrive/Desktop/Pharmacy%20Store%20Management%20System/Daily%20Task/Day%204/src/main/java/com/bank/model/BankAccount.java).
2. Right-click the line number margin inside `withdraw()` / `withdrawWithPlantedBug()`.
3. Select **Add Conditional Breakpoint...**.
4. Enter the expression condition:
   ```java
   amount > 500.00
   ```
5. The execution will pause *only* when a withdrawal request exceeding ₹500 is made.

#### Step 2: Add a Watch Expression
1. Open your IDE's **Watch** panel (Variables/Watch window).
2. Add the following watch expressions:
   - `this.balance`
   - `amount`
   - `this.balance - amount`
3. Observe `this.balance` in real-time as execution reaches the breakpoint. Notice how the calculation `this.balance += amount` causes balance to increase unexpectedly.

#### Step 3: Perform Hot Code Replace (HCR)
1. While the application is **paused** at the breakpoint:
2. Edit the buggy line in `BankAccount.java`:
   ```diff
   - this.balance += amount;
   + this.balance -= amount;
   ```
3. Save the file (`Ctrl + S` / `Cmd + S`).
4. The Java Debugger hot-swaps the compiled bytecode into the running JVM without restarting the application!
5. Step Over (`F10`) or Resume (`F5`) to verify that the balance is now updated correctly.

---

## Running the Application & Tests

### Build & Run Tests with Maven
Run unit tests across model and service packages:
```bash
cd "c:\Users\bhara\OneDrive\Desktop\Pharmacy Store Management System\Daily Task\Day 4"
mvn clean test
```

### Execute Main Application
Run the interactive console application:
```bash
mvn exec:java
```
