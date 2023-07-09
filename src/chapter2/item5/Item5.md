# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라.

## 의존이란?

대부분의 클래스는 하나 이상의 자원에 의존하게 된다.
아래 코드는 맞춤법 검사기에 대한 코드이며, `Lexicon` 이라는 사전을 의존해 단어를 검색한다.
만약 `Lexicon`이 없다면 해당 클래스는 큰 의미가 없게 된다.

```java
public class SpellChecker {
    private static final Lexicon dictionary = new Lexicon();

    private SpellChecker() { }

    public static boolean isValid(String word) {
        return dictionary.findWord(word);
    }
}
```

위 코드는 `SpellChecker.isValid("word")`와 같은 방식으로 사용될 수 있다.
이 방식을 싱글톤으로 구현하면 아래와 같다.

```java
public class SpellChecker {
    private static final Lexicon dictionary = new Lexicon();

    private SpellChecker() { }
    public static SpellChecker INSTANCE = new SpellChecker();

    public static boolean isValid(String word) {
        return dictionary.findWord(word);
    }
}
```

위에서 보여준 두 방식은 모두 단 하나의 사전만 사용한다는 가정으로 활용할 수 있다.
즉, 이 하나의 사전으로 모든 쓰임에 대응할 수 없다는 큰 단점이 존재한다.

이렇게 여러 용도로 활용할 수 있어야하는 유틸리티 클래스를
정적 유틸리티로 만들어버리면 여러 제약이 따르게 된다.

이를 보완하기 위해 의존객체 주입을 사용한다.

## 의존 객체 주입

앞서 보여준 방식의 큰 문제는 다양한 용도로 쓰이기 어렵다는 것이다.
이를 해결하기 위해 아래와 같은 방식으로 변경할 수 있다.

```java
public class SpellChecker {
    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {
        return dictionary.findWord(word);
    }
}
```

```java
public class Lexicon {
    private final String language;
    Lexicon(String language) {
        this.language = language;
    }
    public boolean findWord(String str) {
        return dictionary.contains(str);
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Lexicon eng = new Lexicon("eng");
        SpellChecker checker = new SpellChecker(eng);
    }
}
```

이와 같은 방식으로 만들면 사용자가 원하는대로 사전을 선택할 수 있으며,
`dictionary`는 불변을 보장하여 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다.

## 정리

클래스가 내부적으로 하나 이상의 자원에 의존하고,
그 자원이 클래스 동작에 영향을 준다면
싱글톤과 정적 유틸리티 클래스는 사용하지 않는 것이 것이 좋다.
또한, 이 자원들을 클래스가 직접 만들게 해서도 안 된다.
필요한 자원에 대해서는 생성자를 통해 넘겨주는 것이 좋다.