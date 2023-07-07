# 생성자에 매개변수가 많다면 빌더를 고려하라.

## 매개변수(parameter)란?

함수의 정의에서 전달받은 인자를 함수 내부로 전달하기 위해 사용하는 변수

```java
public class TestCode {
    // int a, int b : 매개변수
    static int sum(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        // 1, 2 : 인자
        int sum = sum(1, 2);
    }
}
```

## 왜 빌더를 고려할까?

아래 패턴들을 통해 빌더 패턴을 사용하는 이유에 대해 알아보자!

### 점층적 생성자 패턴

> 생성자의 선택 매개변수를 점층적으로 늘려나가 최종적으로 모든 선택 매개변수를 다 받는 생성자까지 늘려가는 방식 

```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;

    public NutritionFacts(int servingSize) {
        this(servingSize, 0);
    }

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }
    
    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
    }
}

```

만약 한 클래스에 20개가 넘는 변수가 있다고 가정하자.
이러한 클래스용 생성자 혹은 정적 팩터리에서는 '점층적 생성자 패턴(telescoping constructor pattern)'을 주로 사용한다.

하지만 이 방식은 매개변수가 많아질수록 각 값의 의미가 헷갈리게 되고, 매개변수가 몇 개인지도 주의해야해서 코드를 작성하거나 읽기 어려워진다.

가장 큰 문제는 클라이언트가 실수로 매개변수의 순서를 바꿔 건내줘도 컴파일러가 알아채지 못한다는 것이다!

### 자바빈즈 패턴(JavaBeans pattern)

> 매개변수가 없는 생성자로 객체를 만든 뒤, `setter` 메소드를 통해 매개 변수의 값을 지정하는 방식

```java
public class NutritionFacts {
    private int servingSize;
    private int servings;
    private int calories;

    public NutritionFacts(){ }

    public void setServingSize(int servingSize) { this.servingSize = servingSize; }

    public void setServings(int servings) { this.servings = servings; }

    public void setCalories(int calories) { this.calories = calories; }
}
```

점층적 생성자 패턴에 비해 간결해져서 읽기 쉬운 코드가 되었다.

```java
NutritionFacts n = new NutritionFacts();
n.setServingSize(240);
n.setServings(8);
n.setCalories(100);
```

하지만 객체 하나를 만들기 위해 여러 개의 메소드를 호출해야하고, 객체가 완전히 생성되기 전까지는 **객체의 일관성(consistency)가 무너진 상태**에 놓이게 된다. 이러한 문제 때문에 불변 클래스로 만들 수 없다.

### 빌더 패턴(Builder pattern)

> 필요한 객체를 직접 만드는 대신, 필수 매개변수만으로 생성자(혹은 정적 팩터리)를 호출해 빌더 객체를 얻는다.

```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;

    public static class Builder {
        private final int servingSize;
        private final int servings;

        private int calories = 0;
        private int fat = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
    }
}
```

`NutritionFacts` 클래스는 모든 변수가 `final`이기 때문에 불변이다.

빌더의 세터 메소드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출이 가능하며, 이런 방식을 흐르듯 연결된다는 뜻으로 `플루언트 API(fluent API)` 혹은 `메소드 연쇄(method chaining)`라 한다.

```java
NutritionFacts cola = new NutritionFacts.Builder(240, 8)
        .calories(100)
        .fat(0)
        .build();
```

[링크](https://github.com/Jwhyee/effective-java/blob/master/src/item2/Pizza.java)에 있는 코드를 확인해보자.

```java
public abstract class Pizza {
    ...
    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        protected abstract T self();
    }
}
```

`Pizza`라는 추상 클래스에는 추상 `Builder`를 갖게 하였고,
[NyPizza](https://github.com/Jwhyee/effective-java/blob/master/src/item2/NyPizza.java) 클래스와 
[CalzonePizza](https://github.com/Jwhyee/effective-java/blob/master/src/item2/CalzonePizza.java) 클래스는
구체 클래스(concrete class)이므로 구체 빌더를 갖게 하였다.

```java
import static item2.Pizza.Topping.*;

NyPizza newYorkPizza = new NyPizza.Builder(NyPizza.Size.SMALL)
        .addTopping(SAUSAGE)
        .addTopping(ONION)
        .build();

CalzonePizza calzonePizza = new CalzonePizza.Builder()
        .addTopping(HAM)
        .sauceInside()
        .build();
```

위 코드는 각 구체 클래스의 빌더를 통해 만든 객체이다. 

`.addTopping()` 메소드는 `Pizza.Builder`에 속해있다. 그런데 왜 형변환 없이 메소드 연쇄를 사용할 수 있었을까?

```java
// Pizza.Builder 내부 메소드
abstract static class Builder<T extends Builder<T>> {
    public T addTopping(Topping topping) {
        toppings.add(Objects.requireNonNull(topping));
        return self();
    }
    
    protected abstract T self();
}

// NyPizza.Builder 내부 메소드
public static class Builder extends Pizza.Builder<Builder> {
    @Override
    protected Builder self() {
        return this;
    }
}
```

`Pizza`는 추상 클래스이며, 그 안에 있는 `Builder` 또한 추상 클래스에 속하므로 `this`를 반환하면 안 된다.

때문에 상속받은 `Builder` 클래스를 그대로 반환하기 위해 `self()` 만들어 메소드 연쇄를 지원할 수 있게 하였다.

`NyPizza.Builder`, `CalzonePizza.Builder`와 같은 구체 하위 클래스에서 `Pizza.Builder`를 상속받고,
구체 하위 클래스에 정의한 `Builder`를 제네릭스로 활용해 **재귀적 타입 한정**에 대한 이점을 볼 수 있다.

## 정리

이와 같이 빌더 패턴은 **점층적 생성자 패턴**과 **자바빈즈 패턴**에 비해 다양한 이점이 있다.

1. 가변인수(varargs) 매개변수를 여러개 사용할 수 있다.
2. 객체의 일관성을 보장한다.

하지만, 객체를 만들기 전에 빌더 패턴 먼저 만들어야하며, 매개변수 4개 이상은 되어야 값어치를 한다.

> 생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면, 빌더 패턴을 선택하는게 더 낫다.