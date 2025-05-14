package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static xyz.arinmandri.koreanlunarcalendar.Ganji.CYCLE_SIZE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import xyz.arinmandri.kasiapi.ApiService;
import xyz.arinmandri.kasiapi.Item;


public class KLunarDateTest
{
	protected final int testSize      = 100_0000;
	protected final int testCheckSize =  20_0000;
	protected final int shortTestSize = 100;
	protected final int shortTestCheckSize = 20;

	ApiService api = ApiService.getInstance();
	Random random = new Random( System.currentTimeMillis() );
	ZoneId zoneId = ZoneId.of("Asia/Seoul");

	// -------------------------
	/*
	 * KLunarDate private 값들 땜쳐옴
	 */
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

	public static final int YEAR_MIN = 1391;// 최소 년도
	public static final int YEAR_MAX = YEAR_MIN + ( ydss.length - 1 ) * CYCLE_SIZE + ydss[ydss.length - 1].length - 1;// 최대년도
	public static final int EPOCHDAY_MIN = epochDays[0];
	public static final int EPOCHDAY_MAX = epochDays[epochDays.length - 1] - 1;
	static final int PROLEPTIC_MONTH_MIN = 17205;// 최소년도(1391년) 첫 달의 PROLEPTIC_MONTH 값. 19년에 윤달 7개 규칙으로 추정함.
	static final int PROLEPTIC_MONTH_MAX = 25367;

	// ------------------------------------

	final int EPOCH_DAY_MAX_KASI = 2470172 - 2440588;

	final KLunarDate MIN = KLunarDate.ofEpochDay( EPOCHDAY_MIN );
	final KLunarDate MAX = KLunarDate.ofEpochDay( EPOCHDAY_MAX );
	final LocalDate LD_MIN = LocalDate.ofEpochDay( EPOCHDAY_MIN );
	final LocalDate LD_MAX = LocalDate.ofEpochDay( EPOCHDAY_MAX );
	final LocalDate LD_MAX_KASI = LocalDate.of( 2050, 12, 31 );// KASI API를 안 쓰는 경우 여기까지만 시험 가능. 정답의 기준이 한국천문연구원 API인데 의 지원범위보다 KLunarDate 지원범위 최대가 살짝 더 미래이기 때문에 한국천문연구원 API의 지원범위를 직접 입력함.

	final int[] lengthOfYears = { 354, 384, 354, 355, 384, 354, 355, 383, 354, 355, 383, 355, 384, 355, 354, 384, 354, 354, 384, 354, 384, 355, 355, 384, 354, 354, 384, 354, 354, 384, 355, 384, 355, 354, 384, 354, 354, 384, 354, 384, 355, 354, 384, 354, 354, 384, 355, 354, 384, 355, 384, 354, 354, 384, 354, 355, 384, 354, 355, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 383, 354, 355, 383, 355, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 354, 355, 383, 354, 384, 355, 354, 384, 355, 354, 384, 354, 354, 384, 355, 384, 354, 355, 384, 354, 354, 384, 354, 384, 355, 354, 384, 355, 354, 383, 354, 384, 355, 355, 384, 354, 354, 384, 354, 354, 384, 355, 384, 355, 354, 384, 354, 354, 384, 354, 355, 384, 355, 384, 354, 354, 383, 355, 354, 384, 355, 384, 354, 355, 383, 354, 355, 384, 354, 355, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 384, 354, 354, 384, 354, 354, 385, 354,
	        355, 384, 354, 383, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 383, 355, 354, 384, 355, 354, 384, 355, 353, 384, 355, 384, 354, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 354, 384, 354, 385, 354, 355, 384, 354, 354, 383, 355, 384, 355, 354, 384, 354, 354, 384, 354, 355, 384, 354, 384, 355, 354, 384, 354, 354, 384, 355, 354, 384, 355, 384, 354, 354, 384, 354, 355, 384, 354, 384, 354, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 355, 384, 354, 384, 354, 354, 384, 354, 355, 384, 355, 354, 384, 354, 383, 355, 354, 384, 355, 354, 384, 354, 384, 354, 355, 384, 354, 355, 384, 354, 354, 384, 354, 384, 355, 354, 384, 355, 354, 384, 354, 384, 354, 355, 384, 354, 355, 383, 354, 355, 383, 355, 384, 355, 354, 384, 354, 354, 384, 354, 384, 355, 355, 384, 354, 354, 384, 354, 354, 384, 355, 384, 355, 354, 384, 354, 354, 384, 354, 384, 355,
	        354, 384, 354, 354, 384, 355, 354, 384, 355, 384, 354, 354, 384, 354, 355, 384, 354, 355, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 383, 354, 355, 383, 355, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 355, 384, 354, 384, 354, 354, 384, 354, 355, 384, 354, 355, 383, 354, 384, 355, 354, 384, 355, 354, 384, 354, 384, 354, 355, 384, 354, 355, 384, 354, 354, 384, 354, 384, 355, 354, 384, 355, 354, 383, 354, 384, 355, 355, 384, 354, 354, 384, 354, 354, 384, 355, 384, 355, 354, 384, 354, 354, 384, 354, 355, 384, 355, 384, 354, 354, 383, 355, 354, 384, 355, 384, 354, 355, 383, 354, 355, 384, 354, 355, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 384, 354, 354, 384, 354, 354, 385, 354, 355, 384, 354, 383, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 355, 384, 354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 384, 355, 354, 384, 355, 354, 383, 355, 384, 354, 355, 384,
	        354, 354, 384, 354, 384, 354, 355, 384, 355, 354, 384, 354, 384, 354, 354, 385, 354, 355, 384, 354, 354, 383, 355, 384, 355, 354, 384, 354, 354, 384, 354, 355, 384, 354, 385, 354, 354, 384, 354, 354, 384, 355, 384, 354, 355, 384, 354, 354, 384, 354, 355, 384, 354, 384, 355, 354, 383, 355, 354, 384, 355, 384, 354, 354, 384, 354, 354, 384, 355, 355, 384, 354, 384, 354, 354, 384, 354, 355, 384, };
	{
		if( lengthOfYears.length != YEAR_MAX - YEAR_MIN + 1 ){
			fail( "lengthOfYears 개수가 맞지 않음." );
		}
	}

