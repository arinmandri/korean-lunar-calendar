package xyz.arinmandri.koreanlunarcalendar;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;


public class KLDateFormatters
{
	/**
	 * 인간친화적 꼴
	 * <ul>
	 * <li>2004년 2월 10일
	 * <li>2004년 윤2월 10일
	 * </ul>
	 */
	public static final DateTimeFormatter HUMAN_DATE = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendLiteral( "년 " )
	        .appendText( LunarMonthField.MONTH_LEAP, Map.of( 0L, "", 1L, "윤" ) )
	        .appendValue( LunarMonthField.MONTH_N )
	        .appendLiteral( "월 " )
	        .appendValue( ChronoField.DAY_OF_MONTH )
	        .appendLiteral( "일" )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * yyyy-MM-dd 꼴인데 윤달이면 일에 +30 한다.
	 * yyyy-MM-dd 꼴에 맞추면서도, 텍스트로 바꿨을 때 모든 날짜의 길이가 똑같고, 텍스트순 정렬 결과가 날짜순과 일치하게 된다.
	 * <ul>
	 * <li>2004년 2월 10일 --> 2004-02-10
	 * <li>2004년 윤2월 10일 --> 2004-02-40
	 * </ul>
	 */
	public static final DateTimeFormatter ISOLIKE_DATE = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendLiteral( "-" )
	        .appendValue( LunarMonthField.MONTH_N, 2 )
	        .appendLiteral( "-" )
	        .appendValue( LunarMonthField.DAY_IN_LMONTH_BUNDLE, 2 )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * yyyyMMdd 꼴인데 윤달이면 일에 +30 한다.
	 * yyyyMMdd 꼴에 맞추면서도, 텍스트로 바꿨을 때 모든 날짜의 길이가 똑같고, 텍스트순 정렬 결과가 날짜순과 일치하게 된다.
	 * <ul>
	 * <li>2004년 2월 10일 --> 20040210
	 * <li>2004년 윤2월 10일 --> 20040240
	 * </ul>
	 */
	public static final DateTimeFormatter EIGHT_DIGITS = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendValue( LunarMonthField.MONTH_N, 2 )
	        .appendValue( LunarMonthField.DAY_IN_LMONTH_BUNDLE, 2 )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * yyMMdd 꼴인데 윤달이면 일에 +30 한다.
	 * yyMMdd 꼴에 맞추면서도, 텍스트로 바꿨을 때 모든 날짜의 길이가 똑같고, 텍스트순 정렬 결과가 날짜순과 일치하게 된다.
	 * yy는 1951년에서 2050년까지인 것으로 해석된다.
	 * <ul>
	 * <li>2004년 2월 10일 --> 040210
	 * <li>2004년 윤2월 10일 --> 040240
	 * </ul>
	 */
	public static final DateTimeFormatter SIX_DIGITS = new DateTimeFormatterBuilder()
	        .appendValueReduced( ChronoField.YEAR, 2, 2, 1951 )
	        .appendValue( LunarMonthField.MONTH_N, 2 )
	        .appendValue( LunarMonthField.DAY_IN_LMONTH_BUNDLE, 2 )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * <ul>
	 * <li>2004년 2월 10일 --> 2004/2/10
	 * <li>2004년 윤2월 10일 --> 2004/윤2/10
	 * </ul>
	 */
	public static final DateTimeFormatter SLASHED_DATE = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendLiteral( "/" )
	        .appendText( LunarMonthField.MONTH_LEAP, Map.of( 0L, "", 1L, "윤" ) )
	        .appendValue( LunarMonthField.MONTH_N )
	        .appendLiteral( "/" )
	        .appendValue( ChronoField.DAY_OF_MONTH )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * <ul>
	 * <li>2004년 2월 10일 --> 2004/2/10
	 * <li>2004년 윤2월 10일 --> 2004/2L/10
	 * </ul>
	 */
	public static final DateTimeFormatter SLASHED_DATE_L = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendLiteral( "/" )
	        .appendValue( LunarMonthField.MONTH_N )
	        .appendText( LunarMonthField.MONTH_LEAP, Map.of( 0L, "", 1L, "L" ) )
	        .appendLiteral( "/" )
	        .appendValue( ChronoField.DAY_OF_MONTH )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * <ul>
	 * <li>2004년 2월 10일 --> 2004.2.10.
	 * <li>2004년 윤2월 10일 --> 2004.윤2.10.
	 * </ul>
	 */
	public static final DateTimeFormatter DOTTED_DATE = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendLiteral( "." )
	        .appendText( LunarMonthField.MONTH_LEAP, Map.of( 0L, "", 1L, "윤" ) )
	        .appendValue( LunarMonthField.MONTH_N )
	        .appendLiteral( "." )
	        .appendValue( ChronoField.DAY_OF_MONTH )
	        .appendLiteral( "." )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	/**
	 * <ul>
	 * <li>2004년 2월 10일 --> 2004.2.10.
	 * <li>2004년 윤2월 10일 --> 2004.2L.10.
	 * </ul>
	 */
	public static final DateTimeFormatter DOTTED_DATE_L = new DateTimeFormatterBuilder()
	        .appendValue( ChronoField.YEAR )
	        .appendLiteral( "." )
	        .appendValue( LunarMonthField.MONTH_N )
	        .appendText( LunarMonthField.MONTH_LEAP, Map.of( 0L, "", 1L, "L" ) )
	        .appendLiteral( "." )
	        .appendValue( ChronoField.DAY_OF_MONTH )
	        .appendLiteral( "." )
	        .toFormatter()
	        .withChronology( KLunarChronology.INSTANCE );

	// XXX time:
}
