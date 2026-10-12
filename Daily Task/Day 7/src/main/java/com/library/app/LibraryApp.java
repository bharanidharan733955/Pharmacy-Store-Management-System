package com.library.app;

import com.library.benchmark.ListPerformanceBenchmark;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.PatronType;
import com.library.model.WaitlistRequest;
import com.library.service.BorrowService;
import com.library.service.CatalogueService;
import com.library.service.WaitlistService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Day 7 — Library Waitlist System: Interactive CLI Application
 * <p>
 * Demonstrates all 6 Java Collections concepts:
 * 1. ArrayList catalogue
 * 2. LinkedHashSet de-duplication
 * 3. ArrayDeque FIFO waitlist
 * 4. PriorityQueue faculty-first waitlist
 * 5. Iterator.remove for overdue items
 * 6. ArrayList vs LinkedList performance benchmark
 */
public class LibraryApp {

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════════════════════════");
        System.out.println("        PHARMACY STORE MANAGEMENT — DAY 7: LIBRARY WAITLIST COLLECTIONS LAB    ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════\n");

        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 1: ArrayList Catalogue + LinkedHashSet De-duplication
        // ─────────────────────────────────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  SECTION 1: ArrayList Catalogue + LinkedHashSet ISBN De-Duplication         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n");

        CatalogueService catalogue = new CatalogueService();

        // Add unique books
        catalogue.addBook(new Book("978-0-134-68599-1", "Effective Java", "Joshua Bloch", "Programming", 2018, 5));
        catalogue.addBook(new Book("978-0-596-00712-6", "Head First Design Patterns", "Eric Freeman", "Programming", 2020, 3));
        catalogue.addBook(new Book("978-0-201-63361-0", "Design Patterns (GoF)", "Gang of Four", "Software Engineering", 1994, 2));
        catalogue.addBook(new Book("978-0-132-35088-4", "Clean Code", "Robert C. Martin", "Programming", 2008, 4));
        catalogue.addBook(new Book("978-0-321-12521-7", "Domain-Driven Design", "Eric Evans", "Architecture", 2003, 3));

        System.out.println("  Added 5 unique books to catalogue.");
        System.out.printf("  ArrayList catalogue size: %d%n", catalogue.size());
        System.out.printf("  LinkedHashSet dedup size: %d%n%n", catalogue.getDedupSetSize());

        // Attempt to add duplicates — LinkedHashSet rejects them
        boolean dup1 = catalogue.addBook(new Book("978-0-134-68599-1", "Effective Java (Duplicate)", "Bloch", "Prog", 2018, 10));
        boolean dup2 = catalogue.addBook(new Book("978-0-596-00712-6", "Head First (Duplicate)", "Freeman", "Prog", 2020, 8));
        boolean unique = catalogue.addBook(new Book("978-0-134-75759-9", "Java Concurrency in Practice", "Brian Goetz", "Programming", 2006, 3));

        System.out.println("  De-duplication Results (LinkedHashSet.add() returns false for duplicates):");
        System.out.printf("    ▸ ISBN 978-0-134-68599-1 (Effective Java re-add):      %s%n", dup1 ? "ACCEPTED" : "REJECTED (duplicate)");
        System.out.printf("    ▸ ISBN 978-0-596-00712-6 (Head First re-add):          %s%n", dup2 ? "ACCEPTED" : "REJECTED (duplicate)");
        System.out.printf("    ▸ ISBN 978-0-134-75759-9 (Java Concurrency — new):     %s%n%n", unique ? "ACCEPTED (unique)" : "REJECTED");

        System.out.printf("  Final catalogue size after 3 more add attempts: %d (2 duplicates rejected)%n", catalogue.size());

        System.out.println("\n  Full Catalogue (ArrayList order preserved):");
        for (int i = 0; i < catalogue.size(); i++) {
            System.out.printf("    [%d] %s%n", i, catalogue.getByIndex(i));
        }

        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 2: ArrayDeque FIFO Waitlist
        // ─────────────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  SECTION 2: ArrayDeque — FIFO (First-Come, First-Served) Waitlist           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n");

        WaitlistService waitlistService = new WaitlistService();

        // Simulate 5 patrons joining the FIFO waitlist for "Effective Java" (all copies out)
        String targetIsbn = "978-0-134-68599-1";
        String targetTitle = "Effective Java";

