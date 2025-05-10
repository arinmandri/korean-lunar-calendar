package xyz.arinmandri.koreanlunarcalendar;

import java.time.Duration;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;


// TODO test
public enum LunarMonthUnit implements TemporalUnit
{
	/**
	 * 달.
	 * 대부분 달은 뒤에 윤달이 없으므로 (1달 == 1달묶음)이다.
	 * 1년은 12달 혹은 13달이다.
	 */
	LMONTHS (Duration.ofSeconds( 24 * 60 * 60 * 241058L / 8163 ), true),// 현재의 지원범위 첫날부터 끝날까지 총 월수=8163, 총 일수=241058;

	/**
	 * 평달과 윤달의 묶음.
	 * 예: 2004년은 (1월), (2월, 윤2월), (3월), ...
	 * 1년은 12달묶음이다.
	 */
	LMONTH_BUNDLES (Duration.ofSeconds( 31556952L / 12 ), true),// 1개월이나 2개월이지만 평균으로는 양력 1달과 같다. ChoronoUnit.MONTHS 싹 베껴옴.
	;

	private final Duration duration;
	private final boolean isDurationEstimated;

	private LunarMonthUnit( Duration duration , boolean isDurationEstimated ) {
		this.duration = duration;
		this.isDurationEstimated = isDurationEstimated;
	}

	@Override
	public Duration getDuration () {
		return duration;
	}

	public double getDurationInDays () {
		return ( (double) duration.getSeconds() ) / ( 24 * 60 * 60 );
	}

	@Override
	public boolean isDurationEstimated () {
		return isDurationEstimated;
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
	public boolean isSupportedBy ( Temporal temporal ) {
		//// My: 가능
		if( temporal instanceof ChronoLocalDate
		        && ( (ChronoLocalDate) temporal ).getChronology() == KLunarChronology.INSTANCE ){
			return true;
		}
		else if( temporal instanceof ChronoLocalDateTime<?>
		        && ( (ChronoLocalDateTime<?>) temporal ).getChronology() == KLunarChronology.INSTANCE ){
			return true;
		}

		//// 그외: temporal이 알아서
		try{
			temporal.plus( 1, this );
			return true;
		}
		catch( UnsupportedTemporalTypeException ex ){
			return false;
		}
		catch( RuntimeException ex ){
			try{
				temporal.plus( -1, this );
				return true;
			}
			catch( RuntimeException ex2 ){
				return false;
			}
		}
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public < R extends Temporal > R addTo ( R temporal , long amount ) {

		//// My: 여기서
		if( temporal instanceof KLunarDate ){
			if( amount > Integer.MAX_VALUE
			        || amount < Integer.MIN_VALUE )
			    throw new OutOfRangeException();
			switch( this ){
			case LMONTHS:
				return (R) ( (KLunarDate) temporal ).plusMonths( (int) amount );
			case LMONTH_BUNDLES:
				return (R) ( (KLunarDate) temporal ).plusNamedMonths( (int) amount );
			}
		}

		//// 그외: temporal이 알아서
		return (R) temporal.plus( amount, this );
	}

	@Override
	public long between ( Temporal temporal1Inclusive , Temporal temporal2Exclusive ) {
		if( temporal1Inclusive.getClass() != temporal2Exclusive.getClass() ){
			return temporal1Inclusive.until( temporal2Exclusive, this );
		}

		if( temporal1Inclusive instanceof KLunarDate ){
			KLunarDate kd1 = (KLunarDate) temporal1Inclusive;
			KLunarDate kd2 = (KLunarDate) temporal2Exclusive;
			switch( this ){
			case LMONTHS:
				return kd2.isBefore( kd1 )
				        ? -betweenKd( kd2, kd1 )
				        : betweenKd( kd1, kd2 );
			case LMONTH_BUNDLES:
				//// end의(년*12+월) - start의(년*12+월) - day앞뒤고려
				return ( kd2.getYear() - kd1.getYear() ) * KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y
				        + kd2.getMonth() - kd1.getMonth()
				        - ( kd2.getDay() < kd1.getDay() ? 1 : 0 );
			}
		}

		//// 그외: temporal이 알아서
		return temporal1Inclusive.until( temporal2Exclusive, this );
	}

	private int betweenKd ( KLunarDate kd1 , KLunarDate kd2 ) {// kd1<=kd2
		int count;

		if( kd2.getYear() == kd1.getYear() ){
			count = kd2.getMonthOrdinal() - kd1.getMonthOrdinal();
		}

		else{
			//// kd1(inclusive)~그해끝 달 수
			count = kd1.lengthOfYearInM() - kd1.getMonthOrdinal();

			//// 그해첨~kd2(exclusive) 달 수
			count += kd2.getMonthOrdinal();

			//// kd1의 해와 kd2의 해들의 달 수 합
			for( int y = kd1.getYear() + 1 ; y < kd2.getYear() ; y += 1 ){
				count += KLunarChronology.INSTANCE.isLeapYear( y )
				        ? KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y + 1
				        : KLunarDate.NAMED_MONTHS_NUMBER_IN_1Y;;
			}
		}

		//// 달 빼고 일자만으로 kd1, kd2 선후 비교
		count -= kd1.getDay() > kd2.getDay() ? 1 : 0;

		return count;
	}
}
