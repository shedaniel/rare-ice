package me.shedaniel.rareice.forge;

import me.shedaniel.rareice.forge.blocks.RareIceBlock;
import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceConfig;
import me.shedaniel.rareice.forge.world.gen.feature.RareIceFeature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "rare-ice", bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod("rare-ice")
public class RareIce {
    
    public static final Block RARE_ICE_BLOCK = new RareIceBlock(Block.Properties.from(Blocks.ICE).harvestTool(ToolType.PICKAXE));
    public static final TileEntityType<RareIceBlockEntity> RARE_ICE_BLOCK_ENTITY_TYPE = TileEntityType.Builder.create(RareIceBlockEntity::new, RARE_ICE_BLOCK).build(null);
    public static final Feature<RareIceConfig> RARE_ICE_FEATURE = new RareIceFeature(RareIceConfig::getDefault);
    
    public RareIce() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().register(new RareIceClient()));
        MinecraftForge.EVENT_BUS.addListener(RareIce::rightClickBlock);
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                RARE_ICE_BLOCK.setRegistryName(new ResourceLocation("rare-ice", "rare_ice"))
        );
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new BlockItem(RARE_ICE_BLOCK, new Item.Properties()).setRegistryName(new ResourceLocation("rare-ice", "rare_ice"))
        );
    }
    
    @SubscribeEvent
    public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(
                RARE_ICE_BLOCK_ENTITY_TYPE.setRegistryName(new ResourceLocation("rare-ice", "rare_ice"))
        );
    }
    
    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        if (player.isSneaking()) return;
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        BlockState state = world.getBlockState(pos);
        if ((state.getBlock() == Blocks.ICE || state.getBlock() == RareIce.RARE_ICE_BLOCK)) {
            TileEntity blockEntity = world.getTileEntity(pos);
            if (blockEntity == null) {
                world.setBlockState(pos, RareIce.RARE_ICE_BLOCK.getDefaultState());
                blockEntity = world.getTileEntity(pos);
            }
            if (blockEntity instanceof RareIceBlockEntity) {
                RareIceBlockEntity rareIceBlockEntity = (RareIceBlockEntity) blockEntity;
                ItemStack itemStack = player.getHeldItem(event.getHand());
                itemStack = player.abilities.isCreativeMode ? itemStack.copy() : itemStack;
                ActionResultType type = rareIceBlockEntity.addItem(world, itemStack, player, event.getSide().isServer());
                if (type != ActionResultType.PASS)
                    event.setCanceled(true);
                event.setCancellationResult(type);
            }
        }
    }
    
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome.getDefaultTemperature() < 0.15F).forEach(RareIce::handleBiome);
    }
    
    private static void handleBiome(Biome biome) {
        biome.addFeature(
                GenerationStage.Decoration.TOP_LAYER_MODIFICATION,
                Biome.createDecoratedFeature(RARE_ICE_FEATURE, RareIceConfig.DEFAULT,
                        Placement.RANDOM_COUNT_RANGE, new CountRangeConfig(16, 32, 128, 256))
        );
    }
}
