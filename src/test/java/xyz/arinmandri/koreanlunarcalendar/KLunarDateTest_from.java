package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest_from
        extends KLunarDateTest
{

	@Test
	public void test () {
		repeatShortly( this::randomTest, "from" );
	}

//	@Test
	public void fixedOne () {
		/*
		 * 특정 날짜 잡아서 디버깅할라고
		 */

		testOne( LD_MAX.minusDays( 1 ), true );
	}

	public void randomTest () {
		/*
		 * 범위 내에서 아무 날짜나 뽑아서
		 */
		LocalDate ld = getRandomLd();
		testOne( ld );
	}

	@Test
	public void boundaryTest () {

		//// 지원범위 양끝
		testOne( LD_MIN.minusDays( 2 ), false );
		testOne( LD_MIN.minusDays( 1 ), false );
		testOne( LD_MIN, true );
		testOne( LD_MIN.plusDays( 1 ), true );

		testOne( LD_MAX_KASI.minusDays( 1 ), true );
		testOne( LD_MAX_KASI, true );
		testOne( LD_MAX_KASI.plusDays( 1 ), false );
		testOneWithoutKASI( LD_MAX_KASI.plusDays( 1 ), true );
		testOneWithoutKASI( LD_MAX.minusDays( 1 ), true );
		testOneWithoutKASI( LD_MAX, true );
		testOneWithoutKASI( LD_MAX.plusDays( 1 ), false );

		System.out.println( "지원범위 양끝 통과" );

		//// 각 주기 양끝 (처음과 마지막은 지원범위 양끝과 동일하므로 생략)
		for( int i = 1 ; i < epochDays.length - 1 ; i += 1 ){
			testOne( LocalDate.ofEpochDay( epochDays[i] - 1 ) );
			testOne( LocalDate.ofEpochDay( epochDays[i] ) );
			testOne( LocalDate.ofEpochDay( epochDays[i] + 1 ) );
			System.out.println( "각 주기 끝: " + KLunarDate.ofEpochDay( epochDays[i] ) + " 앞뒤 1일씩 통과" );
		}
	}

	private void testOne ( LocalDate ld ) {
		testOne( ld, true );
	}

	private void testOne ( LocalDate ld , boolean inRange ) {

		KLunarDate kd = getKd( ld, inRange );
		if( kd == null ) return;

		one_verify( ld, kd, inRange );
		one_verify( kd, ld );
	}

	private void testOneWithoutKASI ( LocalDate ld , boolean inRange ) {

		KLunarDate kd = getKd( ld, inRange );
		if( kd == null ) return;

		one_verify( kd, ld );
	}

	private KLunarDate getKd ( LocalDate ld , boolean inRange ) {
		KLunarDate kd;
		try{
			kd = KLunarDate.from( ld );// 우리 클래스로 만든 음력 날짜
			return kd;
		}
		catch( OutOfRangeException e ){
			if( inRange ){
				fail( ld + " 은 지원범위 밖입니다." );
			}
			else{
				return null;
			}
		}
		catch( Exception e ){
			e.printStackTrace();
			fail( "양력 " + ld + " 을를 음력으로 바꾸기 실패 ... " + e.getMessage() );
		}
		return null;
	}

	private void one_verify ( LocalDate ld0 , KLunarDate kd , boolean inRange ) {

		Item item = api.getFromIsoDate(
		        ld0.getYear(),
		        ld0.getMonthValue(),
		        ld0.getDayOfMonth() );
		if( item == null ){
			if( inRange ){
				fail( "양력 " + ld0 + " 정보가 없습니다." );
			}
			else{
				return;
			}
		}

		if( !checkEquality( item, kd ) ){
			fail( "양력 " + ld0 + " 을를 직접 변환한 음력 날짜 " + kd + " 와 KASI 제공 음력날짜 " + item.toLunString() + " 이가 다릅니다." );
		}
	}

	private void one_verify ( KLunarDate kd , LocalDate ld0 ) {

		LocalDate ld1 = kd.toLocalDate();
		if( !ld0.equals( ld1 ) ){
			fail( "원본 양력 날짜 " + ld0 + " 와 음력으로 변환 후 양력으로 재변환한 날짜 " + ld1 + " 가 다릅니다." );
		}
	}

}
