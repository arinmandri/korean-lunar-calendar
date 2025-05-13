package xyz.arinmandri.koreanlunarcalendar;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;


final class Ser implements Externalizable
{
	static final byte CHRONOLOGY_TYPE = 0;
	static final byte DATE_TYPE = 1;
	static final byte PERIOD_TYPE = 10;

	private byte type;
	private Object object;

	public Ser() {}

	Ser( byte type , Object o ) {
		this.type = type;
		this.object = o;
	}

	@Override
	public void writeExternal ( ObjectOutput out ) throws IOException {
		out.writeByte( type );
		switch( type ){
		case CHRONOLOGY_TYPE:
			break;
		case DATE_TYPE:
			KLunarDate kd = (KLunarDate) object;
			out.writeInt( kd.getYear() );
			out.writeInt( kd.getMonth() );
			out.writeBoolean( kd.isLeapMonth() );
			out.writeInt( kd.getDay() );
			break;
		case PERIOD_TYPE:
			KLunarPeriod kp = (KLunarPeriod) object;
			out.writeInt( kp.getYears() );
			out.writeInt( kp.getMonths() );
			out.writeBoolean( kp.isMonthLeapingMode() );
			out.writeInt( kp.getDays() );
			break;
		default:
			throw new InvalidObjectException( "Unknown type: " + type );
		}
	}

	@Override
	public void readExternal ( ObjectInput in ) throws IOException , ClassNotFoundException {
		this.type = in.readByte();
		switch( type ){
		case CHRONOLOGY_TYPE:
			break;
		case DATE_TYPE:
			int year = in.readInt();
			int month = in.readInt();
			boolean isLeapMonth = in.readBoolean();
			int day = in.readInt();
			this.object = KLunarDate.of( year, month, isLeapMonth, day );
			break;
		case PERIOD_TYPE:
			int years = in.readInt();
			int months = in.readInt();
			boolean monthLeapingMode = in.readBoolean();
			int days = in.readInt();
			this.object = KLunarPeriod.of( years, months, monthLeapingMode, days );
			break;
		default:
			throw new InvalidObjectException( "Unknown type: " + type );
		}
	}

	private Object readResolve () throws ObjectStreamException {
		switch( type ){
		case CHRONOLOGY_TYPE:
			return KLunarChronology.INSTANCE;
		default:
			return object;
		}
	}
}
