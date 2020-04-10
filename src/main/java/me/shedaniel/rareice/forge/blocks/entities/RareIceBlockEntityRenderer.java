package me.shedaniel.rareice.forge.blocks.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.rareice.forge.ItemLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RareIceBlockEntityRenderer extends TileEntityRenderer<RareIceBlockEntity> {
    @Override
    public void render(RareIceBlockEntity blockEntity, double x, double y, double z, float tickDelta, int destroyStage) {
        if (blockEntity.isRemoved()) return;
        NonNullList<ItemStack> contained = blockEntity.getItemsContained();
        List<ItemLocation> locations = blockEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translated(x + location.x, y + location.y, z + location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                GlStateManager.rotatef((float) yawDegrees, 0f, 1f, 0f);
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                GlStateManager.rotatef((float) pitchDegrees, 1f, 0f, 0f);
                GlStateManager.scalef(0.8f, 0.8f, 0.8f);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
