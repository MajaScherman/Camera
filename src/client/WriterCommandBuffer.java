package client;

public class WriterCommandBuffer {
	private int[] commandBuffer;
	private int bufferIndex;
	private int nbrOfCommandEntries;

	public WriterCommandBuffer(int bufferSize) {
		bufferIndex = 0;
		commandBuffer = new int[bufferSize];
	}

	public void putCommandToWriterBuffer(int com) {
		commandBuffer[bufferIndex] = com;
		nbrOfCommandEntries++;
		bufferIndex++;
		if (bufferIndex >= commandBuffer.length) {
			bufferIndex = 0;
		}
		if (nbrOfCommandEntries >= commandBuffer.length) {
			nbrOfCommandEntries = commandBuffer.length;
		}
	}

	public int getCommandFromWriterBuffer() {
		if (nbrOfCommandEntries <= 0) {
			System.out.println("Failed to get command from writer buffer");
			return -1;
		} else {
			int com = commandBuffer[bufferIndex];
			nbrOfCommandEntries--;
			bufferIndex--;
			if (bufferIndex < 0) {
				bufferIndex = commandBuffer.length - 1;
			}
			return com;
		}
	}
	
	public int getNbrOfCommandsInWriterBuffer(){
		return nbrOfCommandEntries;
	}

}
