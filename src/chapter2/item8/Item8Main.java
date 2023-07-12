package chapter2.item8;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Item8Main {

    @Test
    @DisplayName("객체 소멸 테스트")
    void finalizerTest() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            FinalizerTester t = new FinalizerTester(i);
        }
        System.gc();

        Thread.sleep(2000);
    }

    @Test
    @DisplayName("Cleaner 객체 소멸 테스트1")
    void cleanerTest1() {
        try (Room myRoom = new Room(7)) {
            System.out.println("청소 ㄱ");
        }
    }

    @Test
    @DisplayName("Cleaner 객체 소멸 테스트2")
    void cleanerTest2() {
        new Room(99);
        System.out.println("청소 ㄱ");
        System.gc();
    }

    @Test
    @DisplayName("Cleaner 객체 소멸 테스트3")
    void cleanerTest3() {
        new Room(99);
        System.out.println("청소 ㄱ");
        System.gc();
    }
}
