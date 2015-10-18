
public class Defs {
	/** version. */
	public static final String VERSION = "v0.0.0.2";
	/** protocol identifier.  */
	public static final String PROTO = "IP Tunnel "+VERSION;
	/** ping interval. */
	public static final int PING_INT = 30000;
	/** check closed interval. */
	public static final long LAT_TOUT = 300000;

	/** buffer size. */
	public static final int MAX_BUF = 1024*64;
	/** max slots. */
	public static final int MAX_SLOTS = 1000;
	
	
	/** connect. */
	public static final char CONN = 'I';
	/** ping. */
	public static final char PING = 'P';

	/** open. */
	public static final char OPEN = 'O';
	/** data. */
	public static final char DATA = 'D';
	/** close. */
	public static final char CLOSE = 'C';
}
