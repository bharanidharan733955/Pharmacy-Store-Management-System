package com.library.service;

import com.library.model.WaitlistRequest;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Manages book waitlists using two queue strategies:
 * <ul>
 *   <li><b>ArrayDeque</b>: Simple FIFO waitlist — first-come, first-served queue for general requests.</li>
 *   <li><b>PriorityQueue</b>: Faculty-first priority waitlist — faculty requests are dequeued before students.</li>
 * </ul>
 *
 * <p>Both queues serve different use cases in the library:
 * <ul>
 *   <li>General reading room requests use the ArrayDeque FIFO queue.</li>
 *   <li>Reserve / reference shelf requests use the PriorityQueue (faculty-first ordering).</li>
 * </ul>
 */
public class WaitlistService {

    /** FIFO waitlist — standard first-come, first-served using ArrayDeque. */
    private final Deque<WaitlistRequest> fifoWaitlist = new ArrayDeque<>();

    /** Priority waitlist — faculty-first ordering using PriorityQueue (min-heap on PatronType.priorityLevel). */
    private final PriorityQueue<WaitlistRequest> priorityWaitlist = new PriorityQueue<>();

    // ─── ArrayDeque FIFO Operations ───────────────────────────────────────────

    /**
     * Adds a request to the end of the FIFO waitlist (ArrayDeque).
     */
    public void addToFifoWaitlist(WaitlistRequest request) {
        fifoWaitlist.offerLast(request);
    }

    /**
     * Retrieves and removes the head of the FIFO waitlist (first-come, first-served).
     *
     * @return the next request, or null if empty
     */
    public WaitlistRequest pollFifoWaitlist() {
        return fifoWaitlist.pollFirst();
    }

    /**
     * Peeks at the head of the FIFO waitlist without removing.
     */
    public WaitlistRequest peekFifoWaitlist() {
        return fifoWaitlist.peekFirst();
    }

    /**
     * Returns the current FIFO waitlist size.
     */
    public int getFifoWaitlistSize() {
        return fifoWaitlist.size();
    }

    /**
     * Returns a snapshot of the FIFO waitlist as a list (preserves insertion order).
     */
    public List<WaitlistRequest> getFifoWaitlistSnapshot() {
        return new ArrayList<>(fifoWaitlist);
    }

    // ─── PriorityQueue Faculty-First Operations ───────────────────────────────

    /**
     * Adds a request to the faculty-first priority waitlist (PriorityQueue).
     * Faculty members will be dequeued before students regardless of arrival time.
     */
    public void addToPriorityWaitlist(WaitlistRequest request) {
        priorityWaitlist.offer(request);
    }

    /**
     * Retrieves and removes the highest-priority request from the priority waitlist.
     * Faculty members are served first; ties broken by earliest request time.
     *
     * @return the highest-priority request, or null if empty
     */
    public WaitlistRequest pollPriorityWaitlist() {
        return priorityWaitlist.poll();
    }

    /**
     * Peeks at the highest-priority request without removing.
     */
    public WaitlistRequest peekPriorityWaitlist() {
        return priorityWaitlist.peek();
    }

    /**
     * Returns the current priority waitlist size.
     */
    public int getPriorityWaitlistSize() {
        return priorityWaitlist.size();
    }

    /**
     * Drains the priority waitlist in priority order and returns the results as a sorted list.
     * This demonstrates that PriorityQueue only guarantees heap ordering on poll(), not on iteration.
     */
    public List<WaitlistRequest> drainPriorityWaitlistInOrder() {
        List<WaitlistRequest> ordered = new ArrayList<>();
        while (!priorityWaitlist.isEmpty()) {
            ordered.add(priorityWaitlist.poll());
        }
        return ordered;
    }

    /**
     * Clears both waitlists.
     */
    public void clearAll() {
        fifoWaitlist.clear();
        priorityWaitlist.clear();
    }
}
