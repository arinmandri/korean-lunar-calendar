package xyz.arinmandri;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;


public class Scrap
{
	ApiService api = ApiService.getInstance();

	/**
	 * 
	 * 모든 음력 월의 1일의 양력날짜, 평윤, 대소 정보 가져오기
	 * KASI API의 getSpcifyLunCalInfo로써 모든 년의 1월을 한 번에 조회, 모든 년의 2월을 한 번에 조회, ...
	 * KLunarDate.ydss 소스코드에 붙여넣을 형식으로 출력한다.
	 */
	@Test
	public void scrap2 () {
		final int YEAR_MIN = 1391;
		final int YEAR_MAX = 2050;
		final int EPOCH_0_JDAY = 2440588;

		YearData[] yds = new YearData[YEAR_MAX - YEAR_MIN + 1];
		for( int i = 0 ; i < yds.length ; i++ )
		    yds[i] = new YearData();

		List<Integer> epochDays = new ArrayList<>();

		//// 각 해의 첫달: 율리우스 적일
		{
			List<Item> items = api.getSpcifyLunCalInfo( YEAR_MIN, YEAR_MAX, 1, 1, false );
			for( Item item : items ){
				YearData yd = yds[item.getLunYear() - YEAR_MIN];
				yd.setJDay( item.getSolJd() );
			}
		}

		//// 윤달정보 및 각 월의 대소
		for( int m = 1 ; m <= 12 ; m += 1 ){
			List<Item> items = api.getSpcifyLunCalInfo( YEAR_MIN, YEAR_MAX, m, 1, null );
			for( Item item : items ){
				YearData yd = yds[item.getLunYear() - YEAR_MIN];
				yd.pushIsBigMonth( item.getLunNday() == 30 );
				if( item.getLunLeapmonth().equals( "윤" ) )
				    yd.setLeapMonth( item.getLunMonth() );
			}
		}
		if( YEAR_MAX >= 2050 ){// 2050년만 각월대소 직접 입력(KASI API 지원이 중간에 잘려서)
			YearData yd2050 = yds[2050 - YEAR_MIN];
			yd2050.isBigMonths = new boolean[]{ true, false, false, true, false, true, false, true, true, false, true, true, false, };
		}

		{
			int y = Integer.MIN_VALUE;
			int c = 0;
			while( true ){// 주기
				System.out.println( "\t{" );

				int y_c = YEAR_MIN + c * 60;
				int cJDay = yds[y_c - YEAR_MIN].getJDay();

				int cEpochDay = cJDay - EPOCH_0_JDAY;
				epochDays.add( cEpochDay );

				for( int cd = 0 ; cd < 60 ; cd += 10 ){// 10년 (10년마다 1줄씩 출력)
					System.out.print( "\t\t" );

					int y_cd = y_c + cd;
					for( int dy = 0 ; dy < 10 ; dy += 1 ){// 해
						y = y_cd + dy;
						if( y > YEAR_MAX ) break;

						YearData yd = yds[y - YEAR_MIN];
						System.out.print( String.format( "0x%08X", yd.export( cJDay ) ) );
						System.out.print( ", " );
					}
					System.out.println( "// " + y_cd );
					if( y >= YEAR_MAX ) break;
				}
				System.out.println( "\t}," );
				if( y >= YEAR_MAX ) break;
				c += 1;
			}
		}

		System.out.println( "----------------------------------------" );

		System.out.println( "static final int[] epochDays = {" );
		for( int c = 0 ; c < epochDays.size() ; c++ ){
			System.out.println( "\t" + epochDays.get( c ) + ",// " + ( YEAR_MIN + c * 60 ) );
		}
		System.out.println( "\t" + 29626 + ",// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)" );
		System.out.println( "};" );
	}

	private class YearData
	{
		private int jDay = 0;// 이 해의 첫날의 율리우스적일
		private int leapMonth = 0xF;
		private boolean[] isBigMonths = new boolean[13];
		private int m0 = 0;// 이 다음엔 몇 번째 월 정보를 채울지 (0부터 시작)

