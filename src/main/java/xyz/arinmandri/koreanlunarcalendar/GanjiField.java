package xyz.arinmandri.koreanlunarcalendar;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;


public enum GanjiField implements TemporalField
{
	SECHA   (ChronoUnit.YEARS,  Gapja.YEAR_GAPJAS,  ValueRange.of( 1, 60 )),// 세차(년의 간지)
	WOLGEON (ChronoUnit.MONTHS, Gapja.MONTH_GAPJAS, ValueRange.of( 1, 60 )),// 월건(월의 간지)
	ILJIN   (ChronoUnit.DAYS,   Gapja.DAY_GAPJAS,   ValueRange.of( 1, 60 )),// 일진(일의 간지)
	// XXX 시진도...?
	;

	private final TemporalUnit baseUnit;
	private final TemporalUnit rangeUnit;
	private final ValueRange range;

	private GanjiField( TemporalUnit baseUnit , TemporalUnit rangeUnit , ValueRange range ) {
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
