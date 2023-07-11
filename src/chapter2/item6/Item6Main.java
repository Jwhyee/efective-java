package chapter2.item6;

import common.ScanUtility;

import java.util.HashMap;
import java.util.Map;

public class Item6Main {
    public static long prefixSum() {
        long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }
        return sum;
    }
    public static void main(String[] args) {
        System.out.println(RomanNumerals.isRomanNumeral("XI"));

        ScanUtility s = new ScanUtility();
        ScanUtility.startMemoryScan();
        s.startTimeScan();
        System.out.println(prefixSum());
        s.endTimeScan();
        ScanUtility.endMemoryScan();
    }
}
