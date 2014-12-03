package client;

public class Image {
	private int cameraNbr;
	private byte[] image;
	private long timeStamp;
	private long delay;
	
	public Image(int cameraNbr, long timeStamp, long delay, byte[] image) {
		this.cameraNbr = cameraNbr;
		this.image = image;
		this.timeStamp = timeStamp;
		this.delay= delay;
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
}
