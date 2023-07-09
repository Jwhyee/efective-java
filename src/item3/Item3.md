# private 생성자나 열거 타입으로 싱글톤임을 보증하라

## 싱글톤이란?

> 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.

## 싱글톤을 생성하는 방법

### public static final 필드 방식의 싱글톤

> 리플렉션 API 중 `AccessibleObject.setAccessible`을 사용해 `private` 생성자를 호출할 수 있음.

```java
public class Singleton {
    public static final Singleton INSTANCE = new Singleton();

    private Singleton() { }

    public void say() {
        System.out.println("Hello, World!");
    }
}

public class Main {
    public static void main(String[] args) {
        Singleton instance = Singleton.INSTANCE;
        instance.say();
    }
}
```

위 클래스를 통해 알 수 있는 사실은 아래와 같다.

1. 생성자는 `private`로 타 클래스에서 객체를 생성할 수 없다.
2. `INSTANCE` static 변수를 통해 클래스 생성과 동시에 만들어진 객체를 가져올 수 있다.

정적 팩터리 메소드에서 알아봤듯이 클래스가 메모리에 올라갈 때, 정적 변수 및 메소드도 함께 메모리에 올라간다.
때문에 객체가 단 1번만 생성될 수 있는 것이다.

해당 방식의 가장 큰 장점은 간결하며, 해당 클래스에서 싱글톤임이 API에 명백히 드러나는 것이다.

<center>
<img src="https://github.com/Jwhyee/effective-java/assets/82663161/89209561-2d56-40bb-8ebf-b74d38148f53">
</center>

### 정적 팩토리 방식의 싱글톤

> 리플렉션 API 중 `AccessibleObject.setAccessible`을 사용해 `private` 생성자를 호출할 수 있음.

```java
public class Singleton {
    private static final Singleton instance = new Singleton();

    private Singleton() {
        
    }
    public static Singleton getInstance() {
        return instance;
    }

    public void say() {
        System.out.println("Hello, World!");
    }
}

public class Main {
    public static void main(String[] args) {
        Singleton INSTANCE = Singleton.getInstance();
        instance.say();
    }
}
```

앞서 본 코드와 크게 다를 것은 없어 보이지만, `public static final`이 아닌 `private static final`로 변경되었다.

해당 코드의 장점은 총 3가지가 존재한다.

1. API를 바꾸지 않고도 싱글톤이 아니게 변경할 수 있다.

하나의 인스턴스를 반환하던 상태에서 호출하는 스레드별로 다른 인스턴스를 넘겨주게 할 수 있다.

2. 원한다면 정적 팩터리를 제네릭 싱글톤 팩터리로 만들 수 있다.
3. 정적 팩터리의 메소드 참조를 공급자(supplier)로 사용할 수 있다.

```java
// 코드1
public static final Singleton INSTANCE = new Singleton();

// 코드2
private static final Singleton INSTANCE = new Singleton();
public static Singleton getInstance() {
    return INSTANCE;
}
```

**코드1**과 같이 인스턴스 변수를 바로 가져와서 사용할 경우 공급자로 사용할 수 없다.
하지만 **코드2**와 같이 정적 팩터리 방식으로 만들면 `Singleton::getInstance`를 `Supplier<Singleton>`으로 사용할 수 있는 것이다.

### 열거 타입 방식의 싱글톤

```java
public enum Singleton {
    INSTANCE;
    
    public void say() {
        System.out.println("Hello, World!");
    }
}
```

앞서 확인한 두 가지 방식 중에서 가장 간결하며, 추가적인 노력 없이 직렬화할 수 있다.
또한, 다른 방식에 취약한 복잡한 직렬화나 역직렬화, 리플렉션 공격에서도 제 2의 인스턴스가 생기는 일을 완벽히 막아준다.

대부분의 상황에서는 원소가 하나뿐인 열거 타입이 싱그롵ㄴ을 만드는 가장 좋은 방식이다.

## 싱글톤의 장점

### 메모리적 이점

```java
Singleton instance = new Singleton();
```

위 코드와 같이 new 연산자를 통해 계속해서 객체를 생성하면 메모리에 여러 객체가 쌓이게 된다.

```java
// Singleton 클래스
public static final Singleton INSTANCE = new Singleton();

// Main 클래스
Singleton instance = Singleton.INSTANCE;
```

하지만 위와 같이 싱글톤 방식을 사용하면, 클래스가 올라갈 때 최초 한 번의 new 연산자를 통해 메모리에 고정해 메모리 낭비를 방지할 수 있다.

### 데이터 공유

싱글톤 인스턴스가 전역으로 사용되기 때문에 다른 클래스의 인스턴스가 접근하기 용이하다.
하지만 모든 코드가 동일하듯, 동시에 해당 인스턴스에 접근하게 되면 동시성 문제가 발생하게 된다.

이를 막기 위해서는 `syncronized`를 통해 멀티스레드 환경에서 동시에 접근하지 못하도록 해야한다.

## 단점

### 테스트 이슈

어플리케이션을 실행한 상태에서는 이미 싱글톤 객체는 메모리에 올라가있다.
때문에 테스트에서 정상적으로 사용하기 위해서는 싱글톤 인스턴스의 상태를 초기화시켜주어야 한다.

만약 내가 인스턴스의 멤버 변수의 값을 변경했다고 가정하자.
그 상태에서 테스트를 진행하면 변경된 값이 나오게 된다.
이러한 상황에서 정상적인 테스트는 어렵기 때문에 인스턴스를 새로 초기화시켜주는 것이 좋다.

## 정리

총 2가지 방식(1번 방식, 2번 방식) 중 공급자가 크게 필요하지 않을 경우에는 `public static final INSTANCE`를 시용하는 것이 효율적이다.
또한, 직렬화된 인스턴스를 역직렬화할 때마다 새로운 인스턴스가 만들어지는 불상사가 생길 수 있어 `readResolve` 메소드를 추가해주는 것이 좋다.

```java
private Object readResolve(){
    // 진짜 INSTANCE를 반환하고, 새로 만들어진 가짜 INSTANCE는 GC에 맡긴다.
    return INSTANCE;
}
```

싱글톤을 사용해야한다면 가능한 세 번째 방식을 사용하는 것이 좋다.