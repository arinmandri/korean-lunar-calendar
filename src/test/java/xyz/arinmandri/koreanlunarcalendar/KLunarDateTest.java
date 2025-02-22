package xyz.arinmandri.koreanlunarcalendar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest
{
	protected final int testSize = 50;

	ApiService api = ApiService.getInstance();
	Random random = new Random( System.currentTimeMillis() );

	/*
	 * KLunarDate private 값들 땜쳐옴
	 */
	final int YEAR_BASE = 1864;// KLunarDate.YEAR_BASE

	protected static final int EPOCH_0_JDAY = 2440588;// 양력(그레고리력) 1970년 1월 1일 = 0 epoch day = 2440588 율리우스적일
	protected static final int[] epochDays = {
	        2401910 - EPOCH_0_JDAY,// 1864
	        2423821 - EPOCH_0_JDAY,// 1924
	        2445733 - EPOCH_0_JDAY,// 1984
	        2467645 - EPOCH_0_JDAY,// 2044
	        2470214 - EPOCH_0_JDAY,// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)
	};

	final LocalDate MIN = LocalDate.ofEpochDay( epochDays[0] );
	final LocalDate MAX0 = LocalDate.of( 2050, 12, 31 );// 정답의 기준이 한국천문연구원 API인데 의 지원범위보다 KLunarDate.MAX가 살짝 더 미래이기 때문에 한국천문연구원 API의 지원범위를 직접 입력함.
	final LocalDate MAX1 = LocalDate.ofEpochDay( epochDays[epochDays.length - 1] - 1 );// 한국천문연구원 API를 안 쓰는 경우 여기까지 시험
	final long MIN_EPOCH = MIN.toEpochDay();
	final long MAX_EPOCH = MAX1.toEpochDay();

	//// ================================ util, private, etc

	protected boolean checkEquality ( Item item , KLunarDate kd ) {
		return item.getLunYear()  == kd.getYear()
		    && item.getLunMonth() == kd.getMonth()
		    && item.getLunDay()   == kd.getDay()
		    && item.getLunLeapmonth().equals( "윤" ) == kd.isLeapMonth();
	}

	protected boolean checkEquality ( Item item , LocalDate ld ) {
		return item.getSolYear()  == ld.getYear()
		    && item.getSolMonth() == ld.getMonthValue()
		    && item.getSolDay()   == ld.getDayOfMonth();
	}

	protected LocalDate getRandomLd () {// 지원범위0 내에서
		return getRandomLd( MIN, MAX0 );
	}

	protected LocalDate getRandomLd ( LocalDate d1 , LocalDate d2 ) {// 이상, 이하

		long n = ChronoUnit.DAYS.between( d1, d2 );// 시작일~종료일 일수
		long randomDays = random.nextLong( n + 1 );// 랜덤 숫자 뽑기
		return d1.plusDays( randomDays );
	}

	protected int getRandomInt ( int a , int b ) {// 이상, 이하
		return random.nextInt( b - a + 1 ) + a;
	}

	protected String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}
}
