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
		if( unit == ChronoUnit.DAYS ){
			return days;
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

		if( monthLeapingMode ){// 윤달 무시: 1년에 12달
			int y = years + months / KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
			int m = months % KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
			return of( y, m, true, days );
		}
		else{// 윤달 취급: 19년마다 윤달 7개, 총 235달.(19년이 어디에 걸치느냐에 따라 7개가 아닐 수는 있지만)
			int y = years + months / 235;
			int m = months % 235;
			return of( y, m, false, days );
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
