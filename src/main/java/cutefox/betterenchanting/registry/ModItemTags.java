package cutefox.betterenchanting.registry;

import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.Util.Utils;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {

    public static final TagKey<Item> ENCHANTEMNT_INGREDIENT = TagKey.of(RegistryKeys.ITEM, Utils.id("enchantment_ingredients"));
    public static final TagKey<Item> ENCHANTMENT_ESSENCE = TagKey.of(RegistryKeys.ITEM, Utils.id("enchantment_essence"));
    public static final TagKey<Item> VANILLA_ESSENCE = TagKey.of(RegistryKeys.ITEM, Utils.id("vanilla_essence"));
    public static final TagKey<Item> LIBRARIAN_RARE_INGREDIENT = TagKey.of(RegistryKeys.ITEM, Utils.id("librarian_rare_ingredient"));
    public static final TagKey<Item> NEOENCHANT_ESSENCE = TagKey.of(RegistryKeys.ITEM, Utils.id("neoenchant_essence"));
    public static final TagKey<Item> BUMBLEZONE_ESSENCE = TagKey.of(RegistryKeys.ITEM, Utils.id("bumblezone_essence"));
    public static final TagKey<Item> SPELL_POWER_ESSENCE = TagKey.of(RegistryKeys.ITEM, Utils.id("spell_engine_essence"));
    public static final TagKey<Item> NOVA_ESSENCE = TagKey.of(RegistryKeys.ITEM, Utils.id("nova_essence"));

    public static final TagKey<Item> IRON_UPGRADE_TEMPLATE = TagKey.of(RegistryKeys.ITEM, Identifier.of("fox_den","iron_upgrade_template"));
    public static final TagKey<Item> DIAMOND_UPGRADE_TEMPLATE = TagKey.of(RegistryKeys.ITEM, Identifier.of("fox_den","diamond_upgrade_template"));

    public static void registerModTags(){
        BetterEnchanting.LOGGER.info("Generating item tags for mod : "+BetterEnchanting.MOD_ID);
    }
}
