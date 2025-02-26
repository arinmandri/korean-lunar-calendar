
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
kd = KLunarDate.of( 2004, 2, 1, false );
// 특정 음력 날짜(윤달)
kd = KLunarDate.of( 2004, 2, 1, true );
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

※ `KLunarDate`는 `java.time.LocalDate`와 마찬가지로 불변의 데이터클래스이며 셈 메서드들은 개체의 값이 바꾸는 기능이 아니고 새 값의 새 개체를 반환한다.





## 날짜 지원 범위

* **맨앞날**
  - 양력 1864-02-08
  - 음력 1864-01-01  
    TODO 지금은 이런 채로 개발 중이지만 훨씬 더 과거 데이터부터 싹 긁어 넣으면 범위 확장 뚝딱?

* **맨뒷날**
  - 양력 2051-02-10
  - 음력 2050-12-29  
    한국천문연구원에서는 공식적으로 음력 2050-11-18까지만 알려주는데 11월이 대월이라고는 하니까 여기까지는 알 수 있음.






## 음력이 대체 뭐냐

맞다. 내가 문외한이라곤 해도 내 나름 라이브러리 만들겠다는데 뭘 알아야 할 것 아닌가?

그래서 찾아봤는데 결론은 모르겠다고.

한 달의 기준은 달의 규칙적인 위상변화, 우리가 잘 아는 그믐달-반달-보름달-반달-그믐달. 그 중에서도 합삭(쉽게 말해 완전한 그믐달의 순간)이 포함된 날을 1일으로 삼음. 주기는 대략 29.5일이므로 29일인 달과 30일인 달이 있으니 각각 소월, 대월이라고 함. 그런데 4년에 한 번 윤년이 있고 어쩌고 하는 양력과는 달리 얘는 몇 년에 한 번 이런 규칙이 없고 실제 달이 합삭일 때를 기준으로 함. 그런데 음력 12개월과 1해의 길이가 꽤나 차이나므로 가끔 윤달을 넣는다. 여기까지 오케이.

근데 윤달 언제 넣음? 몰라.

그 중에서도 몇 월이 1월이다? 몰라.

아니; 사실은 시헌력이니, 동지가 11월이니, 치윤법이 어쩌고 하는 문서들을 찾기는 했는데. 그것들은 죄다 단편적이었고 뭔가 공식적인, 공신력 있는 어쩌고도 아니잖아? "아 내가 여기서 찾아봤습니다" 하고 어떻게 얘기하냐고. 음력은 시헌력이라고 나무위키가 그래. 그런데 나무위키가 없다면 음력이 뭐야? 아! 모르겠다! 그리고 시헌력도 하나로 딱 있지가 않고 그 안에서 뭐가 어쩌고 한다. 이게 도대체? 내가 할 줄 아는 거라곤 구글에 검색하고 인공지능한테 물어보기밖에 없다고!

내가 알아낸 거라곤 우주항공청장이라는 분이 우리나라 달력을 발표한다는 천문법뿐이야. 근데 바로 그 분이! "우리나라 음력은 이렇게 만든답니다" 하고 소개를 안 해주시잖아. 그런 게 대체 어딨냐고!! 진짜 나만 몰라? 왜 다들 모르는 척을 안 해?

그래도 어쨌든 우리의 친절한 [한국천문연구원 API](https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15012679)이가 알려주는 음력 월별 1일 날짜에 남은 일수만 더하면 음력날짜 짜잔 완성이다. 이런 바로 나같은 문외한도 음력 라이브러리를 만든다. 아무튼 공공데이터 API가 알려주는 값이랑 비교해서 똑같은 값 나오니 좋았쓰. 테스트 통과.
