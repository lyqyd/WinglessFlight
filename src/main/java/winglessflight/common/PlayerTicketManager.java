package winglessflight.common;

import java.util.ArrayList;
import java.util.function.Predicate;

import winglessflight.common.util.WFLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerTicketManager {
	
	private ArrayList<FlightTicket> ticketList = new ArrayList<FlightTicket>();
	private EntityPlayerMP player;
	
	public PlayerTicketManager (EntityPlayerMP player) {
		this.player = player;
	}
	
	public void update() {
		WFLog.debug("Update for player %s: %d/%d, %b", this.player.getDisplayName(), this.getFlightTicketCount(), this.getTicketCount(), this.player.capabilities.allowFlying);
		if (this.getFlightTicketCount() > 0 && !this.player.capabilities.allowFlying) {
			WFLog.debug("Player %s granted flight, %d/%d tickets", this.player.getDisplayName(), this.getFlightTicketCount(), this.getTicketCount());
			this.player.capabilities.allowFlying = true;
		} else if (this.getFlightTicketCount() == 0 && this.player.capabilities.allowFlying) {
			WFLog.debug("Player %s flight removed, %d/%d tickets", this.player.getDisplayName(), this.getFlightTicketCount(), this.getTicketCount());
			this.player.capabilities.allowFlying = false;
			this.player.capabilities.isFlying = false;
		}
		this.player.sendPlayerAbilities();
		if (this.player.onGround || this.getFlightTicketCount() > 0) {
			// Player is on ground or still has flying tickets, remove all falling-mode tickets
			this.removeFallingModeTickets(false);
		}
		WFLog.debug("Finalize update for player %s: %d/%d, %b", this.player.getDisplayName(), this.getFlightTicketCount(), this.getTicketCount(), this.player.capabilities.allowFlying);
	}

	public void addTicket(FlightTicket ticket) {
		boolean ticketExists = false;
		for (FlightTicket t : this.ticketList) {
			if (t.getLocation().compareTo(ticket.getLocation()) == 0) {
				WFLog.debug("Discarding old ticket from %s as duplicate",  ticket.getLocation().toString());
				ticketList.remove(t);
				break;
			}
		}
		ticketList.add(ticket);
		WFLog.debug("Adding ticket from %s", ticket.getLocation().toString());
		this.update();
	}
	
	public void removeTicket(ChunkCoordinates location) {
		for (FlightTicket t : this.ticketList) {
			if (t.getLocation().compareTo(location) == 0) {
				this.ticketList.remove(t);
				WFLog.debug("Removing ticket from %s", location.toString());
				break;
			}
		}
		this.update();
	}
	
	public void removeTicket(FlightTicket ticket) {
		for (FlightTicket t : this.ticketList) {
			if (t == ticket) {
				this.ticketList.remove(t);
				WFLog.debug("Removing ticket from %s", ticket.getLocation().toString());
				break;
			}
		}
		this.update();
	}
	
	private class FallingTickets<T> implements Predicate<FlightTicket> {
		@Override
		public boolean test(FlightTicket t) {
			return t.isFalling();
		}
	}
	
	protected void removeFallingModeTickets() {
		this.removeFallingModeTickets(true);
	}
	
	private void removeFallingModeTickets(boolean update) {
		ticketList.removeIf(new FallingTickets<FlightTicket>());
		if (update) {
			this.update();
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
