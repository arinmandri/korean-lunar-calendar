package xyz.arinmandri.koreanlunarcalendar;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;


// TODO test
public enum LunarMonthField implements TemporalField
{
	/**
	 * 몇월달인가
	 */
	MONTH_N (LunarMonthUnit.LMONTH_BUNDLES, ChronoUnit.YEARS, ValueRange.of( 1, 12 )),

	/**
	 * 윤달여부
	 */
	MONTH_LEAP (LunarMonthUnit.LMONTHS, LunarMonthUnit.LMONTH_BUNDLES, ValueRange.of( 0, 1 )),
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

	@Override
	public ValueRange range () {
		return range;
	}

	@Override
	public boolean isDateBased () {
		return true;
	}

	@Override
	public boolean isTimeBased () {
		return false;
	}

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

	@Override
	public ValueRange rangeRefinedBy ( TemporalAccessor temporal ) {
		if( temporal instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case MONTH_N:{
				return ValueRange.of( 1, 12 );
			}
			case MONTH_LEAP:{
				if( kd.isLeapMonth() )
				    return range;
				if( kd.getLeapMonth() == kd.getMonth() )
				    return range;
				return ValueRange.of( 0, 0 );
			}
			}
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}

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
			}
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}

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
			}
		}
		throw new UnsupportedTemporalTypeException( "not supported temporal type" );
	}
}
