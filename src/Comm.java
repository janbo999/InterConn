import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;


public class Comm {
	
	/** command. */
	public char comm;
	/** slot. */
	public short slot;
	/** length. */
	public int len;
	/** buffer. */
	public byte[] buf = new byte[Defs.MAX_BUF];
	
	/**
	 * constructor.
	 * @param cm command
	 * @param sl slot
	 * @param ln length
	 * @param bf data
	 */
	public Comm(char cm, int sl, int ln, byte[] bf){
		comm = cm;
		slot = (short)sl;
		len = ln;
		if(len>0){
			System.arraycopy(bf, 0, buf, 0, len);
		}
	}
	/**
	 * constructor.
	 */
	public Comm(){
	}
	/**
	 * read data.
	 * @param sock from
	 * @return true - data
	 */
	public boolean read(Socket sock){
		try {
			InputStream is = sock.getInputStream();
			if(is.available()<8){
				return false;
			}
			read0(sock);
			return true;
		} catch (IOException e) {
			close(sock);
			return false;
		}
	}
	/**
	 * wait, read data.
	 * @param sock from
	 * @return true - data
	 */
	public boolean readw(Socket sock){
		try {
			InputStream is = sock.getInputStream();
			// time out
			long tm = Utl.getTickCount();
			while(is.available()<8){
				if(Utl.getTickCount() - tm > 30000){
					throw new IOException();
				}
				Utl.idle(100);
			}
			read0(sock);
			return true;
		} catch (IOException e) {
			close(sock);
			return false;
		}
	}
	/**
	 * internal read.
	 * @param sock from
	 * @throws IOException error
	 */
	private void read0(Socket sock) throws IOException{
		ByteBuffer bb = ByteBuffer.allocate(8);
		InputStream is = sock.getInputStream();
		is.read(bb.array(), 0, 8);
		comm = bb.getChar();
		slot = bb.getShort();
		len = bb.getInt();
		int ln = len;
		int of = 0;
		while(ln > 0){
			int rd = sock.getInputStream().read(buf, of, ln);
			ln -= rd;
			of += rd;
		}
	}
	/**
	 * set command.
	 * @param cm command
	 * @param sl slot
	 * @param ln elngth
	 * @param bf data
	 */
	public void set(char cm, int sl, int ln, byte[] bf){
		comm = cm;
		slot = (short)sl;
		len = ln;
		if(len>0){
			System.arraycopy(bf, 0, buf, 0, len);
		}
	}
	/**
	 * write.
	 * @param sock to
	 * @return true - OK
	 */
	public boolean write(Socket sock){
		try {
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.putChar(comm);
			bb.putShort((short)slot);
			bb.putInt(len);
			sock.getOutputStream().write(bb.array(), 0, 8);
			if(len>0){
				sock.getOutputStream().write(buf, 0, len);
			}
			return true;
		} catch (IOException e) {
			close(sock);
			return false;
		}
	}
	/**
	 * equals.
	 * @return true - equal
	 */
	public boolean equals(Object obj){
		if(!(obj instanceof Comm)){
			return false;
		}
		Comm cm = (Comm)obj;
		if(comm != cm.comm || slot != cm.slot || len != cm.len){
			return false;
		}
		for(int i=0;i<len;i++){
			if(buf[i] != cm.buf[i]){
				return false;
			}
		}
		return true;
	}
	/**
	 * to string.
	 * @return string
	 */
	public String toString(){
//		String str = (len == 0) ? "" : new String(buf);
		return "Comm:"+comm+','+slot+','+len; //+','+str;
	}
	/**
	 * force close.
	 * @param sock to close
	 */
	private static void close(Socket sock){
		try {
			sock.close();
		} catch (IOException e) {
			//
		}
	}
}
