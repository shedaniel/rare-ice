package me.shedaniel.rareice.forge.mixin;

public class MixinTransparentBlock {}

//@Mixin(TransparentBlock.class)
//public class MixinTransparentBlock {
//    @Inject(method = "isSideInvisible", cancellable = true, at = @At("RETURN"))
//    private void isSideInvisible(BlockState state, BlockState neighbor, Direction facing, CallbackInfoReturnable<Boolean> cir) {
//        if (!cir.getReturnValueZ() && (Object) this instanceof IceBlock && neighbor.getBlock() == RareIce.RARE_ICE_BLOCK)
//            cir.setReturnValue(true);
//    }
//}
