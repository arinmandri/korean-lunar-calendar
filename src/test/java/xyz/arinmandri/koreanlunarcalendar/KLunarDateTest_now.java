package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

public class KLunarDateTest_now extends KLunarDateTest
{

	@Test
	public void timezone() {
		int offset0 = ZonedDateTime.now(zoneId).getOffset().getTotalSeconds();// 의도한 값: UTC 기준 한국시간대의 offset
		int offset1 = KLunarDate.TIME_ZONE_OFFSET;

		assertEquals(offset0, offset1);
		System.out.println("timezone: " + offset0 + " secs = " + (offset0 / 3600.0) + " hours");
	}

	@Test
	void now() {
		//// 시간대가 잘못 되었거나 시간대 계산을 잘못 했어도 우연히 통과할 수 있을 듯. 임의의 현재시각을 설정하면서 테스트할 수가 있나?

		KLunarDate kd = KLunarDate.now();
		long epochDay_kd = kd.toEpochDay();

		LocalDate ld = LocalDate.now(zoneId);
		long epochDay_ld = ld.toEpochDay();

		assertEquals(epochDay_kd, epochDay_ld);
	}
}
