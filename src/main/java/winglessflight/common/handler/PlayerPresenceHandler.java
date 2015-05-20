package winglessflight.common.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import winglessflight.WinglessFlight;
import winglessflight.common.util.WFLog;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class PlayerPresenceHandler {
	
	public static final PlayerPresenceHandler instance = new PlayerPresenceHandler();
	
	public final ArrayList<IPlayerPresenceHandler> listeners = new ArrayList<IPlayerPresenceHandler>();

	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent event) {
		synchronized(listeners) {
			for (IPlayerPresenceHandler listener : listeners) {
				listener.onLogin(event);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeft(PlayerLoggedOutEvent event) {
		synchronized(listeners) {
			for (IPlayerPresenceHandler listener : listeners) {
				listener.onLogout(event);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.entity != null && event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entity;
			int dimension = event.world.provider.dimensionId;
			WFLog.debug("PPH EJW %s %d", player.getDisplayName(), dimension);
			String name = player.getDisplayName();
			synchronized(listeners) {
				for(IPlayerPresenceHandler listener : listeners) {
					if (listener.getHandlerDimension() == dimension) {
						listener.onWorldChange(event);
					}
				}
				for(IPlayerPresenceHandler listener : listeners) {
					if(listener.getHandlerDimension() != dimension) {
						listener.onWorldChange(event);
					}
				}
			}
		}
	}
	
	public void addListener(IPlayerPresenceHandler listener) {
		synchronized(listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
}