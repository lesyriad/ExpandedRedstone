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

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.NEI_AnonymousHideConfig;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ExpandedRedstone.Base.AnalogWireless;
import Reika.ExpandedRedstone.Registry.RedstoneBlocks;
import Reika.ExpandedRedstone.Registry.RedstoneItems;
import Reika.ExpandedRedstone.Registry.RedstoneOptions;
import Reika.ExpandedRedstone.Registry.RedstoneTiles;
import Reika.RotaryCraft.API.BlockColorInterface;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = "ExpandedRedstone", name="ExpandedRedstone", version="Gamma", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
clientPacketHandlerSpec = @SidedPacketHandler(channels = { "ExpandedData" }, packetHandler = ExpandedClientPackets.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = { "ExpandedData" }, packetHandler = ExpandedServerPackets.class))

public class ExpandedRedstone extends DragonAPIMod {

	@Instance("ExpandedRedstone")
	public static ExpandedRedstone instance = new ExpandedRedstone();

	public static final String packetChannel = "ExpandedData";

	public static final ControlledConfig config = new ControlledConfig(instance, RedstoneOptions.optionList, RedstoneBlocks.blockList, RedstoneItems.itemList, null, 1);

	public static Block[] blocks = new Block[RedstoneBlocks.blockList.length];

	public static Item[] items = new Item[RedstoneItems.itemList.length];

	public static final TabRedstone tab = new TabRedstone(CreativeTabs.getNextID(), instance.getDisplayName());

	public static ModLogger logger;

	@SidedProxy(clientSide="Reika.ExpandedRedstone.ClientProxy", serverSide="Reika.ExpandedRedstone.CommonProxy")
	public static CommonProxy proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(this);

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		logger = new ModLogger(instance, RedstoneOptions.LOGLOADING.getState(), RedstoneOptions.DEBUGMODE.getState(), false);
		proxy.registerSounds();

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.addBlocks();
		this.addItems();
		NetworkRegistry.instance().registerGuiHandler(instance, new GuiLoader());
		proxy.registerRenderers();
		this.addRecipes();

