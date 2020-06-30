package me.shedaniel.rareice.mixin;

import me.shedaniel.rareice.RareIce;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TransparentBlock.class)
public class MixinTransparentBlock {
    @Inject(method = "isSideInvisible", cancellable = true, at = @At("RETURN"))
    private void isSideInvisible(BlockState state, BlockState neighbor, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && (Object) this instanceof IceBlock && neighbor.getBlock() == RareIce.RARE_ICE_BLOCK)
            cir.setReturnValue(true);
    }
}
