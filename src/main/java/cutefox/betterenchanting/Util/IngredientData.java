package cutefox.betterenchanting.Util;

import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class IngredientData {
    private Identifier enchantment_id;
    private String enchantment;
    private ArrayList<IngredientLevelData> ingredients;

    public IngredientData(){
        if(ingredients != null)
            ingredients.get(0);
    }

    public IngredientData(String enchantment) {
        this.enchantment_id = Identifier.of(enchantment);
    }

    public Identifier getEnchantment_id() {
        if(enchantment_id == null)
            convertEnchantment();
        return enchantment_id;
    }

    public void setEnchantment(String enchantment) {
        this.enchantment = enchantment;
    }

    private void convertEnchantment(){
        if(enchantment_id == null){
            this.enchantment_id = Identifier.of(enchantment);
        }
    }

    public ArrayList<IngredientLevelData> getIngredients() {
        return ingredients;
    }
}
