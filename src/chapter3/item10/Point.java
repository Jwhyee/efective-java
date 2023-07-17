package chapter3.item10;

import java.util.Set;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private static final Set<Point> unitCircle = Set.of(
            new Point(1, 0), new Point(0, 1),
            new Point(-1, 0), new Point(0, -1)
    );

    public static boolean onUnitCircle(Point p) {
        return unitCircle.contains(p);
    }

    /*@Override
    public boolean equals(Object o) {
        if (!(o instanceof Point point)) {
            System.out.println("test4");
            return false;
        }

        System.out.println("test5");
        return x == point.x && y == point.y;
    }*/

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }

}
