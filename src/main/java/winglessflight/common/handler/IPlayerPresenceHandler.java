package winglessflight.common.handler;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public interface IPlayerPresenceHandler {
	void onLogin(PlayerLoggedInEvent event);
	void onLogout(PlayerLoggedOutEvent event);
}
