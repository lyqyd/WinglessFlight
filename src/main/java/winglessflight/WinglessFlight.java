package winglessflight;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import winglessflight.common.CommonProxy;
import winglessflight.common.block.AdvancedFlightBlock;
import winglessflight.common.block.BasicFlightBlock;
import winglessflight.common.util.WFLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "WinglessFlight", name = "WinglessFlight", version = "0.0.7")
public class WinglessFlight {
	
	public static class Blocks {
		public static AdvancedFlightBlock flightBlockAdv;
		public static BasicFlightBlock flightBlockBsc;
	}
	
	public static class Config {
		public static boolean enableAdvanced;
		public static boolean enableAdvancedRecipe;
		public static boolean silkTouchRequiredAdv;
		public static int chargeTimeAdv;
		public static int radiusAdv;
		public static boolean cheapRecipe;
		
		public static boolean enableBasic;
		public static boolean enableBasicRecipe;
		public static boolean silkTouchRequiredBsc;
		public static int chargeTimeBsc;
		public static int radiusBsc;
		
		public static boolean debug = false;
	}
	
	@Instance(value = "WinglessFlight")
	public static WinglessFlight instance;
	
	@SidedProxy(clientSide = "winglessflight.client.ClientProxy", serverSide = "winglessflight.common.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		WFLog.init();
		
		Configuration configFile = new Configuration(event.getSuggestedConfigurationFile());
		
		Property prop = configFile.get("balance", "silkTouchRequiredAdvanced", true);
		prop.comment = "Advanced Flight Blocks require silk touch to pick up intact";
		Config.silkTouchRequiredAdv = prop.getBoolean();
		
		prop = configFile.get("balance", "chargeTimeAdvanced", 10);
		prop.comment = "Time Advanced Flight Blocks require to charge before working, in seconds";
		Config.chargeTimeAdv = prop.getInt();
		
		prop = configFile.get("balance", "radiusAdvanced", 32);
		prop.comment = "Distance in blocks for Advanced Flight Blocks to enable flight";
		Config.radiusAdv = prop.getInt();
		
		prop = configFile.get("balance", "cheapRecipeAdvanced", false);
		prop.comment = "Use a significantly cheaper crafting recipe for the Advanced Flight Blocks.";
		Config.cheapRecipe = prop.getBoolean();
		
		prop = configFile.get("balance", "silkTouchRequiredBasic", false);
		prop.comment = "Basic Flight Blocks require silk touch to pick up intact";
		Config.silkTouchRequiredBsc = prop.getBoolean();
		
		prop = configFile.get("balance", "chargeTimeBasic", 30);
		prop.comment = "Time Basic Flight Blocks require to charge before working, in seconds";
		Config.chargeTimeBsc = prop.getInt();
		
		prop = configFile.get("balance", "radiusBasic", 6);
		prop.comment = "Distance in blocks for Basic Flight Blocks to enable flight";
		Config.radiusBsc = prop.getInt();
		
		prop = configFile.get("general", "enableBasic", true);
		prop.comment = "Enable Basic Flight Blocks";
		Config.enableBasic = prop.getBoolean();
		
		prop = configFile.get("general", "enableBasicRecipe", true);
		prop.comment = "Enable Basic Flight Block Recipe";
		Config.enableBasicRecipe = prop.getBoolean();
		
		prop = configFile.get("general", "enableAdvanced", true);
		prop.comment = "Enable Advanced Flight Blocks";
		Config.enableAdvanced = prop.getBoolean();
		
		prop = configFile.get("general", "enableAdvancedRecipe", true);
		prop.comment = "Enable Advanced Flight Block Recipe";
		Config.enableAdvancedRecipe = prop.getBoolean();

		configFile.save();
		
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
}