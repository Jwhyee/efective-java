# equals는 일반 규약을 지켜 재정의하라.

## 왜 재정의할까?

### 재정의하지 않는 경우

`equals`를 재정의하지 않고 그대로 두면 그 클래스의 인스턴스는 오직 자기 자신과만 같게 된다.
아래 상황 중 하나에 해당된다면 재정의하지 않는 것이 최선이다.

- 각 인스턴스가 본질적으로 고유하다.
- 인스턴스의 '논리적 동치성(logical equality)'을 검사할 일이 없다.
- 상위 클래스에서 재정의한 `equals`가 하위 클래스에도 딱 들어맞는다.
- 클래스가 `private`이거나 `package-private`이고, `equals` 메소드를 호출할 일이 없다.

> **논리적 동치성이란?** <br>
> **객체**의 실제 값을 비교하는 것을 논리적 동치성이라고 하며, 
> 메모리에 저장된 **변수**가 가진 값을 비교하는 것을 물리적 동치성이라고 한다.
> <br>
> [참고 블로그](https://javanitto.tistory.com/9)

### 재정의하는 경우

객체 식별성(object identity; 두 객체가 물리적으로 같은가)이 아니라 논리적 동치성을 확인해야 하는데,
상위 클래스의 `equals`가 논리적 동치성을 비교하도록 재정의되지 않았을 때다.

주로 값 클래스인 `Integer`, `String`과 같은 클래스가 이에 해당한다.
우리는 두 객체가 동일한지 궁금한 것이 아니라,
주로 두 객체가 갖고 있는 값이 같은지를 확인하고 싶어한다.
그렇기 때문에 `equals`가 논리적 동치성을 확인해도록 재정의해두면,
정확한 값을 비교할 수 있으며, `Map`의 키와 `Set`의 원소로 사용할 수 있게 된다.

## 재정의 유약

`equals` 메소드를 재정의할 때는 반드시 일반 규약에 따라야한다.
이 규약을 어길 경우 프로그램이 이상하게 동작하거나 종료될 위험이 있고,
원인이 되는 코드를 찾기도 굉장히 어려울 것이다.
또한, 한 클래스의 인스턴스는 다른 곳으로 빈번히 전달되고,
컬렉션 클래스들을 포함해 수 많은 클래스는 전달 받은 객체가 `equals` 규약을 지킨다고 가정하고 동작한다.

> **_세상에 홀로 존재하는 클래스는 없다.<br>_**
> 존 던(John Donne)

`equals` 메소드는 동치관계(equivalence relation)를 구현하며, 다음을 만족해야한다.

> **동치 관계란?**<br>
> 집합을 서로 같은 원소들로 이뤄진 부분집합으로 나누는 연산이다.
> 이 부분집합을 동치류(equivalence class; 동치 클래스)라 부른다.
> equals 메소드가 쓸모 있으려면 모든 원소가 같은 동치류에 속한 어떤 원소와도 서로 교환할 수 있어야 한다.

### 반사성(reflexivity)

> null이 아닌 모든 참조 값 x에 대해,
> x.equals(x)는 true다.

쉽게 얘기하자면, 객체는 자기 자신과 같아야한다는 뜻이다.
사실 이 요건은 어기는 것이 더 어려울 것이다.

```java
public class RelationTest {

    @Test
    void collectionTest() {
        List<Person> personList = new ArrayList<>();
        Person p = new Person("010203-1111111", "Tom Holland");
        personList.add(p);

        assertTrue(personList.contains(p));
    }
    
}
```

너무나도 당연하게 `true`가 나오는 것을 우리는 알 수 있다.
하지만 반사성을 지키지 않는다면 `false`가 나오는 진귀한 관경을 볼 수 있을 것이다.

### 대칭성(symmentry)

> null이 아닌 모든 참조 값 x, y에 대해,
> x.equals(y)가 true면, y.equals(x)도 true다.

두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다는 뜻이다.

아래 코드는 `Polish`, `polish`와 같이 대소문자의 차이가 있는 문자열을 비교했을 때,
차이를 보여주는 클래스이다.

```java
public class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString) {
            return s.equalsIgnoreCase(
                    ((CaseInsensitiveString) o).s
            );
        }
        if (o instanceof String) {
            return s.equalsIgnoreCase((String) o);
        }
        return false;
    }

    @Override
    public String toString() {
        return s;
    }
}
```

`CaseInsensitiveString`의 `equals`는 일반 문자열과도 비교를 시도한다.

```java
public class RelationTest {
    @Test
    void caseInsensitiveCompareTest() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Holland");
        String s = "holland";
        assertTrue(cis.equals(s));
    }
}
```

위 코드의 결과는 `ture`를 반환한다. `CaseInsensitiveString`의 `equals`는 일반 `String`을 알고 있지만,
`String`의 `equals`는 `CaseInsensitiveString`의 존재를 모른다.
따라서 `s.equals(cis)`는 `false`를 반환하여, 대칭성을 명백히 위반한다.

이번에는 아래와 같이 컬렉션을 통해 확인해보자.

```java
public class RelationTest {
    @Test
    void collectionContainsTest() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Holland");
        String s = "holland";

        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);

        assertTrue(list.contains(s));
    }
}
```

현재 `false`가 반환되는 것을 볼 수 있다.
하지만 이는 순전히 구현하기 나름이라 OpenJDK 버전이 바뀌거나,
다른 JDK에서는 `true`를 반환하거나, 런타임 예외를 던질 수도 있다.

`equals` 규약을 어기면 그 객체를 사용하는 다른 객체들이 어떻게 반응할지 알 수 없다.

### 추이성(transitivity)

> null이 아닌 모든 참조 값 x, y, z에 대해,
> x.equals(y)가 true이고, y.equals(z)도 true면, x.equals(z)도 true다.

### 일관성(consistency)

> null이 아닌 모든 참조 값 x, y에 대해,
> x.equals(y)를 반복해서 호출하면 항상 true(혹은 false)를 반환한다.

### null-아님

> null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다.