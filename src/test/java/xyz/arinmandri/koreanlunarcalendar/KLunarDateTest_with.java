package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_with extends KLunarDateTest
{
	@Test
	public void test () {
		System.out.println("으음... 고민중. 랜덤이 아니라 몇몇 경우를 뽑는 게 나을 듯.");
	}

	public void check ( KLunarDate kd , int year , int month , boolean isLeapMonth , int day ) {
		assertEquals( year, kd.getYear() );
		assertEquals( month, kd.getMonth() );
		assertEquals( isLeapMonth, kd.isLeapMonth() );
		assertEquals( day, kd.getDay() );
	}
}
