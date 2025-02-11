package xyz.arinmandri.koreanLunarCalendar;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 한국 음력으로 특정 날짜를 가리킨다. 불변(immutable)이다.
 * A date in Korean lunar calendar system
 * 
 * java.time.LocalDate랑 최대한 비슷한 형식 + 간지 등 추가정보 제공(하고싶다. TODO )
 */
public final class KLunarDate implements java.io.Serializable
{
	private static final long serialVersionUID = 0L;

	final int year;
	final int month;
	final int day;
	final boolean isLeapMonth;// 윤월 여부

	private transient final int y0;// 여기 정의된 최소 년도로부터 몇 번째 년도인가 (0부터 셈)
	private transient final int m0;// 이 년도의 몇 번째 월인가 (0부터 셈)
	private transient final int d0;// 이 년도의 몇 번째 일인가 (0부터 셈)

	public static final int YEAR_MIN = 2004;// 최소 년도
	public static final int YEAR_MAX = 2049;// 최대 년도

	private static final int BIG_MONTH_SIZE = 30;// 대월의 일수
	private static final int LIL_MONTH_SIZE = 29;// 소월의 일수

	private static final byte LEAP_MONTH_MASK = 2;// byte의 2째 비트가 1이면 윤달
	private static final byte BIG_MONTH_MASK = 1;// byte의 1째 비트가 1이면 대월

	private static final YearData[] yd = {
	        new YearData( LocalDate.of( 2004, 1, 22 ) , new byte[]{ 0, 1, 2, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2005, 2, 9 ) , new byte[]{ 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, } ),
	        new YearData( LocalDate.of( 2006, 1, 29 ) , new byte[]{ 1, 0, 1, 0, 1, 0, 1, 2, 1, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2007, 2, 18 ) , new byte[]{ 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2008, 2, 7 ) , new byte[]{ 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2009, 1, 26 ) , new byte[]{ 1, 1, 0, 0, 1, 2, 0, 1, 0, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2010, 2, 14 ) , new byte[]{ 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2011, 2, 3 ) , new byte[]{ 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2012, 1, 23 ) , new byte[]{ 1, 0, 1, 3, 1, 0, 1, 0, 0, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2013, 2, 10 ) , new byte[]{ 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2014, 1, 31 ) , new byte[]{ 0, 1, 0, 1, 0, 1, 0, 1, 1, 2, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2015, 2, 19 ) , new byte[]{ 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2016, 2, 8 ) , new byte[]{ 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2017, 1, 28 ) , new byte[]{ 0, 1, 0, 1, 0, 2, 1, 0, 1, 0, 1, 1, 1, } ),
	        new YearData( LocalDate.of( 2018, 2, 16 ) , new byte[]{ 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2019, 2, 5 ) , new byte[]{ 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2020, 1, 25 ) , new byte[]{ 1, 0, 1, 1, 2, 1, 0, 0, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2021, 2, 12 ) , new byte[]{ 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2022, 2, 1 ) , new byte[]{ 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2023, 1, 22 ) , new byte[]{ 0, 1, 2, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2024, 2, 10 ) , new byte[]{ 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, } ),
	        new YearData( LocalDate.of( 2025, 1, 29 ) , new byte[]{ 1, 0, 1, 0, 0, 1, 2, 1, 0, 1, 1, 1, 0, } ),
	        new YearData( LocalDate.of( 2026, 2, 17 ) , new byte[]{ 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, } ),
	        new YearData( LocalDate.of( 2027, 2, 7 ) , new byte[]{ 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, } ),
	        new YearData( LocalDate.of( 2028, 1, 27 ) , new byte[]{ 0, 1, 1, 0, 1, 2, 0, 1, 0, 0, 1, 1, 0, } ),
	        new YearData( LocalDate.of( 2029, 2, 13 ) , new byte[]{ 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2030, 2, 3 ) , new byte[]{ 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2031, 1, 23 ) , new byte[]{ 1, 0, 1, 2, 1, 0, 1, 1, 0, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2032, 2, 11 ) , new byte[]{ 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2033, 1, 31 ) , new byte[]{ 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 2, 1, } ),
	        new YearData( LocalDate.of( 2034, 2, 19 ) , new byte[]{ 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, } ),
	        new YearData( LocalDate.of( 2035, 2, 8 ) , new byte[]{ 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2036, 1, 28 ) , new byte[]{ 1, 1, 0, 1, 0, 0, 3, 0, 0, 1, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2037, 2, 15 ) , new byte[]{ 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2038, 2, 4 ) , new byte[]{ 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2039, 1, 24 ) , new byte[]{ 1, 1, 0, 1, 1, 2, 1, 0, 1, 0, 1, 0, 0, } ),
	        new YearData( LocalDate.of( 2040, 2, 12 ) , new byte[]{ 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2041, 2, 1 ) , new byte[]{ 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2042, 1, 22 ) , new byte[]{ 0, 1, 2, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2043, 2, 10 ) , new byte[]{ 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2044, 1, 30 ) , new byte[]{ 1, 0, 1, 0, 0, 1, 0, 2, 1, 0, 1, 1, 1, } ),
	        new YearData( LocalDate.of( 2045, 2, 17 ) , new byte[]{ 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, } ),
	        new YearData( LocalDate.of( 2046, 2, 6 ) , new byte[]{ 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2047, 1, 26 ) , new byte[]{ 1, 0, 1, 1, 0, 3, 0, 1, 0, 0, 1, 0, 1, } ),
	        new YearData( LocalDate.of( 2048, 2, 14 ) , new byte[]{ 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, } ),
	        new YearData( LocalDate.of( 2049, 2, 2 ) , new byte[]{ 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, } ),
	        new YearData( LocalDate.of( 2050, 1, 23 ) , new byte[]{ 1, 0, 0, 3, 0, 1, 0, 1, 1, 0, 1, 1, } ),
	};

