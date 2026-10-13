package com.employee.directory.model;

import java.util.Objects;

/**
 * Fixed implementation of the employee key that adheres strictly to the
 * {@link Object#equals(Object)} and {@link Object#hashCode()} contract.
 * <p>
 * Contract rule: If {@code a.equals(b)} is true, then {@code a.hashCode() == b.hashCode()}
 * MUST also be true.
 * </p>
 */
public class FixedEmployeeKey {

    private final int employeeId;
    private final String departmentCode;

    public FixedEmployeeKey(int employeeId, String departmentCode) {
        this.employeeId = employeeId;
        this.departmentCode = departmentCode != null ? departmentCode.trim().toUpperCase() : "";
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedEmployeeKey that = (FixedEmployeeKey) o;
        return employeeId == that.employeeId && Objects.equals(departmentCode, that.departmentCode);
    }

    /**
     * Fixed hashCode implementation using {@link Objects#hash(Object...)}.
     * Guarantees that equal keys produce identical hash codes.
     */
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, departmentCode);
    }

    @Override
    public String toString() {
        return String.format("FixedEmployeeKey[id=%d, dept='%s', hashCode=%d]",
                employeeId, departmentCode, hashCode());
    }
}
