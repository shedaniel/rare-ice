package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class RareIceClient {
    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(RareIceBlockEntity.class, new RareIceBlockEntityRenderer());
    }
    
    public static boolean isSideInvisibleForIce(Block block, BlockState neighbor) {
        return block instanceof IceBlock && neighbor.getBlock() == RareIce.RARE_ICE_BLOCK;
    }
}
