package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_parse extends ATest
{
	@Test
	public void toStringAndParse () {
		repeat( ()-> {
			KLunarDate kd0 = null;
			KLunarDate kd1 = null;
			String str = null;
			try{
				kd0 = getRaondomKd();
				str = kd0.toString();
				kd1 = KLunarDate.parse( str );
				assertEquals( kd1, kd0 );
				kd1 = KLunarDate.parse( str.subSequence( KLunarChronology.INSTANCE.getId().length() + 1, str.length() ) );
				assertEquals( kd1, kd0 );
			}
			catch( Throwable e ){
				System.out.println( "DOOM: " + e.getMessage() );
				System.out.println( kd0 );
				System.out.println( str );
				System.out.println( kd1 );
				throw e;
			}
		}, "parse normal cases" );
	}

	@Test
	public void manually () {
		printTitle( "parse some cases" );
		KLunarDate kd1 = null;
		try{
			kd1 = KLunarDate.parse( "2000-02-01" );// LocalDate.parse에서는 이런 경우 에러이긴 한데 뭐 어떤가 싶긴 한데.
			assertEquals( kd1, KLunarDate.of( 2000, 2, false, 1 ) );
			kd1 = KLunarDate.parse( "2000-2-1" );// LocalDate.parse에서는 이런 경우 에러이긴 한데 뭐 어떤가 싶긴 한데.
			assertEquals( kd1, KLunarDate.of( 2000, 2, false, 1 ) );
			kd1 = KLunarDate.parse( "2000-02-01" );
			kd1 = KLunarDate.parse( "000001993-0000010-000000000001" );// LocalDate.parse에서는 이런 경우 에러이긴 한데 뭐 어떤가 싶긴 한데.
			assertEquals( kd1, KLunarDate.of( 1993, 10, false, 1 ) );

			kd1 = KLunarDate.parse( "2004-02-10" );
			assertEquals( kd1, KLunarDate.of( 2004, 2, false, 10 ) );
			kd1 = KLunarDate.parse( "2004-02-40" );
			assertEquals( kd1, KLunarDate.of( 2004, 2, true, 10 ) );

			kd1 = KLunarDate.parse( "KoreanLunar 2004-02-10" );
			assertEquals( kd1, KLunarDate.of( 2004, 2, false, 10 ) );
			kd1 = KLunarDate.parse( "KoreanLunar 2004-02-40" );
			assertEquals( kd1, KLunarDate.of( 2004, 2, true, 10 ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM: " + e.getMessage() );
			System.out.println( kd1 );
			throw e;
		}
	}

	@Test
	public void invalidDates () {
		printTitle( "parse some invalid dates" );
		assertThrowsExactly( NonexistentDateException.class, ()-> {
			KLunarDate.parse( "2010-13-01" );
		} );
		assertThrowsExactly( NonexistentDateException.class, ()-> {
			KLunarDate.parse( "1999-19-1" );
		} );
		assertThrowsExactly( OutOfRangeException.class, ()-> {
			KLunarDate.parse( "1000-01-01" );
		} );
		assertThrowsExactly( OutOfRangeException.class, ()-> {
			KLunarDate.parse( "5000-01-01" );
		} );
	}

	@Test
	public void parseFail () {
		printTitle( "parse invalid format" );
		assertThrowsExactly( DateTimeParseException.class, ()-> {
			KLunarDate.parse( "" );
		} );
		assertThrowsExactly( DateTimeParseException.class, ()-> {
			KLunarDate.parse( "a" );
		} );
		assertThrowsExactly( DateTimeParseException.class, ()-> {
			KLunarDate.parse( "a" );
		} );
	}
}
