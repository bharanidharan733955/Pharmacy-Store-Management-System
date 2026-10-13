package com.employee.directory.service;

import com.employee.directory.model.DepartmentAnalytics;
import com.employee.directory.model.Employee;

import java.util.*;

/**
 * Core service managing employee directory operations.
 * Demonstrates:
 * 1. HashMap O(1) lookup, insertion, update, and deletion.
 * 2. Frequency counting and aggregation using Map.merge().
 * 3. Sorted department analytics reporting using TreeMap (NavigableMap).
 * 4. Natural ordering by ID via Comparable.
 * 5. Multi-criteria salary ranking using a chained Comparator.
 */
public class EmployeeDirectoryService {

    // Internal primary store: O(1) average-time complexity by employee ID
    private final Map<Integer, Employee> directory = new HashMap<>();

    /**
     * Comparator chain for ranking employees by salary descending.
     * Tie-breaking chain:
     * 1. Salary (descending)
     * 2. Name (ascending, A-Z)
     * 3. Department (ascending, A-Z)
     * 4. ID (ascending, numeric tie-breaker for deterministic ordering)
     */
    public static final Comparator<Employee> TOP_SALARY_COMPARATOR_CHAIN = Comparator
            .comparingDouble(Employee::getSalary).reversed()
            .thenComparing(Employee::getName)
            .thenComparing(Employee::getDepartment)
            .thenComparingInt(Employee::getId);

