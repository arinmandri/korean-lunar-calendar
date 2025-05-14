
## 소개

Java 한국 음력. 아직 개발중.

음력 양력 변환, 날짜 계산, 간지 조회, 직렬화 등 가능.

java.time API와 호환.(표준 인터페이스 구현)

Korean Lunar Calendar in Java


## 한국 음력 소개

놀랍지 않습니까? 우리나라에서 이렇게 널리 쓰이는 달력인데 "한국 음력 이렇습니다" 하고 문서 하나로 정리해놓은 걸 구글 검색으로 못 찾았다. 단편들뿐이다. 게다가 그것들이 공식적인 자료들도 아니다. 어쨌든 취합하면 대충 이렇다:

태음태양력의 일종이다. 중국의 시헌력을 바탕으로 하고 있다. 그러나 중국과 우리나라의 시간대가 다르기 때문에 날짜가 완전히 일치하진 않는다.(아시아의 다른 나라 음력도 마찬가지)

- 달의 위상 변화에 따라 달력의 한 달을 정한다.
- 합삭(완전한 그믐달)이 포함되는 날짜를 1일로 한다.(시간대에 따라 날짜가 하루 달라지기도 하는 원인)
- 동지가 포함된 달을 11월로 한다.
- 열두 달은 1 태양주기보다 조금 짧으므로 보정이 필요한데; 무중치윤법에 따라 윤달을 넣는다.

무중치윤법

- 24절기는 12절기와 12중기로 구성된다.(절기와 중기가 번갈아 나옴) ※24절기는 달과는 상관없이 완전히 태양주기만을 기준으로 한다.
- 음력 월의 이름은(몇 월 숫자가 아니고 사실 이름이 있었음) 그 월에 포함된 중기의 이름에 따라 짓는다.
- 중기와 중기 사이 간격은 한 달보다 조금 더 길어서 중기가 없는 달이 나타나기도 하는데 그 달은 그 전달의 윤달인 것으로 취급한다.
- 약 19년에 7개 윤달이 들어간다.

내가 찾은 공식적인 무언가는 이것뿐이다:

- 법적으로는 천문법에 따라 우주항공청에서 월력요항(달력 제작의 기준이 되는 자료)를 발표한다.
- 우주항공청의 산하기관인 한국천문연구원에서 음양력 정보를 [공공데이터](https://www.data.go.kr/data/15012679/openapi.do)로 제공한다.



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
KoreanLunar 2000-12-07
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
KoreanLunar 2000-12-07
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

※ `KLunarDate`는 `java.time.LocalDate`와 마찬가지로 불변의 데이터클래스이며 셈 메서드들은 개체의 값이 바뀌는 기능이 아니고 새 값의 새 개체를 반환한다.

#### 필드 변경

```java
KLunarDate kd = KLunarDate.of( 2000, 1, 1 );
kd = kd.withYear( 2004 );// 년 변경
kd = kd.withMonth( 2 );// 월 변경
kd = kd.withMonthLeap( true );// 윤달 여부 변경
kd = kd.withDay( 10 );// 일 변경
```

- 일자가 30일인 날짜에서 30일이 없는 달로 변경하면 자동으로 29일로 조정된다.
- 윤달에서 윤달이 없는 날짜로 변경하면 자동으로 평달로 조정된다.

#### 덧셈 뺄셈

```java
KLunarDate kd = KLunarDate.of( 2000, 1, 1 );
kd = kd.plusYears( 4 );// 년 단위 덧셈
kd = kd.minusMonths( 2 );// 월 단위 뺄셈
kd = kd.plusNamedMonths( 4 );// 월 단위 덧셈 (윤달 무시)
kd = kd.minusDays( 2 );// 일 단위 뺄셈
```

#### 시간 간격

```java
KLunarDate kd1 = KLunarDate.of( 2001, 7, 8 );
KLunarDate kd2 = KLunarDate.of( 2003, 2, 2 );

System.out.println( kd1.until( kd2, ChronoUnit.YEARS ) );// 두 날짜의 해 차이
System.out.println( kd1.until( kd2, ChronoUnit.MONTHS ) );// 두 날짜의 월 차이
System.out.println( kd1.until( kd2, ChronoUnit.DAYS ) );// 두 날짜의 일 차이
```

```
1
18
555
```

```java
KLunarDate kd1 = KLunarDate.of( 2001, 7, 8 );
KLunarDate kd2 = KLunarDate.of( 2003, 2, 2 );

ChronoPeriod p = kd1.until( kd2 );// 두 날짜의 차이(몇개년+몇개월+몇개일)
System.out.println( p );
```

```
KoreanLunar P1Y6M24D
```




## 날짜 지원 범위

* **맨앞날**
  - 양력 1391-02-13 (그레고리력)
  - 음력 1391-01-01

* **맨뒷날**
  - 양력 2051-02-10 (그레고리력)
  - 음력 2050-12-29  
    한국천문연구원에서는 공식적으로 음력 2050-11-18까지만 알려주는데 11월이 대월이라고는 하니까 여기까지는 알 수 있음.





## …

- [블로그 메모](https://peekabook.tistory.com/entry/java-time-date)
