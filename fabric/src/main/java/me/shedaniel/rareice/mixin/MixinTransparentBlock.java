package me.shedaniel.rareice.mixin;

import me.shedaniel.rareice.RareIce;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HalfTransparentBlock.class)
public class MixinTransparentBlock {
    @Inject(method = "skipRendering", cancellable = true, at = @At("RETURN"))
    private void skipRendering(BlockState state, BlockState neighbor, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && (Object) this instanceof IceBlock && neighbor.getBlock() == RareIce.RARE_ICE_BLOCK)
            cir.setReturnValue(true);
    }
}
