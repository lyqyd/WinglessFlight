package winglessflight.common.handler;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public interface IPlayerPresenceHandler {
	void onLogin(PlayerLoggedInEvent event);
	void onLogout(PlayerLoggedOutEvent event);
	void onWorldChange(EntityJoinWorldEvent event);
}
