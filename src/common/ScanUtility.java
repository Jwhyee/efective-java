package common;

public final class ScanUtility {
    private long start;
    private long end;
    public static void startMemoryScan() {
        Runtime.getRuntime().gc();
    }

    public void startTimeScan() {
        start = System.currentTimeMillis();
    }

    public static void endMemoryScan() {

        System.out.print(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() + " bytes");
    }

    public void endTimeScan() {
        end = System.currentTimeMillis();
        System.out.println("실행 시간 : " + (end - start)/1000.0);
    }
}
