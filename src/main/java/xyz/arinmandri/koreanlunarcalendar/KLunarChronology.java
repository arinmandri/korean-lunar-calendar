package xyz.arinmandri.koreanlunarcalendar;

import java.time.DateTimeException;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.util.List;


/**
 * 한국 음력
 */
public class KLunarChronology extends AbstractChronology implements java.io.Serializable
{

	public static final KLunarChronology INSTANCE = new KLunarChronology();

	private KLunarChronology() {}

	/**
	 * 이 역법의 ID
	 * <p>
	 * ID는 {@code Chronology} 을 유일히 식별한다.
	 * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
	 *
	 * @return the chronology ID, not null
	 * @see #getCalendarType()
	 */
	@Override
	public String getId () {
		return "KoreanLunar";
	}

	/**
	 * CLDR에 정의된 식별자??? 가 있나?
	 */
	@Override
	public String getCalendarType () {
		return null;// TODO
	}

	/**
	 * 기년, 월, 일 값으로 이 역법에서의 날짜를 얻는다.
	 *
	 * @param prolepticYear 기년(기원 기준 년도)
	 * @param month         월
	 * @param day           일
	 * @return 이 chronology의 로컬 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 */
	@Override
	public ChronoLocalDate date ( int prolepticYear , int month , int day ) {
		return KLunarDate.of( prolepticYear, month, day );
	}

	/**
	 * 기년, 연중일 값으로 이 역법에서의 날짜를 얻는다.
	 *
	 * @param prolepticYear 기년(기원 기준 년도)
	 * @param dayOfYear     연중일(이 해의 몇 번째 일인가)
	 * @return 이 역법에서의 로컬 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 */
	@Override
	public ChronoLocalDate dateYearDay ( int prolepticYear , int dayOfYear ) {
		return KLunarDate.ofYearDay( prolepticYear, dayOfYear );
	}

	/**
	 * 에포크일 값으로 이 역법에서의 날짜를 얻는다.
	 *
	 * @param epochDay 에포크일(서력 1970년 1월 1일로부터 경과일)
	 * @return 이 역법에서의 로컬 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 */
	@Override
	public ChronoLocalDate dateEpochDay ( long epochDay ) {
		return KLunarDate.ofEpochDay( epochDay );
	}

	/**
	 * 다른 temporal 개체를 이 역법에서의 날짜로 변환한 것을 얻는다.
	 * <p>
	 * This method matches the signature of the functional interface {@link TemporalQuery}
	 * allowing it to be used as a query via method reference, {@code aChronology::date}.
	 *
	 * @param temporal 변환할 temporal 개체, not null
	 * @return 이 역법에서의 날짜, not null
	 * @throws DateTimeException 날짜 생성 불가시
	 * @see KLunarDate#from(TemporalAccessor)
	 */
	@Override
	public ChronoLocalDate date ( TemporalAccessor temporal ) {
		return KLunarDate.from( temporal );
	}

	/**
	 * 특정 년도가 윤년인지 확인한다.
	 * 한국 음력에는 윤달 개념이 있다. 윤달을 포함한 해를 윤년이라 한다.
	 * 유효한 년도 범위를 벗어난 경우에도 예외를 던지지 않으며 false를 반환한다.
	 *
	 * @param prolepticYear 기년(기원 기준 년도), 유효범위를 확인 안 함.
	 * @return 윤년인 경우 true
	 */
	@Override
	public boolean isLeapYear ( long prolepticYear ) {
		// TODO ydss를 어디에 놓고 접근해야 하나 고민
		throw new UnsupportedOperationException( "Unimplemented method 'isLeapYear'" );
	}

    /**
	 * 주어진 시대와 그 시대에서의 연도 값으로 기년을 계산해 내놓는다.
	 * (그러나 이 역법은 년도는 서력의 것을 그대로 쓰고 기원후 외의 다른 시대를 지원하지 않는다.)
	 * 
	 * @param era       {@link IsoEra#CE} 만 허용한다. not null
	 * @param yearOfEra the chronology year-of-era
	 * @return 기년(기원 기준 년도)
	 * @throws DateTimeException  {@link IsoEra#CE} 가 아닌 경우
	 * @throws ClassCastException {@code era} 가 {@link IsoEra} 가 아닌 경우
	 */
	@Override
	public int prolepticYear ( Era era , int yearOfEra ) {
		if( era instanceof IsoEra ){
			if( era == IsoEra.CE )
			    return yearOfEra;
		}
		throw new ClassCastException( "Era must be IsoEra" );
	}

	@Override
	public Era eraOf ( int eraValue ) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException( "Unimplemented method 'eraOf'" );
	}

	@Override
	public List<Era> eras () {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException( "Unimplemented method 'eras'" );
	}

	@Override
	public ValueRange range ( ChronoField field ) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException( "Unimplemented method 'range'" );
	}

	@Override
	public KLunarPeriod period ( int years , int months , int days ) {
		return KLunarPeriod.of( years, months, days );
	}
	//// ================================ TODO serialize
}
