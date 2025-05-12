package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_prolepticMonth extends KLunarDateTest
{
	@Test
	public void range () {
		printTitle( "proleptic month - range" );
		int min = KLunarDate.of( YEAR_MIN, 1, false, 1 ).getProlepticMonth();
		assertEquals( min, KLunarDate.PROLEPTIC_MONTH_MIN );

		int max;
		{
			try{
				max = KLunarDate.of( YEAR_MAX, 12, true, 1 ).getProlepticMonth();
			}
			catch( NonexistentDateException e ){
				max = KLunarDate.of( YEAR_MAX, 12, false, 1 ).getProlepticMonth();
			}
		}
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
}
