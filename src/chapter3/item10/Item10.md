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

## 재정의 규약

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

        // 테스트 성공
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
        
        // 테스트 성공
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

        // 테스트 실패
        assertTrue(list.contains(s));
    }
}
```

현재 `false`가 반환되는 것을 볼 수 있다.
하지만 이는 순전히 구현하기 나름이라 OpenJDK 버전이 바뀌거나,
다른 JDK에서는 `true`를 반환하거나, 런타임 예외를 던질 수도 있다.

`equals` 규약을 어기면 그 객체를 사용하는 다른 객체들이 어떻게 반응할지 알 수 없다.

```java
public class CaseInsensitiveString {

    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString &&
                ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
    }

}
```

기존 `o instanceof String` 때문에 생긴 대칭성 위배를 바로 잡기 위해
비교하는 객체가 `CaseInsensitiveString`의 인스턴스인지 확인하고,
해당 객체가 갖고 있는 변수 `s`가 대소문자를 무시한 뒤 같은 값을 갖고 있는지 확인해야 한다.

### 추이성(transitivity)

> null이 아닌 모든 참조 값 x, y, z에 대해,
> x.equals(y)가 true이고, y.equals(z)도 true면, x.equals(z)도 true다.

1번 객체와 2번 객체가 같고, 2번 객체와 3번 객체가 같다면, 1번과 3번 객체도 같아야 한다는 뜻이다.

상위 클래스에는 없는 새로운 필드를 하위 클래스에 추가하는 상황을 생각해보자.
`equals` 비교에 영향을 주는 새로운 정보를 추가한 것이다.
아래 코드는 2차원에서의 점을 표현하는 클래스이다.

```java
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point point)) {
            return false;
        }
        return x == point.x && y == point.y;
    }
}
```

```java
public class ColorPoint extends Point{
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
}
```

위 코드를 살펴보면 `ColorPoint` 기준으로 상위 클래스인 `Point`가 `equals`를 재정의했기 때문에 다시 할 필요는 없어 보인다.
하지만 상위 클래스에서 재정의한 내용이 하위 클래스인 `ColorPoint`과 딱 들어맞지 않는다.

```java
public class RelationTest {
    @Test
    void transitivityTest() {
        ColorPoint c1 = new ColorPoint(1, 2, new Color(255, 255, 0));
        ColorPoint c2 = new ColorPoint(1, 2, new Color(255, 255, 224));

        // 테스트 성공
        assertTrue(c1.equals(c2));
    }
}
```

즉, 색상 정보는 무시한 채 비교를 수행하게 되는 것이다. `equals` 규약을 어긴 것은 아니지만,
중요한 정보를 놓치게 되는 불상사가 생길 수 있게 되는 것이다.

```java
public class ColorPoint extends Point {
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ColorPoint))
            return false;
        return super.equals(o) && ((ColorPoint) o).color == color;
    }
}
```

그럼 위와 같이 `ColorPoint`의 `equals`를 재정의하면 어떨까?

```java
public class RelationTest {
    @Test
    void transitivityMixTest() {
        Point p = new Point(1, 2);
        ColorPoint c = new ColorPoint(1, 2, new Color(255, 255, 0));

        // 테스트 성공
        assertTrue(p.equals(c));
        
        // 테스트 실패
        assertTrue(c.equals(p));
    }
}
```

일반 `Point`를 `ColorPoint`에 비교한 결과와 그 반대로 비교한 결과가 다르게 나온다.
즉, 위에서 새로 재정의한 코드는 **대칭성을 위배**하고 있다.
`Point`의 `equals`는 색상을 무시하고, `ColorPoint`의 `equals`는 입력 매개변수의 클래스 종류가 다르다며 매번 `false`만 반환할 것이다.

그럼 이런 생각을 할 수 있을 것이다.
'`ColorPoint.equals`가 `Point`와 비교할 때는 색상을 무시하면 되지 않을까?'

```java
public class ColorPoint extends Point {
    @Override
    public boolean equals(Object o) {
        // 1번 비교
        if (!(o instanceof Point))
            return false;

        // 2번 비교
        if(!(o instanceof ColorPoint))
            return o.equals(this);

        return super.equals(o) && ((ColorPoint) o).color == color;
    }
}
```

우선 `ColorPoint`는 `Point`를 상속 받았기 때문에 `ColorPoint` 객체는 `Point` 객체이기도 하다.

**1번 비교 단계**에서 비교할 객체가 `Point` 객체가 아니라면,
비교할 이유가 없기 때문에 `false`를 반환한다.

**2번 비교 단계**에서 비교할 객체가 `ColorPoint` 객체가 아니라면,
비교할 객체의 `equals`를 통해 현재 `ColorPoint` 객체가 동일한지 검사한다.

1, 2번에서 반환되지 않았다면, `ColorPoint` 객체가 확실해졌다.
들어온 객체가 `Point.equals`에도 해당하고, 비교할 대상이 갖고 있는 `color`가 현재 객체의 `color`가 맞는지 비교한다.

아래 테스트를 통해 결과를 확인해보자.

```java
public class RelationTest {
    @Test
    void transitivityMixTest2() {
        ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
        Point p2 = new Point(1, 2);
        ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

        // 1번 : 테스트 통과
        // ColorPoint의 2번 비교를 통해 x, y값만 비교함.
        assertTrue(p1.equals(p2));
        
        // 2번 : 테스트 통과
        // Point의 비교를 통해 x, y값만 비교함.
        assertTrue(p2.equals(p3));
        
        // 3번 : 테스트 실패
        // 같은 클래스에 대한 인스턴스이므로, x, y값과 color 모두 비교한다.
        assertTrue(p1.equals(p3));
    }
}
```

위 코드는 1번 객체와 3번 객체가 동일해야한다는 추이성에 명백히 위배된다.
또한, 이 방식은 `Point`의 하위 클래스를 만들고 같은 방식으로 `equals`를 재정의하면 무한 재귀에 빠질 수 있다.

사실 이 현상은 모든 객체 지향 언어의 동치관계에서 나타나는 근본적인 문제이다.
객체 지향적 추상화 이점을 포기하지 않는 한
**구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.**

```java
public class Point {
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}
```

앞서 설명한 내용은 `equals`안의 `instanceof` 검사를 `getClass` 검사로 바꾸면,
규약도 지키고, 값도 추가하면서 구체 클래스를 상속할 수 있는 뜻으로 들린다.

이번에 재정의한 내용은 같은 구현 클래스의 객체와 비교할 때만 `true`를 반환한다.
괜찮은 방법 같아 보이지만 **리스코프 치환 원친 위반**으로 실제로는 활용할 수 없는 코드이다.
`Point`의 하위 클래스는 정의상 여전히 `Point`이므로 어디서든 `Point`로써 활용될 수 있어야하지만
이 방식에서는 그렇지 못하다.

---

#### 리스코프 치환 원칙

리스코프 치환 원칙(Liskov substitution principle)이란,
하위 타입은 언제나 상위 타입으로 교체할 수 있어야하며,
어떤 타입에 있어서 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다.

따라서 그 타입의 모든 메소드가 하위 타입에서도 똑같이 잘 작동해야 한다.

---

자, 다시 추이성으로 돌아와서 조금 더 수정해보자.

위에서 설명한 리스코프 치환 원칙은
"`Point`의 하위 클래스는 정의상 여전히 `Point`이므로, 어디서든 `Point`로써 활용될 수 있어야한다."를
격식 있게 표현한 말이다.

아래 코드는 주어진 점이 반지름이 1인 단위 원 안에 있는지를 판별하는 코드이다.

```java
public class Point {
    // 단위 원 안의 모든 점을 포함하도록 unitCircle을 초기화한다.
    private static final Set<Point> unitCircle = Set.of(
            new Point(1, 0), new Point(0, 1),
            new Point(-1, 0), new Point(0, -1)
    );

