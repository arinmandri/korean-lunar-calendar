package xyz.arinmandri.koreanlunarcalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;


/**
 * 한국 음력으로 특정 날짜를 가리킨다. 불변(immutable)이다.
 * represents a date in Korean lunar calendar system
 * 
 * 음력 날짜는 다음 네 가지 값으로 특정된다: 년도, 월, 일, 윤달여부
 * 
 * java.time.LocalDate랑 최대한 비슷한 형식 + 간지 등 추가정보 제공(하고싶다. TODO )
 */
public final class KLunarDate implements java.io.Serializable , ChronoLocalDate
{
	private static final long serialVersionUID = 0L;

	final int year;
	final int month;
	final int day;
	final boolean isLeapMonth;// 윤달 여부

	private transient final int c0;// 해들을 주기(육십갑자 60년) 단위로 묶는다. 여기 정의된 묶음들 중 몇 번째 묶음에 속하는가 (0부터 셈)
	private transient final int y0;// 이 주기의 몇 번째 년인가 (0부터 셈)
	private transient final int m0;// 이 년도의 몇 번째 월인가 (0부터 셈)
	private transient final int d0;// 이 년도의 몇 번째 일인가 (0부터 셈)

	public static final int BIG_MONTH_SIZE = 30;// 대월의 일수
	public static final int LIL_MONTH_SIZE = 29;// 소월의 일수

	public static final int CYCLE_SIZE = 60;

	// TODO epoch day

	/*
	 * int 하나에 32비트로 한 해의 정보 저장
	 * 왼쪽부터
	 * 15비트: 이 주기의 첫날과 이 년의 첫 날의 날짜 차이 (일 단위)
	 *         yd >>> 17
	 * 4비트: 몇 월에 윤달이 있나 (예: 이 값이 1이면 1월에 윤달이 있으며 2번째 달이 윤달이다.) (0xF인 경우 윤달 없음) (0인 경우 없음)
	 *        (yd >>> 13) & 0xF
	 * 13비트: 각 월의 대소(0:소 1:대) (0번째 달부터 12번째 달까지 윤달도 똑같이 한 달로 취급, 1의자리부터 1월)
	 *         (yd >>> m0) & 0x1
	 */
	private static final int[][] ydss = {// yd = ydss[c0][y0]
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

	/*
	 * 지원범위 첫 날로부터 각 주기의 첫 날의 차이 (일 단위)
	 */
	private static final int[] jDays = {
	        2401910 - 2401910,// 1864
	        2423821 - 2401910,// 1924
	        2445733 - 2401910,// 1984
	        2467645 - 2401910,// 2044
	        2470214 - 2401910,// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)
	};

	private static final int YEAR_BASE = 1864;// 최소 년도 (갑자년을 최소년도로 설정해야 (TODO 나중에) 갑자 계산이 맞게 동작)
	public static final LocalDate MIN = LocalDate.of( 1864, 2, 8 );
	public static final LocalDate MAX = MIN.plusDays( jDays[jDays.length - 1] - 1 );

	private KLunarDate( int year , int month , int day , boolean isLeapMonth , int c0 , int y0 , int m0 , int d0 ) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.isLeapMonth = isLeapMonth;

		this.c0 = c0;
		this.y0 = y0;
		this.m0 = m0;
		this.d0 = d0;
	}

	//// ================================ CREATION

