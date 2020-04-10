package me.shedaniel.rareice.blocks.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.rareice.ItemLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RareIceBlockEntityRenderer extends BlockEntityRenderer<RareIceBlockEntity> {
    @Override
    public void render(RareIceBlockEntity blockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage) {
        if (blockEntity.isRemoved()) return;
        DefaultedList<ItemStack> contained = blockEntity.getItemsContained();
        List<ItemLocation> locations = blockEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translated(xOffset + location.x, yOffset + location.y, zOffset + location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                GlStateManager.rotatef((float) yawDegrees, 0f, 1f, 0f);
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                GlStateManager.rotatef((float) pitchDegrees, 1f, 0f, 0f);
                GlStateManager.scalef(0.8f, 0.8f, 0.8f);
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Type.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