    public static boolean onUnitCircle(Point p) {
        return unitCircle.contains(p);
    }
}
```

```java
public class ColorPoint extends Point{
    private static final AtomicInteger counter = new AtomicInteger();

    public static int numberCreated() {
        return counter.get();
    }

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        numberCreated();
    }
}
```

만약 여기서 `CounterPoint`의 인스턴스를 `onUnitCircle` 메소드에 넘기면 어떤 일이 발생할까?
`Point` 클래스의 `equals`를 `getClass`를 사용해 작성했다면,
`onUnitCircle`은 인스턴스의 x, y 값과는 무관하게 `false`를 반환할 것이다.

```java
public class RelationTest {
    @Test
    void counterTest1() {
        Point p = new Point(0, 1);
        // false 반환
        assertTrue(Point.onUnitCircle(p));
    }
}
```

분명 `Point(0, 1)`은 원에 포함되어 있지만 `false`를 반환한다.
그 이유는, 컬렉션 구현체에서 **주어진 원소를 담고 있는지를 확인하는 방법**에 있다.

`onUnitCircle`에서 사용한 `Set`을 포함하여, 대부분의 컬렉션은 이 작업에 `equals`를 사용하는데,
`CounterPointer`의 인스턴스는 어떤 `Point`와도 같을 수 없기 때문이다.

반면, `Point`의 `equals`를 `instanceof` 기반으로 올바르게 구현했다면,
`CounterPointer` 인스턴스를 건내줘도 `onUnitCircle` 메소드가 제대로 동작했을 것이다.

구체 클래스의 하위 클래스에서 값을 추가할 방법은 없지만, "상속 대신 컴포지션을 이용하라"는 아이템 18의 조언을 따르면 된다.

```java
public class ColorPoint {
    private final Point point;
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint cp)) {
            return false;
        }
        return cp.point.equals(point) && cp.color.equals(color);
    }
}
```

위 코드와 같이 실질적인 상속이 아닌,
생성자를 통해 받은 좌표값으로 `private` 필드인 `Point`에 주입해준다.
`ColorPoint`와 같은 위치의 일반 `Point`를 반환하는 뷰 메소드인 `asPoint`를 `public`으로 추가하는 것이다. 

자바 라이브러리 중에도 구체 클래스를 확장해 값을 추가한 클래스가 있다.
`java.sql.Timestamp`는 `java.util.Date`를 확장한 후, `nanoseconds` 필드를 추가했다.
그 결과, `Timestamp`의 `equals`는 **대칭성을 위배**하며,
`Date` 객체와 한 컬렉션에 넣거나 서로 섞어 사용하면 엉뚱하게 동작하게 됐다.

`Timestamp`를 위와 같이 설계한 것은 실수니 절대 따라하면 안 된다.

### 일관성(consistency)

> null이 아닌 모든 참조 값 x, y에 대해,
> x.equals(y)를 반복해서 호출하면 항상 true(혹은 false)를 반환한다.

### null-아님

> null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다.