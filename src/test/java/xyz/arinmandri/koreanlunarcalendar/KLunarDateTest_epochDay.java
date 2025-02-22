package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_epochDay extends KLunarDateTest
{

	@Test
	public void test () {
		System.out.println( "=== epoch day ===" );

		for( int i = 0 ; i < testSize ; i += 1 ){// XXX 그러고보니 그냥 이 반복이랑 10째 어쩌고 출력도 묶어내면 되지 않나
			testOne_eke();
			testOne_ekle();
			testOne_elke();

			if( i % 10 == 9 ){
				System.out.println( i + 1 + "째 시험 통과" );
			}
		}
	}

	private void testOne_eke () {
		/*
		 * epoch day --> KLunarDate --> epoch day
		 */
		long e0 = getRandomEpochDay();
		KLunarDate k = KLunarDate.ofEpochDay( e0 );
		long e1 = k.toEpochDay();

		assertEquals( e0, e1 );
	}

	private void testOne_ekle () {
		/*
		 * epoch day --> KLunarDate --> LocalDate --> epoch day
		 */
		long e0 = getRandomEpochDay();
		KLunarDate k = KLunarDate.ofEpochDay( e0 );
		LocalDate l = k.toLocalDate();
		long e1 = l.toEpochDay();

		assertEquals( e0, e1 );
	}

	private void testOne_elke () {
		/*
		 * epoch day --> LocalDate --> KLunarDate --> epoch day
		 */
		long e0 = getRandomEpochDay();
		LocalDate l = LocalDate.ofEpochDay( e0 );
		KLunarDate k = KLunarDate.from( l );
		long e1 = k.toEpochDay();

		assertEquals( e0, e1 );
	}

	private int getRandomEpochDay () {
		return getRandomInt( EPOCH_DAY_MIN, EPOCH_DAY_MAX );
	}

}
