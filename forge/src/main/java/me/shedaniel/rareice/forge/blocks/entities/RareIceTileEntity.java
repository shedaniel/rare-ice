package me.shedaniel.rareice.forge.blocks.entities;

import me.shedaniel.rareice.forge.ItemLocation;
import me.shedaniel.rareice.forge.RareIce;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RareIceTileEntity extends BlockEntity implements Clearable {
    private static final Random RANDOM = new Random();
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("rare-ice:chests/rare_ice");
    private final NonNullList<ItemStack> itemsContained;
    private final List<ItemLocation> itemsLocations;
    private boolean setup = false;
    private int delay = 0;
    
    public RareIceTileEntity(BlockPos pos, BlockState state) {
        super(RareIce.RARE_ICE_TILE_ENTITY_TYPE.get(), pos, state);
        this.itemsContained = NonNullList.create();
        this.itemsLocations = new ArrayList<>();
    }
    
    @Override
    public void clearContent() {
        this.itemsContained.clear();
        this.itemsLocations.clear();
    }
    
    public NonNullList<ItemStack> getItemsContained() {
        return itemsContained;
    }
    
    public List<ItemLocation> getItemsLocations() {
        return itemsLocations;
    }
    
    @Override
    public void load(CompoundTag tag) {
        loadInitialChunkData(tag);
        this.delay = tag.getInt("RevertDelay");
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        saveInitialChunkData(tag);
        tag.putInt("RevertDelay", delay);
        return tag;
    }
    
    private CompoundTag saveInitialChunkData(CompoundTag tag) {
        super.save(tag);
        ListTag itemsTag = new ListTag();
        ListTag itemLocationsTag = new ListTag();
        for (int i = 0; i < itemsLocations.size(); ++i) {
            ItemLocation itemLocation = itemsLocations.get(i);
            ItemStack stack = itemsContained.get(i);
            if (!stack.isEmpty()) {
                CompoundTag CompoundNBT = new CompoundTag();
                stack.save(CompoundNBT);
                itemsTag.add(CompoundNBT);
            }
            if (!stack.isEmpty()) {
                CompoundTag CompoundNBT = new CompoundTag();
                itemLocation.toTag(CompoundNBT);
                itemLocationsTag.add(CompoundNBT);
            }
        }
        tag.put("Items", itemsTag);
        tag.put("ItemLocations", itemLocationsTag);
        return tag;
    }
    
    private void loadInitialChunkData(CompoundTag tag) {
        super.load(tag);
        this.itemsContained.clear();
        this.itemsLocations.clear();
        ListTag itemsTag = tag.getList("Items", 10);
        for (int i = 0; i < itemsTag.size(); ++i) {
            CompoundTag CompoundNBT = itemsTag.getCompound(i);
            itemsContained.add(ItemStack.of(CompoundNBT));
        }
        ListTag itemLocationsTag = tag.getList("ItemLocations", 10);
        for (int i = 0; i < itemLocationsTag.size(); ++i) {
            CompoundTag CompoundNBT = itemLocationsTag.getCompound(i);
            itemsLocations.add(ItemLocation.fromTag(CompoundNBT));
        }
    }
    
    public void addLootTable(Level world) {
        setup = true;
    }
    
    public static void tick(Level world, BlockPos pos, BlockState blockState, RareIceTileEntity blockEntity) {
        if (blockEntity.setup) {
            blockEntity.setup = false;
            blockEntity.delay = 0;
            LootTable lootTable = world.getServer().getLootTables().get(LOOT_TABLE);
            LootContext.Builder builder = new LootContext.Builder((ServerLevel) world);
            List<ItemStack> drops = lootTable.getRandomItems(builder.create(LootContextParamSets.EMPTY));
            int size = Mth.clamp(world.random.nextInt(5) - (world.random.nextInt(1) + 2), 0, drops.size());
            if (drops.size() >= 1) {
                for (int i = 0; i < size; i++) {
                    int index = world.random.nextInt(drops.size());
                    blockEntity.addItem(world, drops.get(index), null);
                    drops.remove(index);
                }
            }
        } else if (blockEntity.itemsContained.isEmpty()) {
            blockEntity.delay++;
            if (blockEntity.delay > 20) {
                world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                blockEntity.setRemoved();
            }
        } else {
            blockEntity.delay = 0;
        }
    }
    
    public InteractionResult addItem(Level world, ItemStack itemStack, Player nullablePlayer) {
        return addItem(world, itemStack, nullablePlayer, true);
    }
    
    public InteractionResult addItem(Level world, ItemStack itemStack, Player nullablePlayer, boolean actuallyDoIt) {
        if (itemStack.getItem() instanceof BlockItem) {
            Material material = ((BlockItem) itemStack.getItem()).getBlock().material;
            if (material == Material.ICE || material == Material.ICE_SOLID)
                return InteractionResult.PASS;
        }
        if (getItemsContained().size() < 8 && itemStack.getCount() >= 1) {
            if (actuallyDoIt) {
                getItemsContained().add(itemStack.split(1));
                Random random = world.random;
                if (random == null) random = RANDOM;
                getItemsLocations().add(new ItemLocation(random.nextDouble() * .95 + .1, random.nextDouble() * .7 + .1, random.nextDouble() * .95 + .1));
                updateListeners();
            }
            if (nullablePlayer != null && world.isClientSide())
                nullablePlayer.playSound(SoundEvents.CORAL_BLOCK_BREAK, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
    
    private void updateListeners() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
    
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, getUpdateTag());
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        return saveInitialChunkData(new CompoundTag());
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadInitialChunkData(tag);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        handleUpdateTag(packet.getTag());
    }
}
