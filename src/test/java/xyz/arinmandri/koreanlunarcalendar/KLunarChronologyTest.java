package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.chrono.Chronology;
import java.time.temporal.ValueRange;

import org.junit.jupiter.api.Test;


public class KLunarChronologyTest extends ATest
{
	@Test
	public void chronologyOf () {
		printTitle( "Chronology.of" );
		Chronology c1 = Chronology.of( "KoreanLunar" );// src/main/resources/META-INF/services/java.time.chrono.Chronology
		System.out.println( c1 );
		assertEquals( KLunarChronology.INSTANCE, c1 );
		Chronology c2 = Chronology.of( "KoreanLunar" );// src/main/resources/META-INF/services/java.time.chrono.Chronology
		assertEquals( c1, c2 );
	}

	@Test
	public void testIsLeapYear () {
		printTitle( "KLunarChronology.isLeapYear" );
		int min = YEAR_MIN - 10;
		int max = YEAR_MAX + 10;

		for( int y = min ; y <= max ; y += 1 ){
			boolean isLeapYear = KLunarChronology.INSTANCE.isLeapYear( y );
			try{
				if( y < YEAR_MIN )
				    assertFalse( isLeapYear );
				else if( y > YEAR_MAX )
				    assertFalse( isLeapYear );
				else{
					KLunarDate kd = KLunarDate.of( y, 1, 1 );
					assertEquals( kd.isLeapYear(), isLeapYear );
				}
			}
			catch( Throwable e ){
				System.out.println( "DOOM: " + y + " - " + isLeapYear );
				throw e;
			}
		}
	}

	@Test
	public void testIsLeapMonth () {
		printTitle( "KLunarChronology.isLeapMonth" );
		int y_min = YEAR_MIN - 10;
		int y_max = YEAR_MAX + 10;
		ValueRange mr = LunarMonthField.MONTH_N.range();
		int m_min = Math.toIntExact( mr.getMinimum() );
		int m_max = Math.toIntExact( mr.getMaximum() );

		for( int y = y_min ; y <= y_max ; y += 1 ){
			Integer leapMonth = leapMonthsMap.get( y );
			for( int m = m_min ; m < m_max ; m += 1 ){
				try{
					boolean isLeapMonth = KLunarChronology.INSTANCE.isLeapMonth( y, m );
					boolean isLeapMonth0 = leapMonth != null && leapMonth == m;
					assertEquals( isLeapMonth0, isLeapMonth );
				}
				catch( Throwable e ){
					System.out.println( "DOOM: " + y + "-" + m );
					throw e;
				}
			}
		}
	}
}
