package winglessflight.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import winglessflight.WinglessFlight;
import winglessflight.common.handler.FallDamageHandler;
import winglessflight.common.handler.IFallDamageHandler;
import winglessflight.common.handler.IPlayerPresenceHandler;
import winglessflight.common.handler.PlayerPresenceHandler;
import winglessflight.common.util.WFLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class FlightTile extends TileEntity implements IPlayerPresenceHandler, IFallDamageHandler {
	
	private ArrayList<EntityPlayerMP> trackedPlayers = new ArrayList<EntityPlayerMP>();
	private ArrayList<String> trackedNames = new ArrayList<String>();
	private ArrayList<String> fallingPlayers = new ArrayList<String>();
	private ArrayList<String> flyingWhenLeft = new ArrayList<String>();
	private boolean enabled = false;
	private int chargeTime = 0;
	
	public FlightTile() {
		PlayerPresenceHandler.instance.addListener(this);
		FallDamageHandler.instance.addListener(this);
		if (WinglessFlight.Config.chargeTime == 0) {
			this.enabled = true;
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 6);
		}
	}
	
	private Vec3 randomSpot(int x0, int y0, int z0) {
		double radius = 5.0d;
		double u = Math.random();
		double v = Math.random();
		double theta = 2 * Math.PI * u;
		double phi = Math.acos(2 * v - 1);
		double x = x0 + (radius * Math.sin(phi) * Math.cos(theta));
		double y = y0 + (radius * Math.sin(phi) * Math.sin(theta));
		double z = z0 + (radius * Math.cos(phi));
		return Vec3.createVectorHelper(x, y, z);
	}

	private void dropPlayer(EntityPlayerMP player) {
		int count = WinglessFlight.flyingPlayers.get(player.getDisplayName());
		count--;
		if (WinglessFlight.Config.debug) {
			WFLog.info("%d%d%d dropping player %s, %d tickets", this.xCoord, this.yCoord, this.zCoord, player.getDisplayName(), count);
		}
		WinglessFlight.flyingPlayers.put(player.getDisplayName(), Math.max(count, 0));
		if (!player.onGround && count == 0) {
			if (!this.fallingPlayers.contains(player.getDisplayName())) {
				this.fallingPlayers.add(player.getDisplayName());
			}
		}
		//disable flying if necessary
		if (!player.capabilities.isCreativeMode && count == 0) {
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
			player.sendPlayerAbilities();
		}
	}
	
	private void flyPlayer(EntityPlayerMP player) {
		player.capabilities.allowFlying = true;
		player.sendPlayerAbilities();
		int count = 0;
		if (WinglessFlight.flyingPlayers.containsKey(player.getDisplayName())) {
			count = WinglessFlight.flyingPlayers.get(player.getDisplayName());
		}
		count++;
		WinglessFlight.flyingPlayers.put(player.getDisplayName(), count);
		if (WinglessFlight.Config.debug) {
			WFLog.info("%d%d%d flying player %s, %d tickets", this.xCoord, this.yCoord, this.zCoord, player.getDisplayName(), count);
		}
		if (this.fallingPlayers.contains(player.getDisplayName())) {
			this.fallingPlayers.remove(player.getDisplayName());
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote){ 
			if (this.enabled) {
				float radius = (float) WinglessFlight.Config.radius;
				List<EntityPlayerMP> players = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox((float)this.xCoord - radius, 0.0f, (float)this.zCoord - radius, (float)this.xCoord + radius, 256.0f, (float)this.zCoord + radius));
				for (EntityPlayerMP player : players) {
					if (!this.trackedPlayers.contains(player) && !this.trackedNames.contains(player.getDisplayName())) {
						if (player.isDead) {continue;}
						
						flyPlayer(player);
					}
				}
				
				for (EntityPlayerMP tracked : this.trackedPlayers) {
					if (!players.contains(tracked)) {
						dropPlayer(tracked);
					}
				}
				
				//remove all players from tracking list and add them fresh from the AABB list.
				this.trackedPlayers.clear();
				this.trackedPlayers.addAll(players);
			} else {
				this.chargeTime++;
				if (this.chargeTime > WinglessFlight.Config.chargeTime * 20) {
					this.enabled = true;
					worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 6);
				}
			}
		} else {
			if (this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) != 1) {
				//spawn particles to show charging.
				double velocity = 0.6d;
				for (int i = 0; i < 4; i++) {
					Vec3 vec = this.randomSpot(this.xCoord, this.yCoord, this.zCoord);
					this.worldObj.spawnParticle("crit", vec.xCoord + 0.5d, vec.yCoord + 0.5d, vec.zCoord + 0.5d, ((double)this.xCoord - vec.xCoord) * velocity, ((double)this.yCoord - vec.yCoord) * velocity, ((double)this.zCoord - vec.zCoord) * velocity);
				}
			}
		}
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		NBTTagList names = tag.getTagList("Names", 8);
		for (int i = 0; i < names.tagCount(); i++) {
			String name = names.getStringTagAt(i);
			this.fallingPlayers.add(name);
		}
		NBTTagList flying = tag.getTagList("FlyingNames", 8);
		for (int i = 0; i < flying.tagCount(); i++) {
			this.flyingWhenLeft.add(flying.getStringTagAt(i));
		}
		
		this.chargeTime = tag.getInteger("chargeTime");
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		NBTTagList names = new NBTTagList();
		for (String name : this.fallingPlayers) {
			names.appendTag(new NBTTagString(name));
		}
		NBTTagList flying = new NBTTagList();
		for (EntityPlayerMP player : this.trackedPlayers) {
			if (player.capabilities.isFlying) {
				flying.appendTag(new NBTTagString(player.getDisplayName()));
			}
		}
		for (String name : this.flyingWhenLeft) {
			flying.appendTag(new NBTTagString(name));
		}
		
		tag.setTag("Names", names);
		tag.setTag("FlyingNames", flying);
		tag.setInteger("chargeTime", this.chargeTime);
	}
	
	public void dropAllFlyers() {
		for (EntityPlayerMP player : this.trackedPlayers) {
			dropPlayer(player);
		}
	}

	@Override
	public void onLogin(PlayerLoggedInEvent event) {
		if (this.flyingWhenLeft.contains(event.player.getDisplayName())) {
			this.flyingWhenLeft.remove(event.player.getDisplayName());
			int radius = WinglessFlight.Config.radius;
			if (event.player.posX >= this.xCoord - radius && event.player.posX <= this.xCoord + radius && event.player.posY >= 0 && event.player.posY <= 256 && event.player.posZ >= this.zCoord - radius && event.player.posZ <= this.zCoord + radius) {
				event.player.capabilities.allowFlying = true;
				event.player.capabilities.isFlying = true;
			}
		}
	}

	@Override
	public void onLogout(PlayerLoggedOutEvent event) {
		int radius = WinglessFlight.Config.radius;
		if (event.player.capabilities.isFlying && event.player.posX >= this.xCoord - radius && event.player.posX <= this.xCoord + radius && event.player.posY >= 0 && event.player.posY <= 256 && event.player.posZ >= this.zCoord - radius && event.player.posZ <= this.zCoord + radius) {
			this.flyingWhenLeft.add(event.player.getDisplayName());
		}
	}

	@Override
	public void onFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP && this.fallingPlayers.contains(((EntityPlayerMP)event.entityLiving).getDisplayName())) {
			event.distance = 0f;
			this.fallingPlayers.remove(((EntityPlayerMP)event.entityLiving).getDisplayName());
		}
		
	}

	@Override
	public void onWorldChange(EntityJoinWorldEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entity;
		if (event.world.provider.dimensionId != this.worldObj.provider.dimensionId) {
			for (EntityPlayerMP tracked : this.trackedPlayers) {
				if (WinglessFlight.Config.debug) WFLog.info("dropping player %s", player.getDisplayName());
				if (player.getDisplayName() == tracked.getDisplayName()) {
					this.fallingPlayers.add(player.getDisplayName());
				}
			}
		}
	}

}
