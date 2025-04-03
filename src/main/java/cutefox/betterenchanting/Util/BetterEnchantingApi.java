package cutefox.betterenchanting.Util;

import cutefox.betterenchanting.datagen.ModEnchantIngredientMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;

public class BetterEnchantingApi {

    /**
     * Add the ingredients for the enchantment of the identifier in parameters
     * If possible, prefer the {@link #addEnchantmentIngredient(Enchantment, List)} of this call.
     * @param enchantmentId The Identifier of the enchantment
     * @param ingredients A list of the ingredients for the enchantment, ordered by level. (so first entry is for Enchantment I, then Enchantment II etc.)
     * @return True if enchantment is found and successfully added to the map. False otherwise.
     */
    public static boolean addEnchantmentIngredient(Identifier enchantmentId, List<Item> ingredients){
        Registry<Enchantment> enchantRegistry =  Utils.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        Enchantment enchantment = enchantRegistry.get(enchantmentId);

        return addEnchantmentIngredient(enchantment, ingredients);

    }

    /**
     * Add ingredients for enchantment of the enchantments of "enchantmentId".
     * If possible, prefer the {@link #addEnchantmentIngredient(Enchantment, List)} of this call.
     * @param enchantmentId The enchantment identifier as a String (includind namespace. Ex : "minecraft:smite")
     * @param ingredients
     * @return
     */
    public static boolean addEnchantmentIngredient(String enchantmentId, List<Item> ingredients){

        Identifier enchantId = Identifier.of(enchantmentId);
        return addEnchantmentIngredient(enchantId, ingredients);
    }

    /**
     * Add the ingredients for the enchantment of the identifier in parameters.
     * This is the preferred method as it's the more robust and error-free one.
     * @param enchantment The Enchantment to be added
     * @param ingredients A list of the ingredients for the enchantment, ordered by level. (so first entry is for Enchantment I, then Enchantment II etc.)
     * @return True if enchantment is found and successfully added to the map. False otherwise.
     */
    public static boolean addEnchantmentIngredient(Enchantment enchantment, List<Item> ingredients){

        if(enchantment == null)
            return false;

        return ModEnchantIngredientMap.addEnchantmentIngredient(enchantment,ingredients);
    }

}