	private KLunarDate( int year , int month , int day , boolean isLeapMonth , int y0 , int m0 , int d0 ) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.isLeapMonth = isLeapMonth;

		this.y0 = y0;
		this.m0 = m0;
		this.d0 = d0;
	}

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

		if( year < YEAR_MIN ) throw new OutOfRangeException();
		if( year > YEAR_MAX ) throw new OutOfRangeException();
		if( month < 1 ) throw new NonexistentDateException();
		if( month > 12 ) throw new NonexistentDateException();
		if( day < 1 ) throw new NonexistentDateException();

		//// 몇번째 년도?
		int y0 = year - YEAR_MIN;

		//// 몇번째 월인지 찾기
		byte[] mts = yd[y0].mts;
		boolean hasLeapMonth = false;
		for( int i = 0 ; i < month - 1 ; i++ ){// 이전월까지 중에 윤달이 있었나
			if( ( mts[i] & LEAP_MONTH_MASK ) == LEAP_MONTH_MASK ){
				hasLeapMonth = true;
				break;
			}
		}
		int m0 = month - 1
		        + ( hasLeapMonth ? 1 : 0 )
		        + ( isLeapMonth ? 1 : 0 );

		int monthSize = ( mts[m0] & BIG_MONTH_MASK ) == BIG_MONTH_MASK ? BIG_MONTH_SIZE : LIL_MONTH_SIZE;
		if( day > monthSize ) throw new NonexistentDateException();

		//// 이 해의 몇번째 날인지 찾기
		int d0 = 0;
		for( int i = 0 ; i < m0 ; i++ ){// 이전월까지의 일수 합
			d0 += ( mts[i] & BIG_MONTH_MASK ) == BIG_MONTH_MASK
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;
		}
		d0 += day - 1;// 이번 월의 몇번째 날? (0부터 셈)

		return new KLunarDate( year , month , day , isLeapMonth , y0 , m0 , d0 );
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
	public static KLunarDate of ( final int year , final int dayOfYear ) {

		int y0 = year - YEAR_MIN;
		if( y0 < 0 ) throw new OutOfRangeException();
		if( y0 >= yd.length ) throw new OutOfRangeException();
		if( dayOfYear < 1 ) throw new OutOfRangeException();

		byte[] mts = yd[y0].mts;

		int month = 0;
		int m0 = 0;
		int dayCount = dayOfYear;
		for( byte mt : mts ){

			month += ( mt & LEAP_MONTH_MASK ) == LEAP_MONTH_MASK// 월수 세기: 이번월이 윤달이면 +0, 아니면 +1
			        ? 0
			        : 1;
			int mSize = ( mt & BIG_MONTH_MASK ) == BIG_MONTH_MASK // 이번 달의 일수
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;

			if( dayCount <= mSize ){// 세고 남은 날짜 수가 이번달 일수 이하임: 이번달임
				return new KLunarDate( year , month , dayCount , ( mt & LEAP_MONTH_MASK ) == LEAP_MONTH_MASK , y0 , m0 , dayOfYear - 1 );
			}

			m0 += 1;
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
		 * (음력년도별 1월1일의 양력날짜)를 이용해서 음력년도를 찾는다. 음력 해당년도 + (파라미터양력날짜 - 해당음력년도1월1일의 양력날짜)일수
		 */
		if( ld.isBefore( yd[0].firstDay ) )
		    throw new OutOfRangeException();

		//// 음력년도 찾기: XXX 년도 수가 안 많아서 그냥 선형탐색함
		int y0 = 0;
		for( ; y0 < yd.length ; y0 += 1 ){
			if( ld.isBefore( yd[y0].firstDay ) ){
				y0 -= 1;
				break;
			}
		}
		if( y0 >= yd.length )
		    throw new OutOfRangeException();

		//// (해당 음력년도 1월 1일 ~ 파라미터 날짜) 차이(단위: 일) 구하기
		int d = ( (Long) DAYS.between( yd[y0].firstDay, ld ) ).intValue();

		return of( y0 + YEAR_MIN, d + 1 );
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

	/**
	 * 음력-->양력
	 *
	 * @return 양력 날짜
	 */
	public LocalDate toLocalDate () {
		/*
		 * 이 년도의 첫날의 양력날자를 구한다.
		 * 이 년도의 몇번째 날인지 구한다.
		 * 첫날의 양력날짜 + (이 년도가 몇번째 날인지-1)
		 */
		return yd[y0].firstDay.plusDays( d0 );
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

	/**
	 * 월, 일이 같은 다음 날짜
	 *
	 * @return 다음 날짜. 예: 2005년 1월 1일 --> 2006년 1월 1일
	 *         null: 계산할 수 있는 범위 내에 같은 날짜가 없음.
	 *         예: 그 뒤로 윤 11월 30일이 다시는 나타나지 않았음.
	 */
	public KLunarDate nextYear () {
		for( int n = 1 ; n < YEAR_MAX - YEAR_MIN ; n++ ){
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

	public int getYear () {
		return year;
	}

	public int getMonth () {
		return month;
	}

	public int getDay () {
		return day;
	}

	/**
	 * 이번 년도의 몇 번째 달인가
	 */
	public int getMonthOfYear () {
		return m0 + 1;
	}

	/**
	 * 이번 년도의 몇 번째 날인가
	 */
	public int getDayOfYear () {
		return d0 + 1;
	}

	public boolean isLeapMonth () {
		return isLeapMonth;
	}

	@Override
	public String toString () {
		StringBuilder sb = new StringBuilder( 10 );
		return sb.append( year )
		        .append( month < 10 ? "-0" : "-" )
		        .append( month )
		        .append( day < 10 ? "-0" : "-" )
		        .append( day )
		        .append( isLeapMonth ? "(leap)" : "" )
		        .toString();
	}

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

	private static class YearData
	{
		final LocalDate firstDay;
		final byte[] mts;

		public YearData( LocalDate firstDay , byte[] mts ) {
			super();
			this.firstDay = firstDay;
			this.mts = mts;
		}
	}
}
