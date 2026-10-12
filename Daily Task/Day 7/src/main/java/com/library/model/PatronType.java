package com.library.model;

/**
 * Enum representing the type of library patron.
 * Used by PriorityQueue to grant faculty-first priority.
 */
public enum PatronType {
    /** Faculty members receive highest priority in the waitlist. */
    FACULTY(1, "Faculty"),

    /** Research scholars receive second-level priority. */
    RESEARCH_SCHOLAR(2, "Research Scholar"),

    /** Regular students receive standard priority. */
    STUDENT(3, "Student"),

    /** External/guest patrons receive lowest priority. */
    GUEST(4, "Guest");

    private final int priorityLevel;
    private final String displayName;

    PatronType(int priorityLevel, String displayName) {
        this.priorityLevel = priorityLevel;
        this.displayName = displayName;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public String getDisplayName() {
        return displayName;
    }
}
