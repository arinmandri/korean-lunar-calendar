package xyz.arinmandri.kasiapi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class ApiService
{
	private static final int EPOCH_J_DAY = 2440588;// 0 epoch day의 율리우스적일

	static private ApiService instance = new ApiService();

	private ApiInterface api;
	String serviceKey = ServiceKey.KEY;

	static public ApiService getInstance () {
		return instance;
	}

	private ApiService() {
		init();
	}

	boolean debugMode = false;

	private void init () {

		Retrofit retrofit = new Retrofit.Builder()
		        .baseUrl( "https://apis.data.go.kr/" )
		        .addConverterFactory( SimpleXmlConverterFactory.create() )// XML
		        .build();

		api = retrofit.create( ApiInterface.class );
	}

	/**
	 * ISO 날짜로 조회
	 *
	 * @param solYear
	 * @param solMonth
	 * @param solDay
	 * @return
	 */
	public Item getFromIsoDate ( int year , int month , int day ) {
		LocalDate ld = LocalDate.of( year, month, day );

		Call<ResponseData> call = api.getJulDayInfo(
		        serviceKey,
		        i( ld.toEpochDay() + EPOCH_J_DAY ) );

		List<Item> items = request( call );
		items = refineItems( items );

		if( items.size() == 0 ) return null;
		if( items.size() == 1 ) return items.get( 0 );
		throw new RuntimeException( "결과가 여러개!?" );
	}

	/**
	 * 양력 날짜로 조회
	 *
	 * @param solYear
	 * @param solMonth
	 * @param solDay
	 * @return
	 */
	public Item getFromSolDate ( int solYear , int solMonth , int solDay ) {
		Call<ResponseData> call = api.getLunCalInfo(
		        serviceKey,
		        i( solYear ),
		        i( solMonth ),
		        i( solDay ) );

		List<Item> items = request( call );
		items = refineItems( items );

		if( items.size() == 0 ) return null;
		if( items.size() == 1 ) return items.get( 0 );
		throw new RuntimeException( "결과가 여러개!?" );
	}

	/**
	 * 음력 날짜로 조회
	 *
	 * @param lunYear
	 * @param lunMonth
	 * @param lunDay
	 * @return
	 *         평월, 윤월이 있는 경우 응답이 2개이다.
	 */
	public List<Item> getFromLunDate ( int lunYear , int lunMonth , int lunDay ) {
		Call<ResponseData> call = api.getSolCalInfo(
		        serviceKey,
		        i( lunYear ),
		        i( lunMonth ),
		        i( lunDay ) );

		List<Item> items = request( call );
		items = refineItems( items );

		return items;
	}

	/*
	 * 출력 서식 확인만 하게
	 */
	public List<Item> getFromLunDateTest ( int lunYear , int lunMonth , int lunDay ) {

		{// 범위 초과
			if( lunYear > 2050 ){
				return List.of();
			}
			else if( lunYear == 2050 ){
				if( lunMonth > 11 ){
					return List.of();
				}
				else if( lunMonth == 11 ){
					if( lunDay > 18 ){
						return List.of();
					}
				}
			}
		}

		Item item = new Item();
		item.lunNday = 29;
		item.lunLeapmonth = "평";
		item.solJd = testCount++;

		return List.of( item );
	}

	/**
	 * 여러 해에 걽여 음력 날짜(월, 일)들 모두 조회
	 *
	 * @param fromYear  조회할 범위 (시작년도)
	 * @param toYear    조회할 범위 (끝년도)
	 * @param lunMonth  조회할 음력 날짜의 달
	 * @param lunDay    조회할 음력 날짜의 일
	 * @param leapMonth 윤달여부. null이면 평달윤달 모두 조회.
	 * @return
	 */
	public List<Item> getSpcifyLunCalInfo ( int fromYear , int toYear , int lunMonth , int lunDay , Boolean leapMonth ) {
		/*
		 * 얘만 오류가 있다. lunNDay 값이 제대로 안 나오고 lunDay 값과 같은 값이 나온다.
		 * 문의 했는데 안 바꿔주고 있다.
		 * 그러니까 일자를 30으로 해서 존재하는 날짜를 확인해서 대월소월을 구별하여 lunNDay를 직접 구한다.
		 */
		//// KASI API로 얻은 item들
		List<Item> items = getSpcifyLunCalInfo0( fromYear, toYear, lunMonth, lunDay, leapMonth );
		if( items.size() == 0 ) return items;

		//// 같은 달의 30일 존재 확인용 데이터
		Map<String, Boolean> checkBigMap = new HashMap<>();
		List<Item> checkBigList = getSpcifyLunCalInfo0( fromYear, toYear, lunMonth, 30, leapMonth );
		for( Item item : checkBigList ){
			checkBigMap.put( checkBigMapKey( item ), true );
		}

		//// 30일이 있으면 lunNDay=30 아니면 29 덮어쓰기
		for( Item item : items ){
			item.lunNday = checkBigMap.containsKey( checkBigMapKey( item ) ) ? 30 : 29;
		}

		return items;
	}

	private String checkBigMapKey ( Item item ) {
		return "" + item.lunYear
		        + i( item.lunMonth )
		        + ( item.lunLeapmonth.equals( "윤" ) ? "L" : "C" );
	}

	private List<Item> getSpcifyLunCalInfo0 ( int fromYear , int toYear , int lunMonth , int lunDay , Boolean leapMonth ) {
		/*
		 * 다른 오퍼레이션들과 달리 얘는 여러 날짜를 동시에 조회하는 기능이며 결과가 매우 많고 여러 페이지에 걸쳐있을 수도 있다.
		 * 끝페이지까지 모두 조회하여 합친 결과를 반환한다.
		 */
		final int pageSize = 500;
		List<Item> items = new ArrayList<>();

		int limit = 200;
		int page = 0;
		while( page++ < limit ){
			Call<ResponseData> call = api.getSpcifyLunCalInfo(
			        serviceKey,
			        pageSize,
			        page,
			        i( fromYear ),
			        i( toYear ),
			        i( lunMonth ),
			        i( lunDay ),
			        leapMonth == null ? null : leapMonth ? "윤" : "평" );

			List<Item> itemsThisPage = request( call );

			items.addAll( itemsThisPage );

			if( itemsThisPage.size() < pageSize )
			    break;
		}

		items = refineItems( items );

		return items;
	}

	/**
	 * 특정 율리우스적일 조회
	 *
	 * @param jDay 율리우스적일
	 * @return
	 */
	public Item getFromJDay ( int jDay ) {
		Call<ResponseData> call = api.getJulDayInfo(
		        serviceKey,
		        String.valueOf( jDay ) );

		List<Item> items = request( call );
		items = refineItems( items );
		if( items.size() == 0 ) return null;
		if( items.size() == 1 ) return items.get( 0 );
		throw new RuntimeException( "결과가 여러개!?" );
	}

	/*
	 * 음 1582-09-09부터 음 1582-09-18까지는 양력날짜가 두 개인데 그레고리력, 율리우스력인 거 같다.
	 * 음 1582-09-09 전까지는 그레고리력 날짜가 안 나오고 율리우스력 날짜가 나오는 거 같다.
	 * 
	 * 그레고리력이든 율리우스력이든 그냥 다 집어치우고 율리우스적일만 사용해서 ISO 날짜로 바꿔 쓰자.
	 */
	private List<Item> refineItems ( List<Item> itemsSrc ) {
		/*
		 * 한 날짜에 여러 값이 있을 리 없는 율리우스적일을 키로 삼아 중복을 없앤다.
		 * 율리우스적일 -> epoch day -> LocalDate(ISO)
		 */
		Map<Integer, Item> removeDuplicate = new LinkedHashMap<>();
		for( Item item : itemsSrc ){
			int jDay = item.solJd;

			int epochDay = jDay - EPOCH_J_DAY;
			LocalDate ld = LocalDate.ofEpochDay( epochDay );
			item.solYear = ld.getYear();
			item.solMonth = ld.getMonthValue();
			item.solDay = ld.getDayOfMonth();
			item.solLeapyear = ld.isLeapYear() ? "윤" : "평";

			removeDuplicate.put( jDay, item );
		}

		return List.copyOf( removeDuplicate.values() );
	}

	private List<Item> request ( Call<ResponseData> call ) {
		try{
			// 동기 요청 실행
			String requestUrl = call.request().url().toString();
			if( debugMode ) System.out.println( "Request URL: " + requestUrl );

			Response<ResponseData> response = call.execute();

			if( response.isSuccessful() && response.body() != null ){

				List<Item> items = response.body().getBody().getItems().getItemList();

				return items;
			}
			else{
				if( debugMode ) System.err.println( "Request failed: " + response.errorBody().string() );
			}
		}
		catch( IOException e ){
			if( debugMode ) e.printStackTrace();
		}

		return null;
	}

	private String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}

	private String i ( long i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}

	private static int testCount = 2401910;
}
