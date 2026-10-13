package com.employee.directory.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Value model encapsulating aggregated department analytics for TreeMap reports.
 */
public class DepartmentAnalytics {

    private final String departmentName;
    private int headcount;
    private double totalPayroll;
    private double minSalary;
    private double maxSalary;
    private Employee highestEarner;
    private final List<Employee> members;

    public DepartmentAnalytics(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            throw new IllegalArgumentException("Department name cannot be null or blank");
        }
        this.departmentName = departmentName;
        this.headcount = 0;
        this.totalPayroll = 0.0;
        this.minSalary = Double.MAX_VALUE;
        this.maxSalary = Double.MIN_VALUE;
        this.highestEarner = null;
        this.members = new ArrayList<>();
    }

    /**
     * Adds an employee to this department aggregation and recalculates statistics.
     */
    public void addEmployee(Employee emp) {
        if (emp == null) return;
        members.add(emp);
        headcount++;
        totalPayroll += emp.getSalary();

        if (emp.getSalary() < minSalary) {
            minSalary = emp.getSalary();
        }
        if (emp.getSalary() > maxSalary) {
            maxSalary = emp.getSalary();
            highestEarner = emp;
        }
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getHeadcount() {
        return headcount;
    }

    public double getTotalPayroll() {
        return totalPayroll;
    }

    public double getAverageSalary() {
        return headcount == 0 ? 0.0 : totalPayroll / headcount;
    }

    public double getMinSalary() {
        return headcount == 0 ? 0.0 : minSalary;
    }

    public double getMaxSalary() {
        return headcount == 0 ? 0.0 : maxSalary;
    }

    public Employee getHighestEarner() {
        return highestEarner;
    }

    public List<Employee> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public String toString() {
        return String.format("%-20s | Headcount: %2d | Total Payroll: $%,11.2f | Avg Salary: $%,10.2f | Top Earner: %s ($%,.2f)",
                departmentName, headcount, totalPayroll, getAverageSalary(),
                (highestEarner != null ? highestEarner.getName() : "N/A"),
                (highestEarner != null ? highestEarner.getSalary() : 0.0));
    }
}
