package me.shedaniel.rareice.forge.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.function.Predicate;

public class RareIceConfig implements IFeatureConfig {
    public static final RareIceConfig DEFAULT = new RareIceConfig();
    public final int size = 20;
    public final Predicate<BlockState> predicate = new BlockMatcher(Blocks.ICE).or(new BlockMatcher(Blocks.PACKED_ICE)).or(new BlockMatcher(Blocks.BLUE_ICE));
    
    public static RareIceConfig getDefault(Dynamic<?> dynamic) {
        return DEFAULT;
    }
    
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}
