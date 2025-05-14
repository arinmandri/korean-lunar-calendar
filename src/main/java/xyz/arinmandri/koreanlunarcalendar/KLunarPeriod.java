package xyz.arinmandri.koreanlunarcalendar;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.time.DateTimeException;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;


/**
 * 한국음력에서 기간(시간량 혹은 시간간격)을 나타낸다.
 * <p>
 * 해수, 달수, 일수, 윤달무시여부로 구성된다.
 * 윤달무시여부가 false이면 윤달도 평달과 동등한 달로 취급한다.
 * 윤달무시여부가 true이면 달을 셈할 때 윤달을 건너뛴다. 예를 들어 윤2월이 있어도 평2월에서 한 달을 더하면 평3월이 된다.
 * <p>
 * {@link KLunarDate#until(java.time.chrono.ChronoLocalDate)}의 결과값은 윤달무시여부 false이다.
 */
public final class KLunarPeriod implements java.time.chrono.ChronoPeriod , java.io.Serializable
{
	private static final long serialVersionUID = 1L;

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

	public int getYears () {
		return years;
	}

	public int getMonths () {
		return months;
	}

	public boolean isMonthLeapingMode () {
		return monthLeapingMode;
	}

	public int getDays () {
		return days;
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
	public KLunarPeriod plus ( TemporalAmount amountToAdd ) {
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
	public KLunarPeriod minus ( TemporalAmount amountToSubtract ) {
		if( amountToSubtract.getClass() != KLunarPeriod.class )
		    throw new DateTimeException( "not same type" );

		/*
		 * plus 참고
		 */
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

	/**
	 * 이 시간량을 다른 날짜/시간에 더한다.
	 * <p>
	 * {@code KLunarPeriod p = start.until( end );} 이렇게 구한 경우
	 * {@code start.plus( p )}의 결과는 {@code end}와 같다.
	 * 
	 * @param temporal 이 시간량이 더해질 피연산자
	 * @return 더한 결과
	 */
	@Override
	public Temporal addTo ( Temporal temporal ) {
		if( temporal instanceof KLunarDate ){
			Temporal t = temporal
			        .plus( years, YEARS )
			        .plus( months, monthLeapingMode ? LunarMonthUnit.LMONTH_BUNDLES : LunarMonthUnit.LMONTHS );
			try{// 년월 바꾸느라 일자 자동조정됐을 수 있는 거 원복 시도.
				t = t.with( ChronoField.DAY_OF_MONTH, temporal.get( ChronoField.DAY_OF_MONTH ) );
			}
			catch( NonexistentDateException e ){}
			return t.plus( days, DAYS );
		}
		throw new DateTimeException( "chronology of temporal must be " + KLunarChronology.INSTANCE.getId() );
	}

	/**
	 * 이 시간량을 다른 날짜/시간에서 뺀다.
	 * <p>
	 * 윤달, 대소월 일수 차이 때문에
	 * {@code KLunarPeriod p = start.until( end );} 이렇게 구했어도
	 * {@code end.minus( p )}는 {@code start}와 같지 않을 수도 있다.
	 * 
	 * @param temporal 이 시간량을 뺄 피연산자
	 * @return 뺀 결과
	 */
	@Override
	public Temporal subtractFrom ( Temporal temporal ) {
		/*
		 * addTo 참고.
		 */
		Temporal t = temporal
		        .minus( years, YEARS )
		        .minus( months, monthLeapingMode ? LunarMonthUnit.LMONTH_BUNDLES : LunarMonthUnit.LMONTHS );
		try{// 년월 바꾸느라 일자 자동조정됐을 수 있는 거 원복 시도.
			t = t.with( ChronoField.DAY_OF_MONTH, temporal.get( ChronoField.DAY_OF_MONTH ) );
		}
		catch( NonexistentDateException e ){}
		return t.minus( days, DAYS );
	}

	//// -------------------------------- object

	public boolean equals ( Object o ) {
		if( o == null ) return false;

		if( o instanceof KLunarPeriod ){
			KLunarPeriod kp = (KLunarPeriod) o;
			return kp.years == years
			        && kp.months == months
			        && kp.monthLeapingMode == monthLeapingMode
			        && kp.days == days;
		}
		return false;
	}

	@Override
	public int hashCode () {
		int i = years
		        + Integer.rotateLeft( months, 8 )
		        + Integer.rotateLeft( days, 16 );
		if( monthLeapingMode )
		    i = i ^ -1;
		return i;
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
				buf.append( months ).append( monthLeapingMode ? "MB" : 'M' );
			}
			if( days != 0 ){
				buf.append( days ).append( 'D' );
			}
			return buf.toString();
		}
	}


	//// -------------------------------- serialize

	@java.io.Serial
	private Object writeReplace () {
		return new Ser( Ser.PERIOD_TYPE , this );
	}

	@java.io.Serial
	private void readObject ( ObjectInputStream in ) throws InvalidObjectException {
		throw new InvalidObjectException( "Deserialization via serialization delegate" );
	}
}
