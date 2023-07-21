package chapter3.item10;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RelationTest {

    @Test
    void salesmanTest() {
        Salesman m = new Salesman("123", "Tom Holland");
    }

    @Test
    void collectionTest() {
        List<Person> personList = new ArrayList<>();
        Person p = new Person("010203-1111111", "Tom Holland");
        personList.add(p);
        Person p2 = new Person("010203-1111111", "Tom Holland");

        assertTrue(personList.contains(p));
    }

    @Test
    void caseInsensitiveCompareTest() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Holland");
        String s = "holland";

        assertTrue(cis.equals(s));
//        assertTrue(s.equals(cis));
    }

    @Test
    void collectionContainsTest() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Holland");
        String s = "holland";

        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);

        assertTrue(list.contains(s));
    }

    @Test
    void transitivityColorPointTest() {
        ColorPoint c1 = new ColorPoint(1, 2, new Color(255, 255, 0));
        ColorPoint c2 = new ColorPoint(1, 2, new Color(255, 255, 224));

        assertTrue(c1.equals(c2));
//        assertTrue(c2.equals(c1));
    }

    @Test
    void transitivityPointTest() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);

//        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
    }

    @Test
    void transitivityMixTest1() {
        Point p = new Point(1, 2);
        ColorPoint c = new ColorPoint(1, 2, new Color(255, 255, 0));

//        assertTrue(p.equals(c));
        assertTrue(c.equals(p));
    }

    @Test
    void transitivityMixTest2() {
        ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
        Point p2 = new Point(1, 2);
        ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

        // test2, 5
//        assertTrue(p1.equals(p2));

        // test5
//        assertTrue(p2.equals(p3));

        // test3, 5
        assertTrue(p1.equals(p3));
    }

    @Test
    void counterTest1() {
        Point p = new Point(0, 1);

        assertTrue(Point.onUnitCircle(p));
    }

    @Test
    void finalClassTest() {
        PostRecord p1 = new PostRecord(1, "title", "content");
        PostRecord p2 = new PostRecord(1, "title", "content");

        assertTrue(p1.equals(p2));
    }
}
