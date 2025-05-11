package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest_ganji extends KLunarDateTest
{
	@Test
	public void valueOfGanJi () {
		for( int i = 0 ; i < Ganji.values().length ; i++ ){
			Ganji gj = Ganji.values()[i];
			Ganji.Gan g = gj.gan;
			Ganji.Ji j = gj.ji;
			Ganji gj1 = Ganji.valueOf( g, j );
			assertEquals( gj, gj1 );
		}
		try{
			Ganji aa = Ganji.valueOf( Ganji.Gan.A, Ganji.Ji._2 );

		}
		catch( Throwable e ){
			System.out.println( e );
		}
	}

//	@Test
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
