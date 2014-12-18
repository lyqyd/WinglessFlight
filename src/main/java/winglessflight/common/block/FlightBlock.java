package winglessflight.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import winglessflight.common.tileentity.FlightTile;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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

}
