package xyz.arinmandri.koreanLunarCalendar;

public class NonexistentDateException extends java.time.DateTimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3005887655734071662L;

	public NonexistentDateException() {
		this( "nonexistent date" );
	}

	public NonexistentDateException( String message ) {
		super( message );
	}
}
