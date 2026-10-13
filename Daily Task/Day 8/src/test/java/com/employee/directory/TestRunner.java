package com.employee.directory;

import com.employee.directory.service.EmployeeDirectoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone test runner for Day 8 Employee Directory tests.
 * Discovers and executes all @Nested test classes and @Test methods via reflection
 * without requiring the Maven CLI.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   RUNNING DAY 8 UNIT TESTS: EMPLOYEE DIRECTORY & OBJECT CONTRACT LAB         ");
        System.out.println("================================================================================\n");

        int totalPassed = 0;
        int totalFailed = 0;

        Class<?> outerClass = EmployeeDirectoryTest.class;

        // Iterate over nested test groups
        for (Class<?> nestedClass : outerClass.getDeclaredClasses()) {
            if (!nestedClass.isAnnotationPresent(Nested.class)) continue;

            String groupName = nestedClass.getSimpleName();
            if (nestedClass.isAnnotationPresent(DisplayName.class)) {
                groupName = nestedClass.getAnnotation(DisplayName.class).value();
            }
            System.out.println("-- " + groupName + " --");

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

            // Sort tests by method name for deterministic output order
            testMethods.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));

            for (Method testMethod : testMethods) {
                String testName = testMethod.getName();
                if (testMethod.isAnnotationPresent(DisplayName.class)) {
                    testName = testMethod.getAnnotation(DisplayName.class).value();
                }

                try {
                    // Nested classes require an enclosing outer instance
                    Object outerInstance = outerClass.getDeclaredConstructor().newInstance();
                    var nestedCtor = nestedClass.getDeclaredConstructor(outerClass);
                    nestedCtor.setAccessible(true);
                    Object nestedInstance = nestedCtor.newInstance(outerInstance);

                    // Execute @BeforeEach on outer class if any
                    for (Method m : outerClass.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(BeforeEach.class)) {
                            m.setAccessible(true);
                            m.invoke(outerInstance);
                        }
                    }

                    // Execute @BeforeEach on nested class if present
                    if (beforeEachMethod != null) {
                        beforeEachMethod.setAccessible(true);
                        beforeEachMethod.invoke(nestedInstance);
                    }

                    // Execute test method
                    testMethod.setAccessible(true);
                    testMethod.invoke(nestedInstance);
                    System.out.printf("   [PASSED] %s%n", testName);
                    totalPassed++;
                } catch (Throwable t) {
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    System.err.printf("   [FAILED] %s%n", testName);
                    cause.printStackTrace(System.err);
                    totalFailed++;
                }
            }
            System.out.println();
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Test Execution Summary: %d Passed, %d Failed (Total: %d)%n",
                totalPassed, totalFailed, totalPassed + totalFailed);
        System.out.println("--------------------------------------------------------------------------------");

        if (totalFailed > 0) {
            System.exit(1);
        }
    }
}
