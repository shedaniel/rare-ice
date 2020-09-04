package me.shedaniel.rareice.forge.mixin;

import me.shedaniel.rareice.forge.RareIce;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBreakable.class)
public class MixinBlockBreakable {
    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Blocks.ICE) {
            IBlockState neighbor = blockAccess.getBlockState(pos.offset(side));
            if (neighbor.getBlock() == RareIce.rareIceBlock) {
                cir.setReturnValue(false);
            }
        } else if ((Object) this == RareIce.rareIceBlock) {
            IBlockState neighbor = blockAccess.getBlockState(pos.offset(side));
            if (neighbor.getBlock() == Blocks.ICE) {
                cir.setReturnValue(false);
            }
        }
    }
}
