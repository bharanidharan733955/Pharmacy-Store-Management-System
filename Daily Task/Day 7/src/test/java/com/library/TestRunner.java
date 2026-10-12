package com.library;

import com.library.service.LibraryCollectionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone test runner for Day 7 Library Collections tests.
 * Executes all nested test classes without requiring Maven CLI.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════════════════════════");
        System.out.println("   RUNNING DAY 7 UNIT TESTS: LIBRARY WAITLIST COLLECTIONS LAB                 ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════\n");

        int totalPassed = 0;
        int totalFailed = 0;

        Class<?> outerClass = LibraryCollectionsTest.class;

        // Iterate over nested test classes (@Nested)
        for (Class<?> nestedClass : outerClass.getDeclaredClasses()) {
            if (!nestedClass.isAnnotationPresent(Nested.class)) continue;

            String groupName = nestedClass.getSimpleName();
            if (nestedClass.isAnnotationPresent(DisplayName.class)) {
                groupName = nestedClass.getAnnotation(DisplayName.class).value();
            }
            System.out.println("── " + groupName + " ──");

            List<Method> testMethods = new ArrayList<>();
            Method beforeEachMethod = null;

            for (Method method : nestedClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(BeforeEach.class)) {
                    beforeEachMethod = method;
                }
                if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                }
            }

            for (Method testMethod : testMethods) {
                String testName = testMethod.getName();
                if (testMethod.isAnnotationPresent(DisplayName.class)) {
                    testName = testMethod.getAnnotation(DisplayName.class).value();
                }

                try {
                    // Nested classes require an enclosing instance
                    Object outerInstance = outerClass.getDeclaredConstructor().newInstance();
                    var nestedCtor = nestedClass.getDeclaredConstructor(outerClass);
                    nestedCtor.setAccessible(true);
                    Object nestedInstance = nestedCtor.newInstance(outerInstance);

                    if (beforeEachMethod != null) {
                        beforeEachMethod.setAccessible(true);
                        beforeEachMethod.invoke(nestedInstance);
                    }
                    testMethod.setAccessible(true);
                    testMethod.invoke(nestedInstance);
                    System.out.printf("    PASSED: %s%n", testName);
                    totalPassed++;
                } catch (Throwable t) {
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    // Check if it's an expected assertion (assertThrows wrapping)
                    System.err.printf("  ❌ FAILED: %s%n", testName);
                    cause.printStackTrace(System.err);
                    totalFailed++;
                }
            }
            System.out.println();
        }

        System.out.println("────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("Test Execution Summary: %d Passed, %d Failed (Total: %d)%n",
                totalPassed, totalFailed, totalPassed + totalFailed);
        System.out.println("────────────────────────────────────────────────────────────────────────────────");

        if (totalFailed > 0) {
            System.exit(1);
        }
    }
}
