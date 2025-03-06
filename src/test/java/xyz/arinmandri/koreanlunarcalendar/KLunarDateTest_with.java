package xyz.arinmandri.koreanlunarcalendar;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_with extends KLunarDateTest
{
	@Test
	public void test () {
		repeat( this::with, "with" );
	}

	@Test
	public void with () {
		/*
		 * 랜덤 년, 월, 일
		 * 날짜를 생성
		 * 랜덤 바꿀값
		 * 날짜에서 년도를 바꾼 뒤 애초부터 그 년도, 월, 일로 생성한 날짜와 같은지 확인
		 */
	}
}
