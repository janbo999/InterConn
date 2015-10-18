import java.io.IOException;
/**
 * server tunnel.
 * @author Janbo
 */

public class TunServer implements Runnable{
	/** server thread. */
	private Thread thrd;
	/** server runs. */
	private boolean runs = true;
	/** IP tunnel. */
	private Tunnel tun;
	/** log message. */
	private String msg;
	/** temporary. */
	private Comm comm = new Comm();
	/** socket slots. */
	private Slots slots;
	/** web client. */
	private WebServer client;
	/**
	 * constructor.
	 * @param port tunnel port
	 * @param wport web port
	 * @throws IOException error
	 */
	public TunServer(int port, int wport) throws IOException{
		msg = "Int.List("+port+")";
		tun = new Tunnel(port);
		slots = new Slots(tun);
		client = new WebServer(wport, slots);
		
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
			if(tun.ping()){
				System.out.println("SRV --- "+slots.status());
			}
			
			while(tun.read(comm)){
				switch(comm.comm){
				case Defs.PING:
					// do nothing
					break;
//				case Comm.OPEN: // server command
				case Defs.CLOSE:
					slots.close(comm.slot);
					break;
				case Defs.DATA:
					slots.write(comm.slot, comm.len, comm.buf);
					break;
				default:
				}
				
				
				
			}
			slots.toSend(true);
			
			Utl.idle(10);
		}
	}
	/**
	 * stop server.
	 */
	public void kill(){
		runs = false;
		client.kill();
		tun.close();
		Utl.idle(1000);
	}
}