        waitlistService.addToFifoWaitlist(new WaitlistRequest("WR-001", "PAT-101", "Alice Johnson", PatronType.STUDENT, targetIsbn, targetTitle));
        waitlistService.addToFifoWaitlist(new WaitlistRequest("WR-002", "PAT-201", "Dr. Raghavan", PatronType.FACULTY, targetIsbn, targetTitle));
        waitlistService.addToFifoWaitlist(new WaitlistRequest("WR-003", "PAT-102", "Bob Smith", PatronType.STUDENT, targetIsbn, targetTitle));
        waitlistService.addToFifoWaitlist(new WaitlistRequest("WR-004", "PAT-301", "Meera Rao", PatronType.RESEARCH_SCHOLAR, targetIsbn, targetTitle));
        waitlistService.addToFifoWaitlist(new WaitlistRequest("WR-005", "PAT-401", "Carlos Extern", PatronType.GUEST, targetIsbn, targetTitle));

        System.out.printf("  FIFO Waitlist for '%s' — %d requests queued.%n%n", targetTitle, waitlistService.getFifoWaitlistSize());

        System.out.println("  Dequeuing in FIFO order (ArrayDeque.pollFirst):");
        int fifoOrder = 1;
        WaitlistRequest next;
        while ((next = waitlistService.pollFifoWaitlist()) != null) {
            System.out.printf("    %d. %s (%s) — served in arrival order%n",
                    fifoOrder++, next.getPatronName(), next.getPatronType().getDisplayName());
        }

        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 3: PriorityQueue Faculty-First Waitlist
        // ─────────────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  SECTION 3: PriorityQueue — Faculty-First Priority Waitlist                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n");

        // Same requests, but now using PriorityQueue — faculty should jump ahead
        LocalDateTime baseTime = LocalDateTime.of(2026, 10, 12, 9, 0);
        waitlistService.addToPriorityWaitlist(new WaitlistRequest("WR-P01", "PAT-101", "Alice Johnson", PatronType.STUDENT, targetIsbn, targetTitle, baseTime.plusMinutes(1)));
        waitlistService.addToPriorityWaitlist(new WaitlistRequest("WR-P02", "PAT-201", "Dr. Raghavan", PatronType.FACULTY, targetIsbn, targetTitle, baseTime.plusMinutes(5)));
        waitlistService.addToPriorityWaitlist(new WaitlistRequest("WR-P03", "PAT-102", "Bob Smith", PatronType.STUDENT, targetIsbn, targetTitle, baseTime.plusMinutes(2)));
        waitlistService.addToPriorityWaitlist(new WaitlistRequest("WR-P04", "PAT-301", "Meera Rao", PatronType.RESEARCH_SCHOLAR, targetIsbn, targetTitle, baseTime.plusMinutes(3)));
        waitlistService.addToPriorityWaitlist(new WaitlistRequest("WR-P05", "PAT-401", "Carlos Extern", PatronType.GUEST, targetIsbn, targetTitle, baseTime.plusMinutes(4)));
        waitlistService.addToPriorityWaitlist(new WaitlistRequest("WR-P06", "PAT-202", "Prof. Sharma", PatronType.FACULTY, targetIsbn, targetTitle, baseTime.plusMinutes(6)));

        System.out.printf("  Priority Waitlist for '%s' — %d requests queued.%n", targetTitle, waitlistService.getPriorityWaitlistSize());
        System.out.println("  Priority Ordering: FACULTY(1) > RESEARCH_SCHOLAR(2) > STUDENT(3) > GUEST(4)\n");

        System.out.println("  Dequeuing in PRIORITY order (PriorityQueue.poll):");
        List<WaitlistRequest> priorityDrained = waitlistService.drainPriorityWaitlistInOrder();
        int prioOrder = 1;
        for (WaitlistRequest req : priorityDrained) {
            System.out.printf("    %d. [Priority %d] %s (%s) — requested at %s%n",
                    prioOrder++, req.getPatronType().getPriorityLevel(),
                    req.getPatronName(), req.getPatronType().getDisplayName(), req.getRequestTime());
        }

