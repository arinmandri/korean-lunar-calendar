package xyz.arinmandri.koreanlunarcalendar;

import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Era;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ValueRange;
import java.util.List;

// TODO
public class KLunarChronology extends AbstractChronology implements java.io.Serializable {
    
    public static final KLunarChronology INSTANCE = new KLunarChronology();

    private KLunarChronology(){}

    @Override
    public String getId() {
        return "KoreanLunar";
    }

    @Override
    public String getCalendarType() {
        return null;// TODO
    }

    @Override
    public ChronoLocalDate date ( int prolepticYear , int month , int dayOfMonth ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'date'");
    }

    @Override
    public ChronoLocalDate dateYearDay ( int prolepticYear , int dayOfYear ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dateYearDay'");
    }

    @Override
    public ChronoLocalDate dateEpochDay ( long epochDay ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dateEpochDay'");
    }

    @Override
    public ChronoLocalDate date ( TemporalAccessor temporal ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'date'");
    }

    @Override
    public boolean isLeapYear ( long prolepticYear ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isLeapYear'");
    }

    @Override
    public int prolepticYear ( Era era , int yearOfEra ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'prolepticYear'");
    }

    @Override
    public Era eraOf ( int eraValue ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eraOf'");
    }

    @Override
    public List<Era> eras () {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eras'");
    }

    @Override
    public ValueRange range ( ChronoField field ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'range'");
    }

	@Override
	public KLunarPeriod period ( int years , int months , int days ) {
		return KLunarPeriod.of( years, months, days );
	}
    //// ================================ TODO serialize
}
