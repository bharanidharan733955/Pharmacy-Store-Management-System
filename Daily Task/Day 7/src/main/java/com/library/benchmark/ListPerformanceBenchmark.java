package com.library.benchmark;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Performance benchmark comparing ArrayList vs LinkedList for:
 * <ol>
 *   <li><b>Head inserts</b> (add at index 0): LinkedList O(1) vs ArrayList O(n) shifting.</li>
 *   <li><b>Tail appends</b> (add at end): Both amortized O(1), but ArrayList has better cache locality.</li>
 *   <li><b>Middle inserts</b> (add at size/2): ArrayList shifts half, LinkedList traverses half.</li>
 *   <li><b>Random get(i)</b>: ArrayList O(1) random access vs LinkedList O(n) traversal.</li>
 * </ol>
 *
 * <p>Key observations:
 * <ul>
 *   <li>ArrayList is faster for most workloads due to CPU cache line locality (contiguous memory).</li>
 *   <li>LinkedList only wins for heavy head/middle insertions when the list is already very large.</li>
 *   <li>For random access (get(i)), ArrayList is <b>dramatically</b> faster — O(1) vs O(n).</li>
 * </ul>
 */
public class ListPerformanceBenchmark {

    public record BenchmarkResult(String operation, String listType, int elementCount,
                                  long durationNanos, double durationMs) {
        @Override
        public String toString() {
            return String.format("%-22s | %-12s | %,10d elements | %,15d ns | %10.3f ms",
                    operation, listType, elementCount, durationNanos, durationMs);
        }
    }

    /**
     * Runs the full benchmark suite and returns structured results.
     */
    public List<BenchmarkResult> runFullBenchmark(int elementCount) {
        List<BenchmarkResult> results = new ArrayList<>();

        // Warm up the JVM JIT compiler
        warmUp();

        // ── 1. TAIL APPENDS (add to end) ──
        results.add(benchmarkTailAppend("ArrayList", new ArrayList<>(), elementCount));
        results.add(benchmarkTailAppend("LinkedList", new LinkedList<>(), elementCount));

        // ── 2. HEAD INSERTS (add at index 0) ──
        results.add(benchmarkHeadInsert("ArrayList", new ArrayList<>(), elementCount));
        results.add(benchmarkHeadInsert("LinkedList", new LinkedList<>(), elementCount));

        // ── 3. MIDDLE INSERTS (add at size/2) ──
        results.add(benchmarkMiddleInsert("ArrayList", new ArrayList<>(), elementCount));
        results.add(benchmarkMiddleInsert("LinkedList", new LinkedList<>(), elementCount));

        // ── 4. RANDOM ACCESS get(i) ──
        ArrayList<Integer> prefilledArrayList = new ArrayList<>();
        LinkedList<Integer> prefilledLinkedList = new LinkedList<>();
        for (int i = 0; i < elementCount; i++) {
            prefilledArrayList.add(i);
            prefilledLinkedList.add(i);
        }
        results.add(benchmarkRandomGet("ArrayList", prefilledArrayList, elementCount));
        results.add(benchmarkRandomGet("LinkedList", prefilledLinkedList, elementCount));

        return results;
    }

