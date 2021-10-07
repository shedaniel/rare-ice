package me.shedaniel.rareice.forge.blocks.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import me.shedaniel.rareice.forge.ItemLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RareIceTileEntityRenderer implements BlockEntityRenderer<RareIceTileEntity> {
    public RareIceTileEntityRenderer(BlockEntityRendererProvider.Context context) {
    }
    
    @Override
    public void render(RareIceTileEntity tileEntity, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay) {
        if (tileEntity.isRemoved()) return;
        NonNullList<ItemStack> contained = tileEntity.getItemsContained();
        List<ItemLocation> locations = tileEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                matrices.pushPose();
                matrices.translate(location.x, location.y, location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                matrices.mulPose(Vector3f.YP.rotationDegrees((float) yawDegrees));
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                matrices.mulPose(Vector3f.XP.rotationDegrees((float) pitchDegrees));
                matrices.scale(0.8f, 0.8f, 0.8f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, matrices, buffer, 0);
                matrices.popPose();
            }
        }
    }
}
