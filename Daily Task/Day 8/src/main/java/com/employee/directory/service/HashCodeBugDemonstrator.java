package com.employee.directory.service;

import com.employee.directory.model.BuggyEmployeeKey;
import com.employee.directory.model.FixedEmployeeKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service demonstrating the consequences of violating the equals/hashCode contract,
 * dissecting the missing hashCode bug at the hash bucket level, and proving the fix.
 */
public class HashCodeBugDemonstrator {

    /**
     * Diagnostic report holding the autopsy results of a missing hashCode bug test.
     */
    public record BugDiagnosis(
            boolean keysAreEqual,
            int key1HashCode,
            int key2HashCode,
            boolean hashCodesMatch,
            int simulatedBucketKey1,
            int simulatedBucketKey2,
            String mapPutValue,
            String mapGetValueWithKey2,
            int setSizeAfterAddingBoth,
            boolean isBugDemonstrated
    ) {}

    /**
     * Diagnostic report holding the resolution results of the fixed hashCode test.
     */
    public record FixDiagnosis(
            boolean keysAreEqual,
            int key1HashCode,
            int key2HashCode,
            boolean hashCodesMatch,
            int simulatedBucketKey1,
            int simulatedBucketKey2,
            String mapPutValue,
            String mapGetValueWithKey2,
            int setSizeAfterAddingBoth,
            boolean isFixConfirmed
    ) {}

    /**
     * Simulates standard HashMap bucket indexing using table capacity n = 16.
     * Index formula: (n - 1) & (h ^ (h >>> 16))
     */
    public static int calculateBucketIndex(int hashCode, int capacity) {
        int h = hashCode ^ (hashCode >>> 16);
        return (capacity - 1) & h;
    }

    /**
     * Runs the Missing-hashCode bug demonstration using {@link BuggyEmployeeKey}.
     *
     * @return structured diagnosis of the failure
     */
    public BugDiagnosis demonstrateMissingHashCodeBug() {
        int empId = 101;
        String dept = "PHARMACY";

        BuggyEmployeeKey key1 = new BuggyEmployeeKey(empId, dept);
        BuggyEmployeeKey key2 = new BuggyEmployeeKey(empId, dept);

        boolean areEqual = key1.equals(key2);
        int hash1 = key1.hashCode();
        int hash2 = key2.hashCode();
        boolean hashesMatch = (hash1 == hash2);

        int bucket1 = calculateBucketIndex(hash1, 16);
        int bucket2 = calculateBucketIndex(hash2, 16);

        // 1. HashMap lookup failure demo
        Map<BuggyEmployeeKey, String> map = new HashMap<>();
        String payload = "Dr. Alice Morgan - Lead Pharmacist";
        map.put(key1, payload);

        // Even though key1.equals(key2) is TRUE, map.get(key2) returns NULL!
        String retrieved = map.get(key2);

        // 2. HashSet duplicate violation demo
        Set<BuggyEmployeeKey> set = new HashSet<>();
        set.add(key1);
        set.add(key2); // Should be rejected by a Set, but succeeds because of different hashCodes!

        boolean bugOccurred = areEqual && !hashesMatch && retrieved == null && set.size() == 2;

        return new BugDiagnosis(
                areEqual,
                hash1,
                hash2,
                hashesMatch,
                bucket1,
                bucket2,
                payload,
                retrieved,
                set.size(),
                bugOccurred
        );
    }

    /**
     * Runs the Fixed-hashCode verification using {@link FixedEmployeeKey}.
     *
     * @return structured diagnosis proving the contract is satisfied
     */
    public FixDiagnosis demonstrateFixedHashCode() {
        int empId = 101;
        String dept = "PHARMACY";

        FixedEmployeeKey key1 = new FixedEmployeeKey(empId, dept);
        FixedEmployeeKey key2 = new FixedEmployeeKey(empId, dept);

        boolean areEqual = key1.equals(key2);
        int hash1 = key1.hashCode();
        int hash2 = key2.hashCode();
        boolean hashesMatch = (hash1 == hash2);

        int bucket1 = calculateBucketIndex(hash1, 16);
        int bucket2 = calculateBucketIndex(hash2, 16);

        // 1. HashMap lookup success demo
        Map<FixedEmployeeKey, String> map = new HashMap<>();
        String payload = "Dr. Alice Morgan - Lead Pharmacist";
        map.put(key1, payload);

        // Since hashCodes match AND equals is true, lookup succeeds!
        String retrieved = map.get(key2);

        // Updating with equal key
        map.put(key2, "Dr. Alice Morgan - Promoted");

        // 2. HashSet duplicate rejection demo
        Set<FixedEmployeeKey> set = new HashSet<>();
        boolean addedFirst = set.add(key1);
        boolean addedSecond = set.add(key2); // Correctly returns false and rejects duplicate!

        boolean fixValid = areEqual && hashesMatch && retrieved != null
                && bucket1 == bucket2 && set.size() == 1 && !addedSecond && addedFirst;

        return new FixDiagnosis(
                areEqual,
                hash1,
                hash2,
                hashesMatch,
                bucket1,
                bucket2,
                payload,
                retrieved,
                set.size(),
                fixValid
        );
    }
}
