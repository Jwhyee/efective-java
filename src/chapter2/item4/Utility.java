package chapter2.item4;

public class Utility {
    public static void printPrettyJson(String json) {
        System.out.println("json = " + json);
    }
    private Utility() {
        throw new AssertionError();
    }
}
