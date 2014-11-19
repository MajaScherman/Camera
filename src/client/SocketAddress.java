package client;

public class SocketAddress {
	private String address;
	private int portNumber;

	/**
	 * This class holds values of the address and port number that we want the client socket to connect to.
	 * @param addr The domain of the server
	 * @param port The port number of the server
	 */
	public SocketAddress(String addr, int port){
		address = addr;
		portNumber = port;
	}
	
	public String getAddress(){
		return address;
	}
	
	public int getPortNumber(){
		return portNumber;
	}
	
}
