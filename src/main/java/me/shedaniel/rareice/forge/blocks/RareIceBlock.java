package me.shedaniel.rareice.forge.blocks;

import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class RareIceBlock extends Block {
    public RareIceBlock(Block.Properties settings) {
        super(settings);
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RareIceBlockEntity();
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity instanceof RareIceBlockEntity) {
                InventoryHelper.dropItems(world, pos, ((RareIceBlockEntity) blockEntity).getItemsContained());
            }
            super.onReplaced(state, world, pos, newState, moved);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    @Deprecated
    public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
        return neighbor.getBlock() == this || neighbor.getBlock() == Blocks.ICE || super.isSideInvisible(state, neighbor, facing);
    }
    
    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity blockEntity, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, blockEntity, stack);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
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
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos)) {
            this.melt(state, world, pos);
        }
    }
    
    protected void melt(BlockState state, World world, BlockPos pos) {
        if (world.dimension.doesWaterVaporize()) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            world.neighborChanged(pos, Blocks.WATER, pos);
        }
    }
    
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public boolean canEntitySpawn(BlockState state, IBlockReader view, BlockPos pos, EntityType<?> type) {
        return type == EntityType.POLAR_BEAR;
    }
    
    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(Blocks.ICE);
    }
}
