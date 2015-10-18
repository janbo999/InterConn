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
	
	
	
	
	