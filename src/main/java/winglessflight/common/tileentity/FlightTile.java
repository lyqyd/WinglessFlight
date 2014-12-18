package winglessflight.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import winglessflight.WinglessFlight;
import winglessflight.common.util.WFLog;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class FlightTile extends TileEntity {
	
	private ArrayList<EntityPlayerMP> trackedPlayers = new ArrayList<EntityPlayerMP>();
	private ArrayList<String> fallingPlayers = new ArrayList<String>();

	@Override
	public void updateEntity() {
		super.updateEntity();
		List<EntityPlayerMP> players = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox((float)this.xCoord - 24.0f, 0.0f, (float)this.zCoord - 24.0f, (float)this.xCoord + 24.0f, 256.0f, (float)this.zCoord + 24.0f));
		for (EntityPlayerMP player : players) {
			if (!this.trackedPlayers.contains(player)) {
				WFLog.info("player %s in range", player.getDisplayName());
				if (player.isDead) {continue;}
				
				player.capabilities.allowFlying = true;
				player.sendPlayerAbilities();
				WFLog.info("player %s given flight", player.getDisplayName());
				if (WinglessFlight.fallingPlayers.contains(player.getDisplayName())) {
					WinglessFlight.fallingPlayers.remove(player.getDisplayName());
				}
				if (this.fallingPlayers.contains(player)) {
					this.fallingPlayers.remove(player);
				}
			}
		}
		
		for (EntityPlayerMP tracked : this.trackedPlayers) {
			if (!players.contains(tracked) && !tracked.onGround) {
				WFLog.info("player %s left range", tracked.getDisplayName());
				//player is outside the bounds now and is in the air.
				if (!this.fallingPlayers.contains(tracked.getDisplayName())) {
					this.fallingPlayers.add(tracked.getDisplayName());
				}
				if (!WinglessFlight.fallingPlayers.contains(tracked.getDisplayName())) {
					WinglessFlight.fallingPlayers.add(tracked.getDisplayName());
				}
				//disable flying if necessary
				if (!tracked.capabilities.isCreativeMode) {
					WFLog.info("player %s flight disabled", tracked.getDisplayName());
					tracked.capabilities.allowFlying = false;
					tracked.capabilities.isFlying = false;
					tracked.sendPlayerAbilities();
				}
			}
		}
		
		//remove all players from tracking list and add them fresh from the AABB list.
		this.trackedPlayers.clear();
		this.trackedPlayers.addAll(players);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		NBTTagList names = tag.getTagList("Names", 8);
		for (int i = 0; i < names.tagCount(); i++) {
			String name = names.getStringTagAt(i);
			this.fallingPlayers.add(name);
			WinglessFlight.fallingPlayers.add(name);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		NBTTagList names = new NBTTagList();
		for (String name : this.fallingPlayers) {
			names.appendTag(new NBTTagString(name));
		}
		for (EntityPlayerMP player : this.trackedPlayers) {
			names.appendTag(new NBTTagString(player.getDisplayName()));
		}
		
		tag.setTag("Names", names);
	}

}
