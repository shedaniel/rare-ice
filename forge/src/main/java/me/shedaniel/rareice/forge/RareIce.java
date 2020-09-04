package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.RareIceBlock;
import me.shedaniel.rareice.forge.blocks.entities.RareIceTileEntity;
import me.shedaniel.rareice.forge.mixin.BlockAccessor;
import me.shedaniel.rareice.forge.proxy.CommonProxy;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceWorldGen;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Mod(modid = "rare-ice")
@Mod.EventBusSubscriber
public class RareIce {
    @SidedProxy(clientSide = "me.shedaniel.rareice.forge.proxy.ClientProxy", serverSide = "me.shedaniel.rareice.forge.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static final String RARE_ICE_BLOCK_LOC = "rare-ice:rare_ice";
    
    @GameRegistry.ObjectHolder(RARE_ICE_BLOCK_LOC)
    public static Block rareIceBlock;
    
    public static boolean allowInsertingItemsToIce = true;
    public static int probabilityOfRareIce = 6;
    
    public RareIce() {
        MinecraftForge.ORE_GEN_BUS.register(RareIceWorldGen.class);
    }
    
    private static void loadConfig(Path file) {
        allowInsertingItemsToIce = true;
        probabilityOfRareIce = 6;
        
        if (Files.exists(file)) {
            try {
                Properties properties = new Properties();
                properties.load(Files.newBufferedReader(file));
                allowInsertingItemsToIce = properties.getProperty("allowInsertingItemsToIce", "true").equals("true");
                probabilityOfRareIce = Integer.parseInt(properties.getProperty("probabilityOfRareIce", "6"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        saveConfig(file);
    }
    
    private static void saveConfig(Path file) {
        try {
            Files.createDirectories(file.getParent());
            Properties properties = new Properties();
            properties.setProperty("allowInsertingItemsToIce", String.valueOf(allowInsertingItemsToIce));
            properties.setProperty("probabilityOfRareIce", String.valueOf(probabilityOfRareIce));
            properties.store(Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE), "Rare Ice Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        loadConfig(event.getModConfigurationDirectory().toPath().resolve("rare-ice.properties"));
        proxy.preInit();
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(RareIceTileEntity.class, new ResourceLocation(RARE_ICE_BLOCK_LOC));
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(((BlockAccessor) new RareIceBlock().setHardness(0.5F).setLightOpacity(3)).invokeSetSoundType(SoundType.GLASS).setUnlocalizedName(RARE_ICE_BLOCK_LOC).setRegistryName(RARE_ICE_BLOCK_LOC));
    }
    
    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!allowInsertingItemsToIce) return;
        EntityPlayer player = event.getEntityPlayer();
        if (player.isSneaking()) return;
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        IBlockState state = world.getBlockState(pos);
        if ((state.getBlock() == Blocks.ICE || state.getBlock() == RareIce.rareIceBlock)) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity == null) {
                world.setBlockState(pos, RareIce.rareIceBlock.getDefaultState());
                blockEntity = world.getTileEntity(pos);
            }
            if (blockEntity instanceof RareIceTileEntity) {
                RareIceTileEntity rareIceBlockEntity = (RareIceTileEntity) blockEntity;
                ItemStack itemStack = player.getHeldItem(event.getHand());
                itemStack = player.capabilities.isCreativeMode ? itemStack.copy() : itemStack;
                EnumActionResult type = rareIceBlockEntity.addItem(world, itemStack, player, event.getSide().isServer());
                if (type != EnumActionResult.PASS)
                    event.setCanceled(true);
                event.setCancellationResult(type);
            }
        }
    }
}
