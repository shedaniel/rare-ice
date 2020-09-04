package me.shedaniel.rareice.forge.blocks.entities;

import com.google.common.collect.Lists;
import me.shedaniel.rareice.forge.ItemLocation;
import me.shedaniel.rareice.forge.loot.LootGenerator;
import me.shedaniel.rareice.forge.mixin.BlockAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RareIceTileEntity extends TileEntity implements ITickable {
    private static final Random RANDOM = new Random();
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("rare-ice:chests/rare_ice");
    private final NonNullList<ItemStack> itemsContained;
    private final List<ItemLocation> itemsLocations;
    private boolean setup = false;
    private int delay = 0;
    
    public RareIceTileEntity() {
        this.itemsContained = NonNullList.create();
        this.itemsLocations = new ArrayList<>();
    }
    
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
    public void readFromNBT(NBTTagCompound tag) {
        loadInitialChunkData(tag);
        this.delay = tag.getInteger("RevertDelay");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        saveInitialChunkData(tag);
        tag.setInteger("RevertDelay", delay);
        return tag;
    }
    
    private NBTTagCompound saveInitialChunkData(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList itemsTag = new NBTTagList();
        NBTTagList itemLocationsTag = new NBTTagList();
        for (int i = 0; i < itemsLocations.size(); ++i) {
            ItemLocation itemLocation = itemsLocations.get(i);
            ItemStack stack = itemsContained.get(i);
            if (!stack.isEmpty()) {
                NBTTagCompound CompoundNBT = new NBTTagCompound();
                stack.writeToNBT(CompoundNBT);
                itemsTag.appendTag(CompoundNBT);
            }
            if (!stack.isEmpty()) {
                NBTTagCompound CompoundNBT = new NBTTagCompound();
                itemLocation.toTag(CompoundNBT);
                itemLocationsTag.appendTag(CompoundNBT);
            }
        }
        tag.setTag("Items", itemsTag);
        tag.setTag("ItemLocations", itemLocationsTag);
        return tag;
    }
    
    private void loadInitialChunkData(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.itemsContained.clear();
        this.itemsLocations.clear();
        NBTTagList itemsTag = tag.getTagList("Items", 10);
        for (int i = 0; i < itemsTag.tagCount(); ++i) {
            NBTTagCompound CompoundNBT = itemsTag.getCompoundTagAt(i);
            itemsContained.add(new ItemStack(CompoundNBT));
        }
        NBTTagList itemLocationsTag = tag.getTagList("ItemLocations", 10);
        for (int i = 0; i < itemLocationsTag.tagCount(); ++i) {
            NBTTagCompound CompoundNBT = itemLocationsTag.getCompoundTagAt(i);
            itemsLocations.add(ItemLocation.fromTag(CompoundNBT));
        }
    }
    
    public void addLootTable(World world) {
        setup = true;
    }
    
    @Override
    public void update() {
        if (setup) {
            setup = false;
            delay = 0;
            List<ItemStack> drops = Lists.newArrayList(LootGenerator.generate(world.rand));
            int size = MathHelper.clamp(world.rand.nextInt(5) - (world.rand.nextInt(1) + 1), 0, drops.size());
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
                invalidate();
            }
        } else {
            delay = 0;
        }
    }
    
    public EnumActionResult addItem(World world, ItemStack itemStack, EntityPlayer nullablePlayer) {
        return addItem(world, itemStack, nullablePlayer, true);
    }
    
    public EnumActionResult addItem(World world, ItemStack itemStack, EntityPlayer nullablePlayer, boolean actuallyDoIt) {
        if (itemStack.getItem() instanceof ItemBlock) {
            Material material = ((BlockAccessor) ((ItemBlock) itemStack.getItem()).getBlock()).getBlockMaterial();
            if (material == Material.ICE || material == Material.PACKED_ICE)
                return EnumActionResult.PASS;
        }
        if (getItemsContained().size() < 8 && itemStack.getCount() >= 1) {
            if (actuallyDoIt) {
                getItemsContained().add(itemStack.splitStack(1));
                Random random = world.rand;
                if (random == null) random = RANDOM;
                getItemsLocations().add(new ItemLocation(random.nextDouble() * .95 + .1, random.nextDouble() * .7 + .1, random.nextDouble() * .95 + .1));
                updateListeners();
            }
            if (nullablePlayer != null && world.isRemote)
                nullablePlayer.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.0F);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.SUCCESS;
    }
    
    private void updateListeners() {
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        return saveInitialChunkData(new NBTTagCompound());
    }
    
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        loadInitialChunkData(tag);
    }
    
    @Override
    public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        handleUpdateTag(packet.getNbtCompound());
    }
}
