package xyz.arinmandri.koreanlunarcalendar;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;


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
	LMONTHS (Duration.ofSeconds( 31556952L * 19 / ( 19 * 12 + 7 ) ), true),// 19년에 윤달 7개라 치고
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

	public int getDurationInDays () {
		return ( (int) duration.getSeconds() ) / ( 24 * 60 * 60 );
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
		return temporal.getClass() == KLunarDate.class;
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
