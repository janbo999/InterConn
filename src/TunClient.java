import java.io.IOException;
/**
 * client tunnel.
 * @author Janbo
 *
 */
public class TunClient implements Runnable{
	/** main thread. */
	Thread thrd;
	/** client runs. */
	boolean runs = true;
	/** IP tunnel. */
	Tunnel tun;
	/** log message. */
	String msg;
	/** temporary. */
	Comm comm = new Comm();
	/** socket slots. */
	Slots slots;
	/** web address. */
	String waddr;
	/** web port. */
	int wport;
	/**
	 * constructor.
	 * @param addr server address
	 * @param port tunnel port
	 * @param string wad web address
	 * @param wpt web port
	 * @throws IOException error
	 */
	public TunClient(String addr, int port, String wad, int wpt) throws IOException{
		msg = "Int.Conn("+addr+":"+port+")";
		tun = new Tunnel(addr, port);
		slots = new Slots(tun);
		waddr = wad;
		wport = wpt;
		
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
				System.out.println("CLI --- "+slots.status());
			}

			while(tun.read(comm)){
				switch(comm.comm){
				case Defs.PING:
					// do nothing
					break;
				case Defs.OPEN:
					slots.open(waddr, comm.slot, wport);
					break;
				case Defs.CLOSE:
					slots.close(comm.slot);
					break;
				case Defs.DATA:
					slots.write(comm.slot, comm.len, comm.buf);
					break;
				default:
				}
			}
			slots.toSend(false);
			
			Utl.idle(10);
		}
	}
	/**
	 * terminate.
	 */
	public void kill(){
		runs = false;
		Utl.idle(1000);
		tun.close();
	}

}
