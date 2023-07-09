package test;

import chapter2.item3.Singleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void instanceTest() {
        // Item3 싱글톤 테스트
        Singleton s = Singleton.INSTANCE;
        int status = s.getStatus();
        System.out.println(status);
        assertEquals(200, status);
    }

}