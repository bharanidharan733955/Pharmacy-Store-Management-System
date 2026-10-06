/**
 * Centralized Constants class for Monthly Usage Analyser application.
 * Eliminates magic numbers and defines immutable business rules.
 */
public final class Constants {

    // Private constructor prevents instantiation
    private Constants() {}

    // Time & Scope Dimensions
    public static final int MONTHS_IN_YEAR = 12;
    public static final int BRANCH_COUNT = 3;

    // Performance Slab Slabs (Units)
    public static final int HIGH_USAGE_SLAB = 800;   // Tier A: >= 800 units
    public static final int MEDIUM_USAGE_SLAB = 500; // Tier B: >= 500 units
    public static final int LOW_USAGE_SLAB = 300;    // Tier C: >= 300 units
                                                     // Tier D: < 300 units

    // Grade Characters
    public static final char GRADE_A = 'A';
    public static final char GRADE_B = 'B';
    public static final char GRADE_C = 'C';
    public static final char GRADE_D = 'D';

    // Labels for 12 Months
    public static final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    // Labels for Branches / Houses
    public static final String[] HOUSE_NAMES = {
        "House Alpha (Central Branch)",
        "House Beta  (North Branch)",
        "House Gamma (South Branch)"
    };
}