    private BenchmarkResult benchmarkTailAppend(String listType, List<Integer> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(i); // append to tail
        }
        long elapsed = System.nanoTime() - start;
        return new BenchmarkResult("Tail Append", listType, count, elapsed, elapsed / 1_000_000.0);
    }

    private BenchmarkResult benchmarkHeadInsert(String listType, List<Integer> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(0, i); // insert at head — ArrayList must shift all elements
        }
        long elapsed = System.nanoTime() - start;
        return new BenchmarkResult("Head Insert (idx 0)", listType, count, elapsed, elapsed / 1_000_000.0);
    }

    private BenchmarkResult benchmarkMiddleInsert(String listType, List<Integer> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(list.size() / 2, i); // insert at middle
        }
        long elapsed = System.nanoTime() - start;
        return new BenchmarkResult("Middle Insert (n/2)", listType, count, elapsed, elapsed / 1_000_000.0);
    }

    private BenchmarkResult benchmarkRandomGet(String listType, List<Integer> list, int count) {
        int accessCount = Math.min(count, 50_000); // cap random access iterations
        long start = System.nanoTime();
        for (int i = 0; i < accessCount; i++) {
            int index = (i * 7 + 13) % list.size(); // pseudo-random scatter pattern
            list.get(index);
        }
        long elapsed = System.nanoTime() - start;
        return new BenchmarkResult("Random get(i)", listType, accessCount, elapsed, elapsed / 1_000_000.0);
    }

    /**
     * Warms up the JIT compiler to avoid measuring compilation overhead.
     */
    private void warmUp() {
        List<Integer> warmList = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            warmList.add(i);
        }
        for (int i = 0; i < 10_000; i++) {
            warmList.get(i);
        }
        warmList.clear();
    }

    /**
     * Prints a formatted comparison report of all benchmark results.
     */
    public static void printReport(List<BenchmarkResult> results) {
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│               ArrayList vs LinkedList — Performance Benchmark Results               │");
        System.out.println("├────────────────────────┬──────────────┬───────────────────┬─────────────────┬────────┤");
        System.out.printf("│ %-22s │ %-12s │ %17s │ %15s │ %6s │%n",
                "Operation", "List Type", "Elements", "Duration (ns)", "ms");
        System.out.println("├────────────────────────┼──────────────┼───────────────────┼─────────────────┼────────┤");

        for (BenchmarkResult r : results) {
            System.out.printf("│ %-22s │ %-12s │ %,17d │ %,15d │ %6.1f │%n",
                    r.operation(), r.listType(), r.elementCount(), r.durationNanos(), r.durationMs());
        }

        System.out.println("└────────────────────────┴──────────────┴───────────────────┴─────────────────┴────────┘");
    }

    /**
     * Prints analysis commentary based on actual results.
     */
    public static void printAnalysis(List<BenchmarkResult> results) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                          Performance Analysis & Observations                        │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────────────┘");
        System.out.println();

        // Group results by operation for comparison
        for (int i = 0; i < results.size(); i += 2) {
            BenchmarkResult arrayListResult = results.get(i);
            BenchmarkResult linkedListResult = results.get(i + 1);

            String winner;
            double ratio;
            if (arrayListResult.durationNanos() < linkedListResult.durationNanos()) {
                ratio = (double) linkedListResult.durationNanos() / arrayListResult.durationNanos();
                winner = "ArrayList";
            } else {
                ratio = (double) arrayListResult.durationNanos() / linkedListResult.durationNanos();
                winner = "LinkedList";
            }

            System.out.printf("  ▸ %s: %s wins by %.1fx%n", arrayListResult.operation(), winner, ratio);
        }

        System.out.println();
        System.out.println("  KEY TAKEAWAYS:");
        System.out.println("  ─────────────");
        System.out.println("  • Tail Append: Both are amortized O(1). ArrayList often wins due to contiguous");
        System.out.println("    memory layout giving better CPU cache hit rates.");
        System.out.println("  • Head Insert: LinkedList is theoretically O(1), but ArrayList's System.arraycopy");
        System.out.println("    is heavily optimized at the hardware level, making it competitive for small N.");
        System.out.println("  • Middle Insert: Both perform traversal/shifting. LinkedList must traverse to");
        System.out.println("    the midpoint first. ArrayList must shift elements via arraycopy.");
        System.out.println("  • Random get(i): ArrayList is O(1) direct array index. LinkedList is O(n)");
        System.out.println("    sequential traversal — the performance gap grows dramatically with size.");
        System.out.println();
        System.out.println("  RECOMMENDATION: Use ArrayList as your default List implementation.");
        System.out.println("  Only use LinkedList when you have proven profiling data showing");
        System.out.println("  frequent head/middle insertions with very large lists (N > 100K).");
    }
}
