# try-finally 보다는 try-with-resources를 사용하라.

## try-finally란?

### try-catch

우선 `try-catch`에 대해서는 모두가 잘 알고 있을 것이다.

```java
public class Item9Main {
    String firstLineOfFile(String path) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String str = "fail";
        try {
            str = br.readLine();
        } catch (IOException e){
            System.out.println(e);
        }
        br.close();
        return str;
    }
}
```

위와 같이 `br.readLine()`을 실행했을 때 발생할 수 있는 `Exception`에 대한 처리를 하는 것이다.

### try-finally

`try-catch`는 발생할 수 있는 `Exception`을 처리하기 용이하다.
하지만 이는 발생하는 예외처리를 메소드 단위로 옮겨 생략할 수 있다.
그럼 `fianlly`는 어떤 용도에 사용하는 것일까?

```java
public class Item9Main {
    String firstLineOfFileOrigin(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
}
```

기본적으로 `finally`에 속한 코드는 `try`문이 끝나면 꼭 실행된다.
그렇기 때문에 사용한 자원을 닫을 때 주로 사용된다.

## 단점

### 자원이 많아질수록 어려워진다.

아래 코드는 파일이 있는 경로를 입력 받은 뒤, 버퍼에 입력하는 코드이다.

```java
public class Item9Main {
    void copyOriginal(String src, String dst) throws IOException {
        int BUFFER_SIZE = 100;
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
```

위 코드는 첫 예시와 다르게 2개의 자원을 예시로 작성되었다.
확실히 두 자원을 모두 닫으려니 코드가 복잡해지고 어려워진다.

## try-with-resources란?

이 기능은 Java 7에서 등장하였고, 위에서 언급한 단점들을 모두 보완할 수 있게 되었다.
위에서 작성한 `firstLineOfFileOrigin()`와 `copyOriginal()` 메소드를 리팩터링해보자.

```java
public class Item9Main {
    String firstLineOfFileRefactor(String path) throws IOException {
        try(BufferedReader br = new BufferedReader(
                new FileReader(path))) {
            return br.readLine();
        }
    }

    void copyRefactor(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        }
    }
}
```

위에서 작성한 `try-finally` 코드들에 비해 훨씬 간결해져 읽기 편해졌고,
문제를 진단하기도 훨씬 좋다.

## 장점

### 예외를 발견하기 쉽다.

`firstLineOfFileRefactor()` 메소드를 예시로 확인해보자.
`readLine()`과 `close()` 호출 양쪽에서 예외가 발생하면,
`close`에서 발생한 예외는 숨겨지고, `readLine`에서 발생한 예외가 기록된다.
즉, 프로그래머에게 보여줄 예외 하나만 보존되고 여러 개의 다른 예외가 숨겨질 수 있다.

숨겨진 예외들은 바로 버려지지 않으며, 스택 추적 내역에 suppressed(숨겨졌다) 라벨을 달고 출력된다.
만약 해당 예외를 가져오고 싶다면 `Throwable`에 추가된 `getSuppressed` 메소드를 이용하면 된다.

### catch와 함께 사용할 수 있다.

`try-with-resources`도 결국 `try`문이기 때문에 `catch`도 함께 사용이 가능하다.

```java
public class Item9Main {
    String firstLineOfFileRefactor(String path, String defaultVal) {
        try (BufferedReader br = new BufferedReader(
                new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            return defaultVal;
        }
    }
}
```

`catch`를 사용하면 `try`문을 중첩하지 않아도 다수의 예외를 처리할 수 있게 된다.

## 정리

예외를 잡기 위해 주로 `try-catch`만 사용했었다.
`finally`를 사용한 경험이 적어 크게 와닿지는 않았던 아이템인 것 같다.
하지만 앞으로 `try`문을 사용해야할 경우 이와 같이 `try-with-resources` 방식으로 개발해봐야할 것 같다.

> 꼭 자원을 회수해야할 경우 `try-finally`가 아닌 `try-with-resources`를 사용하자.
> 코드는 분명히 더 짧고, 분명해지며, 만들어지는 예외 정보도 훨씬 유용하다.