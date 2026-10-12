# Day 7: Library Waitlist — Java Collections Framework Lab

Welcome to **Day 7** of the Pharmacy Store Management System daily tasks. This module implements a comprehensive **Library Waitlist System** demonstrating 6 core Java Collections concepts with a real-world library domain model.

---

## 📑 Table of Contents
1. [Collections Concepts Demonstrated](#-collections-concepts-demonstrated)
2. [Architecture & Class Diagram](#-architecture--class-diagram)
3. [Package Structure](#-package-structure)
4. [Detailed Concept Breakdown](#-detailed-concept-breakdown)
5. [Performance Benchmark Results](#-performance-benchmark-results)
6. [How to Build & Run](#-how-to-build--run)
7. [Unit Test Suite](#-unit-test-suite)

---

## 🧩 Collections Concepts Demonstrated

| # | Concept | Collection Used | Where Applied |
|---|---|---|---|
| 1 | **Ordered Catalogue** | `ArrayList` | `CatalogueService` — indexed, random-access book storage |
| 2 | **ISBN De-duplication** | `LinkedHashSet` | `CatalogueService` — insertion-order-preserving duplicate ISBN rejection |
| 3 | **FIFO Waitlist** | `ArrayDeque` | `WaitlistService` — first-come, first-served queue |
| 4 | **Faculty-First Priority** | `PriorityQueue` | `WaitlistService` — min-heap ordering by patron priority level |
| 5 | **Overdue Item Removal** | `Iterator.remove()` | `BorrowService` — safe removal during iteration without ConcurrentModificationException |
| 6 | **Performance Timing** | `ArrayList` vs `LinkedList` | `ListPerformanceBenchmark` — head/tail/middle inserts and random get(i) |

---

## 📐 Architecture & Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         LibraryApp (Main CLI)                          │
│  Orchestrates all 5 sections and prints formatted output               │
└───────────┬──────────────┬──────────────┬──────────────┬───────────────┘
            │              │              │              │
            v              v              v              v
  ┌─────────────┐  ┌──────────────┐  ┌───────────┐  ┌───────────────────┐
  │ Catalogue   │  │  Waitlist    │  │  Borrow   │  │ ListPerformance   │
  │ Service     │  │  Service     │  │  Service  │  │ Benchmark         │
  │             │  │              │  │           │  │                   │
  │ ArrayList   │  │ ArrayDeque   │  │ ArrayList │  │ ArrayList vs      │
  │ LinkedHash  │  │ PriorityQ    │  │ Iterator  │  │ LinkedList timing │
  │ Set (dedup) │  │ (faculty 1st)│  │ .remove() │  │                   │
  └──────┬──────┘  └──────┬───────┘  └─────┬─────┘  └───────────────────┘
         │                │                │
         v                v                v
  ┌────────────┐  ┌──────────────┐  ┌──────────────┐
  │   Book     │  │ WaitlistReq  │  │ BorrowRecord │
  │            │  │ (Comparable) │  │              │
  │ ISBN-based │  │ PatronType   │  │ isOverdue()  │
  │ equals/    │  │ priorityLvl  │  │ getOverdue   │
  │ hashCode   │  │ requestTime  │  │ Days()       │
  └────────────┘  └──────────────┘  └──────────────┘
```

---

## 📁 Package Structure

```text
Day 7/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── library/
    │               ├── app/
    │               │   └── LibraryApp.java                    # Interactive CLI main application
    │               ├── benchmark/
    │               │   └── ListPerformanceBenchmark.java      # ArrayList vs LinkedList timing
    │               ├── model/
    │               │   ├── Book.java                          # Book entity (ISBN equals/hashCode)
    │               │   ├── BorrowRecord.java                  # Borrow record with overdue detection
    │               │   ├── PatronType.java                    # Faculty/Scholar/Student/Guest enum
    │               │   └── WaitlistRequest.java               # Comparable request for PriorityQueue
    │               └── service/
    │                   ├── BorrowService.java                 # Iterator.remove overdue purging
    │                   ├── CatalogueService.java              # ArrayList + LinkedHashSet catalogue
    │                   └── WaitlistService.java               # ArrayDeque FIFO + PriorityQueue
    └── test/
        └── java/
            └── com/
                └── library/
                    ├── TestRunner.java                        # Standalone nested-class test runner
                    └── service/
                        └── LibraryCollectionsTest.java        # 18 JUnit 5 tests (5 nested groups)
```

---

## 📖 Detailed Concept Breakdown

### 1. ArrayList — Ordered Catalogue Storage
```java
private final List<Book> catalogue = new ArrayList<>();

// O(1) random access by index
catalogue.get(index);

// O(1) amortized append
catalogue.add(book);
```
**Why ArrayList?** Books need insertion-order traversal and fast random access by catalogue index. ArrayList's contiguous memory array provides O(1) `get(i)` and excellent CPU cache locality.

### 2. LinkedHashSet — ISBN De-Duplication
```java
private final Set<Book> isbnDedup = new LinkedHashSet<>();

boolean isNew = isbnDedup.add(book); // returns false if duplicate ISBN exists
if (isNew) {
    catalogue.add(book);
}
```
**Why LinkedHashSet?** It combines `HashSet`'s O(1) duplicate detection with `LinkedList`-backed insertion-order preservation. When iterating the dedup set, elements appear in the order they were first added. The `Book.equals()/hashCode()` are based on ISBN for correct dedup behavior.

### 3. ArrayDeque — FIFO Waitlist
```java
private final Deque<WaitlistRequest> fifoWaitlist = new ArrayDeque<>();

fifoWaitlist.offerLast(request);   // enqueue at tail
fifoWaitlist.pollFirst();          // dequeue from head (FIFO)
```
**Why ArrayDeque?** It's Java's recommended replacement for `Stack` and `Queue`. It's faster than `LinkedList` as a queue due to contiguous circular buffer layout. No capacity restrictions (grows dynamically).

### 4. PriorityQueue — Faculty-First Requests
```java
private final PriorityQueue<WaitlistRequest> priorityWaitlist = new PriorityQueue<>();

// WaitlistRequest implements Comparable:
public int compareTo(WaitlistRequest other) {
    int priorityCompare = Integer.compare(
        this.patronType.getPriorityLevel(),  // Faculty=1, Student=3, Guest=4
        other.patronType.getPriorityLevel()
    );
    if (priorityCompare != 0) return priorityCompare;
    return this.requestTime.compareTo(other.requestTime); // FIFO tie-breaking
}
```
**Why PriorityQueue?** Faculty members (priority 1) must be served before students (priority 3) regardless of arrival time. PriorityQueue is a binary min-heap that guarantees the smallest element (highest priority) is always at the head. Ties within the same priority level are broken by earliest request time (FIFO within same tier).

### 5. Iterator.remove() — Safe Overdue Removal
```java
// ✅ CORRECT: Iterator.remove() is safe during iteration
Iterator<BorrowRecord> iterator = activeLoans.iterator();
while (iterator.hasNext()) {
    BorrowRecord record = iterator.next();
    if (record.isOverdue(today)) {
        iterator.remove();  // Safe! No ConcurrentModificationException
    }
}

// ❌ WRONG: list.remove() inside for-each causes ConcurrentModificationException
for (BorrowRecord record : activeLoans) {
    if (record.isOverdue(today)) {
        activeLoans.remove(record);  // BOOM! ConcurrentModificationException
    }
}
```

---

## ⚡ Performance Benchmark Results

Benchmark run with **50,000 elements** on Java 27:

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│               ArrayList vs LinkedList — Performance Benchmark Results               │
├────────────────────────┬──────────────┬───────────────────┬─────────────────┬────────┤
│ Operation              │ List Type    │          Elements │   Duration (ns) │     ms │
├────────────────────────┼──────────────┼───────────────────┼─────────────────┼────────┤
│ Tail Append            │ ArrayList    │            50,000 │       9,079,500 │    9.1 │
│ Tail Append            │ LinkedList   │            50,000 │       7,216,900 │    7.2 │
│ Head Insert (idx 0)    │ ArrayList    │            50,000 │     142,900,500 │  142.9 │
│ Head Insert (idx 0)    │ LinkedList   │            50,000 │       2,719,400 │    2.7 │
│ Middle Insert (n/2)    │ ArrayList    │            50,000 │      57,778,400 │   57.8 │
│ Middle Insert (n/2)    │ LinkedList   │            50,000 │   1,028,042,300 │ 1028.0 │
│ Random get(i)          │ ArrayList    │            50,000 │       1,398,000 │    1.4 │
│ Random get(i)          │ LinkedList   │            50,000 │     863,036,500 │  863.0 │
└────────────────────────┴──────────────┴───────────────────┴─────────────────┴────────┘
```

### Key Observations:
| Operation | Winner | Speedup |
|---|---|---|
| Tail Append | LinkedList | ~1.3× |
| Head Insert (idx 0) | **LinkedList** | **~52×** |
| Middle Insert (n/2) | **ArrayList** | **~18×** |
| Random get(i) | **ArrayList** | **~617×** |

> **Recommendation**: Use `ArrayList` as your default `List` implementation. LinkedList only wins for heavy head insertions on very large lists.

---

## 🚀 How to Build & Run

### Option 1: Maven
```bash
mvn clean test          # Compile and run tests
mvn exec:java           # Run the interactive application
```

### Option 2: Direct javac/java
```bash
# Compile
javac -d bin -sourcepath src/main/java src/main/java/com/library/app/LibraryApp.java

# Run application
java -cp bin com.library.app.LibraryApp

# Compile and run tests
javac -d bin -cp "bin;<junit-jars>" src/test/java/com/library/service/LibraryCollectionsTest.java src/test/java/com/library/TestRunner.java
java -cp "bin;<junit-jars>" com.library.TestRunner
```

---

## 🧪 Unit Test Suite

**18 tests across 5 nested groups** — all passing:

| Group | Test | Result |
|---|---|---|
| **1. ArrayList + LinkedHashSet** | 1a. Insertion order with indexed access | ✅ PASSED |
| | 1b. LinkedHashSet rejects duplicate ISBNs | ✅ PASSED |
| | 1c. LinkedHashSet allows unique ISBNs | ✅ PASSED |
| | 1d. Bulk add skips duplicates | ✅ PASSED |
| | 1e. Search by title keyword | ✅ PASSED |
| **2. ArrayDeque FIFO** | 2a. Strict FIFO ordering | ✅ PASSED |
| | 2b. Returns null when empty | ✅ PASSED |
| | 2c. Peek does not remove | ✅ PASSED |
| **3. PriorityQueue Faculty-First** | 3a. Faculty before Student (even if later arrival) | ✅ PASSED |
| | 3b. Same priority → FIFO tie-breaking | ✅ PASSED |
| | 3c. Full chain: Faculty→Research→Student→Guest | ✅ PASSED |
| **4. Iterator.remove()** | 4a. Correctly purges overdue records | ✅ PASSED |
| | 4b. Returns empty when no overdue | ✅ PASSED |
| | 4c. Handles empty loan list | ✅ PASSED |
| | 4d. for-each + list.remove() throws CME | ✅ PASSED |
| | 4e. Iterator.remove() avoids CME | ✅ PASSED |
| **5. Benchmark** | 5a. Returns 8 results (4 ops × 2 types) | ✅ PASSED |
| | 5b. ArrayList get(i) faster than LinkedList | ✅ PASSED |