	Map<Integer, Integer> leapMonthsMap;{// (년, 월) 항목이 있으면 윤달이 있음
		int[] leapMonths_y = { 1392, 1395, 1398, 1401, 1403, 1406, 1409, 1411, 1414, 1417, 1420, 1422, 1425, 1428, 1430, 1433, 1436, 1439, 1441, 1444, 1447, 1450, 1452, 1455, 1458, 1460, 1463, 1466, 1469, 1471, 1474, 1477, 1479, 1482, 1485, 1488, 1490, 1493, 1496, 1498, 1501, 1504, 1507, 1509, 1512, 1515, 1517, 1520, 1523, 1525, 1528, 1531, 1534, 1536, 1539, 1542, 1545, 1547, 1550, 1553, 1555, 1558, 1561, 1564, 1566, 1569, 1572, 1574, 1577, 1580, 1583, 1585, 1588, 1591, 1593, 1596, 1599, 1602, 1604, 1607, 1610, 1612, 1615, 1618, 1621, 1623, 1626, 1629, 1631, 1634, 1637, 1640, 1642, 1645, 1648, 1650, 1653, 1656, 1659, 1661, 1664, 1667, 1670, 1672, 1675, 1678, 1680, 1683, 1686, 1689, 1691, 1694, 1697, 1699, 1702, 1705, 1708, 1710, 1713, 1716, 1718, 1721, 1724, 1727, 1729, 1732, 1735, 1737, 1740, 1743, 1746, 1748, 1751, 1754, 1756, 1759, 1762, 1765, 1767, 1770, 1773, 1775, 1778, 1781, 1784, 1786, 1789, 1792, 1795, 1797, 1800, 1803, 1805, 1808, 1811, 1814, 1816, 1819, 1822, 1824,
		        1827, 1830, 1832, 1835, 1838, 1841, 1843, 1846, 1849, 1851, 1854, 1857, 1860, 1862, 1865, 1868, 1870, 1873, 1876, 1879, 1881, 1884, 1887, 1890, 1892, 1895, 1898, 1900, 1903, 1906, 1909, 1911, 1914, 1917, 1919, 1922, 1925, 1928, 1930, 1933, 1936, 1938, 1941, 1944, 1947, 1949, 1952, 1955, 1957, 1960, 1963, 1966, 1968, 1971, 1974, 1976, 1979, 1982, 1984, 1987, 1990, 1993, 1995, 1998, 2001, 2004, 2006, 2009, 2012, 2014, 2017, 2020, 2023, 2025, 2028, 2031, 2033, 2036, 2039, 2042, 2044, 2047, 2050, };
		int[] leapMonths_m = { 12, 9, 5, 3, 11, 7, 4, 12, 9, 5, 1, 12, 7, 4, 12, 8, 6, 2, 11, 7, 4, 1, 9, 6, 2, 11, 7, 3, 2, 9, 6, 2, 10, 8, 4, 1, 9, 5, 3, 11, 7, 4, 1, 9, 5, 4, 12, 8, 4, 12, 10, 6, 2, 12, 7, 5, 1, 9, 6, 3, 11, 7, 5, 2, 10, 6, 2, 12, 8, 4, 2, 9, 6, 3, 11, 8, 4, 2, 9, 6, 3, 11, 8, 4, 2, 10, 6, 4, 11, 8, 4, 1, 11, 6, 3, 11, 7, 5, 3, 7, 6, 4, 2, 7, 5, 3, 8, 6, 4, 3, 7, 5, 3, 7, 6, 4, 3, 7, 5, 3, 8, 6, 4, 3, 7, 5, 4, 9, 6, 4, 3, 7, 5, 4, 9, 6, 5, 2, 7, 5, 3, 10, 6, 5, 3, 7, 5, 4, 2, 6, 4, 2, 6, 5, 3, 2, 6, 4, 3, 7, 5, 4, 9, 6, 4, 3, 7, 5, 4, 8, 7, 5, 3, 8, 5, 4, 10, 6, 5, 3, 7, 5, 4, 2, 6, 5, 3, 8, 5, 4, 2, 6, 5, 2, 7, 5, 4, 2, 6, 5, 3, 7, 6, 4, 2, 7, 5, 3, 8, 6, 4, 3, 7, 5, 4, 8, 6, 4, 10, 6, 5, 3, 8, 5, 4, 2, 7, 5, 3, 9, 5, 4, 2, 6, 5, 3, 11, 6, 5, 2, 7, 5, 3, };
		if( leapMonths_y.length != leapMonths_m.length ){
			throw new RuntimeException( "윤달 목록 년이랑 월이랑 개수 안 맞음" );
		}
		leapMonthsMap = new HashMap<>();
		for( int i = 0 ; i < leapMonths_y.length ; i++ ){
			int y = leapMonths_y[i];
			int m = leapMonths_m[i];
			leapMonthsMap.put( y, m );
		}
	}

