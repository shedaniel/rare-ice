package me.shedaniel.rareice.forge.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LootGenerator {
    private static final List<Pair<Pair<Integer, Integer>, List<Entry>>> LOOT = new ArrayList<>();
    
    static {
        register(1, 2, builder -> {
            builder.add(Items.MAP);
            builder.add(2, Items.EMERALD, Items.DIAMOND, Items.PRISMARINE_CRYSTALS, Items.IRON_INGOT, Items.GOLD_INGOT);
            builder.add(3, Items.COAL);
        });
        register(1, 1, builder -> {
            builder.addBroken(Items.IRON_HELMET, 70, 100);
            builder.addBroken(Items.CHAINMAIL_LEGGINGS, 70, 100);
            builder.addBroken(Items.LEATHER_CHESTPLATE, 70, 100);
            builder.addBroken(Items.GOLDEN_BOOTS, 70, 100);
            builder.addBroken(Items.LEATHER_BOOTS, 70, 100);
            builder.addBroken(Items.GOLDEN_LEGGINGS, 70, 100);
            builder.addBroken(Items.GOLDEN_CHESTPLATE, 70, 100);
            builder.addBroken(Items.GOLDEN_PICKAXE, 70, 100);
            builder.addBroken(Items.GOLDEN_SWORD, 70, 100);
            builder.addBroken(Items.DIAMOND_PICKAXE, 30, 60);
            builder.addBroken(Items.IRON_PICKAXE, 30, 70);
            builder.addBroken(Items.IRON_SWORD, 30, 70);
            builder.addBroken(Items.STONE_PICKAXE, 20, 100);
            builder.addBroken(Items.STONE_SWORD, 20, 100);
            builder.addBroken(Items.WOODEN_SWORD, 20, 100);
        });
        register(0, 1, builder -> {
            builder.add(Items.FISH);
        });
    }
    
    public static void register(int min, int max, Consumer<Builder> builderConsumer) {
        List<Entry> items = new ArrayList<>();
        Builder builder = new Builder(items);
        builderConsumer.accept(builder);
        LOOT.add(Pair.of(Pair.of(min, max == min ? null : max), items));
    }
    
    public static List<ItemStack> generate(Random random) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (Pair<Pair<Integer, Integer>, List<Entry>> pair : LOOT) {
            int count = pair.first().first() + (pair.first().second() != null ? random.nextInt(pair.first().second() - pair.first().first()) : 0);
            for (int i = 0; i < count; i++) {
                Entry entry = WeightedRandom.getRandomItem(random, pair.second());
                builder.add(entry.getStack(random));
            }
        }
        return builder.build();
    }
    
    public static class Builder {
        private List<Entry> items;
        
        public Builder(List<Entry> items) {
            this.items = items;
        }
        
        public void add(int weight, Item item) {
            this.items.add(new Entry(weight, new ItemStack(item)));
        }
        
        public void add(int weight, Item... items) {
            for (Item item : items) {
                add(weight, item);
            }
        }
        
        public void add(int weight, Block block) {
            this.items.add(new Entry(weight, new ItemStack(block)));
        }
        
        public void add(int weight, Block... blocks) {
            for (Block block : blocks) {
                add(weight, block);
            }
        }
        
        public void add(int weight, ItemStack stack) {
            this.items.add(new Entry(weight, stack));
        }
        
        public void add(int weight, ItemStack... stacks) {
            for (ItemStack stack : stacks) {
                add(weight, stack);
            }
        }
        
        public void add(int weight, Supplier<ItemStack> stack) {
            this.items.add(new Entry(weight, stack));
        }
        
        @SafeVarargs
        public final void add(int weight, Supplier<ItemStack>... stacks) {
            for (Supplier<ItemStack> stack : stacks) {
                add(weight, stack);
            }
        }
        
        public void add(int weight, Function<Random, ItemStack> stack) {
            this.items.add(new Entry(weight, stack));
        }
        
        @SafeVarargs
        public final void add(int weight, Function<Random, ItemStack>... stacks) {
            for (Function<Random, ItemStack> stack : stacks) {
                add(weight, stack);
            }
        }
        
        public void add(Item... items) {
            add(1, items);
        }
        
        public void add(Block... blocks) {
            add(1, blocks);
        }
        
        public void add(ItemStack... stacks) {
            add(1, stacks);
        }
        
        @SafeVarargs
        public final void add(Supplier<ItemStack>... stacks) {
            add(1, stacks);
        }
        
        @SafeVarargs
        public final void add(Function<Random, ItemStack>... stacks) {
            add(1, stacks);
        }
        
        public final void addBroken(int weight, Item item, int min, int max) {
            add(weight, random -> {
                ItemStack stack = new ItemStack(item);
                stack.setItemDamage((int) (stack.getMaxDamage() * (1f - (random.nextInt(max - min) + min) / 100f)));
                return stack;
            });
        }
        
        public final void addBroken(Item item, int min, int max) {
            addBroken(1, item, min, max);
        }
    }
    
    public static class Entry extends WeightedRandom.Item {
        private final Function<Random, ItemStack> stack;
        
        public Entry(int itemWeightIn, ItemStack stack) {
            this(itemWeightIn, stack::copy);
        }
        
        public Entry(int itemWeightIn, Supplier<ItemStack> stack) {
            this(itemWeightIn, random -> stack.get());
        }
        
        public Entry(int itemWeightIn, Function<Random, ItemStack> stack) {
            super(itemWeightIn);
            this.stack = stack;
        }
        
        public ItemStack getStack(Random random) {
            return stack.apply(random);
        }
    }
}
