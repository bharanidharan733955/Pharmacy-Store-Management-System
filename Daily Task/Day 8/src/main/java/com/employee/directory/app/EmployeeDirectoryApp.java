package com.employee.directory.app;

import com.employee.directory.model.DepartmentAnalytics;
import com.employee.directory.model.Employee;
import com.employee.directory.service.EmployeeDirectoryService;
import com.employee.directory.service.HashCodeBugDemonstrator;

import java.util.*;

/**
 * Main command-line application showcasing all Day 8 concepts:
 * 1. HashMap O(1) Lookup
 * 2. Frequency Counting with Map.merge()
 * 3. TreeMap Department Report (NavigableMap)
 * 4. Comparable by ID (Natural Ordering)
 * 5. Top-5 Salary Comparator Chain with Tie-Breakers
 * 6. Missing-hashCode Bug Demo & Fix
 */
public class EmployeeDirectoryApp {

    public static void main(String[] args) {
        printBanner();

        EmployeeDirectoryService service = new EmployeeDirectoryService();
        populateSampleData(service);

        runSection1HashMapLookup(service);
        runSection2FrequencyCountWithMerge(service);
        runSection3TreeMapDepartmentReport(service);
        runSection4ComparableById(service);
        runSection5Top5SalaryComparatorChain(service);
        runSection6MissingHashCodeBugDemoAndFix();

        printClosingSummary();
    }

    private static void printBanner() {
        System.out.println("===================================================================================");
        System.out.println("   DAY 8: EMPLOYEE DIRECTORY & JAVA MAPS / OBJECT CONTRACT MASTERCLASS            ");
        System.out.println("   HashMap | Map.merge() | TreeMap | Comparable | Comparator Chain | hashCode Bug  ");
        System.out.println("===================================================================================\n");
    }

    private static void populateSampleData(EmployeeDirectoryService service) {
        List<Employee> sampleStaff = List.of(
                new Employee(108, "Dr. Marcus Chen", "R&D", "Principal Chemist", 145000.0, 12),
                new Employee(102, "Sarah Jenkins", "Pharmacy", "Clinical Pharmacist", 112000.0, 7),
                new Employee(115, "David Rodriguez", "Logistics", "Cold-Chain Manager", 95000.0, 8),
                new Employee(101, "Dr. Alice Morgan", "Pharmacy", "Chief Pharmacist", 158000.0, 15),
                new Employee(112, "Elena Rostova", "QA", "Compliance Auditor", 108000.0, 9),
                new Employee(104, "James Patel", "Finance", "Controller", 125000.0, 11),
                new Employee(120, "Chloe Bennett", "R&D", "Bioinformatics Dev", 115000.0, 4),
                new Employee(107, "Robert Taylor", "Logistics", "Inventory Specialist", 78000.0, 5),
                new Employee(109, "Hannah Kim", "Pharmacy", "Staff Pharmacist", 98000.0, 3),
                new Employee(118, "Victor Vance", "Finance", "Financial Analyst", 92000.0, 6),
                new Employee(103, "Amira Hassan", "QA", "Senior QA Lead", 125000.0, 10), // Same salary as James Patel (tie-breaker test)
                new Employee(111, "Lucas Meyer", "R&D", "Formulation Scientist", 125000.0, 8)  // Same salary as James and Amira
        );

        for (Employee emp : sampleStaff) {
            service.addEmployee(emp);
        }
    }

    // ===========================================================================
    // SECTION 1: HASHMAP LOOKUP
    // ===========================================================================
    private static void runSection1HashMapLookup(EmployeeDirectoryService service) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("[1] HASHMAP LOOKUP - O(1) KEY-VALUE ACCESS");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("Total records stored in HashMap: %d employees%n%n", service.size());

        // Test Lookup existing
        int targetId = 101;
        System.out.println("[Lookup] Querying Employee ID: " + targetId);
        Optional<Employee> found = service.findById(targetId);
        found.ifPresentOrElse(
                emp -> System.out.println("   [FOUND] " + emp),
                () -> System.out.println("   [NOT FOUND] Employee ID " + targetId)
        );

        // Test Lookup missing
        int missingId = 999;
        System.out.println("\n[Lookup] Querying non-existent Employee ID: " + missingId);
        Optional<Employee> missing = service.findById(missingId);
        missing.ifPresentOrElse(
                emp -> System.out.println("   [FOUND] " + emp),
                () -> System.out.println("   [NOT FOUND] Handled gracefully with Optional.empty()")
        );

