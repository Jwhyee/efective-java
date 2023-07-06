package item1;

public class StaticCalcImpl implements StaticCalc {
    static final double PI = 3.14;
    String startMsg = "계산 시작";
    static String endMsg = "계산 종료";

    public static int sum(int a, int b) {
        // System.out.println(msg);
        return a + b;
    }

    public double sphereVolume(double r) {
        System.out.println(endMsg);
        return (4 * PI * r * r * r) / 3;
    }
}
