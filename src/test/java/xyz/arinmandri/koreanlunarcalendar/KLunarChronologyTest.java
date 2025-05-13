package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.chrono.Chronology;

import org.junit.jupiter.api.Test;


public class KLunarChronologyTest extends KLunarDateTest
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
}
