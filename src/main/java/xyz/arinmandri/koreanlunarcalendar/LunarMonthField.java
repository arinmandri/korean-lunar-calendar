package xyz.arinmandri.koreanlunarcalendar;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ValueRange rangeRefinedBy ( TemporalAccessor temporal ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getFrom ( TemporalAccessor temporal ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public < R extends Temporal > R adjustInto ( R temporal , long newValue ) {
		// TODO Auto-generated method stub
		return null;
	}
}
