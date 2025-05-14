package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import org.junit.jupiter.api.Test;


public class KLunarDateTest_withGanji extends ATest
{
	@Test
	public void testWithSecha () {
		repeat( this::testWithSecha_1, "with ganji year" );
		repeat( this::testWithWolgeon_1, "with ganji month" );
		repeat( this::testWithIljin_1, "with ganji day" );
	}

	private void testWithSecha_1 () {
		final KLunarDate kd0 = getRandomKd_crop60y();
		KLunarDate[] kds = new KLunarDate[CYCLE_SIZE];

		try{
			//// 육십갑자의 날짜 모두 수집
			for( int i = 0 ; i < CYCLE_SIZE ; i++ ){
				Ganji gj = Ganji.values()[i];
				kds[i] = kd0.withSecha( gj );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 수집 X" );
			System.out.println( kd0 );
			throw e;
		}

		int i1 = -1;
		try{
			//// 해당 간지 갖고있나 확인
			for( i1 = 0 ; i1 < CYCLE_SIZE ; i1++ ){
				assertEquals( Ganji.values()[i1], kds[i1].getSecha() );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 간지값 X" );
			System.out.println( i1 );
			if( i1 != -1 && i1 < CYCLE_SIZE )
			    System.out.println( kds[i1] );
			throw e;
		}

		int i2 = -1;
		try{
			//// 시간순서 맞나 확인
			for( i2 = 1 ; i2 < CYCLE_SIZE ; i2++ ){
				assertTrue( kds[i2].isAfter( kds[i2 - 1] ) );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 순서 X" );
			System.out.println( i2 );
			if( i2 != -1 && i2 < CYCLE_SIZE )
			    System.out.println( kds[i2] );
			throw e;
		}

		try{
			//// 처음~마지막 시간 차이가 1갑자-1에 해당하나 확인
			assertTrue( kds[CYCLE_SIZE - 1].isAfter( kds[0].plusYears( CYCLE_SIZE - 2 ) ) );
			assertTrue( kds[CYCLE_SIZE - 1].isBefore( kds[0].plusYears( CYCLE_SIZE ) ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 간격 X" );
			System.out.println( "     " + kd0 + " : " + kd0.getSecha().ordinal() );
			System.out.println( " 00: " + kds[0] );
			System.out.println( " 59: " + kds[CYCLE_SIZE - 1] );
			System.out.println( "+58: " + kds[0].plusYears( CYCLE_SIZE - 2 ) );
			System.out.println( "+60: " + kds[0].plusYears( CYCLE_SIZE ) );
			throw e;
		}
	}

	/*
	 * 랜덤 날짜 가져오기
	 * 단; 지원범위에서 앞뒤 60년 쳐냄
	 * (지원범위밖 예외 발생 방지용. 경계값 테스트는 별도.)
	 */
	private KLunarDate getRandomKd_crop60y () {
		int min = KLunarDate.of( YEAR_MIN + CYCLE_SIZE, 1, 1 ).toEpochDayInt();
		int max = KLunarDate.of( YEAR_MAX + 1 - CYCLE_SIZE, 1, 1 ).toEpochDayInt() - 1;
		return KLunarDate.ofEpochDay( getRandomInt( min, max ) );
	}

	private void testWithWolgeon_1 () {
		final KLunarDate kd0 = getRandomKd_crop60m().withMonthLeap( false );// 윤달은 월건이 없다.
		KLunarDate[] kds = new KLunarDate[CYCLE_SIZE];

		try{
			//// 육십갑자의 날짜 모두 수집
			for( int i = 0 ; i < CYCLE_SIZE ; i++ ){
				Ganji gj = Ganji.values()[i];
				kds[i] = kd0.withWolgeon( gj );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 수집 X" );
			System.out.println( kd0 );
			throw e;
		}

		int i1 = -1;
		try{
			//// 해당 간지 갖고있나 확인
			for( i1 = 0 ; i1 < CYCLE_SIZE ; i1++ ){
				assertEquals( Ganji.values()[i1], kds[i1].getWolgeon() );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 간지값 X" );
			System.out.println( i1 );
			if( i1 != -1 && i1 < CYCLE_SIZE )
			    System.out.println( kds[i1] );
			throw e;
		}

		int i2 = -1;
		try{
			//// 시간순서 맞나 확인
			for( i2 = 1 ; i2 < CYCLE_SIZE ; i2++ ){
				assertTrue( kds[i2].isAfter( kds[i2 - 1] ) );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 순서 X" );
			System.out.println( i2 );
			if( i2 != -1 && i2 < CYCLE_SIZE )
			    System.out.println( kds[i2] );
			throw e;
		}

		try{
			//// 처음~마지막 시간 차이가 1갑자-1에 해당하나 확인
			assertTrue( kds[CYCLE_SIZE - 1].isAfter( kds[0].plusNamedMonths( CYCLE_SIZE - 2 ) ) );
			assertTrue( kds[CYCLE_SIZE - 1].isBefore( kds[0].plusNamedMonths( CYCLE_SIZE ) ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 간격 X" );
			System.out.println( "     " + kd0 + " : " + kd0.getWolgeon().ordinal() );
			System.out.println( " 00: " + kds[0] );
			System.out.println( " 59: " + kds[CYCLE_SIZE - 1] );
			System.out.println( "+58: " + kds[0].plusMonths( CYCLE_SIZE - 2 ) );
			System.out.println( "+60: " + kds[0].plusMonths( CYCLE_SIZE ) );
			throw e;
		}
	}

	/*
	 * 랜덤 날짜 가져오기
	 * 단; 지원범위에서 앞뒤 60개월 쳐냄
	 */
	private KLunarDate getRandomKd_crop60m () {
		int min = MIN.plusMonths( 60 ).toEpochDayInt();
		int max = MAX.minusMonths( 60 ).toEpochDayInt();
		return KLunarDate.ofEpochDay( getRandomInt( min, max ) );
	}

	private void testWithIljin_1 () {
		final KLunarDate kd0 = getRandomKd_crop60m();
		KLunarDate[] kds = new KLunarDate[CYCLE_SIZE];

		try{
			//// 육십갑자의 날짜 모두 수집
			for( int i = 0 ; i < CYCLE_SIZE ; i++ ){
				Ganji gj = Ganji.values()[i];
				kds[i] = kd0.withIljin( gj );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 수집 X" );
			System.out.println( kd0 );
			throw e;
		}

		int i1 = -1;
		try{
			//// 해당 간지 갖고있나 확인
			for( i1 = 0 ; i1 < CYCLE_SIZE ; i1++ ){
				assertEquals( Ganji.values()[i1], kds[i1].getIljin() );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 간지값 X" );
			System.out.println( i1 );
			if( i1 != -1 && i1 < CYCLE_SIZE )
			    System.out.println( kds[i1] );
			throw e;
		}

		int i2 = -1;
		try{
			//// 시간순서 맞나 확인
			for( i2 = 1 ; i2 < CYCLE_SIZE ; i2++ ){
				assertTrue( kds[i2].isAfter( kds[i2 - 1] ) );
			}
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 순서 X" );
			System.out.println( i2 );
			if( i2 != -1 && i2 < CYCLE_SIZE )
			    System.out.println( kds[i2] );
			throw e;
		}

		try{
			//// 처음~마지막 시간 차이가 1갑자-1에 해당하나 확인
			assertTrue( kds[CYCLE_SIZE - 1].isAfter( kds[0].plusDays( CYCLE_SIZE - 2 ) ) );
			assertTrue( kds[CYCLE_SIZE - 1].isBefore( kds[0].plusDays( CYCLE_SIZE ) ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM: 간격 X" );
			System.out.println( "     " + kd0 + " : " + kd0.getIljin().ordinal() );
			System.out.println( " 00: " + kds[0] );
			System.out.println( " 59: " + kds[CYCLE_SIZE - 1] );
			System.out.println( "+58: " + kds[0].plusDays( CYCLE_SIZE - 2 ) );
			System.out.println( "+60: " + kds[0].plusDays( CYCLE_SIZE ) );
			throw e;
		}
	}

	/*
	 * 랜덤 날짜 가져오기
	 * 단; 지원범위에서 앞뒤 60일 쳐냄
	 */
	private KLunarDate getRandomKd_crop60d () {
		return KLunarDate.ofEpochDay( getRandomInt( EPOCHDAY_MIN + CYCLE_SIZE, EPOCHDAY_MAX - CYCLE_SIZE ) );
	}

	public void letsSeeBoundary () {
		KLunarDate kd;
		kd = MIN;
		System.out.println( kd.getSecha().ordinal() );// 7
		System.out.println( kd.getWolgeon().ordinal() );// 26
		System.out.println( kd.getIljin().ordinal() );// 25
		kd = MAX;
		System.out.println( kd.getSecha().ordinal() );// 6
		System.out.println( kd.getWolgeon().ordinal() );// 25
		System.out.println( kd.getIljin().ordinal() );// 2
	}

	@Test
	public void testBoundary () {
		printTitle( "with ganji boundary" );

		MIN.withSecha( Ganji.values()[7] );
		assertThrows( OutOfRangeException.class, ()-> {
			MIN.withSecha( Ganji.values()[6] );
		} );
		assertThrows( OutOfRangeException.class, ()-> {
			MIN.withSecha( Ganji.values()[0] );
		} );
		MAX.withSecha( Ganji.values()[6] );
		assertThrows( OutOfRangeException.class, ()-> {
			MAX.withSecha( Ganji.values()[7] );
		} );
		assertThrows( OutOfRangeException.class, ()-> {
			MAX.withSecha( Ganji.values()[59] );
		} );

		MIN.withWolgeon( Ganji.values()[26] );
		assertThrows( OutOfRangeException.class, ()-> {
			MIN.withWolgeon( Ganji.values()[25] );
		} );
		assertThrows( OutOfRangeException.class, ()-> {
			MIN.withWolgeon( Ganji.values()[0] );
		} );
		MAX.withWolgeon( Ganji.values()[25] );
		assertThrows( OutOfRangeException.class, ()-> {
			MAX.withWolgeon( Ganji.values()[26] );
		} );
		assertThrows( OutOfRangeException.class, ()-> {
			MAX.withWolgeon( Ganji.values()[59] );
		} );

		MIN.withIljin( Ganji.values()[25] );
		assertThrows( OutOfRangeException.class, ()-> {
			MIN.withIljin( Ganji.values()[24] );
		} );
		assertThrows( OutOfRangeException.class, ()-> {
			MIN.withIljin( Ganji.values()[0] );
		} );
		MAX.withIljin( Ganji.values()[2] );
		assertThrows( OutOfRangeException.class, ()-> {
			MAX.withIljin( Ganji.values()[3] );
		} );
		assertThrows( OutOfRangeException.class, ()-> {
			MAX.withIljin( Ganji.values()[59] );
		} );
	}
}
