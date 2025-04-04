package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;


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

	private transient final int c0;// 해들을 주기 단위로 묶는다. 여기 정의된 묶음들 중 몇 번째 묶음에 속하는가 (0부터 셈)// 주기: 육십갑자에 대응되는 60년. 그냥 갑자라고 하면 60년을 가리키나? 아니면 60갑자는 단순히 순서있는 60가지이고 그걸 년도에 붙일 뿐인가? 모르겠으니까 애매하니까 그냥 별개의 용어 지어 쓴다.
	private transient final int y0;// 이 주기의 몇 번째 년인가 (0부터 셈)
	private transient final int m0;// 이 년도의 몇 번째 월인가 (0부터 셈)
	private transient final int d0;// 이 년도의 몇 번째 일인가 (0부터 셈)

	public static final int TIME_ZONE_OFFSET = 9 * 60 * 60;// UTC 기준 한국 시간대의 초단위 offset

	public static final int BIG_MONTH_SIZE = 30;// 대월의 일수
	public static final int LIL_MONTH_SIZE = 29;// 소월의 일수

	/*
	 * int 하나에 32비트로 한 해의 정보 저장
	 * 왼쪽부터
	 * 15비트: 이 주기의 첫날과 이 년의 첫 날의 날짜 차이 (일 단위)
	 *         yd >>> 17
	 * 4비트: 몇 월에 윤달이 있나 (예: 이 값이 1이면 1월에 윤달이 있으며 2번째 달이 윤달이다.) (0xF인 경우 윤달 없음) (이 값이 0인 경우는 없음)
	 *        int leapMonth = ( yd >>> 13 ) & 0xF // 윤달이 있는 달
	 *        leapMonth == 0xF // 이 해에 윤달 없음
	 *        leapMonth == month // 이 달에 윤달 있음
	 * 13비트: 각 월의 대소(0:소 1:대) (0번째 달부터 12번째 달까지 윤달도 똑같이 한 달로 취급, 1의자리부터 1월)
	 *         if( ( ( yd >>> m0 ) & 0x1 ) == 0x1 ) 대
	 *         if( ( ( yd >>> m0 ) & 0x1 ) == 0x0 ) 소
	 *
	 * 마지막 요소 빼고는 다 크기 60에 맞춰야 함.
	 */
	private static final int[][] ydss = {// int yd = ydss[c0][y0]
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

	/**
	 * 양력(그레고리력) 1970년 1월 1일 = 0 epoch day = 2440588 율리우스적일
	 */
	public static final int EPOCH_0_JDAY = 2440588;

	/*
	 * 각 주기 첫날의 epoch day
	 */
	private static final int[] epochDays = {
	        2401910 - EPOCH_0_JDAY,// 1864
	        2423821 - EPOCH_0_JDAY,// 1924
	        2445733 - EPOCH_0_JDAY,// 1984
	        2467645 - EPOCH_0_JDAY,// 2044
	        2470214 - EPOCH_0_JDAY,// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)
	};

	public static final int YEAR_MIN = 1864;// 최소 년도 (갑자년부터 시작)
	public static final int YEAR_MAX = YEAR_MIN + ( ydss.length - 1 ) * CYCLE_SIZE + ydss[ydss.length - 1].length - 1;// 최대년도

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

	public static KLunarDate now () {
		long epochDay = ( System.currentTimeMillis() / 1000 + TIME_ZONE_OFFSET ) / ( 24 * 60 * 60 );
		return ofEpochDay( epochDay );
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
	public static KLunarDate of ( int year , int month , boolean isLeapMonth , int day ) {

		if( year < YEAR_MIN ) throw new OutOfRangeException();
		if( month < 1 ) throw new NonexistentDateException();
		if( month > 12 ) throw new NonexistentDateException();
		if( day < 1 ) throw new NonexistentDateException();

		//// 몇번째 년도?
		int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
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
		if( day > monthSize ) throw new NonexistentDateException( "The size of " + year + '-' + month + " is smaller than " + day );
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
		return of( year, month, false, day );
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

		final int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		final int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
		if( c0 >= ydss.length ) throw new OutOfRangeException();
		if( y0 >= ydss[c0].length ) throw new OutOfRangeException();
		final int yd = ydss[c0][y0];

		int dayCount = dayOfYear;

		final int leapMonth = ( yd >>> 13 ) & 0xF;
		int month = 0;
		for( int m0 = 0 ; m0 < 13 ; m0 += 1 ){

			month += leapMonth == m0// 월수 세기: 이번월이 윤달이면 +0, 아니면 +1
			        ? 0
			        : 1;
			int mSize = ( ( yd >>> m0 ) & 0x1 ) == 0x1 // 이번 달의 일수
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;

			if( dayCount <= mSize ){// 세고 남은 날짜 수가 이번달 일수 이하임: 이번달임
				return new KLunarDate( year , month , dayCount , leapMonth == m0 , c0 , y0 , m0 , dayOfYear - 1 );
			}

			dayCount -= mSize;// 세고 남은 일수
		}

		throw new NonexistentDateException( "The number of days of this year is smaller than " + dayOfYear );
	}

	/**
	 * epoch day --> 음력 날짜
	 * 
	 * @param epochDay 1970년 1월 1일을 0으로 하는 누적일수
	 * @return 음력 날짜
	 */
	public static KLunarDate ofEpochDay ( final long epochDay ) {
		/*
		 * 주기별 epoch day 정보로 해당하는 주기를 찾는다.
		 * 그 주기 내 년도별 적일 정보로 해당하는 음력년도를 찾는다.
		 * 그 년도의 첫 날에 남은 적일 더해서 날짜 결정
		 */

		if( epochDay >= epochDays[epochDays.length - 1] )// 지원범위보다 미래
		    throw new OutOfRangeException();

		//// 주기 찾기
		int c0 = ydss.length;
		for( ; c0 >= 0 ; c0 -= 1 ){// 미래에서부터 선형탐색 (일단 주기 개수 적어서 걍 선형탐색... 주기 수 많아지면 1주기의 대략의 크기로 점프 가능할 듯
			if( epochDay >= epochDays[c0] ){
				int jDay = (int) ( epochDay - epochDays[c0] );

				//// 년도 찾기
				int[] yds = ydss[c0];
				int y0 = 1;
				int cycleSize = ydss[c0].length;
				for( ; y0 < cycleSize ; y0 += 1 ){// XXX 일단 대충 선형탐색. 1년의 크기로 계산하여 점프 가능할 듯
					if( jDay < ( yds[y0] >>> 17 ) ){
						break;
					}
				}
				y0 -= 1;
				jDay -= ( yds[y0] >>> 17 );
				return ofYearDay( YEAR_MIN + c0 * CYCLE_SIZE + y0, jDay + 1 );
			}
		}

		throw new OutOfRangeException();// 지원범위보다 과거
	}

	//// ================================ GETTER

	@Override
	public boolean isSupported ( TemporalField field ) {
		if( field == null ) return false;
		if( field instanceof ChronoField ){
			switch( (ChronoField) field ){
			case DAY_OF_MONTH:
			case DAY_OF_YEAR:
			case EPOCH_DAY:
			case MONTH_OF_YEAR:
			case YEAR:
			case YEAR_OF_ERA:
			case ERA:
				return true;
			default:
				return false;
			}
		}
		return field != null && field.isSupportedBy( this );
	}

	/**
	 * the range of valid values for the specified field.
	 * 
	 * @param field not null
	 */
	@Override
	public ValueRange range ( TemporalField field ) {

		if( field instanceof ChronoField ){
			switch( (ChronoField) field ){
			case DAY_OF_MONTH:
				return ValueRange.of( 1, lengthOfMonth() );
			case DAY_OF_YEAR:
				return ValueRange.of( 1, lengthOfYear() );
			case EPOCH_DAY:
				return ValueRange.of( epochDays[0], epochDays[epochDays.length - 1] - 1 );
			case MONTH_OF_YEAR:
				return ValueRange.of( 1, 12 );
			case YEAR:
			case YEAR_OF_ERA:
				return ValueRange.of( YEAR_MIN, YEAR_MAX );
			case ERA:
				return ValueRange.of( 1, 1 );
			}
			throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
		}
		return field.rangeRefinedBy( this );
	}

	@Override
	public int get ( TemporalField field ) {
		if( field == null ) throw new NullPointerException();
		if( field instanceof ChronoField ){
			switch( (ChronoField) field ){
			case DAY_OF_MONTH:
				return getDay();
			case DAY_OF_YEAR:
				return getDayOfYear();
			case EPOCH_DAY:
				return (int) toEpochDay();
			case MONTH_OF_YEAR:
				return getMonth();
			case YEAR:
				return year;
			case YEAR_OF_ERA:
				return year;
			case ERA:
				return 1;
			default:
				throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
			}
		}
		return (int) field.getFrom( this );
	}

	@Override
	public long getLong ( TemporalField field ) {
		/*
		 * 지원범위 내에서 int 범위를 넘는 속성이 없으니 get에 기능구현하고 getLong에서는 캐스팅만 함.
		 * epoch day만 바로 리턴해줌
		 */
		if( field == ChronoField.EPOCH_DAY )
		    return toEpochDay();

		return get( field );
	}

	@Override
	public Chronology getChronology () {
		return KLunarChronology.INSTANCE;
	}

	@Override
	public Era getEra () {
		return IsoEra.CE;
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
	 * 이번 년도의 몇 번째 달인가 (0부터 셈)
	 * 예를 들어 이 해에 윤1월이 있다면 이 해의 1월은 0, 윤1월은 1, 2월은 2, ...
	 */
	public int getMonthOrdinal () {
		return m0;
	}

	/**
	 * 이번 년도의 몇 번째 날인가
	 */
	public int getDayOfYear () {
		return d0 + 1;
	}

	/**
	 * 윤년 여부 (윤달이 포함된 해인 여부)
	 * 고려대 한국어대사전에서 윤년을 "윤달이나 윤일이 드는 해"로 정의하므로 그에 따름.
	 * 그러나 윤년이 아니라고 해서 해의 길이가 일정하지는 않다.
	 * 
	 * @return true 이 해에 윤달 있음
	 *         false 이 해에 윤달 없음
	 */
	@Override
	public boolean isLeapYear () {
		int yd = ydss[c0][y0];
		return ( ( yd >>> 13 ) & 0xF ) == 0xF ? false : true;
	}

	/**
	 * 윤달 여부
	 * 
	 * @return true 윤달
	 *         false 평달
	 */
	public boolean isLeapMonth () {
		return isLeapMonth;
	}

	/**
	 * 몇 월에 윤달이 있는지 반환
	 * 
	 * @return 윤달이 있는 월
	 *         윤달이 없는 해인 경우 0
	 */
	public int getLeapMonth () {
		int yd = ydss[c0][y0];
		int lm = ( yd >>> 13 ) & 0xF;
		return lm == 0xF ? 0 : lm;
	}

	/**
	 * 대월소월
	 * 
	 * @return true 대월
	 *         false 소월
	 */
	public boolean isBigMonth () {
		final int yd = ydss[c0][y0];
		if( ( ( yd >>> m0 ) & 0x1 ) == 0x1 )
		    return true;
		return false;
	}

	/**
	 * 이 날짜의 세차(년의 간지)
	 * 
	 * @return
	 */
	public Ganji getSecha () {
		return Ganji.values()[y0];
	}

	/**
	 * 이 날짜의 월건(월의 간지)
	 * 5년(=윤달 제외 60개월)마다 같은 월건이 반복되며 윤달은 월건이 없다.
	 * 
	 * @return wolgeon
	 *         null if it is in a leap month
	 */
	public Ganji getWolgeon () {
		if( isLeapMonth ) return null;
		return Ganji.values()[( y0 * 12 + month + 1 ) % CYCLE_SIZE];// 갑자년 1월은 병인(丙寅)월 = O2
	}

	/**
	 * 이 날짜의 일진(일의 간지)
	 * 
	 * @return iljin
	 */
	public Ganji getIljin () {
		return Ganji.values()[( ( (int) toEpochDay() + 17 ) % CYCLE_SIZE + CYCLE_SIZE ) % CYCLE_SIZE];// 0 epoch day 는 신사(辛巳)일 = O18 // epoch day가 음수일 수도 있어서 a%c 대신 (a%c+c)%c
	}

	@Override
	public int lengthOfMonth () {
		int yd = ydss[c0][y0];
		if( ( ( yd >>> m0 ) & 0x1 ) == 0x1 )
		    return BIG_MONTH_SIZE;
		else
		    return LIL_MONTH_SIZE;
	}

	@Override
	public int lengthOfYear () {
		int yd = ydss[c0][y0];

		int count = 0;
		int n = ( ( yd >>> 13 ) & 0xF ) == 0xF ? 12 : 13;
		for( int m = 0 ; m < n ; m++ ){
			if( ( ( yd >>> m ) & 0x1 ) == 0x1 )
			    count += BIG_MONTH_SIZE;
			else
			    count += LIL_MONTH_SIZE;
		}
		return count;
	}

	//// ================================ 셈

	@Override
	public boolean isSupported ( TemporalUnit unit ) {
		if( unit == null ) return false;
		if( unit instanceof ChronoUnit ){
			switch( (ChronoUnit) unit ){
			case DAYS:
			case MONTHS:
			case YEARS:
				return true;
			default:
				return false;
			}
		}
		return unit != null && unit.isSupportedBy( this );
	}

	private static KLunarDate resolvePreviousValid_LD ( final int year , final int month , boolean isLeapMonth , int day ) {
		/*
		 * 윤달 조정
		 * │ 입력값이 평달이면 윤달 조정 할 거 없음
		 * └ 입력값으로 윤달이 없는 달이 지정된 경우 평달로 조정
		 * 그 뒤에 일 조정
		 */

		if( !isLeapMonth ){
			return resolvePreviousValid_D( year, month, isLeapMonth, day );
		}

		final int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		final int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
		final int yd = ydss[c0][y0];
		final int leapMonth = ( yd >>> 13 ) & 0xF;
		if( leapMonth == month ){// 윤달이 있는 달
			return resolvePreviousValid_D( year, month, isLeapMonth, day );
		}
		return resolvePreviousValid_D( year, month, false, day );
	}

	private static KLunarDate resolvePreviousValid_D ( int year , int month , boolean isLeapMonth , int day ) {
		/*
		 * 일 조정
		 * 지정된 달이 소월인데 30일이면 29일로 조정
		 */

		if( day > LIL_MONTH_SIZE ){
			KLunarDate withDay29 = of( year, month, isLeapMonth, LIL_MONTH_SIZE );
			if( withDay29.isBigMonth() ){
				return of( year, month, isLeapMonth, day );
			}
			else{
				return withDay29;
			}
		}
		return of( year, month, isLeapMonth, day );
	}

	//// ================================ 셈 - with

	/**
	 * 지정한 필드 값을 바꾼 KLunarDate 개체를 반환.
	 * 
	 * @param field    조정할 필드 not null
	 * @param newValue 그 필드에 새로 넣을 값
	 * 
	 * @return 값 조정된 개체 not null
	 * 
	 * @throws DateTimeException                if the field cannot be set
	 * @throws UnsupportedTemporalTypeException if the field is not supported
	 */
	@Override
	public KLunarDate with ( TemporalField field , long newValue ) {

		if( field instanceof ChronoField ){
			ChronoField chronoField = (ChronoField) field;
			chronoField.checkValidValue( newValue );
			switch( chronoField ){
			case DAY_OF_MONTH:
				return withDay( (int) newValue );
			case DAY_OF_YEAR:
				return withDayOfYear( (int) newValue );
			case EPOCH_DAY:
				return KLunarDate.ofEpochDay( newValue );
			case MONTH_OF_YEAR:
				return withMonth( (int) newValue );
			case YEAR:
			case YEAR_OF_ERA:
				return withYear( (int) newValue );
			case ERA:
				if( getLong( ChronoField.ERA ) == newValue )
				    return this;
				else
				    throw new OutOfRangeException();
			default:
				throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
			}
		}
		return field.adjustInto( this, newValue );
	}

	public KLunarDate withYear ( int year ) {
		if( year == this.year )
		    return this;
		return resolvePreviousValid_LD( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int year , int month ) {
		if( year == this.year && month == this.month )
		    return this;
		return resolvePreviousValid_LD( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int year , int month , boolean isLeapMonth ) {
		if( year == this.year && month == this.month && isLeapMonth == this.isLeapMonth )
		    return this;
		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int month ) {
		if( month == this.month )
		    return this;
		return resolvePreviousValid_LD( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int month , boolean isLeapMonth ) {
		if( month == this.month && isLeapMonth == this.isLeapMonth )
		    return this;
		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonthLeap ( boolean isLeapMonth ) {
		if( isLeapMonth == this.isLeapMonth )
		    return this;
		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	public KLunarDate withDay ( int day ) {
		if( day == this.day )
		    return this;
		return of( year, month, isLeapMonth, day );
	}

	public KLunarDate withDayOfYear ( int dayOfYear ) {
		return ofYearDay( year, dayOfYear );
	}

	// TODO with 간지

	//// ================================ 셈 - plus, minus

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
		return null;// TODO
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

	//// ================================ 셈 - 비교

	public Temporal adjustInto ( Temporal temporal ) {
		return null;// TODO
	}

	@Override
	public long until ( Temporal endExclusive , TemporalUnit unit ) {
		return 0;// TODO
	}

	@Override
	public ChronoPeriod until ( ChronoLocalDate endDateExclusive ) {
		return null;// TODO
	}

	//// ================================ 변환

	/**
	 * 음력-->양력
	 *
	 * @return 양력 날짜
	 */
	public LocalDate toLocalDate () {

		return LocalDate.ofEpochDay( toEpochDay() );
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

		int epochDay = (int) ld.toEpochDay();

		return ofEpochDay( epochDay );
	}

	@Override
	public String format ( DateTimeFormatter formatter ) {
		return null;// TODO 이걸 어케함???
	}

	public static KLunarDate parse ( CharSequence text ) {
		return null;// TODO
	}

	@Override
	public ChronoLocalDateTime<?> atTime ( LocalTime localTime ) {
		return null;// TODO
	}

	@Override
	public long toEpochDay () {
		return epochDays[c0] + ( ydss[c0][y0] >>> 17 ) + d0;
	}

	//// ================================ Object

	@Override
	public boolean equals ( Object o ) {
		if( o == null ) return false;

		if( o instanceof KLunarDate ){
			return ( (KLunarDate) o ).year == year && ( (KLunarDate) o ).month == month && ( (KLunarDate) o ).isLeapMonth == isLeapMonth && ( (KLunarDate) o ).day == day;
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

	//// ================================ 체크체크 나중에

//	@Override public < R > R query ( TemporalQuery<R> query )
//	@Override public KLunarDate with ( TemporalAdjuster adjuster )
//	@Override public int compareTo ( ChronoLocalDate other )

	//// ================================ TODO serialize

}
