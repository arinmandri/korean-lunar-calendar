
## 소개

Java 한국 음력. java.time API와 호환.(표준 인터페이스 구현)

음력 양력 변환, 날짜 계산, 간지 조회, 직렬화 등 가능.

Korean Lunar Calendar in Java


## 한국 음력 소개

놀랍지 않습니까? 우리나라에서 이렇게 널리 쓰이는 달력인데 "한국 음력 이렇습니다" 하고 문서 하나로 정리해놓은 걸 구글 검색으로 못 찾았다. 단편들뿐이다. 게다가 그것들이 공식적인 자료들도 아니다. 어쨌든 취합하면 대충 이렇다:

태음태양력의 일종이다. 중국의 시헌력을 바탕으로 하고 있다. 그러나 중국과 우리나라의 시간대가 다르기 때문에 날짜가 완전히 일치하진 않는다.(아시아의 다른 나라 음력도 마찬가지)

- 달의 위상 변화에 따라 달력의 한 달을 정한다.
- 합삭(완전한 그믐달)이 포함되는 날짜를 1일로 한다.(시간대에 따라 날짜가 하루 달라지기도 하는 원인)
- 동지가 포함된 달을 11월로 한다.
- 열두 달은 1 태양주기보다 조금 짧으므로 보정이 필요한데; 무중치윤법에 따라 윤달을 넣는다.

무중치윤법

- 24절기는 12절기와 12중기로 구성된다.(절기와 중기가 번갈아 나옴) ※24절기는 달과는 상관없이 오롯이 태양주기만을 기준으로 한다.
- 음력 월의 이름은(몇 월 숫자가 아니고 사실 이름이 있었음) 그 월에 포함된 중기의 이름에 따라 짓는다.
- 중기와 중기 사이 간격은 한 달보다 조금 더 길어서 중기가 없는 달이 나타나기도 하는데 그 달은 그 전달의 윤달인 것으로 취급한다.
- 약 19년에 7개 윤달이 들어간다.

내가 찾은 공식적인 무언가는 이것뿐이다:

- 법적으로는 천문법에 따라 우주항공청에서 월력요항(달력 제작의 기준이 되는 자료)를 발표한다.
- 우주항공청의 산하기관인 한국천문연구원에서 음양력 정보를 [공공데이터](https://www.data.go.kr/data/15012679/openapi.do)로 제공한다.

이 라이브러리도 물론 그 공공데이터를 바탕으로 만들어졌다.



## 라이브러리 가져오기

Maven에서

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/arinmandri/korean-lunar-calendar</url>
</repository>
```

```xml
<dependency>
  <groupId>xyz.arinmandri</groupId>
  <artifactId>korean-lunar-calendar</artifactId>
  <version>1.0</version>
</dependency>
```

## 쓰는 법

```java
import xyz.arinmandri.koreanlunarcalendar.*;
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
LocalDate ld = LocalDate.from( kd );// ld = kd.toLocalDate(); 이렇게도 가능
System.out.println( ld );
```

```
2001-01-01
```


#### epoch day와의 상호 변환

※ epoch day: 1970년 1월 1일로부터의 경과 일수 (1970년 1월 1일은 0 epoch day)

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


#### 문자열과의 상호 변환

```java
// 기본 출력: toString
KLunarDate kd = KLunarDate.of( 1950, 10, 13 );
String str = kd.toString();
System.out.println( str );
// KoreanLunar 1950-10-13

// parse를 toString의 역연산으로 쓸 수 있다.
KLunarDate kd1 = KLunarDate.parse( str );
System.out.println( kd1 );
// KoreanLunar 1950-10-13
```

윤달인 경우 일 부분에 30이 더해진다. 예를 들어 2004년 윤2월 1일은 "2004-02-31"로 출력된다. 이상하다고 여겨질지 모르지만 이놈의 윤달 때문에 이 문자열 변환뿐 아니라 여러 군데에서 골치 많이 아팠다. 내 나름의 고심의 결과이다. 이렇게 하면 (ISO-8601의 형식들처럼) 날짜에 관계 없이 글자 수가 일정하며, 글자순 정렬 결과가 날짜순 정렬과 일치하게 되면서도; 그 문자열이 나타내는 날짜가 윤달인지 아닌지, 윤달의 며칠인지도 바로 알 수 있다.

포매터를 직접 정의할 수도 있지만 미리정의된 포매터가 몇 가지 있다.(아래 예시에서 일부 사용) 그 중 일부는 `toString()`처럼 윤달의 일에 +30하고 어떤 것들은 "윤" 혹은 "L" 글자를 붙인다.

```java
// 특정 형식으로 출력
KLunarDate kd;
DateTimeFormatter f = KLDateFormatters.HUMAN_DATE;

kd = KLunarDate.of( 2004, 2, 8 );
System.out.println( kd.format( f ) );
// 2004년 2월 8일

kd = KLunarDate.of( 2004, 2, true, 8 );
System.out.println( kd.format( f ) );
// 2004년 윤2월 8일

f = KLDateFormatters.SLASHED_DATE;
kd = KLunarDate.of( 2004, 2, 8 );
System.out.println( kd.format( f ) );
// 2004/2/8

f = KLDateFormatters.SIX_DIGITS;
kd = KLunarDate.of( 2004, 2, 8 );
System.out.println( kd.format( f ) );
// 040208
```

```java
// 특정 형식의 문자열 해석
DateTimeFormatter f = KLDateFormatters.HUMAN_DATE;

String str1 = "2030년 1월 1일";
KLunarDate kd1 = f.parse( str1, KLunarDate::from );
System.out.println( kd1 );
// KoreanLunar 2030-01-01

String str2 = "2031년 윤3월 13일";
KLunarDate kd2 = f.parse( str2, KLunarDate::from );
System.out.println( kd2 );
// KoreanLunar 2031-03-43

f = KLDateFormatters.SIX_DIGITS;// 년도는 1951년에서 2050년까지인 것으로 해석된다.
String str3 = "980913";
KLunarDate kd3 = f.parse( str3, KLunarDate::from );
System.out.println( kd3 );
// KoreanLunar 1998-09-13
```



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

#### 기간(시간 간격) 구하기

```java
KLunarDate kd1 = KLunarDate.of( 2001, 7, 8 );
KLunarDate kd2 = KLunarDate.of( 2003, 2, 2 );

System.out.println( kd1.until( kd2, ChronoUnit.YEARS ) );// 두 날짜의 해 차이
System.out.println( kd1.until( kd2, ChronoUnit.MONTHS ) );// 두 날짜의 달 차이
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
