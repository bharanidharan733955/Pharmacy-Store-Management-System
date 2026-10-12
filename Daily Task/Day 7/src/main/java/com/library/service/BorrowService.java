package com.library.service;

import com.library.model.BorrowRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages active borrow records and demonstrates Iterator.remove()
 * for safely removing overdue items during iteration without ConcurrentModificationException.
 */
public class BorrowService {
    private final List<BorrowRecord> activeLoans = new ArrayList<>();
    private final List<BorrowRecord> overdueArchive = new ArrayList<>();

    /**
     * Registers a new borrow record.
     */
    public void addLoan(BorrowRecord record) {
        activeLoans.add(record);
    }

    /**
     * Returns all active (non-returned) loans.
     */
    public List<BorrowRecord> getActiveLoans() {
        return new ArrayList<>(activeLoans);
    }

    /**
     * Returns the archived overdue records (removed via Iterator.remove).
     */
    public List<BorrowRecord> getOverdueArchive() {
        return new ArrayList<>(overdueArchive);
    }

    /**
     * Scans all active loans and removes overdue items using Iterator.remove().
     * <p>
     * This is the safe way to remove elements from a collection during iteration.
     * Using list.remove() inside a for-each loop would cause a ConcurrentModificationException.
     * <p>
     * <b>Iterator.remove() Key Points:</b>
     * <ol>
     *   <li>Must call iterator.next() before iterator.remove().</li>
     *   <li>Can only call remove() once per next() call.</li>
     *   <li>Removes the last element returned by next().</li>
     *   <li>Prevents ConcurrentModificationException (unlike list.remove inside for-each).</li>
     * </ol>
     *
     * @param referenceDate the date against which to check overdue status
     * @return the list of overdue records that were removed
     */
    public List<BorrowRecord> purgeOverdueItems(LocalDate referenceDate) {
        List<BorrowRecord> purged = new ArrayList<>();

        // ── ITERATOR.REMOVE() DEMONSTRATION ──
        // We use an explicit Iterator instead of for-each to safely remove during iteration.
        Iterator<BorrowRecord> iterator = activeLoans.iterator();
        while (iterator.hasNext()) {
            BorrowRecord record = iterator.next();

            if (record.isOverdue(referenceDate)) {
                // Iterator.remove() safely removes the current element
                // without triggering ConcurrentModificationException
                iterator.remove();

                purged.add(record);
                overdueArchive.add(record);
            }
        }

        return purged;
    }

    /**
     * Returns the count of currently active loans.
     */
    public int getActiveLoanCount() {
        return activeLoans.size();
    }

    /**
     * Returns the count of archived overdue records.
     */
    public int getOverdueArchiveCount() {
        return overdueArchive.size();
    }

    /**
     * Clears all records.
     */
    public void clearAll() {
        activeLoans.clear();
        overdueArchive.clear();
    }
}
