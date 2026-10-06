public class MonthlyUsageAnalyser {

    // Constants for usage slabs
    public static final int HIGH_USAGE_SLAB = 800;
    public static final int MEDIUM_USAGE_SLAB = 500;

    public static void main(String[] args) {

        // 1-D Array: 12 Months of Medicine Sales / Usage Data (Units)
        int[] monthlyUsage = {450, 620, 890, 310, 750, 920, 580, 640, 710, 830, 490, 960};
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        // Long accumulator to prevent integer overflow
        long totalUsage = 0;
        int maxUsage = monthlyUsage[0];
        int minUsage = monthlyUsage[0];
        String maxMonth = months[0];
        String minMonth = months[0];

        for (int i = 0; i < monthlyUsage.length; i++) {
            totalUsage += monthlyUsage[i]; // Widening conversion / overflow protection

            if (monthlyUsage[i] > maxUsage) {
                maxUsage = monthlyUsage[i];
                maxMonth = months[i];
            }

            if (monthlyUsage[i] < minUsage) {
                minUsage = monthlyUsage[i];
                minMonth = months[i];
            }
        }

        // Type casting for accurate average calculation
        double averageUsage = (double) totalUsage / monthlyUsage.length;

        // Char grade via nested ternary operators
        char usageGrade = (averageUsage >= HIGH_USAGE_SLAB) ? 'A' :
                          (averageUsage >= MEDIUM_USAGE_SLAB) ? 'B' : 'C';

        System.out.println("\n--- 1-D Monthly Analysis Summary ---");
        System.out.println("Total Annual Usage     : " + totalUsage + " units");
        System.out.printf("Average Monthly Usage  : %.2f units%n", averageUsage);
        System.out.println("Peak Usage Month       : " + maxMonth + " (" + maxUsage + " units)");
        System.out.println("Lowest Usage Month     : " + minMonth + " (" + minUsage + " units)");
        System.out.println("Annual Performance Grade: " + usageGrade);

        // 2-D Array: Usage across 3 Pharmacy Branches / Houses over 12 Months
        int[][] branchUsage = {
            {450, 620, 890, 310, 750, 920, 580, 640, 710, 830, 490, 960}, // Branch 1 (Central)
            {300, 410, 550, 280, 600, 710, 490, 520, 610, 680, 420, 790}, // Branch 2 (North)
            {520, 700, 950, 400, 810, 990, 630, 710, 790, 890, 560, 1020} // Branch 3 (South)
        };

        String[] branchNames = {"Central Branch", "North Branch", "South Branch"};

        System.out.println("\n--- 2-D Multi-Branch Analysis ---");
        System.out.printf("%-15s | %-12s | %-12s | %-6s%n", "Branch", "Total Units", "Avg / Month", "Grade");
        System.out.println("---------------------------------------------------------");

        for (int b = 0; b < branchUsage.length; b++) {
            long bTotal = 0;
            for (int m = 0; m < branchUsage[b].length; m++) {
                bTotal += branchUsage[b][m];
            }
            double bAvg = (double) bTotal / branchUsage[b].length;
            char bGrade = (bAvg >= HIGH_USAGE_SLAB) ? 'A' :
                          (bAvg >= MEDIUM_USAGE_SLAB) ? 'B' : 'C';

            System.out.printf("%-15s | %-12d | %-12.2f | %-6c%n", branchNames[b], bTotal, bAvg, bGrade);
        }
        System.out.println("==================================================");
    }
}
