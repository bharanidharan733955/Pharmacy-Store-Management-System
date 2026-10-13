# Day 8: Employee Directory — Java Maps, Comparable, Comparator & Object Contract Masterclass

Welcome to **Day 8** of the Pharmacy Store Management System daily tasks. This module implements an enterprise **Employee Directory System** demonstrating 6 fundamental Java Collections and Object Contract topics: **HashMap $O(1)$ lookup**, **frequency counting & aggregation with `Map.merge()`**, **TreeMap department reporting**, **`Comparable` natural ordering by ID**, **multi-attribute top-5 salary `Comparator` chaining with deterministic tie-breakers**, and an in-depth **missing-`hashCode` bug autopsy & fix**.

---

## 📑 Table of Contents
1. [Core Concepts Demonstrated](#-core-concepts-demonstrated)
2. [Architecture & Class Diagram](#-architecture--class-diagram)
3. [Package Structure](#-package-structure)
4. [Detailed Concept Breakdown](#-detailed-concept-breakdown)
   - [1. HashMap Lookup — O(1) Key-Value Access](#1-hashmap-lookup--o1-key-value-access)
   - [2. Frequency Counting & Aggregation with Map.merge()](#2-frequency-counting--aggregation-with-mapmerge)
   - [3. TreeMap Department Report (NavigableMap)](#3-treemap-department-report-navigablemap)
   - [4. Comparable by ID (Natural Ordering)](#4-comparable-by-id-natural-ordering)
   - [5. Top-5 Salary Comparator Chain & Bounded Heap](#5-top-5-salary-comparator-chain--bounded-heap)
   - [6. Missing-hashCode Bug Demo & Fix (Object Contract Autopsy)](#6-missing-hashcode-bug-demo--fix-object-contract-autopsy)
5. [How to Build & Run](#-how-to-build--run)
6. [Unit Test Suite (22 Tests Passed)](#-unit-test-suite-22-tests-passed)

---

## 🧩 Core Concepts Demonstrated

| # | Concept | Mechanism / API | Description & Role in System |
|---|---|---|---|
| 1 | **HashMap Lookup** | `Map<Integer, Employee>` | Instant average $O(1)$ lookup, insertion, update, and removal by employee ID. |
| 2 | **Frequency Counting** | `Map.merge(k, 1, Integer::sum)` | Java 8 atomic initialization/incrementing for department headcounts and role distributions. |
| 3 | **Sorted Reporting** | `TreeMap<String, DepartmentAnalytics>` | Alphabetically sorted Red-Black tree map supporting `firstKey()`, `lastKey()`, and `subMap()` range queries. |
| 4 | **Natural Ordering** | `Comparable<Employee>` (`compareTo`) | Natural ordering strictly by employee ID ascending; compatible with `equals()`, `TreeSet`, and `binarySearch`. |
| 5 | **Comparator Chaining** | `Comparator.comparing(...).reversed()` | 4-tier comparator chain (Salary DESC $\to$ Name ASC $\to$ Dept ASC $\to$ ID ASC) with both Stream and Bounded Min-Heap ($O(N \log K)$). |
| 6 | **Object Contract Autopsy** | `equals()` vs `hashCode()` | Live reproduction of lost data in `HashMap` and duplicate insertion in `HashSet`, followed by contract-compliant fix. |

---

## 📐 Architecture & Class Diagram

```
+---------------------------------------------------------------------------------+
|                         EmployeeDirectoryApp (CLI)                              |
|           Demonstrates all 6 core sections with formatted console output        |
+----------------------------------------+----------------------------------------+
                                         |
                                         v
+---------------------------------------------------------------------------------+
|                           EmployeeDirectoryService                              |
|  - directory: Map<Integer, Employee> (HashMap O(1) storage)                     |
|  - TOP_SALARY_COMPARATOR_CHAIN: Comparator<Employee> (4-tier tie-breaker)       |
|  + addEmployee() / findById() / updateSalary() / removeEmployee()               |
|  + countByDepartmentUsingMerge() / aggregatePayrollByDepartmentUsingMerge()     |
|  + generateDepartmentReport(): NavigableMap<String, DepartmentAnalytics>        |
|  + getEmployeesSortedByIdNatural(): List<Employee>                              |
|  + getTopEarners(limit): List<Employee>                                         |
|  + getTopEarnersUsingPriorityQueue(limit): List<Employee> (Bounded Min-Heap)    |
+-------------------+--------------------+--------------------+-------------------+
                    |                    |                    |
                    v                    v                    v
         +--------------------+  +--------------------+  +--------------------+
         |      Employee      |  |DepartmentAnalytics |  |HashCodeBugDemonstr.|
         |  (Comparable<Emp>) |  |  TreeMap Value DTO |  | Live Bug & Fix     |
         |  id: int           |  |  headcount: int    |  | Demonstrator       |
         |  name: String      |  |  payroll: double   |  +---------+----------+
         |  department: String|  |  highestEarner: Emp|            |
         |  role: String      |  +--------------------+            v
         |  salary: double    |                         +---------------------+
         |  compareTo(other)  |                         |  BuggyEmployeeKey   |
         |  equals / hashCode |                         |  (equals only)      |
         +--------------------+                         +---------------------+
                                                                   |
                                                                   v
                                                        +---------------------+
                                                        |  FixedEmployeeKey   |
                                                        |  (equals+hashCode)  |
                                                        +---------------------+
```

---

## 📁 Package Structure

```text
Day 8/
├── pom.xml                                               # Maven build configuration (Java 21, JUnit 5)
├── README.md                                             # Comprehensive documentation and lab guide
├── bin/                                                  # Compiled bytecode (.class)
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── employee/
    │               └── directory/
    │                   ├── app/
    │                   │   └── EmployeeDirectoryApp.java          # Interactive console demo application
    │                   ├── model/
    │                   │   ├── BuggyEmployeeKey.java              # Key violating equals/hashCode contract
    │                   │   ├── DepartmentAnalytics.java           # Aggregated statistics container for TreeMap
    │                   │   ├── Employee.java                      # Domain entity implementing Comparable<Employee>
    │                   │   └── FixedEmployeeKey.java              # Key properly adhering to equals/hashCode
    │                   └── service/
    │                       ├── EmployeeDirectoryService.java      # Core service (HashMap, merge, TreeMap, top-5)
    │                       └── HashCodeBugDemonstrator.java       # Bucket-level autopsy & contract verification
    └── test/
        └── java/
            └── com/
                └── employee/
                    └── directory/
                        ├── TestRunner.java                        # Standalone test runner (no Maven needed)
                        └── service/
                            └── EmployeeDirectoryTest.java         # 22 JUnit 5 tests (6 nested groups)
```

---

## 📖 Detailed Concept Breakdown

### 1. HashMap Lookup — O(1) Key-Value Access
The `EmployeeDirectoryService` utilizes a `HashMap<Integer, Employee>` as its primary registry:
```java
private final Map<Integer, Employee> directory = new HashMap<>();

public Optional<Employee> findById(int id) {
    return Optional.ofNullable(directory.get(id));
}
```
- **Time Complexity:** Average $O(1)$ for `get()`, `put()`, `containsKey()`, and `remove()`.
- **Handling Missing Keys:** Uses `Optional<Employee>` to prevent `NullPointerException` in calling code.
- **Uniqueness Guarantee:** `addEmployee(Employee)` guards against duplicate IDs by checking `containsKey(id)`.

---

### 2. Frequency Counting & Aggregation with `Map.merge()`
Prior to Java 8, counting frequencies required cumbersome, repetitive boilerplate:
```java
// ❌ The Verbose Old Way (Pre-Java 8):
if (!deptCounts.containsKey(dept)) {
    deptCounts.put(dept, 1);
} else {
    deptCounts.put(dept, deptCounts.get(dept) + 1);
}
```

With `Map.merge()`, this is accomplished in a single, thread-safe, idiomatic expression:
```java
// ✅ Modern Java Idiom:
deptCounts.merge(employee.getDepartment(), 1, Integer::sum);
```
- **How it works:**
  1. If the key does not exist, `merge()` places `1` directly as the initial value.
  2. If the key already exists, `merge()` invokes the remapping function `Integer::sum(oldVal, 1)` and stores the result.
- **Beyond Counters:** `merge()` is also utilized to aggregate payrolls in one line:
  ```java
  departmentPayroll.merge(employee.getDepartment(), employee.getSalary(), Double::sum);
  ```

---

### 3. TreeMap Department Report (NavigableMap)
`TreeMap` organizes keys in sorted order based on a Red-Black Tree, providing $O(\log N)$ insertions and lookups.
```java
NavigableMap<String, DepartmentAnalytics> report = new TreeMap<>();
for (Employee emp : directory.values()) {
    report.computeIfAbsent(emp.getDepartment(), DepartmentAnalytics::new)
          .addEmployee(emp);
}
```
- **Natural Alphabetical Sorting:** Department keys iterate in strict alphabetical order (`Finance` $\to$ `Logistics` $\to$ `Pharmacy` $\to$ `QA` $\to$ `R&D`).
- **NavigableMap Operations:**
  - `firstKey()`: Retrieves the lowest key (`Finance`).
  - `lastKey()`: Retrieves the highest key (`R&D`).
  - `subMap("Logistics", true, "R&D", true)`: Retrieves a range-filtered view without copying elements.

---

### 4. `Comparable` by ID (Natural Ordering)
`Employee` implements `Comparable<Employee>` to declare that the natural identity and order of an employee is their unique numeric `id`:
```java
public class Employee implements Comparable<Employee> {
    private final int id;
    ...
    @Override
    public int compareTo(Employee other) {
        if (other == null) return 1;
        return Integer.compare(this.id, other.id);
    }
}
```
- **Comparable Contract Adherence:**
  - **Reflexive:** `e.compareTo(e) == 0`.
  - **Antisymmetric:** `sgn(e1.compareTo(e2)) == -sgn(e2.compareTo(e1))`.
  - **Transitive:** `e1 < e2` and `e2 < e3` $\implies$ `e1 < e3`.
  - **Consistency with `equals()`:** `e1.compareTo(e2) == 0` if and only if `e1.equals(e2)`.
- **TreeSet Compatibility:** Storing employees in `new TreeSet<>(employees)` automatically sorts them by ID without requiring an external Comparator.
- **Binary Search:** Enables `Collections.binarySearch(sortedList, probe)`.

---

### 5. Top-5 Salary Comparator Chain & Bounded Heap

#### The 4-Tier Comparator Chain
When ranking employees by compensation, ties frequently occur. A chained comparator resolves ties deterministically:
```java
public static final Comparator<Employee> TOP_SALARY_COMPARATOR_CHAIN = Comparator
        .comparingDouble(Employee::getSalary).reversed() // Tier 1: Highest salary first
        .thenComparing(Employee::getName)                // Tier 2: Alphabetical name (A-Z)
        .thenComparing(Employee::getDepartment)          // Tier 3: Alphabetical dept (A-Z)
        .thenComparingInt(Employee::getId);              // Tier 4: Ascending ID (final tie-breaker)
```

#### Two Implementations Compared:
1. **Full List Sort (`Stream.sorted().limit(K)`):**
   - Complexity: $O(N \log N)$ time, $O(N)$ space.
   - Clean and concise for small to medium datasets.
2. **Bounded Min-Heap (`PriorityQueue` of size $K$):**
   - Complexity: **$O(N \log K)$ time, $O(K)$ space**.
   - Maintains only the top $K$ elements in memory. If $N = 10,000,000$ and $K = 5$, this avoids sorting millions of elements!
   - Both approaches produce identical rankings.

---

### 6. Missing-`hashCode` Bug Demo & Fix (Object Contract Autopsy)

#### The Java Object Contract (JLS & java.lang.Object)
> 1. If two objects are equal according to `equals(Object)`, calling `hashCode()` on each MUST produce the same integer result.
> 2. If two objects produce the same `hashCode()`, they are not necessarily equal (collisions allowed).

#### The Bug (`BuggyEmployeeKey`)
```java
public class BuggyEmployeeKey {
    private final int employeeId;
    private final String departmentCode;

    @Override
    public boolean equals(Object o) { ... } // ✅ Overrides equals based on fields

    // ❌ OMITS hashCode()! Inherits Object.hashCode() (System identity hash)
}
```

#### Under the Hood: Why HashMap Fails
In a `HashMap`, finding a key involves two steps:
1. **Determine Bucket Index:** `bucket = (capacity - 1) & hash(key)`.
2. **Search Linked List / Tree in Bucket:** Check `if (node.hash == hash && key.equals(node.key))`.

```
                    UNDER-THE-HOOD HASHMAP BUCKET AUTOPSY

  Key 1 (id=101, dept='PHARM')              Key 2 (id=101, dept='PHARM')
  identityHashCode: 1392838282              identityHashCode: 523429237
  Bucket Index (n=16): Bucket #15           Bucket Index (n=16): Bucket #7
               │                                         │
               ▼                                         ▼
        ┌─────────────┐                           ┌─────────────┐
        │  Bucket #15 │                           │  Bucket #7  │
        ├─────────────┤                           ├─────────────┤
        │ [Key 1: V]  │                           │    Empty    │
        └─────────────┘                           └─────────────┘
  (Stored by map.put(key1, V))             (Queried by map.get(key2))

  Result: HashMap queries Bucket #7, finds it empty, and NEVER even invokes
          key2.equals(key1)! map.get(key2) returns null! DATA IS LOST!
```

#### The Symptoms:
- **HashMap:** `map.get(key2)` returns `null` despite `key1.equals(key2) == true`.
- **HashSet:** `set.add(key1)` and `set.add(key2)` both return `true`, causing **duplicate entries in a Set** (`set.size() == 2`).

#### The Fix (`FixedEmployeeKey`)
```java
@Override
public int hashCode() {
    return Objects.hash(employeeId, departmentCode); // ✅ Deterministic hash
}
```
- Both `key1` and `key2` produce identical hash codes.
- Both hash to the same bucket (e.g., Bucket #14).
- `map.get(key2)` finds the node and validates `equals()`, returning the correct payload.
- `set.add(key2)` returns `false` and maintains `size == 1`.

---

## 🚀 How to Build & Run

### Prerequisites
- Java JDK 21+ (verified on Java 27)
- PowerShell, CMD, or Bash

### 1. Direct Execution via Java CLI (Zero external tooling required)
From the project workspace root:

```powershell
# 1. Compile Main Sources
javac -d "Daily Task\Day 8\bin" (Get-ChildItem -Path "Daily Task\Day 8\src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)

# 2. Run the Interactive CLI App
java -cp "Daily Task\Day 8\bin" com.employee.directory.app.EmployeeDirectoryApp

# 3. Compile & Run the Standalone Test Suite (22 Unit Tests)
$cp = @(
    "Daily Task\Day 8\bin",
    "C:\Users\bhara\.m2\repository\org\junit\jupiter\junit-jupiter-api\5.10.1\junit-jupiter-api-5.10.1.jar",
    "C:\Users\bhara\.m2\repository\org\opentest4j\opentest4j\1.3.0\opentest4j-1.3.0.jar"
) -join ";"
javac -cp $cp -d "Daily Task\Day 8\bin" (Get-ChildItem -Path "Daily Task\Day 8\src\test\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -cp $cp com.employee.directory.TestRunner
```

### 2. Maven Execution (If Maven CLI is available)
```bash
cd "Daily Task/Day 8"
mvn clean test
mvn exec:java
```

---

## 🧪 Unit Test Suite (22 Tests Passed)

The unit test suite in `EmployeeDirectoryTest.java` is organized into 6 `@Nested` test classes mirroring all 6 requirements:

```
================================================================================
   RUNNING DAY 8 UNIT TESTS: EMPLOYEE DIRECTORY & OBJECT CONTRACT LAB         
================================================================================

-- 1. HashMap Lookup & Directory Operations --
   [PASSED] 1a. HashMap finds existing employee by ID in O(1)
   [PASSED] 1b. HashMap returns Optional.empty for missing ID
   [PASSED] 1c. HashMap prevents duplicate ID insertion
   [PASSED] 1d. Salary update mutates record correctly
   [PASSED] 1e. Removal removes record and returns Optional of removed entity

-- 2. Frequency Count & Aggregation with Map.merge() --
   [PASSED] 2a. Department headcount frequency count matches exact count
   [PASSED] 2b. Role frequencies correctly calculated with merge
   [PASSED] 2c. Total payroll aggregation with merge calculates exact sums
   [PASSED] 2d. Merge implementation yields identical results to old imperative approach

-- 3. TreeMap Department Report (NavigableMap) --
   [PASSED] 3a. TreeMap keys are strictly sorted in alphabetical order
   [PASSED] 3b. DepartmentAnalytics calculates headcount, total, average, and highest earner
   [PASSED] 3c. NavigableMap firstKey, lastKey, and subMap behave as expected

-- 4. Comparable by ID (Natural Ordering) --
   [PASSED] 4a. Collections.sort sorts employees strictly by ID ascending
   [PASSED] 4b. TreeSet automatically orders by natural Comparable without external Comparator
   [PASSED] 4c. Binary search finds employee in naturally sorted list
   [PASSED] 4d. Comparable contract adherence: reflexive, antisymmetric, transitive, equals consistency

-- 5. Top-5 Salary Comparator Chain --
   [PASSED] 5a. Returns exact top 5 earners sorted by salary descending
   [PASSED] 5b. Tie-breaker resolves equal salaries alphabetically by name
   [PASSED] 5c. Bounded PriorityQueue Min-Heap produces identical ranking to full sort
   [PASSED] 5d. Limit edge case: requesting more elements than directory contains

-- 6. Missing-hashCode Bug Demo & Fix --
   [PASSED] 6a. Missing hashCode causes HashMap lookup to return null for equal keys
   [PASSED] 6b. Fixed hashCode restores HashMap lookup and HashSet de-duplication

--------------------------------------------------------------------------------
Test Execution Summary: 22 Passed, 0 Failed (Total: 22)
--------------------------------------------------------------------------------
```
