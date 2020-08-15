package me.shedaniel.rareice.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.function.Predicate;

public class RareIceConfig implements FeatureConfig {
    public static final RareIceConfig DEFAULT = new RareIceConfig(20);
    public static final Codec<RareIceConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.INT.fieldOf("size").orElse(20).forGetter(RareIceConfig::getSize)
        ).apply(instance, RareIceConfig::new);
    });
    public final int size;
    public final Predicate<BlockState> predicate = new BlockPredicate(Blocks.ICE).or(new BlockPredicate(Blocks.PACKED_ICE)).or(new BlockPredicate(Blocks.BLUE_ICE));
    
    public RareIceConfig(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
}
