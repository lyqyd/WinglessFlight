package winglessflight;

import java.util.ArrayList;

import winglessflight.common.CommonProxy;
import winglessflight.common.block.FlightBlock;
import winglessflight.common.util.WFLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "WinglessFlight", name = "WinglessFlight", version = "0.0.1")
public class WinglessFlight {
	
	public static class Blocks {
		public static FlightBlock flightBlock;
	}
	
	public static ArrayList<String> fallingPlayers = new ArrayList<String>();
	
	@Instance(value = "WinglessFlight")
	public static WinglessFlight instance;
	
	@SidedProxy(clientSide = "winglessflight.client.ClientProxy", serverSide = "winglessflight.common.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		WFLog.init();
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
}