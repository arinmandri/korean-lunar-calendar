package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;


public class KLunarDate_print extends ATest
{

//	@org.junit.jupiter.api.Test
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

//	@org.junit.jupiter.api.Test
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

//	@org.junit.jupiter.api.Test
	public void printCycleLengths () {
		printTitle( "1갑자 길이" );

		for( int y = YEAR_MIN ; y <= YEAR_MAX - CYCLE_SIZE ; y += 1 ){
			KLunarDate kd0 = KLunarDate.of( y, 1, 1 );
			KLunarDate kd1 = KLunarDate.of( y + CYCLE_SIZE, 1, 1 );
			int epochDay0 = kd0.toEpochDayInt();
			int epochDay1 = kd1.toEpochDayInt();
			int yl = epochDay1 - epochDay0;
			System.out.println( y + " - " + yl );
			epochDay0 = epochDay1;
		}
	}
}

/*
윤년의 분포. 19년마다 7개라더니 적어도 지원범위 내에서는 진짜 딱 떨어짐.
PROLEPTIC_MONTH도 이걸로 추정해버리련다.
19년 윤달7개; 총 19*12+7=235개월
0년부터 19*73-1=1386년까지 총 235*73=17155개월
그 뒤 1387년 13, 1388년 12, 1388년 12, 1390년 13개월 있다 치면 1391년 전까지 총 17205개월
    -O--O--O--O-O--// 1387(=19*73)
O--O-O--O--O--O-O--
O--O-O--O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O-O--O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O-O--
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O-O--O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O-
O--O--O-O--O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O-
O--O--O--O-O--O--O

*/
