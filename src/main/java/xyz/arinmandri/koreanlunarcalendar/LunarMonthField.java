package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.BIG_MONTH_SIZE;
import static xyz.arinmandri.koreanlunarcalendar.KLunarDate.LIL_MONTH_SIZE;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;


public enum LunarMonthField implements TemporalField
{
	/**
	 * 몇월달인가
	 */
	MONTH_N (LunarMonthUnit.LMONTH_BUNDLES, ChronoUnit.YEARS, ValueRange.of( 1, KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y )),

	/**
	 * 윤달여부
	 */
	MONTH_LEAP (LunarMonthUnit.LMONTHS, LunarMonthUnit.LMONTH_BUNDLES, ValueRange.of( 0, 1 )),

	/**
	 * 일 부분. 단; 윤달이면 +30.
	 * 날짜를 문자열로 바꿀 때 yyyy-MM-dd로만 나타내면 윤달 정보를 나타낼 수 없어서... 여러 가지 고민해봤지만 내 생각에 이게 최선이다.
	 */
	DAY_IN_LMONTH_BUNDLE (ChronoUnit.DAYS, LunarMonthUnit.LMONTH_BUNDLES, ValueRange.of( 1, 60 )),
	;

	private final TemporalUnit baseUnit;
	private final TemporalUnit rangeUnit;
	private final ValueRange range;

	private LunarMonthField( TemporalUnit baseUnit , TemporalUnit rangeUnit , ValueRange range ) {
		this.baseUnit = baseUnit;
		this.rangeUnit = rangeUnit;
		this.range = range;
	}

	@Override
	public TemporalUnit getBaseUnit () {
		return baseUnit;
	}

	@Override
	public TemporalUnit getRangeUnit () {
		return rangeUnit;
	}

	/**
	 * 이 필드의 가능한 값의 범위.
	 */
	@Override
	public ValueRange range () {
		return range;
	}

	/**
	 * 이 필드가 날짜의 성분인지 확인한다.<br>
	 * Checks if this field represents a component of a date.
	 */
	@Override
	public boolean isDateBased () {
		return true;
	}

	/**
	 * 이 필드가 시간의 성분인지 확인한다.<br>
	 * Checks if this field represents a component of a time.
	 */
	@Override
	public boolean isTimeBased () {
		return false;
	}

	/**
	 * 주어진 날짜/시간 개체가 이 필드를 지원하는지 확인한다.
	 * 
	 * @param temporal 이 필드를 지원하는지 확인할 날짜/시간 개체.
	 */
	@Override
	public boolean isSupportedBy ( TemporalAccessor temporal ) {
		//// My: 가능
		if( temporal instanceof KLunarDate ){
			return true;
		}

		//// 그외: temporal이 알아서
		try{
			temporal.getLong( this );
			return true;
		}
		catch( UnsupportedTemporalTypeException ex ){
			return false;
		}
	}

	/**
	 * 날짜/시간 값을 갖고 이 필드의 유효값 범위를 가져온다.<br>
	 * Get the range of valid values for this field using the temporal object to refine the result.
	 * <p>
	 * {@link #range()}와 달리 파라미터 {@code temporal}에 따라 결과가 달라진다.
	 * {@code temporal}이 포함된 달이 윤달 없는 달이라면 {@link #MONTH_LEAP}(윤달여부) 필드 값의 범위는 (0, 0)인데 비해
	 * 윤달 있는 달이면 (0, 1)이다.
	 * <p>
	 * {@link TemporalAccessor#range(TemporalField)}와 같다.
	 * 
	 * @throws UnsupportedTemporalTypeException
	 */
	@Override
	public ValueRange rangeRefinedBy ( TemporalAccessor temporal ) {
		if( temporal instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case MONTH_N:{
				return range;
			}
			case MONTH_LEAP:{
				if( kd.isLeapMonth() )
				    return range;
				if( kd.getLeapMonth() == kd.getMonth() )
				    return range;
				return ValueRange.of( 0, 0 );
			}
			case DAY_IN_LMONTH_BUNDLE:
				if( kd.isLeapMonth() )// 이번달이 윤달
				    return ValueRange.of( 1, kd.isBigMonth()
				            ? BIG_MONTH_SIZE + BIG_MONTH_SIZE
				            : BIG_MONTH_SIZE + LIL_MONTH_SIZE );
				if( kd.getLeapMonth() == kd.getMonth() )// 다음달이 윤달
				    return ValueRange.of( 1, kd.nextMonth().isBigMonth()
				            ? BIG_MONTH_SIZE + BIG_MONTH_SIZE
				            : BIG_MONTH_SIZE + LIL_MONTH_SIZE );
				return ValueRange.of( 1, kd.isBigMonth()// 윤달아님
				        ? BIG_MONTH_SIZE
				        : LIL_MONTH_SIZE );
			}
		}
		if( isSupportedBy( temporal ) ){
			return range;
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}

	/**
	 * 날짜/시간에서 이 필드의 값을 가져온다.<br>
	 * Gets the value of this field from the specified temporal object.
	 */
	@Override
	public long getFrom ( TemporalAccessor temporal ) {
		if( temporal instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case MONTH_N:{
				return kd.getMonth();
			}
			case MONTH_LEAP:{
				if( kd.isLeapMonth() )
				    return 1;
				else
				    return 0;
			}
			case DAY_IN_LMONTH_BUNDLE:
				return kd.getDay() + ( kd.isLeapMonth() ? 30 : 0 );
			}
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}

	/**
	 * 날짜/시간에서 이 필드의 값을 바꾼다.
	 * {@code temporal}의 값이 바뀌는 게 아니라 새 값의 새 개체가 반환된다.
	 * 
	 * @param temporal 필드에 값을 부여할 원본 날짜/시간 값, not null
	 * @param newValue 이 필드에 부여할 값
	 * @return 이 필드의 값이 입력값과 같은 새 날짜/시간 값, not null
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public < R extends Temporal > R adjustInto ( R temporal , long newValue ) {
		if( temporal instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case MONTH_N:{
				if( newValue > Integer.MAX_VALUE || newValue < Integer.MIN_VALUE )
				    throw new NonexistentDateException();
				return (R) kd.withMonth( (int) newValue );
			}
			case MONTH_LEAP:{
				if( newValue == 0 )
				    return (R) kd.withMonthLeap( false );
				if( newValue == 1 )
				    return (R) kd.withMonthLeap( true );
				throw new NonexistentDateException( "only 0 or 1 is valid for this field" );
			}
			case DAY_IN_LMONTH_BUNDLE:{
				if( newValue > Integer.MAX_VALUE || newValue < Integer.MIN_VALUE )
				    throw new NonexistentDateException();
				if( newValue > 30 )
				    return (R) kd.withMonthLeap( true ).withDay( (int) newValue - 30 );
				return (R) kd.withMonthLeap( false ).withDay( (int) newValue );
			}
			}
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}
}
