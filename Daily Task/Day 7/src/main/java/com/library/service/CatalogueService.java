package com.library.service;

import com.library.model.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages the library book catalogue using:
 * <ul>
 *   <li><b>ArrayList</b>: Ordered, indexed catalogue storage for fast random access.</li>
 *   <li><b>LinkedHashSet</b>: Insertion-order-preserving de-duplication filter (no duplicate ISBNs).</li>
 * </ul>
 */
public class CatalogueService {

    /** Primary catalogue storage — ArrayList for indexed access and ordered iteration. */
    private final List<Book> catalogue = new ArrayList<>();

    /** De-duplication guard — LinkedHashSet rejects duplicate Book objects (by ISBN via equals/hashCode). */
    private final Set<Book> isbnDedup = new LinkedHashSet<>();

    /**
     * Adds a book to the catalogue. Duplicate ISBNs are silently rejected by the LinkedHashSet guard.
     *
     * @param book the book to add
     * @return true if added successfully, false if duplicate ISBN was rejected
     */
    public boolean addBook(Book book) {
        // LinkedHashSet.add() returns false if the element is already present (de-dup)
        boolean isNew = isbnDedup.add(book);
        if (isNew) {
            catalogue.add(book);
        }
        return isNew;
    }

    /**
     * Attempts to add multiple books, skipping duplicates.
     *
     * @param books the list of books (may contain duplicates)
     * @return the number of unique books actually added
     */
    public int addBooks(List<Book> books) {
        int addedCount = 0;
        for (Book book : books) {
            if (addBook(book)) {
                addedCount++;
            }
        }
        return addedCount;
    }

    /**
     * Gets a book by index from the ArrayList catalogue. O(1) random access.
     */
    public Book getByIndex(int index) {
        if (index < 0 || index >= catalogue.size()) {
            throw new IndexOutOfBoundsException("Invalid catalogue index: " + index);
        }
        return catalogue.get(index);
    }

    /**
     * Finds a book by ISBN. Linear scan through the ArrayList.
     */
    public Optional<Book> findByIsbn(String isbn) {
        return catalogue.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst();
    }

    /**
     * Searches for books by title keyword (case-insensitive partial match).
     */
    public List<Book> searchByTitle(String keyword) {
        return catalogue.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the full catalogue as an unmodifiable list.
     */
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(catalogue);
    }

    /**
     * Returns the de-dup set snapshot (preserves insertion order).
     */
    public Set<Book> getDedupSet() {
        return Collections.unmodifiableSet(isbnDedup);
    }

    /**
     * Returns catalogue size.
     */
    public int size() {
        return catalogue.size();
    }

    /**
     * Returns the number of attempts that were rejected as duplicates.
     * (Useful for demonstrating LinkedHashSet de-dup in action.)
     */
    public int getDedupSetSize() {
        return isbnDedup.size();
    }
}
