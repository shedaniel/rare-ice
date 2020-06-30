package me.shedaniel.rareice.forge.blocks.entities;

import me.shedaniel.rareice.forge.ItemLocation;
import me.shedaniel.rareice.forge.RareIce;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RareIceTileEntity extends TileEntity implements IClearable, ITickableTileEntity {
    private static final Random RANDOM = new Random();
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("rare-ice:chests/rare_ice");
    private final NonNullList<ItemStack> itemsContained;
    private final List<ItemLocation> itemsLocations;
    private boolean setup = false;
    private int delay = 0;
    
    public RareIceTileEntity() {
        super(RareIce.RARE_ICE_TILE_ENTITY_TYPE.get());
        this.itemsContained = NonNullList.create();
        this.itemsLocations = new ArrayList<>();
    }
    
    @Override
    public void clear() {
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
    public void fromTag(BlockState state, CompoundNBT tag) {
        loadInitialChunkData(state, tag);
        this.delay = tag.getInt("RevertDelay");
    }
    
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        saveInitialChunkData(tag);
        tag.putInt("RevertDelay", delay);
        return tag;
    }
    
    private CompoundNBT saveInitialChunkData(CompoundNBT tag) {
        super.write(tag);
        ListNBT itemsTag = new ListNBT();
        ListNBT itemLocationsTag = new ListNBT();
        for (int i = 0; i < itemsLocations.size(); ++i) {
            ItemLocation itemLocation = itemsLocations.get(i);
            ItemStack stack = itemsContained.get(i);
            if (!stack.isEmpty()) {
                CompoundNBT CompoundNBT = new CompoundNBT();
                stack.write(CompoundNBT);
                itemsTag.add(CompoundNBT);
            }
            if (!stack.isEmpty()) {
                CompoundNBT CompoundNBT = new CompoundNBT();
                itemLocation.toTag(CompoundNBT);
                itemLocationsTag.add(CompoundNBT);
            }
        }
        tag.put("Items", itemsTag);
        tag.put("ItemLocations", itemLocationsTag);
        return tag;
    }
    
    private void loadInitialChunkData(BlockState state, CompoundNBT tag) {
        super.fromTag(state, tag);
        this.itemsContained.clear();
        this.itemsLocations.clear();
        ListNBT itemsTag = tag.getList("Items", 10);
        for (int i = 0; i < itemsTag.size(); ++i) {
            CompoundNBT CompoundNBT = itemsTag.getCompound(i);
            itemsContained.add(ItemStack.read(CompoundNBT));
        }
        ListNBT itemLocationsTag = tag.getList("ItemLocations", 10);
        for (int i = 0; i < itemLocationsTag.size(); ++i) {
            CompoundNBT CompoundNBT = itemLocationsTag.getCompound(i);
            itemsLocations.add(ItemLocation.fromTag(CompoundNBT));
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
            LootTable lootTable = world.getServer().getLootTableManager().getLootTableFromLocation(LOOT_TABLE);
            LootContext.Builder builder = new LootContext.Builder((ServerWorld) world);
            List<ItemStack> drops = lootTable.generate(builder.build(LootParameterSets.EMPTY));
            int size = MathHelper.clamp(world.rand.nextInt(5) - (world.rand.nextInt(1) + 2), 0, drops.size());
            if (drops.size() >= 1) {
                for (int i = 0; i < size; i++) {
                    int index = world.rand.nextInt(drops.size());
                    addItem(world, drops.get(index), null);
                    drops.remove(index);
                }
            }
        } else if (itemsContained.isEmpty()) {
            delay++;
            if (delay > 20) {
                world.setBlockState(pos, Blocks.ICE.getDefaultState());
                remove();
            }
        } else {
            delay = 0;
        }
    }
    
    public ActionResultType addItem(World world, ItemStack itemStack, PlayerEntity nullablePlayer) {
        return addItem(world, itemStack, nullablePlayer, true);
    }
    
    public ActionResultType addItem(World world, ItemStack itemStack, PlayerEntity nullablePlayer, boolean actuallyDoIt) {
        if (itemStack.getItem() instanceof BlockItem) {
            Material material = ((BlockItem) itemStack.getItem()).getBlock().material;
            if (material == Material.ICE || material == Material.PACKED_ICE)
                return ActionResultType.PASS;
        }
        if (getItemsContained().size() < 8 && itemStack.getCount() >= 1) {
            if (actuallyDoIt) {
                getItemsContained().add(itemStack.split(1));
                Random random = world.rand;
                if (random == null) random = RANDOM;
                getItemsLocations().add(new ItemLocation(random.nextDouble() * .95 + .1, random.nextDouble() * .7 + .1, random.nextDouble() * .95 + .1));
                updateListeners();
            }
            if (nullablePlayer != null && world.isRemote())
                nullablePlayer.playSound(SoundEvents.BLOCK_CORAL_BLOCK_BREAK, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.CONSUME;
    }
    
    private void updateListeners() {
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }
    
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }
    
    @Override
    public CompoundNBT getUpdateTag() {
        return saveInitialChunkData(new CompoundNBT());
    }
    
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        loadInitialChunkData(state, tag);
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        handleUpdateTag(getBlockState(), packet.getNbtCompound());
    }
}
