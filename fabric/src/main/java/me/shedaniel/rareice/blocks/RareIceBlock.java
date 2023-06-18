package me.shedaniel.rareice.blocks;

import me.shedaniel.rareice.RareIce;
import me.shedaniel.rareice.blocks.entities.RareIceBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RareIceBlock extends BaseEntityBlock {
    public RareIceBlock(Properties settings) {
        super(settings);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RareIceBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (world.isClientSide) {
            return null;
        } else {
            return createTickerHelper(type, RareIce.RARE_ICE_BLOCK_ENTITY_TYPE, RareIceBlockEntity::tick);
        }
    }
    
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RareIceBlockEntity) {
                Containers.dropContents(world, pos, ((RareIceBlockEntity) blockEntity).getItemsContained());
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @SuppressWarnings("deprecation")
    @Deprecated
    public boolean skipRendering(BlockState state, BlockState neighbor, Direction facing) {
        return neighbor.getBlock() == this || neighbor.getBlock() == Blocks.ICE || super.skipRendering(state, neighbor, facing);
    }
    
    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, state, blockEntity, stack);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            if (world.dimensionType().ultraWarm()) {
                world.removeBlock(pos, false);
            } else {
                BlockState blockState = world.getBlockState(pos.below());
                if (blockState.blocksMotion() || blockState.liquid()) {
                    world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightBlock(world, pos)) {
            this.melt(state, world, pos);
        }
    }
    
    protected void melt(BlockState state, Level world, BlockPos pos) {
        if (world.dimensionType().ultraWarm()) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            world.neighborChanged(pos, Blocks.WATER, pos);
        }
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.ICE);
    }
}
