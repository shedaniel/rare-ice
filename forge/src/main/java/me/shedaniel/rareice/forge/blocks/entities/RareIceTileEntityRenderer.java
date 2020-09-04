package me.shedaniel.rareice.forge.blocks.entities;

import me.shedaniel.rareice.forge.ItemLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class RareIceTileEntityRenderer extends TileEntitySpecialRenderer<RareIceTileEntity> {
    @Override
    public void render(RareIceTileEntity tileEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int destroyStage, float alpha) {
        if (tileEntity.isInvalid()) return;
        NonNullList<ItemStack> contained = tileEntity.getItemsContained();
        List<ItemLocation> locations = tileEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(xOffset + location.x, yOffset + location.y, zOffset + location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                GlStateManager.rotate((float) yawDegrees, 0f, 1f, 0f);
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                GlStateManager.rotate((float) pitchDegrees, 1f, 0f, 0f);
                GlStateManager.scale(0.8f, 0.8f, 0.8f);
                Minecraft.getMinecraft().getItemRenderer().renderItem(Minecraft.getMinecraft().player, stack, ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