	//// ================================ repeat test

	void repeat ( Runnable test , String title , int size , int testCheckSize ) {
		printTitle( title );

		long t0 = System.currentTimeMillis();

		for( int i = 0 ; i < size ; i += 1 ){
			try{
				test.run();
			}
			catch( NoNeedToTest e ){
				i -= 1;
				continue;
			}
			if( ( i + 1 ) % testCheckSize == 0 ){
				System.out.println( "repeat-" + ( i + 1 ) );
			}
		}

		long t1 = System.currentTimeMillis();
		long d = t1 - t0;
		long a = d / 1000;
		long b = d % 1000;

		System.out.println( "GOOD " + String.format( "%3d.%03d", a, b ) );
	}

	void printTitle ( String title ) {
		if( title != null )
		    System.out.println( "\n=== " + title + " ===" );
	}

	void repeat ( Runnable test , String title ) {
		repeat( test, title, testSize, testCheckSize );
	}

	void repeat ( Runnable test ) {
		repeat( test, null, testSize, testCheckSize );
	}

	void repeatShortly ( Runnable test , String title ) {
		repeat( test, title, shortTestSize, shortTestCheckSize );
	}

	void repeatShortly ( Runnable test ) {
		repeat( test, null, shortTestSize, shortTestCheckSize );
	}

	//// ================================ util, private, etc

	boolean checkEquality ( Item item , KLunarDate kd ) {
		return item.getLunYear()  == kd.getYear()
		    && item.getLunMonth() == kd.getMonth()
		    && item.getLunDay()   == kd.getDay()
		    && item.getLunLeapmonth().equals( "윤" ) == kd.isLeapMonth();
	}

	boolean checkEquality ( Item item , LocalDate ld ) {
		return item.getSolYear()  == ld.getYear()
		    && item.getSolMonth() == ld.getMonthValue()
		    && item.getSolDay()   == ld.getDayOfMonth();
	}

	void checkKdRoundtrip ( int y , int m , boolean isLeapMonth , int d ) {
		KLunarDate kd = KLunarDate.of( y, m, isLeapMonth, d );
		LocalDate ld = kd.toLocalDate();
		kd = KLunarDate.from( ld );
		assertEquals( kd, KLunarDate.of( y, m, isLeapMonth, d ) );
	}

	LocalDate getRandomLd () {// 지원범위0 내에서
		return getRandomLd( LD_MIN, LD_MAX );
	}

	LocalDate getRandomLd ( LocalDate d1 , LocalDate d2 ) {// 이상, 이하

		long n = ChronoUnit.DAYS.between( d1, d2 );// 시작일~종료일 일수
		long randomDays = random.nextLong( n + 1 );// 랜덤 숫자 뽑기
		return d1.plusDays( randomDays );
	}

	KLunarDate getRaondomKd () {
		return KLunarDate.ofEpochDay( getRandomEpochDay() );
	}

	int getRandomEpochDay () {
		return getRandomInt( EPOCHDAY_MIN, EPOCHDAY_MAX );
	}

	int getRandomEpochDay_kasi () {
		return getRandomInt( EPOCHDAY_MIN, EPOCH_DAY_MAX_KASI );
	}

	int getRandomInt ( int a , int b ) {// 이상, 이하
		return random.nextInt( b - a + 1 ) + a;
	}

	String i ( int i ) {
		if( i < 10 ) return "0" + i;
		return i + "";
	}

	/**
	 * 테스트 반복에서 반복횟수 차감 취소하고 다시하려면 이 예외를 던지세요.
	 */
	class NoNeedToTest extends RuntimeException
	{}
}
