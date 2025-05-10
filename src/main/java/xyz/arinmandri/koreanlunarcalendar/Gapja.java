package xyz.arinmandri.koreanlunarcalendar;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;


public enum Gapja implements TemporalUnit {
	DAY_GAPJAS (
	        ChronoUnit.DAYS,
	        Duration.ofSeconds( 60 * 60 * 60 * 24 ),// 60일
	        false),
	MONTH_GAPJAS (
	        LunarMonthUnit.LMONTH_BUNDLES,
	        Duration.ofSeconds( 31556952L * 5 ),// 60개월
	        true),
	YEAR_GAPJAS (
	        ChronoUnit.YEARS,
	        Duration.ofSeconds( 60 * 31_556_925L ),// 60년 // 1년 평균 Tropical Year 365.2421896698일 * 하루 86400초 = 1년 31,556,925.18747072초 https://en.wikipedia.org/wiki/Tropical_year
	        true),
	;

	private final TemporalUnit baseUnit;// 이거는 이거의 60배
	private final Duration duration;
	private final boolean isDurationEstimated;

	private Gapja( TemporalUnit baseUnit , Duration duration , boolean isDurationEstimated ) {
		this.baseUnit = baseUnit;
		this.duration = duration;
		this.isDurationEstimated = isDurationEstimated;
	}

	@Override
	public Duration getDuration () {
		return duration;
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
	public < R extends Temporal > R addTo ( R temporal , long amount ) {
		return baseUnit.addTo( temporal, amount * Ganji.CYCLE_SIZE );
	}

	@Override
	public long between ( Temporal temporal1Inclusive , Temporal temporal2Exclusive ) {
		if( temporal1Inclusive.getClass() != temporal2Exclusive.getClass() ){
			return temporal1Inclusive.until( temporal2Exclusive, this );
		}

		return baseUnit.between( temporal1Inclusive, temporal2Exclusive ) / Ganji.CYCLE_SIZE;
	}

}
