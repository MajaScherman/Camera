package client;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientMonitor {
	private String command;
	private String image;
	private boolean syncMode;
	private boolean movieMode;
	
	public ClientMonitor(){
		syncMode=false;
		movieMode=false;		
	}
	
	public void setResult(){
		//The reader sets the command, if one has been received, according to the info received from the inputstream
		//from the cilent socket. String getLine(InputStream s)?
		//If image then the reader does not set the command. but sets the image
	}
	
	public String getToServerCommand(){
		//The writer gets the command set by the reader. 
		//Combine with putLine(OutputStream s, String str)?
		return command;
		
	}
	
	public String getFromServerCommand(){
		//The writer gets the command set by the reader. 
		//Combine with putLine(OutputStream s, String str)?
		return command;
		
	}
	
	public String getImage(){
		//image is set by the reader if an image is received. This method is used by the updater which updates the GUI.
		//The update continously calls getImage() to check if there's a new image.
		//How to distinguish from which camera the image comes from?
		return image;
	}
	
	public boolean checkSyncMode(){
		//Checks if the cameras are in sync
		return syncMode;
		
	}
	
	public boolean changeModeFlag(){
		return movieMode;
		
	}
	
}
