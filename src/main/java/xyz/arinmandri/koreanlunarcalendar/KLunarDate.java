package xyz.arinmandri.koreanlunarcalendar;

import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;


/**
 * 한국 음력으로 특정 날짜를 가리킨다. 불변(immutable)이다.
 * represents a date in Korean lunar calendar system
 * 
 * 음력 날짜는 다음 네 가지 값으로 특정된다: 년, 월, 윤달여부, 일
 */
public final class KLunarDate implements java.io.Serializable , ChronoLocalDate
{
	private static final long serialVersionUID = 1L;

	final int year;
	final int month;
	final int day;
	final boolean isLeapMonth;// 윤달 여부

	private final int c0;// (transient) 해들을 주기 단위로 묶는다. 여기 정의된 묶음들 중 몇 번째 묶음에 속하는가 (0부터 셈)// 주기: 육십갑자에 대응되는 60년. 그냥 갑자라고 하면 60년을 가리키나? 아니면 60갑자는 단순히 순서있는 60가지이고 그걸 년도에 붙일 뿐인가? 모르겠으니까 애매하니까 그냥 별개의 용어 지어 쓴다.
	private final int y0;// (transient) 이 주기의 몇 번째 년인가 (0부터 셈)
	private final int m0;// (transient) 이 년도의 몇 번째 월인가 (0부터 셈)
	private final int d0;// (transient) 이 년도의 몇 번째 일인가 (0부터 셈)

	public static final int TIME_ZONE_OFFSET = 9 * 60 * 60;// UTC 기준 한국 시간대의 초단위 offset

	public static final int BIG_MONTH_SIZE = 30;// 대월의 일수
	public static final int LIL_MONTH_SIZE = 29;// 소월의 일수
	public static final int NAMED_MONTHS_NUMBER_IN_1Y = 12;// 윤달을 빼면 1년의 달 수

