package com.pharmacy.order;

import com.pharmacy.order.service.OrderProcessorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone runner for Day 6 JUnit 5 tests.
 * Runs tests directly without requiring maven CLI installed.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   RUNNING DAY 6 UNIT TESTS: ORDER PROCESSOR EXCEPTION HANDLING LAB             ");
        System.out.println("================================================================================\n");

        Class<?> testClass = OrderProcessorTest.class;
        List<Method> testMethods = new ArrayList<>();
        Method beforeEachMethod = null;

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeEach.class)) {
                beforeEachMethod = method;
            }
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }

        int passed = 0;
        int failed = 0;

        for (Method testMethod : testMethods) {
            String testName = testMethod.getName();
            if (testMethod.isAnnotationPresent(DisplayName.class)) {
                testName = testMethod.getAnnotation(DisplayName.class).value();
            }

            try {
                Object instance = testClass.getDeclaredConstructor().newInstance();
                if (beforeEachMethod != null) {
                    beforeEachMethod.setAccessible(true);
                    beforeEachMethod.invoke(instance);
                }
                testMethod.setAccessible(true);
                testMethod.invoke(instance);
                System.out.printf("  PASSED: %s%n", testName);
                passed++;
            } catch (Throwable t) {
                System.err.printf("❌ FAILED: %s%n", testName);
                Throwable cause = t.getCause() != null ? t.getCause() : t;
                cause.printStackTrace(System.err);
                failed++;
            }
        }

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("Test Execution Summary: %d Passed, %d Failed (Total: %d)%n", passed, failed, passed + failed);
        System.out.println("--------------------------------------------------------------------------------");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
