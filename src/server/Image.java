package server;

/**
 * 
 * @author Amy
 * Use this class to connect information like time when image was taken, from which camera it was taken from etc. to the actual image? 
 */
public class Image {
	private int time;
	private int camera;
	private int length;
	
	public Image (int time, int camera, int length){
		this.time = time;
		this.camera = camera;
		this.length = length;
	}
	
	//Getters for the attributes.
}
