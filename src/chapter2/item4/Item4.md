# 인스턴스화를 막으려거든 private 생성자를 사용하라.

객체는 `new` 키워드를 통해 클래스로부터 인스턴스화가 된다.

## 방어 방법

정적 멤버만 담은 유틸리티 클래스가 있다고 가정하자.
기본적으로 생성자를 명시하지 않으면 컴파일러가 자동으로 아래와 같은 기본 생성자를 만들어준다.

```java
// 작성한 코드
public class Utility {
    
    public static void printPrettyJson(String json) {
        ...
    }
    
}

// 컴파일 시점
public class Utility {
    
    public static void printPrettyJson(String json) {
        ...
    }
    
    public Utility() {
        
    }
}
```

이러한 경우 객체를 생성하지 않아도 정적 팩터리 메서드를 이용할 수 있는데
누군가는 객체를 만들어 이를 이용할 수 있게되는 것이다.

이를 방지하기 위해 아래와 같이 코드를 변경하는 것이 좋다.

```java
public class Utility {
    
    public static void printPrettyJson(String json) {
        ...
    }
    
    private Utility() {
        throw new AssertionError();
    }
}
```

생성자를 `public`이 아닌 `private`로 명시하면
클래스 외부에서 접근할 수 없게 되어 정적 메소드와 필드만을 사용할 수 있게 된다.

꼭 `AssertionError()`를 던질 필요는 없다.
해당 코드를 작성한 이유는 혹시라도 다른 방식으로 해당 생성자를 호출하게 된다면
에러를 던져 생성자를 사용 및 호출하지 못하는 방식으로 인스턴스화를 방지한다.

또한 이 방식은 상속도 못하게 막을 수 있다.
하위 클래스에서 해당 클래스의 생성자를 호출하지 못하므로 상속이 불가능하다.

## 정리

> 생성자가 존재해도 호출할 수 없는 이유에 대해서는 꼭 명시하는 것이 좋을 것 같다!