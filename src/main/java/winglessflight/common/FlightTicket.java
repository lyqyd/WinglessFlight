package winglessflight.common;

import net.minecraft.util.ChunkCoordinates;

public class FlightTicket {
	
	private ChunkCoordinates flightBlock;
	private int flightState;

	public FlightTicket(int x, int y, int z, int dimension, String playerID) {
		this.flightBlock = new ChunkCoordinates(x, y, z);
		this.flightState = 1;
	}
	
	public ChunkCoordinates getLocation() {
		return this.flightBlock;
	}
	
	public boolean isFlying() {
		return this.flightState == 1;
	}
	
	public boolean isFalling() {
		return this.flightState == 2;
	}
	
	public void setFlying() {
		this.flightState = 1;
	}
	
	public void setFalling() {
		this.flightState = 2;
	}
}