	/*
	 * int 하나에 32비트로 한 해의 정보 저장
	 * 왼쪽부터
	 * 15비트: 이 주기의 첫날과 이 년의 첫 날의 날짜 차이 (일 단위)
	 *         yd >>> 17
	 * 4비트: 몇 월에 윤달이 있나 (예: 이 값이 1이면 1월에 윤달이 있으며 2번째 달이 윤달이다.) (0xF인 경우 윤달 없음) (이 값이 0인 경우는 없음)
	 *        int leapMonth = ( yd >>> 13 ) & 0xF // 윤달이 있는 달
	 *        leapMonth == 0xF // 이 해에 윤달 없음
	 *        leapMonth == month // 이 달에 윤달 있음
	 * 13비트: 각 월의 대소(0:소 1:대) (0번째 달부터 12번째 달까지 윤달도 똑같이 한 달로 취급, 1의자리부터 1월)
	 *         if( ( ( yd >>> m0 ) & 0x1 ) == 0x1 ) 대
	 *         if( ( ( yd >>> m0 ) & 0x1 ) == 0x0 ) 소
	 *
	 * 마지막 요소 빼고는 다 크기 60에 맞춰야 함.
	 */
	static final int[][] ydss = {// int yd = ydss[c0][y0]
	        {
	                0x0001ECA6, 0x02C59956, 0x05C5E55A, 0x0889EAD5, 0x0B4F35D4, 0x0E4FE6D4, 0x1113EEC5, 0x13D8AE8A, 0x16D7E68B, 0x199BED27, // 1391
	                0x1C606956, 0x1F5FE55B, 0x22256ADA, 0x2525EB6A, 0x27EBE754, 0x2AAEF745, 0x2DAFEB45, 0x3073EA8B, 0x3336952B, 0x3637E4AD, // 1401
	                0x38FB896D, 0x3BFBE5B5, 0x3EC1EBAA, 0x41873BA4, 0x4487EDA2, 0x474BED45, 0x4A0EBA95, 0x4D0FEA95, 0x4FD3E4AD, 0x52962AAD, // 1411
	                0x5597E6D5, 0x585D8DAA, 0x5B5DEED2, 0x5E23EEA2, 0x60E6FD4A, 0x63E7ED4A, 0x66ABEA96, 0x696E9536, 0x6C6FE55A, 0x6F338AD5, // 1421
	                0x7233E765, 0x74F9E752, 0x77BD0EA5, 0x7ABDE6A5, 0x7D81E54B, 0x8044CA97, 0x8345EAAB, 0x860BE56A, 0x88CE4B55, 0x8BCFEBA9, // 1431
	                0x8E957752, 0x9195ED92, 0x9459EB25, 0x971CF64B, 0x9A1DE54D, 0x9CE1EAAD, 0x9FA6956A, 0xA2A7E5B4, 0xA56BEDA9, 0xA8303D52, // 1441
	        },
	        {
	                0x0001ED92, 0x02C53D25, 0x05C5ED26, 0x0889E956, 0x0B4CCAB5, 0x0E4DEAD6, 0x1113E6D4, 0x13D64EA9, 0x16D7EEC9, 0x199D6E92, // 1451
	                0x1C9BE693, 0x1F5FED27, 0x2224E956, 0x2523E55B, 0x27E9EB5A, 0x2AAE76D4, 0x2DAFE764, 0x3073E749, 0x33365693, 0x3637EA93, // 1461
	                0x38FB352B, 0x3BFBE52D, 0x3EBFE96D, 0x4184CB6A, 0x4485EDAA, 0x474BEBA4, 0x4A0E5B49, 0x4D0FED49, 0x4FD35A95, 0x52D3EA96, // 1471
	                0x5597E52E, 0x585B0AAD, 0x5B5BE6D5, 0x5E21EDCA, 0x60E69DA4, 0x63E7EEA4, 0x66ABED4A, 0x696E3A95, 0x6C6FEA96, 0x6F333556, // 1481
	                0x7233E55A, 0x74F7EAD5, 0x77BCB6D2, 0x7ABDE752, 0x7D81EEA5, 0x80466E4A, 0x8345E54B, 0x86096A9B, 0x8909EAAD, 0x8BCFE56A, // 1491
	                0x8E92EB69, 0x9193EBA9, 0x9459E752, 0x971C9B25, 0x9A1DEB25, 0x9CE1EA4B, 0x9FA42AAB, 0xA2A5EAAD, 0xA56B356A, 0xA86BE5B4, // 1501
	        },
	        {
	                0x0001EDA9, 0x02C6BD92, 0x05C7EE92, 0x088BED25, 0x0B4E9A4D, 0x0E4FE956, 0x11138AB5, 0x1413EADA, 0x16D9E6D4, 0x199D0EA9, // 1511
	                0x1C9DEF49, 0x1F63EE92, 0x22268D26, 0x2525E52B, 0x27E98A57, 0x2AE9E96B, 0x2DAFEB6A, 0x307556D4, 0x3375E764, 0x3639E749, // 1521
	                0x38FCD693, 0x3BFDEA93, 0x3EC1E52B, 0x41844A5B, 0x4485E9AD, 0x474B8B6A, 0x4A4BEDAA, 0x4D11EBA4, 0x4FD4FB49, 0x52D5ED49, // 1531
	                0x5599EA95, 0x585CB52D, 0x5B5DE536, 0x5E21EAB5, 0x60E62DAA, 0x63E7EDD2, 0x66AD3DA4, 0x69ADEEA4, 0x6C71ED4A, 0x6F34CA95, // 1541
	                0x7233EA97, 0x74F9E556, 0x77BC6AB5, 0x7ABDEAD5, 0x7D8376D2, 0x8083E752, 0x8347EEA5, 0x860CF64A, 0x890BE64B, 0x8BCFEA9B, // 1551
	                0x8E94B55A, 0x9195E56A, 0x9459EB69, 0x971E5752, 0x9A1FE752, 0x9CE35B25, 0x9FE3EB25, 0xA2A7EA4B, 0xA56AD2AB, 0xA86BEAAD, // 1561
	        },
	        {
	                0x0001E5AC, 0x02C44BA9, 0x05C5EDA9, 0x088B9D92, 0x0B8BEE92, 0x0E4FED25, 0x11131A4D, 0x1413EA56, 0x16D7E2B6, 0x199A95B5, // 1571
	                0x1C9DE6D4, 0x1F61EEC9, 0x22265E92, 0x2527EE92, 0x27EB2D26, 0x2AE9E52B, 0x2DADEA57, 0x3072D2D6, 0x3373EB6A, 0x3639E6D4, // 1581
	                0x38FC6F49, 0x3BFDE749, 0x3EC17693, 0x41C1EA95, 0x4485E52B, 0x47490A5B, 0x4A49EAAD, 0x4D0FED6A, 0x4FD49B64, 0x52D5EBA4, // 1591
	                0x5599EB49, 0x585C5A93, 0x5B5DEA95, 0x5E21352D, 0x6121E556, 0x63E5EAB5, 0x66AAD5AA, 0x69ABEDD2, 0x6C71EDA4, 0x6F347D4A, // 1601
	                0x7235ED4A, 0x74F96A95, 0x77F7EA97, 0x7ABDE556, 0x7D810AB5, 0x8081EAD9, 0x8347E6D2, 0x860A8EA5, 0x890BEF25, 0x8BD1E64A, // 1611
	                0x8E924C97, 0x9193EAAB, 0x9459555A, 0x9759E56A, 0x9A1DEB69, 0x9CE2D752, 0x9FE3E792, 0xA2A7EB25, 0xA56A964B, 0xA86BEA4B, // 1621
	        },
	        {
	                0x000174AB, 0x0301E2AD, 0x05C5E5AD, 0x088B0BA9, 0x0B8BEDA9, 0x0E51ED92, 0x11149D25, 0x1415ED25, 0x16D9EA55, 0x199C34AD, // 1631
	                0x1C9DE2B6, 0x1F6175B5, 0x2263E6D4, 0x2527EEC9, 0x27ECDE92, 0x2AEDEE92, 0x2DB1ED26, 0x30746A56, 0x3373EA5B, 0x363972D6, // 1641
	                0x3939EB6A, 0x3BFFE754, 0x3EC2EF49, 0x41C3E749, 0x4487E693, 0x474AB52B, 0x4A4BE52B, 0x4D0FEA5B, 0x4FD4755A, 0x52D5E56A, // 1651
	                0x5598EB65, 0x5899EBA5, 0x5B5FEB49, 0x5E22DA95, 0x6123EA95, 0x63E7E52D, 0x66AA8AAD, 0x69ABEAB5, 0x6C71E5AA, 0x6F344BA5, // 1661
	                0x7235EDA5, 0x74FAFD4A, 0x77FBED4A, 0x7ABFEC96, 0x7D82B52E, 0x8083E556, 0x8347EAB5, 0x860C75B2, 0x890DE6D2, 0x8BD10EA5, // 1671
	                0x8ED1E725, 0x9195E64B, 0x9458CC97, 0x9759E4AB, 0x9A1DE55B, 0x9CE28AD6, 0x9FE3EB6A, 0xA2A9E752, 0xA56C7725, 0xA86DEB25, // 1681
	        },
	        {
	                0x0000FA4B, 0x0301EA4D, 0x05C5E4AB, 0x0888A56B, 0x0B89E5AD, 0x0E4FEBAA, 0x11147B52, 0x1415ED92, 0x16D8FD26, 0x19D9ED25, // 1691
	                0x1C9DEA55, 0x1F60D4AD, 0x2261E4B6, 0x2525E5B5, 0x27EA8DAA, 0x2AEBEEC9, 0x2DB1EE92, 0x30747D26, 0x3375EB26, 0x3638EA56, // 1701
	                0x3937EA5B, 0x3BFDE55A, 0x3EC0A6D5, 0x41C1E755, 0x4487E749, 0x474A6E93, 0x4A4BE693, 0x4D0F152B, 0x500FE52B, 0x52D3EAAB, // 1711
	                0x5598D55A, 0x5899E56A, 0x5B5DEB65, 0x5E22974A, 0x6123EB4A, 0x63E7EA95, 0x66AA752B, 0x69ABE52D, 0x6C6EEAAD, 0x6F6FEAB5, // 1721
	                0x7235E5AA, 0x74F8ABA5, 0x77F9EDA5, 0x7ABFED4A, 0x7D829D15, 0x8083EC96, 0x83473956, 0x8647E556, 0x890BEAD5, 0x8BD0D5B2, // 1731
	                0x8ED1E6D2, 0x9195EEA5, 0x945A8E8A, 0x9759E68B, 0x9A1DEC97, 0x9CE26956, 0x9FE1E55B, 0xA2A6EADA, 0xA5A7EB6A, 0xA86DE752, // 1741
	        },
	        {
	                0x0000B725, 0x0301EB45, 0x05C5EA8B, 0x088894AB, 0x0B89E4AD, 0x0E4D296B, 0x114DE5B5, 0x1413EBAA, 0x16D8DB54, 0x19D9EDA2, // 1751
	                0x1C9DED45, 0x1F60BA8D, 0x2261EA95, 0x2525E4AD, 0x27E849AD, 0x2AE9E6B5, 0x2DAEEDAA, 0x30AFEECA, 0x3375EEA2, 0x3638BD46, // 1761
	                0x3939ED4A, 0x3BFDEA96, 0x3EC07536, 0x41C1E55A, 0x44854AD5, 0x4785EB65, 0x4A4BE752, 0x4D0ECEA5, 0x500FE6A5, 0x52D3E54B, // 1771
	                0x5596AA97, 0x5897EAAB, 0x5B5DE55A, 0x5E206AD5, 0x6121EB65, 0x63E6F752, 0x66E7ED52, 0x69ABEB15, 0x6C6EB54B, 0x6F6FE54D, // 1781
	                0x7233EAAD, 0x74F8956A, 0x77F9E5B2, 0x7ABDEBA9, 0x7D825D52, 0x8083ED92, 0x8346DD15, 0x8647ED26, 0x890BE956, 0x8BCE8AAD, // 1791
	                0x8ECFEAD6, 0x9195E5D4, 0x94584DA9, 0x9759EEC9, 0x9A1ECE8A, 0x9D1DE68B, 0x9FE1ED27, 0xA2A6A956, 0xA5A5E95B, 0xA86BEADA, // 1801
	        },
	        {
	                0x000076D4, 0x0301E754, 0x05C5E745, 0x0888568B, 0x0B89EA93, 0x0E4CD52B, 0x114DE4AD, 0x1411E96D, 0x16D68B6A, 0x19D7EBAA, // 1811
	                0x1C9DEBA4, 0x1F607B45, 0x2261ED45, 0x2524FA95, 0x2825EA95, 0x2AE9E52D, 0x2DACAAAD, 0x30ADEAB5, 0x3373EDAA, 0x36389DA4, // 1821
	                0x3939EEA4, 0x3BFD3D4A, 0x3EFDED4A, 0x41C1EA96, 0x4484D536, 0x4785E55A, 0x4A49EAD5, 0x4D0E96CA, 0x500FE752, 0x52D3EEA5, // 1831
	                0x55986D4A, 0x5897E54B, 0x5B5AEA97, 0x5E5BEAAB, 0x6121E55A, 0x63E4AB55, 0x66E5EBA9, 0x69ABE752, 0x6C6E9AA5, 0x6F6FEB25, // 1841
	                0x72331A4B, 0x7533E94D, 0x77F7EAAD, 0x7ABCF56A, 0x7DBDE5B4, 0x8081EBA9, 0x8346BD52, 0x8647ED92, 0x890BED25, 0x8BCE7A4D, // 1851
	                0x8ECFE956, 0x919312B5, 0x9493EAD6, 0x9759E6D4, 0x9A1CADA9, 0x9D1DEEC9, 0x9FE3EE92, 0xA2A68D26, 0xA5A5E527, 0xA8694A57, // 1861
	        },
	        {
	                0x0001E95B, 0x02C7EB5A, 0x058CD6D4, 0x088DE754, 0x0B51E749, 0x0E14B693, 0x1115EA93, 0x13D9E52B, 0x169C6A5B, 0x199DE96D, // 1871
	                0x1C62EB6A, 0x1F63EDAA, 0x2229EBA4, 0x24ECBB49, 0x27EDED49, 0x2AB1EA95, 0x2D74952B, 0x3075E52D, 0x3339EAAD, 0x35FE556A, // 1881
	                0x38FFEDAA, 0x3BC4DDA4, 0x3EC5EEA4, 0x4189ED4A, 0x444CAA95, 0x474BEA97, 0x4A11E556, 0x4CD46AB5, 0x4FD5EAD5, 0x529B16D2, // 1891
	                0x559BE752, 0x585FEEA5, 0x5B24B64A, 0x5E23E64B, 0x60E7EA9B, 0x63AC9556, 0x66ADE56A, 0x6971EB59, 0x6C365752, 0x6F37E752, // 1901
	                0x71FADB25, 0x74FBEB25, 0x77BFEA4B, 0x7A82B29B, 0x7D83EAAD, 0x8049E56A, 0x830C4B69, 0x860DEBA9, 0x88D2FB52, 0x8BD3ED92, // 1911
	                0x8E97ED25, 0x915ABA4D, 0x945BE956, 0x971FE2B5, 0x99E295AD, 0x9CE5E6D4, 0x9FA9EDA9, 0xA26E5D92, 0xA56FEE92, 0xA832CD26, // 1921
	        },
	        {
	                0x0001E527, 0x02C5EA57, 0x058AB2B6, 0x088BEADA, 0x0B51E6D4, 0x0E146EA9, 0x1115E749, 0x13D8F693, 0x16D9EA93, 0x199DE52B, // 1931
	                0x1C60CA5B, 0x1F61E96D, 0x2227EB6A, 0x24EC9B54, 0x27EDEBA4, 0x2AB1EB49, 0x2D745A93, 0x3075EA95, 0x3338F52B, 0x3639E52D, // 1941
	                0x38FDEAAD, 0x3BC2B56A, 0x3EC3EDB2, 0x4189EDA4, 0x444C7D49, 0x474DED4A, 0x4A111A95, 0x4D11EA96, 0x4FD5E556, 0x5298CAB5, // 1951
	                0x5599EAD5, 0x585FE6D2, 0x5B228EA5, 0x5E23EEA5, 0x60E9EE4A, 0x63AC6C96, 0x66ABEA9B, 0x6970F556, 0x6C71E56A, 0x6F35EB59, // 1961
	                0x71FAB752, 0x74FBE752, 0x77BFE725, 0x7A82964B, 0x7D83EA4B, 0x804712AB, 0x8347E2AD, 0x860BE56B, 0x88D0CB69, 0x8BD1EDA9, // 1971
	                0x8E97ED92, 0x915A9B25, 0x945BED25, 0x971F5A4D, 0x9A1FEA56, 0x9CE3E2B6, 0x9FA6D5AD, 0xA2A9E6D4, 0xA56DEDA9, 0xA832BD92, // 1981
	        },
	        {
	                0x0001EE92, 0x02C5ED26, 0x05886A56, 0x0887EA57, 0x0B4D12B6, 0x0E4DEB5A, 0x1113E6D4, 0x13D6AEC9, 0x16D7E749, 0x199BE693, // 1991
	                0x1C5E9527, 0x1F5FE52B, 0x2223EA5B, 0x24E8555A, 0x27E9E36A, 0x2AACFB55, 0x2DAFEBA4, 0x3073EB49, 0x3336BA93, 0x3637EA95, // 2001
	                0x38FBE52D, 0x3BBE6A5D, 0x3EBFEAAD, 0x418535AA, 0x4485E5D2, 0x4749EDA5, 0x4A0EBD4A, 0x4D0FED4A, 0x4FD3EA95, 0x5296952D, // 2011
	                0x5597E556, 0x585BEAB5, 0x5B2055AA, 0x5E21E6D2, 0x60E4CEA5, 0x63E5EEA5, 0x66ABEE4A, 0x696EAC96, 0x6C6DEC9B, 0x6F33E55A, // 2021
	                0x71F66AD5, 0x74F7EB69, 0x77BD7752, 0x7ABDE752, 0x7D81EB25, 0x8044D64B, 0x8345EA4B, 0x8609E4AB, 0x88CCA55B, 0x8BCDE56D, // 2031
	                0x8E93EB69, 0x91585B52, 0x9459ED92, 0x971CFD25, 0x9A1DED25, 0x9CE1EA4D, 0x9FA4B4AD, 0xA2A5E2B6, 0xA569E5B5, 0xA82E6DA9, // 2041
	        },
	};

