# 정적 팩터리 매소드(static factory method)

> 클래스의 인스턴스를 반환하는 단순한 정적 메소드

## 정적(static)이란?

```java
public class Calc {
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

public class Main {
    public static void main(String[] args) {
        // static method
        int sum = Calc.sum(10, 20);

        // instance method
        Calc c = new Calc();
        double volume = c.sphereVolume(3.0);
    }
}
```

`static` 메서드는 위와 같이 클래스를 호출해 바로 메소드를 사용할 수 있다.
반면에 `static`이 붙지 않은 인스턴스 메소드는 객체(인스턴스)를 생성한 뒤에 사용이 가능하다.

주로 모든 인스턴스에 공통으로 사용하는 것에 `static`을 붙여 여러 객체에서 메모리를 공유하도록 한다.

단, `sum()` 메소드에 있는 `msg` 변수와 같이 클래스 내 인스턴스 변수는 `static` 메서드에서 활용할 수 없다.
반대로 인스턴스 메소드에서는 `static` 변수를 사용해도 상관없다.

> `static`은 클래스가 메모리에 올라갈 때 정의되지만, 인스턴스는 객체를 생성해야 메모리에 올라가기 때문이다.
> 그렇기 때문에 `static` 메소드에서는 인스턴스 변수를 사용할 수 없고, 인스턴스 메소드에서는 `static`을 사용할 수 있는 것이다.

## 장점

```java
public static Boolean valueOf(boolean b) {
    return (b ? TRUE : FALSE);
}
```

위 메소드 `boolean` 값을 받아 `Boolean` 객체 참조로 변환하는 메소드이다.
위와 같이 정적 팩터리 메소드를 이용하면 아래와 같은 장점이 존재한다.

### 1. 이름을 가질 수 있다.

생성자에 넘기는 매개변수와 생성자 자체만으로는 반환될 객체의 특성을 제대로 설명할 수 없다.

아래 두 코드에서 더 이해하기 쉬운 코드는 어떤 것일까?

```java
BigInteger bigInteger1 = new BigInteger(5, 10, new Random());
BigInteger bigInteger2 = BigInteger.probablePrime(5, new Random());
```

사실 두 코드 모두 '값이 소수인 `BigInteger`를 반환한다.'에 대한 코드이다.<br>
우리는 이름을 갖고 있는 `probablePrime`이 더 어떤 특징을 담고 있는지 알기 쉽다.

> 이와 같이 한 클래스에 시그니처가 같은 생성자가 여러 개 필요할 것 같으면, 생성자를 정적 팩터리 메소드로 바꾸고 각각의 차이를 잘 드러내는 이름을 지어주는 것이 좋다.

### 2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.

불변 클래스는 이러한 정적 팩터리 메소드 덕분에 인스턴스를 미리 만들어 놓거나 새로 생성한 인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.

```java
public static Boolean valueOf(boolean b) {
    return (b ? TRUE : FALSE);
}
```

앞서 본 코드와 동일한 `Boolean.valueOf()` 메소드는 `Boolean` 타입으로 반환하지만, 객체를 아예 생성하지 않는다.
그 이유는 바로 `Boolean`이 불변 클래스(final class)기 때문이다.

> (특히 생성 비용이 큰) 같은 객체가 자주 요청되는 상황이라면, 성능을 상당히 끌어 올려줄 수 있다.

### 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

### 4. 입력 매게변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

### 5. 정적 팩터리 메소드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

## 단점

### 1. 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.

상속을 하기 위해서는 `public`, `protected` 생성자가 필요하다.
하지만 `private`를 사용한 정적 팩터리 메서드만 제공하면, 하위 클래스를 만들 수 없게 된다.

### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

생성자와 같이 API 설명에 명확히 드러나지 않아 정적 팩터리 메소드 방식 클래스를 인스턴스화할 방법을 알아내야 한다.