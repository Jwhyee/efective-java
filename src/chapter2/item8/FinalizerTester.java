package chapter2.item8;

public class FinalizerTester {

    private int num;

    public FinalizerTester(int num) {
        this.num = num;
        System.out.println(num + "번 객체 생성");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println(num + "번 객체 소멸 -> finalize()");
        super.finalize();
    }
}
