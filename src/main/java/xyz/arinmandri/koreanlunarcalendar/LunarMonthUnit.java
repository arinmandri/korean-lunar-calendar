package xyz.arinmandri.koreanlunarcalendar;

import java.time.Duration;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;


public enum LunarMonthUnit implements TemporalUnit
{
	/**
	 * 평달과 윤달의 묶음.
	 * 예: 2004년은 (1월), (2월, 윤2월), (3월), ...
	 * 1년은 12달묶음이다.
	 */
	LMONTH_BUNDLES (Duration.ofSeconds( 31556952L / 12 ), true),// 1개월이나 2개월이지만 평균으로는 양력 1달과 같다. ChoronoUnit.MONTHS 싹 베껴옴.

	/**
	 * 달.
	 * 대부분 달은 (1달 == 1달묶음)이다.
	 * 1년은 12달 혹은 13달이다.
	 */
	LMONTHS (Duration.ofSeconds( 24 * 60 * 60 * 68304L / 2313 ), true),// TODO extend_range: 현재의 지원범위 첫날부터 끝날까지 총 월수=68304, 총 일수=2313;
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
	public < R extends Temporal > R addTo ( R temporal , long amount ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long between ( Temporal temporal1Inclusive , Temporal temporal2Exclusive ) {
		// TODO Auto-generated method stub
		return 0;
	}

}
