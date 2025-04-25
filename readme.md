
## 소개

Java 한국 음력. 아직 개발중.

TODO 음력 양력 변환, 날짜 계산, 간지 조회 등. java.time의 인터페이스 구현. 네트워크 불필요.

Korean Lunar Calendar in Java


## 쓰는 법

```java
import xyz.arinmandri.koreanlunarcalendar.KLunarDate;
```

### 날짜 개체 생성

```java
// 오늘 (시간대 지정 불가. 한국 시간대(UTC+9)로만 동작.)
KLunarDate kd = KLunarDate.now();

// 특정 음력 날짜(평달)
kd = KLunarDate.of( 2004, 2, 1 );
kd = KLunarDate.of( 2004, 2, false, 1 );
// 특정 음력 날짜(윤달)
kd = KLunarDate.of( 2004, 2, true, 1 );
// 특정 년의 n번째 날짜
kd = KLunarDate.ofYearDay( 2004, 30 );
```




### 다른 타입으로/에서 변환

#### 양력과의 상호 변환

```java
// 양력에서 음력으로
LocalDate ld = LocalDate.of( 2001, 1, 1 );// 양력 날짜
KLunarDate kd = KLunarDate.from( ld );
System.out.println( kd );
```

```
2000-12-07
```

```java
// 음력에서 양력으로
KLunarDate kd = KLunarDate.of( 2000, 12, 7 );
LocalDate ld = kd.toLocalDate();
System.out.println( ld );
```

```
2001-01-01
```


#### epoch day와의 상호 변환

```java
// epoch day에서 음력으로
long epochDay = 11323;
KLunarDate kd = KLunarDate.ofEpochDay( epochDay );
System.out.println( kd );
```

```
2000-12-07
```

```java
// 음력에서 epoch day로
KLunarDate kd = KLunarDate.of( 2000, 12, 7 );
long epochDay = kd.toEpochDay();
System.out.println( epochDay );
```

```
11323
```

※ epoch day: 1970년 1월 1일로부터의 경과 일수 (1970년 1월 1일은 0 epoch day)


#### 문자열과의 상호 변환

TODO 음력 표기에 표준도 딱히 없는 거 같고... 어쩔지 고민중.


### 간지(干支) 조회

```java
KLunarDate kd = KLunarDate.of( 1969, 11, 24 );
System.out.println( kd.getSecha() + "년" );
System.out.println( kd.getWolgeon() + "월" );
System.out.println( kd.getIljin() + "일" );
```

```
기유(己酉)년
병자(丙子)월
신사(辛巳)일
```

```java
KLunarDate kd = KLunarDate.of( 1969, 11, 24 );
System.out.println( kd.getSecha().toKoreanString() + "년" );
System.out.println( kd.getWolgeon().toKoreanString() + "월" );
System.out.println( kd.getIljin().toKoreanString() + "일" );
```

```
기유년
병자월
신사일
```

```java
KLunarDate kd = KLunarDate.of( 1969, 11, 24 );
System.out.println( kd.getSecha().toChineseString() + "년" );
System.out.println( kd.getWolgeon().toChineseString() + "월" );
System.out.println( kd.getIljin().toChineseString() + "일" );
```

```
己酉년
丙子월
辛巳일
```



### 날짜 계산

TODO 덧뺄셈, 특정 필드 값 변경, 비교

※ `KLunarDate`는 `java.time.LocalDate`와 마찬가지로 불변의 데이터클래스이며 셈 메서드들은 개체의 값이 바뀌는 기능이 아니고 새 값의 새 개체를 반환한다.





## 날짜 지원 범위

* **맨앞날**
  - 양력 1864-02-08
  - 음력 1864-01-01  
    TODO 지금은 이런 채로 개발 중이지만 훨씬 더 과거 데이터부터 싹 긁어 넣으면 범위 확장 뚝딱? 조선시대까지만 커버할까 생각 중.

* **맨뒷날**
  - 양력 2051-02-10
  - 음력 2050-12-29  
    한국천문연구원에서는 공식적으로 음력 2050-11-18까지만 알려주는데 11월이 대월이라고는 하니까 여기까지는 알 수 있음.

