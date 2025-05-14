package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest_of extends KLunarDateTest
{

	@Test
	public void testYmdl () {

		repeatShortly( this::testYmdl_one, "of ymdl" );
	}

	private void testYmdl_one () {
		final int y = getRandomInt( LD_MIN.getYear(), LD_MAX.getYear() );
		final int m = getRandomInt( 1, 12 );
		final int d = getRandomInt( 1, 30 );

		testYmdl_one( y, m, d );
	}

	private void testYmdl_one ( final int y , final int m , final int d ) {
		/*
		 * 랜덤으로 년월일을 각각 뽑아 음력날짜를 만든다.
		 * 음력날짜에서 양력날짜로 변환한다. KLunarDate.toLocalDate의 양력날짜와 정답의 양력날짜가 일치하나 확인한다.
		 * 정답에서 윤달이 확인되는 경우 윤달 날짜도 생성해서 양력날짜 비교 
		 * 
		 * NonexistentDateException 경우 정답에서도 없는 날짜인지 확인한다.
		 * OutOfRangeException 경우 실패가 아니고 날짜 다시 뽑아서 테스트한다.
		 * 
		 */

		KLunarDate kd1;
		try{
			kd1 = KLunarDate.of( y, m, d );
		}
		catch( OutOfRangeException e ){
			throw new NoNeedToTest();
		}
		catch( NonexistentDateException e ){
			List<Item> items = api.getFromLunDate( y, m, d );
			if( items.size() != 0 )
			    fail( "음력 날짜 " + '-' + i( m ) + '-' + i( d ) + " 이 KLunarDate에서 없는 날짜로 나오지만 KASI 제공 날짜가 확인됩니다." );
			return;
		}

		LocalDate ld1 = kd1.toLocalDate();
		if( ld1.isAfter( LD_MAX ) ){// KASI 지원범위 넘는 날짜 나오면 테스트하지 말자
			throw new NoNeedToTest();
		}

		//// 정답 확인
		List<Item> items = api.getFromLunDate( y, m, d );
		if( items.size() == 0 ){
			fail( "음력 날짜 " + '-' + i( m ) + '-' + i( d ) + " 이 KLunarDate에서 있는 날짜로 나오지만 KASI 제공 날짜가 없습니다." );
			return;
		}
		Item item1 = items.get( 0 );
		if( !checkEquality( item1, ld1 ) ){
			fail( "음력 " + kd1 + " 을를 직접 변환한 양력 날짜 " + ld1 + " 와 KASI 제공 날짜 " + item1.toSolString() + " 이가 다릅니다." );
		}

		//// 윤달 있는 경우 윤달도 확인
		if( items.size() == 2 ){

			KLunarDate kd2;
			try{
				kd2 = KLunarDate.of( y, m, true, d );
			}
			catch( NonexistentDateException e ){
				fail( "음력 날짜 " + '-' + i( m ) + '-' + i( d ) + "L(윤달) 이 KLunarDate에서 없는 날짜로 나오지만 KASI 제공 날짜가 확인됩니다." );
				return;
			}
			LocalDate ld2 = kd2.toLocalDate();

			Item item2 = items.get( 1 );
			if( !checkEquality( item2, ld2 ) ){
				fail( "음력 " + kd2 + " 을를 직접 변환한 양력 날짜 " + ld2 + " 와 KASI 제공 날짜 " + item2.toSolString() + " 이가 다릅니다." );
			}
		}
	}

	@Test
	public void testOfLeapMonths () {
		printTitle( "testOfLeapMonths" );
		int d = 29;

		System.out.println( "윤달 아닌 날짜 정상 생성 확인" );
		for( int y = YEAR_MIN ; y <= YEAR_MAX ; y++ ){
			for( int m = 1 ; m <= 12 ; m++ ){
				checkKdRoundtrip( y, m, false, d );
			}
		}

		System.out.println( "윤달 날짜 정상 생성 확인" );

		for( Entry<Integer, Integer> e : leapMonthsMap.entrySet() ){
			int y = e.getKey();
			int m = e.getValue();
			checkKdRoundtrip( y, m, true, d );
		}

		System.out.println( "윤달 아닌 날짜 비정상 생성 확인" );
		for( int y = YEAR_MIN ; y <= YEAR_MAX ; y++ ){
			for( int m = 1 ; m <= 12 ; m++ ){
				final int y1 = y;
				final int m1 = m;
				final Integer m2 = leapMonthsMap.get( y1 );
				final boolean isLeapMonth = m2 != null && m2 == m1;
				if( isLeapMonth ){
					checkKdRoundtrip( y, m, true, d );
				}
				else{
					Exception exception = assertThrows( NonexistentDateException.class, ()-> {
						checkKdRoundtrip( y1, m1, true, d );
					} );
				}
			}
		}
	}
}
