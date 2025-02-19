package xyz.arinmandri.koreanlunarcalendar;

import java.time.LocalDate;
import java.time.chrono.Chronology;
import org.junit.jupiter.api.Test;


public class KLunarChronologyTest
{

	@Test
	public void test () {

		Chronology c = Chronology.of( "KoreanLunar" );// src/main/resources/META-INF/services/java.time.chrono.Chronology TODO 빌드해서 딴데서 써도 이거 되는지 모르겠네 확인 필요
		System.out.println( c );
	}

}
