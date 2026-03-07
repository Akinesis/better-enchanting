package cutefox.betterenchanting.mixin;

//import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//import com.terraformersmc.modmenu.util.mod.Mod;
//import cutefox.betterenchanting.config.GlobalConfig;
import cutefox.betterenchanting.registry.ModItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandlerMixin{

    @Shadow
    private Inventory result;
    @Shadow
    Inventory input;


    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
    public void betterEnchanting$acceptCatalyst(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci){

        GrindstoneScreenHandler instance = (GrindstoneScreenHandler)(Object)this;

        instance.slots.clear();
        instance.trackedStacks.clear();
        //instance.previousTrackedStacks.clear();

        instance.addSlot(new Slot( input, 0, 49, 19) {
            public boolean canInsert(ItemStack stack) {
                boolean vanillaCheck = stack.isDamageable() || EnchantmentHelper.hasEnchantments(stack);
                return vanillaCheck || (stack.getItem().equals(ModItems.ENCHANTMENT_CATALYST) && EnchantmentHelper.hasEnchantments(stack));
            }
        });
        instance.addSlot(new Slot( input, 1, 49, 40) {
            public boolean canInsert(ItemStack stack) {
                boolean vanillaCheck = stack.isDamageable() || EnchantmentHelper.hasEnchantments(stack);
                return vanillaCheck || (stack.getItem().equals(ModItems.ENCHANTMENT_CATALYST) && !EnchantmentHelper.hasEnchantments(stack));
            }
        });
        instance.addSlot(new Slot(this.result, 2, 129, 34) {
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                context.run((world, pos) -> {
                    if (world instanceof ServerWorld) {
                        ExperienceOrbEntity.spawn((ServerWorld)world, Vec3d.ofCenter(pos), this.getExperience(world));
                    }

                    world.syncWorldEvent(1042, pos, 0);
                });
                input.setStack(0, ItemStack.EMPTY);
                if(input.getStack(1).getItem().equals(ModItems.ENCHANTMENT_CATALYST))
                    input.getStack(1).decrement(1);
                else
                    input.setStack(1, ItemStack.EMPTY);
            }

            private int getExperience(World world) {
                int i = 0;
                i += this.getExperience(input.getStack(0));
                i += this.getExperience(input.getStack(1));
                if (i > 0) {
                    int j = (int)Math.ceil((double)i / 2.0);
                    return j + world.random.nextInt(j);
                } else {
                    return 0;
                }
            }

            private int getExperience(ItemStack stack) {
                int i = 0;
                ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(stack);
                Iterator var4 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

                while(var4.hasNext()) {
                    Object2IntMap.Entry<RegistryEntry<Enchantment>> entry = (Object2IntMap.Entry)var4.next();
                    RegistryEntry<Enchantment> registryEntry = (RegistryEntry)entry.getKey();
                    int j = entry.getIntValue();
                    if (!registryEntry.isIn(EnchantmentTags.CURSE)) {
                        i += ((Enchantment)registryEntry.value()).getMinPower(j);
                    }
                }

                return i;
            }
        });

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                instance.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            instance.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

    }

    @Inject(method = "getOutputStack", at = @At(value = "HEAD"), cancellable = true)
    public void betterEnchanting$createCatalyst(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir){
        boolean firstItemIsCatalyst = firstInput.isEmpty()?false:firstInput.getItem().equals(ModItems.ENCHANTMENT_CATALYST);
        boolean secondItemIsCatalyst = secondInput.isEmpty()?false:secondInput.getItem().equals(ModItems.ENCHANTMENT_CATALYST);

        if(firstItemIsCatalyst && secondInput.isEmpty())
            cir.setReturnValue(new ItemStack(ModItems.ENCHANTMENT_CATALYST));

        if(secondItemIsCatalyst && !EnchantmentHelper.hasEnchantments(secondInput)){
            if(!firstInput.isEmpty() && EnchantmentHelper.hasEnchantments(firstInput)){

                ItemStack output = new ItemStack(ModItems.ENCHANTMENT_CATALYST);

                ItemEnchantmentsComponent enchantmentsComponent = EnchantmentHelper.getEnchantments(firstInput);

                enchantmentsComponent.getEnchantmentEntries().stream().forEach(e -> output.addEnchantment(e.getKey(), e.getIntValue()));
                output.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE,true);
                output.set(DataComponentTypes.MAX_STACK_SIZE,1);

                cir.setReturnValue(output);
            }
        }
    }

}
