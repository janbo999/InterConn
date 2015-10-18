/**
 * main
 * @author Janbo
 */

public class TestServer {
	/**
	 * display help & exit.
	 */
	private static void help(){
		System.out.println(
			"use: InterConn SERVER wPport tPort\n"+
			"use: InterConn CLIENT tAddr tPort sAddr sPort\n"+
			"use: InterConn CHAIN wPort tAddr tPort sAddr sPort\n"+
			"\n"+
			"   wPort - port to expose service\n"+
			"   tPort - IP tunnel port\n"+
			"   tAddr - tunnel server address\n"+
			"	sAddr - service address\n"+
			"   sPort - service port\n"+
			"\n"+
			"for example:\n"+
			"	on machine exposing service(exp.com) : java -jar interconn.jar SERVER 8080 4000\n"+
			"	on machine running service : java -jar interconn.jar CLIENT exp.com 4000 localhost 8080\n"+
			"\n"
		);
		System.exit(0);
	}
	/**
	 * main.
	 * @param args arguments
	 */
	public static void main(String[] args){
		System.out.println("InterComm "+Defs.VERSION);
		try {
			if(args.length==0){
				help();
			}
			switch(args[0].toUpperCase()){
			case "SERVER":
				new TunServer(Utl.latoi(args[2]), Utl.latoi(args[1]));
				break;
			case "CLIENT":
				new TunClient(args[1], Utl.latoi(args[2]), args[3], Utl.latoi(args[4]));
				break;
			case "CHAIN":
				new TunServer(Utl.latoi(args[3]), Utl.latoi(args[1]));
				new TunClient(args[2], Utl.latoi(args[3]), args[4], Utl.latoi(args[5]));
				break;
			default:
				help();
			}
			
			while(true){
				Utl.idle(1000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			//
		}
	}
}
