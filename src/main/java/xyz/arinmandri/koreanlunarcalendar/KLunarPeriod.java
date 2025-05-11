package xyz.arinmandri.koreanlunarcalendar;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;


// TODO test
/**
 * 한국음력에서 시간량 혹은 시간간격
 */
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

	/**
	 * 두 시간량을 합친다.
	 * 같은 타입끼리만 합쳐진다.
	 * 
	 * @throws DateTimeException
	 * @throws ArithmeticException if numeric overflow occurs
	 */
	@Override
	public ChronoPeriod plus ( TemporalAmount amountToAdd ) {
		if( amountToAdd.getClass() != KLunarPeriod.class )
		    throw new DateTimeException( "not same type" );

		KLunarPeriod a = (KLunarPeriod) amountToAdd;
		if( monthLeapingMode != a.monthLeapingMode )
		    throw new DateTimeException( "not same mode" );

		return of(
		        Math.addExact( years, a.years ),
		        Math.addExact( months, a.months ),
		        monthLeapingMode,
		        Math.addExact( days, a.days ) );
	}

	/**
	 * 시간량을 뺀다.
	 * 같은 타입끼리만 뺄셈이 된다.
	 * 
	 * @throws DateTimeException
	 * @throws ArithmeticException if numeric overflow occurs
	 */
	@Override
	public ChronoPeriod minus ( TemporalAmount amountToSubtract ) {
		if( amountToSubtract.getClass() != KLunarPeriod.class )
		    throw new DateTimeException( "not same type" );

		KLunarPeriod a = (KLunarPeriod) amountToSubtract;
		if( monthLeapingMode != a.monthLeapingMode )
		    throw new DateTimeException( "not same mode" );

		return of(
		        Math.subtractExact( years, a.years ),
		        Math.subtractExact( months, a.months ),
		        monthLeapingMode,
		        Math.subtractExact( days, a.days ) );
	}

	@Override
	public ChronoPeriod multipliedBy ( int scalar ) {
		return of( years * scalar, months * scalar, monthLeapingMode, days * scalar );
	}

	/**
	 * normalize
	 * 윤달 무시 상태인 경우: 12달을 1년으로 바꾼다.
	 * 윤달 취급 상태인 경우: 235달을 19년으로 바꾼다.
	 * 
	 * @throws ArithmeticException if numeric overflow occurs
	 */
	@Override
	public ChronoPeriod normalized () {
		long y;
		int m;
		if( monthLeapingMode ){// 윤달 무시: 1년에 12달
			y = years + months / KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
			m = months % KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;
		}
		else{// 윤달 취급: 19년마다 윤달 7개, 총 235달.(19년이 어디에 걸치느냐에 따라 7개가 아닐 수는 있지만)
			y = years + months / 235;
			m = months % 235;
		}
		return of( Math.toIntExact( y ), m, monthLeapingMode, days );
	}

	@Override
	public Temporal addTo ( Temporal temporal ) {
		if( ( temporal instanceof ChronoLocalDate
		        && ( (ChronoLocalDate) temporal ).getChronology() == KLunarChronology.INSTANCE )
		        || ( temporal instanceof ChronoLocalDateTime<?>
		                && ( (ChronoLocalDateTime<?>) temporal ).getChronology() == KLunarChronology.INSTANCE ) ){
			temporal.plus( years, YEARS );
			temporal.plus( months, monthLeapingMode ? LunarMonthUnit.LMONTH_BUNDLES : LunarMonthUnit.LMONTHS );
			temporal.plus( days, DAYS );
		}
		throw new DateTimeException( "chronology of temporal must be " + KLunarChronology.INSTANCE.getId() );
	}

	@Override
	public Temporal subtractFrom ( Temporal temporal ) {
		if( ( temporal instanceof ChronoLocalDate
		        && ( (ChronoLocalDate) temporal ).getChronology() == KLunarChronology.INSTANCE )
		        || ( temporal instanceof ChronoLocalDateTime<?>
		                && ( (ChronoLocalDateTime<?>) temporal ).getChronology() == KLunarChronology.INSTANCE ) ){
			temporal.minus( years, YEARS );
			temporal.minus( months, monthLeapingMode ? LunarMonthUnit.LMONTH_BUNDLES : LunarMonthUnit.LMONTHS );
			temporal.minus( days, DAYS );
		}
		throw new DateTimeException( "chronology of temporal must be " + KLunarChronology.INSTANCE.getId() );
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