        // Salary update demo
        System.out.println("\n[Update] Updating salary for Employee ID 102 from $112,000 to $120,000:");
        boolean updated = service.updateSalary(102, 120000.0);
        System.out.printf("   Update successful: %s -> %s%n", updated, service.findById(102).orElse(null));
        System.out.println();
    }

    // ===========================================================================
    // SECTION 2: FREQUENCY COUNT WITH Map.merge()
    // ===========================================================================
    private static void runSection2FrequencyCountWithMerge(EmployeeDirectoryService service) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("[2] FREQUENCY COUNTING & AGGREGATION USING Map.merge()");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Java 8 Map.merge(key, 1, Integer::sum) seamlessly initializes or increments:");

        // Department headcounts
        Map<String, Integer> deptCounts = service.countByDepartmentUsingMerge();
        System.out.println("\n[*] Department Headcounts (Map.merge):");
        deptCounts.forEach((dept, count) ->
                System.out.printf("   - %-16s : %2d employees%n", dept, count)
        );

        // Role frequencies
        Map<String, Integer> roleCounts = service.countByRoleUsingMerge();
        System.out.println("\n[*] Role Frequencies (Map.merge):");
        roleCounts.forEach((role, count) ->
                System.out.printf("   - %-24s : %2d%n", role, count)
        );

        // Total payroll by department with merge
        Map<String, Double> deptPayroll = service.aggregatePayrollByDepartmentUsingMerge();
        System.out.println("\n[*] Department Total Payroll (Map.merge with Double::sum):");
        deptPayroll.forEach((dept, total) ->
                System.out.printf("   - %-16s : $%,12.2f%n", dept, total)
        );
        System.out.println();
    }

    // ===========================================================================
    // SECTION 3: TREEMAP DEPARTMENT REPORT
    // ===========================================================================
    private static void runSection3TreeMapDepartmentReport(EmployeeDirectoryService service) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("[3] TREEMAP DEPARTMENT REPORT - SORTED NavigableMap");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("TreeMap maintains keys in natural alphabetical order (A-Z) with O(log N) operations.\n");

        NavigableMap<String, DepartmentAnalytics> report = service.generateDepartmentReport();

        System.out.printf("%-15s | %-5s | %-15s | %-15s | %-20s | %-12s%n",
                "Department", "Count", "Total Payroll", "Avg Salary", "Highest Earner", "Top Salary");
        System.out.println("-".repeat(92));

        for (Map.Entry<String, DepartmentAnalytics> entry : report.entrySet()) {
            DepartmentAnalytics stats = entry.getValue();
            System.out.printf("%-15s | %5d | $%,13.2f | $%,13.2f | %-20s | $%,10.2f%n",
                    stats.getDepartmentName(),
                    stats.getHeadcount(),
                    stats.getTotalPayroll(),
                    stats.getAverageSalary(),
                    stats.getHighestEarner() != null ? stats.getHighestEarner().getName() : "None",
                    stats.getMaxSalary());
        }

        // NavigableMap queries
        System.out.println("\n[*] NavigableMap Methods Demo:");
        System.out.printf("   - First Department (firstKey) : %s%n", report.firstKey());
        System.out.printf("   - Last Department  (lastKey)  : %s%n", report.lastKey());

        NavigableMap<String, DepartmentAnalytics> subRange = service.getDepartmentSubRange("Logistics", true, "R&D", true);
        System.out.printf("   - Sub-range ['Logistics' to 'R&D'] : %s%n", subRange.keySet());
        System.out.println();
    }

    // ===========================================================================
    // SECTION 4: COMPARABLE BY ID (NATURAL ORDERING)
    // ===========================================================================
    private static void runSection4ComparableById(EmployeeDirectoryService service) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("[4] COMPARABLE BY ID - NATURAL ORDERING & CONTRACT");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Employee implements Comparable<Employee> defining natural sort order by ID:\n");

        List<Employee> naturallySorted = service.getEmployeesSortedByIdNatural();
        System.out.println("[*] Employees Sorted Naturally by ID (Collections.sort):");
        for (Employee emp : naturallySorted) {
            System.out.printf("   ID: %-4d | %-18s | %-14s | $%,10.2f%n",
                    emp.getId(), emp.getName(), emp.getDepartment(), emp.getSalary());
        }

        // TreeSet automatic sorting demo
        TreeSet<Employee> treeSet = service.getEmployeeTreeSet();
        System.out.printf("%n[*] TreeSet<Employee> auto-sorted without external Comparator:%n");
        System.out.printf("   Lowest ID: %d (%s) | Highest ID: %d (%s)%n",
                treeSet.first().getId(), treeSet.first().getName(),
                treeSet.last().getId(), treeSet.last().getName());

        // Binary search demo
        int targetId = 108;
        int index = service.binarySearchById(naturallySorted, targetId);
        System.out.printf("   Binary Search for ID %d -> Index %d (%s)%n",
                targetId, index, (index >= 0 ? naturallySorted.get(index).getName() : "Not Found"));
        System.out.println();
    }

    // ===========================================================================
    // SECTION 5: TOP-5 SALARY COMPARATOR CHAIN
    // ===========================================================================
    private static void runSection5Top5SalaryComparatorChain(EmployeeDirectoryService service) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("[5] TOP-5 SALARY COMPARATOR CHAIN - MULTI-ATTRIBUTE RANKING");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Comparator Chain:");
        System.out.println("  1. Salary (DESC) -> 2. Name (ASC) -> 3. Dept (ASC) -> 4. ID (ASC)\n");

        List<Employee> top5 = service.getTopEarners(5);

        System.out.printf("%-5s | %-20s | %-14s | %-22s | %-12s%n",
                "Rank", "Name", "Department", "Role", "Salary");
        System.out.println("-".repeat(82));

        for (int i = 0; i < top5.size(); i++) {
            Employee emp = top5.get(i);
            System.out.printf("#%-4d | %-20s | %-14s | %-22s | $%,10.2f%n",
                    (i + 1), emp.getName(), emp.getDepartment(), emp.getRole(), emp.getSalary());
        }

        // Notice ties: Amira Hassan ($125,000), James Patel ($125,000), Lucas Meyer ($125,000)
        System.out.println("\n[Note] Tie-Breaking Observation:");
        System.out.println("   Notice employees with identical $125,000 salary are deterministically ordered");
        System.out.println("   by Name alphabetical order: Amira Hassan < James Patel < Lucas Meyer.");

        // Bounded PriorityQueue verification
        List<Employee> heapTop5 = service.getTopEarnersUsingPriorityQueue(5);
        boolean matchingResults = top5.equals(heapTop5);
        System.out.printf("   Bounded Min-Heap PriorityQueue top-5 matches full stream sort: %s%n", matchingResults);
        System.out.println();
    }

    // ===========================================================================
    // SECTION 6: MISSING-HASHCODE BUG DEMO AND FIX
    // ===========================================================================
    private static void runSection6MissingHashCodeBugDemoAndFix() {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("[6] MISSING-HASHCODE BUG DEMO & FIX - OBJECT CONTRACT AUTOPSY");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("The Java Object Contract specifies:");
        System.out.println("  'If two objects are equal according to equals(Object), then calling hashCode()");
        System.out.println("   on each of the two objects MUST produce the same integer result.'\n");

        HashCodeBugDemonstrator demonstrator = new HashCodeBugDemonstrator();

        // PART A: THE BUG
        System.out.println("[BUG] PART A: Reproducing Bug with BuggyEmployeeKey (equals() overridden, hashCode() omitted)");
        HashCodeBugDemonstrator.BugDiagnosis bugResult = demonstrator.demonstrateMissingHashCodeBug();

        System.out.printf("   - key1.equals(key2)                : %b%n", bugResult.keysAreEqual());
        System.out.printf("   - key1.hashCode() (identity)        : %d%n", bugResult.key1HashCode());
        System.out.printf("   - key2.hashCode() (identity)        : %d%n", bugResult.key2HashCode());
        System.out.printf("   - HashCodes match?                  : %b (CONTRACT VIOLATED!)%n", bugResult.hashCodesMatch());
        System.out.printf("   - Bucket Index for Key1 (n=16)      : Bucket #%d%n", bugResult.simulatedBucketKey1());
        System.out.printf("   - Bucket Index for Key2 (n=16)      : Bucket #%d%n", bugResult.simulatedBucketKey2());
        System.out.printf("   - map.put(key1, payload)            : Stored successfully%n");
        System.out.printf("   - map.get(key2)                     : %s  <-- DATA LOST!%n", bugResult.mapGetValueWithKey2());
        System.out.printf("   - HashSet.size() after adding both  : %d  <-- DUPLICATE IN SET!%n", bugResult.setSizeAfterAddingBoth());

        System.out.println("\n   [Warning] Under the Hood: HashMap looks in bucket #" + bugResult.simulatedBucketKey2()
                + " for key2, but key1 was placed into bucket #" + bugResult.simulatedBucketKey1() + ".");
        System.out.println("      HashMap NEVER even calls equals()! The lookup fails instantly.\n");

        // PART B: THE FIX
        System.out.println("[FIX] PART B: Resolving Bug with FixedEmployeeKey (both equals() & hashCode() overridden)");
        HashCodeBugDemonstrator.FixDiagnosis fixResult = demonstrator.demonstrateFixedHashCode();

        System.out.printf("   - key1.equals(key2)                : %b%n", fixResult.keysAreEqual());
        System.out.printf("   - key1.hashCode() (Objects.hash)   : %d%n", fixResult.key1HashCode());
        System.out.printf("   - key2.hashCode() (Objects.hash)   : %d%n", fixResult.key2HashCode());
        System.out.printf("   - HashCodes match?                  : %b (CONTRACT SATISFIED!)%n", fixResult.hashCodesMatch());
        System.out.printf("   - Bucket Index for Key1 (n=16)      : Bucket #%d%n", fixResult.simulatedBucketKey1());
        System.out.printf("   - Bucket Index for Key2 (n=16)      : Bucket #%d (Identical bucket!)%n", fixResult.simulatedBucketKey2());
        System.out.printf("   - map.get(key2)                     : \"%s\"%n", fixResult.mapGetValueWithKey2());
        System.out.printf("   - HashSet.size() after adding both  : %d (Duplicate rejected!)%n", fixResult.setSizeAfterAddingBoth());
        System.out.println("   [Success] All contract guarantees restored!");
        System.out.println();
    }

    private static void printClosingSummary() {
        System.out.println("===================================================================================");
        System.out.println("   ALL 6 DAY 8 REQUIREMENTS DEMONSTRATED SUCCESSFULLY!");
        System.out.println("===================================================================================");
    }
}
