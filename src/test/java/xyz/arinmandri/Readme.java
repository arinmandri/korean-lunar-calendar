package xyz.arinmandri;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;

import xyz.arinmandri.koreanlunarcalendar.KLunarDate;


public class Readme
{
	public static void main ( String[] args ) {
		/*
		 * 모든 메서드를 순회하며 한 번씩 실행.
		 * Order 어노테이션으로 순서 지정
		 */
		Readme instance = new Readme();
		Method[] methods = Readme.class.getDeclaredMethods();

		// Order 값 기준으로 정렬 (어노테이션이 없으면 Integer.MAX_VALUE로 처리)
		Arrays.sort( methods, ( m1 , m2 )-> {
			Order o1 = m1.getAnnotation( Order.class );
			Order o2 = m2.getAnnotation( Order.class );
			int order1 = ( o1 != null ) ? o1.value() : Integer.MAX_VALUE;
			int order2 = ( o2 != null ) ? o2.value() : Integer.MAX_VALUE;
			return Integer.compare( order1, order2 );
		} );

		for( Method method : methods ){
			if( method.getParameterCount() == 0 ){ // 파라미터가 없는 메서드만 실행
				System.out.println( "------------------------------" + method.getName() );
				try{
					method.invoke( instance );
				}
				catch( Exception e ){
					System.out.println( "Error invoking " + method.getName() + ": " + e.getMessage() );
				}
			}
		}
	}

	// ### 날짜 개체 생성
	@Order( 1_00_00 )
	public void 날짜개체생성 () {
		// 오늘 (시간대 지정 불가. 한국 시간대(UTC+9)로만 동작.)
		KLunarDate kd = KLunarDate.now();

		// 특정 음력 날짜(평달)
		kd = KLunarDate.of( 2004, 2, 1 );
		kd = KLunarDate.of( 2004, 2, false, 1 );
		// 특정 음력 날짜(윤달)
		kd = KLunarDate.of( 2004, 2, true, 1 );
		// 특정 년의 n번째 날짜
		kd = KLunarDate.ofYearDay( 2004, 30 );
	}

	// ### 다른 타입으로/에서 변환

	// #### 양력과의 상호 변환
	@Order( 2_01_01 )
	public void 양력과의상호변환1 () {
		// 양력에서 음력으로
		LocalDate ld = LocalDate.of( 2001, 1, 1 );// 양력 날짜
		KLunarDate kd = KLunarDate.from( ld );
		System.out.println( kd );
	}

	@Order( 2_01_02 )
	public void 양력과의상호변환2 () {
		// 음력에서 양력으로
		KLunarDate kd = KLunarDate.of( 2000, 12, 7 );
		LocalDate ld = kd.toLocalDate();
		System.out.println( ld );
	}

	// #### epoch day와의 상호 변환
	@Order( 2_02_01 )
	public void epochday와의상호변환1 () {
		// epoch day에서 음력으로
		long epochDay = 11323;
		KLunarDate kd = KLunarDate.ofEpochDay( epochDay );
		System.out.println( kd );
	}

	@Order( 2_02_02 )
	public void epochday와의상호변환2 () {
		// 음력에서 epoch day로
		KLunarDate kd = KLunarDate.of( 2000, 12, 7 );
		long epochDay = kd.toEpochDay();
		System.out.println( epochDay );
	}

	// #### 문자열과의 상호 변환

	// ### 간지(干支) 조회

	@Order( 3_00_01 )
	public void 간지조회1 () {
		KLunarDate kd = KLunarDate.of( 1969, 11, 24 );
		System.out.println( kd.getSecha() + "년" );
		System.out.println( kd.getWolgeon() + "월" );
		System.out.println( kd.getIljin() + "일" );
	}

	@Order( 3_00_02 )
	public void 간지조회2 () {
		KLunarDate kd = KLunarDate.of( 1969, 11, 24 );
		System.out.println( kd.getSecha().toKoreanString() + "년" );
		System.out.println( kd.getWolgeon().toKoreanString() + "월" );
		System.out.println( kd.getIljin().toKoreanString() + "일" );
	}

	@Order( 3_00_03 )
	public void 간지조회3 () {
		KLunarDate kd = KLunarDate.of( 1969, 11, 24 );
		System.out.println( kd.getSecha().toChineseString() + "년" );
		System.out.println( kd.getWolgeon().toChineseString() + "월" );
		System.out.println( kd.getIljin().toChineseString() + "일" );
	}

	// ### 날짜 계산

	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.METHOD )
	@interface Order {
		int value();
	}
}
