package winglessflight;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import winglessflight.common.CommonProxy;
import winglessflight.common.block.FlightBlock;
import winglessflight.common.util.WFLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "WinglessFlight", name = "WinglessFlight", version = "0.0.5")
public class WinglessFlight {
	
	public static class Blocks {
		public static FlightBlock flightBlock;
	}
	
	public static class Config {
		public static boolean silkTouchRequired;
		public static int chargeTime;
		public static int radius;
		public static boolean cheapRecipe;
		public static boolean debug = false;
	}
	
	public static HashMap<String, Integer> flyingPlayers = new HashMap();
	
	@Instance(value = "WinglessFlight")
	public static WinglessFlight instance;
	
	@SidedProxy(clientSide = "winglessflight.client.ClientProxy", serverSide = "winglessflight.common.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		WFLog.init();
		
		Configuration configFile = new Configuration(event.getSuggestedConfigurationFile());
		
		Property prop = configFile.get("balance", "silkTouchRequired", true);
		prop.comment = "Flight Blocks require silk touch to pick up intact";
		Config.silkTouchRequired = prop.getBoolean();
		
		prop = configFile.get("balance", "chargeTime", 10);
		prop.comment = "Time Flight Blocks require to charge before working, in seconds";
		Config.chargeTime = prop.getInt();
		
		prop = configFile.get("balance", "radius", 32);
		prop.comment = "Distance in blocks for Flight Blocks to enable flight";
		Config.radius = prop.getInt();
		
		prop = configFile.get("balance", "cheapRecipe", false);
		prop.comment = "Use a significantly cheaper crafting recipe for the flight blocks.";
		Config.cheapRecipe = prop.getBoolean();

		configFile.save();
		
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
}