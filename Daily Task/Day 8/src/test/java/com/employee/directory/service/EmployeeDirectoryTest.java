package com.employee.directory.service;

import com.employee.directory.model.DepartmentAnalytics;
import com.employee.directory.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Day 8 — Employee Directory Masterclass Unit Tests")
public class EmployeeDirectoryTest {

    private EmployeeDirectoryService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeDirectoryService();
        service.addEmployee(new Employee(101, "Dr. Alice Morgan", "Pharmacy", "Chief Pharmacist", 158000.0, 15));
        service.addEmployee(new Employee(102, "Sarah Jenkins", "Pharmacy", "Clinical Pharmacist", 112000.0, 7));
        service.addEmployee(new Employee(103, "Amira Hassan", "QA", "Senior QA Lead", 125000.0, 10));
        service.addEmployee(new Employee(104, "James Patel", "Finance", "Controller", 125000.0, 11));
        service.addEmployee(new Employee(108, "Dr. Marcus Chen", "R&D", "Principal Chemist", 145000.0, 12));
        service.addEmployee(new Employee(111, "Lucas Meyer", "R&D", "Formulation Scientist", 125000.0, 8));
        service.addEmployee(new Employee(115, "David Rodriguez", "Logistics", "Cold-Chain Manager", 95000.0, 8));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GROUP 1: HASHMAP LOOKUP & OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("1. HashMap Lookup & Directory Operations")
    class HashMapLookupTests {

        @Test
        @DisplayName("1a. HashMap finds existing employee by ID in O(1)")
        void testFindByIdSuccess() {
            Optional<Employee> empOpt = service.findById(101);
            assertTrue(empOpt.isPresent(), "Employee 101 should be present");
            assertEquals("Dr. Alice Morgan", empOpt.get().getName());
            assertEquals("Pharmacy", empOpt.get().getDepartment());
        }

        @Test
        @DisplayName("1b. HashMap returns Optional.empty for missing ID")
        void testFindByNonExistentId() {
            Optional<Employee> empOpt = service.findById(999);
            assertTrue(empOpt.isEmpty(), "Non-existent employee ID should return empty Optional");
        }

        @Test
        @DisplayName("1c. HashMap prevents duplicate ID insertion")
        void testPreventDuplicateId() {
            Employee duplicate = new Employee(101, "Impostor", "Pharmacy", "Intern", 40000.0, 1);
            boolean added = service.addEmployee(duplicate);
            assertFalse(added, "Duplicate ID must be rejected");
            assertEquals(7, service.size(), "Directory size must not change on duplicate insertion");
        }

        @Test
        @DisplayName("1d. Salary update mutates record correctly")
        void testSalaryUpdate() {
            boolean updated = service.updateSalary(102, 118000.0);
            assertTrue(updated, "Update should succeed for existing ID");
            assertEquals(118000.0, service.findById(102).get().getSalary(), 0.001);

            boolean updatedMissing = service.updateSalary(999, 100000.0);
            assertFalse(updatedMissing, "Update should fail for missing ID");
        }

