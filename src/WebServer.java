import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
/**
 * web server.
 * @author Janbo
 *
 */
public class WebServer implements Runnable{
	/** main thread. */
	private Thread thrd;
	/** server runs. */
	private boolean runs = true;
	/** socket slots. */
	private Slots slots;
	/** log message. */
	private String msg;
	/** listening socket. */
	private ServerSocket list;
	/**
	 * constructor.
	 * @param port web port
	 * @param sl slots
	 * @throws IOException error
	 */
	public WebServer(int port, Slots sl) throws IOException{
		msg = "Web.List("+port+")";
		slots = sl;
		
		list = ServerSocketFactory.getDefault().createServerSocket(port);
		thrd = new Thread(this);
		thrd.setDaemon(true);
		thrd.setName(msg);
		thrd.start();
		System.out.println(msg+" Started");
	}
	/**
	 * main loop.
	 */
	public void run(){
		while (runs) {
			Socket sock;
			try {
				sock = list.accept();
//				sock.setKeepAlive(true);
//				sock.setSoTimeout(9000);
//				sock.setSoLinger(true, 9000);
			} catch (IOException e) {
				continue;
			}
			slots.put(sock);
		}
	}
	/**
	 * terminate.
	 */
	public void kill(){
		runs = false;
		Utl.idle(1000);
	}

}