	/**
	 * 음력 년월일로 새 날짜개체 생성
	 *
	 * @param year        년도
	 * @param month       월
	 * @param day         일
	 * @param isLeapMonth 윤달 여부
	 * 
	 * @return 생성된 새 날짜 개체
	 * 
	 * @throws NonexistentDateException 해당 날짜 없음
	 * @throws OutOfRangeException      지원 범위 밖
	 */
	public static KLunarDate of ( int year , int month , int day , boolean isLeapMonth ) {

		if( year < YEAR_BASE ) throw new OutOfRangeException();
		if( month < 1 ) throw new NonexistentDateException();
		if( month > 12 ) throw new NonexistentDateException();
		if( day < 1 ) throw new NonexistentDateException();

		//// 몇번째 년도?
		int c0 = ( year - YEAR_BASE ) / CYCLE_SIZE;
		int y0 = ( year - YEAR_BASE ) % CYCLE_SIZE;
		if( c0 >= ydss.length ) throw new OutOfRangeException();
		if( y0 >= ydss[c0].length ) throw new OutOfRangeException();
		int yd = ydss[c0][y0];

		//// 몇번째 월인지 찾기
		int leapMonth = ( yd >>> 13 ) & 0xF;
		boolean hasLeapMonth = leapMonth < month;
		int m0 = month - 1
		        + ( hasLeapMonth ? 1 : 0 )
		        + ( isLeapMonth ? 1 : 0 );

		int monthSize = ( ( yd >> m0 ) & 0x1 ) == 0x1
		        ? BIG_MONTH_SIZE
		        : LIL_MONTH_SIZE;

		//// 이 해의 몇번째 날인지 찾기
		if( day > monthSize ) throw new NonexistentDateException();
		int d0 = 0;
		for( int i = 0 ; i < m0 ; i++ ){// 이전월까지의 일수 합
			d0 += ( ( yd >>> i ) & 0x1 ) == 1
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;
		}
		d0 += day - 1;// 이번 월의 몇번째 날? (0부터 셈)

		return new KLunarDate( year , month , day , isLeapMonth , c0 , y0 , m0 , d0 );
	}

	/**
	 * 음력 년월일로 새 날짜개체(윤달 아님) 생성
	 *
	 * @param year  년도
	 * @param month 월
	 * @param day   일
	 * 
	 * @return 생성된 새 날짜 개체
	 * 
	 * @throws NonexistentDateException 해당 날짜 없음
	 * @throws OutOfRangeException      지원 범위 밖
	 */
	public static KLunarDate of ( int year , int month , int day ) {
		return of( year, month, day, false );
	}

	/**
	 * 어떤 년도의 n번째 날짜
	 *
	 * @param year      년도(음력)
	 * @param dayOfYear 그 년도의 몇 번째 일인가(1부터 셈)
	 * 
	 * @return 생성된 새 날짜 개체
	 * 
	 * @throws NonexistentDateException 해당 날짜 없음
	 * @throws OutOfRangeException      지원 범위 밖
	 */
	public static KLunarDate ofYearDay ( final int year , final int dayOfYear ) {

		if( dayOfYear < 1 ) throw new NonexistentDateException();

		int c0 = ( year - YEAR_BASE ) / CYCLE_SIZE;
		int y0 = ( year - YEAR_BASE ) % CYCLE_SIZE;
		if( c0 >= ydss.length ) throw new OutOfRangeException();
		if( y0 >= ydss[c0].length ) throw new OutOfRangeException();
		int yd = ydss[c0][y0];

		int dayCount = dayOfYear;

		int leapMonth = ( yd >>> 13 ) & 0xF;
		int month = 0;
		for( int m0 = 0 ; m0 < 13 ; m0 += 1 ){

			month += leapMonth == m0// 월수 세기: 이번월이 윤달이면 +0, 아니면 +1
			        ? 0
			        : 1;
			int mSize = ( ( yd >>> m0 ) & 0x1 ) == 0x1 // 이번 달의 일수
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;

			if( dayCount <= mSize ){// 세고 남은 날짜 수가 이번달 일수 이하임: 이번달임
				return new KLunarDate( year , month , dayCount , ( ( yd >>> 13 ) & 0xF ) == m0 , c0 , y0 , m0 , dayOfYear - 1 );
			}

			dayCount -= mSize;// 세고 남은 일수
		}

		throw new NonexistentDateException( "The number of days of this year is smaller than " + dayOfYear );
	}

