package xyz.arinmandri.koreanlunarcalendar;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;


public enum Gapja implements TemporalUnit {
	DAY_GAPJAS   (Duration.ofSeconds( 60 * 60 * 60 * 24 ), false),// 60일
	MONTH_GAPJAS (Duration.ofSeconds( 153_084_657L ), true),// 60개월 // 19년마다 7개 윤달이라 치자. 19년마다 19*12+7개월. 1년마다 ((19*12+7)/19)개월 ... 60 * 31_556_925L / ( ( 19 * 12 + 7 ) / 19.0 )
	YEAR_GAPJAS  (Duration.ofSeconds( 60 * 31_556_925L ), true),// 60년
	;// 1년 평균 Tropical Year 365.2421896698일 * 하루 86400초 = 1년 31,556,925.18747072초 https://en.wikipedia.org/wiki/Tropical_year

	private final Duration duration;
	private final boolean isDurationEstimated;

	private Gapja( Duration duration , boolean isDurationEstimated ) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long between ( Temporal temporal1Inclusive , Temporal temporal2Exclusive ) {
		// TODO Auto-generated method stub
		return 0;
	}

}
