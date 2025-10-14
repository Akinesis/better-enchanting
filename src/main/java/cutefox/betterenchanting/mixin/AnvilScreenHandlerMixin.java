package cutefox.betterenchanting.mixin;

import cutefox.betterenchanting.Util.ModEnchantmentHelper;
import cutefox.betterenchanting.config.GlobalConfig;
import cutefox.betterenchanting.registry.ModItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandlerMixin{

    @Shadow
    private Property levelCost;

    private int betterEnchanting$materialRepairLevelCost;
    private boolean betterEnchanting$shouldAddRepairCost;
    private int betterEnchanting$customCost;

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void betterEnchanting$preventCombineEnchantedItem(CallbackInfo ci){
        ItemStack firstStack = input.getStack(0);
        ItemStack secondStack = input.getStack(1);

        if (!firstStack.isEmpty() && !secondStack.isEmpty()){

            if ((firstStack.getItem() == Items.ENCHANTED_BOOK || secondStack.getItem() == Items.ENCHANTED_BOOK) && !GlobalConfig.allowBookInAnvil)
                ci.cancel();

            if(secondStack.getItem().equals(ModItems.ENCHANTMENT_CATALYST) && (firstStack.isEnchantable() && !firstStack.hasEnchantments() && !firstStack.getItem().equals(ModItems.ENCHANTMENT_CATALYST))){
                //if first item is un-enchanted and can be enchanted and second is enchantment catalyst
                if(!secondStack.hasEnchantments()){
                    ci.cancel(); //do nothing if enchantment catalyst don't contains enchantments
                    return;
                }

                ItemStack outputStack = firstStack.copy();

                for(Object2IntMap.Entry<RegistryEntry<Enchantment>> e : secondStack.getEnchantments().getEnchantmentEntries()){
                    if(outputStack.canBeEnchantedWith(e.getKey(), EnchantingContext.ACCEPTABLE))
                        outputStack.addEnchantment(e.getKey(), secondStack.getEnchantments().getLevel(e.getKey()));
                }
                output.setStack(0, outputStack);
                betterEnchanting$customCost = ModEnchantmentHelper.getCatalystEnchantmentCost(outputStack);
                levelCost.set(betterEnchanting$customCost);
                ci.cancel();

            }else if(secondStack.getItem().equals(ModItems.ENCHANTMENT_CATALYST) && firstStack.getItem().equals(ModItems.ENCHANTMENT_CATALYST)){
                ItemStack outputStack = firstStack.copy();
                outputStack.set(DataComponentTypes.MAX_STACK_SIZE,1);

                //EnchantmentHelper.getEnchantments(firstStack).getEnchantmentEntries().stream().forEach(e -> outputStack.addEnchantment(e.getKey(), e.getIntValue()));
                //EnchantmentHelper.getEnchantments(secondStack).getEnchantmentEntries().stream().forEach(e -> outputStack.addEnchantment(e.getKey(), e.getIntValue()));


                output.setStack(0, ModEnchantmentHelper.combineCatalyst(firstStack, secondStack));
                int finalCost = ModEnchantmentHelper.getCatalystEnchantmentCost(firstStack);
                finalCost += ModEnchantmentHelper.getCatalystEnchantmentCost(secondStack);
                finalCost = Math.round(finalCost*GlobalConfig.catalystCombinationMultiplier);
                levelCost.set(finalCost);
                betterEnchanting$customCost = finalCost;
                ci.cancel();

            }else {
                if (firstStack.getItem() == secondStack.getItem()){
                    //if both same items
                    if(firstStack.hasEnchantments() && secondStack.hasEnchantments()){
                        //if both items has enchants
                        ci.cancel();
                    }
                }
            }
        }

    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    public void betterEnchanting$useCatalyst(PlayerEntity player, ItemStack stack, CallbackInfo ci){

        if(input.getStack(1).getItem().equals(ModItems.ENCHANTMENT_CATALYST)){

            input.setStack(0,ItemStack.EMPTY);
            if(EnchantmentHelper.hasEnchantments(stack)){//Don't destroy the catalyst if none of it's enchant where transferred to the item
                Random rand = Random.create();
                int breakChance = rand.nextBetween(0,100);
                if (breakChance <= GlobalConfig.catalystGivebackChance) //10 percent chance to return an empty catalyst
                    input.setStack(1, new ItemStack(ModItems.ENCHANTMENT_CATALYST));
                else
                    input.getStack(1).decrement(1);
            }
            if(input.getStack(1).getCount() == 0)
                input.setStack(1, ItemStack.EMPTY);

            //int levelCost = ModEnchantmentHelper.getCatalystEnchantmentCost(stack);
            if (!player.isInCreativeMode())
                player.addExperienceLevels(-betterEnchanting$customCost);
            ci.cancel();
        }

    }

    @Inject(method = "updateResult", at = @At("HEAD"))
    public void betterEnchanting$resetMaterialLevelCost(CallbackInfo ci) {
        betterEnchanting$materialRepairLevelCost = 0;
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V", ordinal = 0))
    public void betterEnchanting$addMaterialLevelCost(CallbackInfo ci) {
        betterEnchanting$materialRepairLevelCost++;
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void betterEnchanting$getShouldAddRepairCost(CallbackInfo ci, ItemStack itemStack, int i, long l, int j) {
        betterEnchanting$shouldAddRepairCost = i - betterEnchanting$materialRepairLevelCost > 0 || j > 0;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"))
    public int betterEnchanting$getNextCostConditional(int cost) {
        // Only increase repairCost of the item if the increase is not caused by
        // material repair
        if (!betterEnchanting$shouldAddRepairCost) {
            return cost;
        }
        return AnvilScreenHandler.getNextCost(cost);
    }

    @Inject(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    public void betterEnchanting$preventRepaireCost(CallbackInfo ci, ItemStack itemStack, int i, long l, int j, ItemStack itemStack2){
        AnvilScreenHandler screenHandler = (AnvilScreenHandler) (Object) this;

        levelCost.set(j);
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    public void betterEnchanting$canTakeFreeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue((player.isInCreativeMode() || player.experienceLevel >= this.levelCost.get()) && this.levelCost.get() >= 0);
    }
}
