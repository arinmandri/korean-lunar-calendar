package xyz.arinmandri.kasiapi;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


// item 요소
@Root( name = "item" , strict = false )
@lombok.Data
public class Item
{

	@Element( name = "lunYear" , required = false ) int lunYear;// 음력 년도
	@Element( name = "lunMonth" , required = false ) int lunMonth;// 음력 월
	@Element( name = "lunLeapmonth" , required = false ) String lunLeapmonth;// 음력 윤달구분 (평/윤)
	@Element( name = "lunDay" , required = false ) int lunDay;// 음력 일
	@Element( name = "lunNday" , required = false ) int lunNday;// 월일수(음력) 오류인지? 문서에는 그렇게 나와있는데 getSpcifyLunCalInfo 에서는 lunDay랑 똑같은 값 나옴. 오류 제보는? 했?는데?

	@Element( name = "lunSecha" , required = false ) String secha;// 간지(년)
	@Element( name = "lunWolgeon" , required = false ) String wolgeon;// 간지(월)
	@Element( name = "lunIljin" , required = false ) String iljin;// 간지(일)

	@Element( name = "solYear" , required = false ) int solYear;// 양력 년도
	@Element( name = "solLeapyear" , required = false ) String solLeapyear;// 양력 윤년구분 (평/윤)
	@Element( name = "solMonth" , required = false ) int solMonth;// 양력 월
	@Element( name = "solDay" , required = false ) int solDay;// 양력 일
	@Element( name = "solWeek" , required = false ) String solWeek;// 요일

	@Element( name = "solJd" , required = false ) int solJd;// 율리우스적일

	public String toSolString () {
		StringBuilder sb = new StringBuilder()
		        .append( solYear )
		        .append( solMonth < 10 ? "-0" : "-" )
		        .append( solMonth )
		        .append( solDay < 10 ? "-0" : "-" )
		        .append( solDay );

		return sb.toString();
	}

	public String toLunString () {
		StringBuilder sb = new StringBuilder()
		        .append( lunYear )
		        .append( lunMonth < 10 ? "-0" : "-" )
		        .append( lunMonth )
		        .append( lunDay < 10 ? "-0" : "-" )
		        .append( lunDay )
		        .append( "(" + lunNday + ')' )
		        .append( '(' + lunLeapmonth + ')' );

		return sb.toString();
	}

	public String toString () {
		StringBuilder sb = new StringBuilder()
		        .append( "양력: " )
		        .append( solYear )
		        .append( solMonth < 10 ? "-0" : "-" )
		        .append( solMonth )
		        .append( solDay < 10 ? "-0" : "-" )
		        .append( solDay )
		        .append( '(' + solWeek + ')' )
		        .append( '(' + solLeapyear + ')' )
		        .append( " 음력: " )
		        .append( lunYear )
		        .append( lunMonth < 10 ? "-0" : "-" )
		        .append( lunMonth )
		        .append( lunDay < 10 ? "-0" : "-" )
		        .append( lunDay )
		        .append( "(" + lunNday + ')' )
		        .append( '(' + lunLeapmonth + ')' );

		return sb.toString();
	}
}