	/*
	 * 각 주기 첫날의 epoch day
	 */
	static final int[] epochDays = {
	        -211432,// 1391
	        -189520,// 1451
	        -167609,// 1511
	        -145697,// 1571
	        -123786,// 1631
	        -101874,// 1691
	        -79962,// 1751
	        -58050,// 1811
	        -36110,// 1871
	        -14198,// 1931
	        7715,// 1991
	        29626,// 마지막 값은 지원범위 판별용// 음력 2050-12-29의 다음 날에 해당. 한국천문연구원 API에서 지원범위의 막날은 2050-11-18이지만 2050-11이 대월, 30일까지 있음은 알 수 있으므로 이 라이브러리의 지원범위는 2050-12-29까지는 늘려짐.(이 해에 윤달이 이미 나왔고 그 이전의 두 달이 대월이므로 2050-12-29의 다음 날은 아마 2051-01-01일 거 같지만)
	};

	public static final int YEAR_MIN = 1391;// 최소년도
	public static final int YEAR_MAX = YEAR_MIN + ( ydss.length - 1 ) * CYCLE_SIZE + ydss[ydss.length - 1].length - 1;// 최대년도
	public static final int EPOCHDAY_MIN = epochDays[0];
	public static final int EPOCHDAY_MAX = epochDays[epochDays.length - 1] - 1;
	static final int PROLEPTIC_MONTH_MIN = 17205;// 최소년도(1391년) 첫 달의 PROLEPTIC_MONTH 값. 19년에 윤달 7개 규칙으로 추정함.
	static final int PROLEPTIC_MONTH_MAX = 25367;

	private KLunarDate( int year , int month , int day , boolean isLeapMonth , int c0 , int y0 , int m0 , int d0 ) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.isLeapMonth = isLeapMonth;