		void setJDay ( int _jDay ) {
			if( jDay != 0 )
			    throw new RuntimeException( "jDay 중복설정" );
			jDay = _jDay;
		}

		int getJDay () {
			return jDay;
		}

		void setLeapMonth ( int _leapMonth ) {
			if( leapMonth != 0xF )
			    throw new RuntimeException( "leapMonth 중복설정" );
			leapMonth = _leapMonth;
		}

		void pushIsBigMonth ( boolean isBigMonth ) {
			if( m0 >= 13 )
			    throw new RuntimeException( "isBigMonths 13개 넘음" );
			isBigMonths[m0++] = isBigMonth;
		}

		int export ( int cJDay ) {// cJDay: 이 주기의 첫날의 율리우스적일
			int yd = 0;

			// 15비트: 이 주기의 첫날과 이 년의 첫 날의 날짜 차이 (일 단위)
			int yOffset = jDay - cJDay;
			yd |= yOffset << 17;

			// 4비트: 몇 월에 윤달이 있나
			yd |= leapMonth << 13;

			// 13비트: 각 월의 대소(0:소 1:대) (0번째 달부터 12번째 달까지 윤달도 똑같이 한 달로 취급, 1의자리부터 1월)
			for( int m0 = 0 ; m0 < 13 ; m0 += 1 ){
				if( isBigMonths[m0] )
				    yd |= 1 << m0;
			}

			return yd;
		}

		public String toString () {
			String str = "jDay(" + jDay + ") 윤" + ( leapMonth == 0xF ? "-" : leapMonth ) + " / ";
			for( boolean b : isBigMonths ){
				str += b ? "1" : "0";
			}
			return str;
		}
	}

	/**
	 * 모든 음력 월의 1일의 양력날짜, 평윤, 대소 정보 가져오기
	 * KASI API의 getFromLunDate로써 API 한 번에 한 달씩 조회한다.
	 * KLunarDate.ydss 소스코드에 붙여넣을 형식으로 출력한다.
	 */
//	@Test
	public void scrap () {

		final int YEAR_BASE = 2049;// KLunarDate.YEAR_BASE
		final int year_min = YEAR_BASE;
		final int year_max = YEAR_BASE + CYCLE_SIZE * 10;

		final int leapMonthNone = 0xF;

		int jDayC0 = 0;// 지원범위 첫날의 율리우스적일

		boolean end = false;// 지원범위 끝을 넘음
		String endDate = "";

		StringBuilder cDaySB = new StringBuilder();
		for( int c = year_min ; c < year_max ; c += CYCLE_SIZE ){// 매 주기
			System.out.print( "{\n" );

			int jDayC = 0;// 지원범위 첫날 ~ 이 주기 첫날 적일 차이
			int jDayY0 = 0;// 이 주기 첫날의 율리우스적일

			int decade = -999;// 주석용

			for( int y0 = 0 ; y0 < CYCLE_SIZE ; y0++ ){// 주기 중 1년
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

			cDaySB.append( jDayC + " - EPOCH_0_JDAY,// " + c + "\n" );

			if( end ) break;
		}// 매 주기
		if( end ) System.out.println( "넘은 날짜: " + endDate );

		System.out.print( "\n\n" );
		System.out.println( cDaySB );
		System.out.println( "jDays 마지막 항목은 손수 입력... (" + jDayC0 + ")" );

		/*
		 * 년도별 일수 긁어오기
		 */
		int jDay = 0;
		for( int y = year_min ; y <= 2049 ; y++ ){
			List<Item> items = api.getFromLunDate( y, 1, 1 );
			Item item = items.get( 0 );
			int jDay1 = item.getSolJd();
			System.out.println( jDay1 - jDay + "// " + ( y - 1 ) );
			jDay = jDay1;
		}
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
