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
		final int jDay = EPOCH_0_JDAY + epochDay;

		KLunarDate kd = KLunarDate.ofEpochDay( epochDay );
		Item item = api.getFromJDay( jDay );

		checkGanji( kd.getSecha(), item.getSecha() );
		checkGanji( kd.getWolgeon(), item.getWolgeon() );
		checkGanji( kd.getIljin(), item.getIljin() );
	}

	private void checkGanji ( Ganji ganji , String GanjiStr ) {
		if( ganji == null ){
			assertEquals( null, GanjiStr );
			return;
		}

		assertEquals( ganji.toString(), GanjiStr );
	}
}
