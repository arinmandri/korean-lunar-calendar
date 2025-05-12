package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.chrono.ChronoPeriod;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_until extends KLunarDateTest
{

	@Test
	public void until2 () {
		printTitle( "until 2" );
		// TODO
	}

	@Test
	public void until1 () {
		repeat( this::until1_period0_one, "until 1 - period 0" );
		repeat( this::plusPeriod, "until 1 - add period" );
	}

	private void until1_period0_one () {
		KLunarDate kd = getRaondomKd();
		ChronoPeriod p = kd.until( kd );
		assertTrue( p.isZero() );
	}

	private void plusPeriod () {
		KLunarDate kd1 = getRaondomKd();
		KLunarDate kd2 = getRaondomKd();
		KLunarPeriod p = null;
		try{
			p = kd1.until( kd2 );
			assertEquals( kd2, kd1.plus( p ) );
		}
		catch( Throwable e ){
			System.out.println( "DOOM" );
			System.out.println( kd1 );
			System.out.println( kd2 );
			System.out.println( p );
			throw e;
		}
	}
}
