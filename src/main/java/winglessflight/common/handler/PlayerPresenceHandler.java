package winglessflight.common.handler;

import java.util.ArrayList;

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
		WinglessFlight.flyingPlayers.put(event.player.getDisplayName(), 0);
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
	
	public void addListener(IPlayerPresenceHandler listener) {
		synchronized(listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
}