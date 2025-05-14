package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_prolepticMonth extends ATest
{
	@Test
	public void range () {
		printTitle( "proleptic month - range" );
		int min = MIN.getProlepticMonth();
		assertEquals( min, KLunarDate.PROLEPTIC_MONTH_MIN );

		int max = MAX.getProlepticMonth();
		assertEquals( max, KLunarDate.PROLEPTIC_MONTH_MAX );
	}

	@Test
	public void between () {
		repeat( this::between1, "proleptic month - between" );
	}

	private void between1 () {
		KLunarDate kd1 = getRaondomKd().withDay( 11 );
		KLunarDate kd2 = getRaondomKd().withDay( 11 );

		long diff1 = kd1.until( kd2, ChronoUnit.MONTHS );
		long diff2 = kd2.getProlepticMonth() - kd1.getProlepticMonth();

		try{
			assertEquals( diff1, diff2 );
		}
		catch( Throwable e ){
			System.out.println( "DOOM" );
			System.out.println( kd1 );
			System.out.println( kd2 );
			System.out.println( diff1 );
			System.out.println( diff2 );
			throw e;
		}
	}

	@Test
	public void nextMonth () {
		printTitle( "nextMonth till end" );

		int n = PROLEPTIC_MONTH_MAX - PROLEPTIC_MONTH_MIN;
		KLunarDate kd1 = MIN;
		KLunarDate kd2 = MAX.withDay( 1 );
		for( int i = 0 ; i < n ; i++ ){
			kd1 = kd1.nextMonth();
		}
		assertEquals( kd2, kd1 );
	}

	@Test
	public void prevMonth () {
		printTitle( "prevMonth till end" );

		int n = PROLEPTIC_MONTH_MAX - PROLEPTIC_MONTH_MIN;
		KLunarDate kd1 = MIN;
		KLunarDate kd2 = MAX.withDay( 1 );
		for( int i = 0 ; i < n ; i++ ){
			kd2 = kd2.prevMonth();
		}
		assertEquals( kd1, kd2 );
	}

	@Test
	public void boundary () {
		printTitle( "boundary months" );

		KLunarDate kd1 = MIN;
		KLunarDate kd2 = MAX.withDay( 1 );

		assertThrowsExactly( OutOfRangeException.class, ()-> {
			kd1.prevMonth();
		} );
		assertThrowsExactly( OutOfRangeException.class, ()-> {
			kd2.nextMonth();
		} );
	}
}
