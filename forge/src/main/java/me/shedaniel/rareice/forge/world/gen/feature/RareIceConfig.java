package me.shedaniel.rareice.forge.world.gen.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.function.Predicate;

public class RareIceConfig {
    public static final RareIceConfig DEFAULT = new RareIceConfig(20);
    public final int size;
    public final Predicate<IBlockState> predicate = state -> state.getBlock() == Blocks.ICE;
    
    public RareIceConfig(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
}
