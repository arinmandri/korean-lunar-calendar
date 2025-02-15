package xyz.arinmandri.koreanlunarcalendar;

public class OutOfRangeException extends java.time.DateTimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6783778321701069724L;

	public OutOfRangeException() {
		this( "out of supported range" );
	}

	public OutOfRangeException( String message ) {
		super( message );
	}
}
