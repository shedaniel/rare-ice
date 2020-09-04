package me.shedaniel.rareice.forge.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {
    @Accessor
    Material getBlockMaterial();
    
    @Invoker
    Block invokeSetSoundType(SoundType sound);
}
