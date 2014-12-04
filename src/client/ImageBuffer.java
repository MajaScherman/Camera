package client;

public class ImageBuffer {
	
		private Image[] imageBuffer;
		private int putIndex, getIndex;
		private int nbrOfImageEntries;

		public ImageBuffer(int bufferSize) {
			putIndex = getIndex = 0;
			imageBuffer = new Image[bufferSize];
		}

		public void putImageToBuffer(Image image) {
			imageBuffer[putIndex] = image;
			putIndex++;
			if (putIndex >= imageBuffer.length) {
				putIndex = 0;
			}
			if (nbrOfImageEntries < imageBuffer.length -1) {
				nbrOfImageEntries++;
			}
		}

		public Image getImageFromBuffer() {
			if (nbrOfImageEntries <= 0) {
				System.out.println("Failed to get command from writer buffer");
				return null;
			} else {
				Image img = imageBuffer[getIndex];
				nbrOfImageEntries--;
				getIndex++;
				if (getIndex >= imageBuffer.length) {
					getIndex = 0;
				}
				return img;
			}
		}
		
		public int getNbrOfImagesInBuffer(){
			return nbrOfImageEntries;
		}

	
}
