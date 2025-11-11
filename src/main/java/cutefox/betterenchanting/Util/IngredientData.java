package cutefox.betterenchanting.Util;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class IngredientData {
    private Identifier enchantment_id;
    private String enchantment;
    private ArrayList<IngredientLevelData> ingredients;

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

    public Item getIngredientForLevel(int level){
        for(IngredientLevelData data : ingredients){
            if (data.getLevel()==level+1)
                return data.getItem();
        }

        return null;
    }

    public int getIngredientCostForLevel(int level){
        for(IngredientLevelData data : ingredients){
            if (data.getLevel()==level)
                return data.getCount();
        }

        return 0;
    }
}
