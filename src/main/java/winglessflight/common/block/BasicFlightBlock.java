package winglessflight.common.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import winglessflight.WinglessFlight;
import winglessflight.common.tileentity.BasicFlightTile;
import cpw.mods.fml.common.registry.GameRegistry;

public class BasicFlightBlock extends FlightBlockBase {
	
	public BasicFlightBlock() {
		super(WinglessFlight.Config.silkTouchRequiredBsc, new ItemStack(Item.getItemFromBlock((Block)Block.blockRegistry.getObject("iron_block")), 2));
		GameRegistry.registerBlock(this, "flightBlockBsc");
		GameRegistry.registerTileEntity(BasicFlightTile.class, "flightBlockBsc");
		setBlockName("winglessflight.bscflightblock");
		this.setBlockTextureName("winglessflight:basic_flightblock");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(this.textureName);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BasicFlightTile();
	}
	
}
