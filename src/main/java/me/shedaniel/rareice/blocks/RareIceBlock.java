package me.shedaniel.rareice.blocks;

import me.shedaniel.rareice.blocks.entities.RareIceBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Random;

public class RareIceBlock extends BlockWithEntity {
    public RareIceBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new RareIceBlockEntity();
    }
    
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RareIceBlockEntity) {
                ItemScatterer.spawn(world, pos, ((RareIceBlockEntity) blockEntity).getItemsContained());
            }
            super.onBlockRemoved(state, world, pos, newState, moved);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @SuppressWarnings("deprecation")
    @Deprecated
    public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
        return neighbor.getBlock() == this || neighbor.getBlock() == Blocks.ICE || super.isSideInvisible(state, neighbor, facing);
    }
    
    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            if (world.dimension.doesWaterVaporize()) {
                world.removeBlock(pos, false);
            } else {
                Material material = world.getBlockState(pos.down()).getMaterial();
                if (material.blocksMovement() || material.isLiquid()) {
                    world.setBlockState(pos, Blocks.WATER.getDefaultState());
                }
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos)) {
            this.melt(state, world, pos);
        }
    }
    
    protected void melt(BlockState state, World world, BlockPos pos) {
        if (world.dimension.doesWaterVaporize()) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            world.updateNeighbor(pos, Blocks.WATER, pos);
        }
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.ICE);
    }
}
