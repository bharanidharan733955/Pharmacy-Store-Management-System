/**
 * Monthly Usage Analyser - Java Language Fundamentals Demonstration
 * 
 * Demonstrates:
 * 1. Primitive data types & ranges
 * 2. 1-D and 2-D Arrays
 * 3. Final Constants (eliminating magic numbers via Constants class)
 * 4. Operators (Arithmetic, Relational, Logical, Ternary)
 * 5. Type Casting (Implicit Widening vs Explicit Narrowing)
 * 6. Integer Overflow demonstration & fix using long
 * 7. Integer division vs Floating-point division
 */
public class MonthlyUsageAnalyser {

    public static void main(String[] args) {

        System.out.println("==================================================================");
        System.out.println("        MONTHLY USAGE ANALYSER - JAVA LANGUAGE FUNDAMENTALS       ");
        System.out.println("==================================================================");

        // ------------------------------------------------------------------
        // 1. Primitive Data Types Demonstration
        // ------------------------------------------------------------------
        byte sampleByte = 127;                          // 8-bit integer (-128 to 127)
        short sampleShort = 32767;                      // 16-bit integer (-32,768 to 32,767)
        int sampleInt = 2147483647;                     // 32-bit integer
        long sampleLong = 9223372036854775807L;         // 64-bit integer
        float sampleFloat = 3.14159f;                   // 32-bit IEEE 754 floating point
        double sampleDouble = 3.141592653589793;        // 64-bit IEEE 754 floating point
        char sampleChar = 'A';                          // 16-bit Unicode character
        boolean sampleBoolean = true;                   // boolean (true/false)

        System.out.println("\n--- 1. Primitive Data Types Overview ---");
        System.out.println("byte    : " + sampleByte + " (Range: " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE + ")");
        System.out.println("short   : " + sampleShort + " (Range: " + Short.MIN_VALUE + " to " + Short.MAX_VALUE + ")");
        System.out.println("int     : " + sampleInt + " (Range: " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + ")");
        System.out.println("long    : " + sampleLong);
        System.out.println("float   : " + sampleFloat);
        System.out.println("double  : " + sampleDouble);
        System.out.println("char    : " + sampleChar);
        System.out.println("boolean : " + sampleBoolean);

        // ------------------------------------------------------------------
        // 2. 1-D Array Processing (12 Months Usage Data)
        // ------------------------------------------------------------------
        int[] monthlyUsage = {450, 620, 890, 310, 750, 920, 580, 640, 710, 830, 490, 960};

        // Long accumulator prevents potential integer overflow during summation
        long totalUsage = 0;
        int maxUsage = monthlyUsage[0];
        int minUsage = monthlyUsage[0];
        String maxMonth = Constants.MONTH_NAMES[0];
        String minMonth = Constants.MONTH_NAMES[0];

        for (int i = 0; i < monthlyUsage.length; i++) {
            // Implicit Widening Conversion: int value added to long totalUsage
            totalUsage += monthlyUsage[i];

            // Relational Operator (>)
            if (monthlyUsage[i] > maxUsage) {
                maxUsage = monthlyUsage[i];
                maxMonth = Constants.MONTH_NAMES[i];
            }

            // Relational Operator (<)
            if (monthlyUsage[i] < minUsage) {
                minUsage = monthlyUsage[i];
                minMonth = Constants.MONTH_NAMES[i];
            }
        }

        // ------------------------------------------------------------------
        // 3. Integer Division vs Floating-Point Division (Type Casting)
        // ------------------------------------------------------------------
        // Integer Division: Truncates fractional part
        int integerAverage = (int) totalUsage / Constants.MONTHS_IN_YEAR;

        // Floating-Point Division: Explicit Narrowing/Widening Cast for accuracy
        double accurateAverage = (double) totalUsage / Constants.MONTHS_IN_YEAR;

        // ------------------------------------------------------------------
        // 4. Ternary Operator for Performance Grading
        // ------------------------------------------------------------------
        // Ternary Expression: (condition) ? value_if_true : value_if_false
        char annualGrade = (accurateAverage >= Constants.HIGH_USAGE_SLAB) ? Constants.GRADE_A :
                           (accurateAverage >= Constants.MEDIUM_USAGE_SLAB) ? Constants.GRADE_B :
                           (accurateAverage >= Constants.LOW_USAGE_SLAB) ? Constants.GRADE_C : Constants.GRADE_D;

        System.out.println("\n--- 2. 1-D Monthly Usage Summary ---");
        System.out.println("Total Usage (12 Months): " + totalUsage + " units");
        System.out.println("Integer Division Avg   : " + integerAverage + " units (Loss of precision!)");
        System.out.printf("Casted Double Average  : %.2f units (Accurate)%n", accurateAverage);
        System.out.println("Peak Month             : " + maxMonth + " (" + maxUsage + " units)");
        System.out.println("Lowest Month           : " + minMonth + " (" + minUsage + " units)");
        System.out.println("Annual Performance Grade: " + annualGrade);

        // ------------------------------------------------------------------
        // 5. Integer Overflow Demonstration & Mitigation
        // ------------------------------------------------------------------
        System.out.println("\n--- 3. Integer Overflow Demonstration ---");
        int maxInt = Integer.MAX_VALUE; // 2,147,483,647
        int overflowedVal = maxInt + 100; // Wraps around to negative due to 32-bit signed overflow

        System.out.println("Integer.MAX_VALUE      : " + maxInt);
        System.out.println("Overflowed (maxInt + 100): " + overflowedVal + " ❌ (WRONG due to 32-bit wraparound!)");

        // Mitigation: Use long primitive type (64-bit)
        long safeCalculation = (long) maxInt + 100;
        System.out.println("Fixed with long cast   : " + safeCalculation + " ✅ (CORRECT!)");

        // ------------------------------------------------------------------
        // 6. Explicit Narrowing Type Casting Demonstration
        // ------------------------------------------------------------------
        System.out.println("\n--- 4. Type Casting Demonstration ---");
        double preciseReading = 987.65432;
        int narrowedInt = (int) preciseReading; // Explicit narrowing cast (truncates decimal)
        byte narrowedByte = (byte) 300;         // Narrowing cast causing byte overflow (300 % 256)

        System.out.println("Original double value  : " + preciseReading);
        System.out.println("Narrowed to int        : " + narrowedInt + " (Decimal truncated)");
        System.out.println("Narrowed (byte) 300    : " + narrowedByte + " (Byte overflow wraparound)");

        // ------------------------------------------------------------------
        // 7. 2-D Array Processing (3 Houses / Branches x 12 Months)
        // ------------------------------------------------------------------
        int[][] houseUsage = {
            {450, 620, 890, 310, 750, 920, 580, 640, 710, 830, 490, 960}, // House Alpha
            {300, 410, 550, 280, 600, 710, 490, 520, 610, 680, 420, 790}, // House Beta
            {520, 700, 950, 400, 810, 990, 630, 710, 790, 890, 560, 1020} // House Gamma
        };

        System.out.println("\n--- 5. 2-D Multi-House Usage Analysis ---");
        System.out.printf("%-30s | %-12s | %-12s | %-6s%n", "House / Branch", "Total Units", "Monthly Avg", "Grade");
        System.out.println("-------------------------------------------------------------------");

        for (int h = 0; h < houseUsage.length; h++) {
            long hTotal = 0;
            int hMax = houseUsage[h][0];

            for (int m = 0; m < houseUsage[h].length; m++) {
                hTotal += houseUsage[h][m];

                // Logical Operator (&&)
                if (houseUsage[h][m] > hMax && houseUsage[h][m] > 500) {
                    hMax = houseUsage[h][m];
                }
            }

            double hAvg = (double) hTotal / houseUsage[h].length;
            char hGrade = (hAvg >= Constants.HIGH_USAGE_SLAB) ? Constants.GRADE_A :
                          (hAvg >= Constants.MEDIUM_USAGE_SLAB) ? Constants.GRADE_B :
                          (hAvg >= Constants.LOW_USAGE_SLAB) ? Constants.GRADE_C : Constants.GRADE_D;

            System.out.printf("%-30s | %-12d | %-12.2f | %-6c%n", Constants.HOUSE_NAMES[h], hTotal, hAvg, hGrade);
        }

        System.out.println("==================================================================");
    }
}
