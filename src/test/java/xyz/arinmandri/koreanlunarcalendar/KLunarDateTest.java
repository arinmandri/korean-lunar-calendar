package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest
{
	protected final int testSize = 100;

	ApiService api = ApiService.getInstance();
	Random random = new Random( System.currentTimeMillis() );
	ZoneId zoneId = ZoneId.of("Asia/Seoul");

	/*
	 * KLunarDate private 값들 땜쳐옴
	 */
	static final int BIG_MONTH_SIZE = 30;// 대월의 일수
	static final int LIL_MONTH_SIZE = 29;// 소월의 일수
	static final int[][] ydss = {// yd = ydss[c0][y0]
	        {
	                0x0001E6D4, 0x02C4ADA9, 0x05C5EEC9, 0x088BEE92, 0x0B4E8D26, 0x0E4DE527, 0x11114A57, 0x1411E95B, 0x16D7EB5A, 0x199CD6D4, // 1864
	                0x1C9DE754, 0x1F61E749, 0x2224B693, 0x2525EA93, 0x27E9E52B, 0x2AAC6A5B, 0x2DADE96D, 0x3072EB6A, 0x3373EDAA, 0x3639EBA4, // 1874
	                0x38FCBB49, 0x3BFDED49, 0x3EC1EA95, 0x4184952B, 0x4485E52D, 0x4749EAAD, 0x4A0E556A, 0x4D0FEDAA, 0x4FD4DDA4, 0x52D5EEA4, // 1884
	                0x5599ED4A, 0x585CAA95, 0x5B5BEA97, 0x5E21E556, 0x60E46AB5, 0x63E5EAD5, 0x66AB16D2, 0x69ABE752, 0x6C6FEEA5, 0x6F34B64A, // 1894
	                0x7233E64B, 0x74F7EA9B, 0x77BC9556, 0x7ABDE56A, 0x7D81EB59, 0x80465752, 0x8347E752, 0x860ADB25, 0x890BEB25, 0x8BCFEA4B, // 1904
	                0x8E92B29B, 0x9193EAAD, 0x9459E56A, 0x971C4B69, 0x9A1DEBA9, 0x9CE2FB52, 0x9FE3ED92, 0xA2A7ED25, 0xA56ABA4D, 0xA86BE956, // 1914
	        },
	        {
	                0x0001E2B5, 0x02C495AD, 0x05C7E6D4, 0x088BEDA9, 0x0B505D92, 0x0E51EE92, 0x1114CD26, 0x1413E527, 0x16D7EA57, 0x199CB2B6, // 1924
	                0x1C9DEADA, 0x1F63E6D4, 0x22266EA9, 0x2527E749, 0x27EAF693, 0x2AEBEA93, 0x2DAFE52B, 0x3072CA5B, 0x3373E96D, 0x3639EB6A, // 1934
	                0x38FE9B54, 0x3BFFEBA4, 0x3EC3EB49, 0x41865A93, 0x4487EA95, 0x474AF52B, 0x4A4BE52D, 0x4D0FEAAD, 0x4FD4B56A, 0x52D5EDB2, // 1944
	                0x559BEDA4, 0x585E7D49, 0x5B5FED4A, 0x5E231A95, 0x6123EA96, 0x63E7E556, 0x66AACAB5, 0x69ABEAD5, 0x6C71E6D2, 0x6F348EA5, // 1954
	                0x7235EEA5, 0x74FBEE4A, 0x77BE6C96, 0x7ABDEA9B, 0x7D82F556, 0x8083E56A, 0x8347EB59, 0x860CB752, 0x890DE752, 0x8BD1E725, // 1964
	                0x8E94964B, 0x9195EA4B, 0x945912AB, 0x9759E2AD, 0x9A1DE56B, 0x9CE2CB69, 0x9FE3EDA9, 0xA2A9ED92, 0xA56C9B25, 0xA86DED25, // 1974
	        },
	        {
	                0x00015A4D, 0x0301EA56, 0x05C5E2B6, 0x0888D5AD, 0x0B8BE6D4, 0x0E4FEDA9, 0x1114BD92, 0x1415EE92, 0x16D9ED26, 0x199C6A56, // 1984
	                0x1C9BEA57, 0x1F6112B6, 0x2261EB5A, 0x2527E6D4, 0x27EAAEC9, 0x2AEBE749, 0x2DAFE693, 0x30729527, 0x3373E52B, 0x3637EA5B, // 1994
	                0x38FC555A, 0x3BFDE36A, 0x3EC0FB55, 0x41C3EBA4, 0x4487EB49, 0x474ABA93, 0x4A4BEA95, 0x4D0FE52D, 0x4FD26A5D, 0x52D3EAAD, // 2004
	                0x559935AA, 0x5899E5D2, 0x5B5DEDA5, 0x5E22BD4A, 0x6123ED4A, 0x63E7EA95, 0x66AA952D, 0x69ABE556, 0x6C6FEAB5, 0x6F3455AA, // 2014
	                0x7235E6D2, 0x74F8CEA5, 0x77F9EEA5, 0x7ABFEE4A, 0x7D82AC96, 0x8081EC9B, 0x8347E55A, 0x860A6AD5, 0x890BEB69, 0x8BD17752, // 2024
	                0x8ED1E752, 0x9195EB25, 0x9458D64B, 0x9759EA4B, 0x9A1DE4AB, 0x9CE0A55B, 0x9FE1E56D, 0xA2A7EB69, 0xA56C5B52, 0xA86DED92, // 2034
	        },
	        {
	                0x0000FD25, 0x0301ED25, 0x05C5EA4D, 0x0888B4AD, 0x0B89E2B6, 0x0E4DE5B5, 0x11126DA9, // 2044
	        },
	};
	static final int EPOCH_0_JDAY = 2440588;
	static final int[] epochDays = {
	        2401910 - EPOCH_0_JDAY,// 1864
	        2423821 - EPOCH_0_JDAY,// 1924
	        2445733 - EPOCH_0_JDAY,// 1984
	        2467645 - EPOCH_0_JDAY,// 2044
	        2470214 - EPOCH_0_JDAY,// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)
	};

	static final int YEAR_MIN = 1864;// 최소 년도 (갑자년부터 시작)
	static final int YEAR_MAX = YEAR_MIN + ( ydss.length - 1 ) * CYCLE_SIZE + ydss[ydss.length - 1].length - 1;// 최대년도

	final int EPOCH_DAY_MIN = epochDays[0];
	final int EPOCH_DAY_MAX = epochDays[epochDays.length - 1] - 1;

	final LocalDate MIN = LocalDate.ofEpochDay( EPOCH_DAY_MIN );
	final LocalDate MAX0 = LocalDate.of( 2050, 12, 31 );// 정답의 기준이 한국천문연구원 API인데 의 지원범위보다 KLunarDate.MAX가 살짝 더 미래이기 때문에 한국천문연구원 API의 지원범위를 직접 입력함.
	final LocalDate MAX1 = LocalDate.ofEpochDay( EPOCH_DAY_MAX );// 한국천문연구원 API를 안 쓰는 경우 여기까지 시험

	//// ================================ repeat test

	void repeat ( Runnable test , String title , int size ) {
		System.out.println( "\n=== " + title + " ===" );

		for( int i = 0 ; i < size ; i += 1 ){
			try{
				test.run();
			}
			catch( NoNeedToTest e ){
				i -= 1;
				continue;
			}

			if( i % 10 == 9 ){
				System.out.println( i + 1 + "째 시험 통과" );
			}
		}
	}

	void repeat ( Runnable test , String title ) {
		repeat( test, title, testSize );
	}

	//// ================================ util, private, etc

	boolean checkEquality ( Item item , KLunarDate kd ) {
		return item.getLunYear()  == kd.getYear()
		    && item.getLunMonth() == kd.getMonth()
		    && item.getLunDay()   == kd.getDay()
		    && item.getLunLeapmonth().equals( "윤" ) == kd.isLeapMonth();
	}

	boolean checkEquality ( Item item , LocalDate ld ) {
		return item.getSolYear()  == ld.getYear()
		    && item.getSolMonth() == ld.getMonthValue()
		    && item.getSolDay()   == ld.getDayOfMonth();
	}

	LocalDate getRandomLd () {// 지원범위0 내에서
		return getRandomLd( MIN, MAX0 );
	}

	LocalDate getRandomLd ( LocalDate d1 , LocalDate d2 ) {// 이상, 이하

		long n = ChronoUnit.DAYS.between( d1, d2 );// 시작일~종료일 일수
		long randomDays = random.nextLong( n + 1 );// 랜덤 숫자 뽑기
		return d1.plusDays( randomDays );
	}

	int getRandomEpochDay () {
		return getRandomInt( EPOCH_DAY_MIN, EPOCH_DAY_MAX );
	}

	int getRandomInt ( int a , int b ) {// 이상, 이하
		return random.nextInt( b - a + 1 ) + a;
	}

	String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}

	class NoNeedToTest extends RuntimeException
	{}
}
