package winglessflight.common.block;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import winglessflight.WinglessFlight;
import winglessflight.common.tileentity.FlightTileBase;
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

public abstract class FlightBlockBase extends BlockContainer {

	
	private boolean requiresSilkTouch = false;
	private ItemStack drop;
	
	public FlightBlockBase() {
		super(Material.ground);
		setHardness(0.5F);
		setCreativeTab(CreativeTabs.tabTransport);
	}
	
	public FlightBlockBase(boolean silk, ItemStack droppedIfNotSilky) {
		this();
		this.requiresSilkTouch = silk;
		this.drop = droppedIfNotSilky;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int n) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof FlightTileBase) {
			((FlightTileBase)tile).dropAllFlyers();
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
		if (this.requiresSilkTouch && metadata == 1) {
			if (this.drop != null) {
				drops.add(this.drop);
			}
		} else {
			drops.add(new ItemStack(Item.getItemFromBlock(this), 1));
		}
		return drops;
	}

}
