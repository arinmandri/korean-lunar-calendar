package xyz.arinmandri.koreanlunarcalendar;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;


public enum Leap implements TemporalField
{
	/**
	 * 이 필드가 0이면 평달, 1이면 윤달이다.
	 * 음력의 윤달 때문에 [년-월-일]만으로 날짜를 다 표현하지 못한다.
	 * 예: 2004년에는 1월, 2월, 윤2월, 3월, ...이 있다.
	 * 이를 (1월), (2월, 윤2월), (3월), ... 식으로 같은 수의 월끼리 묶는다.
	 * 이 묶음의 단위 내에서 몇 번째 월인지를 0부터 센 것과 같다.
	 * 
	 * @see KLunarAdditionalUnit#MONTH_GROUPS
	 */
	MONTH(ChronoUnit.MONTHS, KLunarAdditionalUnit.MONTH_GROUPS, ValueRange.of( 0, 1 )),
	;

	private final TemporalUnit baseUnit;
	private final TemporalUnit rangeUnit;
	private final ValueRange range;

	private Leap( TemporalUnit baseUnit , TemporalUnit rangeUnit , ValueRange range ) {
		this.baseUnit = baseUnit;
		this.rangeUnit = rangeUnit;
		this.range = range;
	}

	@Override
	public TemporalUnit getBaseUnit () {
		return baseUnit;
	}

	@Override
	public TemporalUnit getRangeUnit () {
		return rangeUnit;
	}

	@Override
	public ValueRange range () {
		return range;
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
	public boolean isSupportedBy ( TemporalAccessor temporal ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ValueRange rangeRefinedBy ( TemporalAccessor temporal ) {
		if(temporal instanceof KLunarDate) {
			KLunarDate kd = (KLunarDate) temporal;
			switch(this) {
			case MONTH:{
				if( kd.isLeapMonth() )
				    return range;
				if( kd.getLeapMonth() == kd.getMonth() )
				    return range;
				return ValueRange.of( 0, 0 );
			}
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public long getFrom ( TemporalAccessor temporal ) {
		if( temporal instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) temporal;
			switch( this ){
			case MONTH:{
				if( kd.isLeapMonth() )
				    return 1;
				else
				    return 0;
			}
			default:
				break;
			}
		}
		throw new UnsupportedTemporalTypeException( "Unsupported field: " + this );
	}

	@Override
	public < R extends Temporal > R adjustInto ( R temporal , long newValue ) {
		// TODO Auto-generated method stub
		return null;
	}
}
