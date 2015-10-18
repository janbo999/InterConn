import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * socket slots.
 * @author Janbo
 */
public class Slots{
	/** IP tunnel. */
	private Tunnel tun;
	/** buffer. */
	private byte[] buf = new byte[Defs.MAX_BUF];
	/** dummy socket. */
	private Socket dummy = new Socket();
	/** slots. */
	private Socket[] arr = new Socket[Defs.MAX_SLOTS];
	/** last access time. */
	private long[] lat = new long[Defs.MAX_SLOTS];
	
	/**
	 * constructor.
	 * @param tu IP tunnel
	 */
	public Slots(Tunnel tu){
		tun = tu;
	}
	/**
	 * get status.
	 * @return status
	 */
	public String status(){
		int o = 0;
		for(int i=0;i<Defs.MAX_SLOTS;i++){
			if(arr[i] != null){
				o++;
			}
		}
		return "Slolts:"+o+'/'+Defs.MAX_SLOTS;
	}
	/**
	 * open slot.
	 * @param addr remote address
	 * @param slot slot no.
	 * @param port port
	 */
	public void open(String addr, int slot, int port){
		try {
			arr[slot] = new Socket(addr, port);
			lat[slot] = Utl.getTickCount();
		} catch (IOException e) {
			forceClose(slot);
		}
	}
	/**
	 * find free slot.
	 * @return index
	 */
	private synchronized int find(){
		while(true){
			for(int i=0;i<Defs.MAX_SLOTS;i++){
				if(arr[i] == null){
					arr[i] = dummy;
					lat[i] = Utl.getTickCount();
					return i;
				}
			}
			Utl.idle(1000);
		}
	}
	/**
	 * write to slot.
	 * @param slot slot no.
	 * @param len length
	 * @param bf data
	 */
	public void write(int slot, int len, byte[] bf){
		try {
			Socket sock = arr[slot];
			if(sock == null){
				throw new IOException();
			}
			OutputStream os = sock.getOutputStream();
			os.write(bf, 0, len);
			lat[slot] = Utl.getTickCount();
		} catch (IOException e) {
			forceClose(slot);
		}
	}
	/**
	 * set new slot.
	 * @param sock socket
	 */
	public void put(Socket sock){
		int nm = find();
		arr[nm] = sock;
		lat[nm] = Utl.getTickCount();
		tun.write(Defs.OPEN, nm, 0, null);
	}
	/**
	 * check to send something
	 */
	public void toSend(boolean urg){
		long tm = Utl.getTickCount();
		for(int i=0;i<arr.length;i++){
			Socket sock = arr[i];
			if(sock == null){
				continue;
			}
			try {
				InputStream is = sock.getInputStream();
				while(true){
					if(is.available()<=0){
						if(tm - lat[i] > Defs.LAT_TOUT){
							// detect closed
							int to = sock.getSoTimeout();
							sock.setSoTimeout(100);
							int x;
							try {
								x = is.read();
							} catch (Exception e) {
								x = 0;
							}
							sock.setSoTimeout(to);
							if(x<0){
								throw new IOException();
							}
							lat[i] = tm;
						}
						break;
					}
					int ln = is.read(buf, 0, Defs.MAX_BUF);
					tun.write(Defs.DATA, i, ln, buf);
					lat[i] = tm;
				}
				
			} catch (IOException e) {
				forceClose(i);
			}
		}
	}
	/**
	 * force close slot.
	 * @param slot slot no.
	 */
	private void forceClose(int slot){
		try {
			arr[slot].close();
		} catch (Exception e) {
			//
		}
		arr[slot] = null;
		tun.write(Defs.CLOSE, slot, 0, null);
	}
	/**
	 * close slot.
	 * @param slot slot no.
	 */
	public void close(int slot){
		try {
			if(arr[slot]!=null){
				arr[slot].close();
			}
		} catch (IOException e) {
			//
		}
		arr[slot] = null;
	}
}
