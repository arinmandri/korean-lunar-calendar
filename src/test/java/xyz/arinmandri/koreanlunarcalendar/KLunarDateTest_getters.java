package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_getters extends KLunarDateTest
{
	final int[] lengthOfYears = { 354, 384, 355, 354, 383, 354, 384, 355, 355, 384, 354, 354, 384, 354, 354, 384, 355, 384, 355, 354, 384, 354, 354, 384, 354, 355, 384, 355, 384, 354, 354, 383, 355, 354, 384, 355, 384, 354, 355, 383, 354, 355, 384, 354, 355, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 384, 354, 354, 384, 354, 354, 385, 354, 355, 384, 354, 383, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 354, 383, 355, 384, 354, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 385, 354, 355, 384, 354, 354, 383, 355, 384, 355, 354, 384, 354, 354, 384, 354, 355, 384, 354, 385, 354, 354, 384, 354, 354, 384, 355, 384, 354, 355, 384, 354, 354, 384, 354, 355, 384, 354, 384, 355, 354, 383, 355, 354, 384, 355, 384, 354, 354, 384, 354, 354, 384, 355, 355, 384, 354, 384, 354, 354, 384, 354, 355, };

	@Test
	public void test () {
		repeat( this::getYearMonthDay, "getYear getMont gethDay" );
	}

	public void getYearMonthDay () {
		int y0 = getRandomInt( YEAR_MIN, YEAR_MAX );
		int m0 = getRandomInt( 1, 12 );
		int d0 = getRandomInt( 1, 30 );

		KLunarDate kd;
		try{
			kd = KLunarDate.of( y0, m0, d0 );
		}
		catch( NonexistentDateException e ){
			throw new NoNeedToTest();
		}

		int y1 = kd.getYear();
		int m1 = kd.getMonth();
		int d1 = kd.getDay();

		assertEquals( y0, y1 );
		assertEquals( m0, m1 );
		assertEquals( d0, d1 );
	}

	@Test
	public void lengthOfYear () {
		printTitle( "lengthOfYear" );

		final int tesetUntil = 2050;

		if( lengthOfYears.length != tesetUntil - YEAR_MIN ){
			fail( "lengthOfYears 개수가 맞지 않음." );
		}

		for( int y = YEAR_MIN ; y < tesetUntil ; y++ ){
			KLunarDate kd0 = KLunarDate.of( y, 1, 1 );
			KLunarDate kd1 = KLunarDate.of( y, 12, 1 );
			int lengthOfYear = lengthOfYears[y - YEAR_MIN];

			assertEquals( lengthOfYear, kd0.lengthOfYear() );
			assertEquals( lengthOfYear, kd1.lengthOfYear() );
		}
	}
}
