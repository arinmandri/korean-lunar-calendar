package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest_ganji extends KLunarDateTest
{

	@Test
	public void test () {

		repeatShortly( this::testOne, "ganji" );
	}

	private void testOne () {

		final int epochDay = getRandomEpochDay_kasi();
		final int jDay = 2440588 + epochDay;

		KLunarDate kd = KLunarDate.ofEpochDay( epochDay );
		Item item = api.getFromJDay( jDay );

		try{
			checkGanji( kd.getSecha(), item.getSecha() );
		}
		catch( Exception e ){
			System.out.println( "y err date: " + kd );
			throw e;
		}
		try{
			checkGanji( kd.getWolgeon(), item.getWolgeon() );
		}
		catch( Exception e ){
			System.out.println( "m err date: " + kd );
			throw e;
		}
		try{
			checkGanji( kd.getIljin(), item.getIljin() );
		}
		catch( Exception e ){
			System.out.println( "d err date: " + kd );
			throw e;
		}
	}

	private void checkGanji ( Ganji ganji , String GanjiStr ) {
		if( ganji == null ){
			assertEquals( null, GanjiStr );
			return;
		}

		assertEquals( ganji.toString(), GanjiStr );
	}
}
