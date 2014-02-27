/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ExpandedRedstone;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import Reika.ExpandedRedstone.Base.ExpandedRedstoneTileEntity;
import Reika.ExpandedRedstone.ItemBlocks.BlockRedTile;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.ExpandedRedstone.TileEntities.TileEntityCamo;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CircuitBlockRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block b, int meta, int modelID, RenderBlocks rb) {
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		Tessellator tessellator = Tessellator.instance;

		rb.renderMaxY = 1;
		if (r.isThinTile())
			rb.renderMaxY = 0.1875;

		if (r != RedstoneTiles.CAMOFLAGE) {
			rb.renderMaxX = 1;
			rb.renderMinY = 0;
			rb.renderMaxZ = 1;
			rb.renderMinX = 0;
			rb.renderMinZ = 0;
		}

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		rb.renderFaceYNeg(b, 0.0D, 0.0D, 0.0D, rb.getBlockIconFromSideAndMetadata(b, 0, meta));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		rb.renderFaceYPos(b, 0.0D, 0.0D, 0.0D, rb.getBlockIconFromSideAndMetadata(b, 1, meta));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		rb.renderFaceZNeg(b, 0.0D, 0.0D, 0.0D, rb.getBlockIconFromSideAndMetadata(b, 2, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		rb.renderFaceZPos(b, 0.0D, 0.0D, 0.0D, rb.getBlockIconFromSideAndMetadata(b, 3, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		rb.renderFaceXNeg(b, 0.0D, 0.0D, 0.0D, rb.getBlockIconFromSideAndMetadata(b, 4, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		rb.renderFaceXPos(b, 0.0D, 0.0D, 0.0D, rb.getBlockIconFromSideAndMetadata(b, 5, meta));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int modelId, RenderBlocks rb) {
		ExpandedRedstoneTileEntity te = (ExpandedRedstoneTileEntity)world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		RedstoneTiles r = RedstoneTiles.TEList[meta];
		Icon[] ico = new Icon[6];
		Icon[] overlay = new Icon[6];
		Icon front = ((BlockRedTile)b).getFrontTexture(world, x, y, z);
		for (int i = 0; i < 6; i++)
			ico[i] = rb.getBlockIcon(b, world, x, y, z, i);
		Tessellator v5 = Tessellator.instance;

		double maxx = b.getBlockBoundsMaxX();
		double minx = b.getBlockBoundsMinX();
		double miny = b.getBlockBoundsMinY();
		double maxy = b.getBlockBoundsMaxY();
		double maxz = b.getBlockBoundsMaxZ();
		double minz = b.getBlockBoundsMinZ();

		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;
		float f7 = f4;
		float f8 = f4;
		float f9 = f4;
		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		f10 = f3;
		f11 = f5;
		f12 = f6;
		f13 = f3;
		f14 = f5;
		f15 = f6;
		f16 = f3;
		f17 = f5;
		f18 = f6;

		int l = b.getMixedBrightnessForBlock(world, x, y, z);

		v5.setBrightness(rb.renderMaxY < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y+1, z));
		v5.setColorOpaque_F(f7, f8, f9);

		float[] mult = {1, 1, 1};
		int[] color = {255, 255, 255};

		v5.addTranslation(x, y, z);
		ForgeDirection dir = te.getFacing();
		if (r.isReversedTopTexture())
			dir = dir.getOpposite();
		if (!r.isThinTile())
			dir = ForgeDirection.WEST;
		if (r == RedstoneTiles.CAMOFLAGE) {
			TileEntityCamo cam = (TileEntityCamo)te;
			int metaread = te.worldObj.getBlockMetadata(te.xCoord, te.yCoord-1, te.zCoord);
			if (ico[1] == Block.grass.getBlockTextureFromSide(1)) {
				color = ReikaBiomeHelper.biomeToRGB(world, x, z, "Grass");
				v5.setColorOpaque(color[0], color[1], color[2]);
				if (rb.fancyGrass)
					for (int i = 2; i < 6; i++) {
						overlay[i] = BlockGrass.getIconSideOverlay();
					}
			}
			if (ico[1] == Block.leaves.getIcon(1, metaread)) {
				metaread = metaread & 3;
				color = ReikaBiomeHelper.biomeToRGB(world, x, z, "Leaves");
				if (metaread == 0 || metaread == 3) {
					for (int i = 0; i < 3; i++) {
						mult[i] = color[i]/255F;
					}
				}
				else if (metaread == 1) {
					int hex = ColorizerFoliage.getFoliageColorPine();
					Color c = Color.decode(String.valueOf(hex));
					color[0] = c.getRed();
					color[1] = c.getGreen();
					color[2] = c.getBlue();
					for (int i = 0; i < 3; i++) {
						mult[i] = color[i]/255F;
					}
				}
				else if (metaread == 2) {
					int hex = ColorizerFoliage.getFoliageColorBirch();
					Color c = Color.decode(String.valueOf(hex));
					color[0] = c.getRed();
					color[1] = c.getGreen();
					color[2] = c.getBlue();
					for (int i = 0; i < 3; i++) {
						mult[i] = color[i]/255F;
					}
				}
				v5.setColorOpaque(color[0], color[1], color[2]);
			}
		}
		switch(dir) {
		case WEST:
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMinU(), ico[1].getMinV());
			break;
		case NORTH:
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMinU(), ico[1].getMinV());
			break;
		case SOUTH:
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMinU(), ico[1].getMinV());
			break;
		case EAST:
			v5.addVertexWithUV(1, maxy, 1, ico[1].getMinU(), ico[1].getMaxV());
			v5.addVertexWithUV(1, maxy, 0, ico[1].getMaxU(), ico[1].getMaxV());
			v5.addVertexWithUV(0, maxy, 0, ico[1].getMaxU(), ico[1].getMinV());
			v5.addVertexWithUV(0, maxy, 1, ico[1].getMinU(), ico[1].getMinV());
			break;
		default:
			break;
		}
		v5.addTranslation(-x, -y, -z);

		v5.setBrightness(rb.renderMinY > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y-1, z));
		v5.setColorOpaque_F(mult[0]*f10, mult[1]*f13, mult[2]*f16);
		rb.renderFaceYNeg(b, x, y, z, ico[0]);

		v5.setBrightness(rb.renderMinZ > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
		v5.setColorOpaque_F(mult[0]*f11, mult[1]*f14, mult[2]*f17);
		rb.renderFaceZNeg(b, x, y, z, ico[2]);

		v5.setBrightness(rb.renderMaxZ < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
		v5.setColorOpaque_F(mult[0]*f11, mult[1]*f14, mult[2]*f17);
		rb.renderFaceZPos(b, x, y, z, ico[3]);

		v5.setBrightness(rb.renderMinX > 0.0D ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
		v5.setColorOpaque_F(mult[0]*f12, mult[1]*f15, mult[2]*f18);
		rb.renderFaceXNeg(b, x, y, z, ico[4]);

		v5.setBrightness(rb.renderMaxX < 1.0D ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
		v5.setColorOpaque_F(mult[0]*f12, mult[1]*f15, mult[2]*f18);
		rb.renderFaceXPos(b, x, y, z, ico[5]);

		if (overlay[0] != null) {
			v5.setBrightness(rb.renderMinY > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y-1, z));
			v5.setColorOpaque_F(color[0]*f10/255F, color[1]*f13/255F, color[2]*f16/255F);
			rb.renderFaceYNeg(b, x, y, z, overlay[0]);
		}

		if (overlay[2] != null) {
			v5.setBrightness(rb.renderMinZ > 0.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z - 1));
			v5.setColorOpaque_F(color[0]*f11/255F, color[1]*f14/255F, color[2]*f17/255F);
			rb.renderFaceZNeg(b, x, y, z, overlay[2]);
		}

		if (overlay[3] != null) {
			v5.setBrightness(rb.renderMaxZ < 1.0D ? l : b.getMixedBrightnessForBlock(world, x, y, z + 1));
			v5.setColorOpaque_F(color[0]*f11/255F, color[1]*f14/255F, color[2]*f17/255F);
			rb.renderFaceZPos(b, x, y, z, overlay[3]);
		}

		if (overlay[4] != null) {
			v5.setBrightness(rb.renderMinX > 0.0D ? l : b.getMixedBrightnessForBlock(world, x - 1, y, z));
			v5.setColorOpaque_F(color[0]*f12/255F, color[1]*f15/255F, color[2]*f18/255F);
			rb.renderFaceXNeg(b, x, y, z, overlay[4]);
		}

		if (overlay[5] != null) {
			v5.setBrightness(rb.renderMaxX < 1.0D ? l : b.getMixedBrightnessForBlock(world, x + 1, y, z));
			v5.setColorOpaque_F(color[0]*f12/255F, color[1]*f15/255F, color[2]*f18/255F);
			rb.renderFaceXPos(b, x, y, z, overlay[5]);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ExpandedRedstone.proxy.tileRender;
	}

}
