package xyz.arinmandri.koreanlunarcalendar;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;


// TODO 다른 달력끼리 ChronoPeriodImpl 클래스를 공유해도 덧셈은 안 되는군. 마찬가지로 이건 KLunarChronology 전용이어야 하는 듯.
final class KLunarPeriod implements java.time.chrono.ChronoPeriod
{
	final int years;
	final int months;
	final int days;

	private static final List<TemporalUnit> SUPPORTED_UNITS = List.of( YEARS, MONTHS, DAYS );// TODO LunarMonthUnit 넣어? 말어?

	public KLunarPeriod( int years , int months , int days ) {
		super();
		this.years = years;
		this.months = months;
		this.days = days;
	}

	@Override
	public long get ( TemporalUnit unit ) {
		if( unit == ChronoUnit.YEARS ){
			return years;
		}
		else if( unit == ChronoUnit.MONTHS ){
			return months;
		}
		else if( unit == ChronoUnit.DAYS ){
			return days;
		}
		else{
			throw new UnsupportedTemporalTypeException( "Unsupported unit: " + unit );
		}
	}

	@Override
	public List<TemporalUnit> getUnits () {
		return SUPPORTED_UNITS;
	}

	@Override
	public Chronology getChronology () {
		return KLunarChronology.INSTANCE;
	}

	@Override
	public ChronoPeriod plus ( TemporalAmount amountToAdd ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChronoPeriod minus ( TemporalAmount amountToSubtract ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChronoPeriod multipliedBy ( int scalar ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChronoPeriod normalized () {
		// TODO 1년의 달 개수가 일정치는 않지만 19년마다 윤달 7개라는데 이건 진짜 일정한지 확인 해야지
		return null;
	}

	@Override
	public Temporal addTo ( Temporal temporal ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Temporal subtractFrom ( Temporal temporal ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString () {
		if( isZero() ){
			return getChronology().toString() + " P0D";
		}
		else{
			StringBuilder buf = new StringBuilder();
			buf.append( getChronology().toString() ).append( ' ' ).append( 'P' );
			if( years != 0 ){
				buf.append( years ).append( 'Y' );
			}
			if( months != 0 ){
				buf.append( months ).append( 'M' );
			}
			if( days != 0 ){
				buf.append( days ).append( 'D' );
			}
			return buf.toString();
		}
	}
}
