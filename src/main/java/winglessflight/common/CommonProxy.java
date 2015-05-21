package winglessflight.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import winglessflight.WinglessFlight;
import winglessflight.common.block.AdvancedFlightBlock;
import winglessflight.common.block.BasicFlightBlock;
import winglessflight.common.handler.FallDamageHandler;
import winglessflight.common.handler.PlayerPresenceHandler;
import winglessflight.common.util.WFLog;

public class CommonProxy {

	public void preInit() {
		MinecraftForge.EVENT_BUS.register(FallDamageHandler.instance);
		MinecraftForge.EVENT_BUS.register(PlayerPresenceHandler.instance);
		FMLCommonHandler.instance().bus().register(PlayerPresenceHandler.instance);
		ServerTicketManager.instance.init();
		registerBlocks();
	}
	
	public void init() {
		registerRecipes();
	}
	
	private void registerBlocks() {
		if (WinglessFlight.Config.enableAdvanced) {
			WinglessFlight.Blocks.flightBlockAdv = new AdvancedFlightBlock();
		}
		if (WinglessFlight.Config.enableBasic) {
			WinglessFlight.Blocks.flightBlockBsc = new BasicFlightBlock();
		}
	}
	
	private void registerRecipes() {
		int count = 0;
		ItemStack feather = new ItemStack((Item)Item.itemRegistry.getObject("feather"));
		
		if (WinglessFlight.Config.enableAdvanced && WinglessFlight.Config.enableAdvancedRecipe) { 
			ItemStack flightBlockAdv = new ItemStack(WinglessFlight.Blocks.flightBlockAdv);
			ItemStack diamond = new ItemStack((Block)Block.blockRegistry.getObject("diamond_block"));
			ItemStack emerald = new ItemStack((Item)Item.itemRegistry.getObject("emerald"));
			ItemStack gold = new ItemStack((Block)Block.blockRegistry.getObject("gold_block"));
			ItemStack diamond_item = new ItemStack((Item)Item.itemRegistry.getObject("diamond"));
			if (WinglessFlight.Config.cheapRecipe) {
				GameRegistry.addRecipe(flightBlockAdv,
						" g ",
						"ifi",
						" e ",
				        'e', emerald, 'f', feather, 'g', gold, 'i', diamond_item);
				count++;
			} else {
				GameRegistry.addRecipe(flightBlockAdv,
						"gig",
						"dfd",
						"eie",
				        'd', diamond, 'e', emerald, 'f', feather, 'g', gold, 'i', diamond_item);
				count++;
			}
		}
		
		if (WinglessFlight.Config.enableBasic && WinglessFlight.Config.enableBasicRecipe) {
			ItemStack flightBlockBsc = new ItemStack(WinglessFlight.Blocks.flightBlockBsc);
			ItemStack iron = new ItemStack((Block)Block.blockRegistry.getObject("iron_block"));
			GameRegistry.addRecipe(flightBlockBsc,
					" i ",
					" f ",
					" i ",
					'i', iron, 'f', feather);
			count++;
		}
		
		WFLog.debug("Added %d recipes", count);
	}
}
