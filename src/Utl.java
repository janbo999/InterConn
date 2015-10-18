
public class Utl {
    /**
     * get current time in milliseconds.
     * @return The current time in milliseconds.
     */
    public static long getTickCount() {
        return System.currentTimeMillis();
    }
    /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds.
     * @param m the specified length of time to sleep in milliseconds.
     */
    public static void idle(int m) {
        try {
            Thread.sleep(m);
        }
        catch (InterruptedException e) {
            //Log.log(Log.FATAL, e);
        }
    }
    /**
     * Converts a specified string to integer.
     * @param b The source String to convert.
     * @return The obtained integer value representing the string conversion.
     */
    public static int latoi(String b) {
    	return Integer.parseInt(b);
    }
}
