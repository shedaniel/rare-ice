package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.entities.RareIceTileEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "rare-ice", bus = Mod.EventBusSubscriber.Bus.MOD)
public class RareIceClient {
    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(RareIce.RARE_ICE_BLOCK.get(), RenderType.getTranslucent());
        ClientRegistry.bindTileEntityRenderer(RareIce.RARE_ICE_TILE_ENTITY_TYPE.get(), RareIceTileEntityRenderer::new);
    }
    
    public static boolean isSideInvisibleForIce(Block block, BlockState neighbor) {
        return block instanceof IceBlock && neighbor.getBlock() == RareIce.RARE_ICE_BLOCK.get();
    }
}
