package winglessflight.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import winglessflight.WinglessFlight;
import winglessflight.common.FlightTicket;
import winglessflight.common.PlayerTicketManager;
import winglessflight.common.ServerTicketManager;
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
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class FlightTile extends TileEntity implements IPlayerPresenceHandler, IFallDamageHandler {

	private ArrayList<String> flyingWhenLeft = new ArrayList<String>();
	private ArrayList<String> fallingWhenLeft = new ArrayList<String>();
	private HashMap<EntityPlayerMP, FlightTicket> tickets = new HashMap<EntityPlayerMP, FlightTicket>();
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
		String id = player.getUniqueID().toString();
		int count = 0;
		PlayerTicketManager manager = ServerTicketManager.instance.getManagerForPlayer(player);
		if (manager != null) {
			count = manager.getTicketCount();
			WFLog.debug("%d, %d, %d dropping player %s, %d -> %d flight tickets", this.xCoord, this.yCoord, this.zCoord, player.getDisplayName(), count, count--);
			//don't remove the ticket here, it'll be removed either below, where it's removed from this instance, or in the fall damage event.
		}
		
		if (!player.onGround && count == 0) {
			this.tickets.get(player).setFalling();
		} else {
			if (manager != null) {
				manager.removeTicket(this.tickets.get(player));
			}
			this.tickets.remove(player);
		}
	}
	
	private void flyPlayer(EntityPlayerMP player) {
		int count = 0;
		PlayerTicketManager manager = ServerTicketManager.instance.getManagerForPlayer(player);
		String id = player.getUniqueID().toString();
		FlightTicket ticket;
		if (this.tickets.containsKey(player)) {
			ticket = this.tickets.get(player);
			ticket.setFlying();
		} else {
			ticket = new FlightTicket(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId, id);
			this.tickets.put(player, ticket);
		}
		if (manager != null) {
			count = manager.getFlightTicketCount();
			manager.addTicket(ticket);
			WFLog.debug("%d, %d, %d flying player %s, %d -> %d tickets", this.xCoord, this.yCoord, this.zCoord, player.getDisplayName(), count, count++);
		}
	}
	
	@Override
	public void invalidate() {
		this.dropAllFlyers();
		super.invalidate();
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote){ 
			if (this.enabled) {
				float radius = (float) WinglessFlight.Config.radius;
				List<EntityPlayerMP> players = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox((float)this.xCoord - radius, 0.0f, (float)this.zCoord - radius, (float)this.xCoord + radius, 256.0f, (float)this.zCoord + radius));
				for (EntityPlayerMP player : players) {
					if (!this.tickets.containsKey(player)) {
						if (player.isDead) {continue;}
						
						flyPlayer(player);
					}
				}
				
				for (EntityPlayerMP tracked : this.tickets.keySet()) {
					if (!players.contains(tracked)) {
						dropPlayer(tracked);
					}
				}
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
			this.fallingWhenLeft.add(name);
		}
		NBTTagList flying = tag.getTagList("FlyingNames", 8);
		for (int i = 0; i < flying.tagCount(); i++) {
			this.flyingWhenLeft.add(flying.getStringTagAt(i));
		}
		NBTTagList ids = tag.getTagList("UUIDs", 8);
		NBTTagList flyingIDs = tag.getTagList("FlyingUUIDs", 8);
		
		this.chargeTime = tag.getInteger("chargeTime");
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		NBTTagList names = new NBTTagList();
		NBTTagList flying = new NBTTagList();
		for (Entry<EntityPlayerMP, FlightTicket> entry : this.tickets.entrySet()) {
			EntityPlayerMP player = entry.getKey();
			String id = player.getUniqueID().toString();
			if (entry.getValue().isFalling()) {
				names.appendTag(new NBTTagString(id));
			} else if (player.capabilities.isFlying) {
				flying.appendTag(new NBTTagString(id));
			}
		}
		for (String name : this.flyingWhenLeft) {
			flying.appendTag(new NBTTagString(name));
		}
		for (String name : this.fallingWhenLeft) {
			names.appendTag(new NBTTagString(name));
		}
		
		tag.setTag("UUIDs", names);
		tag.setTag("FlyingUUIDs", flying);
		tag.setInteger("chargeTime", this.chargeTime);
	}
	
	public void dropAllFlyers() {
		for (EntityPlayerMP player : this.tickets.keySet()) {
			dropPlayer(player);
		}
	}

	@Override
	public void onLogin(PlayerLoggedInEvent event) {
		if (this.flyingWhenLeft.contains(event.player.getDisplayName())) {
			this.flyingWhenLeft.remove(event.player.getDisplayName());
			int radius = WinglessFlight.Config.radius;
			if (event.player.posX >= this.xCoord - radius && event.player.posX <= this.xCoord + radius && event.player.posY >= 0 && event.player.posY <= 256 && event.player.posZ >= this.zCoord - radius && event.player.posZ <= this.zCoord + radius) {
				if (event.player instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP)event.player;
					flyPlayer(player);
				} else {
					WFLog.debug("Player logged in, but event player is not instance of EntityPlayerMP");
				}
			}
		}
	}

	@Override
	public void onLogout(PlayerLoggedOutEvent event) {
		int radius = WinglessFlight.Config.radius;
		PlayerTicketManager manager = ServerTicketManager.instance.getManagerForPlayer(event.player.getUniqueID().toString());
		if (event.player.capabilities.isFlying && event.player.posX >= this.xCoord - radius && event.player.posX <= this.xCoord + radius && event.player.posY >= 0 && event.player.posY <= 256 && event.player.posZ >= this.zCoord - radius && event.player.posZ <= this.zCoord + radius) {
			this.flyingWhenLeft.add(event.player.getDisplayName());
		} else if (manager.getFlightTicketCount() == 0) {
			
		}
	}

	@Override
	public void onFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
			String id = player.getUniqueID().toString();
			if (this.tickets.containsKey(player) && this.tickets.get(player).isFalling()) {
				event.distance = 0f;
				
				int count = 0;
				PlayerTicketManager manager = ServerTicketManager.instance.getManagerForPlayer(player);
				if (manager != null) {
					manager.removeTicket(this.tickets.get(player));
				}
				this.tickets.remove(player);
			}
		}
		
	}

	@Override
	public void onWorldChange(EntityJoinWorldEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.entity;
		if (event.world.provider.dimensionId != this.worldObj.provider.dimensionId) {
			if (this.tickets.containsKey(player)) {
				this.tickets.get(player).setFalling();
			}
		}
	}

}
