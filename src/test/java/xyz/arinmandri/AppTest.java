package xyz.arinmandri;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;
import xyz.arinmandri.koreanlunarcalendar.KLunarDate;


public class AppTest
{
	ApiService api = ApiService.getInstance();

	final int YEAR_BASE = 1864;// KLunarDate.YEAR_BASE
	final int year_min = YEAR_BASE;
	final int year_max = YEAR_BASE + KLunarDate.CYCLE_SIZE * 10;

	@Test
	public void readme () {
		KLunarDate kd = KLunarDate.of( 2000, 12, 7 );
		LocalDate ld = kd.toLocalDate();
		System.out.println( ld );
	}

//	@Test
	public void justOneApi () {
		List<Item> items = api.getFromLunDate( 2050, 11, 18 );
		for( Item item : items ){
			System.out.println( item );
		}
	}

//	@Test
	public void printYd () {
		int yd = 0x11126DA9;
		System.out.println( "적일: " + ( yd >>> 17 ) );
		System.out.println( "윤달위치 " + ( ( yd >>> 13 ) & 0xF ) );
		System.out.println( "각월대소 " + Integer.toBinaryString( yd & 0x1FFF ) );
	}

	/**
	 * 모든 음력 월의 1일의 양력날짜, 평윤, 대소 정보 가져오기
	 */
//	@Test
	public void scrap () {

		final int leapMonthNone = 0xF;

		int jDayC0 = 0;// 지원범위 첫날의 율리우스적일

		boolean end = false;// 지원범위 끝을 넘음
		String endDate = "";

		StringBuilder cDaySB = new StringBuilder();
		for( int c = year_min ; c < year_max ; c += KLunarDate.CYCLE_SIZE ){// 매 주기
			System.out.print( "{\n" );

			int jDayC = 0;// 지원범위 첫날 ~ 이 주기 첫날 적일 차이
			int jDayY0 = 0;// 이 주기 첫날의 율리우스적일

			int decade = -999;// 주석용

			for( int y0 = 0 ; y0 < KLunarDate.CYCLE_SIZE ; y0++ ){// 주기 중 1년
				int y = c + y0;
				decade = ( y0 % 10 == 0 ) ? y : decade;
				int yd = 0;// KLunarDate.ydss에 듦

				int jDayY = 0;// 이 주기 첫날 ~ 이 해 첫날 적일 차이

				int leapMonth = leapMonthNone;// 몇 번째 달이 윤달? (0부터 셈. 즉 이 값이 0이면 1월에 윤달이 있다.) (0xF인 경우 윤달 없음)

				int bigLilBit = 0x1;

				for( int m = 1 ; m < 13 ; m++ ){// 월

					List<Item> items = api.getFromLunDate( y, m, 1 );
//					List<Item> items = api.getFromLunDateTest( y, m, 1 );// 출력 형식만 확인

					if( items.size() == 0 ){// 날짜 제공 안 됨
						end = true;
						endDate = y + "-" + i( m );
						break;
					}

					if( items.size() == 2 ){// 윤달 있음
						if( leapMonth != leapMonthNone )
						    throw new RuntimeException( "??? ㄴㅇㄱ 윤달이 한 해에 두 개 있다!? 이건 당장 제보해야 돼. " + y + '-' + i( m ) );
						leapMonth = m;
					}

					for( Item item : items ){// 윤달 있으면 2개임.

						int nDay = item.getLunNday();// 한 달의 일수

						switch( nDay ){// 각 월의 대소
						case 29:{
							break;
						}
						case 30:{
							yd |= bigLilBit;// 해당 월을 대월로 표시
							break;
						}
						default:
							throw new RuntimeException( "??? ㄴㅇㄱ 대월도 소월도 아닌 달이 있다!? 이건 당장 제보해야 돼. " + y + '-' + i( m ) );
						}

						int jDay = item.getSolJd();
						if( bigLilBit == 0x1 ){// 첫 달
							if( y0 == 0 ){// 주기의 첫 해
								if( c == year_min ){// 첫 주기
									jDayC0 = jDay;
								}
								jDayC = jDay;
								jDayY0 = jDay;
							}
							jDayY = jDay - jDayY0;
						}

						bigLilBit <<= 1;
					}// 윤달 있으면 2개
				}// 월

				yd |= leapMonth << 13;
				yd |= jDayY << 17;

				System.out.print( String.format( "0x%08X", yd ) );// 16진수로 표시
//				System.out.print( binaryYd( yd ) );// 2진수로 표시
				System.out.print( ", " );
				if( y0 % 10 == 9 || end )// 10년마다 줄내림
				    System.out.print( "// " + decade + "\n" );

				if( end ) break;
			}// 주기 중 1년
			System.out.print( "},\n" );

			cDaySB.append( jDayC + " - " + jDayC0 + ",// " + c + "\n" );

			if( end ) break;
		}// 매 주기
		if( end ) System.out.println( "넘은 날짜: " + endDate );

		System.out.print( "\n\n" );
		System.out.println( cDaySB );
		System.out.println( "jDays 마지막 항목은 손수 입력... (" + jDayC0 + ")" );
	}

	//// ================================ util, private, etc

	private String binaryYd ( int yd ) {
		String b0 = Integer.toBinaryString( yd );
		while( b0.length() < 32 ){
			b0 = "0" + b0;
		}
		return "0b" + b0.substring( 0, 15 ) + '_' + b0.substring( 15, 19 ) + '_' + b0.substring( 19, 32 );
	}

	private String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}
}
