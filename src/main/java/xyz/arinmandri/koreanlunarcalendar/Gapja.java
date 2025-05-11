package xyz.arinmandri.koreanlunarcalendar;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;


/**
 * 육십갑자 시간단위(60년, 60개월, 60일)
 */
public enum Gapja implements TemporalUnit {
	DAY_GAPJAS (ChronoUnit.DAYS, false),
	MONTH_GAPJAS (LunarMonthUnit.LMONTH_BUNDLES, true),
	YEAR_GAPJAS (ChronoUnit.YEARS, true),
	;

	private final TemporalUnit baseUnit;// 이거는 이거의 60배
	private final boolean isDurationEstimated;

	private Gapja( TemporalUnit baseUnit , boolean isDurationEstimated ) {
		this.baseUnit = baseUnit;
		this.isDurationEstimated = isDurationEstimated;
	}

	@Override
	public Duration getDuration () {
		return baseUnit.getDuration().multipliedBy( Ganji.CYCLE_SIZE );
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
