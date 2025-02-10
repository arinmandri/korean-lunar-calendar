package xyz.arinmandri.kasiapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

	@GET("/B090041/openapi/service/LrsrCldInfoService/getLunCalInfo")
	Call<ResponseData> getLunCalInfo(
	        @Query(value = "serviceKey", encoded = true) String serviceKey,
	        @Query("solYear") String solYear,
	        @Query("solMonth") String solMonth,
	        @Query("solDay") String solDay);

	@GET("/B090041/openapi/service/LrsrCldInfoService/getSolCalInfo")
	Call<ResponseData> getSolCalInfo(
	        @Query(value = "serviceKey", encoded = true) String serviceKey,
	        @Query("lunYear") String lunYear,
	        @Query("lunMonth") String lunMonth,
	        @Query("lunDay") String lunDay);

	@GET("/B090041/openapi/service/LrsrCldInfoService/getSpcifyLunCalInfo")
	Call<ResponseData> getSpcifyLunCalInfo(
	        @Query(value = "serviceKey", encoded = true) String serviceKey,
	        @Query("fromSolYear") String fromSolYear,
	        @Query("toSolYear") String toSolYear,
	        @Query("lunMonth") String lunMonth,
	        @Query("lunDay") String lunDay,
	        @Query("lunDleapMonthay") String leapMonth);
}
