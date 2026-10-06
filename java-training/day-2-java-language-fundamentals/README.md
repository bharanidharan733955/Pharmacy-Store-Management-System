# Day 2: Java Language Fundamentals + Git Fundamentals

## 🎯 Project Overview & Objective
Day 2 focuses on mastering core **Java Language Fundamentals** (primitive data types, arrays, constants, operators, type casting, overflow prevention, and ternary operations) and **Git Version Control Fundamentals** (working directory, staging area, commit history, branch/tag tracking, and GitHub SSH authentication).

The practical project is a console application called **Monthly Usage Analyser**, which analyzes 12 months of usage data across multiple pharmacy store branches/houses.

---

## 🛠️ Technologies
- **Language**: Java JDK 21 (or JDK 27)
- **Tooling**: Terminal CLI (`javac`, `java`, `git`, `ssh`)
- **IDE Independence**: Zero external frameworks, Maven, Gradle, or IDE dependencies.

---

## 📖 Key Java Concepts Demonstrated

### 1. Primitive Data Types & Memory Ranges
Java provides 8 primitive data types with fixed memory sizes:

| Primitive | Type | Size | Range / Precision |
| :--- | :--- | :--- | :--- |
| `byte` | Signed Integer | 8 bits | $-128$ to $127$ |
| `short` | Signed Integer | 16 bits | $-32,768$ to $32,767$ |
| `int` | Signed Integer | 32 bits | $-2,147,483,648$ to $2,147,483,647$ |
| `long` | Signed Integer | 64 bits | $-9,223,372,036,854,775,808$ to $9,223,372,036,854,775,807$ |
| `float` | Floating Point | 32 bits | IEEE 754 ($\approx 6\text{--}7$ decimal digits) |
| `double` | Floating Point | 64 bits | IEEE 754 ($\approx 15\text{--}17$ decimal digits) |
| `char` | Unicode Character | 16 bits | `\u0000` ($0$) to `\uffff` ($65,535$) |
| `boolean` | Logical Value | 1 bit | `true` or `false` |

---

### 2. Elimination of Magic Numbers via `Constants.java`
All business rule thresholds (slabs, month names, branch names, grade characters) are defined as `public static final` constants in `Constants.java`:

```java
public final class Constants {
    private Constants() {}

    public static final int MONTHS_IN_YEAR = 12;
    public static final int HIGH_USAGE_SLAB = 800;
    public static final int MEDIUM_USAGE_SLAB = 500;
    public static final int LOW_USAGE_SLAB = 300;
}
```

---

### 3. Operators & Precedence
The application utilizes:
- **Arithmetic**: `+`, `-`, `*`, `/`, `%`
- **Relational**: `>`, `<`, `>=`, `<=`, `==`, `!=`
- **Logical**: `&&` (AND), `||` (OR), `!` (NOT)
- **Ternary Operator (`? :`)**: Compact conditional expression:
  ```java
  char annualGrade = (accurateAverage >= Constants.HIGH_USAGE_SLAB) ? Constants.GRADE_A :
                     (accurateAverage >= Constants.MEDIUM_USAGE_SLAB) ? Constants.GRADE_B :
                     (accurateAverage >= Constants.LOW_USAGE_SLAB) ? Constants.GRADE_C : Constants.GRADE_D;
  ```

---

### 4. Type Casting: Widening vs Narrowing

```
Widening Cast (Implicit - Automatic):
byte ---> short ---> int ---> long ---> float ---> double

Narrowing Cast (Explicit - Requires (target_type)):
double ---> float ---> long ---> int ---> short ---> byte
```

- **Widening Conversion (Implicit)**: Automatically converts a smaller data type to a larger data type without loss of data.
  Example: `long totalUsage = 0; totalUsage += monthlyUsage[i];` (converts `int` to `long`).
- **Narrowing Conversion (Explicit)**: Explicitly casts a larger data type to a smaller data type; can result in truncation or data loss.
  Example: `int integerAverage = (int) totalUsage / 12;` (truncates decimal places).

---

### 5. Floating-Point Precision vs Integer Division
- **Integer Division**: `totalUsage / 12` drops the fractional part (e.g., $8150 / 12 = 679$).
- **Floating-Point Division**: `(double) totalUsage / 12` preserves double precision ($8150.0 / 12 = 679.17$).

---

### 6. Integer Overflow & Prevention using `long`
When an `int` variable exceeds its maximum value ($2,147,483,647$), it silently wraps around to negative numbers due to 32-bit two's complement representation:

```java
int maxInt = Integer.MAX_VALUE; // 2147483647
int overflowed = maxInt + 100;   // Result: -2147483549 ❌ (Overflow error!)

long safeTotal = (long) maxInt + 100; // Result: 2147483747 L ✅ (Fixed with 64-bit long)
```

---

### 7. 1-D vs 2-D Arrays
- **1-D Array**: Single dimension array storing 12 months of sales usage units:
  ```java
  int[] monthlyUsage = {450, 620, 890, 310, 750, 920, 580, 640, 710, 830, 490, 960};
  ```
- **2-D Array**: Matrix storing 12 months of data across 3 distinct pharmacy branches/houses:
  ```java
  int[][] houseUsage = {
      {450, 620, 890, 310, 750, 920, 580, 640, 710, 830, 490, 960}, // House Alpha
      {300, 410, 550, 280, 600, 710, 490, 520, 610, 680, 420, 790}, // House Beta
      {520, 700, 950, 400, 810, 990, 630, 710, 790, 890, 560, 1020} // House Gamma
  };
  ```

