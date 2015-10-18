import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;


public class Tunnel {
	/** protocol bytes. */
	public static final byte[] PROTOB = Defs.PROTO.getBytes();
	
	/** connect command. */
	private static final Comm CONNECT = new Comm(Defs.CONN, -1, PROTOB.length, PROTOB);
	/** ping command. */
	private static final Comm PING = new Comm(Defs.PING, 0, 0, null);
	
	/** is in server mode.*/
	private boolean server;
	/** server address. */
	private String addr;
	/** tunnel port. */
	private int port;
	/** socket. */
	private Socket sock;
	/** log message. */
	private String msg = "Tunnel(";
	/** ping counter. */
	private long ping = Utl.getTickCount();
	/** temporary. */
	private Comm comm = new Comm();
	/**
	 * client constructor.
	 * @param ad address
	 * @param pt port
	 */
	public Tunnel(String ad, int pt){
		server = false;
		addr = ad;
		port = pt;
		msg += ad+","+port+") "; 
	}
	/**
	 * server constructor.
	 * @param pt port
	 */
	public Tunnel(int pt){
		server = true;
		port = pt;
		msg += port+") "; 
	}
	private String srvStr(){
		return server ? "SRV " : "CLI "; 
	}
	/**
	 * read from tunnel.
	 * @return Comm | null
	 */
	public boolean read(Comm cm){
		checkOpen();
		boolean res = cm.read(sock);
		if(res && cm.comm != Defs.PING){
			System.out.println(srvStr()+"<<< "+cm);
		}
		return res;
	}
	/**
	 * write to tunnel.
	 * @param cm Comm to write
	 */
	public synchronized void write(Comm cm){
		checkOpen();
		while(!cm.write(sock)){
			checkOpen();
		}
		System.out.println(srvStr()+">>> "+cm);
	}
	/**
	 * write to tunnel.
	 * @param cm command
	 * @param sl slot
	 * @param ln length
	 * @param bf data
	 */
	public synchronized void write(char cm, int sl, int ln, byte[] bf){
		comm.set(cm, sl, ln, bf);
		write(comm);
	}
	/**
	 * close.
	 */
	public void close(){
		try {
			sock.close();
		} catch (IOException e) {
			//
		}
	}
	/**
	 * ping if time-out.
	 */
	public boolean ping(){
		long tm = Utl.getTickCount();
		if(tm-ping>Defs.PING_INT){
			ping = tm;
			PING.write(sock);
			return true;
		}
		return false;
	}
	/**
	 * wait for connection.
	 * @throws IOException
	 */
	private void connServer() throws IOException{
		System.out.println(msg + " Waiting connection");
		ServerSocket list = ServerSocketFactory.getDefault().createServerSocket(port);
		sock = list.accept();
		list.close();
		
		if(!comm.readw(sock) || !comm.equals(CONNECT)){
			sock.close();
			throw new IOException();
		}
		CONNECT.write(sock);
		System.out.println(msg + "Connected:"+sock.getRemoteSocketAddress().toString());
	}
	/**
	 * connect to server.
	 * @throws IOException
	 */
	private void connClient() throws IOException{
		System.out.println(msg + " Connecting to:"+addr);
		
		sock = new Socket(addr, port);
		CONNECT.write(sock);
		if(!comm.readw(sock) || !comm.equals(CONNECT)){
			sock.close();
			throw new IOException();
		}
		System.out.println(msg+" Connected");
		System.out.println();
	}
	/**
	 * check tunnel is open, connect.
	 */
	private void checkOpen(){
		if(sock != null && !sock.isClosed()){
			return;
		}
		while(true){
			
			try {
				if(server){
					connServer();
				}
				else{
					connClient();
				}
				break;
			} catch (IOException e) {
				System.out.println(msg + " Failed");
				Utl.idle(12000);
			}
		}
	}

}
