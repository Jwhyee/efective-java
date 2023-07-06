package item1;

import common.MemoryScan;

import java.math.BigInteger;
import java.util.Random;

public class StaticFactoryMethod {

    public static void main(String[] args) {
        MemoryScan.startScan();

        // static method
        int sum = StaticCalcImpl.sum(10, 20);

        // instance method
        StaticCalcImpl c = new StaticCalcImpl();
        double volume = c.sphereVolume(3.0);

        Boolean valueOf = Boolean.valueOf(true);
        System.out.println(valueOf);

        BigInteger bigInteger1 = new BigInteger(5, 10, new Random());
        BigInteger bigInteger2 = BigInteger.probablePrime(5, new Random());
        System.out.println("bigInteger1 = " + bigInteger1);
        System.out.println("bigInteger2 = " + bigInteger2);

        MemoryScan.endScan();
    }

}
