package xyz.arinmandri.koreanlunarcalendar;

public enum Ganji
{
	A1, B2, C3, D4, E5, F6, G7, H8, I9, J10, A11, B12,// 갑자 을축 병인 정묘 무진 기사 경오 신미 임신 계유 갑술 을해
	C1, D2, E3, F4, G5, H6, I7, J8, A9, B10, C11, D12,// 병자 정축 무인 기묘 경진 신사 임오 계미 갑신 을유 병술 정해
	E1, F2, G3, H4, I5, J6, A7, B8, C9, D10, E11, F12,// 무자 기축 경인 신묘 임진 계사 갑오 을미 병신 정유 무술 기해
	G1, H2, I3, J4, A5, B6, C7, D8, E9, F10, G11, H12,// 경자 신축 임인 계묘 갑진 을사 병오 정미 무신 기유 경술 신해
	I1, J2, A3, B4, C5, D6, E7, F8, G9, H10, I11, J12,// 임자 계축 갑인 을묘 병진 정사 무오 기미 경신 신유 임술 계해
	;

	public static final int CYCLE_SIZE = 60;

	public final Gan gan;
	public final Ji ji;

	private Ganji() {
		this.gan = Gan.values()[ordinal() % Gan.values().length];
		this.ji = Ji.values()[ordinal() % Ji.values().length];
	}

	public String toString () {
		return new StringBuilder()
		        .append( gan.k )
		        .append( ji.k )
		        .append( '(' )
		        .append( gan.c )
		        .append( ji.c )
		        .append( ')' )
		        .toString();
	}

	public String toKoreanString () {
		return String.valueOf( gan.k ) + String.valueOf( ji.k );
	}

	public String toChineseString () {
		return String.valueOf( gan.c ) + String.valueOf( ji.c );
	}

	public enum Gan
	{
		A ('갑', '甲'),
		B ('을', '乙'),
		C ('병', '丙'),
		D ('정', '丁'),
		E ('무', '戊'),
		F ('기', '己'),
		G ('경', '庚'),
		H ('신', '辛'),
		I ('임', '壬'),
		J ('계', '癸'),
		;

		public final char k;
		public final char c;

		private Gan( char k , char c ) {
			this.k = k;
			this.c = c;
		}

		public String toString () {
			return String.valueOf( k );
		}
	}

	public enum Ji
	{
		_1 ('자', '子'),
		_2 ('축', '丑'),
		_3 ('인', '寅'),
		_4 ('묘', '卯'),
		_5 ('진', '辰'),
		_6 ('사', '巳'),
		_7 ('오', '午'),
		_8 ('미', '未'),
		_9 ('신', '申'),
		_10 ('유', '酉'),
		_11 ('술', '戌'),
		_12 ('해', '亥'),
		;

		public final char k;
		public final char c;

		private Ji( char k , char c ) {
			this.k = k;
			this.c = c;
		}

		public String toString () {
			return String.valueOf( k );
		}
	}

}