        System.out.println("\n  ✦ Notice: Dr. Raghavan (Faculty, arrived 5th) was served BEFORE Alice (Student, arrived 1st)!");
        System.out.println("  ✦ Notice: Prof. Sharma (Faculty, arrived 6th) was served 2nd — both Faculty before any Student.");

        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 4: Iterator.remove for Overdue Items
        // ─────────────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  SECTION 4: Iterator.remove() — Safe Removal of Overdue Borrow Records     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n");

        BorrowService borrowService = new BorrowService();
        LocalDate today = LocalDate.of(2026, 10, 12);

        // Add active loans — mix of overdue and current
        borrowService.addLoan(new BorrowRecord("BR-001", "PAT-101", "Alice Johnson", PatronType.STUDENT,
                "978-0-134-68599-1", "Effective Java", today.minusDays(20), 14));    // Due 6 days ago → OVERDUE
        borrowService.addLoan(new BorrowRecord("BR-002", "PAT-201", "Dr. Raghavan", PatronType.FACULTY,
                "978-0-596-00712-6", "Head First Design Patterns", today.minusDays(5), 14)); // Due in 9 days → ACTIVE
        borrowService.addLoan(new BorrowRecord("BR-003", "PAT-102", "Bob Smith", PatronType.STUDENT,
                "978-0-201-63361-0", "Design Patterns (GoF)", today.minusDays(30), 14));     // Due 16 days ago → OVERDUE
        borrowService.addLoan(new BorrowRecord("BR-004", "PAT-301", "Meera Rao", PatronType.RESEARCH_SCHOLAR,
                "978-0-132-35088-4", "Clean Code", today.minusDays(10), 14));                // Due in 4 days → ACTIVE
        borrowService.addLoan(new BorrowRecord("BR-005", "PAT-103", "Charlie Davis", PatronType.STUDENT,
                "978-0-321-12521-7", "Domain-Driven Design", today.minusDays(25), 14));      // Due 11 days ago → OVERDUE

        System.out.printf("  Active loans before purge: %d%n", borrowService.getActiveLoanCount());
        System.out.println("  Scanning for overdue items (reference date: " + today + ")...\n");

        System.out.println("  All loans before Iterator.remove():");
        for (BorrowRecord r : borrowService.getActiveLoans()) {
            String status = r.isOverdue(today)
                    ? String.format("⚠️  OVERDUE by %d days", r.getOverdueDays(today))
                    : "✅ ACTIVE";
            System.out.printf("    ▸ %s — %s%n", r, status);
        }

        // Execute the purge using Iterator.remove()
        System.out.println("\n  Executing purgeOverdueItems() using Iterator.remove()...");
        List<BorrowRecord> purged = borrowService.purgeOverdueItems(today);

        System.out.printf("\n  Purged %d overdue records using Iterator.remove():%n", purged.size());
        for (BorrowRecord r : purged) {
            System.out.printf("    ✘ REMOVED: %s (overdue by %d days)%n", r.getRecordId() + " — " + r.getBookTitle(), r.getOverdueDays(today));
        }

        System.out.printf("\n  Active loans after purge:  %d%n", borrowService.getActiveLoanCount());
        System.out.printf("  Overdue archive count:     %d%n", borrowService.getOverdueArchiveCount());

        System.out.println("\n  Remaining active loans:");
        for (BorrowRecord r : borrowService.getActiveLoans()) {
            System.out.printf("    ✔ %s%n", r);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 5: ArrayList vs LinkedList Performance Benchmark
        // ─────────────────────────────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  SECTION 5: ArrayList vs LinkedList — Performance Benchmark                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n");

        ListPerformanceBenchmark benchmark = new ListPerformanceBenchmark();
        int benchmarkSize = 50_000;

        System.out.printf("  Running benchmark with %,d elements...%n%n", benchmarkSize);

        List<ListPerformanceBenchmark.BenchmarkResult> results = benchmark.runFullBenchmark(benchmarkSize);

        ListPerformanceBenchmark.printReport(results);
        ListPerformanceBenchmark.printAnalysis(results);

        // ─────────────────────────────────────────────────────────────────────────
        System.out.println("\n════════════════════════════════════════════════════════════════════════════════");
        System.out.println("        DAY 7 COMPLETE — ALL 6 COLLECTIONS CONCEPTS DEMONSTRATED               ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════");
    }
}
