package xyz.arinmandri.koreanlunarcalendar;

public enum Ganji
{
	O1,  O2,  O3,  O4,  O5,  O6,  O7,  O8,  O9,  O10, O11, O12, // 갑자 을축 병인 정묘 무진 기사 경오 신미 임신 계유 갑술 을해
	O13, O14, O15, O16, O17, O18, O19, O20, O21, O22, O23, O24, // 병자 정축 무인 기묘 경진 신사 임오 계미 갑신 을유 병술 정해
	O25, O26, O27, O28, O29, O30, O31, O32, O33, O34, O35, O36, // 무자 기축 경인 신묘 임진 계사 갑오 을미 병신 정유 무술 기해
	O37, O38, O39, O40, O41, O42, O43, O44, O45, O46, O47, O48, // 경자 신축 임인 계묘 갑진 을사 병오 정미 무신 기유 경술 신해
	O49, O50, O51, O52, O53, O54, O55, O56, O57, O58, O59, O60, // 임자 계축 갑인 을묘 병진 정사 무오 기미 경신 신유 임술 계해
	;

	public final Gan gan;
	public final Ji ji;

	private Ganji() {
		this.gan = Gan.values()[ordinal() % Gan.values().length];
		this.ji  = Ji .values()[ordinal() % Ji .values().length];
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

	public enum Gan {
		O1('갑', '甲'),
		O2('을', '乙'),
		O3('병', '丙'),
		O4('정', '丁'),
		O5('무', '戊'),
		O6('기', '己'),
		O7('경', '庚'),
		O8('신', '辛'),
		O9('임', '壬'),
		O10('계', '癸'),
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

	public enum Ji {
		O1('자', '子'),
		O2('축', '丑'),
		O3('인', '寅'),
		O4('묘', '卯'),
		O5('진', '辰'),
		O6('사', '巳'),
		O7('오', '午'),
		O8('미', '未'),
		O9('신', '申'),
		O10('유', '酉'),
		O11('술', '戌'),
		O12('해', '亥'),
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
