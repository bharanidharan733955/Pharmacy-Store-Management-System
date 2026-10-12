package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a waitlist request for a book that is currently unavailable.
 * Implements Comparable for PriorityQueue ordering: faculty-first, then by request time.
 */
public class WaitlistRequest implements Comparable<WaitlistRequest> {
    private final String requestId;
    private final String patronId;
    private final String patronName;
    private final PatronType patronType;
    private final String bookIsbn;
    private final String bookTitle;
    private final LocalDateTime requestTime;

    public WaitlistRequest(String requestId, String patronId, String patronName,
                           PatronType patronType, String bookIsbn, String bookTitle) {
        this.requestId = requestId;
        this.patronId = patronId;
        this.patronName = patronName;
        this.patronType = patronType;
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.requestTime = LocalDateTime.now();
    }

    public WaitlistRequest(String requestId, String patronId, String patronName,
                           PatronType patronType, String bookIsbn, String bookTitle,
                           LocalDateTime requestTime) {
        this.requestId = requestId;
        this.patronId = patronId;
        this.patronName = patronName;
        this.patronType = patronType;
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.requestTime = requestTime;
    }

    public String getRequestId() {
        return requestId;
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

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    /**
     * Natural ordering for PriorityQueue: lower priority level comes first (Faculty=1 before Student=3).
     * On ties, earlier request time wins (FIFO within same priority).
     */
    @Override
    public int compareTo(WaitlistRequest other) {
        int priorityCompare = Integer.compare(this.patronType.getPriorityLevel(), other.patronType.getPriorityLevel());
        if (priorityCompare != 0) return priorityCompare;
        return this.requestTime.compareTo(other.requestTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitlistRequest that = (WaitlistRequest) o;
        return requestId.equals(that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    @Override
    public String toString() {
        return String.format("WaitlistRequest[%s | %s (%s) | Book: '%s' | Requested: %s]",
                requestId, patronName, patronType.getDisplayName(), bookTitle, requestTime);
    }
}
