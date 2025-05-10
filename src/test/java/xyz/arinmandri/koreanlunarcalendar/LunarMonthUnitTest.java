package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class LunarMonthUnitTest extends KLunarDateTest
{

	@Test
	public void between_LMONTH () {
		printTitle( "LunarMonthUnit.LMONTHS.bewteen" );
		LunarMonthUnit u = LunarMonthUnit.LMONTHS;
		KLunarDate kd1, kd2;

		kd1 = KLunarDate.of( 2000, 1, 1 );
		kd2 = KLunarDate.of( 2000, 2, 1 );
		assertEquals( +1, u.between( kd1, kd2 ) );
		assertEquals( -1, u.between( kd2, kd1 ) );
		kd1 = KLunarDate.of( 2000, 1, 2 );
		kd2 = KLunarDate.of( 2000, 2, 1 );
		assertEquals( 0, u.between( kd1, kd2 ) );
		assertEquals( 0, u.between( kd2, kd1 ) );

		kd1 = KLunarDate.of( 2000, 1, 1 );
		kd2 = KLunarDate.of( 2000, 12, 1 );
		assertEquals( +11, u.between( kd1, kd2 ) );
		assertEquals( -11, u.between( kd2, kd1 ) );
		kd1 = KLunarDate.of( 2000, 1, 10 );
		kd2 = KLunarDate.of( 2000, 12, 9 );
		assertEquals( +10, u.between( kd1, kd2 ) );
		assertEquals( -10, u.between( kd2, kd1 ) );

		kd1 = KLunarDate.of( 2000, 1, 1 );
		kd2 = KLunarDate.of( 2001, 1, 1 );
		assertEquals( +12, u.between( kd1, kd2 ) );
		assertEquals( -12, u.between( kd2, kd1 ) );
		kd1 = KLunarDate.of( 2000, 12, 1 );
		kd2 = KLunarDate.of( 2001, 1, 1 );
		assertEquals( +1, u.between( kd1, kd2 ) );
		assertEquals( -1, u.between( kd2, kd1 ) );
		kd1 = KLunarDate.of( 2000, 12, 2 );
		kd2 = KLunarDate.of( 2001, 1, 1 );
		assertEquals( 0, u.between( kd1, kd2 ) );
		assertEquals( 0, u.between( kd2, kd1 ) );

		kd1 = KLunarDate.of( 2030, 1, 1 );
		kd2 = KLunarDate.of( 2050, 1, 1 );
		assertEquals( +( 20 * 12 + 7 ), u.between( kd1, kd2 ) );
		assertEquals( -( 20 * 12 + 7 ), u.between( kd2, kd1 ) );

		kd1 = KLunarDate.of( 1406, 1, 1 );
		kd2 = KLunarDate.of( 1425, 1, 1 );
		assertEquals( +( ( 1425 - 1406 ) * 12 + 7 ), u.between( kd1, kd2 ) );
		assertEquals( -( ( 1425 - 1406 ) * 12 + 7 ), u.between( kd2, kd1 ) );

		kd1 = KLunarDate.of( 1406, 1, 1 );
		kd2 = KLunarDate.of( 2033, 1, 1 );
		assertEquals( +( ( 2033 - 1406 ) * 12 + 33 * 7 ), u.between( kd1, kd2 ) );
		assertEquals( -( ( 2033 - 1406 ) * 12 + 33 * 7 ), u.between( kd2, kd1 ) );
		kd1 = KLunarDate.of( 1406, 12, 10 );
		kd2 = KLunarDate.of( 2033, 1, 1 );
		assertEquals( +( ( 2033 - 1406 ) * 12 + 33 * 7 - 13 ), u.between( kd1, kd2 ) );
		assertEquals( -( ( 2033 - 1406 ) * 12 + 33 * 7 - 13 ), u.between( kd2, kd1 ) );
	}

	@Test
	public void addTo_LMONTHS () {
		repeat( ()-> {
			KLunarDate kd1 = getRaondomKd().withDay( 1 );
			KLunarDate kd2 = getRaondomKd().withDay( 1 );
			long diff = 0;

			try{
				diff = LunarMonthUnit.LMONTHS.between( kd1, kd2 );
				assertEquals( kd2, LunarMonthUnit.LMONTHS.addTo( kd1, diff ) );
			}
			catch( Throwable e ){
				System.out.println( "=DOOM=" );
				System.out.println( "diff: " + diff );
				System.out.println( "kd1:  " + kd1 );
				System.out.println( "kd2:  " + kd2 );
				System.out.println( "kd1+: " + LunarMonthUnit.LMONTHS.addTo( kd1, diff ) );
				throw e;
			}
		}, "LunarMonthUnit.LMONTHS.addTo" );
	}

	@Test
	public void addTo_LMONTH_BUNDLES () {
		repeat( ()-> {
			KLunarDate kd1 = getRaondomKd().withDay( 1 );
			KLunarDate kd2 = getRaondomKd().withDay( 1 );
			long diff = 0;

			try{
				diff = LunarMonthUnit.LMONTH_BUNDLES.between( kd1, kd2 );
				assertEquals( kd2, LunarMonthUnit.LMONTH_BUNDLES.addTo( kd1, diff ).withMonthLeap( kd2.isLeapMonth() ) );
			}
			catch( Throwable e ){
				System.out.println( "=DOOM=" );
				System.out.println( "diff: " + diff );
				System.out.println( "kd1:  " + kd1 );
				System.out.println( "kd2:  " + kd2 );
				System.out.println( "kd1+: " + LunarMonthUnit.LMONTH_BUNDLES.addTo( kd1, diff ).withMonthLeap( kd2.isLeapMonth() ) );
				throw e;
			}
		}, "LunarMonthUnit.LMONTH_BUNDLES.addTo" );
	}

	// TODO 안 되는 경우
}
