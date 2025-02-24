package xyz.arinmandri;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;


public class TestTest
{
	ApiService api = ApiService.getInstance();

	@Test
	public void test () {
		LocalDate ld = LocalDate.now();
		LocalDateTime ldt = LocalDateTime.now();
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
