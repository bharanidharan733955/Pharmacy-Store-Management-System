package com.library.service;

import com.library.benchmark.ListPerformanceBenchmark;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.PatronType;
import com.library.model.WaitlistRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Day 7 — Library Waitlist Collections Lab Tests")
public class LibraryCollectionsTest {

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST GROUP 1: ArrayList Catalogue + LinkedHashSet De-duplication
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("1. ArrayList Catalogue & LinkedHashSet De-Dup")
    class CatalogueTests {

        private CatalogueService catalogue;

        @BeforeEach
        void setUp() {
            catalogue = new CatalogueService();
            catalogue.addBook(new Book("ISBN-001", "Java Basics", "Author A", "CS", 2020, 5));
            catalogue.addBook(new Book("ISBN-002", "Data Structures", "Author B", "CS", 2019, 3));
            catalogue.addBook(new Book("ISBN-003", "Algorithms", "Author C", "CS", 2021, 4));
        }

        @Test
        @DisplayName("1a. ArrayList stores books in insertion order with indexed access")
        void testArrayListInsertionOrderAndIndexedAccess() {
            assertEquals(3, catalogue.size());
            assertEquals("Java Basics", catalogue.getByIndex(0).getTitle());
            assertEquals("Data Structures", catalogue.getByIndex(1).getTitle());
            assertEquals("Algorithms", catalogue.getByIndex(2).getTitle());
        }

        @Test
        @DisplayName("1b. LinkedHashSet rejects duplicate ISBNs")
        void testLinkedHashSetDeduplication() {
            // Attempt to add a duplicate ISBN
            boolean result = catalogue.addBook(new Book("ISBN-001", "Java Basics DUPLICATE", "Other", "CS", 2022, 10));

            assertFalse(result, "LinkedHashSet.add() should return false for duplicate ISBN");
            assertEquals(3, catalogue.size(), "Catalogue size must remain unchanged");
            assertEquals(3, catalogue.getDedupSetSize(), "Dedup set size must remain unchanged");
        }

        @Test
        @DisplayName("1c. LinkedHashSet allows unique ISBNs through")
        void testLinkedHashSetAcceptsUnique() {
            boolean result = catalogue.addBook(new Book("ISBN-004", "Machine Learning", "Author D", "AI", 2023, 2));

            assertTrue(result, "Unique ISBN should be accepted");
            assertEquals(4, catalogue.size());
            assertEquals(4, catalogue.getDedupSetSize());
        }

        @Test
        @DisplayName("1d. Bulk add skips duplicates and counts unique additions")
        void testBulkAddWithDuplicates() {
            List<Book> booksWithDuplicates = List.of(
                    new Book("ISBN-001", "Dup 1", "A", "CS", 2020, 1), // duplicate
                    new Book("ISBN-004", "New Book 1", "D", "CS", 2022, 2), // new
                    new Book("ISBN-002", "Dup 2", "B", "CS", 2019, 1), // duplicate
                    new Book("ISBN-005", "New Book 2", "E", "CS", 2023, 3)  // new
            );

            int added = catalogue.addBooks(booksWithDuplicates);
            assertEquals(2, added, "Only 2 unique books should be added");
            assertEquals(5, catalogue.size());
        }

