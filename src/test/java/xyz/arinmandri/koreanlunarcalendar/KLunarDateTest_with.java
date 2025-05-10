package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_with extends KLunarDateTest
{
	@Test
	public void testWithYear () {
		KLunarDate kd0, kd1;

		kd0 = KLunarDate.of( YEAR_MIN, 1, false, 1 );
		kd1 = kd0.withYear( YEAR_MAX );
		checkValue( kd1, YEAR_MAX, 1, false, 1 );

		kd0 = KLunarDate.of( 2004, 2, true, 1 );
		kd1 = kd0.withYear( 2005 );
		checkValue( kd1, 2005, 2, false, 1 );

		kd0 = KLunarDate.of( 2010, 12, false, 30 );
		kd1 = kd0.withYear( 2009 );
		checkValue( kd1, 2009, 12, false, 30 );
		kd1 = kd0.withYear( 2005 );
		checkValue( kd1, 2005, 12, false, 29 );
	}// TODO 안 되는 경우

	@Test
	public void testWithMonth () {
		KLunarDate kd0, kd1;

		kd0 = KLunarDate.of( 2004, 2, true, 11 );
		for( int m = 1 ; m <= 12 ; m++ ){
			if( m != 2 ){
				kd1 = kd0.withMonth( m );
				checkValue( kd1, 2004, m, false, 11 );
			}
			else{
				kd1 = kd0.withMonth( m );
				checkValue( kd1, 2004, m, true, 11 );
			}
		}
	}// TODO 안 되는 경우

	@Test
	public void testWithDay () {
		KLunarDate kd0, kd1;

		kd0 = KLunarDate.of( 2004, 12, false, 1 );
		for( int d = 1 ; d <= 29 ; d++ ){
			kd1 = kd0.withDay( d );
			checkValue( kd1, 2004, 12, false, d );
		}
		kd0 = KLunarDate.of( 2020, 12, false, 1 );
		for( int d = 1 ; d <= 30 ; d++ ){
			kd1 = kd0.withDay( d );
			checkValue( kd1, 2020, 12, false, d );
		}
	}// TODO 안 되는 경우

	@Test
	public void testWithSecha () {
		KLunarDate kd;

		// TODO 이거 걍 단순한
		kd = KLunarDate.of( 2004, 12, false, 1 );
		kd = kd.withSecha( Ganji.O11 );
		assertEquals( Ganji.O11, kd.getSecha() );
		kd = kd.withSecha( Ganji.O7 );
		assertEquals( Ganji.O7, kd.getSecha() );
		kd = kd.withSecha( Ganji.O1 );
		assertEquals( Ganji.O1, kd.getSecha() );
		kd = kd.withSecha( Ganji.O60 );
		assertEquals( Ganji.O60, kd.getSecha() );
	}

	// TODO with ganji
}
