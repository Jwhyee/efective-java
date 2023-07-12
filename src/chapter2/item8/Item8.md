# finalizer와 cleaner 사용을 피하라.

## finalizer와 cleaner란?

> 위 두 기능은 Java에서 제공하는 객체 소멸자이다.

객체를 생성하는 생성자(Constructor)가 있다면,
반대로 객체를 소멸시키는 소멸자(Destructor)가 존재한다. 

### finalizer

클래스에서 제공하는 소멸자이다. 
API를 살펴보면 아래와 같은 문구가 있는 것을 볼 수 있다.

> Overrides deprecated method in 'java.lang.Object' 

자바 자체에서도 권장하지 않으며, 더이상 사용되지 않음을 알 수 있다.
하지만 일부 라이브러리에는 아직 존재한다.

우선 아래 코드를 통해 예시를 살펴보자!

```java
package chapter2.item8;

public class FinalizerTester {

    // 객체 번호를 저장할 변수
    private int num;

    // 생성자를 통해 객체 번호를 받아옴
    public FinalizerTester(int num) {
        this.num = num;
        System.out.println(num + "번 객체 생성");
    }

    // 객체 소멸을 위한 finalize 메소드
    @Override
    protected void finalize() throws Throwable {
        System.out.println(num + "번 객체 소멸 -> finalize()");
        super.finalize();
    }
}
```

위의 설계대로 객체가 생성될 때는 `num번 객체 생성`이 출력되고,
객체가 소멸될 때는 `num번 객체 소멸`이 출력된다.

그럼 위 설계가 제대로 돌아갈지 테스트를 통해 확인해보자!

```java
public class Item8Main {
    @Test
    @DisplayName("객체 소멸 테스트")
    void finalizerTest() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            FinalizerTester t = new FinalizerTester(i);
        }
        Thread.sleep(10000);
    }
}
```

반복문을 통해 총 10개의 객체를 생성하고,
gc에서 수거해갈 수 있도록 10초 정도 스레드를 10초 동안 재워놓았다.
그 결과 아래와 같다.

```bash
1번 객체 생성
2번 객체 생성
3번 객체 생성
4번 객체 생성
5번 객체 생성
6번 객체 생성
7번 객체 생성
8번 객체 생성
9번 객체 생성
10번 객체 생성
```

그냥 아무런 일도 안 일어났다.
그러면 객체를 생성하자마자 gc가 수거해갈 수 있도록 수정한 뒤 다시 돌려보자!

```java
public class Item8Main {
    @Test
    @DisplayName("객체 소멸 테스트")
    void finalizerTest() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            FinalizerTester t = new FinalizerTester(i);
            System.gc();
        }
    }
}
```

```bash
1번 객체 생성
2번 객체 생성
3번 객체 생성
4번 객체 생성
5번 객체 생성
1번 객체 소멸 -> finalize()
6번 객체 생성
3번 객체 소멸 -> finalize()
4번 객체 소멸 -> finalize()
7번 객체 생성
5번 객체 소멸 -> finalize()
2번 객체 소멸 -> finalize()
6번 객체 소멸 -> finalize()
8번 객체 생성
9번 객체 생성
7번 객체 소멸 -> finalize()
10번 객체 생성
8번 객체 소멸 -> finalize()
9번 객체 소멸 -> finalize()
```

뭔가 이상하지 않은가? 객체의 생명주기를 예측할 수가 없다.

즉, 제때 실행되어야 하는 작업에서 `finalizer`는 효과도 없을 뿐더러,
예측할 수 없기 때문에 기본적으로 쓰지 말아야한다. 

### cleaner

`AutoCloseable`을 구현해 주로 사용하는 소멸자로 아래 코드를 통해 천천히 살펴보자!

```java
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        @Override
        public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }

    private final State state;
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}
```

`static`으로 선언된 중첩 클래스 `State`는 cleaner가 방을 청소할 때 **수거할 자원**들을 담고 있다.
`numJunkPiles`는 방 안의 쓰레기 수를 뜻하며, **수거할 자원**에 해당한다.

