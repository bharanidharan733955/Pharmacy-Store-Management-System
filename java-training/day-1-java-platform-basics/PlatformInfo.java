public class PlatformInfo {

    public static void main(String[] args) {

        System.out.println("===== Java Platform Information =====");
        System.out.println("Java Version    : " + System.getProperty("java.version"));
        System.out.println("OS Name         : " + System.getProperty("os.name"));
        System.out.println("Processors      : " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max Heap        : " + Runtime.getRuntime().maxMemory() + " bytes");
        System.out.println("Free Heap       : " + Runtime.getRuntime().freeMemory() + " bytes");
        System.out.println("====================================");
    }
}
