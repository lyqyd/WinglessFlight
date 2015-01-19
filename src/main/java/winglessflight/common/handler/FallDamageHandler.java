package winglessflight.common.handler;

import java.util.ArrayList;

import winglessflight.WinglessFlight;
import winglessflight.common.util.WFLog;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class FallDamageHandler {
	
	public static final FallDamageHandler instance = new FallDamageHandler();
	
	public final ArrayList<IFallDamageHandler> listeners = new ArrayList<IFallDamageHandler>();

	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		synchronized(listeners) {
			for (IFallDamageHandler listener : listeners) {
				listener.onFall(event);
			}
		}
	}
	
	public void addListener(IFallDamageHandler listener) {
		synchronized(listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	public void removeListener(IFallDamageHandler listener) {
		synchronized(listeners) {
			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
		}
	}
}
