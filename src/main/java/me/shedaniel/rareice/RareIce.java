package me.shedaniel.rareice;

import me.shedaniel.rareice.blocks.RareIceBlock;
import me.shedaniel.rareice.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.world.gen.feature.RareIceConfig;
import me.shedaniel.rareice.world.gen.feature.RareIceFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

public class RareIce implements ModInitializer {
    
    public static final Block RARE_ICE_BLOCK = new RareIceBlock(FabricBlockSettings.copy(Blocks.ICE).breakByTool(FabricToolTags.PICKAXES).build());
    public static final BlockEntityType<RareIceBlockEntity> RARE_ICE_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(RareIceBlockEntity::new, RARE_ICE_BLOCK).build(null);
    public static final Feature<RareIceConfig> RARE_ICE_FEATURE = new RareIceFeature(RareIceConfig::getDefault);
    
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK_ENTITY, new Identifier("rare-ice", "rare_ice"), RARE_ICE_BLOCK_ENTITY_TYPE);
        Registry.register(Registry.BLOCK, new Identifier("rare-ice", "rare_ice"), RARE_ICE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("rare-ice", "rare_ice"), new BlockItem(RARE_ICE_BLOCK, new Item.Settings()));
        Registry.BIOME.forEach(this::handleBiome);
        RegistryEntryAddedCallback.event(Registry.BIOME).register((i, identifier, biome) -> handleBiome(biome));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
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
                    Biome.configureFeature(RARE_ICE_FEATURE, RareIceConfig.DEFAULT,
                            Decorator.RANDOM_COUNT_RANGE, new RangeDecoratorConfig(16, 32, 128, 256))
            );
        }
    }
}
