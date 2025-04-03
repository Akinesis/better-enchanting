package cutefox.betterenchanting.screen;

import com.mojang.datafixers.util.Pair;
import cutefox.betterenchanting.Util.ModEnchantmentHelper;
import cutefox.betterenchanting.config.GlobalConfig;
import cutefox.betterenchanting.registry.ModItems;
import cutefox.betterenchanting.registry.ModScreenHandlerType;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CustomEnchantmentScreenHandler extends ScreenHandler {
    static final Identifier EMPTY_LAPIS_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/lapis_lazuli");
    private final Inventory inventory;
    private final ScreenHandlerContext context;
    public final int[] enchantmentPower;
    public final int[] enchantmentId;
    public final int[] enchantmentLevel;
    private List<EnchantmentLevelEntry> possibleEnchantments;
    public static final int ENCHANT_ARRAY_SIZE = 50;
    public static final int SHARD_FILLING_LAPIS_COST = GlobalConfig.shardFillingLapisCost;
    public static final int SHARD_FILLING_EXPERIENCE_COST = GlobalConfig.shardFillingExperienceCost;

    public CustomEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public CustomEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlerType.CUSTOM_ENCHANTMENT_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(3) {
            public void markDirty() {
                super.markDirty();
                CustomEnchantmentScreenHandler.this.onContentChanged(this);
            }
        };

        this.enchantmentPower = new int[3];
        this.enchantmentId = new int[ENCHANT_ARRAY_SIZE];
        this.enchantmentLevel = new int[ENCHANT_ARRAY_SIZE];
        this.context = context;

        this.addSlot(new Slot(this.inventory, 0, 11, 91) {
            public int getMaxItemCount() {
                return 1;
            }
        });

        this.addSlot(new Slot(this.inventory, 1, 31, 91) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.LAPIS_LAZULI);
            }

            @Override
            public Identifier getBackgroundSprite() {
                return CustomEnchantmentScreenHandler.EMPTY_LAPIS_SLOT_TEXTURE;
            }
        });

        this.addSlot(new Slot(this.inventory, 2, 20, 111) { });

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 11 + j * 18, 140 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 11 + i * 18, 198));
        }

        this.addProperty(Property.create(this.enchantmentPower, 0));
        this.addProperty(Property.create(this.enchantmentPower, 1));
        this.addProperty(Property.create(this.enchantmentPower, 2));

        for(i = 0; i < ENCHANT_ARRAY_SIZE; ++i){
            enchantmentId[i] = -1;
            enchantmentLevel[i] = -1;
            this.addProperty(Property.create(this.enchantmentId, i));
            this.addProperty(Property.create(this.enchantmentLevel, i));
        }
    }

    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            ItemStack itemStack = inventory.getStack(0);
            if (!itemStack.isEmpty() && ModEnchantmentHelper.itemIsEnchantable(itemStack)) {
                this.context.run((world, pos) -> {
                    IndexedIterable<RegistryEntry<Enchantment>> indexedIterable = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getIndexedEntries();
                    int i = 0;
                    Iterator var6 = EnchantingTableBlock.POWER_PROVIDER_OFFSETS.iterator();

                    while(var6.hasNext()) {
                        BlockPos blockPos = (BlockPos)var6.next();
                        if (EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) {
                            ++i;
                        }
                    }

                    for(int k = 0; k< ENCHANT_ARRAY_SIZE; k++){
                        this.enchantmentId[k] = -1;
                        this.enchantmentLevel[k] = -1;
                    }

                    possibleEnchantments = getListOfApplicableEnchantments(world.getRegistryManager(), itemStack, i);

                    int increment = 0;
                    if( possibleEnchantments != null && !possibleEnchantments.isEmpty()){
                        for(EnchantmentLevelEntry enchant : possibleEnchantments){
                            this.enchantmentId[increment] = indexedIterable.getRawId(enchant.enchantment);
                            this.enchantmentLevel[increment] = enchant.level;
                            increment++;
                        }
                    }

                    this.sendContentUpdates();
                });
            } else {
                if(!itemStack.isEmpty() && itemStack.getItem() == ModItems.MAGIC_SHARD_DULL){
                    this.enchantmentId[0] = -5;
                }else {
                    for(int i = 0; i < ENCHANT_ARRAY_SIZE; ++i) {
                        this.enchantmentId[i] = -1;
                        this.enchantmentLevel[i] = -1;
                    }
                }
            }
        }

    }

    public boolean onButtonClick(PlayerEntity player, int buttonId) {
        int id = (int)Math.floor(buttonId/10);
        int level = buttonId-(10*id);

        if(enchantmentId[id] > -1){
            ItemStack itemToEnchant = this.inventory.getStack(0);
            ItemStack lapisStack = this.inventory.getStack(1);
            ItemStack enchantMaterialStack = this.inventory.getStack(2);


            Optional<RegistryEntry.Reference<Enchantment>> enchant =  player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(this.enchantmentId[id]);

            if(!enchant.isEmpty()){
                Enchantment enchantment = enchant.get().value();
                RegistryEntry<Enchantment> enchantEntry1 = enchant.get();//player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchantment);
                int displayedEnchantLevel = level + 1;
                int enchantLevelCost = ModEnchantmentHelper.getEnchantmentLevelCost(enchantment,displayedEnchantLevel,itemToEnchant, player.getWorld());
                int enchantLevReq = ModEnchantmentHelper.getEnchantmentLeveRequierment(enchantment,displayedEnchantLevel);
                //Item enchantIngredient = ModEnchantmentHelper.getEnchantIngredient(enchantEntry1.getKey().get(), level);
                Item enchantIngredient = ModEnchantmentHelper.getEnchantIngredient(enchantment, level);
                int enchantIngredientCost = ModEnchantmentHelper.getEnchantmentIngredientCost(enchantment,displayedEnchantLevel,enchantIngredient);
                int tempLapisCost = (int)Math.floor(enchantLevelCost/2);
                int lapisCost = tempLapisCost<=0?1:tempLapisCost;
                boolean hasEnchantLevel = EnchantmentHelper.getLevel(enchantEntry1,itemToEnchant)>=displayedEnchantLevel;

                if(level > 0 && !ModEnchantmentHelper.itemHasPreviousLevelOfEnchant(itemToEnchant, enchantEntry1, level) && !hasEnchantLevel){
                    return false;
                }

                if(hasEnchantLevel)
                    return false;


                if ((lapisStack.isEmpty() || lapisStack.getCount() < lapisCost) && !player.isInCreativeMode()) {
                    return false;
                } else if (itemToEnchant.isEmpty() || (player.experienceLevel < enchantLevelCost || player.experienceLevel < enchantLevReq) && !player.isInCreativeMode()) {
                    return false;
                }else if((getSlot(2).getStack().getItem() != enchantIngredient || getSlot(2).getStack().getCount() < enchantIngredientCost) && !player.isInCreativeMode()){
                    return false;
                }else{
                    //can enchant
                    this.context.run((world, pos) -> {
                        RegistryEntry<Enchantment> enchantEntry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(enchant.get().value());

                        //ItemStack itemToEnchantCopy = itemToEnchant;
                        if(!player.isInCreativeMode())
                            player.applyEnchantmentCosts(itemToEnchant, enchantLevelCost);
                        itemToEnchant.addEnchantment(enchantEntry, displayedEnchantLevel);

                        lapisStack.decrementUnlessCreative(lapisCost, player);
                        if (lapisStack.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }

                        enchantMaterialStack.decrementUnlessCreative(enchantIngredientCost, player);
                        if (enchantMaterialStack.isEmpty()) {
                            this.inventory.setStack(2, ItemStack.EMPTY);
                        }

                        player.incrementStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayerEntity) {
                            Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemToEnchant, enchantLevelCost);
                        }

                        this.inventory.markDirty();
                        this.onContentChanged(this.inventory);
                        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);

                    });
                    return true;
                }
            }
            Util.logErrorOrPause("Enchantment not found for ID : " + id);
            return false;

        }else if(enchantmentId[0] == -5){
            ItemStack itemToEnchant = this.inventory.getStack(0);
            ItemStack lapisStack = this.inventory.getStack(1);

            if ((lapisStack.isEmpty() || lapisStack.getCount() < SHARD_FILLING_LAPIS_COST) && !player.isInCreativeMode()) {
                return false;
            } else if (itemToEnchant.isEmpty() || (player.experienceLevel < SHARD_FILLING_EXPERIENCE_COST) && !player.isInCreativeMode()) {
                return false;
            }else {
                this.context.run((world, pos) -> {
                    this.inventory.setStack(0, new ItemStack(ModItems.MAGIC_SHARD_FULL,1));

                    lapisStack.decrementUnlessCreative(SHARD_FILLING_LAPIS_COST, player);
                    if(!player.isInCreativeMode())
                        player.applyEnchantmentCosts(itemToEnchant, SHARD_FILLING_EXPERIENCE_COST);

                    this.inventory.markDirty();
                    this.onContentChanged(this.inventory);
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);

                });
                return true;
            }

        }else {
            String var10000 = String.valueOf(player.getName());
            Util.logErrorOrPause(var10000 + " pressed invalid button id: " + id);
            return false;
        }

    }

    private List<EnchantmentLevelEntry> getListOfApplicableEnchantments(DynamicRegistryManager registryManager, ItemStack itemToEnchant, int numberOfBookshelf){

       return ModEnchantmentHelper.getPossibleEntries(numberOfBookshelf, itemToEnchant, registryManager);

    }

    public int getLapisCount() {
        ItemStack itemStack = this.inventory.getStack(1);
        return itemStack.isEmpty() ? 0 : itemStack.getCount();
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.inventory);
        });
    }

    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.ENCHANTING_TABLE);
    }


    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot sourceSlot = this.slots.get(slot);
        if (sourceSlot.hasStack()) {
            ItemStack sourceStack = sourceSlot.getStack();
            itemStack = sourceStack.copy();

            // checks if slot is either 0, 1, or 2, as any value higher will return false
            if(slot - 2 < 1) {
                if (!this.insertItem(sourceStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            }

            else if (sourceStack.isOf(Items.LAPIS_LAZULI)) {
                if (!this.insertItem(sourceStack, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                int slotIdToInsert = 0;
                //Is not lapis
                if (this.slots.get(0).hasStack() || !this.slots.get(0).canInsert(sourceStack)) {
                    //Item slot is full or can't insert
                    if (this.slots.get(2).hasStack() || !this.slots.get(2).canInsert(sourceStack)) {
                        //Ingredint slot is also full and can't insert
                        return ItemStack.EMPTY;
                    }
                    slotIdToInsert = 2;
                } //Can insert is slot 0 (item to enchant)


                ItemStack insertedStack;
                if(slotIdToInsert == 0){
                    insertedStack = sourceStack.copyWithCount(1);
                    sourceStack.decrement(1);
                }else{
                    insertedStack = sourceStack.copy();
                    sourceStack.setCount(0);
                }

                this.slots.get(slotIdToInsert).setStack(insertedStack);
            }

            if (sourceStack.isEmpty()) {
                sourceSlot.setStack(ItemStack.EMPTY);
            }

            if (sourceStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            sourceSlot.onTakeItem(player, sourceStack);
        }

        return itemStack;
    }

    public int totalEnchantForItem(){
        int numberOfEnchant = 0;
        for(int i =0; i<ENCHANT_ARRAY_SIZE;i++){
            if(enchantmentId[i]>-1)
                numberOfEnchant++;
        }
        return numberOfEnchant;
    }

    private void autofill(ItemStack stack) {
        for (int i = 3; i < 39; ++i) {
            ItemStack itemStack =  slots.get(i).getStack();
            if (itemStack.isEmpty() || !ItemStack.areItemsAndComponentsEqual(itemStack, stack)) continue;
            int j = itemStack.getMaxCount();
            //int k = Math.min(j - itemStack2.getCount(), itemStack.getCount());
            ItemStack itemStack3 = itemStack.copyWithCount(itemStack.getCount());
            //itemStack.decrement(k);
            this.inventory.setStack(2, itemStack3);
            slots.remove(i);
            break;
            //if (itemStack3.getCount() >= j) break;
        }
    }
}
