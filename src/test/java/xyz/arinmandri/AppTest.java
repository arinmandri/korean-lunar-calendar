package xyz.arinmandri;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;
import xyz.arinmandri.koreanLunarCalendar.KLunarDate;


public class AppTest
{
	ApiService api = new ApiService();

	public void doit () {}

	/**
	 * 모든 음력 월의 1일의 양력날짜, 평윤, 대소 정보 가져오기
	 */
	@Test
	public void scrap () {

		for( int y = 2004 ; y < 2006 ; y++ ){
			System.out.print( "new YearData( LocalDate.of( " );
			for( int m = 1 ; m < 13 ; m++ ){

				List<Item> items = api.getFromLunDate( y, m, 1 );
				for( Item item : items ){
					LocalDate ld = LocalDate.of( item.getSolYear(), item.getSolMonth(), item.getSolDay() );
//					System.out.println( y + "-" + i( m ) + "(" + item.getLunLeapmonth() + "/" + item.getLunNday() + ")" + " --- " + ld );
					
					if(m==1) {
						System.out.print( ld.getYear() + ", " + ld.getMonthValue() + ", " + ld.getDayOfMonth() + " ) , new byte[]{ " );
					}

					int isLeapMonth = switch( item.getLunLeapmonth() ){
					case "평" -> 0;
					case "윤" -> 2;
					default-> throw new RuntimeException("??? ㄴㅇㄱ 평달도 윤달도 아닌 달이 있다!? 이건 당장 제보해야 돼.");
					};

					int isBigMonth = switch( item.getLunNday() ){
					case 29 -> 0;
					case 30 -> 1;
					default-> throw new RuntimeException("??? ㄴㅇㄱ 대월도 소월도 아닌 달이 있다!? 이건 당장 제보해야 돼.");
					};

					System.out.print( ( isLeapMonth + isBigMonth ) + ", " );
				}
			}
			System.out.println( "} )," );
		}
	}

	public void staticTest () {

		LocalDate ld = LocalDate.of( 2036, 7, 23 );
//		LocalDate ld = LocalDate.of( 2036, 8, 3 );
		test( ld );
	}

	/**
	 * 범위 내에서 아무 날짜나 뽑아서 우리 음력 클래스를 이용해서 음력으로 변환 공공데이터 API 결과와 일치하는지 확인.
	 */
	@Test
	public void randomTest () {

		int testSize = 100;

		for( int i = 0 ; i < testSize ; i++ ){

			LocalDate d1 = LocalDate.of( 2004, 1, 22 );
			LocalDate d2 = LocalDate.of( 2049, 1, 22 );

			LocalDate ld = getRandomDate( d1, d2 );

			test( ld );

			if( i % 10 == 9 ){
				System.out.println( i + 1 + "째 시험 통과" );
			}
		}
	}

	//// ================================

	private void test ( LocalDate ld ) {
		KLunarDate kd = getKLunarDate( ld );
		verifyMyKLunarDate( ld, kd );
	}

	private KLunarDate getKLunarDate ( LocalDate ld ) {

		try{
			KLunarDate kd = KLunarDate.from( ld );// 우리 클래스로 만든 음력 날짜
			return kd;
		}
		catch( Exception e ){
			fail( "양력 " + ld + " 을를 음력으로 바꾸기 실패 ... " + e.getMessage() );
			return null;
		}
	}

	private void verifyMyKLunarDate ( LocalDate ld , KLunarDate kd ) {

		Item item = api.getFromSolDate(
		        ld.getYear(),
		        ld.getMonthValue(),
		        ld.getDayOfMonth() );
		if( item == null ){
			fail( "양력 " + ld + " 정보가 없습니다." );
		}

		if( item.getLunYear() == kd.getYear()
		        && item.getLunMonth() == kd.getMonth()
		        && item.getLunDay() == kd.getDay()
		        && item.getLunLeapmonth().equals( "윤" ) == kd.isLeapMonth() ){
			//
		}
		else{
			fail( "양력 " + ld + " 을를 직접 변환한 음력 날짜 " + kd + " 와 KASI 제공 음력날짜 " + item.toLunString() + " 이가 다릅니다." );
		}
	}

	private LocalDate getRandomDate ( LocalDate d1 , LocalDate d2 ) {

		long n = ChronoUnit.DAYS.between( d1, d2 );// 시작일~종료일 일수
		long randomDays = ThreadLocalRandom.current().nextLong( n + 1 );// 랜덤 숫자 뽑기
		return d1.plusDays( randomDays );
	}

	private String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}
}
