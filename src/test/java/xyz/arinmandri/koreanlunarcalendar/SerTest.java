package xyz.arinmandri.koreanlunarcalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.chrono.Chronology;

import org.junit.jupiter.api.Test;


public class SerTest extends ATest
{

	@Test
	public void serializeChronology () {
		printTitle( "Chronology serialize" );

		final KLunarChronology c = KLunarChronology.INSTANCE;

		byte[] sered = serialize( c );
		KLunarChronology desered = (KLunarChronology) deserialize( sered );
		assertTrue( c.equals( desered ) );

		Chronology c1 = Chronology.of( "KoreanLunar" );
		assertEquals( c, c1 );
	}

	@Test
	public void serializeKLunarDate () {
		repeat( this::serializeKLunarDate1, "KLunarDate serialize" );
	}

	public void serializeKLunarDate1 () {

		final KLunarDate kd = getRaondomKd();

		byte[] sered = serialize( kd );
		KLunarDate desered = (KLunarDate) deserialize( sered );
		assertTrue( kd.equals( desered ) );

		KLunarDate kd1 = KLunarDate.of( kd.getYear(), kd.getMonth(), kd.isLeapMonth(), kd.getDay() );
		assertEquals( kd, kd1 );
	}

	@Test
	public void serializeKLunarPeriod () {
		repeat( this::serializeKLunarPeriod1, "KLunarPeriod serialize" );
	}

	public void serializeKLunarPeriod1 () {
		KLunarDate kd1 = getRaondomKd();
		KLunarDate kd2 = getRaondomKd();
		KLunarPeriod kp = kd1.until( kd2 );

		byte[] sered = serialize( kp );
		KLunarPeriod desered = (KLunarPeriod) deserialize( sered );
		assertTrue( kp.equals( desered ) );

		KLunarPeriod kp1 = KLunarPeriod.of( kp.getYears(), kp.getMonths(), kp.isMonthLeapingMode(), kp.getDays() );
		assertEquals( kp, kp1 );
	}

	public byte[] serialize ( Object instance ) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try{
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream( bos );
			oos.writeObject( instance );
			return bos.toByteArray();
		}
		catch( Exception e ){
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if( oos != null ) oos.close();
			}
			catch( Exception ignore ){}
			try{
				if( bos != null ) bos.close();
			}
			catch( Exception ignore ){}
		}
	}

	public Object deserialize ( byte[] serializedData ) {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try{
			bis = new ByteArrayInputStream( serializedData );
			ois = new ObjectInputStream( bis );
			return ois.readObject();
		}
		catch( Exception e ){
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if( ois != null ) ois.close();
			}
			catch( Exception ignore ){}
			try{
				if( bis != null ) bis.close();
			}
			catch( Exception ignore ){}
		}
	}
}