        @Test
        @DisplayName("1e. Removal removes record and returns Optional of removed entity")
        void testRemoveEmployee() {
            Optional<Employee> removed = service.removeEmployee(115);
            assertTrue(removed.isPresent());
            assertEquals("David Rodriguez", removed.get().getName());
            assertEquals(6, service.size());
            assertFalse(service.containsEmployee(115));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GROUP 2: FREQUENCY COUNT WITH Map.merge()
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("2. Frequency Count & Aggregation with Map.merge()")
    class MergeFrequencyTests {

        @Test
        @DisplayName("2a. Department headcount frequency count matches exact count")
        void testDepartmentHeadcountMerge() {
            Map<String, Integer> counts = service.countByDepartmentUsingMerge();

            assertEquals(5, counts.size(), "There are 5 distinct departments");
            assertEquals(2, counts.get("Pharmacy"));
            assertEquals(2, counts.get("R&D"));
            assertEquals(1, counts.get("QA"));
            assertEquals(1, counts.get("Finance"));
            assertEquals(1, counts.get("Logistics"));
        }

        @Test
        @DisplayName("2b. Role frequencies correctly calculated with merge")
        void testRoleFrequencies() {
            Map<String, Integer> roleCounts = service.countByRoleUsingMerge();
            assertEquals(7, roleCounts.size(), "Each test employee has a unique role");
            assertEquals(1, roleCounts.get("Chief Pharmacist"));
            assertEquals(1, roleCounts.get("Senior QA Lead"));
        }

        @Test
        @DisplayName("2c. Total payroll aggregation with merge calculates exact sums")
        void testPayrollAggregationWithMerge() {
            Map<String, Double> payroll = service.aggregatePayrollByDepartmentUsingMerge();

            // Pharmacy: Alice (158000) + Sarah (112000) = 270000
            assertEquals(270000.0, payroll.get("Pharmacy"), 0.001);
            // R&D: Marcus (145000) + Lucas (125000) = 270000
            assertEquals(270000.0, payroll.get("R&D"), 0.001);
            // Finance: James (125000)
            assertEquals(125000.0, payroll.get("Finance"), 0.001);
        }

        @Test
        @DisplayName("2d. Merge implementation yields identical results to old imperative approach")
        void testMergeMatchesOldWay() {
            Map<String, Integer> mergeResult = service.countByDepartmentUsingMerge();
            Map<String, Integer> oldWayResult = service.countByDepartmentOldWay();
            assertEquals(mergeResult, oldWayResult, "Map.merge() and old verbose approach must produce identical maps");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GROUP 3: TREEMAP DEPARTMENT REPORT
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("3. TreeMap Department Report (NavigableMap)")
    class TreeMapReportTests {

        @Test
        @DisplayName("3a. TreeMap keys are strictly sorted in alphabetical order")
        void testSortedAlphabeticalKeys() {
            NavigableMap<String, DepartmentAnalytics> report = service.generateDepartmentReport();

            List<String> departmentKeys = new ArrayList<>(report.keySet());
            List<String> expectedOrder = List.of("Finance", "Logistics", "Pharmacy", "QA", "R&D");

            assertEquals(expectedOrder, departmentKeys, "TreeMap must iterate keys in natural alphabetical order");
        }

        @Test
        @DisplayName("3b. DepartmentAnalytics calculates headcount, total, average, and highest earner")
        void testDepartmentAnalyticsCalculations() {
            NavigableMap<String, DepartmentAnalytics> report = service.generateDepartmentReport();

            DepartmentAnalytics pharmacyStats = report.get("Pharmacy");
            assertNotNull(pharmacyStats);
            assertEquals(2, pharmacyStats.getHeadcount());
            assertEquals(270000.0, pharmacyStats.getTotalPayroll(), 0.001);
            assertEquals(135000.0, pharmacyStats.getAverageSalary(), 0.001);
            assertEquals(112000.0, pharmacyStats.getMinSalary(), 0.001);
            assertEquals(158000.0, pharmacyStats.getMaxSalary(), 0.001);
            assertEquals("Dr. Alice Morgan", pharmacyStats.getHighestEarner().getName());
        }

        @Test
        @DisplayName("3c. NavigableMap firstKey, lastKey, and subMap behave as expected")
        void testNavigableQueries() {
            NavigableMap<String, DepartmentAnalytics> report = service.generateDepartmentReport();

            assertEquals("Finance", service.getFirstDepartmentName().orElse(null));
            assertEquals("R&D", service.getLastDepartmentName().orElse(null));

            NavigableMap<String, DepartmentAnalytics> subRange =
                    service.getDepartmentSubRange("Logistics", true, "QA", true);
            List<String> subKeys = new ArrayList<>(subRange.keySet());
            assertEquals(List.of("Logistics", "Pharmacy", "QA"), subKeys);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GROUP 4: COMPARABLE BY ID (NATURAL ORDERING)
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("4. Comparable by ID (Natural Ordering)")
    class ComparableTests {

        @Test
        @DisplayName("4a. Collections.sort sorts employees strictly by ID ascending")
        void testCollectionsSortById() {
            List<Employee> sorted = service.getEmployeesSortedByIdNatural();

            assertEquals(7, sorted.size());
            for (int i = 0; i < sorted.size() - 1; i++) {
                assertTrue(sorted.get(i).getId() < sorted.get(i + 1).getId(),
                        "Employee at index " + i + " must have smaller ID than next index");
            }
            assertEquals(101, sorted.get(0).getId());
            assertEquals(115, sorted.get(sorted.size() - 1).getId());
        }

        @Test
        @DisplayName("4b. TreeSet automatically orders by natural Comparable without external Comparator")
        void testTreeSetOrdering() {
            TreeSet<Employee> set = service.getEmployeeTreeSet();
            assertEquals(101, set.first().getId());
            assertEquals(115, set.last().getId());
        }

        @Test
        @DisplayName("4c. Binary search finds employee in naturally sorted list")
        void testBinarySearch() {
            List<Employee> sorted = service.getEmployeesSortedByIdNatural();
            int index = service.binarySearchById(sorted, 108);
            assertTrue(index >= 0, "Binary search should find ID 108");
            assertEquals("Dr. Marcus Chen", sorted.get(index).getName());
        }

        @Test
        @DisplayName("4d. Comparable contract adherence: reflexive, antisymmetric, transitive, equals consistency")
        void testComparableContract() {
            Employee e1 = new Employee(101, "Alice", "Dept", "Role", 100.0, 1);
            Employee e2 = new Employee(102, "Bob", "Dept", "Role", 100.0, 1);
            Employee e3 = new Employee(103, "Charlie", "Dept", "Role", 100.0, 1);
            Employee e1Clone = new Employee(101, "Alice Clone", "OtherDept", "OtherRole", 200.0, 5);

            // Reflexive
            assertEquals(0, e1.compareTo(e1));

            // Antisymmetric: sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
            assertEquals(Integer.signum(e1.compareTo(e2)), -Integer.signum(e2.compareTo(e1)));

            // Transitive: e1 < e2 and e2 < e3 => e1 < e3
            assertTrue(e1.compareTo(e2) < 0);
            assertTrue(e2.compareTo(e3) < 0);
            assertTrue(e1.compareTo(e3) < 0);

            // Consistency with equals: compareTo == 0 <=> equals == true
            assertEquals(0, e1.compareTo(e1Clone));
            assertEquals(e1, e1Clone);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GROUP 5: TOP-5 SALARY COMPARATOR CHAIN
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("5. Top-5 Salary Comparator Chain")
    class ComparatorChainTests {

        @Test
        @DisplayName("5a. Returns exact top 5 earners sorted by salary descending")
        void testTop5Earners() {
            List<Employee> top5 = service.getTopEarners(5);

            assertEquals(5, top5.size());
            assertEquals(158000.0, top5.get(0).getSalary(), 0.001); // Alice
            assertEquals(145000.0, top5.get(1).getSalary(), 0.001); // Marcus
            assertEquals(125000.0, top5.get(2).getSalary(), 0.001); // Tie
            assertEquals(125000.0, top5.get(3).getSalary(), 0.001); // Tie
            assertEquals(125000.0, top5.get(4).getSalary(), 0.001); // Tie
        }

        @Test
        @DisplayName("5b. Tie-breaker resolves equal salaries alphabetically by name")
        void testSalaryTieBreaker() {
            List<Employee> top5 = service.getTopEarners(5);

            // Employees with $125,000 salary: Amira Hassan, James Patel, Lucas Meyer
            // Chained comparator tie-breaker is Name (ASC):
            // "Amira Hassan" < "James Patel" < "Lucas Meyer"
            assertEquals("Amira Hassan", top5.get(2).getName());
            assertEquals("James Patel", top5.get(3).getName());
            assertEquals("Lucas Meyer", top5.get(4).getName());
        }

        @Test
        @DisplayName("5c. Bounded PriorityQueue Min-Heap produces identical ranking to full sort")
        void testMinHeapMatchesStreamSort() {
            List<Employee> streamTop5 = service.getTopEarners(5);
            List<Employee> heapTop5 = service.getTopEarnersUsingPriorityQueue(5);

            assertEquals(streamTop5, heapTop5, "PriorityQueue top-5 must match Stream sort top-5 exactly");
        }

        @Test
        @DisplayName("5d. Limit edge case: requesting more elements than directory contains")
        void testRequestMoreThanAvailable() {
            List<Employee> top100 = service.getTopEarners(100);
            assertEquals(7, top100.size(), "Should return all available elements when limit exceeds size");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GROUP 6: MISSING-HASHCODE BUG DEMO AND FIX
    // ═══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("6. Missing-hashCode Bug Demo & Fix")
    class HashCodeBugTests {

        private final HashCodeBugDemonstrator demonstrator = new HashCodeBugDemonstrator();

        @Test
        @DisplayName("6a. Missing hashCode causes HashMap lookup to return null for equal keys")
        void testMissingHashCodeCausesLookupFailure() {
            HashCodeBugDemonstrator.BugDiagnosis diagnosis = demonstrator.demonstrateMissingHashCodeBug();

            assertTrue(diagnosis.keysAreEqual(), "Keys MUST evaluate to true via equals()");
            assertFalse(diagnosis.hashCodesMatch(), "HashCodes do NOT match due to inheriting Object.hashCode()");
            assertNull(diagnosis.mapGetValueWithKey2(), "HashMap.get() MUST return null due to mismatched hash buckets");
            assertEquals(2, diagnosis.setSizeAfterAddingBoth(), "HashSet MUST contain 2 duplicate keys violating Set contract");
            assertTrue(diagnosis.isBugDemonstrated());
        }

        @Test
        @DisplayName("6b. Fixed hashCode restores HashMap lookup and HashSet de-duplication")
        void testFixedHashCodeRestoresContract() {
            HashCodeBugDemonstrator.FixDiagnosis diagnosis = demonstrator.demonstrateFixedHashCode();

            assertTrue(diagnosis.keysAreEqual(), "Keys MUST evaluate to true via equals()");
            assertTrue(diagnosis.hashCodesMatch(), "HashCodes MUST match when properly overridden");
            assertEquals(diagnosis.simulatedBucketKey1(), diagnosis.simulatedBucketKey2(), "Both keys MUST land in the same hash bucket");
            assertNotNull(diagnosis.mapGetValueWithKey2(), "HashMap.get() MUST successfully retrieve stored value");
            assertEquals("Dr. Alice Morgan - Lead Pharmacist", diagnosis.mapGetValueWithKey2());
            assertEquals(1, diagnosis.setSizeAfterAddingBoth(), "HashSet MUST reject duplicate key and maintain size = 1");
            assertTrue(diagnosis.isFixConfirmed());
        }
    }
}