여기서 `numJunkPiles`를 `int`가 아닌 **네이티브 피어**를 가리키는 포인터를 담은
`final long` 변수로 하는게 더 현실적이긴하다.

> 네이티브 피어는 [아래](https://github.com/Jwhyee/effective-java/blob/master/src/chapter2/item8/Item8.md#2-%EB%84%A4%EC%9D%B4%ED%8B%B0%EB%B8%8C-%ED%94%BC%EC%96%B4%EC%99%80-%EC%97%B0%EA%B2%B0%EB%90%9C-%EA%B0%9D%EC%B2%B4) 설명을 확인해주세요.

또한, `State` 인스턴스는 절대로 `Room` 인스턴스를 참조해서는 안 된다.
만약 참조할 경우 순환 참조가 생겨 GC가 `Room` 인스턴스를 회수해갈 기회가 오지 않는다.
`State`가 정접 중첩 클래스인 이유 또한 정적이 아닌 중첩 클래스는 바깥 객체의 참조를 갖게 되기 때문이다.

그럼 이제 위에서 구현한 내용을 코드로 실행해보자!

```java
public class Item8Main {
    @Test
    @DisplayName("Cleaner 객체 소멸 테스트1")
    void cleanerTest() {
        try (Room myRoom = new Room(7)) {
            System.out.println("청소 ㄱ");
        }
    }
}
```

```bash
청소 ㄱ
방 청소
```

정상적으로 방 청소하는 코드를 볼 수 있다.
그러면 위 코드와 다르게 `try-with-resources` 블록으로 감싸지 않으면 어떤 결과가 나올까?

```java
public class Item8Main {
    @Test
    @DisplayName("Cleaner 객체 소멸 테스트2")
    void cleanerTest() {
        new Room(99);
        System.out.println("청소 ㄱ");
    }
}
```

```bash
청소 ㄱ
```

절대 청소를 하지 않는다. 그 이유는 `cleaner` 명세에서 찾아볼 수 있다.

> System.exit()을 호출할 때의 cleaner 동작은 구현하기 나름이다.
> 청소가 이뤄질지는 보장하지 않는다.

만약 코드 가장 마지막에 `System.gc()`를 추가하면 청소를할 수 있겠지만,
모든 컴퓨터에서 동작한다는 보장은 없다!

## 사용하지 않는 이유

### 1. 제때 실행되어야 하는 작업에서 사용할 수 없다.

시스템이 동시에 열 수 있는 파일의 개수에는 한계가 있다.
파일을 닫을 때, `finalizer` 혹은 `cleaner`를 사용하면
언제 닫힐지 모르는 상태로 계속 열린 상태를 유지하게 된다.
만약 `finalizer` 혹은 `cleaner`가 실행을 게을리해서 파일을 계속 열어둔다면
새로운 파일을 열지 못해 프로그램이 실패할 수 있다.

이러한 문제는 GC 튜닝 과정에서 수정할 수 있긴하다.
하지만 이를 구현하는 알고리즘 방식마다 천차만별이며
테스트에서 제대로 동작한 것이 고객 시스템에서는 큰 재앙을 불러올 수 있다.

### 2. 상태를 영구적으로 수정하는 작업에서 사용할 수 없다.

앞서 설명한 것과 같이 `finalizer`와 `cleaner`는 수행 시점도 알 수 없지만,
수행 여부조차 보장하지 않는다.
즉, 데이터를 DB에 저장하는 과정을 수행하지 못한 채 프로그램이 중단될 수 있다는 것이다.
이러한 이유로 영구적으로 수정하는 작업에서는 절대 해당 기능에 의존하면 안 된다.

게다가 `finalizer`는 동작 중 발생한 예외는 모두 무시하며,
처리할 작업이 남았더라도 그 순간 종료된다.
잡지 못한 예외처리 하나 때문에 해당 객체는 자칫 마무리가 덜 된 상태로 남을 수 있다.

### 3. 심각한 성능 문제를 동반한다.

간단한 `AutoCloseable` 객체를 생성하고, 가비지 컬렉터가 수거하기 까지 12ns가 걸리는 반면
`finalizer`를 사용하면 550ns가 나오게 된다.
즉, `finalizer`는 사용한 객체를 생성하고 소멸까지 시키니 50배나 더 걸린 것이다.
`cleaner`도 모든 인스턴스를 수거하는 형태로 사용하면 비슷한 성능을 보이게 된다.

### 4. finalizer 공격에 노출되어 심각한 보안 문제를 일으킨다.

`finalizer` 공격 원리는 단순하다.
생성자나 직렬화 과정에서 예외가 발생하면,
이 생성되다 만 객체에서 악위적인 하위 클래스의 `finalizer`가 수행될 수 있게 한다.

객체 생성을 막으려면 생성자에서 예외를 던지는 것만으로도 충분하지만,
`finalizer`가 있다면 그렇지도 않다.

`final`이 아닌 클래스를 `finalizer` 공격으로부터 방어하려면
아무 일도 하지 않는 `finalizer` 메소드를 만들고 `final`로 선언하자.

## 그럼 어디에 사용할까?

위에서 본 단점들을 보면 절대 사용하지 말아야할 것 같지만,
나름의 쓰임새가 존재한다.

### 1. 안전망 역할

자원의 소유자가 `close()` 메소드를 호출하지 않으면, 자원이 회수되지 않는다.
그렇기 때문에 `finalizer`나 `cleaner`를 통해 즉시가 아니더라도,
늦게라도 자원을 회수할 수 있도록 안 하는 것보다 낫다.

자바 라이브러리의 일부 클래스는 안전망 역할의 `finalizer`를 제공한다.
`FileInputStream`, `FileOutputStream`, `ThreadPoolExecutor`가 대표적이다.

### 2. 네이티브 피어와 연결된 객체

일반적으로 네이티브 피어는 일반 자바 객체가 네이티브 메소드를 통해 기능을 위임한 네이티브 메소드를 말한다.
아래는 조금 더 자세한 내용이 있으며, 참고 링크 내부에 있는 Baeldung에서도 자세히 소개하고 있으니 한 번 확인해 보는 것이 좋을 것 같다!

> **네이티브 피어(native peer)란?** <br>
> C/C++이나 어셈블리 프로그램을 컴파일한 기계어 프로그램를 지칭하는데
> 이를 라이브러리로써 자바 피어가 실행할 수 있게 해주는 인터페이스를
> JNI (Java Native Interface)라고 합니다.
> 자바 피어가 로딩될 때 정적으로 System.loadLibrary() 메소드를 호출해
> 네이티브 피어를 로딩하고 네이티브 메소드는 native 키워드를 사용해 호출하는 방식으로 사용합니다. <br>
> [참고 링크](https://github.com/java-squid/effective-java/issues/8#issuecomment-698310315)

네이티브 피어는 자바 객체가 아니니 GC는 그 존재를 알아차리지 못한다.
즉, 자바 피어를 회수할 때 네이티브 객체까지 회수하지 못한다는 것이다.

이럴 경우 `finalizer`나 `cleaner`를 이용하는 것이 차라리 나을 수도 있다.
단, 성능 저하를 감당할 수 있고, 네이티브 피어가 심각한 자원을 가지고 있지 않을 때만 해당된다.

## 정리

아직 많은 내용을 보진 않았지만, 지금까지 정리한 내용 중에 가장 흥미로웠던 내용 같다.
생성자가 아닌 소멸자가 있다는 것을 이번에 처음 알았다.
Java를 깊게 공부하지 않은 느낌도 들었다.
`finalizer`를 단순하게 보면 여러모로 쓰임새가 있을 것 같지만,
이 정도로 사용을 막을 정도면 공부하는 정도에서만 끝내는 것이 좋을 것 같다!