    // ═══════════════════════════════════════════════════════════════════════════
    // 1. HASHMAP LOOKUP & DIRECTORY OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Adds an employee to the directory.
     *
     * @param employee the employee to add
     * @return true if added, false if an employee with the same ID already exists
     */
    public boolean addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Cannot add null employee");
        }
        if (directory.containsKey(employee.getId())) {
            return false;
        }
        directory.put(employee.getId(), employee);
        return true;
    }

    /**
     * Looks up an employee by their ID in O(1) average time.
     *
     * @param id the employee ID
     * @return Optional containing the employee if found, or empty Optional
     */
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(directory.get(id));
    }

    /**
     * Checks if an employee exists in the directory.
     */
    public boolean containsEmployee(int id) {
        return directory.containsKey(id);
    }

    /**
     * Removes an employee by their ID.
     *
     * @param id the employee ID to remove
     * @return Optional containing the removed employee if found, or empty Optional
     */
    public Optional<Employee> removeEmployee(int id) {
        return Optional.ofNullable(directory.remove(id));
    }

    /**
     * Updates an employee's salary.
     *
     * @param id        the employee ID
     * @param newSalary the updated salary
     * @return true if updated, false if employee was not found
     */
    public boolean updateSalary(int id, double newSalary) {
        Employee emp = directory.get(id);
        if (emp != null) {
            emp.setSalary(newSalary);
            return true;
        }
        return false;
    }

    /**
     * Returns the total number of employees currently in the directory.
     */
    public int size() {
        return directory.size();
    }

    /**
     * Returns an unmodifiable collection of all employees.
     */
    public Collection<Employee> getAllEmployees() {
        return Collections.unmodifiableCollection(directory.values());
    }

    /**
     * Clears all employees from the directory.
     */
    public void clear() {
        directory.clear();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 2. FREQUENCY COUNT & AGGREGATION WITH Map.merge()
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Computes the headcount frequency for each department using {@link Map#merge(Object, Object, java.util.function.BiFunction)}.
     * <p>
     * Idiomatic modern Java:
     * {@code counts.merge(dept, 1, Integer::sum)}
     * If key is absent -> maps to 1.
     * If key is present -> combines old value and new value via Integer::sum.
     * </p>
     *
     * @return Map of department name to employee count
     */
    public Map<String, Integer> countByDepartmentUsingMerge() {
        Map<String, Integer> departmentCounts = new HashMap<>();
        for (Employee emp : directory.values()) {
            departmentCounts.merge(emp.getDepartment(), 1, Integer::sum);
        }
        return departmentCounts;
    }

    /**
     * Computes the frequency of each job role/designation using {@link Map#merge}.
     *
     * @return Map of role name to employee count
     */
    public Map<String, Integer> countByRoleUsingMerge() {
        Map<String, Integer> roleCounts = new HashMap<>();
        for (Employee emp : directory.values()) {
            roleCounts.merge(emp.getRole(), 1, Integer::sum);
        }
        return roleCounts;
    }

    /**
     * Aggregates total payroll per department using {@link Map#merge}.
     *
     * @return Map of department name to total payroll expenditure
     */
    public Map<String, Double> aggregatePayrollByDepartmentUsingMerge() {
        Map<String, Double> departmentPayroll = new HashMap<>();
        for (Employee emp : directory.values()) {
            departmentPayroll.merge(emp.getDepartment(), emp.getSalary(), Double::sum);
        }
        return departmentPayroll;
    }

    /**
     * Educational comparison: Demonstrates the pre-Java 8 verbose way of counting.
     * Used in benchmarks and documentation to highlight why merge() is superior.
     */
    public Map<String, Integer> countByDepartmentOldWay() {
        Map<String, Integer> departmentCounts = new HashMap<>();
        for (Employee emp : directory.values()) {
            String dept = emp.getDepartment();
            if (!departmentCounts.containsKey(dept)) {
                departmentCounts.put(dept, 1);
            } else {
                departmentCounts.put(dept, departmentCounts.get(dept) + 1);
            }
        }
        return departmentCounts;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 3. TREEMAP DEPARTMENT REPORT (NavigableMap)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Generates a department breakdown report sorted alphabetically by department name.
     * Uses {@link TreeMap} to maintain keys in sorted order with O(log N) lookup/insertion.
     *
     * @return NavigableMap with department names as keys and DepartmentAnalytics as values
     */
    public NavigableMap<String, DepartmentAnalytics> generateDepartmentReport() {
        NavigableMap<String, DepartmentAnalytics> report = new TreeMap<>();

        for (Employee emp : directory.values()) {
            // computeIfAbsent ensures a DepartmentAnalytics container is created on first encounter
            report.computeIfAbsent(emp.getDepartment(), DepartmentAnalytics::new)
                  .addEmployee(emp);
        }

        return report;
    }

    /**
     * Returns the alphabetically first department in the report.
     */
    public Optional<String> getFirstDepartmentName() {
        NavigableMap<String, DepartmentAnalytics> report = generateDepartmentReport();
        return report.isEmpty() ? Optional.empty() : Optional.of(report.firstKey());
    }

    /**
     * Returns the alphabetically last department in the report.
     */
    public Optional<String> getLastDepartmentName() {
        NavigableMap<String, DepartmentAnalytics> report = generateDepartmentReport();
        return report.isEmpty() ? Optional.empty() : Optional.of(report.lastKey());
    }

    /**
     * Returns a sub-range view of the department report using {@link NavigableMap#subMap}.
     */
    public NavigableMap<String, DepartmentAnalytics> getDepartmentSubRange(
            String fromDept, boolean fromInclusive,
            String toDept, boolean toInclusive) {
        NavigableMap<String, DepartmentAnalytics> report = generateDepartmentReport();
        return report.subMap(fromDept, fromInclusive, toDept, toInclusive);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 4. COMPARABLE BY ID (Natural Ordering)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Returns all employees sorted by their natural ordering (Employee ID ascending).
     * Demonstrates {@link Comparable#compareTo(Object)}.
     */
    public List<Employee> getEmployeesSortedByIdNatural() {
        List<Employee> list = new ArrayList<>(directory.values());
        Collections.sort(list); // Uses Employee.compareTo
        return list;
    }

    /**
     * Returns a {@link TreeSet} containing all employees.
     * Because Employee implements Comparable, TreeSet automatically organizes elements
     * in natural order without requiring an external Comparator.
     */
    public TreeSet<Employee> getEmployeeTreeSet() {
        return new TreeSet<>(directory.values());
    }

    /**
     * Performs a binary search for an employee by ID on a naturally sorted list.
     * Requires the list to be sorted according to natural ordering.
     *
     * @param sortedList list sorted by natural ordering
     * @param targetId   employee ID to search for
     * @return index in list if found, or negative insertion point
     */
    public int binarySearchById(List<Employee> sortedList, int targetId) {
        // Construct a probe employee with target ID
        Employee probe = new Employee(targetId, "Probe", "Dept", "Role", 0.0, 0);
        return Collections.binarySearch(sortedList, probe);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 5. TOP-5 SALARY COMPARATOR CHAIN
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Retrieves the top N highest-earning employees using the chained Comparator.
     * <p>
     * Employs:
     * {@code TOP_SALARY_COMPARATOR_CHAIN}
     * Ranking: Salary DESC -> Name ASC -> Department ASC -> ID ASC
     * </p>
     *
     * @param limit maximum number of top earners to return (e.g. 5)
     * @return list of top earners
     */
    public List<Employee> getTopEarners(int limit) {
        if (limit <= 0) return Collections.emptyList();

        return directory.values().stream()
                .sorted(TOP_SALARY_COMPARATOR_CHAIN)
                .limit(limit)
                .toList();
    }

    /**
     * Alternative Top-K implementation using a bounded Min-Heap (PriorityQueue).
     * Time complexity: O(N log K) instead of O(N log N).
     * Ideal for massive enterprise directories where K << N.
     *
     * @param limit K top earners
     * @return list of top earners sorted in descending rank
     */
    public List<Employee> getTopEarnersUsingPriorityQueue(int limit) {
        if (limit <= 0) return Collections.emptyList();

        // Min-heap ordering: opposite of TOP_SALARY_COMPARATOR_CHAIN so the lowest of the top-K is at head
        Comparator<Employee> minHeapComparator = TOP_SALARY_COMPARATOR_CHAIN.reversed();
        PriorityQueue<Employee> minHeap = new PriorityQueue<>(limit, minHeapComparator);

        for (Employee emp : directory.values()) {
            if (minHeap.size() < limit) {
                minHeap.offer(emp);
            } else if (TOP_SALARY_COMPARATOR_CHAIN.compare(emp, minHeap.peek()) < 0) {
                // emp ranks higher than the current minimum in our top-K heap
                minHeap.poll();
                minHeap.offer(emp);
            }
        }

        // Drain heap and sort in correct descending order
        List<Employee> result = new ArrayList<>(minHeap);
        result.sort(TOP_SALARY_COMPARATOR_CHAIN);
        return result;
    }
}
