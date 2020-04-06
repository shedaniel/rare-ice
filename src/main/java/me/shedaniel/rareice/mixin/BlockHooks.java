package me.shedaniel.rareice.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface BlockHooks {
    @Accessor("material")
    Material getMaterial();
}
