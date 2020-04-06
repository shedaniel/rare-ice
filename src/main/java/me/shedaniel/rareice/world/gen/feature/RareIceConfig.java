package me.shedaniel.rareice.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.function.Predicate;

public class RareIceConfig implements FeatureConfig {
    public static final RareIceConfig DEFAULT = new RareIceConfig();
    public final int size = 20;
    public final Predicate<BlockState> predicate = new BlockPredicate(Blocks.ICE).or(new BlockPredicate(Blocks.PACKED_ICE)).or(new BlockPredicate(Blocks.BLUE_ICE));
    
    public static RareIceConfig getDefault(Dynamic<?> dynamic) {
        return DEFAULT;
    }
    
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.emptyMap());
    }
}
