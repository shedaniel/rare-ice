package me.shedaniel.rareice.forge.world.gen.feature;

import com.mojang.serialization.Codec;
import me.shedaniel.rareice.forge.RareIce;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class RareIceCountPlacement extends RepeatingPlacement {
    public static final Codec<RareIceCountPlacement> CODEC = Codec.unit(RareIceCountPlacement::new);

    protected int count(RandomSource randomSource, BlockPos blockPos) {
        return RareIce.probabilityOfRareIce;
    }

    @Override
    public PlacementModifierType<?> type() {
        return RareIce.COUNT_PLACEMENT.get();
    }
}
