package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_with extends KLunarDateTest
{
	@Test
	public void testWithYear () {
		printTitle( "with year" );
		KLunarDate kd0, kd1;

		kd0 = KLunarDate.of( YEAR_MIN, 1, false, 1 );
		kd1 = kd0.withYear( YEAR_MAX );
		assertEquals( kd1, KLunarDate.of( YEAR_MAX, 1, false, 1 ) );

		kd0 = KLunarDate.of( 2004, 2, true, 1 );
		kd1 = kd0.withYear( 2005 );
		assertEquals( kd1, KLunarDate.of( 2005, 2, false, 1 ) );

		kd0 = KLunarDate.of( 2010, 12, false, 30 );
		kd1 = kd0.withYear( 2009 );
		assertEquals( kd1, KLunarDate.of( 2009, 12, false, 30 ) );
		kd1 = kd0.withYear( 2005 );
		assertEquals( kd1, KLunarDate.of( 2005, 12, false, 29 ) );

		repeatShortly( this::testWithYear_1 );
	}

	private void testWithYear_1 () {
		KLunarDate kd0 = null;
		ValueRange r = null;
		int i = -9999;
		try{
			kd0 = getRaondomKd().withMonthLeap( false );
			r = kd0.range( ChronoField.YEAR );
			int min = Math.toIntExact( r.getMinimum() );
			int max = Math.toIntExact( r.getMaximum() );
			for( i = min ; i < max ; i++ ){
				KLunarDate kd1 = kd0.withYear( i );
				assertEquals( kd1,
				        KLunarDate.of(
				                i,
				                kd0.getMonth(),
				                kd0.isLeapMonth(),
				                kd1.isBigMonth() ? kd0.getDay() : Math.min( kd0.getDay(), KLunarDate.LIL_MONTH_SIZE ) ) );
			}
		}
		catch( Throwable e ){
			System.out.println( "=DOOM1=" );
			System.out.println( kd0 );
			System.out.println( r );
			System.out.println( i );
			throw e;
		}
	}

	@Test
	public void testWithMonth () {
		printTitle( "with month" );
		KLunarDate kd0, kd1;

		kd0 = KLunarDate.of( 2004, 2, true, 11 );
		for( int m = 1 ; m <= 12 ; m++ ){
			if( m != 2 ){
				kd1 = kd0.withMonth( m );
				assertEquals( kd1, KLunarDate.of( 2004, m, false, 11 ) );
			}
			else{
				kd1 = kd0.withMonth( m );
				assertEquals( kd1, KLunarDate.of( 2004, m, true, 11 ) );
			}
		}

		repeat( this::testWithMonth_1 );
	}

	private void testWithMonth_1 () {
		final KLunarDate kd0 = getRaondomKd().withMonthLeap( false );
		ValueRange r = null;
		int i = -9999;
		try{
			r = kd0.range( LunarMonthField.MONTH_N );
			int min = Math.toIntExact( r.getMinimum() );
			int max = Math.toIntExact( r.getMaximum() );
			for( i = min ; i < max ; i++ ){
				KLunarDate kd1 = kd0.withMonth( i );
				assertEquals( kd1,
				        KLunarDate.of(
				                kd0.getYear(),
				                i,
				                kd0.isLeapMonth(),
				                kd1.isBigMonth() ? kd0.getDay() : Math.min( kd0.getDay(), KLunarDate.LIL_MONTH_SIZE ) ) );
			}

			assertThrowsExactly( NonexistentDateException.class, ()-> {
				kd0.withMonth( min - 1 );
			} );
			assertThrowsExactly( NonexistentDateException.class, ()-> {
				kd0.withMonth( max + 1 );
			} );
		}
		catch( Throwable e ){
			System.out.println( "=DOOM2=" );
			System.out.println( kd0 );
			System.out.println( r );
			System.out.println( i );
			throw e;
		}
	}

	@Test
	public void testWithLeapMonth () {
		printTitle( "with leapMonth" );
		/*
		 * 모든 년월에 대해 윤달이 있으면 withMonthLeap로 윤달을 만들어보고
		 * 윤달이 없는 달이면 윤달로 바꿨을 때 에러가 나는지 확인
		 */
		int y_min = YEAR_MIN;
		int y_max = YEAR_MAX;
		ValueRange mr = LunarMonthField.MONTH_N.range();
		int m_min = Math.toIntExact( mr.getMinimum() );
		int m_max = Math.toIntExact( mr.getMaximum() );
		int y = -9999, m = -9999;

		try{
			for( y = y_min ; y <= y_max ; y += 1 ){
				for( m = m_min ; m < m_max ; m += 1 ){
					boolean isLeapMonth = KLunarChronology.INSTANCE.isLeapMonth( y, m );
					if( isLeapMonth ){
						KLunarDate kd = KLunarDate.of( y, m, 1 );
						assertEquals(
						        KLunarDate.of( y, m, true, 1 ),
						        kd.withMonthLeap( true ) );
						assertEquals(
						        KLunarDate.of( y, m, false, 1 ),
						        kd.withMonthLeap( true ).withMonthLeap( false ) );
					}
					else{
						final int yy = y;
						final int mm = m;
						assertThrowsExactly( NonexistentDateException.class, ()-> {
							KLunarDate.of( yy, mm, 1 ).withMonthLeap( true );
						} );
					}
				}
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: " + e.getMessage() );
			System.out.println( y + "-" + m );
			throw e;
		}
	}

	@Test
	public void testWithDay () {
		printTitle( "with day" );
		KLunarDate kd0, kd1;

		kd0 = KLunarDate.of( 2004, 12, false, 1 );
		for( int d = 1 ; d <= 29 ; d++ ){
			kd1 = kd0.withDay( d );
			assertEquals( kd1, KLunarDate.of( 2004, 12, false, d ) );
		}
		kd0 = KLunarDate.of( 2020, 12, false, 1 );
		for( int d = 1 ; d <= 30 ; d++ ){
			kd1 = kd0.withDay( d );
			assertEquals( kd1, KLunarDate.of( 2020, 12, false, d ) );
		}

		repeat( this::testWithDay_1 );
	}

	private void testWithDay_1 () {
		final KLunarDate kd0 = getRaondomKd();;
		ValueRange r = null;
		int i = -9999;
		try{
			r = kd0.range( ChronoField.DAY_OF_MONTH );
			int min = Math.toIntExact( r.getMinimum() );
			int max = Math.toIntExact( r.getMaximum() );
			for( i = min ; i <= max ; i++ ){
				assertEquals( kd0.withDay( i ),
				        KLunarDate.of(
				                kd0.getYear(),
				                kd0.getMonth(),
				                kd0.isLeapMonth(),
				                i ) );
			}

			assertThrowsExactly( NonexistentDateException.class, ()-> {
				kd0.withDay( min - 1 );
			} );
			assertThrowsExactly( NonexistentDateException.class, ()-> {
				kd0.withDay( max + 1 );
			} );
		}
		catch( Throwable e ){
			System.out.println( "=DOOM3=" );
			System.out.println( kd0 );
			System.out.println( r );
			System.out.println( i );
			throw e;
		}
	}

	@Test
	public void testWithSecha () {
		printTitle( "with ganji year" );
		KLunarDate kd;

		kd = KLunarDate.of( 2004, 12, false, 1 );
		kd = kd.withSecha( Ganji.A11 );
		assertEquals( Ganji.A11, kd.getSecha() );
		kd = kd.withSecha( Ganji.G7 );
		assertEquals( Ganji.G7, kd.getSecha() );
		kd = kd.withSecha( Ganji.A1 );
		assertEquals( Ganji.A1, kd.getSecha() );
		kd = kd.withSecha( Ganji.J12 );
		assertEquals( Ganji.J12, kd.getSecha() );

		repeat( this::testWithSecha_1 );
	}

	public void testWithSecha_1 () {

	}

	// TODO with ganji
}
