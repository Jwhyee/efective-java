package item3;

public class Singleton {
    public static final Singleton INSTANCE = new Singleton();

    private int status = 100;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private Singleton() {

    }
    public static Singleton getInstance() {
        return INSTANCE;
    }

    public void say() {
        System.out.println("Hello, World!");
    }
}
