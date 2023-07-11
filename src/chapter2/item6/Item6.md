# 불필요한 객체 생성을 피하라.

같은 기능을 하는 객체를 매번 생성하기보단 객체 하나를 재사용하는 편이 나을 때가 많다.

```java
String s = new String("hello");
```

요즘 위와 같이 `new` 키워드를 통해 `String`을 생성하는 개발자는 없을 것이다.

만약 이러한 코드로 반복문 혹은 자주 호출되는 메소드에 들어가게 되면
정말 쓸데없는 `String` 인스턴스가 누적해서 쌓이게 될 것이다.

```java
String s = "hello";
```

그럼 위 코드는 어떨까?

앞서 봤던 코드와 달리 [리터럴](https://docs.oracle.com/javase/specs/jls/se13/html/jls-3.html#jls-3.10.5)을 통해 생성된 문자열은 모든 코드가 같은 객체를 재사용함이 보장된다.

[Item1](https://github.com/Jwhyee/effective-java/blob/master/src/chapter2/item1/Item1.md)에서
봤던 정적 팩터리 메소드를 제공하는 불변 클래스에서는 정적 팩터리 메소드를 사용해 불필요한 객체 생성을 피할 수 있었다.

생성자는 호출할 때마다 새로운 객체를 만들지만, 팩터리 메소드는 전혀 그렇지 않다.
불변 객체만이 아니라 가변 객체라 해도 사용 중에 변경되지 않을 것임을 안다면 재사용할 수 있다.

## 생성 비용이 비싼 객체

> 내가 만든 객체가 비싼 객체인지 매번 병확히 알 수는 없다.

아래 코드는 주어진 문자열이 유효한 로마 숫자인지 확인하는 메소드이다.

```java
public class RomanNumerals {
    static boolean isRomanNumeral(String s) {
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3}"
                + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})&");
    }
}
```

이 방식의 문제는 `String.matches` 메소드를 사용하는 것이다.
정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방식이지만,
성능이 중요한 상황에서는 반복해 사용하기엔 적합하지 않다.

내부에서 만드는 정규표현식용 `Pattern` 인스턴스는,
한 번 쓰고 버려져서 곧 바로 가비지 컬렉션 대상이 된다.
즉, 입력받은 정규표현식에 해당하는 유한 상태 머신(finite status machine)을 만들기 때문에 인스턴스 생성 비용이 높다.

```java
public class RomanNumerals {

    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})"
                    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    static boolean isRomanNumeral(String s) {
        return ROMAN.matcher(s).matches();
    }
}
```

위와 같이 정적 초기화 과정에서 직접 생성해 캐싱을 해둔 뒤,
해당 메소드가 호출될 때마다 이 인스턴스를 재사용하도록 한다.

이렇게 하면 `isRomanNumeral()`이 빈번히 호출되어도 성능을 크게 끌어올려줄 수 있다!

하지만 해당 메소드가 아예 사용되지 않거나,
빈번하게 사용되는 경우가 아니라면 쓸데없이 메모리를 차지하게 될 수 있다.
해당 기능이 주로 사용되는지 먼저 확인하고, 정적 필드로 개선하는 것을 고려하는 것이 좋다!

## 오토 박싱

> 기본 타입과 박싱된 기본 타입을 섞어 쓸 때, 자동으로 상호 변환해주는 기능

오토 박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐려주지만,
완전히 없애주는 것은 아니다.

아래와 같이 모든 양의 정수의 총합을 구하는 코드가 있다고 가정하자.

```java
public class Calc {
    public long prefixSum() {
        Long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }
        return sum;
    }
}
```

정확한 답을 내기는 하지만, 굉장히 느리다는 것을 알 수 있다. 
그 이유는 바로 `long` 타입 변수 `i`를 더하는 과정에서
`sum` 변수를 `long`이 아닌 `Long`으로 선언했기 때문이다.

위 코드를 이용했을 때, 실행시간은 2.685초가 걸리지만,
`Long`을 `long`으로만 변경해도 0.74초가 나오게 된다!

즉, `long` 타입인 `i`가 `Long` 타입인 `sum`에 더해질 때마다
불필요한 인스턴스 만들어져 최종적으로 $2^31$개나 만들어졌기 때문이다.

> 박싱된 기본 타입보다는 가능한 기본 타입을 사용하고,
> 의도치 않은 오토박싱이 숨어들지 않도록 주의하자!