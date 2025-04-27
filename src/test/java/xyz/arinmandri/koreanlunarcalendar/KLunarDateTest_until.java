package xyz.arinmandri.koreanlunarcalendar;

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
	}

	private void until1_period0_one () {
		KLunarDate kd = getRaondomKd();
		ChronoPeriod p = kd.until( kd );
		assertTrue( p.isZero() );
	}
}
