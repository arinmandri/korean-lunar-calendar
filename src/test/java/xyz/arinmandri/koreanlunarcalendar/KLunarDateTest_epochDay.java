package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_epochDay extends ATest
{

	@Test
	public void test () {

		repeat( this::testOne_eke, "epoch day - eke" );
		repeat( this::testOne_ekle, "epoch day - ekle" );
		repeat( this::testOne_elke, "epoch day - elke" );
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

	@Test
	public void testOne_boundary () {
		printTitle( "epoch day - boundary" );
		KLunarDate kd;

		kd = KLunarDate.ofEpochDay( EPOCHDAY_MIN );
		kd = KLunarDate.ofEpochDay( EPOCHDAY_MAX );

		assertThrowsExactly( OutOfRangeException.class, ()-> {
			KLunarDate kd1 = KLunarDate.ofEpochDay( EPOCHDAY_MIN - 1 );
		} );
		assertThrowsExactly( OutOfRangeException.class, ()-> {
			KLunarDate kd1 = KLunarDate.ofEpochDay( EPOCHDAY_MIN - 2 );
		} );
		assertThrowsExactly( OutOfRangeException.class, ()-> {
			KLunarDate kd1 = KLunarDate.ofEpochDay( EPOCHDAY_MAX + 1 );
		} );
		assertThrowsExactly( OutOfRangeException.class, ()-> {
			KLunarDate kd1 = KLunarDate.ofEpochDay( EPOCHDAY_MAX + 2 );
		} );
	}
}
