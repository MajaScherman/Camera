package client;

public class DataForUpdater {
	private int type;
	private int command;
	private boolean forceMode;
	private boolean movieMode;
	private boolean onlyOneImage;
	private int syncMode;
	private Image[] image;
	
	public DataForUpdater(int type, int c, boolean f, boolean m, boolean o, int s, Image[] image){
		this.type = type;
		command =c;
		forceMode =f;
		movieMode = m;
		onlyOneImage = o;
		syncMode = s;
		this.image = image;
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public boolean isForceMode() {
		return forceMode;
	}

	public void setForceMode(boolean forceMode) {
		this.forceMode = forceMode;
	}

	public boolean isMovieMode() {
		return movieMode;
	}

	public void setMovieMode(boolean movieMode) {
		this.movieMode = movieMode;
	}

	public boolean isOnlyOneImage() {
		return onlyOneImage;
	}

	public void setOnlyOneImage(boolean onlyOneImage) {
		this.onlyOneImage = onlyOneImage;
	}

	public int isSyncMode() {
		return syncMode;
	}

	public void setSyncMode(int syncMode) {
		this.syncMode = syncMode;
	}

	public Image[] getImage() {
		return image;
	}

	public void setImage(Image[] image) {
		this.image = image;
	}
	
	
	
}
