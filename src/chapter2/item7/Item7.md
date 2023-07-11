# 다 쓴 객체 참조를 해제하라.

C, C++ 언어를 사용해본 개발자라면 `malloc`, `free` 등을 사용한 메모리 관리가 귀찮게 느껴진 적이 있을 것이다.
하지만 Java는 GC(Garbage Collector)를 이용해 메모리를 관리하기 때문에 정말 편하게 느껴진다.
다 사용하고 버려질 객체를 알아서 회수해가기 때문이다.

하지만 이것은 사실이 아니다! 아래 내용을 통해 자세히 확인해보자!

## 메모리 누수

메모리 누수의 가장 큰 문제점은 오래 실행하다 보면 가비지 컬렉션 활동과 메모리 사용량이 늘어나 결국 성능이 저하될 것이다.
드물지만 심할 경우 디스크 페이징이나 `OutOfMemoryError`를 일으켜 프로그램이 종료될 수도 있다.

### null을 이용한 참조 해제

아래는 `Stack`을 간략하게 구현한 코드이다.

우선 `Stack`은 선입후출의 개념이며, 가장 먼저 들어온 데이터가 가장 마지막에 나오게 된다.

예시로 프링글스를 통해 알아보자! 우리가 그 통에 과자를 하나씩 채운다고 가정하자.
처음에 넣는 1번 과자는 가장 아래에 들어갈 것이고, 그 다음 2번 과자는 1번 위에 쌓이게 된다.
즉, 가장 먼저 넣은 과자가 가장 마지막에 나오게 되는 것이다!

여기서 넣는 것을 `push()`, 꺼내는 것을 `pop()`이라고 한다!

```java
public class MemoryStack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public MemoryStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}

```

제네릭을 사용하지 않았을 뿐, 특별히 큰 문제는 없어 보인다.
하지만 여기에는 꼭꼭 숨어있는 문제가 있다. 그것은 바로 **메모리 누수**이다!

위 코드에서 스택이 커지거나 줄어들 때, 스택에서 꺼내진 객체들을 가비지 컬렉터가 회수하지 않는다.
프로그램에서 그 객체를 사용하지 않아도 말이다!

그 이유는 스택이 그 객체들의 다 쓴 참조(obsolete reference)를 여전히 갖고 있기 때문이다.
다 쓴 참조란, 말 그대로 다시 쓰지 않을 참조를 의미한다!

아래 간단한 코드를 통해 한 번 확인해보자!

```java
public class MemoryStack {
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }
}

public class StackTest {
    public static void main(String[] args) {
        MemoryStack s = new MemoryStack();
        s.push("Hello");
        s.push("World!");

        String pop = (String) s.pop();
        System.out.println(pop);
        
        while (true) {
            ...
        }
    }
}
```

`pop()`을 하더라도, `elements[--size]`에 대한 객체를 반환만할 뿐 해당 객체의 참조는 아직 `elements` 배열에 존재한다.

즉, 이 경우 반환한 객체를 사용하지 않더라도 `elements` 배열에 잔존해 있어
가비지 컬렉터가 회수하지 않는다는 것이다.

이를 해결하기 위해서는 아래와 같이 수정하면 된다.

```java
public class MemoryStack {
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elemets[size] = null;
        return result;
    }
}
```

단순히 해당 객체의 자리를 `null`처리만 해도 참조가 해제되며,
혹시라도 해당 객체를 실수로라도 사용하려하면 `NPE(NullPointerException)`를 던지며 종료된다.

하지만, 객체 참조를 `null` 처리하는 일은 예외적인 경우여야 한다.
가장 좋은 방법은 해당 참조를 담은 변수를 유효 범위(scope) 밖으로 밀어내는 것이다.

그럼 꼭 `null`로 처리해야하는 경우는 언제일까?

우선 `Stack` 클래스는 사실 자기 메모리를 직접 관리한다.
`elements` 배열로 저장소 풀(pool)을 만들어 원소들을 관리한다.
활성 영역에 속한 원소들이 사용되고, 비활성 영역은 쓰이지 않는다.

가장 큰 문제는 GC가 이 사실을 알 수 없다는 것이다.
GC의 입장에서 봤을 때 비활성 영역이 `Stack`의 영역에 포함되어 있기 때문이다.
사용하지 않는 객체는 우리만 알고 있는 사실이다.
때문에 이러한 경우에는 비활성 영역이 되는 순간 `null` 처리를 통해 해당 객체를 앞으로 쓰지 않을 것임을 GC에게 알려줘야한다.

## 정리

이전에 `ArrayList`를 직접 구현해본 경험이 있다.
가장 마지막에 있는 요소를 제거하는 기능을 만들었는데, 당시에도 `null`을 처리하지 않았다.
만약 해당 기능을 `SpringBoot` 서버 내에 구현을 해서 실제 서비스되고 있었다면 정말 끔찍한 일이 일어났을 것 같다.
앞으로 객체의 유효 기간을 잘 확인하는 습관을 가져야할 것 같다!