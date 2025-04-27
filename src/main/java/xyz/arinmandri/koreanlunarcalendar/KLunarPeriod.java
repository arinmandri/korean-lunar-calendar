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


// TODO test
public final class KLunarPeriod implements java.time.chrono.ChronoPeriod
{
	final int years;
	final int months;
	final boolean monthLeapingMode;// true: 달을 헬 때 윤달을 무시한다. false: 윤달을 동등한 한 달로 취급한다.
	final int days;

	private static final List<TemporalUnit> SUPPORTED_UNITS = List.of( YEARS, MONTHS, DAYS );

	private KLunarPeriod( int years , int months , boolean monthLeapingMode , int days ) {
		this.years = years;
		this.months = months;
		this.monthLeapingMode = monthLeapingMode;
		this.days = days;
	}

	public static KLunarPeriod of ( int years , int months , int days ) {
		return new KLunarPeriod( years , months , false , days );
	}

	public static KLunarPeriod of ( int years , int months , boolean monthLeapingMode , int days ) {
		return new KLunarPeriod( years , months , monthLeapingMode , days );
	}

	@Override
	public long get ( TemporalUnit unit ) {
		if( unit == ChronoUnit.YEARS ){
			return years;
		}
		if( unit == ChronoUnit.MONTHS ){
			return months;
		}
		if( unit == LunarMonthUnit.LMONTH_BUNDLES ){
			if( monthLeapingMode )
			    return months;
			else
			    throw new UnsupportedTemporalTypeException( "this period instance is not in month-leaping mode. use LunarMonthUnit.LMONTHS instead." );
		}
		if( unit == LunarMonthUnit.LMONTHS ){
			if( !monthLeapingMode )
			    return months;
			else
			    throw new UnsupportedTemporalTypeException( "this period instance is in month-leaping mode. use LunarMonthUnit.LMONTH_BUNDLES instead." );
		}
		if( unit == ChronoUnit.MONTHS ){
			return months;
		}
		throw new UnsupportedTemporalTypeException( "Unsupported unit: " + unit );
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

		if( monthLeapingMode ){
			int y = years + months / KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
			int m = months % KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
			return of( y, m, true, days );
		}
		else{
			final int M_IN_19Y = 12 * 19 + 7;// 19년의 달의 수 TODO 1년의 달 개수가 일정치는 않지만 19년마다 윤달 7개라는데 이건 진짜 일정한지 확인 해야지
			int y = years + months / M_IN_19Y;
			int m = months % M_IN_19Y;
			return of( y, m, true, days );
		}
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
