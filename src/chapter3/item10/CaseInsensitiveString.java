package chapter3.item10;

import java.util.Objects;

public class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        /*if (o instanceof CaseInsensitiveString) {
            return s.equalsIgnoreCase(
                    ((CaseInsensitiveString) o).s
            );
        }
        if (o instanceof String) {
            return s.equalsIgnoreCase((String) o);
        }
        return false;
        */

        return o instanceof CaseInsensitiveString &&
                ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
    }

    @Override
    public String toString() {
        return s;
    }
}