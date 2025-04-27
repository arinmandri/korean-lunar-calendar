package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import org.junit.jupiter.api.Test;


public class KLunarDate_print extends KLunarDateTest
{

	@Test
	public void printLeapMonths () {
		printTitle( "윤달 목록" );
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

	@Test
	public void printYearLengths () {
		printTitle( "1년 길이" );

		KLunarDate kd = KLunarDate.of( YEAR_MIN, 1, 1 );
		int epochDay0 = kd.toEpochDayInt();
		for( int y = YEAR_MIN + 1 ; y <= YEAR_MAX ; y++ ){
			kd = KLunarDate.of( y, 1, 1 );
			int epochDay1 = kd.toEpochDayInt();
			int yl = epochDay1 - epochDay0;
			System.out.println( ( y - 1 ) + " - " + yl );
			epochDay0 = epochDay1;
		}
	}

	@Test
	public void printCycleLengths () {
		printTitle( "1갑자 길이" );

		KLunarDate kd = KLunarDate.of( YEAR_MIN, 1, 1 );
		int epochDay0 = kd.toEpochDayInt();
		for( int y = YEAR_MIN + CYCLE_SIZE ; y <= YEAR_MAX ; y += CYCLE_SIZE ){
			kd = KLunarDate.of( y, 1, 1 );
			int epochDay1 = kd.toEpochDayInt();
			int yl = epochDay1 - epochDay0;
			System.out.println( ( y - CYCLE_SIZE ) + " - " + yl );
			epochDay0 = epochDay1;
		}
	}
}
