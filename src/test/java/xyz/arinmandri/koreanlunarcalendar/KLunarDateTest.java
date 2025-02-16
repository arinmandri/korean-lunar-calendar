package xyz.arinmandri.koreanlunarcalendar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

import xyz.arinmandri.kasiapi.ApiService;


public class KLunarDateTest
{
	ApiService api = ApiService.getInstance();

	/*
	 * KLunarDate private 값들 땜쳐옴
	 */
	final int YEAR_BASE = 1864;// KLunarDate.YEAR_BASE
	final LocalDate MIN = KLunarDate.MIN;
	final LocalDate MAX0 = LocalDate.of( 2050, 12, 31 );// 정답의 기준이 한국천문연구원 API인데 의 지원범위보다 KLunarDate.MAX가 살짝 더 미래이기 때문에 한국천문연구원 API의 지원범위를 직접 입력함.
	final LocalDate MAX1 = KLunarDate.MAX;// 한국천문연구원 API를 안 쓰는 경우 여기까지 시험

	protected static final int[] jDays = {
	        2401910 - 2401910,// 1864
	        2423821 - 2401910,// 1924
	        2445733 - 2401910,// 1984
	        2467645 - 2401910,// 2044
	        2470214 - 2401910,// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)
	};

	//// ================================ util, private, etc

	protected LocalDate getRandomDate ( LocalDate d1 , LocalDate d2 ) {

		long n = ChronoUnit.DAYS.between( d1, d2 );// 시작일~종료일 일수
		long randomDays = ThreadLocalRandom.current().nextLong( n + 1 );// 랜덤 숫자 뽑기
		return d1.plusDays( randomDays );
	}
}
