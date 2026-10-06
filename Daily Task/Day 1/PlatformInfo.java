/**
 * PlatformInfo.java - JVM & Operating System Diagnostic Utility
 * 
 * Demonstrates:
 * 1. Java System properties retrieval
 * 2. Runtime environment heap memory analysis
 * 3. Formatted terminal output
 */
public class PlatformInfo {

    private static final double MB_CONVERSION = 1024.0 * 1024.0;
    private static final double GB_CONVERSION = 1024.0 * 1024.0 * 1024.0;

    public static void main(String[] args) {

        // Retrieve Runtime Instance
        Runtime runtime = Runtime.getRuntime();

        // Memory calculations
        long maxMemoryBytes = runtime.maxMemory();
        long totalMemoryBytes = runtime.totalMemory();
        long freeMemoryBytes = runtime.freeMemory();
        long usedMemoryBytes = totalMemoryBytes - freeMemoryBytes;

        System.out.println("==========================================================================");
        System.out.println("                 JAVA PLATFORM & JVM RUNTIME DIAGNOSTIC                   ");
        System.out.println("==========================================================================");

        // 1. Java Environment Information
        System.out.println("\n☕ --- JAVA ENVIRONMENT ---");
        System.out.printf("%-22s : %s%n", "Java Version", System.getProperty("java.version"));
        System.out.printf("%-22s : %s%n", "Java Vendor", System.getProperty("java.vendor"));
        System.out.printf("%-22s : %s%n", "Java Home Directory", System.getProperty("java.home"));
        System.out.printf("%-22s : %s%n", "JVM Name", System.getProperty("java.vm.name"));
        System.out.printf("%-22s : %s%n", "JVM Specification", System.getProperty("java.vm.specification.version"));

        // 2. Operating System Details
        System.out.println("\n💻 --- OPERATING SYSTEM ---");
        System.out.printf("%-22s : %s%n", "OS Name", System.getProperty("os.name"));
        System.out.printf("%-22s : %s%n", "OS Architecture", System.getProperty("os.arch"));
        System.out.printf("%-22s : %s%n", "OS Version", System.getProperty("os.version"));
        System.out.printf("%-22s : %d cores%n", "Available Processors", runtime.availableProcessors());

        // 3. JVM Memory Allocation (Bytes & Human Readable MB/GB)
        System.out.println("\n⚡ --- JVM HEAP MEMORY METRICS ---");
        System.out.printf("%-22s : %,d bytes (%.2f MB / %.2f GB)%n", 
                "Max Heap Memory", maxMemoryBytes, maxMemoryBytes / MB_CONVERSION, maxMemoryBytes / GB_CONVERSION);
        System.out.printf("%-22s : %,d bytes (%.2f MB)%n", 
                "Total Allocated Heap", totalMemoryBytes, totalMemoryBytes / MB_CONVERSION);
        System.out.printf("%-22s : %,d bytes (%.2f MB)%n", 
                "Used Memory", usedMemoryBytes, usedMemoryBytes / MB_CONVERSION);
        System.out.printf("%-22s : %,d bytes (%.2f MB)%n", 
                "Free Heap Memory", freeMemoryBytes, freeMemoryBytes / MB_CONVERSION);

        System.out.println("==========================================================================");
    }
}
