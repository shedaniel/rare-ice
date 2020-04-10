package me.shedaniel.rareice.blocks.entities;

import me.shedaniel.rareice.ItemLocation;
import me.shedaniel.rareice.RareIce;
import me.shedaniel.rareice.mixin.BlockHooks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RareIceBlockEntity extends BlockEntity implements Clearable, BlockEntityClientSerializable, Tickable {
    private static final Random RANDOM = new Random();
    private static final Identifier LOOT_TABLE = new Identifier("rare-ice:chests/rare_ice");
    private final DefaultedList<ItemStack> itemsContained;
    private final List<ItemLocation> itemsLocations;
    private boolean setup = false;
    private int delay = 0;
    
    public RareIceBlockEntity() {
        super(RareIce.RARE_ICE_BLOCK_ENTITY_TYPE);
        this.itemsContained = DefaultedList.of();
        this.itemsLocations = new ArrayList<>();
    }
    
    @Override
    public void clear() {
        this.itemsContained.clear();
        this.itemsLocations.clear();
    }
    
    public DefaultedList<ItemStack> getItemsContained() {
        return itemsContained;
    }
    
    public List<ItemLocation> getItemsLocations() {
        return itemsLocations;
    }
    
    @Override
    public void fromTag(CompoundTag tag) {
        loadInitialChunkData(tag);
        this.delay = tag.getInt("RevertDelay");
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        saveInitialChunkData(tag);
        tag.putInt("RevertDelay", delay);
        return tag;
    }
    
    private CompoundTag saveInitialChunkData(CompoundTag tag) {
        super.toTag(tag);
        ListTag itemsTag = new ListTag();
        ListTag itemLocationsTag = new ListTag();
        for (int i = 0; i < itemsLocations.size(); ++i) {
            ItemLocation itemLocation = itemsLocations.get(i);
            ItemStack stack = itemsContained.get(i);
            if (!stack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                stack.toTag(compoundTag);
                itemsTag.add(compoundTag);
            }
            if (!stack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                itemLocation.toTag(compoundTag);
                itemLocationsTag.add(compoundTag);
            }
        }
        tag.put("Items", itemsTag);
        tag.put("ItemLocations", itemLocationsTag);
        return tag;
    }
    
    private void loadInitialChunkData(CompoundTag tag) {
        super.fromTag(tag);
        this.itemsContained.clear();
        this.itemsLocations.clear();
        ListTag itemsTag = tag.getList("Items", 10);
        for (int i = 0; i < itemsTag.size(); ++i) {
            CompoundTag compoundTag = itemsTag.getCompound(i);
            itemsContained.add(ItemStack.fromTag(compoundTag));
        }
        ListTag itemLocationsTag = tag.getList("ItemLocations", 10);
        for (int i = 0; i < itemLocationsTag.size(); ++i) {
            CompoundTag compoundTag = itemLocationsTag.getCompound(i);
            itemsLocations.add(ItemLocation.fromTag(compoundTag));
        }
    }
    
    public void addLootTable(World world) {
        setup = true;
    }
    
    @Override
    public void tick() {
        if (setup) {
            setup = false;
            delay = 0;
            LootTable lootTable = world.getServer().getLootManager().getSupplier(LOOT_TABLE);
            LootContext.Builder builder = new LootContext.Builder((ServerWorld) world);
            List<ItemStack> drops = lootTable.getDrops(builder.build(LootContextTypes.EMPTY));
            int size = MathHelper.clamp(world.random.nextInt(5) - (world.random.nextInt(1) + 2), 0, drops.size());
            if (drops.size() >= 1) {
                for (int i = 0; i < size; i++) {
                    int index = world.random.nextInt(drops.size());
                    addItem(world, drops.get(index), null);
                    drops.remove(index);
                }
            }
        } else if (itemsContained.isEmpty()) {
            delay++;
            if (delay > 20) {
                world.setBlockState(pos, Blocks.ICE.getDefaultState());
                markRemoved();
            }
        } else {
            delay = 0;
        }
    }
    
    public ActionResult addItem(World world, ItemStack itemStack, PlayerEntity nullablePlayer) {
        return addItem(world, itemStack, nullablePlayer, true);
    }
    
    public ActionResult addItem(World world, ItemStack itemStack, PlayerEntity nullablePlayer, boolean actuallyDoIt) {
        if (itemStack.getItem() instanceof BlockItem) {
            Material material = ((BlockHooks) ((BlockItem) itemStack.getItem()).getBlock()).getMaterial();
            if (material == Material.ICE || material == Material.PACKED_ICE)
                return ActionResult.PASS;
        }
        if (getItemsContained().size() < 8 && itemStack.getCount() >= 1) {
            if (actuallyDoIt) {
                getItemsContained().add(itemStack.split(1));
                Random random = world.random;
                if (random == null) random = RANDOM;
                getItemsLocations().add(new ItemLocation(random.nextDouble() * .95 + .1, random.nextDouble() * .7 + .1, random.nextDouble() * .95 + .1));
                updateListeners();
            }
            if (nullablePlayer != null)
                nullablePlayer.playSound(SoundEvents.BLOCK_CORAL_BLOCK_BREAK, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    
    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }
    
    @Override
    public void fromClientTag(CompoundTag tag) {
        loadInitialChunkData(tag);
    }
    
    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return saveInitialChunkData(tag);
    }
}