		this.c0 = c0;
		this.y0 = y0;
		this.m0 = m0;
		this.d0 = d0;
	}

	//// ================================ CREATION

	public static KLunarDate now () {
		long epochDay = ( System.currentTimeMillis() / 1000 + TIME_ZONE_OFFSET ) / ( 24 * 60 * 60 );
		return ofEpochDay( epochDay );
	}

	/**
	 * 음력 년월일로 새 날짜개체 생성
	 *
	 * @param year        년도
	 * @param month       월
	 * @param day         일
	 * @param isLeapMonth 윤달 여부
	 * 
	 * @return 생성된 새 날짜 개체
	 * 
	 * @throws NonexistentDateException 해당 날짜 없음
	 * @throws OutOfRangeException      지원 범위 밖
	 */
	public static KLunarDate of ( int year , int month , boolean isLeapMonth , int day ) {

		if( year < YEAR_MIN ) throw new OutOfRangeException();
		if( month < 1 ) throw new NonexistentDateException();
		if( month > 12 ) throw new NonexistentDateException();
		if( day < 1 ) throw new NonexistentDateException();

		//// 몇번째 년도?
		int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
		if( c0 >= ydss.length ) throw new OutOfRangeException();
		if( y0 >= ydss[c0].length ) throw new OutOfRangeException();
		int yd = ydss[c0][y0];

		//// 몇번째 월인지 찾기
		int m0;
		int leapMonth = ( yd >>> 13 ) & 0xF;
		boolean hasLeapMonth = leapMonth < month;
		if( isLeapMonth ){
			if( leapMonth != month )
			    throw new NonexistentDateException( "No leap month in " + year + '-' + month );
			m0 = month + ( hasLeapMonth ? 1 : 0 );
		}
		else{
			m0 = month - 1 + ( hasLeapMonth ? 1 : 0 );
		}

		int monthSize = ( ( yd >> m0 ) & 0x1 ) == 0x1
		        ? BIG_MONTH_SIZE
		        : LIL_MONTH_SIZE;

		//// 이 해의 몇번째 날인지 찾기
		if( day > monthSize ) throw new NonexistentDateException( "The size of " + year + '-' + month + " is smaller than " + day );
		int d0 = 0;
		for( int i = 0 ; i < m0 ; i++ ){// 이전월까지의 일수 합
			d0 += ( ( yd >>> i ) & 0x1 ) == 1
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;
		}
		d0 += day - 1;// 이번 월의 몇번째 날? (0부터 셈)

		return new KLunarDate( year , month , day , isLeapMonth , c0 , y0 , m0 , d0 );
	}

	/**
	 * 음력 년월일로 새 날짜개체(윤달 아님) 생성
	 *
	 * @param year  년도
	 * @param month 월
	 * @param day   일
	 * 
	 * @return 생성된 새 날짜 개체
	 * 
	 * @throws NonexistentDateException 해당 날짜 없음
	 * @throws OutOfRangeException      지원 범위 밖
	 */
	public static KLunarDate of ( int year , int month , int day ) {
		return of( year, month, false, day );
	}

	/**
	 * 어떤 년도의 n번째 날짜
	 *
	 * @param year      년도(음력)
	 * @param dayOfYear 그 년도의 몇 번째 일인가(1부터 셈)
	 * 
	 * @return 생성된 새 날짜 개체
	 * 
	 * @throws NonexistentDateException 해당 날짜 없음
	 * @throws OutOfRangeException      지원 범위 밖
	 */
	public static KLunarDate ofYearDay ( final int year , final int dayOfYear ) {

		if( dayOfYear < 1 ) throw new NonexistentDateException();

		final int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		final int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
		if( c0 >= ydss.length ) throw new OutOfRangeException();
		if( y0 >= ydss[c0].length ) throw new OutOfRangeException();
		final int yd = ydss[c0][y0];

		int dayCount = dayOfYear;

		final int leapMonth = ( yd >>> 13 ) & 0xF;
		int month = 0;
		for( int m0 = 0 ; m0 < 13 ; m0 += 1 ){

			month += leapMonth == m0// 월수 세기: 이번월이 윤달이면 +0, 아니면 +1
			        ? 0
			        : 1;
			int mSize = ( ( yd >>> m0 ) & 0x1 ) == 0x1 // 이번 달의 일수
			        ? BIG_MONTH_SIZE
			        : LIL_MONTH_SIZE;

			if( dayCount <= mSize ){// 세고 남은 날짜 수가 이번달 일수 이하임: 이번달임
				return new KLunarDate( year , month , dayCount , leapMonth == m0 , c0 , y0 , m0 , dayOfYear - 1 );
			}

			dayCount -= mSize;// 세고 남은 일수
		}

		throw new NonexistentDateException( "The number of days of this year is smaller than " + dayOfYear );
	}

	/**
	 * epoch day --> 음력 날짜
	 * 
	 * @param epochDay 1970년 1월 1일을 0으로 하는 누적일수
	 * @return 음력 날짜
	 * @throws OutOfRangeException
	 */
	public static KLunarDate ofEpochDay ( final long epochDay ) {
		if( epochDay < EPOCHDAY_MIN ) throw new OutOfRangeException();
		if( epochDay > EPOCHDAY_MAX ) throw new OutOfRangeException();
		/*
		 * 주기별 epoch day 정보로 해당하는 주기를 찾는다.
		 * 그 주기 내 년도별 적일 정보로 해당하는 음력년도를 찾는다.
		 * 그 년도의 첫 날에 남은 적일 더해서 날짜 결정
		 */

		//// 주기 찾기
		int c0 = ydss.length;
		for( ; c0 >= 0 ; c0 -= 1 ){// 미래에서부터 선형탐색
			if( epochDay >= epochDays[c0] ){
				final int cd = (int) ( epochDay - epochDays[c0] );

				//// 년도 찾기
				int[] yds = ydss[c0];
				int cycleSize = ydss[c0].length;
				int y0 = cd / 385;// 385: 1년의 크기 최대 (윤달있음+대월8개)
				for( ; y0 < cycleSize ; y0 += 1 ){
					if( cd < ( yds[y0] >>> 17 ) ){
						break;
					}
				}
				y0 -= 1;
				return ofYearDay( YEAR_MIN + c0 * CYCLE_SIZE + y0, cd - ( yds[y0] >>> 17 ) + 1 );
			}
		}

		throw new OutOfRangeException();// 지원범위보다 과거
	}

	//// ================================ 변환

	/**
	 * 다른(날짜/시각)값-->음력
	 *
	 * @param temporal TemporalAccessor
	 * 
	 * @return 음력 날짜
	 * 
	 * @throws OutOfRangeException 지원 범위 밖
	 */
	public static KLunarDate from ( final TemporalAccessor temporal ) {
		Objects.requireNonNull( temporal, "temporal" );

		long epochDay = temporal.getLong( ChronoField.EPOCH_DAY );

		return ofEpochDay( epochDay );
	}

	/**
	 * 음력-->양력
	 *
	 * @return 양력 날짜
	 */
	public LocalDate toLocalDate () {

		return LocalDate.ofEpochDay( toEpochDay() );
	}

	public static KLunarDate parse ( CharSequence text ) {
		return null;// TODO
	}

