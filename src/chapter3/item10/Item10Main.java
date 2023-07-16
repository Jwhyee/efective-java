package chapter3.item10;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Item10Main {

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
    }

    @Test
    void collectionContainsTest() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Holland");
        String s = "holland";

        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);

        assertTrue(list.contains(s));
    }
}
