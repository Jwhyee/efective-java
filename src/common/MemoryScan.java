package common;

public final class MemoryScan {
    public static void startScan() {
        Runtime.getRuntime().gc();
    }

    public static void endScan() {
        System.out.print(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() + " bytes");
    }
}
