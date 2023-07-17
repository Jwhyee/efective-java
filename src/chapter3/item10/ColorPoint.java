package chapter3.item10;

import java.awt.Color;
import java.util.Objects;

public class ColorPoint {
    private final Point point;
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint cp)) {
            return false;
        }
        return cp.point.equals(point) && cp.color.equals(color);
    }

    /*@Override
    public boolean equals(Object o) {
        if(!(o instanceof ColorPoint))
            return false;
        return super.equals(o) && ((ColorPoint) o).color == color;
    }*/

    /*@Override
    public boolean equals(Object o) {
        // o가 Point면 false를 반환한다.
        if (!(o instanceof Point)) {
            System.out.println("test1");
            return false;
        }

        // o가 일반 Point면 색상을 무시하고 비교한다.
        if (!(o instanceof ColorPoint)) {
            System.out.println("test2");
            return o.equals(this);
        }
        System.out.println("test3");
        // o가 ColorPoint면 색상까지 비교한다.
        return super.equals(o) && ((ColorPoint) o).color == color;
    }*/
}
