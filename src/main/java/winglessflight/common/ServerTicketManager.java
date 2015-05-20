package winglessflight.common;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import winglessflight.common.handler.FallDamageHandler;
import winglessflight.common.handler.IFallDamageHandler;
import winglessflight.common.handler.IPlayerPresenceHandler;
import winglessflight.common.handler.PlayerPresenceHandler;
import winglessflight.common.util.WFLog;

public class ServerTicketManager implements IPlayerPresenceHandler, IFallDamageHandler {

	private HashMap<String, PlayerTicketManager> ticketManagers = new HashMap<String, PlayerTicketManager>();
	
	public static final ServerTicketManager instance = new ServerTicketManager();
	
	public void init() {
		PlayerPresenceHandler.instance.addListener(this);
		FallDamageHandler.instance.addListener(this);
	}
	
	public PlayerTicketManager getManagerForPlayer(EntityPlayerMP player) {
		String id = player.getUniqueID().toString();
		if (ticketManagers.containsKey(id)) {
			return ticketManagers.get(id);
		}
		return null;
	}
	
	public PlayerTicketManager getManagerForPlayer(String id) {
		if (ticketManagers.containsKey(id)) {
			return ticketManagers.get(id);
		}
		return null;
	}

	@Override
	public void onLogin(PlayerLoggedInEvent event) {
		// TODO Auto-generated method stub
		String id = event.player.getUniqueID().toString();
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			ticketManagers.put(id, new PlayerTicketManager(player));
		} else {
			WFLog.debug("Event player isn't EntityPlayerMP onLogin");
		}
		
	}

	@Override
	public void onLogout(PlayerLoggedOutEvent event) {
		// TODO Make this save the flying/falling state.
		String id = event.player.getUniqueID().toString();
		ticketManagers.remove(id);
	}

	@Override
	public void onWorldChange(EntityJoinWorldEvent event) {
		// TODO Auto-generated method stub	
	}

	@Override
	public int getHandlerDimension() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
			String id = player.getUniqueID().toString();
			PlayerTicketManager manager = ticketManagers.get(id);
			if (manager.getTicketCount() > manager.getFlightTicketCount()) {
				// There is at least one falling-mode ticket, so cancel event damage.
				event.distance = 0f;
				// And then clear all falling-mode tickets.
				manager.removeFallingModeTickets();
			}
		}
	}
}