	/**
	 * 양력-->음력
	 *
	 * @param ld 양력 날짜
	 * 
	 * @return 음력 날짜
	 * 
	 * @throws OutOfRangeException 지원 범위 밖
	 */
	public static KLunarDate from ( final LocalDate ld ) {
		/*
		 * 지원하는 첫날 ~ 파라미터 날짜 일수 차이
		 * 주기별 적일 정보로 해당하는 주기를 찾는다.
		 * 그 주기 내 년도별 적일 정보로 해당하는 음력년도를 찾는다.
		 * 그 년도의 첫 날에 남은 적일 더해서 날짜 결정
		 */
		if( ld.isAfter( MAX ) )// 지원범위보다 미래
		    throw new OutOfRangeException();

		//// 주기 찾기
		int diff = (int) ChronoUnit.DAYS.between( MIN, ld );

		int c0 = ydss.length;
		for( ; c0 >= 0 ; c0 -= 1 ){// 미래에서부터 선형탐색 (가장 미래 부분이 현재랑 가깝고 제일 많이 찾을 거 같으니….)
			if( diff >= jDays[c0] ){
				diff -= jDays[c0];

				//// 년도 찾기
				int[] yds = ydss[c0];
				int y0 = 1;
				int cycleSize = ydss[c0].length;
				for( ; y0 < cycleSize ; y0 += 1 ){// XXX 일단 대충 선형탐색
					if( diff < ( yds[y0] >>> 17 ) ){
						break;
					}
				}
				y0 -= 1;
				diff -= ( yds[y0] >>> 17 );
				return ofYearDay( YEAR_BASE + c0 * CYCLE_SIZE + y0, diff + 1 );
			}
		}

		throw new OutOfRangeException();// 지원범위보다 과거
	}

	/**
	 * 양력-->음력 (시간 부분 제외, 날짜 부분만)
	 *
	 * @param ldt 양력 시각
	 * 
	 * @return 음력 날짜
	 * 
	 * @throws OutOfRangeException 지원 범위 밖
	 */
	public static KLunarDate from ( LocalDateTime ldt ) {
		return from( ldt.toLocalDate() );
	}

	public static KLunarDate parse ( CharSequence text ) {
		return null;// TODO
	}

	/**
	 * 음력-->양력
	 *
	 * @return 양력 날짜
	 */
	public LocalDate toLocalDate () {

		int cDiff = jDays[c0];
		int yDiff = ydss[c0][y0] >>> 17;
		return MIN.plusDays( cDiff + yDiff + d0 );
	}

	//// ================================ GETTER

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	/**
	 * 이번 년도의 몇 번째 달인가
	 */
	public int getMonthOfYear() {
		return m0 + 1;
	}

	/**
	 * 이번 년도의 몇 번째 날인가
	 */
	public int getDayOfYear() {
		return d0 + 1;
	}

	public boolean isLeapMonth() {
		return isLeapMonth;
	}

	public String getSecha () {
		return null;// TODO
	}

	public String getWolgeon () {
		return null;// TODO
	}

	public String getIljin () {
		return null;// TODO
	}

	//// ================================ ChronoLocalDate - Temporal - TemporalAccessor

	@Override
	public boolean isSupported ( TemporalField field ) {
		return false;// TODO
	}

	@Override
	public ValueRange range ( TemporalField field ) {
		return null;// TODO
	}

	@Override
	public int get ( TemporalField field ) {
		return 0;// TODO
	}

	@Override
	public long getLong ( TemporalField field ) {
		return 0;// TODO
	}

	@Override
	public < R > R query ( TemporalQuery<R> query ) {
		return null;// TODO
	}

	//// ================================ ChronoLocalDate - Temporal

	@Override
	public boolean isSupported ( TemporalUnit unit ) {
		return false;// TODO
	}

	@Override
	public KLunarDate with ( TemporalAdjuster adjuster ) {
		return null;// TODO
	}

	@Override
	public KLunarDate with ( TemporalField field , long newValue ) {
		return null;// TODO
	}

	public KLunarDate withYear ( int year ) {
		return null;// TODO
	}

	public KLunarDate withMonth ( int month ) {
		return null;// TODO
	}

	public KLunarDate withDay ( int day ) {
		return null;// TODO
	}

	public KLunarDate withLeapMonth () {
		return null;// TODO
	}

	public KLunarDate withCommonMonth () {
		return null;// TODO
	}

	@Override
	public KLunarDate plus ( TemporalAmount amount ) {
		return null;// TODO
	}

	@Override
	public KLunarDate plus ( long amountToAdd , TemporalUnit unit ) {
		return null;// TODO
	}