        @Test
        @DisplayName("1e. Search by title keyword works on ArrayList")
        void testSearchByTitle() {
            List<Book> results = catalogue.searchByTitle("java");
            assertEquals(1, results.size());
            assertEquals("ISBN-001", results.get(0).getIsbn());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST GROUP 2: ArrayDeque FIFO Waitlist
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("2. ArrayDeque FIFO Waitlist")
    class ArrayDequeTests {

        private WaitlistService waitlistService;

        @BeforeEach
        void setUp() {
            waitlistService = new WaitlistService();
        }

        @Test
        @DisplayName("2a. ArrayDeque serves requests in strict FIFO order")
        void testFifoOrdering() {
            waitlistService.addToFifoWaitlist(new WaitlistRequest("W1", "P1", "First", PatronType.STUDENT, "ISBN-X", "Book X"));
            waitlistService.addToFifoWaitlist(new WaitlistRequest("W2", "P2", "Second", PatronType.FACULTY, "ISBN-X", "Book X"));
            waitlistService.addToFifoWaitlist(new WaitlistRequest("W3", "P3", "Third", PatronType.GUEST, "ISBN-X", "Book X"));

            assertEquals("First", waitlistService.pollFifoWaitlist().getPatronName());
            assertEquals("Second", waitlistService.pollFifoWaitlist().getPatronName());
            assertEquals("Third", waitlistService.pollFifoWaitlist().getPatronName());
        }

        @Test
        @DisplayName("2b. ArrayDeque returns null when empty")
        void testFifoEmptyPoll() {
            assertNull(waitlistService.pollFifoWaitlist(), "Polling empty ArrayDeque must return null");
        }

        @Test
        @DisplayName("2c. Peek does not remove from ArrayDeque")
        void testFifoPeekDoesNotRemove() {
            waitlistService.addToFifoWaitlist(new WaitlistRequest("W1", "P1", "Alice", PatronType.STUDENT, "ISBN-X", "Book X"));

            assertEquals("Alice", waitlistService.peekFifoWaitlist().getPatronName());
            assertEquals(1, waitlistService.getFifoWaitlistSize(), "Peek must not reduce queue size");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST GROUP 3: PriorityQueue Faculty-First Waitlist
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("3. PriorityQueue Faculty-First Waitlist")
    class PriorityQueueTests {

        private WaitlistService waitlistService;

        @BeforeEach
        void setUp() {
            waitlistService = new WaitlistService();
        }

        @Test
        @DisplayName("3a. Faculty is served before Student even when Student arrived first")
        void testFacultyFirstPriority() {
            LocalDateTime base = LocalDateTime.of(2026, 10, 12, 9, 0);

            // Student arrives FIRST at 9:01
            waitlistService.addToPriorityWaitlist(
                    new WaitlistRequest("W1", "P1", "Alice Student", PatronType.STUDENT, "ISBN-X", "Book X", base.plusMinutes(1)));
            // Faculty arrives SECOND at 9:05
            waitlistService.addToPriorityWaitlist(
                    new WaitlistRequest("W2", "P2", "Dr. Faculty", PatronType.FACULTY, "ISBN-X", "Book X", base.plusMinutes(5)));

            WaitlistRequest served = waitlistService.pollPriorityWaitlist();
            assertEquals("Dr. Faculty", served.getPatronName(), "Faculty must be served first regardless of arrival time");
            assertEquals(PatronType.FACULTY, served.getPatronType());
        }

        @Test
        @DisplayName("3b. Within same priority level, earlier request wins (FIFO tie-breaking)")
        void testSamePriorityFifoTieBreaking() {
            LocalDateTime base = LocalDateTime.of(2026, 10, 12, 9, 0);

            waitlistService.addToPriorityWaitlist(
                    new WaitlistRequest("W1", "P1", "Student B (later)", PatronType.STUDENT, "ISBN-X", "Book X", base.plusMinutes(10)));
            waitlistService.addToPriorityWaitlist(
                    new WaitlistRequest("W2", "P2", "Student A (earlier)", PatronType.STUDENT, "ISBN-X", "Book X", base.plusMinutes(2)));

            WaitlistRequest first = waitlistService.pollPriorityWaitlist();
            assertEquals("Student A (earlier)", first.getPatronName(), "Earlier request should win within same priority");
        }

        @Test
        @DisplayName("3c. Full priority chain: Faculty → Research → Student → Guest")
        void testFullPriorityChain() {
            LocalDateTime base = LocalDateTime.of(2026, 10, 12, 9, 0);

            waitlistService.addToPriorityWaitlist(new WaitlistRequest("W1", "P1", "Guest", PatronType.GUEST, "ISBN-X", "Book X", base));
            waitlistService.addToPriorityWaitlist(new WaitlistRequest("W2", "P2", "Student", PatronType.STUDENT, "ISBN-X", "Book X", base));
            waitlistService.addToPriorityWaitlist(new WaitlistRequest("W3", "P3", "Faculty", PatronType.FACULTY, "ISBN-X", "Book X", base));
            waitlistService.addToPriorityWaitlist(new WaitlistRequest("W4", "P4", "Scholar", PatronType.RESEARCH_SCHOLAR, "ISBN-X", "Book X", base));

            List<WaitlistRequest> drained = waitlistService.drainPriorityWaitlistInOrder();
            assertEquals("Faculty", drained.get(0).getPatronName());
            assertEquals("Scholar", drained.get(1).getPatronName());
            assertEquals("Student", drained.get(2).getPatronName());
            assertEquals("Guest", drained.get(3).getPatronName());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST GROUP 4: Iterator.remove for Overdue Items
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("4. Iterator.remove() — Overdue Item Purging")
    class IteratorRemoveTests {

        private BorrowService borrowService;
        private final LocalDate today = LocalDate.of(2026, 10, 12);

        @BeforeEach
        void setUp() {
            borrowService = new BorrowService();
        }

        @Test
        @DisplayName("4a. Iterator.remove correctly purges overdue records")
        void testPurgeOverdueItems() {
            // Overdue loan: borrowed 20 days ago with 14-day loan period
            borrowService.addLoan(new BorrowRecord("BR-1", "P1", "Alice", PatronType.STUDENT,
                    "ISBN-1", "Book 1", today.minusDays(20), 14));
            // Active loan: borrowed 5 days ago with 14-day loan period
            borrowService.addLoan(new BorrowRecord("BR-2", "P2", "Bob", PatronType.STUDENT,
                    "ISBN-2", "Book 2", today.minusDays(5), 14));
            // Overdue loan: borrowed 30 days ago
            borrowService.addLoan(new BorrowRecord("BR-3", "P3", "Charlie", PatronType.STUDENT,
                    "ISBN-3", "Book 3", today.minusDays(30), 14));

            assertEquals(3, borrowService.getActiveLoanCount());

            List<BorrowRecord> purged = borrowService.purgeOverdueItems(today);

            assertEquals(2, purged.size(), "Two overdue records should be purged");
            assertEquals(1, borrowService.getActiveLoanCount(), "Only 1 active loan should remain");
            assertEquals(2, borrowService.getOverdueArchiveCount());
        }

        @Test
        @DisplayName("4b. Purge returns empty list when no loans are overdue")
        void testPurgeNoOverdueItems() {
            borrowService.addLoan(new BorrowRecord("BR-1", "P1", "Alice", PatronType.STUDENT,
                    "ISBN-1", "Book 1", today.minusDays(5), 14));

            List<BorrowRecord> purged = borrowService.purgeOverdueItems(today);

            assertEquals(0, purged.size(), "No records should be purged");
            assertEquals(1, borrowService.getActiveLoanCount());
        }

        @Test
        @DisplayName("4c. Purge safely handles empty loan list")
        void testPurgeEmptyList() {
            List<BorrowRecord> purged = borrowService.purgeOverdueItems(today);
            assertEquals(0, purged.size());
            assertEquals(0, borrowService.getActiveLoanCount());
        }

        @Test
        @DisplayName("4d. Using list.remove() in for-each causes ConcurrentModificationException")
        void testForEachRemoveThrowsCME() {
            List<String> items = new ArrayList<>(List.of("a", "b", "c", "d"));

            assertThrows(ConcurrentModificationException.class, () -> {
                for (String item : items) {
                    if (item.equals("b")) {
                        items.remove(item); // WRONG! Triggers CME
                    }
                }
            }, "for-each + list.remove() must throw ConcurrentModificationException");
        }

        @Test
        @DisplayName("4e. Using Iterator.remove() avoids ConcurrentModificationException")
        void testIteratorRemoveIsSafe() {
            List<String> items = new ArrayList<>(List.of("a", "b", "c", "d"));

            // This must NOT throw
            assertDoesNotThrow(() -> {
                Iterator<String> it = items.iterator();
                while (it.hasNext()) {
                    String item = it.next();
                    if (item.equals("b")) {
                        it.remove(); // CORRECT! Safe removal
                    }
                }
            });

            assertEquals(3, items.size());
            assertFalse(items.contains("b"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEST GROUP 5: ArrayList vs LinkedList Benchmark
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("5. ArrayList vs LinkedList Benchmark")
    class BenchmarkTests {

        @Test
        @DisplayName("5a. Benchmark runs without errors and returns 8 results (4 ops × 2 list types)")
        void testBenchmarkRuns() {
            ListPerformanceBenchmark benchmark = new ListPerformanceBenchmark();
            List<ListPerformanceBenchmark.BenchmarkResult> results = benchmark.runFullBenchmark(5_000);

            assertEquals(8, results.size(), "Must return 8 results: 4 operations × 2 list types");
        }

        @Test
        @DisplayName("5b. ArrayList random get(i) is faster than LinkedList for large N")
        void testArrayListGetFasterThanLinkedList() {
            ListPerformanceBenchmark benchmark = new ListPerformanceBenchmark();
            List<ListPerformanceBenchmark.BenchmarkResult> results = benchmark.runFullBenchmark(10_000);

            // Random get(i) results are at index 6 (ArrayList) and 7 (LinkedList)
            ListPerformanceBenchmark.BenchmarkResult arrayListGet = results.get(6);
            ListPerformanceBenchmark.BenchmarkResult linkedListGet = results.get(7);

            assertTrue(arrayListGet.durationNanos() < linkedListGet.durationNanos(),
                    String.format("ArrayList get(i) [%d ns] should be faster than LinkedList get(i) [%d ns]",
                            arrayListGet.durationNanos(), linkedListGet.durationNanos()));
        }
    }
}
