package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.BIG_MONTH_SIZE;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.LIL_MONTH_SIZE;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.YEAR_MAX;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.YEAR_MIN;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.epochDays;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.ydss;

import java.time.DateTimeException;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.List;


/**
 * 한국 음력
 */
public class KLunarChronology extends AbstractChronology implements java.io.Serializable
{
	public static final KLunarChronology INSTANCE = new KLunarChronology();

	private KLunarChronology() {}

	/**
	 * 이 역법의 ID
	 * <p>
	 * ID는 {@code Chronology} 을 유일히 식별한다.
	 * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
	 *
	 * @return the chronology ID, not null
	 * @see #getCalendarType()
	 */
	@Override
	public String getId () {
		return "KoreanLunar";
	}

	/**
	 * CLDR에 정의된 식별자 없음.
	 * 
	 * @return null
	 */
	@Override
	public String getCalendarType () {
		/*
		 * 이거 확인하는 거 맞는지 모르겠는데.
		 * https://cldr.unicode.org/index/downloads
		 * 여기서 latest-Data 눌러서
		 * https://unicode.org/Public/cldr/47/?_gl=1*16ha7od*_ga*MjAyODM4MzI3MC4xNzQ2OTMwOTk3*_ga_BPN1D3SEJM*czE3NDY5MzA5OTYkbzEkZzEkdDE3NDY5MzE4MjMkajAkbDAkaDA.
		 * 여기서 cldr-common-47 다운받아서 cldr-common-47/common/bcp47/calendar.xml
		 * 여기에 없다.
		 * 근데 웃긴 건 살면서 거의 들어본 적도 없는 단기력은 있음.
		 */
		return null;
	}

	/**
	 * 기년, 월, 일 값으로 음력 날짜를 얻는다.
	 *
	 * @param prolepticYear 기년(기원 기준 년도)
	 * @param month         월
	 * @param day           일
	 * @return 이 chronology의 로컬 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 */
	@Override
	public ChronoLocalDate date ( int prolepticYear , int month , int day ) {
		return KLunarDate.of( prolepticYear, month, day );
	}

	/**
	 * 기년, 연중일 값으로 음력 날짜를 얻는다.
	 *
	 * @param prolepticYear 기년(기원 기준 년도)
	 * @param dayOfYear     연중일(이 해의 몇 번째 일인가)
	 * @return 음력 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 */
	@Override
	public ChronoLocalDate dateYearDay ( int prolepticYear , int dayOfYear ) {
		return KLunarDate.ofYearDay( prolepticYear, dayOfYear );
	}

	/**
	 * 에포크일 값으로 음력 날짜를 얻는다.
	 *
	 * @param epochDay 에포크일(서력 1970년 1월 1일로부터 경과일)
	 * @return 음력 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 */
	@Override
	public ChronoLocalDate dateEpochDay ( long epochDay ) {
		return KLunarDate.ofEpochDay( epochDay );
	}

	/**
	 * 다른 시간 개체를 음력 날짜로 변환한 것을 얻는다.
	 * <p>
	 * This method matches the signature of the functional interface {@link TemporalQuery}
	 * allowing it to be used as a query via method reference, {@code aChronology::date}.
	 *
	 * @param temporal 변환할 temporal 개체, not null
	 * @return 음력 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 * @see KLunarDate#from(TemporalAccessor)
	 */
	@Override
	public ChronoLocalDate date ( TemporalAccessor temporal ) {
		return KLunarDate.from( temporal );
	}

	/**
	 * 특정 년도가 윤년인지 확인한다.
	 * 한국 음력에는 윤달 개념이 있다. 윤달을 포함한 해를 윤년이라 한다.
	 * 유효한 년도 범위를 벗어난 경우에도 예외를 던지지 않으며 false를 반환한다.
	 *
	 * @param prolepticYear 기년(기원 기준 년도), 유효범위를 확인 안 함.
	 * @return 윤년인 경우 true
	 */
	@Override
	public boolean isLeapYear ( long prolepticYear ) {
		if( prolepticYear < YEAR_MIN ) return false;
		if( prolepticYear > YEAR_MAX ) return false;
		int year = (int) prolepticYear;
		int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
		int yd = ydss[c0][y0];
		return ( ( yd >>> 13 ) & 0xF ) != 0xF;
	}

