package chapter3.item10;

public class Person {
    private String idNumber;
    private String name;

    public Person() {
    }

    public Person(String idNumber, String name) {
        this.idNumber = idNumber;
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }
}