	/**
	 * n년 뒤의 날짜.
	 *
	 * @param n 몇년 뒤의 날짜?
	 * 
	 * @return n년 뒤의 날짜.
	 * 
	 * @throws OutOfRangeException      지원 범위 내에 그런 날짜가 없으면
	 * @throws NonexistentDateException 그런 날짜가 없으면 (예: 이년도 1월이 대월이고 1월 30일에서 plusYears(n) 했는데 n년 후 1월이 소월이라 1월 30일이 없는 경우)
	 */
	public KLunarDate plusHardYears ( int n ) throws OutOfRangeException , NonexistentDateException {
		return of( year + n, month, day, isLeapMonth );
	}

	public KLunarDate plusYears ( int a ) {
		return null;// TODO
	}

	/**
	 * 월, 일이 같은 다음 날짜
	 *
	 * @return 다음 날짜. 예: 2005년 1월 1일 --> 2006년 1월 1일
	 *         null: 계산할 수 있는 범위 내에 같은 날짜가 없음.
	 *         예: 그 뒤로 윤 11월 30일이 다시는 나타나지 않았음.
	 */
	public KLunarDate nextYear () {
		for( int n = 1 ; n < 999 ; n++ ){
			KLunarDate kld;
			try{
				kld = plusHardYears( n );
			}
			catch( NonexistentDateException e ){
				continue;
			}
			catch( OutOfRangeException e ){
				return null;
			}
			return kld;
		}
		return null;
	}

	@Override
	public KLunarDate minus ( TemporalAmount amount ) {
		return null;// TODO
	}

	@Override
	public KLunarDate minus ( long amountToSubtract , TemporalUnit unit ) {
		return null;// TODO
	}

	@Override
	public long until ( Temporal endExclusive , TemporalUnit unit ) {
		return 0;// TODO
	}

	//// ================================ ChronoLocalDate - TemporalAdjuster

	public Temporal adjustInto ( Temporal temporal ) {
		return null;// TODO
	}

	//// ================================ ChronoLocalDate - Comparable

	// TODO hmm 할필요없나?

	//// ================================ ChronoLocalDate

	@Override
	public Chronology getChronology () {
		return null;// TODO
	}

	@Override
	public Era getEra () {
		return null;// TODO
	}

	@Override
	public boolean isLeapYear () {
		return false;// TODO
	}

	@Override
	public int lengthOfMonth () {
		return 0;// TODO
	}

	@Override
	public int lengthOfYear () {
		return 0;// TODO
	}

	@Override
	public ChronoPeriod until ( ChronoLocalDate endDateExclusive ) {
		return null;// TODO
	}

	@Override
	public String format ( DateTimeFormatter formatter ) {
		return null;// TODO
	}

	@Override
	public ChronoLocalDateTime<?> atTime ( LocalTime localTime ) {
		return null;// TODO
	}

	@Override
	public long toEpochDay () {
		return 0;// TODO
	}

	@Override
	public int compareTo ( ChronoLocalDate other ) {
		return 0;// TODO
	}

	@Override
	public boolean isAfter ( ChronoLocalDate other ) {
		return this.toEpochDay() > other.toEpochDay();
	}

	@Override
	public boolean isBefore ( ChronoLocalDate other ) {
		return this.toEpochDay() < other.toEpochDay();
	}

	@Override
	public boolean isEqual ( ChronoLocalDate other ) {
		return this.toEpochDay() == other.toEpochDay();
	}

	//// ================================ Object

	@Override
	public boolean equals ( Object o ) {
		if( o == null ) return false;

		if( o instanceof KLunarDate ){
			if( ( (KLunarDate) o ).year == year && ( (KLunarDate) o ).month == month && ( (KLunarDate) o ).day == day && ( (KLunarDate) o ).isLeapMonth == isLeapMonth )
			    return true;
		}
		return false;
	}

	@Override
	public int hashCode () {
		return ( y0 << 11 ) + ( m0 << 6 ) + ( d0 );
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder( 10 );
		return sb.append( year )
		        .append( month < 10 ? "-0" : "-" )
		        .append( month )
		        .append( day < 10 ? "-0" : "-" )
		        .append( day )
		        .append( isLeapMonth ? "L" : "" )
		        .toString();
	}

	//// ================================ TODO serialize
}
