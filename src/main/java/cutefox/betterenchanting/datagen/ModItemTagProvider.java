package cutefox.betterenchanting.datagen;

import com.terraformersmc.modmenu.util.mod.Mod;
import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.registry.ModItemTags;
import cutefox.betterenchanting.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        BetterEnchanting.LOGGER.info("Generating Item tags for : "+BetterEnchanting.MOD_ID);

        getOrCreateTagBuilder(ModItemTags.LIBRARIAN_RARE_INGREDIENT)
                .add(Items.EXPERIENCE_BOTTLE)
                .add(Items.TURTLE_SCUTE)
                .add(Items.WIND_CHARGE)
                .add(Items.HEART_OF_THE_SEA)
                .add(Items.NAUTILUS_SHELL)
                .add(Items.QUARTZ)
                .add(ModItems.MAGIC_SHARD_FULL);

        getOrCreateTagBuilder(ModItemTags.ENCHANTEMNT_INGREDIENT)
                .add(Items.EXPERIENCE_BOTTLE)
                .add(Items.PUFFERFISH)
                .add(Items.GOLDEN_CARROT)
                .add(Items.PISTON)
                .add(Items.POINTED_DRIPSTONE)
                .add(Items.PHANTOM_MEMBRANE)
                .add(Items.GLASS_BOTTLE)
                .add(ModItems.MAGIC_SHARD_FULL)
                .add(ModItems.MAGIC_SHARD_DULL)
                .add(ModItems.INFUSED_LAPIS);

        getOrCreateTagBuilder(ModItemTags.VANILLA_ESSENCE)
                .add(ModItems.ESSENCE_OF_EXPERIENCE)
                .add(ModItems.ESSENCE_OF_ARROWS)
                .add(ModItems.ESSENCE_OF_FEATHER)
                .add(ModItems.ESSENCE_OF_RESPIRATION)
                .add(ModItems.ESSENCE_OF_THORN)
                .add(ModItems.ESSENCE_OF_SEA)
                .add(ModItems.ESSENCE_OF_ICE)
                .add(ModItems.ESSENCE_OF_PROTECTION)
                .add(ModItems.ESSENCE_OF_BLAST_PROTECTION)
                .add(ModItems.ESSENCE_OF_FIRE_PROTECTION)
                .add(ModItems.ESSENCE_OF_PROJECTILE_PROTECTION)
                .add(ModItems.ESSENCE_OF_ARTHROPODS)
                .add(ModItems.ESSENCE_OF_SHARPNESS)
                .add(ModItems.ESSENCE_OF_SMITE)
                .add(ModItems.ESSENCE_OF_FIRE)
                .add(ModItems.ESSENCE_OF_KNOCKBACK)
                .add(ModItems.ESSENCE_OF_LOOTING)
                .add(ModItems.ESSENCE_OF_SWEEPING)
                .add(ModItems.ESSENCE_OF_PUNCH)
                .add(ModItems.ESSENCE_OF_POWER)
                .add(ModItems.ESSENCE_OF_SEA_LUCK)
                .add(ModItems.ESSENCE_OF_FORTUNE)
                .add(ModItems.ESSENCE_OF_EFFICIENCY)
                .add(ModItems.ESSENCE_OF_LURE)
                .add(ModItems.ESSENCE_OF_SILK_TOUCH)
                .add(ModItems.ESSENCE_OF_MENDING)
                .add(ModItems.ESSENCE_OF_UNBREAKING)
                .add(ModItems.ESSENCE_OF_CHANNELING)
                .add(ModItems.ESSENCE_OF_IMPALING)
                .add(ModItems.ESSENCE_OF_LOYALTY)
                .add(ModItems.ESSENCE_OF_RIPTIDE)
                .add(ModItems.ESSENCE_OF_MULTISHOT)
                .add(ModItems.ESSENCE_OF_PIERCING)
                .add(ModItems.ESSENCE_OF_QUICK_CHARGE)
                .add(ModItems.ESSENCE_OF_DENSITY)
                .add(ModItems.ESSENCE_OF_WIND)
                .add(ModItems.ESSENCE_OF_BREACH);

        getOrCreateTagBuilder(ModItemTags.ENCHANTMENT_ESSENCE)
                .addTag(ModItemTags.VANILLA_ESSENCE)
                .addTag(ModItemTags.BUMBLEZONE_ESSENCE)
                .addTag(ModItemTags.NEOENCHANT_ESSENCE)
                .addTag(ModItemTags.NOVA_ESSENCE)
                .addTag(ModItemTags.SPELL_POWER_ESSENCE);

        getOrCreateTagBuilder(ModItemTags.NEOENCHANT_ESSENCE)
                .add(ModItems.ESSENCE_OF_POISON)
                .add(ModItems.ESSENCE_OF_POISON_PROTECTION)
                .add(ModItems.ESSENCE_OF_MINING)
                .add(ModItems.ESSENCE_OF_SMELTING)
                .add(ModItems.ESSENCE_OF_SIGHT)
                .add(ModItems.ESSENCE_OF_LEVITATION)
                .add(ModItems.ESSENCE_OF_FORAGING)
                .add(ModItems.ESSENCE_OF_STRIKE)
                .add(ModItems.ESSENCE_OF_HEALTH)
                .add(ModItems.ESSENCE_OF_VAMPIRISM)
                .add(ModItems.ESSENCE_OF_AGILITY)
                .add(ModItems.ESSENCE_OF_COMBAT)
                .add(ModItems.ESSENCE_OF_BUILDING)
                .add(ModItems.ESSENCE_OF_FEAR)
                .add(ModItems.ESSENCE_OF_REACH)
                .add(ModItems.ESSENCE_OF_PULLING)
                .add(ModItems.ESSENCE_OF_FOOD);

        getOrCreateTagBuilder(ModItemTags.BUMBLEZONE_ESSENCE)
                .add(ModItems.ESSENCE_OF_POISON)
                .add(ModItems.ESSENCE_OF_NEUROTOXIN)
                .add(ModItems.ESSENCE_OF_COMB_CUTTER);

        getOrCreateTagBuilder(ModItemTags.SPELL_POWER_ESSENCE)
                .add(ModItems.ESSENCE_OF_MULTI_ROLL)
                .add(ModItems.ESSENCE_OF_LONGFOOT)
                .add(ModItems.ESSENCE_OF_CRITICAL_SPELL)
                .add(ModItems.ESSENCE_OF_ENERGY)
                .add(ModItems.ESSENCE_OF_HASTE)
                .add(ModItems.ESSENCE_OF_MAGIC_PROTECTION)
                .add(ModItems.ESSENCE_OF_SOULFROST)
                .add(ModItems.ESSENCE_OF_SPELL_POWER)
                .add(ModItems.ESSENCE_OF_SPELL_INFINITY)
                .add(ModItems.ESSENCE_OF_SUNFIRE);

        getOrCreateTagBuilder(ModItemTags.NOVA_ESSENCE)
                .add(ModItems.ESSENCE_OF_PHOTOSYNTHESIS)
                .add(ModItems.ESSENCE_OF_GRAVITY)
                .add(ModItems.ESSENCE_OF_WINGS);

        getOrCreateTagBuilder(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.WEAPON_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.MACE_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.TRIDENT_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.FISHING_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.CROSSBOW_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.BOW_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.MINING_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

        getOrCreateTagBuilder(ItemTags.MINING_LOOT_ENCHANTABLE)
                .add(ModItems.ENCHANTMENT_CATALYST);

    }
}
