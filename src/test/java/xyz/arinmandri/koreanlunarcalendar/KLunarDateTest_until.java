package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_until extends KLunarDateTest
{

	@Test
	public void until2 () {
		repeat( this::until2_year, "until year" );
		repeat( this::until2_month, "until month" );
		repeat( this::until2_day, "until day" );
	}

	private void until2_year () {
		int m = getRandomInt( 1, 12 );
		int d = getRandomInt( 1, 29 );
		KLunarDate kd1 = getRaondomKd().withMonth( m ).withMonthLeap( false ).withDay( d );
		KLunarDate kd2 = getRaondomKd().withMonth( m ).withMonthLeap( false ).withDay( d );
		Long p = null;

		try{
			p = kd1.until( kd2, ChronoUnit.YEARS );
			assertEquals( kd2, kd1.plus( p, ChronoUnit.YEARS ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM" );
			System.out.println( kd1 );
			System.out.println( kd2 );
			System.out.println( p );
			throw e;
		}
	}

	private void until2_month () {
		int d = getRandomInt( 1, 29 );
		KLunarDate kd1 = getRaondomKd().withDay( d );
		KLunarDate kd2 = getRaondomKd().withDay( d );
		Long p = null;

		try{
			p = kd1.until( kd2, ChronoUnit.MONTHS );
			assertEquals( kd2, kd1.plus( p, ChronoUnit.MONTHS ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM" );
			System.out.println( kd1 );
			System.out.println( kd2 );
			System.out.println( p );
			throw e;
		}
	}

	private void until2_day () {
		KLunarDate kd1 = getRaondomKd();
		KLunarDate kd2 = getRaondomKd();
		Long p = null;

		try{
			p = kd1.until( kd2, ChronoUnit.DAYS );
			assertEquals( kd2, kd1.plus( p, ChronoUnit.DAYS ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM" );
			System.out.println( kd1 );
			System.out.println( kd2 );
			System.out.println( p );
			throw e;
		}
	}

	@Test
	public void until1 () {
		repeat( this::until1_period0_one, "until period - period 0" );
		repeat( this::plusPeriod, "until period - add period" );
	}

	private void until1_period0_one () {
		KLunarDate kd = getRaondomKd();
		ChronoPeriod p = kd.until( kd );
		assertTrue( p.isZero() );
	}

	private void plusPeriod () {
		KLunarDate kd1 = getRaondomKd();
		KLunarDate kd2 = getRaondomKd();
		KLunarPeriod p = null;
		try{
			p = kd1.until( kd2 );
			assertEquals( kd2, kd1.plus( p ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM" );
			System.out.println( kd1 );
			System.out.println( kd2 );
			System.out.println( p );
			throw e;
		}
	}

	@Test
	public void testOfYearDay () {
		printTitle( "until year of day" );

		for( int y = YEAR_MIN ; y <= YEAR_MAX ; y++ ){
			KLunarDate kd0 = KLunarDate.of( y, 1, 1 );
			KLunarDate kd1 = KLunarDate.ofYearDay( y, 1 );
			assertEquals( kd1, kd0 );

			final int yearLength = kd0.lengthOfYear();

			for( int yd = 2 ; yd <= yearLength ; yd++ ){
				kd1 = KLunarDate.ofYearDay( y, yd );
				int diff = Math.toIntExact( kd0.until( kd1, ChronoUnit.DAYS ) );
				assertEquals( yd - 1, diff );
			}
		}
		System.out.println( "GOOD" );
	}
}
