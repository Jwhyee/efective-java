package chapter2.item7;

public class Item7Main {
    public static void main(String[] args) {
        MemoryStack s = new MemoryStack();
        s.push("Hello");
        s.push("World!");

        String pop = (String) s.pop();
        System.out.println(pop);

    }
}
