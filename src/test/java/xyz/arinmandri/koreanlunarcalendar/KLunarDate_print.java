package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import org.junit.jupiter.api.Test;


public class KLunarDate_print extends KLunarDateTest
{
	/**
	 * 윤달들 확인
	 */
	@Test
	public void printLeapMonths () {
		System.out.println( "=== 윤달 목록 ===" );
		for( int c = 0 ; c < ydss.length ; c++ ){
			for( int y0 = 0 ; y0 < ydss[c].length ; y0++ ){
				int yd = ydss[c][y0];
				int year = YEAR_MIN + c * CYCLE_SIZE + y0;
				int leapMonth = ( yd >>> 13 ) & 0xF;
				if( leapMonth != 0xF ){
					System.out.println( year + "-" + leapMonth );
				}
			}
		}
	}
}
