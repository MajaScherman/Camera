package client;

public class Image {
	private int cameraNbr;
	private byte[] image;
	private long timeStamp;
	private long delay;
	
	public Image(int cameraNbr, long timeStamp,  byte[] image) {
		this.cameraNbr = cameraNbr;
		this.image = image;
		this.timeStamp = timeStamp;
		delay = 0;
	}

	public int getCameraNbr() {
		return cameraNbr;
	}

	public byte[] getImage() {
		return image;
	}
	
	public long  getTimeStamp(){
		return timeStamp;
	}
	public boolean isSynchronized(){
		if(delay<200){
			return true;
		}else{
			return false;
		}
		
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long transferDelay) {
		delay = transferDelay;
	}
}
