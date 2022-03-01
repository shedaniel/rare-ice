package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.RareIceBlock;
import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceConfig;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Mod("rare_ice")
public class RareIce {
    
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, "rare-ice");
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "rare-ice");
    public static final DeferredRegister<Feature<?>> FEATURE_REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, "rare-ice");
    
    public static final RegistryObject<Block> RARE_ICE_BLOCK = BLOCK_REGISTRY.register("rare_ice", () ->
            new RareIceBlock(BlockBehaviour.Properties.copy(Blocks.ICE).isValidSpawn((state, world, pos, type) -> type == EntityType.POLAR_BEAR)));
    public static final RegistryObject<BlockEntityType<RareIceBlockEntity>> RARE_ICE_TILE_ENTITY_TYPE = TILE_ENTITY_REGISTRY.register("rare_ice", () ->
            BlockEntityType.Builder.of(RareIceBlockEntity::new, RARE_ICE_BLOCK.get()).build(null));
    public static final RegistryObject<Feature<RareIceConfig>> RARE_ICE_FEATURE = FEATURE_REGISTRY.register("rare_ice", () -> new RareIceFeature(RareIceConfig.CODEC));
    public static ConfiguredFeature<?, ?> configuredFeature;
    public static PlacedFeature placedFeature;
    public static boolean allowInsertingItemsToIce = true;
    public static int probabilityOfRareIce = 3;
    
    private static void loadConfig(Path file) {
        allowInsertingItemsToIce = true;
        probabilityOfRareIce = 3;
        
        if (Files.exists(file)) {
            try {
                Properties properties = new Properties();
                properties.load(Files.newBufferedReader(file));
                allowInsertingItemsToIce = properties.getProperty("allowInsertingItemsToIce", "true").equals("true");
                probabilityOfRareIce = Integer.parseInt(properties.getProperty("probabilityOfRareIce", "3"));
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
    
    public RareIce() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK_REGISTRY.register(bus);
        TILE_ENTITY_REGISTRY.register(bus);
        FEATURE_REGISTRY.register(bus);
        
        bus.addListener(RareIce::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(RareIce::rightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(RareIce::modifyBiome);
    }
    
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        loadConfig(FMLPaths.CONFIGDIR.get().resolve("rare-ice.properties"));
        configuredFeature = RARE_ICE_FEATURE.get().configured(RareIceConfig.DEFAULT);
        placedFeature = configuredFeature.placed(CountPlacement.of(probabilityOfRareIce),
                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(32), VerticalAnchor.belowTop(32)));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation("rare-ice:rare-ice"), configuredFeature);
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation("rare-ice:rare-ice"), placedFeature);
    }
    
    public static void modifyBiome(BiomeLoadingEvent event) {
        event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeature);
    }
    
    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!allowInsertingItemsToIce) return;
        Player player = event.getPlayer();
        if (player.isShiftKeyDown()) return;
        BlockPos pos = event.getPos();
        Level world = event.getWorld();
        BlockState state = world.getBlockState(pos);
        if ((state.getBlock() == Blocks.ICE || state.getBlock() == RareIce.RARE_ICE_BLOCK.get())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity == null) {
                world.setBlockAndUpdate(pos, RareIce.RARE_ICE_BLOCK.get().defaultBlockState());
                blockEntity = world.getBlockEntity(pos);
            }
            if (blockEntity instanceof RareIceBlockEntity) {
                RareIceBlockEntity rareIceBlockEntity = (RareIceBlockEntity) blockEntity;
                ItemStack itemStack = player.getItemInHand(event.getHand());
                itemStack = player.getAbilities().instabuild ? itemStack.copy() : itemStack;
                InteractionResult type = rareIceBlockEntity.addItem(world, itemStack, player, event.getSide().isServer());
                if (type != InteractionResult.PASS)
                    event.setCanceled(true);
                event.setCancellationResult(type);
            }
        }
    }
}