---

## ⚡ Program Compilation & Execution Instructions

Navigate to the `day-2-java-language-fundamentals` folder:

```bash
cd day-2-java-language-fundamentals
```

### Step 1: Compile Source Files via Terminal
```bash
javac src/Constants.java src/MonthlyUsageAnalyser.java
```

### Step 2: Execute Program
```bash
java -cp src MonthlyUsageAnalyser
```

---

## 🖥️ Expected Sample Terminal Output

```text
==================================================================
        MONTHLY USAGE ANALYSER - JAVA LANGUAGE FUNDAMENTALS       
==================================================================

--- 1. Primitive Data Types Overview ---
byte    : 127 (Range: -128 to 127)
short   : 32767 (Range: -32768 to 32767)
int     : 2147483647 (Range: -2147483648 to 2147483647)
long    : 9223372036854775807
float   : 3.14159
double  : 3.141592653589793
char    : A
boolean : true

--- 2. 1-D Monthly Usage Summary ---
Total Usage (12 Months): 8150 units
Integer Division Avg   : 679 units (Loss of precision!)
Casted Double Average  : 679.17 units (Accurate)
Peak Month             : Dec (960 units)
Lowest Month           : Apr (310 units)
Annual Performance Grade: B

--- 3. Integer Overflow Demonstration ---
Integer.MAX_VALUE      : 2147483647
Overflowed (maxInt + 100): -2147483549 ❌ (WRONG due to 32-bit wraparound!)
Fixed with long cast   : 2147483747 ✅ (CORRECT!)

--- 4. Type Casting Demonstration ---
Original double value  : 987.65432
Narrowed to int        : 987 (Decimal truncated)
Narrowed (byte) 300    : 44 (Byte overflow wraparound)

--- 5. 2-D Multi-House Usage Analysis ---
House / Branch                 | Total Units  | Monthly Avg  | Grade 
-------------------------------------------------------------------
House Alpha (Central Branch)   | 8150         | 679.17       | B     
House Beta  (North Branch)     | 6360         | 530.00       | B     
House Gamma (South Branch)     | 8970         | 747.50       | B     
==================================================================
```

---

## 🐙 Git Fundamentals & Workflow

### 1. The 4 Git Lifecycle Stages

```
 +------------------+           +------------------+           +------------------+           +------------------+
 | Working Directory| --add---> |   Staging Area   | -commit-> | Local Repository | --push--> | GitHub Remote    |
 | (Unstage edits)  |           | (Index buffer)   |           | (.git database)  |           | (Remote origin)  |
 +------------------+           +------------------+           +------------------+           +------------------+
```

### 2. Core Git CLI Commands

| Command | Description |
| :--- | :--- |
| `git init` | Initializes a new local Git repository in the current folder. |
| `git status` | Displays modified, untracked, and staged files. |
| `git add <file>` | Moves changed files from Working Directory to Staging Area. |
| `git commit -m "msg"` | Saves staged snapshot into the local repository with a descriptive commit message. |
| `git push origin <branch>` | Uploads local repository commits to remote repository (GitHub). |
| `git pull origin <branch>` | Fetches and merges updates from remote repository to local workspace. |
| `git clone <url>` | Downloads an existing remote repository onto the local computer. |
| `git log --oneline` | Displays formatted commit history log. |

---

## 🔑 GitHub SSH Authentication Setup Guide

Using SSH keys provides a secure, passwordless authentication method for connecting to GitHub.

### Step 1: Generate a New SSH Key Pair
Run the following command in PowerShell / Terminal (replace with your GitHub email):

```bash
ssh-keygen -t ed25519 -C "your-email@example.com"
```
*Note:* Press `Enter` to accept the default file location (`~/.ssh/id_ed25519`).

### Step 2: Copy the Public Key
Display and copy your **public key** content:

```bash
cat ~/.ssh/id_ed25519.pub
```

> ⚠️ **CRITICAL SECURITY WARNING**:
> - **Public Key (`id_ed25519.pub`)**: Safe to share and upload to GitHub Settings -> SSH Keys.
> - **Private Key (`id_ed25519`)**: MUST NEVER BE SHARED OR COMMITTED TO GIT! Ensure `~/.ssh/` or `.pem`/`.key` files are listed in `.gitignore`.

### Step 3: Add Public Key to GitHub
1. Go to **GitHub -> Settings -> SSH and GPG keys**.
2. Click **New SSH Key**.
3. Paste the contents of `id_ed25519.pub` and click **Add SSH Key**.

### Step 4: Test SSH Connection
Verify authentication with GitHub:

```bash
ssh -T git@github.com
```
*Expected Output:*
```text
Hi username! You've successfully authenticated, but GitHub does not provide shell access.
```

---

## ✅ Day 2 Learning Outcomes
1. Understood primitive data types, sizes, memory allocations, and boundary limits in Java.
2. Mastered 1-D and 2-D array iteration, summation, minimum/maximum search, and matrix formatting.
3. Implemented clean code rules by replacing magic numbers with `final` constants in `Constants.java`.
4. Gained practical experience with implicit widening, explicit narrowing type casts, and floating-point division precision.
5. Learned how to detect and fix 32-bit integer overflow using 64-bit `long` primitive variables.
6. Applied logical and ternary operators (`? :`) for clean decision-making and grading logic.
7. Mastered Git lifecycle architecture, CLI commands, `.gitignore` rules, and GitHub SSH key security.
