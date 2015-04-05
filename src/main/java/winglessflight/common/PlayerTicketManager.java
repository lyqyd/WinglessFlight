package winglessflight.common;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerTicketManager {
	
	private ArrayList<FlightTicket> ticketList = new ArrayList<FlightTicket>();
	private EntityPlayer player;
	
	public PlayerTicketManager (EntityPlayer player) {
		this.player = player;
	}

	public void addTicket(FlightTicket ticket) {
		boolean ticketExists = false;
		for (FlightTicket t : this.ticketList) {
			if (t.getLocation().compareTo(ticket.getLocation()) == 0) {
				ticketExists = true;
				break;
			}
		}
		if (!ticketExists) {
			ticketList.add(ticket);
			if (this.getFlightTicketCount() > 0) {
				this.player.capabilities.allowFlying = true;
				this.player.sendPlayerAbilities();
			}
		}
	}
	
	public void removeTicket(ChunkCoordinates location) {
		for (FlightTicket t : this.ticketList) {
			if (t.getLocation().compareTo(location) == 0) {
				this.ticketList.remove(t);
				break;
			}
		}
		if (this.getFlightTicketCount() == 0) {
			this.player.capabilities.allowFlying = false;
			this.player.capabilities.isFlying = false;
			this.player.sendPlayerAbilities();
		}
	}
	
	public void removeTicket(FlightTicket ticket) {
		for (FlightTicket t : this.ticketList) {
			if (t == ticket) {
				this.ticketList.remove(t);
				break;
			}
		}
	}
	
	public int getTicketCount() {
		return ticketList.size();
	}
	
	public int getFlightTicketCount() {
		int count = 0;
		for (FlightTicket t : this.ticketList) {
			if (t.isFlying()) {
				count++;
			}
		}
		return count;
	}
}
