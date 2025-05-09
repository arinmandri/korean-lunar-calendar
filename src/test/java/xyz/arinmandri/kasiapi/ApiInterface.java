package xyz.arinmandri.kasiapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface
{

	/*
	 * 1582-10-04까지는 율리우스력 취급인 거 같다.
	 * 1582-10-05부터는 그레고리력 취급인 거 같다.
	 */
	@GET( "/B090041/openapi/service/LrsrCldInfoService/getLunCalInfo" )
	Call<ResponseData> getLunCalInfo (
	        @Query( value = "serviceKey" , encoded = true ) String serviceKey ,
	        @Query( "solYear" ) String solYear ,
	        @Query( "solMonth" ) String solMonth ,
	        @Query( "solDay" ) String solDay );

	/*
	 * 1582-09-08까지는 양력 부분이 율리우스력 날짜가 조회되는 거 같다.
	 * 1582-09-09부터 1582-09-18까지는 날짜가 두 개 나오는데 음력 날짜는 같고 양력이 그레고리력, 율리우스력 두 가지가 다 나오는 거 같다.
	 */
	@GET( "/B090041/openapi/service/LrsrCldInfoService/getSolCalInfo" )
	Call<ResponseData> getSolCalInfo (
	        @Query( value = "serviceKey" , encoded = true ) String serviceKey ,
	        @Query( "lunYear" ) String lunYear ,
	        @Query( "lunMonth" ) String lunMonth ,
	        @Query( "lunDay" ) String lunDay );

	/*
	 * 파라미터명은 fromSolYear, toSolYear인데 실제로는 음력을 기준으로 하는 거 같다.
	 */
	@GET( "/B090041/openapi/service/LrsrCldInfoService/getSpcifyLunCalInfo" )
	Call<ResponseData> getSpcifyLunCalInfo (
	        @Query( value = "serviceKey" , encoded = true ) String serviceKey ,
	        @Query( "numOfRows" ) Integer numOfRows ,
	        @Query( "pageNo" ) Integer pageNo ,
	        @Query( "fromSolYear" ) String fromSolYear ,
	        @Query( "toSolYear" ) String toSolYear ,
	        @Query( "lunMonth" ) String lunMonth ,
	        @Query( "lunDay" ) String lunDay ,
	        @Query( "leapMonth" ) String leapMonth );

	@GET( "/B090041/openapi/service/LrsrCldInfoService/getJulDayInfo" )
	Call<ResponseData> getJulDayInfo (
	        @Query( value = "serviceKey" , encoded = true ) String serviceKey ,
	        @Query( "solJd" ) String solJd );
}
