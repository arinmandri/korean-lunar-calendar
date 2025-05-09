package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest_of
        extends KLunarDateTest
{

	@Test
	public void testYmdl () {

		repeatShortly( this::testYmdl_one, "of ymdl" );
	}

	private void testYmdl_one () {
		final int y = getRandomInt( MIN.getYear(), MAX.getYear() );
		final int m = getRandomInt( 1, 12 );
		final int d = getRandomInt( 1, 30 );

		testYmdl_one( y, m, d );
	}

	private void testYmdl_one ( final int y , final int m , final int d ) {
		/*
		 * 랜덤으로 년월일을 각각 뽑아 음력날짜를 만든다.
		 * 음력날짜에서 양력날짜로 변환한다. KLunarDate.toLocalDate의 양력날짜와 정답의 양력날짜가 일치하나 확인한다.
		 * 정답에서 윤달이 확인되는 경우 윤달 날짜도 생성해서 양력날짜 비교 
		 * 
		 * NonexistentDateException 경우 정답에서도 없는 날짜인지 확인한다.
		 * OutOfRangeException 경우 실패가 아니고 날짜 다시 뽑아서 테스트한다.
		 * 
		 */

		KLunarDate kd1;
		try{
			kd1 = KLunarDate.of( y, m, d );
		}
		catch( OutOfRangeException e ){
			throw new NoNeedToTest();
		}
		catch( NonexistentDateException e ){
			List<Item> items = api.getFromLunDate( y, m, d );
			if( items.size() != 0 )
			    fail( "음력 날짜 " + '-' + i( m ) + '-' + i( d ) + " 이 KLunarDate에서 없는 날짜로 나오지만 KASI 제공 날짜가 확인됩니다." );
			return;
		}

		LocalDate ld1 = kd1.toLocalDate();
		if( ld1.isAfter( MAX ) ){// KASI 지원범위 넘는 날짜 나오면 테스트하지 말자
			throw new NoNeedToTest();
		}

		//// 정답 확인
		List<Item> items = api.getFromLunDate( y, m, d );
		if( items.size() == 0 ){
			fail( "음력 날짜 " + '-' + i( m ) + '-' + i( d ) + " 이 KLunarDate에서 있는 날짜로 나오지만 KASI 제공 날짜가 없습니다." );
			return;
		}
		Item item1 = items.get( 0 );
		if( !checkEquality( item1, ld1 ) ){
			fail( "음력 " + kd1 + " 을를 직접 변환한 양력 날짜 " + ld1 + " 와 KASI 제공 날짜 " + item1.toSolString() + " 이가 다릅니다." );
		}

		//// 윤달 있는 경우 윤달도 확인
		if( items.size() == 2 ){

			KLunarDate kd2;
			try{
				kd2 = KLunarDate.of( y, m, true, d );
			}
			catch( NonexistentDateException e ){
				fail( "음력 날짜 " + '-' + i( m ) + '-' + i( d ) + "L(윤달) 이 KLunarDate에서 없는 날짜로 나오지만 KASI 제공 날짜가 확인됩니다." );
				return;
			}
			LocalDate ld2 = kd2.toLocalDate();

			Item item2 = items.get( 1 );
			if( !checkEquality( item2, ld2 ) ){
				fail( "음력 " + kd2 + " 을를 직접 변환한 양력 날짜 " + ld2 + " 와 KASI 제공 날짜 " + item2.toSolString() + " 이가 다릅니다." );
			}
		}
	}

	@Test
	public void testOfLeapMonths () {
		printTitle( "testOfLeapMonths" );
		int d = 29;

		System.out.println( "윤달 아닌 날짜 정상 생성 확인" );
		for( int y = YEAR_MIN ; y <= YEAR_MAX ; y++ ){
			for( int m = 1 ; m <= 12 ; m++ ){
				checkKdRoundtrip( y, m, false, d );
			}
		}

		System.out.println( "윤달 날짜 정상 생성 확인" );
		int[] leapMonths_y = { 1392, 1395, 1398, 1401, 1403, 1406, 1409, 1411, 1414, 1417, 1420, 1422, 1425, 1428, 1430, 1433, 1436, 1439, 1441, 1444, 1447, 1450, 1452, 1455, 1458, 1460, 1463, 1466, 1469, 1471, 1474, 1477, 1479, 1482, 1485, 1488, 1490, 1493, 1496, 1498, 1501, 1504, 1507, 1509, 1512, 1515, 1517, 1520, 1523, 1525, 1528, 1531, 1534, 1536, 1539, 1542, 1545, 1547, 1550, 1553, 1555, 1558, 1561, 1564, 1566, 1569, 1572, 1574, 1577, 1580, 1583, 1585, 1588, 1591, 1593, 1596, 1599, 1602, 1604, 1607, 1610, 1612, 1615, 1618, 1621, 1623, 1626, 1629, 1631, 1634, 1637, 1640, 1642, 1645, 1648, 1650, 1653, 1656, 1659, 1661, 1664, 1667, 1670, 1672, 1675, 1678, 1680, 1683, 1686, 1689, 1691, 1694, 1697, 1699, 1702, 1705, 1708, 1710, 1713, 1716, 1718, 1721, 1724, 1727, 1729, 1732, 1735, 1737, 1740, 1743, 1746, 1748, 1751, 1754, 1756, 1759, 1762, 1765, 1767, 1770, 1773, 1775, 1778, 1781, 1784, 1786, 1789, 1792, 1795, 1797, 1800, 1803, 1805, 1808, 1811, 1814, 1816, 1819, 1822, 1824, 1827, 1830, 1832, 1835, 1838, 1841, 1843, 1846, 1849, 1851, 1854, 1857, 1860, 1862, 1865, 1868, 1870, 1873, 1876, 1879, 1881, 1884, 1887, 1890, 1892, 1895, 1898, 1900, 1903, 1906, 1909, 1911, 1914, 1917, 1919, 1922, 1925, 1928, 1930, 1933, 1936, 1938, 1941, 1944, 1947, 1949, 1952, 1955, 1957, 1960, 1963, 1966, 1968, 1971, 1974, 1976, 1979, 1982, 1984, 1987, 1990, 1993, 1995, 1998, 2001, 2004, 2006, 2009, 2012, 2014, 2017, 2020, 2023, 2025, 2028, 2031, 2033, 2036, 2039, 2042, 2044, 2047, 2050, };
		int[] leapMonths_m = { 12, 9, 5, 3, 11, 7, 4, 12, 9, 5, 1, 12, 7, 4, 12, 8, 6, 2, 11, 7, 4, 1, 9, 6, 2, 11, 7, 3, 2, 9, 6, 2, 10, 8, 4, 1, 9, 5, 3, 11, 7, 4, 1, 9, 5, 4, 12, 8, 4, 12, 10, 6, 2, 12, 7, 5, 1, 9, 6, 3, 11, 7, 5, 2, 10, 6, 2, 12, 8, 4, 2, 9, 6, 3, 11, 8, 4, 2, 9, 6, 3, 11, 8, 4, 2, 10, 6, 4, 11, 8, 4, 1, 11, 6, 3, 11, 7, 5, 3, 7, 6, 4, 2, 7, 5, 3, 8, 6, 4, 3, 7, 5, 3, 7, 6, 4, 3, 7, 5, 3, 8, 6, 4, 3, 7, 5, 4, 9, 6, 4, 3, 7, 5, 4, 9, 6, 5, 2, 7, 5, 3, 10, 6, 5, 3, 7, 5, 4, 2, 6, 4, 2, 6, 5, 3, 2, 6, 4, 3, 7, 5, 4, 9, 6, 4, 3, 7, 5, 4, 8, 7, 5, 3, 8, 5, 4, 10, 6, 5, 3, 7, 5, 4, 2, 6, 5, 3, 8, 5, 4, 2, 6, 5, 2, 7, 5, 4, 2, 6, 5, 3, 7, 6, 4, 2, 7, 5, 3, 8, 6, 4, 3, 7, 5, 4, 8, 6, 4, 10, 6, 5, 3, 8, 5, 4, 2, 7, 5, 3, 9, 5, 4, 2, 6, 5, 3, 11, 6, 5, 2, 7, 5, 3, };
		if( leapMonths_y.length != leapMonths_m.length ){
			throw new RuntimeException( "윤달 목록 년이랑 월이랑 개수 안 맞음" );
		}

		for( int i = 0 ; i < leapMonths_y.length ; i++ ){
			int y = leapMonths_y[i];
			int m = leapMonths_m[i];
			checkKdRoundtrip( y, m, true, d );
		}

		System.out.println( "윤달 아닌 날짜 비정상 생성 확인" );
		Map<Integer, Integer> leapMonthsMap = new HashMap<>();
		for( int i = 0 ; i < leapMonths_y.length ; i++ ){
			int y = leapMonths_y[i];
			int m = leapMonths_m[i];
			leapMonthsMap.put( y, m );
		}
		for( int y = YEAR_MIN ; y <= YEAR_MAX ; y++ ){
			for( int m = 1 ; m <= 12 ; m++ ){
				final int y1 = y;
				final int m1 = m;
				final Integer m2 = leapMonthsMap.get( y1 );
				final boolean isLeapMonth = m2 != null && m2 == m1;
				if( isLeapMonth ){
					checkKdRoundtrip( y, m, true, d );
				}
				else{
					Exception exception = assertThrows( NonexistentDateException.class, ()-> {
						checkKdRoundtrip( y1, m1, true, d );
					} );
				}
			}
		}
	}
}
