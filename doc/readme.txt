used to expose service running behind NAT / router / proxy.

for example:
	having web server running behind router, but can't control router settings - 
	can't open incoming port on router.
	
	having another location with control on it's router or exposed directly to internet.
	let it's address is "exp.com".
	here start "java -jar InterConn.jar SERVER 8080 4000",
	open incoming ports on router: 8080 - service port & 4000 - tunnel port.
	
	on first machine start:"java -jar InterConn.jar CLIENT exp.com 4000 localhost 8080"
	
	now connection exp.com:8080 reaches server on first machine. 
	
how it works:
	server part ( on exposed location) waits for incoming connection on tunnel port.
	
	client part connects to server on tunnel port and establishes tunnel connection.
	
	server waits for connections on service port.
	
	when something connected to server, server establishes connection through tunnel
	to client, client establishes connection to service on it's side.
	
tunnel transfers:
	CONN - handshake to ensure same protocol.
	PING - ignored on both sides, just ensure tunnel is alive.
	OPEN - open new connection.
	DATA - send data in both direction.
	CLOSE - close connection - in both direction if detected closed service connection / service client connection.
	if tunnel connection closed, server starts waiting for another, client starts trying to connect.
	
proxy:
	to use with proxy, set java socks properties: 
	"socksProxyHost", "socksProxyPort", "socksProxyVersion", "java.net.socks.username", "java.net.socks.password"
	on client side.   
	
	