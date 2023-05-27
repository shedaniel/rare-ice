package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.RareIceBlock;
import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceConfig;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceCountPlacement;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, "rare-ice");
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "rare-ice");
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "rare-ice");
    public static final DeferredRegister<Feature<?>> FEATURE_REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, "rare-ice");
    
    public static final RegistryObject<Block> RARE_ICE_BLOCK = BLOCK_REGISTRY.register("rare_ice", () ->
            new RareIceBlock(BlockBehaviour.Properties.copy(Blocks.ICE).isValidSpawn((state, world, pos, type) -> type == EntityType.POLAR_BEAR)));
    public static final RegistryObject<BlockEntityType<RareIceBlockEntity>> RARE_ICE_TILE_ENTITY_TYPE = TILE_ENTITY_REGISTRY.register("rare_ice", () ->
            BlockEntityType.Builder.of(RareIceBlockEntity::new, RARE_ICE_BLOCK.get()).build(null));
    public static final RegistryObject<Feature<RareIceConfig>> RARE_ICE_FEATURE = FEATURE_REGISTRY.register("rare_ice", () -> new RareIceFeature(RareIceConfig.CODEC));
    public static final RegistryObject<PlacementModifierType<RareIceCountPlacement>> COUNT_PLACEMENT = PLACEMENT_MODIFIERS.register("rare_ice_count", () -> () -> RareIceCountPlacement.CODEC);
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
        PLACEMENT_MODIFIERS.register(bus);
        BLOCK_REGISTRY.register(bus);
        TILE_ENTITY_REGISTRY.register(bus);
        FEATURE_REGISTRY.register(bus);
        
        bus.addListener(RareIce::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(RareIce::rightClickBlock);
    }
    
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        loadConfig(FMLPaths.CONFIGDIR.get().resolve("rare-ice.properties"));
    }
    
    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!allowInsertingItemsToIce) return;
        Player player = event.getEntity();
        if (player.isShiftKeyDown()) return;
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
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
