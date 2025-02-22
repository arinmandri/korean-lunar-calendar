package xyz.arinmandri;

import java.util.List;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;
import xyz.arinmandri.koreanlunarcalendar.KLunarDate;


public class TestTest
{
	ApiService api = ApiService.getInstance();

	final int YEAR_BASE = 1864;// KLunarDate.YEAR_BASE
	final int year_min = YEAR_BASE;
	final int year_max = YEAR_BASE + KLunarDate.CYCLE_SIZE * 10;

//	@Test
	public void test () {
	}

//	@Test
	public void justOneApi () {
		List<Item> items = api.getFromLunDate( 2050, 11, 18 );
		for( Item item : items ){
			System.out.println( item );
		}
	}

//	@Test
	public void printYd () {
		int yd = 0x11126DA9;
		System.out.println( "적일: " + ( yd >>> 17 ) );
		System.out.println( "윤달위치 " + ( ( yd >>> 13 ) & 0xF ) );
		System.out.println( "각월대소 " + Integer.toBinaryString( yd & 0x1FFF ) );
	}

	//// ================================ util, private, etc

	private String binaryYd ( int yd ) {
		String b0 = Integer.toBinaryString( yd );
		while( b0.length() < 32 ){
			b0 = "0" + b0;
		}
		return "0b" + b0.substring( 0, 15 ) + '_' + b0.substring( 15, 19 ) + '_' + b0.substring( 19, 32 );
	}

	private String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}
}
