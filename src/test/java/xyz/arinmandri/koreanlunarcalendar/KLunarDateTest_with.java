package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_with extends KLunarDateTest
{
	@Test
	public void test () {
		repeat( this::withYear, "with - year" );
		repeat( this::withMonth, "with - month" );
		repeat( this::withDay, "with - day" );
	}

	public void withYear () {
		/*
		 * 랜덤 년, 월, 일
		 * 날짜를 생성
		 * 랜덤 바꿀값
		 * 날짜에서 년도를 바꾼 뒤 애초부터 그 년도, 월, 일로 생성한 날짜와 같은지 확인
		 * 일자가 다른 경우 원래일이 30일, 결과가 29일, 목표일이 소월인지 확인
		 */
		int y0 = getRandomInt( YEAR_MIN, YEAR_MAX );
		int y1 = getRandomInt( YEAR_MIN, YEAR_MAX );
		int m0 = getRandomInt( 1, 12 );
		int d0 = getRandomInt( 1, 30 );

		KLunarDate kd0;
		KLunarDate kd1;
		try{
			kd0 = KLunarDate.of( y0, m0, d0 );
		}
		catch( NonexistentDateException e ){
			throw new NoNeedToTest();
		}
		try{
			kd1 = kd0.withYear( y1 );
		}
		catch( NonexistentDateException e ){
			System.out.println( kd0 );
			throw e;
		}

		try{
			assertEquals( y1, kd1.getYear() );
			assertEquals( m0, kd1.getMonth() );

			int d1 = kd1.getDay();
			if( d0 != d1 ){
				assertEquals( 30, d0 );
				assertEquals( 29, d1 );
			}
			// TODO 윤달 어케 함?
		}
		catch( Exception e ){
			System.out.println( kd0 + " --> " + kd1 );
			throw e;
		}
	}

	public void withMonth () {
		int y0 = getRandomInt( YEAR_MIN, YEAR_MAX );
		int m0 = getRandomInt( 1, 12 );
		int m1 = getRandomInt( 1, 12 );
		int d0 = getRandomInt( 1, 30 );

		KLunarDate kd0;
		KLunarDate kd1;
		try{
			kd0 = KLunarDate.of( y0, m0, d0 );
		}
		catch( NonexistentDateException e ){
			throw new NoNeedToTest();
		}
		try{
			kd1 = kd0.withMonth( m1 );
		}
		catch( NonexistentDateException e ){
			System.out.println( kd0 );
			throw e;
		}

		try{
			assertEquals( y0, kd1.getYear() );
			assertEquals( m1, kd1.getMonth() );

			int d1 = kd1.getDay();
			if( d0 != d1 ){
				assertEquals( 30, d0 );
				assertEquals( 29, d1 );
			}
			// TODO 윤달 어케 함?
		}
		catch( Exception e ){
			System.out.println( kd0 + " --> " + kd1 );
			throw e;
		}
	}

	public void withDay () {
		int y0 = getRandomInt( YEAR_MIN, YEAR_MAX );
		int m0 = getRandomInt( 1, 12 );
		int d0 = getRandomInt( 1, 30 );
		int d1 = getRandomInt( 1, 30 );

		KLunarDate kd0;
		KLunarDate kd1;
		try{
			kd0 = KLunarDate.of( y0, m0, d0 );
		}
		catch( NonexistentDateException e ){
			throw new NoNeedToTest();
		}
		try{
			kd1 = kd0.withDay( d1 );
		}
		catch( NonexistentDateException e ){
			System.out.println( kd0 );
			throw e;
		}

		try{
			assertEquals( y0, kd1.getYear() );
			assertEquals( m0, kd1.getMonth() );

			if( d1 != kd1.getDay() ){
				assertEquals( 30, d1 );
				assertEquals( 29, kd1.getDay() );
			}
			// TODO 윤달 어케 함?
		}
		catch( Exception e ){
			System.out.println( kd0 + " --> " + kd1 );
			throw e;
		}
	}

	// TODO 윤달
}
