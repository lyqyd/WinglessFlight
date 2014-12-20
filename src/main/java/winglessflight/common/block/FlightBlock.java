package winglessflight.common.block;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import winglessflight.WinglessFlight;
import winglessflight.common.tileentity.FlightTile;
import winglessflight.common.util.WFLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class FlightBlock extends BlockContainer {

	public IIcon[] icons = new IIcon[6];
	
	public FlightBlock() {
		super(Material.ground);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabTransport);
		GameRegistry.registerBlock(this, "flightBlock");
		GameRegistry.registerTileEntity(FlightTile.class, "flightBlock");
		setBlockName("winglessflight.flightblock");
		this.setBlockTextureName("winglessflight:flightblock");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < 6; i++) {
			this.icons[i] = register.registerIcon(this.textureName + "_" + Math.min(i, 2));
		}
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[side];
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new FlightTile();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int n) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof FlightTile) {
			((FlightTile)tile).dropAllFlyers();
		}
		super.breakBlock(world, x, y, z, block, n);
	}
	
	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return true;
    }
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if (WinglessFlight.Config.silkTouchRequired && metadata == 1) {
			drops.add(new ItemStack(Item.getItemFromBlock((Block)Block.blockRegistry.getObject("diamond_block")), 2));
		} else {
			drops.add(new ItemStack(Item.getItemFromBlock(this), 1));
		}
		return drops;
	}

}
