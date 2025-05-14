package xyz.arinmandri.koreanlunarcalendar;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;


/**
 * 날짜/시간의 간지 필드
 */
public enum GanjiField implements TemporalField
{
    // XXX time: 시진도...?
	ILJIN   (ChronoUnit.DAYS,   Gapja.DAY_GAPJAS),  // 일진(일의 간지)
	WOLGEON (ChronoUnit.MONTHS, Gapja.MONTH_GAPJAS),// 월건(월의 간지)
	SECHA   (ChronoUnit.YEARS,  Gapja.YEAR_GAPJAS), // 세차(년의 간지)
	;

	private final TemporalUnit baseUnit;
	private final TemporalUnit rangeUnit;

	private GanjiField( TemporalUnit baseUnit , TemporalUnit rangeUnit ) {
		this.baseUnit = baseUnit;
		this.rangeUnit = rangeUnit;
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
	 * 1부터 60까지이다.
	 */
	@Override
	public ValueRange range () {
		return ValueRange.of( 1, 60 );
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
			temporal.get( this );
			return true;
		}
		catch( UnsupportedTemporalTypeException ex ){
			return false;
		}
	}

	/**
	 * 이 날짜/시간에서 가질 수 있는 이 필드의 가능한 값의 범위.
	 * 간지 값은 날짜/시간에 무관하게 일정히 1부터 60까지 가질 수 있다.
	 * 즉 {@link #range()}와 같다.
	 * 
	 * @throws UnsupportedTemporalTypeException
	 */
	@Override
	public ValueRange rangeRefinedBy ( TemporalAccessor temporal ) {
		if( temporal instanceof KLunarDate ){
			return ValueRange.of( 1, 60 );
		}
		throw new UnsupportedTemporalTypeException( "Unsupported field: " + this );
	}

	/**
	 * 이 날짜/시간의 간지값 조회.
	 * 
	 * @throws UnsupportedTemporalTypeException
	 */
	@Override
	public long getFrom ( TemporalAccessor temporal ) {
		if( temporal instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case SECHA:
				return kd.getSecha().ordinal() + 1;
			case WOLGEON:
				return kd.getWolgeon().ordinal() + 1;
			case ILJIN:
				return kd.getIljin().ordinal() + 1;
			default:
				break;
			}
		}
		throw new UnsupportedTemporalTypeException( "Unsupported field: " + this );
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
			int newValueInt = Math.toIntExact( newValue );
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case ILJIN:
				return (R) kd.withIljin( Ganji.values()[newValueInt - 1] );
			case WOLGEON:
				return (R) kd.withWolgeon( Ganji.values()[newValueInt - 1] );
			case SECHA:
				return (R) kd.withSecha( Ganji.values()[newValueInt - 1] );
			}
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}

}
