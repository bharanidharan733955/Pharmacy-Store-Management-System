# Day 5 - Payment Processing Hierarchy & Git Merge Conflict / Rebase Workflow

Welcome to **Day 5** of the Pharmacy Store Management System daily tasks. This repository folder contains a complete Java Object-Oriented Payment Processing System along with a live Git workflow demonstration (Merge Conflicts, Resolution, and Branch Rebasing).

---

## 📐 Project Architecture & OOP Design

The system models a flexible multi-channel payment gateway for a pharmacy store using key Object-Oriented Programming (OOP) concepts:
- **Abstract Superclass**: `Payment` encapsulates common payment properties (`transactionId`, `amount`, `timestamp`, `status`, `refundedAmount`) and defines abstract contracts (`processPayment()`, `getPaymentChannelDetails()`).
- **Interface Segregation**: `Refundable` interface defines contract methods (`processRefund()`, `getMaxRefundableAmount()`, `isEligibleForRefund()`, and default method `calculateRefundFee()`).
- **Concrete Subclasses**:
  - `CardPayment`: Implements card validation, masked card display (`****-****-****-5678`), card PIN verification, surcharge fees, reward points, and card refund processing.
  - `UpiPayment`: Implements virtual payment address (VPA) validation, transaction reference generation, instant UPI refunding, and recurring Auto-Pay mandates.
  - `CashPayment`: Implements POS cash tender validation, exact change computation, and store register cash refunds.
- **Method Overloading (`pay()`)**:
  - `pay()`: Default process call with existing amount.
  - `pay(double amount)`: Custom amount payment execution.
  - `pay(double amount, String currency)`: Multi-currency payment execution.
  - `pay(double amount, int pin)`: Card security PIN authorized payment.
  - `pay(double amount, double discountPercentage)`: Discounted payment.
  - `pay(double amount, String mandateId, boolean isAutoPay)`: UPI Auto-Pay mandate subscription.
  - `pay(double amount, String promoCode, double promoDiscount)`: Promotional voucher deduction payment.

```
                  +-------------------------+
                  |    <<Interface>>        |
                  |       Refundable        |
                  +-------------------------+
                  | + processRefund()       |
                  | + getMaxRefundableAmt() |
                  | + isEligibleForRefund() |
                  | + calculateRefundFee()  |
                  +------------+------------+
                               ^
                               | implements
                               |
   +---------------------------+---------------------------+
   |                           |                           |
+--+--------------------+ +----+-------------------+ +-----+-------------------+
|     CardPayment        | |    UpiPayment          | |    CashPayment         |
+------------------------+ +------------------------+ +------------------------+
| - cardNumber           | | - upiId                | | - cashTendered         |
| - cardHolderName       | | - transactionRefNo     | | - changeGiven          |
| - cardType             | | - upiProvider          | +------------------------+
| - expiryDate           | +------------------------+ | + processPayment()     |
+------------------------+ | + processPayment()     | | + pay(amount, tendered)|
| + processPayment()     | | + pay(amount, pin)     | | + processRefund()      |
| + pay(amount, pin)     | | + pay(amount, mandate)| +------------------------+
| + processRefund()      | | + processRefund()      |
+-----------+------------+ +-----------+------------+
            |                          |
            +------------+-------------+
                         | extends
                         v
            +------------------------+
            |    <<Abstract>>        |
            |        Payment         |
            +------------------------+
            | - transactionId        |
            | - amount               |
            | - timestamp            |
            | - status               |
            | - refundedAmount       |
            +------------------------+
            | / processPayment() /   |
            | / getChannelDetails()/ |
            | + pay(...) [Overloaded]|
            | + generateReceipt()    |
            +------------------------+
```

---

## 🔀 Git Branching, Merge Conflict Resolution & Rebase Workflow

As part of the Day 5 task requirements, a pair-programming Git simulation was executed in the workspace:

### 1. Parallel Branch Creation
Two feature branches were created off `main` to simulate pair developers editing `CardPayment.java`:
- **Student 1 Branch** (`feature/student1-card-fees`): Added a 1.5% card processing fee computation inside `processPayment()`.
- **Student 2 Branch** (`feature/student2-card-rewards`): Added a 2% cashback reward points logic inside `processPayment()`.

### 2. Triggering Merge Conflict
- **Student 1** merged `feature/student1-card-fees` into `main` (Fast-forward merge).
- **Student 2** attempted to merge `feature/student2-card-rewards` into `main`:
  ```bash
  $ git merge feature/student2-card-rewards
  Auto-merging Daily Task/Day 5/src/main/java/com/payment/model/CardPayment.java
  CONFLICT (content): Merge conflict in Daily Task/Day 5/src/main/java/com/payment/model/CardPayment.java
  Automatic merge failed; fix conflicts and then commit the result.
  ```

### 3. Resolving the Merge Conflict
Git added conflict markers (`<<<<<<< HEAD`, `=======`, `>>>>>>>`):
```java
<<<<<<< HEAD
        // Student 1 Feature: Calculate 1.5% processing fee
        double convenienceFee = getAmount() * 0.015;
        double totalCharged = getAmount() + convenienceFee;
=======
        // Student 2 Feature: Calculate 2% cashback reward points earned
        int rewardPointsEarned = (int) (getAmount() * 0.02);
>>>>>>> feature/student2-card-rewards
```

**Resolution**: Both features were combined into a unified payment processing function:
```java
        // Combined Feature (Student 1 + Student 2 Conflict Resolution):
        // Student 1: Calculate 1.5% processing fee
        double convenienceFee = getAmount() * 0.015;
        double totalCharged = getAmount() + convenienceFee;
        // Student 2: Calculate 2% cashback reward points earned
        int rewardPointsEarned = (int) (getAmount() * 0.02);
```
Staged and committed:
```bash
git add "Daily Task/Day 5/src/main/java/com/payment/model/CardPayment.java"
git commit -m "fix(merge): resolve merge conflict in CardPayment.java combining fee & reward features"
```

### 4. Rebasing a Second Branch onto Main
To keep a clean linear history, a third branch (`feature/student-upi-rebase`) containing UPI Auto-Pay mandate support was rebased onto `main`:
```bash
git checkout feature/student-upi-rebase
git rebase main
git checkout main
git merge feature/student-upi-rebase
```

### 5. Final Verified Commit Log (`git log --graph --oneline`)
```text
* 529e6ed feat(student-rebase): add UPI Auto-Pay mandate support in UpiPayment
*   ccfbde2 fix(merge): resolve merge conflict in CardPayment.java combining fee & reward features
|\  
| * 2cc2d48 feat(student2): add 2% cashback reward points to processPayment()
* | ac183dc feat(student1): add 1.5% card convenience surcharge to processPayment()
|/  
* 6504519 feat(day-5): initial commit of Payment hierarchy system
```

---

## 🚀 How to Run & Execute

### Option A: Running via Maven
```bash
# Build and run unit tests
mvn clean test

# Execute Main App
mvn exec:java
```

### Option B: Running directly with Java / javac
```bash
# Compile project sources
javac -d bin -sourcepath src/main/java src/main/java/com/payment/app/PaymentApp.java

# Run Payment Application
java -cp bin com.payment.app.PaymentApp
```

---

## 🧪 Unit Tests Included

- `CardPaymentTest`: Validates card number masking, invalid card parameters, PIN authorization, convenience fee/reward points, and partial/full card refunds.
- `UpiPaymentTest`: Validates VPA format (`@` check), reference generation, discount overloading, auto-pay mandates, and instant UPI refunds.
- `CashPaymentTest`: Validates exact change calculations, insufficient cash validation, and POS cash refunds.
