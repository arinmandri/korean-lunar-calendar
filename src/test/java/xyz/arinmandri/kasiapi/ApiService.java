package xyz.arinmandri.kasiapi;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class ApiService
{
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

		return items;
	}

	/*
	 * 출력 서식 확인만 하게
	 */
	public List<Item> getFromLunDateTest(int lunYear, int lunMonth, int lunDay) {

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

		return List.of(item);
	}

	/**
	 * 여러 해(양력)에 걽여 음력 날짜(월, 일)들 모두 조회
	 *
	 * @param fromSolYear
	 * @param toSolYear
	 * @param lunMonth
	 * @param lunDay
	 * @param leapMonth
	 * @return
	 */
	public List<Item> getSpcifyLunCalInfo ( int fromSolYear , int toSolYear , int lunMonth , int lunDay , boolean leapMonth ) {
		Call<ResponseData> call = api.getSpcifyLunCalInfo(
		        serviceKey,
		        i( fromSolYear ),
		        i( toSolYear ),
		        i( lunMonth ),
		        i( lunDay ),
		        leapMonth ? "평" : "윤" );

		return request( call );
	}

	/**
	 * 여러 해(양력)에 걽여 음력 날짜(월, 일)들 모두 조회
	 *
	 * @param jDay 율리우스적일
	 * @return
	 */
	public Item getFromJDay ( int jDay ) {
		Call<ResponseData> call = api.getJulDayInfo(
		        serviceKey,
		        String.valueOf( jDay ) );

		List<Item> items = request( call );
		if( items.size() == 0 ) return null;
		if( items.size() == 1 ) return items.get( 0 );
		throw new RuntimeException( "결과가 여러개!?" );
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

	private static int testCount = 2401910;
}
