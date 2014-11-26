package client;

public class SocketAddress {
	private String host;
	private int portNumber;

	/**
	 * This class holds values of the address and port number that we want the client socket to connect to.
	 * @param addr The domain of the server
	 * @param port The port number of the server
	 */
	public SocketAddress(String addr, int port){
		host = addr;
		portNumber = port;
	}
	
	public String getHost(){
		return host;
	}
	
	public int getPortNumber(){
		return portNumber;
	}
	
}
