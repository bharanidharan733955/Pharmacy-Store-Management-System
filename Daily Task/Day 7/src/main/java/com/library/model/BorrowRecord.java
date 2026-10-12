package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a borrowing record for a patron who has checked out a book.
 * Used by Iterator.remove to purge overdue items from the active loans list.
 */
public class BorrowRecord {
    private final String recordId;
    private final String patronId;
    private final String patronName;
    private final PatronType patronType;
    private final String bookIsbn;
    private final String bookTitle;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private boolean returned;
    private LocalDate returnDate;

    public BorrowRecord(String recordId, String patronId, String patronName, PatronType patronType,
                        String bookIsbn, String bookTitle, LocalDate borrowDate, int loanDays) {
        this.recordId = recordId;
        this.patronId = patronId;
        this.patronName = patronName;
        this.patronType = patronType;
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(loanDays);
        this.returned = false;
        this.returnDate = null;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getPatronName() {
        return patronName;
    }

    public PatronType getPatronType() {
        return patronType;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void markReturned() {
        this.returned = true;
        this.returnDate = LocalDate.now();
    }

    /**
     * Checks if this borrow record is overdue as of the given reference date.
     */
    public boolean isOverdue(LocalDate referenceDate) {
        return !returned && referenceDate.isAfter(dueDate);
    }

    /**
     * Calculates the number of overdue days relative to a reference date.
     */
    public long getOverdueDays(LocalDate referenceDate) {
        if (!isOverdue(referenceDate)) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, referenceDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecord that = (BorrowRecord) o;
        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }

    @Override
    public String toString() {
        String status = returned
                ? "RETURNED on " + returnDate
                : (isOverdue(LocalDate.now()) ? "OVERDUE (due: " + dueDate + ")" : "ACTIVE (due: " + dueDate + ")");
        return String.format("BorrowRecord[%s | Patron: %s (%s) | Book: '%s' | Borrowed: %s | Status: %s]",
                recordId, patronName, patronType.getDisplayName(), bookTitle, borrowDate, status);
    }
}
