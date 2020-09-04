package me.shedaniel.rareice.forge;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RareIceClient {
    public static boolean isSideInvisibleForIce(Block block, IBlockState neighbor) {
        return block instanceof BlockIce && neighbor.getBlock() == RareIce.rareIceBlock;
    }
}
