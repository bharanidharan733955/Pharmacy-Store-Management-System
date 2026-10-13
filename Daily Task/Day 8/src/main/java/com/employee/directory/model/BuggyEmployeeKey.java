package com.employee.directory.model;

import java.util.Objects;

/**
 * Demonstrates the classic "Missing hashCode" bug.
 * <p>
 * This class overrides {@link #equals(Object)} based on employee ID and department code,
 * but deliberately OMITS overriding {@link #hashCode()}.
 * As a result, it inherits {@link Object#hashCode()}, which yields identity hash codes
 * (typically derived from memory location / object header).
 * </p>
 * <p>
 * Consequence: Two distinct instances with identical fields evaluate to true via {@code equals()},
 * but produce DIFFERENT hash codes, violating the Java Object contract and breaking
 * {@link java.util.HashMap} and {@link java.util.HashSet}.
 * </p>
 */
public class BuggyEmployeeKey {

    private final int employeeId;
    private final String departmentCode;

    public BuggyEmployeeKey(int employeeId, String departmentCode) {
        this.employeeId = employeeId;
        this.departmentCode = departmentCode != null ? departmentCode.trim().toUpperCase() : "";
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    /**
     * Correctly defines logical equivalence.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuggyEmployeeKey that = (BuggyEmployeeKey) o;
        return employeeId == that.employeeId && Objects.equals(departmentCode, that.departmentCode);
    }

    // NOTE: hashCode() is DELIBERATELY NOT OVERRIDDEN here!
    // Inherits java.lang.Object.hashCode() -> system identity hash code.

    @Override
    public String toString() {
        return String.format("BuggyEmployeeKey[id=%d, dept='%s', identityHash=%d]",
                employeeId, departmentCode, System.identityHashCode(this));
    }
}
