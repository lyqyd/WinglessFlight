package winglessflight.common.handler;

import winglessflight.WinglessFlight;
import winglessflight.common.util.WFLog;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class FallDamageHandler {

	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP && WinglessFlight.fallingPlayers.contains(((EntityPlayerMP)event.entityLiving).getDisplayName())) {
			event.distance = 0f;
			WFLog.info("player %s fall damage reset", event.entityLiving.getCommandSenderName());
			WinglessFlight.fallingPlayers.remove(((EntityPlayerMP)event.entityLiving).getDisplayName());
		}
	}
}
