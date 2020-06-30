package me.shedaniel.rareice.forge.blocks.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.shedaniel.rareice.forge.ItemLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RareIceTileEntityRenderer extends TileEntityRenderer<RareIceTileEntity> {
    public RareIceTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    @Override
    public void render(RareIceTileEntity tileEntity, float tickDelta, MatrixStack matrices, IRenderTypeBuffer buffer, int light, int overlay) {
        if (tileEntity.isRemoved()) return;
        NonNullList<ItemStack> contained = tileEntity.getItemsContained();
        List<ItemLocation> locations = tileEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                matrices.push();
                matrices.translate(location.x, location.y, location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                matrices.rotate(Vector3f.YP.rotationDegrees((float) yawDegrees));
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                matrices.rotate(Vector3f.XP.rotationDegrees((float) pitchDegrees));
                matrices.scale(0.8f, 0.8f, 0.8f);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED, light, overlay, matrices, buffer);
                matrices.pop();
            }
        }
    }
}
