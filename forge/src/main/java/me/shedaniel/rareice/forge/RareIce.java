package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.RareIceBlock;
import me.shedaniel.rareice.forge.blocks.entities.RareIceTileEntity;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceConfig;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceFeature;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Mod("rare-ice")
public class RareIce {
    
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "rare-ice");
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "rare-ice");
    
    public static final RegistryObject<Block> RARE_ICE_BLOCK = BLOCK_REGISTRY.register("rare_ice", () ->
            new RareIceBlock(AbstractBlock.Properties.from(Blocks.ICE).allowsSpawning((state, world, pos, type) -> type == EntityType.POLAR_BEAR).harvestTool(ToolType.PICKAXE).harvestLevel(0)));
    public static final RegistryObject<TileEntityType<RareIceTileEntity>> RARE_ICE_TILE_ENTITY_TYPE = TILE_ENTITY_REGISTRY.register("rare_ice", () ->
            TileEntityType.Builder.create(RareIceTileEntity::new, RARE_ICE_BLOCK.get()).build(null));
    public static final Feature<RareIceConfig> RARE_ICE_FEATURE = new RareIceFeature(RareIceConfig.CODEC);
    
    public static boolean allowInsertingItemsToIce = true;
    
    private static void loadConfig(Path file) {
        allowInsertingItemsToIce = true;
        
        if (Files.exists(file)) {
            try {
                Properties properties = new Properties();
                properties.load(Files.newBufferedReader(file));
                allowInsertingItemsToIce = properties.getOrDefault("allowInsertingItemsToIce", "true").equals("true");
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
            properties.store(Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE), "Rare Ice Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public RareIce() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK_REGISTRY.register(bus);
        TILE_ENTITY_REGISTRY.register(bus);
        
        bus.addListener(RareIce::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(RareIce::rightClickBlock);
    }
    
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        loadConfig(FMLPaths.CONFIGDIR.get().resolve("rare-ice.properties"));
        ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome.getDefaultTemperature() < 0.15F).forEach(RareIce::handleBiome);
    }
    
    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!allowInsertingItemsToIce) return;
        PlayerEntity player = event.getPlayer();
        if (player.isSneaking()) return;
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        BlockState state = world.getBlockState(pos);
        if ((state.getBlock() == Blocks.ICE || state.getBlock() == RareIce.RARE_ICE_BLOCK.get())) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity == null) {
                world.setBlockState(pos, RareIce.RARE_ICE_BLOCK.get().getDefaultState());
                blockEntity = world.getTileEntity(pos);
            }
            if (blockEntity instanceof RareIceTileEntity) {
                RareIceTileEntity rareIceBlockEntity = (RareIceTileEntity) blockEntity;
                ItemStack itemStack = player.getHeldItem(event.getHand());
                itemStack = player.abilities.isCreativeMode ? itemStack.copy() : itemStack;
                ActionResultType type = rareIceBlockEntity.addItem(world, itemStack, player, event.getSide().isServer());
                if (type != ActionResultType.PASS)
                    event.setCanceled(true);
                event.setCancellationResult(type);
            }
        }
    }
    
    private static void handleBiome(Biome biome) {
        if (biome.getDefaultTemperature() < 0.15f) {
            biome.addFeature(
                    GenerationStage.Decoration.TOP_LAYER_MODIFICATION,
                    RARE_ICE_FEATURE.withConfiguration(RareIceConfig.DEFAULT).withPlacement(
                            Placement.RANDOM_COUNT_RANGE.configure(new CountRangeConfig(16, 32, 128, 256))
                    ));
        }
    }
}
