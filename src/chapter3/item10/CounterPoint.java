package chapter3.item10;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterPoint extends Point{
    private static final AtomicInteger counter = new AtomicInteger();

    public static int numberCreated() {
        return counter.get();
    }

    public CounterPoint(int x, int y) {
        super(x, y);
        counter.incrementAndGet();
    }

}
