package xyz.arinmandri.koreanlunarcalendar;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
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
		//// KLunarChronology: 가능
		if( temporal instanceof ChronoLocalDate ){
			if( ( (ChronoLocalDate) temporal ).getChronology() == KLunarChronology.INSTANCE )
			    return true;
		}
		else if( temporal instanceof ChronoLocalDateTime<?> ){
			if( ( (ChronoLocalDateTime<?>) temporal ).getChronology() == KLunarChronology.INSTANCE )
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
				return ValueRange.of( 0, 12 );
			}
			case MONTH_LEAP:{
				if( kd.isLeapMonth() )
				    return range;
				if( kd.getLeapMonth() == kd.getMonth() )// TODO ??? 이거 맞음?
				    return range;
				return ValueRange.of( 0, 0 );
			}
			default:
				break;
			}
		}
		return null;
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
			default:
				break;
			}
		}
		throw new UnsupportedTemporalTypeException( "Unsupported field: " + this );
	}

	@Override
	public < R extends Temporal > R adjustInto ( R temporal , long newValue ) {
		// TODO Auto-generated method stub
		return null;
	}
}
