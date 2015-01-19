package winglessflight.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import winglessflight.WinglessFlight;
import winglessflight.common.block.FlightBlock;
import winglessflight.common.handler.FallDamageHandler;
import winglessflight.common.handler.PlayerPresenceHandler;

public class CommonProxy {

	public void preInit() {
		MinecraftForge.EVENT_BUS.register(FallDamageHandler.instance);
		MinecraftForge.EVENT_BUS.register(PlayerPresenceHandler.instance);
		FMLCommonHandler.instance().bus().register(PlayerPresenceHandler.instance);
		registerBlocks();
	}
	
	public void init() {
		registerRecipes();
	}
	
	private void registerBlocks() {
		WinglessFlight.Blocks.flightBlock = new FlightBlock();
	}
	
	private void registerRecipes() {
		ItemStack flightBlock = new ItemStack(WinglessFlight.Blocks.flightBlock);
		ItemStack diamond = new ItemStack((Block)Block.blockRegistry.getObject("diamond_block"));
		ItemStack emerald = new ItemStack((Item)Item.itemRegistry.getObject("emerald"));
		ItemStack feather = new ItemStack((Item)Item.itemRegistry.getObject("feather"));
		ItemStack gold = new ItemStack((Block)Block.blockRegistry.getObject("gold_block"));
		ItemStack diamond_item = new ItemStack((Item)Item.itemRegistry.getObject("diamond"));
		if (WinglessFlight.Config.cheapRecipe) {
			GameRegistry.addRecipe(flightBlock,
					" g ",
					"ifi",
					" e ",
			        'e', emerald, 'f', feather, 'g', gold, 'i', diamond_item);
		} else {
			GameRegistry.addRecipe(flightBlock,
					"gig",
					"dfd",
					"eie",
			        'd', diamond, 'e', emerald, 'f', feather, 'g', gold, 'i', diamond_item);
		}
	}
}
