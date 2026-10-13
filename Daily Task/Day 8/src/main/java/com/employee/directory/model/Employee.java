package com.employee.directory.model;

import java.util.Objects;

/**
 * Domain model representing an Employee in the directory.
 * Implements {@link Comparable} to provide a natural ordering based on employee ID.
 */
public class Employee implements Comparable<Employee> {

    private final int id;
    private String name;
    private String department;
    private String role;
    private double salary;
    private int yearsOfExperience;

    public Employee(int id, String name, String department, String role, double salary, int yearsOfExperience) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive: " + id);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department cannot be null or blank");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative: " + salary);
        }
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative: " + yearsOfExperience);
        }

        this.id = id;
        this.name = name.trim();
        this.department = department.trim();
        this.role = role.trim();
        this.salary = salary;
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
        this.name = name.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department cannot be null or blank");
        }
        this.department = department.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }
        this.role = role.trim();
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative: " + salary);
        }
        this.salary = salary;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative: " + yearsOfExperience);
        }
        this.yearsOfExperience = yearsOfExperience;
    }

    /**
     * Natural ordering for Employee is defined by employee ID in ascending order.
     * Complies strictly with the Comparable contract:
     * - sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
     * - Transitive: (x.compareTo(y) > 0 && y.compareTo(z) > 0) implies x.compareTo(z) > 0
     * - Consistent with equals: (x.compareTo(y) == 0) iff x.equals(y)
     */
    @Override
    public int compareTo(Employee other) {
        if (other == null) {
            return 1;
        }
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Employee[ID=%-4d | %-16s | Dept: %-15s | Role: %-18s | Salary: $%,10.2f | Exp: %2dyrs]",
                id, name, department, role, salary, yearsOfExperience);
    }
}
