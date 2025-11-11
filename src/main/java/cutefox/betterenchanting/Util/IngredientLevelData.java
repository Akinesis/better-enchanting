package cutefox.betterenchanting.Util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IngredientLevelData {
    private int level;
    private String ingredient;
    private Item item;
    private int count;

    public int getLevel() {
        return level;
    }

    public String getIngredient() {
        return ingredient;
    }

    @Nullable
    public Item getItem() {
        if(ingredient == null)
            return null;
        if(item == null)
            convertItemStringToItem();
        return item;
    }

    public int getCount() {
        return count;
    }

    public void convertItemStringToItem(){
        item = Registries.ITEM.get(Identifier.of(ingredient));
    }
}
