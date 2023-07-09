package chapter2.item5;

import java.util.ArrayList;
import java.util.List;

public class Lexicon {
    private final String language;
    private static final List<String> dictionary = new ArrayList<>();
    Lexicon(String language) {
        this.language = language;
        wordInit();
    }

    private void wordInit() {
        dictionary.add("word");
        dictionary.add("hello");
    }
    public boolean findWord(String str) {
        return dictionary.contains(str);
    }
}
