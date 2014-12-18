package winglessflight.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import winglessflight.WinglessFlight;
import winglessflight.common.util.WFLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class FlightTile extends TileEntity {
	
	private ArrayList<EntityPlayerMP> trackedPlayers = new ArrayList<EntityPlayerMP>();
	private ArrayList<String> fallingPlayers = new ArrayList<String>();

	private void dropPlayer(EntityPlayerMP player) {
		WFLog.info("dropping player %s", player.getDisplayName());
		int count = WinglessFlight.flyingPlayers.get(player.getDisplayName());
		count--;
		WinglessFlight.flyingPlayers.put(player.getDisplayName(), count);
		WFLog.info("player %s has %d active flights", player.getDisplayName(), count);
		if (!player.onGround) {
			if (!this.fallingPlayers.contains(player.getDisplayName())) {
				this.fallingPlayers.add(player.getDisplayName());
			}
			if (!WinglessFlight.fallingPlayers.contains(player.getDisplayName())) {
				WinglessFlight.fallingPlayers.add(player.getDisplayName());
			}
		}
		//disable flying if necessary
		if (!player.capabilities.isCreativeMode && count == 0) {
			WFLog.info("player %s flight disabled", player.getDisplayName());
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
		WFLog.info("player %s given flight, has %d active flights", player.getDisplayName(), count);
		if (this.fallingPlayers.contains(player.getDisplayName())) {
			this.fallingPlayers.remove(player.getDisplayName());
		}
		if (WinglessFlight.fallingPlayers.contains(player.getDisplayName())) {
			WinglessFlight.fallingPlayers.remove(player.getDisplayName());
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		List<EntityPlayerMP> players = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox((float)this.xCoord - 24.0f, 0.0f, (float)this.zCoord - 24.0f, (float)this.xCoord + 24.0f, 256.0f, (float)this.zCoord + 24.0f));
		for (EntityPlayerMP player : players) {
			if (!this.trackedPlayers.contains(player)) {
				WFLog.info("player %s in range", player.getDisplayName());
				if (player.isDead) {continue;}
				
				flyPlayer(player);
			}
		}
		
		for (EntityPlayerMP tracked : this.trackedPlayers) {
			if (!players.contains(tracked)) {
				WFLog.info("player %s left range", tracked.getDisplayName());
				dropPlayer(tracked);
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
		
		NBTTagList flyingNames = tag.getTagList("FlyingNames", 10);
		for (int i = 0; i < names.tagCount(); i++) {
			NBTTagCompound info = names.getCompoundTagAt(i);
			String name = info.getString("Name");
			EntityPlayer player = worldObj.getPlayerEntityByName(name);
			if (player != null && player instanceof EntityPlayerMP) {
				this.trackedPlayers.add((EntityPlayerMP) player);
				player.capabilities.allowFlying = true;
				int count = 0;
				if (WinglessFlight.flyingPlayers.containsKey(player.getDisplayName())) {
					count = WinglessFlight.flyingPlayers.get(player.getDisplayName());
				}
				count++;
				WinglessFlight.flyingPlayers.put(player.getDisplayName(), count);
				if (info.getBoolean("IsFlying")) {
					player.capabilities.isFlying = true;
				}
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		NBTTagList names = new NBTTagList();
		for (String name : this.fallingPlayers) {
			names.appendTag(new NBTTagString(name));
		}
		NBTTagList flying = new NBTTagList();
		for (EntityPlayerMP player : this.trackedPlayers) {
			NBTTagCompound info = new NBTTagCompound();
			info.setString("Name", player.getCommandSenderName());
			info.setBoolean("IsFlying", player.capabilities.isFlying);
			flying.appendTag(info);
		}
		
		tag.setTag("Names", names);
		tag.setTag("FlyingNames", flying);
	}
	
	public void dropAllFlyers() {
		for (EntityPlayerMP player : this.trackedPlayers) {
			dropPlayer(player);
		}
	}

}
