package client;

public class Image {
	private int cameraNbr;
	private byte[] image;
	private byte[] timeStamp;
	
	public Image(int cameraNbr, byte[] timeStamp, byte[] image) {
		this.cameraNbr = cameraNbr;
		this.image = image;
		this.timeStamp = timeStamp;
	}

	public int getCameraNbr() {
		return cameraNbr;
	}

	public byte[] getImage() {
		return image;
	}
	
	public byte[]  getTimeStamp(){
		return timeStamp;
	}
}