// import java.time.LocalTime;
// import java.time.chrono.ChronoLocalDateTime;
//	@Override
//	public ChronoLocalDateTime<?> atTime ( LocalTime localTime ) {
//		return null;// XXX time:
//	}

	/**
	 * 이 날짜를 epoch day로 환산한다.
	 * Converts this date to the Epoch Day.
	 * <p>
	 * epoch day란 세계표준날짜 1970년 1월 1일으로부터의 경과일수이다.
	 * 즉 양력 1970년 1월 1일은 0 epoch day이고 1970년 1월 2일은 1 epoch day이다.
	 */
	@Override
	public long toEpochDay () {
		return toEpochDayInt();
	}

	/**
	 * 이 날짜를 epoch day로 환산한다.
	 * Converts this date to the Epoch Day.
	 * <p>
	 * epoch day란 세계표준날짜 1970년 1월 1일으로부터의 경과일수이다.
	 * 즉 양력 1970년 1월 1일은 0 epoch day이고 1970년 1월 2일은 1 epoch day이다.
	 */
	public int toEpochDayInt () {
		return epochDays[c0] + ( ydss[c0][y0] >>> 17 ) + d0;
	}

	//// ================================ GETTER

	/**
	 * 지원되는 필드인지 확인한다.
	 * 이 날짜/시간값의 그 필드를 대상으로 쿼리가 가능한지 확인한다.
	 * 
	 * @param field 확인할 필드
	 *              null이면 false를 반환.
	 * @return true if the field can be queried.
	 *         false if not.
	 */
	@Override
	public boolean isSupported ( TemporalField field ) {
		if( field == null ) return false;
		if( field instanceof ChronoField ){
			switch( (ChronoField) field ){
			case DAY_OF_MONTH:
			case DAY_OF_YEAR:
			case EPOCH_DAY:
			case MONTH_OF_YEAR:
			case PROLEPTIC_MONTH:
			case YEAR:
			case YEAR_OF_ERA:
			case ERA:
				return true;
			default:
				return false;
			}
		}
		return field.isSupportedBy( this );
	}

	/**
	 * 특정 필드가 가질 수 있는 유효한 값의 범위를 얻는다.
	 * Gets the range of valid values for the specified field.
	 * <p>
	 * All fields can be expressed as a {@code long} integer.
	 * This method returns an object that describes the valid range for that value.
	 * The value of this temporal object is used to enhance the accuracy of the returned range.
	 * If the date-time cannot return the range,
	 * because the field is unsupported or for some other reason, an exception will be thrown.
	 * 
	 * @param field, not null
	 * @throws DateTimeException                if the range for the field cannot be obtained
	 * @throws UnsupportedTemporalTypeException if the field is not supported
	 */
	@Override
	public ValueRange range ( TemporalField field ) {

		if( field instanceof ChronoField ){
			switch( (ChronoField) field ){
			case DAY_OF_MONTH:
				return ValueRange.of( 1, lengthOfMonth() );
			case DAY_OF_YEAR:
				return ValueRange.of( 1, lengthOfYear() );
			case EPOCH_DAY:
				return ValueRange.of( EPOCHDAY_MIN, EPOCHDAY_MAX );
			case MONTH_OF_YEAR:
				return ValueRange.of( 1, 12 );
			case PROLEPTIC_MONTH:
				return ValueRange.of( PROLEPTIC_MONTH_MIN, PROLEPTIC_MONTH_MAX );
			case YEAR:
			case YEAR_OF_ERA:
				return ValueRange.of( YEAR_MIN, YEAR_MAX );
			case ERA:
				return ValueRange.of( 1, 1 );
			}
			throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
		}
		return field.rangeRefinedBy( this );
	}

	/**
	 * 특정 필드의 값을 {@code int} 꼴로 얻는다.
	 * 
	 * @return 대상 필드
	 */
	@Override
	public int get ( TemporalField field ) {
		if( field == null ) throw new NullPointerException();
		if( field instanceof ChronoField ){
			switch( (ChronoField) field ){
			case DAY_OF_MONTH:
				return getDay();
			case DAY_OF_YEAR:
				return getDayOfYear();
			case EPOCH_DAY:
				return (int) toEpochDay();
			case MONTH_OF_YEAR:
				return getMonth();
			case PROLEPTIC_MONTH:
				return getProlepticMonth();
			case YEAR:
				return year;
			case YEAR_OF_ERA:
				return year;
			case ERA:
				return 1;
			default:
				throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
			}
		}
		return (int) field.getFrom( this );
	}

	/**
	 * 특정 필드의 값을 {@code long} 꼴로 얻는다.
	 * 
	 * @return 대상 필드
	 */
	@Override
	public long getLong ( TemporalField field ) {
		/*
		 * 지원범위 내에서 int 범위를 넘는 속성이 없으니 get에 기능구현하고 getLong에서는 캐스팅만 함.
		 * epoch day만 바로 리턴해줌
		 */
		if( field == ChronoField.EPOCH_DAY )
		    return toEpochDay();

		return get( field );
	}

	/**
	 * 이 날짜가 속한 역법을 확인
	 * 
	 * @return {@link KLunarChronology#INSTANCE}
	 */
	@Override
	public Chronology getChronology () {
		return KLunarChronology.INSTANCE;
	}

	@Override
	public Era getEra () {
		return IsoEra.CE;
	}

	/**
	 * 날짜의 년 부분을 확인
	 * 
	 * @return 날짜의 년 부분
	 */
	public int getYear () {
		return year;
	}

	/**
	 * 날짜의 월 부분을 확인
	 * 
	 * @return 날짜의 월 부분
	 */
	public int getMonth () {
		return month;
	}

	/**
	 * 0년 1월부터의 경과월
	 */
	public int getProlepticMonth () {
		KLunarDate min = of( YEAR_MIN, 1, false, 1 );
		return (int) min.until( this, LunarMonthUnit.LMONTHS ) + PROLEPTIC_MONTH_MIN;
	}

	/**
	 * 날짜의 일 부분을 확인
	 * 
	 * @return 날짜의 일 부분
	 */
	public int getDay () {
		return day;
	}

	/**
	 * 이번 년도의 몇 번째 달인가 (0부터 셈)
	 * 예를 들어 이 해에 윤1월이 있다면 이 해의 1월은 0, 윤1월은 1, 2월은 2, ...
	 */
	public int getMonthOrdinal () {
		return m0;
	}

	/**
	 * 이번 년도의 몇 번째 날인가
	 */
	public int getDayOfYear () {
		return d0 + 1;
	}

	/**
	 * 윤년 여부 (윤달이 포함된 해인 여부)
	 * 고려대 한국어대사전에서 윤년을 "윤달이나 윤일이 드는 해"로 정의하므로 그에 따름.
	 * 그러나 윤년이 아니라고 해서 해의 길이가 일정하지는 않다.
	 * 
	 * @return true 이 해에 윤달 있음
	 *         false 이 해에 윤달 없음
	 */
	@Override
	public boolean isLeapYear () {
		int yd = ydss[c0][y0];
		return ( ( yd >>> 13 ) & 0xF ) == 0xF ? false : true;
	}

	/**
	 * 윤달 여부
	 * 
	 * @return true 윤달
	 *         false 평달
	 */
	public boolean isLeapMonth () {
		return isLeapMonth;
	}

	/**
	 * 몇 월에 윤달이 있는지 반환
	 * 
	 * @return 윤달이 있는 월
	 *         윤달이 없는 해인 경우 0
	 */
	public int getLeapMonth () {
		int yd = ydss[c0][y0];
		int lm = ( yd >>> 13 ) & 0xF;
		return lm == 0xF ? 0 : lm;
	}

	/**
	 * 대월소월
	 * 
	 * @return true 대월
	 *         false 소월
	 */
	public boolean isBigMonth () {
		final int yd = ydss[c0][y0];
		if( ( ( yd >>> m0 ) & 0x1 ) == 0x1 )
		    return true;
		return false;
	}

	/**
	 * 이 날짜의 세차(년의 간지)
	 * 
	 * @return
	 */
	public Ganji getSecha () {
		// 1391년은 신미(辛未)년; 신미는 8번째.
		return Ganji.values()[( y0 + 7 ) % CYCLE_SIZE];
	}

	/**
	 * 이 날짜의 월건(월의 간지)
	 * 5년(=윤달 제외 60개월)마다 같은 월건이 반복되며 윤달은 월건이 없다.
	 * 
	 * @return wolgeon
	 *         null if it is in a leap month
	 */
	public Ganji getWolgeon () {
		if( isLeapMonth ) return null;
		return Ganji.values()[( y0 * 12 + month + 25 ) % CYCLE_SIZE];// 신미(辛未)년 1월은 경인(庚寅)월; 경인은 27번째. // month가 0이 아닌 1부터 시작하므로 +26이 아니라 +25이다.
	}

	/**
	 * 이 날짜의 일진(일의 간지)
	 * 
	 * @return iljin
	 */
	public Ganji getIljin () {
		return Ganji.values()[( ( toEpochDayInt() + 17 ) % CYCLE_SIZE + CYCLE_SIZE ) % CYCLE_SIZE];// 0 epoch day 는 신사(辛巳)일 = O18 // epoch day가 음수일 수도 있어서 a%c 대신 (a%c+c)%c
	}

	/**
	 * 한 달의 길이 (일 단위)
	 */
	@Override
	public int lengthOfMonth () {
		int yd = ydss[c0][y0];
		if( ( ( yd >>> m0 ) & 0x1 ) == 0x1 )
		    return BIG_MONTH_SIZE;
		else
		    return LIL_MONTH_SIZE;
	}

	/**
	 * 1년의 길이 (일 단위)
	 */
	@Override
	public int lengthOfYear () {
		int yd = ydss[c0][y0];

		int count = 0;
		int n = ( ( yd >>> 13 ) & 0xF ) == 0xF ? 12 : 13;
		for( int m = 0 ; m < n ; m++ ){
			if( ( ( yd >>> m ) & 0x1 ) == 0x1 )
			    count += BIG_MONTH_SIZE;
			else
			    count += LIL_MONTH_SIZE;
		}
		return count;
	}

	/**
	 * 1년의 길이 (월 단위)
	 */
	public int lengthOfYearInM () {
		return isLeapYear() ? NAMED_MONTHS_NUMBER_IN_1Y + 1 : NAMED_MONTHS_NUMBER_IN_1Y;
	}

	//// ================================ 셈

	@Override
	public boolean isSupported ( TemporalUnit unit ) {
		if( unit == null ) return false;
		if( unit instanceof ChronoUnit ){
			switch( (ChronoUnit) unit ){
			case DAYS:
			case MONTHS:
			case YEARS:
				return true;
			default:
				return false;
			}
		}
		return unit != null && unit.isSupportedBy( this );
	}

	private static KLunarDate resolvePreviousValid_LD ( final int year , final int month , boolean isLeapMonth , int day ) {
		/*
		 * 윤달 조정
		 * │ 입력값이 평달이면 윤달 조정 할 거 없음
		 * └ 입력값으로 윤달이 없는 달이 지정된 경우 평달로 조정
		 * 그 뒤에 일 조정
		 */

		if( !isLeapMonth ){
			return resolvePreviousValid_D( year, month, isLeapMonth, day );
		}

		final int c0 = ( year - YEAR_MIN ) / CYCLE_SIZE;
		final int y0 = ( year - YEAR_MIN ) % CYCLE_SIZE;
		final int yd = ydss[c0][y0];
		final int leapMonth = ( yd >>> 13 ) & 0xF;
		if( leapMonth == month ){// 윤달이 있는 달
			return resolvePreviousValid_D( year, month, isLeapMonth, day );
		}
		return resolvePreviousValid_D( year, month, false, day );
	}

	private static KLunarDate resolvePreviousValid_D ( int year , int month , boolean isLeapMonth , int day ) {
		/*
		 * 일 조정
		 * 지정된 달이 소월인데 30일이면 29일로 조정
		 */
		if( day > LIL_MONTH_SIZE ){
			KLunarDate withDay29 = of( year, month, isLeapMonth, LIL_MONTH_SIZE );
			if( withDay29.isBigMonth() ){
				return of( year, month, isLeapMonth, day );
			}
			else{
				return withDay29;
			}
		}
		return of( year, month, isLeapMonth, day );
	}

	private KLunarDate withDayResolvedPreviousValid ( int day ) {
		if( day == this.day )
		    return this;

		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	//// ================================ 셈 - with

	/**
	 * 지정한 필드 값을 바꾼 KLunarDate 개체를 반환.
	 * 
	 * @param field    조정할 필드 not null
	 * @param newValue 그 필드에 새로 넣을 값
	 * 
	 * @return 값 조정된 개체 not null
	 * 
	 * @throws DateTimeException                if the field cannot be set
	 * @throws UnsupportedTemporalTypeException if the field is not supported
	 */
	@Override
	public KLunarDate with ( TemporalField field , long newValue ) {

		if( field instanceof ChronoField ){
			ChronoField chronoField = (ChronoField) field;
			chronoField.checkValidValue( newValue );// 뭐든지 int 범위를 안 벗어난다
			switch( chronoField ){
			case DAY_OF_MONTH:
				return withDay( (int) newValue );
			case DAY_OF_YEAR:
				return withDayOfYear( (int) newValue );
			case EPOCH_DAY:
				return KLunarDate.ofEpochDay( newValue );
			case MONTH_OF_YEAR:
				return withMonth( (int) newValue );
			case PROLEPTIC_MONTH:
				return plusMonths( (int) newValue - getProlepticMonth() );
			case YEAR:
			case YEAR_OF_ERA:
				return withYear( (int) newValue );
			case ERA:
				if( getLong( ChronoField.ERA ) == newValue )
				    return this;
				else
				    throw new OutOfRangeException();
			default:
				throw new UnsupportedTemporalTypeException( "Unsupported field: " + field );
			}
		}
		return field.adjustInto( this, newValue );
	}

	public KLunarDate withYear ( int year ) {
		if( year == this.year )
		    return this;
		return resolvePreviousValid_LD( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int year , int month ) {
		if( year == this.year && month == this.month )
		    return this;
		return resolvePreviousValid_LD( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int year , int month , boolean isLeapMonth ) {
		if( year == this.year && month == this.month && isLeapMonth == this.isLeapMonth )
		    return this;
		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int month ) {
		if( month == this.month )
		    return this;
		return resolvePreviousValid_LD( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonth ( int month , boolean isLeapMonth ) {
		if( month == this.month && isLeapMonth == this.isLeapMonth )
		    return this;
		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	public KLunarDate withMonthLeap ( boolean isLeapMonth ) {
		if( isLeapMonth == this.isLeapMonth )
		    return this;
		return resolvePreviousValid_D( year, month, isLeapMonth, day );
	}

	public KLunarDate withDay ( int day ) {
		if( day == this.day )
		    return this;
		return of( year, month, isLeapMonth, day );
	}

	public KLunarDate withDayOfYear ( int dayOfYear ) {
		return ofYearDay( year, dayOfYear );
	}

	public KLunarDate withSecha ( Ganji ganji ) {
		if( ganji == this.getSecha() )
		    return this;
		int diff = ganji.ordinal() - this.getSecha().ordinal();
		return plusYears( diff );
	}

	public KLunarDate withWolgeon ( Ganji ganji ) {
		if( ganji == this.getWolgeon() )
		    return this;
		int diff = ganji.ordinal() - this.getWolgeon().ordinal();
		return plusNamedMonths( diff );
	}

	public KLunarDate withIljin ( Ganji ganji ) {
		if( ganji == this.getIljin() )
		    return this;
		int diff = ganji.ordinal() - this.getIljin().ordinal();
		return plusDays( diff );
	}

	//// ================================ 셈 - plus, minus

	@Override
	public KLunarDate plus ( long amountToAdd , TemporalUnit unit ) {
		if( amountToAdd > Integer.MAX_VALUE || amountToAdd < Integer.MIN_VALUE )
		    throw new OutOfRangeException();

		int amountToAddInt = (int) amountToAdd;

		if( unit instanceof ChronoUnit ){
			ChronoUnit chronoUnit = (ChronoUnit) unit;
			switch( chronoUnit ){
			case DAYS:
				return plusDays( amountToAddInt );
			case MONTHS:
				return plusMonths( amountToAddInt );
			case YEARS:
				return plusYears( amountToAddInt );
			case DECADES:
				return plusYears( Math.multiplyExact( amountToAddInt, 10 ) );
			case CENTURIES:
				return plusYears( Math.multiplyExact( amountToAddInt, 100 ) );
			case MILLENNIA:
				return plusYears( Math.multiplyExact( amountToAddInt, 1000 ) );
			case ERAS:
				if( amountToAdd != 0L )
				    throw new OutOfRangeException();
				return this;
			default:
				throw new UnsupportedTemporalTypeException( "Unsupported unit: " + unit );
			}
		}

		return unit.addTo( this, amountToAdd );
	}

	@Override
	public KLunarDate minus ( long amountToSubtract , TemporalUnit unit ) {
		return plus( -amountToSubtract, unit );
	}

	/**
	 * n년 뒤.
	 *
	 * @param n 몇년 뒤의 날짜?
	 * 
	 * @return n년 뒤의 날짜.
	 *         null : 그런 날짜가 없으면 (예: 이년도 1월이 대월이고 1월 30일에서 plusYears(n) 했는데 n년 후 1월이 소월이라 1월 30일이 없는 경우)
	 * 
	 * @throws OutOfRangeException 지원 범위 내에 그런 날짜가 없으면
	 */
	public KLunarDate plusHardYears ( int n ) throws OutOfRangeException {
		try{
			return of( year + n, month, isLeapMonth, day );
		}
		catch( NonexistentDateException e ){
			return null;
		}
	}

	/**
	 * n년 뒤.
	 * 윤달에서 윤달 없는 년도로 가면 평달로 조정.
	 * 대월 30일에서 소월로 가면 29일로 조정.
	 * 
	 * @param n
	 * @return n년 뒤의 날짜
	 */
	public KLunarDate plusYears ( int n ) {
		if( n == 0 ) return this;
		return resolvePreviousValid_LD( year + n, month, isLeapMonth, day );
	}

	/**
	 * 월, 일이 같은 다음 날짜
	 *
	 * @return 다음 날짜. 예: 2005년 1월 1일 --> 2006년 1월 1일
	 *         null: 계산할 수 있는 범위 내에 같은 날짜가 없음.
	 *         예: 그 뒤로 윤 11월 30일이 다시는 나타나지 않았음.
	 */
	public KLunarDate nextYear () {
		int max = YEAR_MAX - YEAR_MIN;
		for( int n = 1 ; n < max ; n++ ){
			KLunarDate kld;
			try{
				kld = plusHardYears( n );
				if( kld == null )
				    continue;
			}
			catch( OutOfRangeException e ){
				return null;
			}
			return kld;
		}
		return null;
	}

	/**
	 * n년 앞.
	 * 윤달에서 윤달 없는 년도로 가면 평달로 조정.
	 * 대월 30일에서 소월로 가면 29일로 조정.
	 * 
	 * @param n
	 * @return n년 앞의 날짜
	 */
	public KLunarDate minusYears ( int n ) {
		if( n == 0 ) return this;
		return resolvePreviousValid_LD( year - n, month, isLeapMonth, day );
	}

	/**
	 * n달 뒤.
	 * 대월 30일에서 소월로 가면 29일로 조정.
	 * 
	 * @param n
	 * @return n개월 뒤의 날짜
	 */
	public KLunarDate plusMonths ( int n ) {
		if( n == 0 ) return this;

		/*
		 * 달을 정확히 하나씩 세는 게 아니라 달의 평균 크기를 이용하여 점프한 다음 일자를 재조정한다.
		 * 지원범위 경계값으로 가는 경우에는 일자를 재조정하기 전의 중간결과가 범위를 벗어나는 경우가 있어서
		 * 범위를 조금만 벗어나는 경우에는 범위 내로 맞춤.
		 */
		double mAvgLength = LunarMonthUnit.LMONTHS.getDurationInDays();

		long epochDay = toEpochDay() + (long) ( n * mAvgLength );
		if( epochDay < EPOCHDAY_MIN && epochDay > EPOCHDAY_MIN - ( LIL_MONTH_SIZE / 2 ) )
		    epochDay = EPOCHDAY_MIN;
		if( epochDay > EPOCHDAY_MAX && epochDay < EPOCHDAY_MAX + ( LIL_MONTH_SIZE / 2 ) )
		    epochDay = EPOCHDAY_MAX;
		return resolveClosestDayOfMonth( ofEpochDay( epochDay ), day );
	}

	/**
	 * n달 뒤. 단, 윤달은 안 세고 건너뜀.
	 * 대월 30일에서 소월로 가면 29일로 조정.
	 * 
	 * @param n 넘어갈 개월 수
	 * @return 윤달을 무시한 n개월 뒤의 날짜
	 */
	public KLunarDate plusNamedMonths ( int n ) {
		if( n < 0 )
		    return minusNamedMonths( -n );
		if( n == 0 )
		    return this;

		int y = year;
		int m0 = month - 1 + n;
		y += m0 / NAMED_MONTHS_NUMBER_IN_1Y;
		m0 %= NAMED_MONTHS_NUMBER_IN_1Y;
		return resolvePreviousValid_LD( y, m0 + 1, isLeapMonth, day );
	}

	/**
	 * n달 앞.
	 * 대월 30일에서 소월로 가면 29일로 조정.
	 * 
	 * @param n
	 * @return n년 앞의 날짜
	 */
	public KLunarDate minusMonths ( int n ) {
		if( n == 0 ) return this;

		/*
		 * plusMonths 참고.
		 */
		double mAvgLength = LunarMonthUnit.LMONTHS.getDurationInDays();

		long epochDay = toEpochDay() - (long) ( n * mAvgLength );
		if( epochDay < EPOCHDAY_MIN && epochDay > EPOCHDAY_MIN - ( LIL_MONTH_SIZE / 2 ) )
		    epochDay = EPOCHDAY_MIN;
		if( epochDay > EPOCHDAY_MAX && epochDay < EPOCHDAY_MAX + ( LIL_MONTH_SIZE / 2 ) )
		    epochDay = EPOCHDAY_MAX;
		return resolveClosestDayOfMonth( ofEpochDay( epochDay ), day );
	}

	/**
	 * n달 앞. 단, 윤달은 안 세고 건너뜀.
	 * 대월 30일에서 소월로 가면 29일로 조정.
	 * 
	 * @param n 넘어갈 개월 수
	 * @return 윤달을 무시한 n개월 앞의 날짜
	 */
	public KLunarDate minusNamedMonths ( int n ) {
		if( n < 0 )
		    return plusNamedMonths( -n );
		if( n == 0 )
		    return this;

		int y = year;
		int m = month - n;
		if( m > 0 ){
			return resolvePreviousValid_LD( y, m, isLeapMonth, day );
		}
		y += m / NAMED_MONTHS_NUMBER_IN_1Y - 1;
		m = m % NAMED_MONTHS_NUMBER_IN_1Y + NAMED_MONTHS_NUMBER_IN_1Y;
		return resolvePreviousValid_LD( y, m, isLeapMonth, day );
	}

	private KLunarDate resolveClosestDayOfMonth ( KLunarDate kd , int day ) {
		int kdd = kd.getDay();
		if( kdd == day )
		    return kd;

		int diff = day - kdd;
		if( diff < -LIL_MONTH_SIZE / 2 ){
			return kd.nextMonth().withDayResolvedPreviousValid( day );
		}
		if( LIL_MONTH_SIZE / 2 < diff ){
			return kd.prevMonth().withDayResolvedPreviousValid( day );
		}
		return resolvePreviousValid_D( kd.year, kd.month, kd.isLeapMonth, day );
	}

	/**
	 * 다음달.
	 * 이 달이 30일인데 다음달이 소월이면 29일로 조정.
	 * 
	 * @return 다음달
	 */
	public KLunarDate nextMonth () {
		int yd = ydss[c0][y0];
		int leapMonth = ( yd >>> 13 ) & 0xF;

		//// 이 달이 올해의 마지막 달인가?
		boolean thisIsLastMonthOfYear;{
			if( month < 12 ){
				thisIsLastMonthOfYear = false;
			}
			else{// 12월임
				if( leapMonth == 12 && !isLeapMonth )// 올해 윤12월 있고 이 날짜는 평달임
				    thisIsLastMonthOfYear = false;
				else
				    thisIsLastMonthOfYear = true;
			}
		}

		if( thisIsLastMonthOfYear ){
			return resolvePreviousValid_D( year + 1, 1, false, day );
		}
		else{
			if( isLeapMonth || leapMonth != month )
			    return resolvePreviousValid_D( year, month + 1, false, day );
			else
			    return resolvePreviousValid_D( year, month, true, day );
		}
	}

	/**
	 * 저번달.
	 * 이 달이 30일인데 저번달이 소월이면 29일로 조정.
	 * 
	 * @return 저번달
	 */
	public KLunarDate prevMonth () {
		if( isLeapMonth ){
			return resolvePreviousValid_D( year, month, false, day );
		}

		if( m0 == 0 ){// 이 달이 올해의 첫 달
			int yd; {// 작년의 yd
				if( y0 > 0 )
				    yd = ydss[c0][y0 - 1];
				else if( c0 > 0 )
				    yd = ydss[c0 - 1][ydss[c0 - 1].length - 1];
				else
				    throw new OutOfRangeException();
			}
			int leapMonth = ( yd >>> 13 ) & 0xF;
			if( leapMonth == 12 )
			    return resolvePreviousValid_D( year - 1, 12, true, day );
			else
			    return resolvePreviousValid_D( year - 1, 12, false, day );
		}
		else{
			return resolvePreviousValid_LD( year, month - 1, true, day );
		}
	}

	/**
	 * n일 뒤
	 * 
	 * @param n
	 * @return
	 */
	public KLunarDate plusDays ( int n ) {
		return ofEpochDay( toEpochDayInt() + n );// 여기 다들 범위가 int 한참 아래랍니다.
	}

	/**
	 * n일 전
	 * 
	 * @param n
	 * @return
	 */
	public KLunarDate minusDays ( int n ) {
		return ofEpochDay( toEpochDayInt() - n );
	}

	//// ================================ 셈 - until

	/**
	 * 이 날짜에서 다른 날짜 전까지의, 특정 단위에서의 기간
	 * <ul>
	 * <li>{@code this}: 시작점
	 * <li>{@code endExclusive}: 끝점
	 * </ul>
	 * <p>
	 * {@code endExclusive}는 기간에 포함 안 된다.
	 * 예: "7월 1일부터 7월 말일 전까지"는 한 달이 아니다.
	 * "7월 1일부터 8월 1일 전까지"가 한 달이다.
	 * <p>
	 * 그 단위에서 완전히 하나를 이루어야 1로 세어진다.
	 * 예: "2001년 1월 2일에서 2003년 1월 1일전까지"에서 두 날짜의 년도값은 2가 차이나더라도
	 * 둘 사이 간격은 완전한 2년에서 하루가 모자라므로 결과는 1이다.
	 * 간단히 말해 반올림이 아니라 버림이다.
	 * <p>
	 * 하위 필드 값을 생략함으로써 일컬어지는 단위 시간이 성립하지 않더라도 시간의 총량으로 따진다.
	 * 예를 들어 "7월 15일부터 8월 1일 전까지"는 한 달이 아니고,
	 * "8월 1일부터 8월 15일 전까지" 역시 한 달이 아니지만
	 * "7월 15일부터 8월 15일 전까지"는 한 달이다.
	 * <p>
	 * 시작점(이것)보다 끝점(파라미터)이 더 과거인 경우 값은 (0이 아니면) 음수이다.
	 * 
	 * @param endExclusive 기간의 끝점, exclusive
	 * @param unit         간격을 측정할 단위
	 */
	@Override
	public long until ( Temporal endExclusive , TemporalUnit unit ) {
		KLunarDate end = KLunarDate.from( endExclusive );
		if( unit instanceof ChronoUnit ){
			return this.isBefore( end )
			        ? until0( end, (ChronoUnit) unit )
			        : -end.until0( this, (ChronoUnit) unit );
		}
		return unit.between( this, end );
	}

	private long until0 ( KLunarDate end , ChronoUnit chronoUnit ) {// this>=end
		switch( chronoUnit ){
		case DAYS:
			return end.toEpochDay() - toEpochDay();
		case MONTHS:
			return LunarMonthUnit.LMONTHS.between( this, end );
		case YEARS:
			return untilYear( end );
		case DECADES:
			return untilYear( end ) / 10;
		case CENTURIES:
			return untilYear( end ) / 100;
		case MILLENNIA:
			return untilYear( end ) / 1000;// 사실 지원범위가 1000년이 안 됨.
		case ERAS:
			return 0;
		default:
			throw new UnsupportedTemporalTypeException( "Unsupported unit: " + chronoUnit );
		}
	}

	/**
	 * 이 날짜에서 다른 날짜까지의 시간 간격
	 * <ul>
	 * <li>이 날짜보다 그 날짜가 미래인 경우 결과의 각 필드 값은 0 이상이다.
	 * <li>이 날짜보다 그 날짜가 과거인 경우 결과의 각 필드 값은 0 이하이다.
	 * </ul>
	 * 
	 * @see KLunarPeriod
	 * 
	 * @param endDateExclusive 그 날짜, exclusive
	 * @return 시간 간격(년,월,일)
	 */
	@Override
	public KLunarPeriod until ( ChronoLocalDate endDateExclusive ) {
		KLunarDate end = KLunarDate.from( endDateExclusive );
		KLunarDate start = this;

		int years = start.untilYear( end );
		start = start.plusYears( years );

		int months = start.untilMonthIn1Y( end );
		start = start.plusMonths( months );

		try{// resolvePreviousValid 때문에 바뀐 일자 원복 시도
			start = start.withDay( getDay() );
		}
		catch( NonexistentDateException e ){}

		int days = end.toEpochDayInt() - start.toEpochDayInt();

		return KLunarPeriod.of( years, months, days );
	}

	private int untilYear ( KLunarDate end ) {
		int diff = end.getYear() - getYear();

		if( diff == 0 )// 같은 해
		    return 0;

		if( diff > 0 ){// this < end
			if( getMonth() < end.getMonth() )
			    return diff;
			if( getMonth() > end.getMonth() )
			    return diff - 1;

			if( !isLeapMonth() && end.isLeapMonth() )
			    return diff;
			if( isLeapMonth() && !end.isLeapMonth() )
			    return diff - 1;

			if( getDay() > end.getDay() )
			    return diff - 1;

			return diff;
		}
		else{// end < this
			if( end.getMonth() < getMonth() )
			    return diff;
			if( end.getMonth() > getMonth() )
			    return diff + 1;

			if( !end.isLeapMonth() && isLeapMonth() )
			    return diff;
			if( end.isLeapMonth() && !isLeapMonth() )
			    return diff + 1;

			if( end.getDay() > getDay() )
			    return diff + 1;

			return diff;
		}
	}

	private int untilMonthIn1Y ( KLunarDate end ) {// this와 end는 1년 미만 차이
		if( this.isAfter( end ) ){
			return -end.untilMonthIn1Y_main( this );
		}
		else{
			return untilMonthIn1Y_main( end );
		}
	}

	private int untilMonthIn1Y_main ( KLunarDate end ) {// this <= end (1년 미만 차이)
		int count;

		if( end.getYear() == getYear() ){
			count = end.getMonthOrdinal() - getMonthOrdinal();
		}
		else{
			count = lengthOfYearInM() + end.getMonthOrdinal() - getMonthOrdinal();
		}

		count -= getDay() > end.getDay() ? 1 : 0;// 달 빼고 일자만으로 kd1, end 선후 비교

		return count;
	}

	//// ================================ Object

	@Override
	public boolean equals ( Object o ) {
		if( o == null ) return false;

		if( o instanceof KLunarDate ){
			KLunarDate kd = (KLunarDate) o;
			return kd.year == year
			        && kd.month == month
			        && kd.isLeapMonth == isLeapMonth
			        && kd.day == day;
		}
		return false;
	}

	@Override
	public int hashCode () {
		return toEpochDayInt();
	}

	@Override
	public String toString () {
		int d = isLeapMonth ? day + BIG_MONTH_SIZE : day;

		StringBuilder sb = new StringBuilder( 10 );
		return sb.append( getChronology().toString() )
		        .append( ' ' )
		        .append( year )
		        .append( month < 10 ? "-0" : "-" )
		        .append( month )
		        .append( d < 10 ? "-0" : "-" )
		        .append( d )
		        .toString();
	}

	//// ================================ 체크체크 나중에

//	@Override public < R > R query ( TemporalQuery<R> query )
//	@Override public KLunarDate with ( TemporalAdjuster adjuster )
//	@Override public int compareTo ( ChronoLocalDate other )

	//// -------------------------------- serialize

	@java.io.Serial
	private Object writeReplace () {
		return new Ser( Ser.DATE_TYPE , this );
	}

	@java.io.Serial
	private void readObject ( ObjectInputStream in ) throws InvalidObjectException {
		throw new InvalidObjectException( "Deserialization via serialization delegate" );
	}
}
