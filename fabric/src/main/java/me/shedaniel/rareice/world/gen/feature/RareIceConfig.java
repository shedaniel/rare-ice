package me.shedaniel.rareice.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Predicate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RareIceConfig implements FeatureConfiguration {
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
