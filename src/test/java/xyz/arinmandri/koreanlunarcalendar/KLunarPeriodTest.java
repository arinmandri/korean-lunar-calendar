package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class KLunarPeriodTest extends ATest
{
	// addTo 테스트는 KLunarDate의 addTo 테스트에 포함. 여기서 따로 할 건 Period끼리 산수밖에 없는 거 같다. 근데 머 기능이나 구현이나 워낙 단순해서 할 건덕지가 있나 싶긴 한데.

	@Test
	public void plus1 () {
		repeat( ()-> {
			KLunarPeriod kp1 = getRandomPeriodF();
			KLunarPeriod kp2 = getRandomPeriodF();
			plusTest( kp1, kp2 );
		}, "KLunarPeriod.plus 1" );
	}

	@Test
	public void plus2 () {
		repeat( ()-> {
			KLunarPeriod kp1 = getRandomPeriodT();
			KLunarPeriod kp2 = getRandomPeriodT();
			plusTest( kp1, kp2 );
		}, "KLunarPeriod.plus 2" );
	}

	@Test
	public void minus1 () {
		repeat( ()-> {
			KLunarPeriod kp1 = getRandomPeriodF();
			KLunarPeriod kp2 = getRandomPeriodF();
			minsTest( kp1, kp2 );
		}, "KLunarPeriod.minus 1" );
	}

	@Test
	public void minus2 () {
		repeat( ()-> {
			KLunarPeriod kp1 = getRandomPeriodT();
			KLunarPeriod kp2 = getRandomPeriodT();
			minsTest( kp1, kp2 );
		}, "KLunarPeriod.minus 2" );
	}

	private void plusTest ( KLunarPeriod kp1 , KLunarPeriod kp2 ) {
		KLunarPeriod kp3 = kp1.plus( kp2 );
		KLunarPeriod kp4 = kp2.plus( kp1 );
		assertEquals( kp3, kp4 );
		assertEquals( kp3.getYears(), kp1.getYears() + kp2.getYears() );
		assertEquals( kp3.getMonths(), kp1.getMonths() + kp2.getMonths() );
		assertEquals( kp3.getDays(), kp1.getDays() + kp2.getDays() );
	}

	private void minsTest ( KLunarPeriod kp1 , KLunarPeriod kp2 ) {
		KLunarPeriod kp3 = kp1.minus( kp2 );
		KLunarPeriod kp4 = kp2.minus( kp1 );
		assertEquals( kp3, kp4.multipliedBy( -1 ) );
		assertEquals( kp3.getYears(), kp1.getYears() - kp2.getYears() );
		assertEquals( kp3.getMonths(), kp1.getMonths() - kp2.getMonths() );
		assertEquals( kp3.getDays(), kp1.getDays() - kp2.getDays() );
	}

	private KLunarPeriod getRandomPeriodF () {
		int y = getRandomInt( -Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2 );
		int m = getRandomInt( -Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2 );
		int d = getRandomInt( -Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2 );
		return KLunarPeriod.of( y, m, false, d );
	}

	private KLunarPeriod getRandomPeriodT () {
		int y = getRandomInt( -Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2 );
		int m = getRandomInt( -Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2 );
		int d = getRandomInt( -Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2 );
		return KLunarPeriod.of( y, m, true, d );
	}
}
