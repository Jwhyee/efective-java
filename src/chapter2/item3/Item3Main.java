package chapter2.item3;

public class Item3Main {
    public static void main(String[] args) {
        Singleton instance1 = Singleton.INSTANCE;
        int status = instance1.getStatus();
        System.out.println("status = " + status);
        instance1.setStatus(200);

        Singleton instance2 = Singleton.getInstance();
        status = instance2.getStatus();
        System.out.println("status = " + status);

        SingletonEnum instance3 = SingletonEnum.INSTANCE;

    }
}
