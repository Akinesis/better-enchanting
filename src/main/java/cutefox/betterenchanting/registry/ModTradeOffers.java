package cutefox.betterenchanting.registry;

import com.google.common.collect.ImmutableMap;
import cutefox.betterenchanting.BetterEnchanting;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModTradeOffers extends TradeOffers {

    public static final void removeEnchantedBooks(){
        //TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.LIBRARIAN) .replace(1, new TheFoxDenCollection.Factory[]{new TradeOffers.BuyItemFactory(Items.PAPER, 24, 16, 2), new TradeOffers.BuyItemFactory(Items.DIAMOND, 24, 16, 2), new TradeOffers.SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)});

        PROFESSION_TO_LEVELED_TRADE.put(VillagerProfession.LIBRARIAN,
                betterEnchanting$copyToFastUtilMap(ImmutableMap.of(
                        1, new Factory[]{new BuyItemFactory(Items.PAPER, 24, 16, 2), new EnchantmentIngredientsFactory(2, 1), new SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)},
                        2, new Factory[]{new BuyItemFactory(Items.BOOK, 4, 12, 10), new EnchantmentIngredientsFactory(5, 2), new SellItemFactory(Items.LANTERN, 1, 1, 5)},
                        3, new Factory[]{new BuyItemFactory(Items.INK_SAC, 5, 12, 20), new EnchantmentIngredientsFactory(10, 3), new SellItemFactory(Items.GLASS, 1, 4, 10)},
	                    4, new Factory[]{new BuyItemFactory(Items.WRITABLE_BOOK, 2, 12, 30), new EnchantmentIngredientsFactory(15, 4), new SellItemFactory(Items.CLOCK, 5, 1, 15), new SellItemFactory(Items.COMPASS, 4, 1, 15)},
                        5, new Factory[]{new SellItemFactory(Items.NAME_TAG, 20, 1, 30),new EnchantmentIngredientsFactory(25, 5),})));

        /*Map<VillagerProfession, Int2ObjectMap<Factory[]>> map = Map.of(
                VillagerProfession.CARTOGRAPHER, REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CARTOGRAPHER),
                VillagerProfession.ARMORER,REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.ARMORER),
                VillagerProfession.LIBRARIAN,
                betterEnchanting$copyToFastUtilMap(ImmutableMap.of(
                        1, new Factory[]{new BuyItemFactory(Items.PAPER, 24, 16, 2), createLibrarianTradeIngredientFactory(1), new SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)},
                        2, new Factory[]{new BuyItemFactory(Items.BOOK, 4, 12, 10), createLibrarianTradeIngredientFactory(5), new SellItemFactory(Items.LANTERN, 1, 1, 5)},
                        3, new Factory[]{new BuyItemFactory(Items.INK_SAC, 5, 12, 20), createLibrarianTradeIngredientFactory(10), new SellItemFactory(Items.GLASS, 1, 4, 10)},
                        4, new Factory[]{new BuyItemFactory(Items.WRITABLE_BOOK, 2, 12, 30), new SellItemFactory(Items.CLOCK, 5, 1, 15), new SellItemFactory(Items.COMPASS, 4, 1, 15)},
                        5, new Factory[]{createMasterLibrarianTradeFactory(), new SellItemFactory(Items.NAME_TAG, 20, 1, 30)})));


        TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE = map;*/

    }

    private static Int2ObjectMap<Factory[]> betterEnchanting$copyToFastUtilMap(ImmutableMap<Integer, Factory[]> map) {
        return new Int2ObjectOpenHashMap<Factory[]>(map);
    }

    public static class EnchantmentIngredientsFactory implements Factory {
        private final int experience;
        private final int tradeLevel;

        public EnchantmentIngredientsFactory(int experience, int level) {

            this.experience = experience;
            this.tradeLevel = level;
        }


        @Override
        public @Nullable TradeOffer create(ServerWorld world, Entity entity, Random random) {
            int l;
            ItemStack itemStack;
            RegistryEntryList.Named<Item> ingredients = Registries.ITEM.getOrThrow(ModItemTags.ENCHANTEMNT_INGREDIENT);
            //RegistryEntryList.Named<Item> essences = Registries.ITEM.getOrCreateEntryList(ModItemTags.ENCHANTMENT_ESSENCE);
            RegistryEntryList.Named<Item> vanillaEssences = Registries.ITEM.getOrThrow(ModItemTags.VANILLA_ESSENCE);
            List<RegistryEntry<Item>> essences = vanillaEssences.stream().collect(Collectors.toList());
            RegistryEntryList.Named<Item> librarian = Registries.ITEM.getOrThrow(ModItemTags.LIBRARIAN_RARE_INGREDIENT);

            if(BetterEnchanting.NEO_ENCHANT_PRESENT)
                essences.addAll(Registries.ITEM.getOrThrow(ModItemTags.NEOENCHANT_ESSENCE).stream().toList());

            if(BetterEnchanting.BUMBLEZONE_PRESENT)
                essences.addAll(Registries.ITEM.getOrThrow(ModItemTags.BUMBLEZONE_ESSENCE).stream().toList());

            if(BetterEnchanting.SPELL_POWER_PRESENT)
                essences.addAll(Registries.ITEM.getOrThrow(ModItemTags.SPELL_POWER_ESSENCE).stream().toList());

            if(BetterEnchanting.DUNGEONS_AND_TAVERNS_PRESENT)
                essences.addAll(Registries.ITEM.getOrThrow(ModItemTags.NOVA_ESSENCE).stream().toList());

            int price;

            if(ingredients.size() > 0 && essences.size() > 0){
                if(tradeLevel >= 4) {
                    if(random.nextBoolean()){
                        itemStack = essences.get(random.nextBetween(0,essences.size()-1)).value().getDefaultStack();
                        itemStack.setCount(1);
                        price = random.nextBetween(25,38);
                    }else {
                        if(librarian.size() > 0){
                            itemStack = librarian.get(random.nextBetween(0,librarian.size()-1)).value().getDefaultStack();
                            itemStack.setCount(random.nextBetween(1,2));
                            price = random.nextBetween(21,34);
                        }else {
                            itemStack = new ItemStack(Items.BOOK);
                            itemStack.setCount(random.nextBetween(1,2));
                            price = random.nextBetween(12,23);
                        }

                    }
                }else{
                    itemStack = ingredients.get(random.nextBetween(0,ingredients.size()-1)).value().getDefaultStack();
                    itemStack.setCount(random.nextBetween(1,3));
                    price = random.nextBetween(12,23);
                }
            }else {
                itemStack = new ItemStack(Items.BOOK);
                price = random.nextBetween(12,23);
            }
            return new TradeOffer(new TradedItem(Items.EMERALD, price), itemStack, 10, this.experience, 0.2f);
        }
    }

    public static class RebalancedEnchantmentIngredientsFactory implements Factory {
        private final int experience;
        private final int tradeLevel;


        public RebalancedEnchantmentIngredientsFactory(int experience, TagKey<Item> regionTag) {

            this.experience = experience;
            this.tradeLevel = 1;
        }

        @Override
        public TradeOffer create(ServerWorld serverworld,Entity entity, Random random) {
            int l;
            ItemStack itemStack;
            RegistryEntryList.Named<Item> ingredients = Registries.ITEM.getOrThrow(ModItemTags.ENCHANTEMNT_INGREDIENT);
            RegistryEntryList.Named<Item> essences = Registries.ITEM.getOrThrow(ModItemTags.ENCHANTMENT_ESSENCE);
            RegistryEntryList.Named<Item> librarian = Registries.ITEM.getOrThrow(ModItemTags.LIBRARIAN_RARE_INGREDIENT);

            int price;

            if(ingredients.size() > 0 && essences.size() > 0){
                if(tradeLevel >= 4) {
                    if(random.nextBoolean()){
                        itemStack = essences.get(random.nextBetween(0,essences.size()-1)).value().getDefaultStack();
                        itemStack.setCount(1);
                        price = random.nextBetween(25,38);
                    }else {
                        if(librarian.size() > 0){
                            itemStack = librarian.get(random.nextBetween(0,librarian.size()-1)).value().getDefaultStack();
                            itemStack.setCount(random.nextBetween(1,2));
                            price = random.nextBetween(21,34);
                        }else {
                            itemStack = new ItemStack(Items.BOOK);
                            itemStack.setCount(random.nextBetween(1,2));
                            price = random.nextBetween(12,23);
                        }

                    }
                }else{
                    itemStack = ingredients.get(random.nextBetween(0,ingredients.size()-1)).value().getDefaultStack();
                    itemStack.setCount(random.nextBetween(1,3));
                    price = random.nextBetween(12,23);
                }
            }else {
                itemStack = new ItemStack(Items.BOOK);
                price = random.nextBetween(12,23);
            }
            return new TradeOffer(new TradedItem(Items.EMERALD, price), itemStack, 10, this.experience, 0.2f);
        }
    }

    /*private static Factory createLibrarianTradeIngredientFactory(int experience) {
        return new betterEnchanting$TypedWrapperFactory(Map.of(
                VillagerType.DESERT, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT),
                VillagerType.JUNGLE, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT),
                VillagerType.PLAINS, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT),
                VillagerType.SAVANNA, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT),
                VillagerType.SNOW, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT),
                VillagerType.SWAMP, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT),
                VillagerType.TAIGA, new RebalancedEnchantmentIngredientsFactory(experience, ModItemTags.ENCHANTEMNT_INGREDIENT)));
    }

    private static Factory createMasterLibrarianTradeFactory() {
        return new betterEnchanting$TypedWrapperFactory(Map.of(
                VillagerType.DESERT, new RebalancedEnchantmentIngredientsFactory(30, ModItemTags.LIBRARIAN_RARE_INGREDIENT),
                VillagerType.JUNGLE, new RebalancedEnchantmentIngredientsFactory(30, ModItemTags.LIBRARIAN_RARE_INGREDIENT),
                VillagerType.PLAINS, new RebalancedEnchantmentIngredientsFactory(30, ModItemTags.LIBRARIAN_RARE_INGREDIENT),
                VillagerType.SAVANNA, new RebalancedEnchantmentIngredientsFactory(30,  ModItemTags.LIBRARIAN_RARE_INGREDIENT),
                VillagerType.SNOW, new RebalancedEnchantmentIngredientsFactory(30, ModItemTags.LIBRARIAN_RARE_INGREDIENT),
                VillagerType.SWAMP, new RebalancedEnchantmentIngredientsFactory(30, ModItemTags.LIBRARIAN_RARE_INGREDIENT),
                VillagerType.TAIGA, new RebalancedEnchantmentIngredientsFactory(30, ModItemTags.LIBRARIAN_RARE_INGREDIENT)));
    }

    private record betterEnchanting$TypedWrapperFactory(Map<VillagerType, Factory> typeToFactory) implements Factory {

        public static betterEnchanting$TypedWrapperFactory of(Factory factory, VillagerType... types) {
            return new betterEnchanting$TypedWrapperFactory((Map) Arrays.stream(types).collect(Collectors.toMap((type) -> {
                return type;
            }, (type) -> {
                return factory;
            })));
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            if (entity instanceof VillagerDataContainer villagerDataContainer) {
                VillagerType villagerType = villagerDataContainer.getVillagerData().getType();
                Factory factory = this.typeToFactory.get(villagerType);
                return factory == null ? null : factory.create(entity, random);
            } else {
                return null;
            }
        }

        public Map<VillagerType, Factory> typeToFactory() {
            return this.typeToFactory;
        }
    }*/


}
