package com.library.model;

import java.util.Objects;

/**
 * Represents a book in the library catalogue.
 * Equality is based on ISBN so that LinkedHashSet de-duplication works correctly.
 */
public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final String genre;
    private final int publicationYear;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int publicationYear, int totalCopies) {
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public synchronized boolean checkoutCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public synchronized void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    /**
     * Equality based on ISBN for LinkedHashSet de-duplication.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Book[ISBN=%s, '%s' by %s (%d), Available: %d/%d]",
                isbn, title, author, publicationYear, availableCopies, totalCopies);
    }
}
