package me.shedaniel.rareice;

import me.shedaniel.rareice.blocks.RareIceBlock;
import me.shedaniel.rareice.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.world.gen.feature.RareIceConfig;
import me.shedaniel.rareice.world.gen.feature.RareIceFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class RareIce implements ModInitializer {
    
    public static final Block RARE_ICE_BLOCK = new RareIceBlock(FabricBlockSettings.copyOf(Blocks.ICE).allowsSpawning((state, world, pos, type) -> type == EntityType.POLAR_BEAR).breakByTool(FabricToolTags.PICKAXES));
    public static final BlockEntityType<RareIceBlockEntity> RARE_ICE_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(RareIceBlockEntity::new, RARE_ICE_BLOCK).build(null);
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
    
    @Override
    public void onInitialize() {
        loadConfig(FabricLoader.getInstance().getConfigDirectory().toPath().resolve("rare-ice.properties"));
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("rare-ice", "rare_ice"), RARE_ICE_BLOCK_ENTITY_TYPE);
        Registry.register(Registry.BLOCK, new Identifier("rare-ice", "rare_ice"), RARE_ICE_BLOCK);
        Registry.BIOME.forEach(this::handleBiome);
        RegistryEntryAddedCallback.event(Registry.BIOME).register((i, identifier, biome) -> handleBiome(biome));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!allowInsertingItemsToIce) return ActionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (player == null || player.isSneaking())
                return ActionResult.PASS;
            if ((state.getBlock() == Blocks.ICE || state.getBlock() == RareIce.RARE_ICE_BLOCK)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity == null) {
                    world.setBlockState(pos, RareIce.RARE_ICE_BLOCK.getDefaultState());
                    blockEntity = world.getBlockEntity(pos);
                }
                if (blockEntity instanceof RareIceBlockEntity) {
                    RareIceBlockEntity rareIceBlockEntity = (RareIceBlockEntity) blockEntity;
                    ItemStack itemStack = player.getStackInHand(hand);
                    itemStack = player.abilities.creativeMode ? itemStack.copy() : itemStack;
                    return rareIceBlockEntity.addItem(world, itemStack, player, !world.isClient());
                }
            }
            return ActionResult.PASS;
        });
    }
    
    private void handleBiome(Biome biome) {
        if (biome.getTemperature() < 0.15f) {
            biome.addFeature(
                    GenerationStep.Feature.TOP_LAYER_MODIFICATION,
                    RARE_ICE_FEATURE.configure(RareIceConfig.DEFAULT).createDecoratedFeature(
                            Decorator.RANDOM_COUNT_RANGE.configure(new RangeDecoratorConfig(16, 32, 128, 256))
                    ));
        }
    }
}
