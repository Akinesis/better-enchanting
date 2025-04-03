package cutefox.betterenchanting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.Util.ModEnchantmentHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    private List<ItemStack> essences = new ArrayList<>();

    @Shadow protected abstract List<Integer> getFreeSlots(Inventory inventory, Random random);

    @Inject(method = "supplyInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    public void betterEnchanting$replaceEnchantedBook(Inventory inventory, LootWorldContext parameters, long seed, CallbackInfo ci, @Local LocalRef<ItemStack> localRef){

        if(localRef.get().isEmpty())
            return;

        ItemStack localRefItemStack = localRef.get().copy();

        essences.addAll(ModEnchantmentHelper.replaceEnchantedBook(localRef,localRefItemStack));
    }

    @Inject(method = "supplyInventory", at = @At(value = "TAIL"),locals = LocalCapture.CAPTURE_FAILHARD)
    public void betterEnchanting$addEssencesAfterLootGeneration(Inventory inventory, LootWorldContext parameters, long seed, CallbackInfo ci){

        Random random = Random.create(53844);

        this.betterEnchanting$addEssences(inventory, random);
        this.essences.clear();
    }

    private void betterEnchanting$addEssences(Inventory inventory, Random random){

        List<Integer> list = this.getFreeSlots(inventory, random);


        for (ItemStack stack : essences){
            if (list.isEmpty()) {
                BetterEnchanting.LOGGER.warn("Tried to over-fill a container");
                return;
            }
            inventory.setStack(list.remove(list.size()-1), stack);
        }
    }
}
