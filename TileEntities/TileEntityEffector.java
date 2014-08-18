/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone.TileEntities;

import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import Reika.ExpandedRedstone.Base.InventoriedRedstoneTileEntity;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;

import java.util.List;

import net.minecraft.block.BlockSkull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEntityEffector extends InventoriedRedstoneTileEntity {

	private boolean lastPower;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z);
		if (ReikaRedstoneHelper.isPositiveEdge(world, x, y, z, lastPower)) {
			this.useItem();
		}
		lastPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	private void useItem() {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				this.fakeClick(i, is);
				return;
			}
		}
	}

	private void fakeClick(int slot, ItemStack is) {
		World world = worldObj;
		int dx = this.getFacingX();
		int dy = this.getFacingY();
		int dz = this.getFacingZ();
		EntityPlayer ep = this.getPlacer();
		Item it = is.getItem();
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(dx, dy, dz);
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		boolean flag = true;
		for (int i = 0; i < li.size() && flag; i++) {
			EntityLivingBase e = li.get(i);
			flag = !it.itemInteractionForEntity(is, ep, e);
		}
		if (flag) {
			if (is.getItem() == Items.skull) {
				it.onItemUse(is, ep, world, dx, dy-1, dz, 1, 0F, 0F, 0F);
				if (is.getItemDamage() == 1) {
					TileEntitySkull te = (TileEntitySkull)world.getTileEntity(dx, dy, dz);
					if (te != null)
						((BlockSkull)Blocks.skull).func_149965_a(world, dx, dy, dz, te);
				}
			}
			else {
				it.onItemUse(is, ep, world, dx, dy, dz, this.getFacing().getOpposite().ordinal(), 0F, 0F, 0F);
			}
		}
		inv[slot] = is;
		if (RedstoneOptions.NOISES.getState())
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click");
	}

	@Override
	public int getTEIndex() {
		return RedstoneTiles.EFFECTOR.ordinal();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int getFrontTexture() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 9;
	}
}
