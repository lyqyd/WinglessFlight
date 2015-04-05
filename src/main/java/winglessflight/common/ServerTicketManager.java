package winglessflight.common;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import winglessflight.common.handler.IPlayerPresenceHandler;
import winglessflight.common.handler.PlayerPresenceHandler;

public class ServerTicketManager implements IPlayerPresenceHandler {

	private HashMap<String, PlayerTicketManager> ticketManagers = new HashMap<String, PlayerTicketManager>();
	
	public static final ServerTicketManager instance = new ServerTicketManager();
	
	public void init() {
		PlayerPresenceHandler.instance.addListener(this);
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
		ticketManagers.put(id, new PlayerTicketManager(event.player));
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
}
