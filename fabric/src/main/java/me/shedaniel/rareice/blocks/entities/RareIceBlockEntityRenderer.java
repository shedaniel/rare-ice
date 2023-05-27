package me.shedaniel.rareice.blocks.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.shedaniel.rareice.ItemLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RareIceBlockEntityRenderer implements BlockEntityRenderer<RareIceBlockEntity> {
    public RareIceBlockEntityRenderer(BlockEntityRendererProvider.Context dispatcher) {
    }
    
    @Override
    public void render(RareIceBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (blockEntity.isRemoved()) return;
        NonNullList<ItemStack> contained = blockEntity.getItemsContained();
        List<ItemLocation> locations = blockEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                matrices.pushPose();
                matrices.translate(location.x, location.y, location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                matrices.mulPose(Axis.YP.rotationDegrees((float) yawDegrees));
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                matrices.mulPose(Axis.XP.rotationDegrees((float) pitchDegrees));
                matrices.scale(0.8f, 0.8f, 0.8f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, Minecraft.getInstance().level, 0);
                matrices.popPose();
            }
        }
    }
}