		if (ModList.NEI.isLoaded())
			NEI_AnonymousHideConfig.addBlocks(blocks);
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < RedstoneBlocks.blockList.length; i++) {
				RedstoneBlocks r = RedstoneBlocks.blockList[i];
				for (int k = 0; k < r.getNumberMetadatas(); k++)
					BlockColorInterface.addGPRBlockColor(r.getBlockID(), k, ReikaColorAPI.RGBtoHex(140, 140, 140));
			}
		}
	}

	@ForgeSubscribe
	public void onClose(WorldEvent.Unload evt) {
		AnalogWireless.resetChannelData();
		logger.debug("Resetting wireless data.");

		//TileEntityEqualizer.unregisterAllInWorld(evt.world.provider.dimensionId);
	}

	private static void addItems() {
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, RedstoneItems.itemList, items);
	}

	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, RedstoneBlocks.blockList, blocks);
		for (int i = 0; i < RedstoneTiles.TEList.length; i++)
			GameRegistry.registerTileEntity(RedstoneTiles.TEList[i].getTEClass(), "ExpRedstone"+RedstoneTiles.TEList[i].getName());
	}

	private static void addRecipes() {
		RedstoneTiles.PLACER.addRecipe("CCC", "CDC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'D', Block.dispenser);
		RedstoneTiles.CLOCK.addRecipe("srs", "rer", "srs", 's', ReikaItemHelper.stoneSlab, 'r', Item.redstone, 'e', Item.netherQuartz);
		RedstoneTiles.BUD.addRecipe("ccc", "rne", "ccc", 'c', Block.cobblestone, 'r', Item.redstone, 'n', Item.netherQuartz, 'e', Item.enderPearl);
		RedstoneTiles.CAMOFLAGE.addSizedRecipe(8, "rir", "iei", "rir", 'r', Item.redstone, 'i', Item.ingotIron, 'e', Item.eyeOfEnder);
		RedstoneTiles.CHESTREADER.addRecipe(" c ", "rer", " s ", 's', ReikaItemHelper.stoneSlab, 'r', Item.redstone, 'e', Item.netherQuartz, 'c', Block.chestTrapped);
		RedstoneTiles.DRIVER.addRecipe(" s ", "rer", " s ", 's', ReikaItemHelper.stoneSlab, 'r', Item.redstone, 'e', Item.netherQuartz);
		RedstoneTiles.EFFECTOR.addRecipe("CCC", "NDE", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'D', Block.dispenser, 'N', Item.netherQuartz, 'E', Item.enderPearl);
		RedstoneTiles.PROXIMITY.addRecipe("rdr", "nen", "sss", 's', ReikaItemHelper.stoneSlab, 'r', Item.redstone, 'e', Item.enderPearl, 'd', Item.diamond, 'n', Item.netherQuartz);
		RedstoneTiles.TOGGLE.addRecipe("tRt", "rer", "sRs", 't', Block.torchRedstoneActive, 'R', Item.redstoneRepeater, 's', ReikaItemHelper.stoneSlab, 'r', Item.redstone, 'e', Item.netherQuartz);
		RedstoneTiles.WEATHER.addRecipe("nSn", "rnr", "sss", 's', ReikaItemHelper.stoneSlab, 'r', Item.redstone, 'n', Item.netherQuartz, 'S', Block.daylightSensor);
		RedstoneTiles.PUMP.addRecipe("crc", "cCc", "cbc", 'c', Block.cobblestone, 'r', Item.redstone, 'C', Block.chest, 'b', Item.bucketEmpty);
		RedstoneTiles.HOPPER.addRecipe(" n ", "rtr", " r ", 't', RedstoneTiles.CLOCK.getItem(), 'r', Item.redstone, 'n', Item.netherQuartz);

		RedstoneItems.BLUEWIRE.addSizedShapelessRecipe(2, ReikaItemHelper.lapisDye, Item.redstone);

		RedstoneTiles.BREAKER.addNBTRecipe(3, "CCC", "CPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.pickaxeDiamond);
		RedstoneTiles.BREAKER.addNBTRecipe(2, "CCC", "CPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.pickaxeIron);
		RedstoneTiles.BREAKER.addNBTRecipe(1, "CCC", "CPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.pickaxeStone);
		RedstoneTiles.BREAKER.addNBTRecipe(0, "CCC", "CPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.pickaxeWood);

		RedstoneTiles.EMITTER.addSizedRecipe(4, "CCC", "CRG", "CCC", 'C', Block.cobblestone, 'R', Item.redstone, 'G', Block.glowStone);
		RedstoneTiles.RECEIVER.addSizedRecipe(4, "CCC", "GRC", "CCC", 'C', Block.cobblestone, 'R', Item.emerald, 'G', Block.glass);

		RedstoneTiles.SHOCK.addNBTRecipe(0, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Block.thinGlass); 	//range 1; damage 0.5
		RedstoneTiles.SHOCK.addNBTRecipe(1, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Item.netherQuartz); //range 1; damage 1
		RedstoneTiles.SHOCK.addNBTRecipe(2, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Block.glowStone); 	//range 2; damage 0.5
		RedstoneTiles.SHOCK.addNBTRecipe(3, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Item.enderPearl); 	//range 2; damage 1
		RedstoneTiles.SHOCK.addNBTRecipe(4, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Item.diamond); 		//range 2; damage 2
		RedstoneTiles.SHOCK.addNBTRecipe(5, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Item.emerald); 		//range 3; damage 2
		RedstoneTiles.SHOCK.addNBTRecipe(6, "CCC", "LPC", "CRC", 'C', Block.cobblestone, 'R', Item.redstone, 'P', Item.eyeOfEnder, 'L', Item.netherStar);	//range 5; damage infinity

		RedstoneTiles.SCALER.addRecipe("rnr", "sss", 'r', Item.redstone, 'n', Item.netherQuartz, 's', ReikaItemHelper.stoneSlab);
		RedstoneTiles.ANALOGTRANSMITTER.addSizedRecipe(2, "rrr", "nen", "sss", 'r', Item.redstone, 'n', Item.netherQuartz, 's', ReikaItemHelper.stoneSlab, 'e', Item.enderPearl);
		RedstoneTiles.ANALOGRECEIVER.addSizedRecipe(2, "rrr", "ene", "sss", 'r', Item.redstone, 'n', Item.netherQuartz, 's', ReikaItemHelper.stoneSlab, 'e', Item.enderPearl);
		RedstoneTiles.COLUMN.addRecipe("CCC", "RRR", "CRC", 'R', Item.redstone, 'C', Block.cobblestone);
	}

	@Override
	public String getDisplayName() {
		return "Expanded Redstone";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return true;
	}

	@Override
	public String getVersionName() {
		return "Gamma";
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

}
