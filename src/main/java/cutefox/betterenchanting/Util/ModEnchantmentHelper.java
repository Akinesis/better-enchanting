package cutefox.betterenchanting.Util;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.config.GlobalConfig;
import cutefox.betterenchanting.datagen.ModEnchantIngredientMap;
import cutefox.betterenchanting.registry.ModEnchantmentTags;
import cutefox.betterenchanting.registry.ModItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.bunten.enderscape.registry.EnderscapeItems;
import net.hyper_pigeon.horseshoes.register.ItemRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class ModEnchantmentHelper {

    public static int getEnchantmentLevelCost(Enchantment enchantment, int enchantLevel, ItemStack stack, World world){
        float tempCost = (GlobalConfig.baseEnchantmentCost/enchantment.getWeight())+enchantLevel;
        tempCost *= (1+(GlobalConfig.consecutiveEnchantIncrease*stack.getEnchantments().getSize())); //price increase by 10% for each different enchantments.
        for(Object2IntMap.Entry<RegistryEntry<Enchantment>> e : stack.getEnchantments().getEnchantmentEntries()){
            tempCost += stack.getEnchantments().getLevel(e.getKey());
        }
        if(enchantIsTreasure(enchantment, world))
            tempCost *= GlobalConfig.tresaureMultiplier;

        int encahntability = stack.getItem().getEnchantability();

        tempCost = tempCost * (1-(encahntability/100));

        return Math.round(tempCost);
    }

    public static int getCatalystEnchantmentCost(ItemStack outputItem){
        float tempCost = 0;
        for(Object2IntMap.Entry<RegistryEntry<Enchantment>> e : EnchantmentHelper.getEnchantments(outputItem).getEnchantmentEntries()){

            tempCost += e.getKey().value().getAnvilCost();
            tempCost += Math.min(Math.round(e.getIntValue()/2),1);
        }

        return Math.round(tempCost);
    }

    private static boolean enchantIsTreasure(Enchantment enchantment,World world){
        Optional<RegistryEntryList.Named<Enchantment>> treasureList = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntryList(ModEnchantmentTags.BENEFICIAL_TREASURE);
        if(treasureList != null && !treasureList.isEmpty()){
            return  (treasureList.get().stream().filter(e -> {
                return e.value() == enchantment;
            }).count() > 0);
        }
        return false;
    }

    public static int getEnchantmentLeveRequierment(Enchantment enchantment, int enchantLevel){
        int tempLevelReq = 10-enchantment.getWeight()+(enchantLevel*enchantLevel);
        return tempLevelReq>30?30:tempLevelReq; //Cap level requirement at 30 to keep it vanilla like
    }

    public static int getBookshelfCountRequierment(Enchantment enchantment, int enchantLevel){
        int tempReq = (int)Math.floor(getEnchantmentLeveRequierment(enchantment, enchantLevel)/2);
        return tempReq<=3?0:tempReq;
    }

    public static Item getEnchantIngredient(Enchantment enchantment, int enchantLevel){
        return ModEnchantIngredientMap.getIngredientOfLevel(enchantment,enchantLevel);
    }

    public static List<Item> getIngredientsOfEnchantment(Enchantment enchantment){
        return ModEnchantIngredientMap.getIngredientsOfEnchantment(enchantment);
    }

    public static int getEnchantmentIngredientCost(Enchantment value, int displayedEnchantLevel, Item ingredient) {
        if(GlobalConfig.overideItemCost){
            if(GlobalConfig.overidenItemCost > ingredient.getMaxCount())
                return ingredient.getMaxCount();
            if(value.getMaxLevel() == displayedEnchantLevel)
                return 1;

            return GlobalConfig.overidenItemCost;
        }

        int cost = ModEnchantIngredientMap.getCostOfLevel(value, displayedEnchantLevel);
        if (cost > 0)
            return cost;

        int tempValue = (int)Math.floor(value.getWeight()/2);
        if (value.getMaxLevel() == displayedEnchantLevel)
            tempValue = 1;
        tempValue = tempValue==0?1:tempValue;
        if(ingredient == null)
            return tempValue;
        else
            return tempValue>ingredient.getMaxCount()?ingredient.getMaxCount():tempValue;
    }

    public static List<EnchantmentLevelEntry> getPossibleEntries(int bookshelfCount, ItemStack itemToEnchant, DynamicRegistryManager registryManager){

        if(itemToEnchant.isOf(Items.BOOK))
            return List.of();

        Optional<RegistryEntryList.Named<Enchantment>> enchantingTableList = registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.IN_ENCHANTING_TABLE);
        Optional<RegistryEntryList.Named<Enchantment>> treasureList = registryManager.get(RegistryKeys.ENCHANTMENT).getEntryList(EnchantmentTags.TREASURE);

        Stream <RegistryEntry<Enchantment>> concatEnchantList;
        List<EnchantmentLevelEntry> list = Lists.newArrayList();

        if(enchantingTableList.isEmpty())
            return list;

        if(!treasureList.isEmpty())
            concatEnchantList = Stream.concat(enchantingTableList.get().stream(), treasureList.get().stream());
        else
            concatEnchantList = enchantingTableList.get().stream();

        List<RegistryKey<Enchantment>> swordEnchants = new ArrayList<>();


        swordEnchants.add(Enchantments.FIRE_ASPECT);
        swordEnchants.add(Enchantments.LOOTING);
        swordEnchants.add(Enchantments.KNOCKBACK);
        //TODO: ADD MODDED SWORD ENCHANTS
        //swordEnchants.add(RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("mod_identifier:moded_enchant")));

        concatEnchantList.distinct()
                .filter(enchant -> {
                        boolean validEnchant;
                        if(enchant.isIn(EnchantmentTags.CURSE))
                            return false;
                        validEnchant = enchant.value().isAcceptableItem(itemToEnchant);
                        if(validEnchant == false && itemToEnchant.isIn(ItemTags.AXES) && swordEnchants.contains(enchant.getKey().get()))
                            validEnchant = true;
                        if(GlobalConfig.disabledEnchants.contains(enchant.getKey().get().getValue()))
                            return false;
                        return validEnchant;
                })
                .forEach(enchant -> {

                    Enchantment enchantmentValue = enchant.value();

                    if(isCompatible(itemToEnchant.getEnchantments().getEnchantments(), enchant)){
                        for(int j = enchantmentValue.getMaxLevel(); j >= enchantmentValue.getMinLevel(); --j) {
                            if (getBookshelfCountRequierment(enchantmentValue, j) <= bookshelfCount) {
                                list.add(new EnchantmentLevelEntry(enchant, j));
                                break;
                            }
                        }
                    }
                });

        return list;

    }

    public static boolean itemHasPreviousLevelOfEnchant(ItemStack stack, RegistryEntry<Enchantment> enchant, int targetLevel){

        int currentEnchantLevel = EnchantmentHelper.getLevel(enchant,stack);
        return currentEnchantLevel == targetLevel;

    }

    public static boolean canBeCombined(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second) {
        return !first.value().exclusiveSet().contains(second) && !second.value().exclusiveSet().contains(first);
    }

    public static boolean isCompatible(Collection<RegistryEntry<Enchantment>> existing, RegistryEntry<Enchantment> candidate) {
        for (RegistryEntry<Enchantment> registryEntry : existing) {
            if (!canBeCombined(registryEntry, candidate)){
                if(!registryEntry.equals(candidate))
                    return false;
            }
        }
        return true;
    }

    public static boolean itemIsEnchantable(ItemStack itemStacks){

        //ADD COMPATIBILITY ITEMS HERE

        List<Item> enchantableModdedItems = new ArrayList<>();

        if(BetterEnchanting.HORSESHOES_PRESENT)
            enchantableModdedItems.addAll(List.of(
                    ItemRegistry.DIAMOND_HORSESHOES_ITEM.get().asItem(),
                    ItemRegistry.IRON_HORSESHOES_ITEM.get().asItem(),
                    ItemRegistry.GOLD_HORSESHOES_ITEM.get().asItem()));

        if(BetterEnchanting.ENDERSCAPE_PRESENT)
            enchantableModdedItems.addAll(List.of(
                    EnderscapeItems.MIRROR,
                    EnderscapeItems.MAGNIA_ATTRACTOR));

        return enchantableModdedItems.contains(itemStacks.getItem()) || itemStacks.getItem().isEnchantable(itemStacks);
    }

    public static List<ItemStack> replaceEnchantedBook(@Nullable @Local LocalRef<ItemStack> localRef, ItemStack item){

        List<ItemStack> returnList = new ArrayList<>();
        int i = 0;
        ItemStack newItem;
        ItemStack localRefItemStack = item.copy();

        if(localRefItemStack.getItem().equals(Items.ENCHANTED_BOOK)){
            ItemEnchantmentsComponent bookEnchants = EnchantmentHelper.getEnchantments(localRefItemStack);


            for(Object2IntMap.Entry<RegistryEntry<Enchantment>> ench : bookEnchants.getEnchantmentEntries()){
                bookEnchants.getEnchantmentEntries();
                int enchantLevel = ench.getIntValue();
                Enchantment enchantment = ench.getKey().value();
                List<Item> ingredientsOfEnchant = ModEnchantIngredientMap.getIngredientsOfEnchantment(enchantment);
                if(ingredientsOfEnchant !=null && !ingredientsOfEnchant.isEmpty()){
                    if(enchantLevel > ingredientsOfEnchant.size()) //If not all ingredients are configured for the enchant
                        enchantLevel = ingredientsOfEnchant.size();

                    newItem = new ItemStack(ingredientsOfEnchant.get(enchantLevel-1));
                    if(enchantLevel < ingredientsOfEnchant.size()){
                        //If the ingredient is not an essence
                        net.minecraft.util.math.random.Random rand = Random.create();
                        newItem.setCount(enchantLevel+rand.nextBetween(1,3));
                    }
                }else {
                    newItem = new ItemStack(Items.EXPERIENCE_BOTTLE);
                }
                if(i > 0 || localRef == null)
                    returnList.add(newItem);
                else
                    if (localRef != null)
                        localRef.set(newItem);
                i++;
            }
        }

        return returnList;
    }

    public static ItemStack replaceEnchantedBook(ItemStack enchantedBook){

        ItemEnchantmentsComponent bookEnchants = EnchantmentHelper.getEnchantments(enchantedBook);
        List<ItemStack> returnList = new ArrayList<>();
        ItemStack newItem;

        int i =0;

        for(Object2IntMap.Entry<RegistryEntry<Enchantment>> ench : bookEnchants.getEnchantmentEntries()){
            bookEnchants.getEnchantmentEntries();
            int enchantLevel = ench.getIntValue();
            Enchantment enchantment = ench.getKey().value();
            List<Item> ingredientsOfEnchant = ModEnchantIngredientMap.getIngredientsOfEnchantment(enchantment);
            if(ingredientsOfEnchant !=null && !ingredientsOfEnchant.isEmpty()){
                if(enchantLevel > ingredientsOfEnchant.size()) //If not all ingredients are configured for the enchant
                    enchantLevel = ingredientsOfEnchant.size();

                newItem = new ItemStack(ingredientsOfEnchant.get(enchantLevel-1));
                if(enchantLevel < ingredientsOfEnchant.size()){
                    //If the ingredient is not an essence
                    net.minecraft.util.math.random.Random rand = Random.create();
                    newItem.setCount(enchantLevel+rand.nextBetween(1,3));
                }
            }else {
                newItem = new ItemStack(Items.EXPERIENCE_BOTTLE);
            }
            returnList.add(newItem);
            i++;
        }

        Random rand = Random.create();
        return returnList.get(rand.nextBetween(0, returnList.size()-1));
    }

    public static ItemStack combineCatalyst(ItemStack firstCatalyst, ItemStack secondCatalyst){
        ItemStack output = new ItemStack(ModItems.ENCHANTMENT_CATALYST);
        output.set(DataComponentTypes.MAX_STACK_SIZE, 1);

        EnchantmentHelper.getEnchantments(firstCatalyst).getEnchantmentEntries().stream().forEach(e -> output.addEnchantment(e.getKey(), e.getIntValue()));

        for(Object2IntMap.Entry<RegistryEntry<Enchantment>> e : EnchantmentHelper.getEnchantments(secondCatalyst).getEnchantmentEntries()){
            if(output.getEnchantments().getEnchantments().contains(e.getKey())) {
                int newLevel = e.getIntValue();
                int oldValue = output.getEnchantments().getLevel(e.getKey());
                if(newLevel > oldValue){
                    output.addEnchantment(e.getKey(), e.getIntValue());
                }else if(oldValue == newLevel){
                    int maxLevel = e.getKey().value().getMaxLevel();
                    output.addEnchantment(e.getKey(), Math.min(oldValue+1, maxLevel));
                }

            }else{

                if(EnchantmentHelper.isCompatible(output.getEnchantments().getEnchantments(), e.getKey()))
                    output.addEnchantment(e.getKey(), e.getIntValue());
            }

        }

        return output;
    }
}
