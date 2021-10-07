package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.entities.RareIceTileEntityRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "rare_ice", bus = Mod.EventBusSubscriber.Bus.MOD)
public class RareIceClient {
    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(RareIce.RARE_ICE_BLOCK.get(), RenderType.translucent());
    }
    
    public static boolean isSideInvisibleForIce(Block block, BlockState neighbor) {
        return block instanceof IceBlock && neighbor.getBlock() == RareIce.RARE_ICE_BLOCK.get();
    }
    
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RareIce.RARE_ICE_TILE_ENTITY_TYPE.get(), RareIceTileEntityRenderer::new);
    }
}
