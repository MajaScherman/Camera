package client;

public class CommandBuffer {
	private int[] commandBuffer;
	private int putIndex, getIndex;
	private int nbrOfCommandEntries;

	public CommandBuffer(int bufferSize) {
		putIndex = getIndex = 0;
		commandBuffer = new int[bufferSize];
	}

	public void putCommandToBuffer(int com) {
		commandBuffer[putIndex] = com;
		nbrOfCommandEntries++;
		putIndex++;
		if (putIndex >= commandBuffer.length) {
			putIndex = 0;
		}
		if (nbrOfCommandEntries >= commandBuffer.length) {
			nbrOfCommandEntries = commandBuffer.length;
		}
	}

	public int getCommandFromBuffer() {
		if (nbrOfCommandEntries <= 0) {
			System.out.println("Failed to get command from writer buffer");
			return -1;
		} else {
			int com = commandBuffer[getIndex];
			nbrOfCommandEntries--;
			getIndex++;
			if (getIndex >= commandBuffer.length) {
				getIndex = 0;
			}
			return com;
		}
	}
	
	public int getNbrOfCommandsInBuffer(){
		return nbrOfCommandEntries;
	}

}