    /**
	 * 주어진 시대와 그 시대에서의 연도 값으로 기년을 계산해 내놓는다.
	 * (그러나 이 역법은 년도는 서력의 것을 그대로 쓰고 기원후 외의 다른 시대를 지원하지 않는다.)
	 * 
	 * @param era       {@link IsoEra#CE} 만 허용한다. not null
	 * @param yearOfEra the chronology year-of-era
	 * @return 기년(기원 기준 년도)
	 * @throws DateTimeException  {@code era} 가 {@link IsoEra#CE} 가 아닌 경우
	 * @throws ClassCastException {@code era} 가 {@link IsoEra} 가 아닌 경우
	 */
	@Override
	public int prolepticYear ( Era era , int yearOfEra ) {
		if( era instanceof IsoEra ){
			if( era == IsoEra.CE )
			    return yearOfEra;
		}
		throw new ClassCastException( "Era must be IsoEra.CE" );
	}

	/**
	 * 정수->시대 값
	 * 여기서는 1만 받아서 서력기원후({@link IsoEra#CE})만 반환한다.
	 *
	 * @param eraValue 1만 가능
	 * @return {@link IsoEra#CE}
	 * @throws DateTimeException if unable to create the era
	 */
	@Override
	public Era eraOf ( int eraValue ) {
		if( eraValue == 1 )
		    return IsoEra.CE;
		throw new DateTimeException( "" );
	}

	/**
	 * 이 역법의 시대 목록
	 * 여기서는 서력기원후({@link IsoEra#CE})만 반환한다.
	 */
	@Override
	public List<Era> eras () {
		return List.of( IsoEra.CE );
	}

	/**
	 * 필드의 유효한 범위.
	 * 단순히 최대값 및 최소값만 나타내며; 그 범위 안이면 다 유효하단 뜻은 아니다.
	 * Gets the range of valid values for the specified field.
	 * <p>
	 * All fields can be expressed as a {@code long} integer.
	 * This method returns an object that describes the valid range for that value.
	 * <p>
	 * Note that the result only describes the minimum and maximum valid values
	 * and it is important not to read too much into them. For example, there
	 * could be values within the range that are invalid for the field.
	 * <p>
	 * 
	 *
	 * @param field the field to get the range for, not null
	 * @return the range of valid values for the field, not null
	 * @throws DateTimeException if the range for the field cannot be obtained
	 */
	@Override
	public ValueRange range ( ChronoField field ) {
		switch( field ){
		case ALIGNED_DAY_OF_WEEK_IN_MONTH:
		case ALIGNED_DAY_OF_WEEK_IN_YEAR:
		case ALIGNED_WEEK_OF_MONTH:
		case ALIGNED_WEEK_OF_YEAR:
		case PROLEPTIC_MONTH:
			throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
		case DAY_OF_MONTH:
			return ValueRange.of( 1, BIG_MONTH_SIZE );
		case DAY_OF_YEAR:
			return ValueRange.of( 1, 5 * LIL_MONTH_SIZE + 8 * BIG_MONTH_SIZE );// 윤달 있어서 13달, 그 중 대월 8개, 소월 5개
		case EPOCH_DAY:
			return ValueRange.of( epochDays[0], epochDays[epochDays.length - 1] - 1 );
		case MONTH_OF_YEAR:
			return ValueRange.of( 1, NAMED_MONTHS_NUMBER_IN_1Y );
		case YEAR:
		case YEAR_OF_ERA:
			return ValueRange.of( YEAR_MIN, YEAR_MAX );
		case ERA:
			return ValueRange.of( 1, 1 );
		// XXX time:
//		case INSTANT_SECONDS:
//		case MICRO_OF_SECOND:
//		case MILLI_OF_SECOND:
//		case NANO_OF_SECOND:
//		case OFFSET_SECONDS:
//		case SECOND_OF_MINUTE:
//		case MINUTE_OF_HOUR:
//		case HOUR_OF_AMPM:
//		case CLOCK_HOUR_OF_AMPM:
//		case AMPM_OF_DAY:
//		case CLOCK_HOUR_OF_DAY:
//		case HOUR_OF_DAY:
//		case MICRO_OF_DAY:
//		case MILLI_OF_DAY:
//		case MINUTE_OF_DAY:
//		case NANO_OF_DAY:
//		case SECOND_OF_DAY:
//		case DAY_OF_WEEK:
		default:
			return field.range();
		}
	}

	/**
	 * 음력에서 년월일에 기반한 기간 값을 얻는다.
	 *
	 * @param years  the number of years, may be negative
	 * @param months the number of months, may be negative
	 * @param days   the number of days, may be negative
	 * @return 음력 기반 기간, not null
	 */
	@Override
	public KLunarPeriod period ( int years , int months , int days ) {
		return KLunarPeriod.of( years, months, days );
	}
	//// ================================ TODO serialize
}
