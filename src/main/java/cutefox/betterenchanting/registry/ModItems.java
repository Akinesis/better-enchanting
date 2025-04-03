package cutefox.betterenchanting.registry;

import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.Util.Utils;
import cutefox.betterenchanting.item.ItemWithRemainderChance;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    public static void registerModItems(){
        BetterEnchanting.LOGGER.info("Registering mod iems for : "+ BetterEnchanting.MOD_ID);
    }


    //region ENCHANTMENT INGREDIENTS
    public static final List<Item> MOD_ITEM_LIST = new ArrayList<>();
    public static final List<Item> MOD_ITEM_LIST_BUMBLEZONE_COMPAT = new ArrayList<>();
    public static final Item INFUSED_LAPIS = registerItem("infused_lapis", new Item.Settings());
    public static final Item MAGIC_SHARD_DULL = registerItem("magic_shard_dull", new Item.Settings());
    public static final Item MAGIC_SHARD_FULL = registerItemWithRemainderChance("magic_shard_full", new Item.Settings().maxCount(16).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).recipeRemainder(ModItems.MAGIC_SHARD_DULL));
    //endregion

    //region UPGRADE TEMPLATES
    public static final Item IRON_UPGRADE_SMITHING_TEMPLATE = registerItem("iron_upgrade_smithing_template", new Item.Settings());
    public static final Item DIAMOND_UPGRADE_SMITHING_TEMPLATE = registerItem("diamond_upgrade_smithing_template", new Item.Settings());
    public static final Item ENCHANTMENT_CATALYST = registerItem("enchantment_catalyst", new Item.Settings().maxCount(16));
    //endregion

    //Armors
    public static final Item ESSENCE_OF_PROTECTION = registerItem("essence_of_protection", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_FIRE_PROTECTION = registerItem("essence_of_fire_protection", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_BLAST_PROTECTION = registerItem("essence_of_blast_protection", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_PROJECTILE_PROTECTION = registerItem("essence_of_projectile_protection", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item SOUL_ESSENCE_1 = registerItem("soul_essence_1", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item SOUL_ESSENCE_2 = registerItem("soul_essence_2", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).recipeRemainder(ModItems.SOUL_ESSENCE_1));
    public static final Item SOUL_ESSENCE_3 = registerItem("soul_essence_3", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).recipeRemainder(ModItems.SOUL_ESSENCE_2));
    public static final Item ESSENCE_OF_SNEAK_1 = registerItem("essence_of_sneak_1", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SNEAK_2 = registerItem("essence_of_sneak_2", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).recipeRemainder(ModItems.ESSENCE_OF_SNEAK_1));
    public static final Item ESSENCE_OF_SNEAK_3 = registerItem("essence_of_sneak_3", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).recipeRemainder(ModItems.ESSENCE_OF_SNEAK_2));
    public static final Item ESSENCE_OF_FEATHER = registerItem("essence_of_feather", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_RESPIRATION = registerItem("essence_of_respiration", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SEA = registerItem("essence_of_sea", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_THORN = registerItem("essence_of_thorn", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_ICE = registerItem("essence_of_ice", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Swords
    public static final Item ESSENCE_OF_SWEEPING = registerItem("essence_of_sweeping", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SHARPNESS = registerItem("essence_of_sharpness", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SMITE = registerItem("essence_of_smite", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_ARTHROPODS = registerItem("essence_of_arthropods", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_KNOCKBACK = registerItem("essence_of_knockback", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_FIRE = registerItem("essence_of_fire", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_LOOTING = registerItem("essence_of_looting", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Bows
    public static final Item ESSENCE_OF_ARROWS = registerItem("essence_of_arrows", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_POWER = registerItem("essence_of_power", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_PUNCH = registerItem("essence_of_punch", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Tools
    public static final Item ESSENCE_OF_EFFICIENCY = registerItem("essence_of_efficiency", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SILK_TOUCH = registerItem("essence_of_silk_touch", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_FORTUNE = registerItem("essence_of_fortune", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Fishing tool
    public static final Item ESSENCE_OF_SEA_LUCK = registerItem("essence_of_sea_luck", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_LURE = registerItem("essence_of_lure", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Anything
    public static final Item ESSENCE_OF_EXPERIENCE = registerItem("essence_of_experience", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_MENDING = registerItem("essence_of_mending", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_UNBREAKING = registerItem("essence_of_unbreaking", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Trident
    public static final Item ESSENCE_OF_CHANNELING = registerItem("essence_of_channeling", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_IMPALING = registerItem("essence_of_impaling", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_LOYALTY = registerItem("essence_of_loyalty", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_RIPTIDE = registerItem("essence_of_riptide", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Crossbow
    public static final Item ESSENCE_OF_MULTISHOT = registerItem("essence_of_multishot", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_PIERCING = registerItem("essence_of_piercing", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_QUICK_CHARGE = registerItem("essence_of_quick_charge", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Mace
    public static final Item ESSENCE_OF_DENSITY = registerItem("essence_of_density", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_BREACH = registerItem("essence_of_breach", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_WIND = registerItem("essence_of_wind", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));


    //Neo Enchant Plus
    public static final Item ESSENCE_OF_POISON = registerItem("essence_of_poison", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_POISON_PROTECTION = registerItem("essence_of_poison_protection", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_MINING = registerItem("essence_of_mining", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SMELTING = registerItem("essence_of_smelting", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SIGHT = registerItem("essence_of_sight", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_LEVITATION = registerItem("essence_of_levitation", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_FORAGING = registerItem("essence_of_foraging", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_STRIKE = registerItem("essence_of_strike", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_HEALTH = registerItem("essence_of_health", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_VAMPIRISM = registerItem("essence_of_vampirism", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_AGILITY = registerItem("essence_of_agility", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_COMBAT = registerItem("essence_of_combat", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_BUILDING = registerItem("essence_of_building", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_FEAR = registerItem("essence_of_fear", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_REACH = registerItem("essence_of_reach", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_PULLING = registerItem("essence_of_pulling", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_FOOD = registerItem("essence_of_food", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //The Bumblezone
    public static final Item ESSENCE_OF_NEUROTOXIN = registerItemBulblezoneCompat("essence_of_neurotoxin", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_COMB_CUTTER = registerItemBulblezoneCompat("essence_of_comb_cutter", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Dungeons and Taverns
    public static final Item ESSENCE_OF_PHOTOSYNTHESIS = registerItem("essence_of_photosynthesis", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_GRAVITY = registerItem("essence_of_gravity", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_WINGS = registerItem("essence_of_wings", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Diversity
    public static final Item ESSENCE_OF_CAPACITY = registerItem("essence_of_capacity", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_REFILL = registerItem("essence_of_refill", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Toss Up
    public static final Item ESSENCE_OF_EXPLOSION = registerItem("essence_of_explosion", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    //Spell engine
    public static final Item ESSENCE_OF_MULTI_ROLL = registerItem("essence_of_multi_roll", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_LONGFOOT = registerItem("essence_of_longfoot", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static final Item ESSENCE_OF_CRITICAL_SPELL = registerItem("essence_of_critical_spell", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_ENERGY = registerItem("essence_of_energy", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_HASTE = registerItem("essence_of_haste", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_MAGIC_PROTECTION = registerItem("essence_of_magic_protection", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SOULFROST = registerItem("essence_of_soulfrost", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SPELL_POWER = registerItem("essence_of_spell_power", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SUNFIRE = registerItem("essence_of_sunfire", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item ESSENCE_OF_SPELL_INFINITY = registerItem("essence_of_spell_infinity", new Item.Settings().maxCount(1).fireproof().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));


    //endregion

    private static Item registerItem(String id, Item.Settings settings){
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Utils.id(id));
        Item i = Registry.register(Registries.ITEM, key, new Item(settings.registryKey(key)));
        MOD_ITEM_LIST.add(i);
        return i;
    }

    private static Item registerItemWithRemainderChance(String id, Item.Settings settings){
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Utils.id(id));
        Item i = Registry.register(Registries.ITEM, key, new ItemWithRemainderChance(settings.registryKey(key)));
        MOD_ITEM_LIST.add(i);
        return i;
    }

    private static Item registerItemBulblezoneCompat(String id, Item.Settings settings){
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Utils.id(id));
        Item i = Registry.register(Registries.ITEM, Utils.id(id), new Item(settings.registryKey(key)));
        MOD_ITEM_LIST_BUMBLEZONE_COMPAT.add(i);
        return i;
    }



}