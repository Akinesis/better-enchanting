package cutefox.betterenchanting.datagen;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.Util.IngredientData;
import cutefox.betterenchanting.Util.Utils;
import cutefox.betterenchanting.registry.ModItems;
import io.netty.buffer.ByteBuf;
import net.bunten.enderscape.registry.EnderscapeItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.lang.reflect.Type;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ModEnchantIngredientMap {

    public static HashMap<String, List<String>> defaultMap = new HashMap<>();
    private static final HashMap<Enchantment, List<Item>> ENCHANTMENT_INGREDIENTS_MAP = new HashMap<>();
    private static HashMap<Enchantment, List<Item>> externalEnchantmentIngredientsMap = new HashMap<>();
    private static HashMap<Enchantment, List<Item>> modedmap = new HashMap<>();
    public static Map<String, List<String>> jsonMap = new HashMap<>();
    public static Map<Enchantment, IngredientData> customIngredientsDataMap = new HashMap<>();

    static{
        //Armor enchantment
        defaultMap.put(Enchantments.PROTECTION.getValue().toString(), listOfIdentifiers(List.of(Items.COPPER_INGOT,Items.IRON_INGOT,Items.DIAMOND, ModItems.ESSENCE_OF_PROTECTION)));
        defaultMap.put(Enchantments.FIRE_PROTECTION.getValue().toString(), listOfIdentifiers(List.of(Items.MAGMA_BLOCK,Items.MAGMA_CREAM,Items.LAVA_BUCKET,ModItems.ESSENCE_OF_FIRE_PROTECTION)));
        defaultMap.put(Enchantments.FEATHER_FALLING.getValue().toString(), listOfIdentifiers(List.of(Items.FEATHER,Items.PHANTOM_MEMBRANE,Items.WIND_CHARGE,ModItems.ESSENCE_OF_FEATHER)));
        defaultMap.put(Enchantments.BLAST_PROTECTION.getValue().toString(), listOfIdentifiers(List.of(Items.GUNPOWDER, Items.TNT, Items.CREEPER_HEAD, ModItems.ESSENCE_OF_BLAST_PROTECTION)));
        defaultMap.put(Enchantments.PROJECTILE_PROTECTION.getValue().toString(), listOfIdentifiers(List.of(Items.LEATHER,Items.BRICK,Items.IRON_BARS,ModItems.ESSENCE_OF_PROJECTILE_PROTECTION)));
        defaultMap.put(Enchantments.RESPIRATION.getValue().toString(), listOfIdentifiers(List.of(Items.PUFFERFISH,Items.TURTLE_SCUTE,ModItems.ESSENCE_OF_RESPIRATION)));
        defaultMap.put(Enchantments.AQUA_AFFINITY.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SEA)));
        defaultMap.put(Enchantments.THORNS.getValue().toString(), listOfIdentifiers(List.of(Items.CACTUS,Items.PUFFERFISH_BUCKET,ModItems.ESSENCE_OF_THORN)));
        defaultMap.put(Enchantments.DEPTH_STRIDER.getValue().toString(), listOfIdentifiers(List.of(Items.COD,Items.SPONGE, ModItems.ESSENCE_OF_SEA)));
        defaultMap.put(Enchantments.FROST_WALKER.getValue().toString(), listOfIdentifiers(List.of(Items.BLUE_ICE,ModItems.ESSENCE_OF_ICE))); //Treasure
        defaultMap.put(Enchantments.SOUL_SPEED.getValue().toString(), listOfIdentifiers(List.of(ModItems.SOUL_ESSENCE_1,ModItems.SOUL_ESSENCE_2,ModItems.SOUL_ESSENCE_3))); //Treasure
        defaultMap.put(Enchantments.SWIFT_SNEAK.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SNEAK_1,ModItems.ESSENCE_OF_SNEAK_2,ModItems.ESSENCE_OF_SNEAK_3))); //Treasure

        //Sword enchantment
        defaultMap.put(Enchantments.SHARPNESS.getValue().toString(), listOfIdentifiers(List.of(Items.FLINT,Items.IRON_SWORD,Items.GRINDSTONE,Items.DIAMOND,ModItems.ESSENCE_OF_SHARPNESS)));
        defaultMap.put(Enchantments.SMITE.getValue().toString(), listOfIdentifiers(List.of(Items.ROTTEN_FLESH,Items.GLOWSTONE,Items.QUARTZ,Items.AMETHYST_SHARD,ModItems.ESSENCE_OF_SMITE)));
        defaultMap.put(Enchantments.BANE_OF_ARTHROPODS.getValue().toString(), listOfIdentifiers(List.of(Items.SHEARS,Items.SPIDER_EYE,Items.COBWEB,Items.FERMENTED_SPIDER_EYE,ModItems.ESSENCE_OF_ARTHROPODS)));
        defaultMap.put(Enchantments.KNOCKBACK.getValue().toString(), listOfIdentifiers(List.of(Items.PISTON, ModItems.ESSENCE_OF_KNOCKBACK)));
        defaultMap.put(Enchantments.FIRE_ASPECT.getValue().toString(), listOfIdentifiers(List.of(Items.FIRE_CHARGE,ModItems.ESSENCE_OF_FIRE)));
        defaultMap.put(Enchantments.LOOTING.getValue().toString(), listOfIdentifiers(List.of(Items.EMERALD,Items.SPORE_BLOSSOM,ModItems.ESSENCE_OF_LOOTING)));
        defaultMap.put(Enchantments.SWEEPING_EDGE.getValue().toString(), listOfIdentifiers(List.of(Items.IRON_BARS,Items.OBSIDIAN,ModItems.ESSENCE_OF_SWEEPING)));

        //Bow enchantment
        defaultMap.put(Enchantments.POWER.getValue().toString(), listOfIdentifiers(List.of(Items.SNOWBALL,Items.LEATHER,Items.GHAST_TEAR,Items.SHULKER_SHELL,ModItems.ESSENCE_OF_POWER)));
        defaultMap.put(Enchantments.PUNCH.getValue().toString(), listOfIdentifiers(List.of(Items.DEEPSLATE,ModItems.ESSENCE_OF_PUNCH)));
        defaultMap.put(Enchantments.FLAME.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_FIRE)));
        defaultMap.put(Enchantments.INFINITY.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_ARROWS)));

        //Tool enchantement
        defaultMap.put(Enchantments.EFFICIENCY.getValue().toString(), listOfIdentifiers(List.of(Items.FLINT,Items.GOLDEN_APPLE,Items.OBSIDIAN,Items.CHORUS_FRUIT,ModItems.ESSENCE_OF_EFFICIENCY)));
        defaultMap.put(Enchantments.SILK_TOUCH.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SILK_TOUCH)));
        defaultMap.put(Enchantments.FORTUNE.getValue().toString(), listOfIdentifiers(List.of(Items.GOLD_BLOCK,Items.EMERALD_BLOCK,ModItems.ESSENCE_OF_FORTUNE)));

        //Fishing tool enchantment
        defaultMap.put(Enchantments.LUCK_OF_THE_SEA.getValue().toString(), listOfIdentifiers(List.of(Items.NAUTILUS_SHELL,Items.HEART_OF_THE_SEA,ModItems.ESSENCE_OF_SEA_LUCK)));
        defaultMap.put(Enchantments.LURE.getValue().toString(), listOfIdentifiers(List.of(Items.CARROT_ON_A_STICK,Items.BREAD,ModItems.ESSENCE_OF_LURE)));

        //Anything enchantment
        defaultMap.put(Enchantments.UNBREAKING.getValue().toString(), listOfIdentifiers(List.of(Items.DIAMOND,Items.CRYING_OBSIDIAN, ModItems.ESSENCE_OF_UNBREAKING)));
        defaultMap.put(Enchantments.MENDING.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_MENDING))); //Treasure

        //Trident enchantment
        defaultMap.put(Enchantments.CHANNELING.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_CHANNELING)));
        defaultMap.put(Enchantments.IMPALING.getValue().toString(), listOfIdentifiers(List.of(Items.POINTED_DRIPSTONE,Items.IRON_BARS,Items.QUARTZ,Items.DIAMOND_SWORD,ModItems.ESSENCE_OF_IMPALING)));
        defaultMap.put(Enchantments.LOYALTY.getValue().toString(), listOfIdentifiers(List.of(Items.BONE_BLOCK,Items.GOLDEN_CARROT,ModItems.ESSENCE_OF_LOYALTY)));
        defaultMap.put(Enchantments.RIPTIDE.getValue().toString(), listOfIdentifiers(List.of(Items.WATER_BUCKET,Items.NAUTILUS_SHELL,ModItems.ESSENCE_OF_RIPTIDE)));

        //Crossbow enchantment
        defaultMap.put(Enchantments.MULTISHOT.getValue().toString(), listOfIdentifiers(List.of(ModItems.ESSENCE_OF_MULTISHOT)));
        defaultMap.put(Enchantments.PIERCING.getValue().toString(), listOfIdentifiers(List.of(Items.FLINT,Items.ARROW,Items.SPECTRAL_ARROW,ModItems.ESSENCE_OF_PIERCING)));
        defaultMap.put(Enchantments.QUICK_CHARGE.getValue().toString(), listOfIdentifiers(List.of(Items.AMETHYST_SHARD,Items.GLOWSTONE,ModItems.ESSENCE_OF_QUICK_CHARGE)));

        //Mace enchantment
        defaultMap.put(Enchantments.DENSITY.getValue().toString(), listOfIdentifiers(List.of(Items.STONE,Items.DEEPSLATE,Items.OBSIDIAN,Items.LODESTONE,ModItems.ESSENCE_OF_DENSITY)));
        defaultMap.put(Enchantments.BREACH.getValue().toString(), listOfIdentifiers(List.of(Items.IRON_INGOT,Items.SMOOTH_STONE,Items.TNT,ModItems.ESSENCE_OF_BREACH)));
        defaultMap.put(Enchantments.WIND_BURST.getValue().toString(), listOfIdentifiers(List.of(Items.SUGAR_CANE,Items.WIND_CHARGE,ModItems.ESSENCE_OF_WIND)));

    }

    public static void createMap() {
        BetterEnchanting.LOGGER.info("Creating enchantement ingredient map for mod : "+BetterEnchanting.MOD_ID);

    }

    public static void genMapFromJson(World world){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Resolve default config for supported mod here. Put entry in default map

        try {

            File configFolder = new File(FabricLoader.getInstance().getConfigDir().resolve("better-enchanting").toString());
            configFolder.mkdirs();

            File configFile = new File(configFolder.getAbsolutePath()+"/enchantment_ingredients.json");
            if(configFile.createNewFile()){
                //init default file
                BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
                String temp = gson.toJson(defaultMap);
                writer.write(temp);
                writer.close();
            }

            JsonReader reader = new JsonReader(new FileReader(configFile));
            jsonMap = gson.fromJson(reader, Map.class);
            reader.close();

            //get enchant present in default map but not in config and
            Map<String, List<String>> missingEntries = getEnchantNotPresentInConfig(world);

            //Append entries not present in config file
            if(!missingEntries.isEmpty()){
                BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
                jsonMap.putAll(missingEntries);
                String temp = gson.toJson(jsonMap);
                writer.append(temp);
                writer.close();
            }

            if (!world.isClient){
                genMapFromJsonStringMap(world, jsonMap);
            }
        } catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }

    public static void genMapFromJsonStringMap(World world, Map<String, List<String>> stringMap) {
        //enchantmentIngredientsMap;
        Enchantment enchantment;

        for (String key : stringMap.keySet()) {
            Identifier enchantId = Identifier.of(key);
            enchantment = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).get(enchantId);

            List<Item> ingredients = new ArrayList<>();

            stringMap.get(key).forEach(s -> {
                Item temp;
                Identifier itemId = Identifier.of(s);
                temp = Registries.ITEM.get(itemId);
                ingredients.add(temp!=null?temp:Items.BARRIER);
            });

            ENCHANTMENT_INGREDIENTS_MAP.put(enchantment, ingredients);

            //Merge map containing any entry added by external mods via API
            //If externalMap contains enchants already present in default map, entry will be replaced by the new ones.
            if(!externalEnchantmentIngredientsMap.isEmpty()){
                ENCHANTMENT_INGREDIENTS_MAP.putAll(externalEnchantmentIngredientsMap);
            }

        }
    }

    public static void buildCustomIngredientDataMap(World world, List<IngredientData> ingredientData){
        if (!world.isClient){
            Enchantment enchantment;
            for(IngredientData data : ingredientData){
                enchantment = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).get(data.getEnchantment_id());
                customIngredientsDataMap.put(enchantment, data);
            }
        }

    }

    /**
     * Add the ingredients for the enchantment of the identifier in parameters
     * If possible, prefer the {@link #addEnchantmentIngredient(Enchantment, List)} of this call.
     * @param enchantmentId The Identifier of the enchantment
     * @param ingredients A list of the ingredients for the enchantment, ordered by level. (so first entry is for Enchantment I, then Enchantment II etc.)
     * @return True if enchantment is found and successfully added to the map. False otherwise.
     */
    private static boolean addEnchantmentIngredient(Identifier enchantmentId, List<Item> ingredients){
        Registry<Enchantment> enchantRegistry =  Utils.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        Enchantment enchantment = enchantRegistry.get(enchantmentId);

        return addEnchantmentIngredient(enchantment, ingredients);

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

        externalEnchantmentIngredientsMap.put(enchantment,ingredients);
        return true;
    }

    private static List<String> listOfIdentifiers(List<Item> items){

        List<String> list = new ArrayList<>();

        for(Item item : items){
            list.add(Registries.ITEM.getId(item).toString());
        }

        return list;
    }

    private static Map<String, List<String>> getEnchantNotPresentInConfig(World world) throws Exception{

        Map<String, List<String>> missingEntries = new HashMap<>();

        for(String enchant : defaultMap.keySet()){
            if(!jsonMap.keySet().contains(enchant))
                missingEntries.put(enchant, defaultMap.get(enchant));
        }

        Set<Map.Entry<RegistryKey<Enchantment>, Enchantment>> entrySet = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntrySet();

        entrySet.forEach(entry -> {
            if(!jsonMap.keySet().contains(entry.getKey().getValue().toString())){
                List<String> barrierList = new ArrayList<>();
                for(int i = 0; i<entry.getValue().getMaxLevel();i++){
                    barrierList.add("minecraft:barrier");
                }
                missingEntries.put(entry.getKey().getValue().toString(), barrierList);
            }

        });

        return missingEntries;
    }

    public static final PacketCodec<ByteBuf, Map<String, List<String>>> MAP_CODEC = new PacketCodec<ByteBuf, Map<String, List<String>>>() {
        public Map<String, List<String>> decode(ByteBuf byteBuf) {
            Gson gson = new GsonBuilder().create();
            int length = byteBuf.readInt();
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            String message = new String(bytes, Charsets.UTF_8);
            Type mapType = new TypeToken<Map<String, List<String>>>() {
            }.getType();
            return gson.fromJson(message, mapType);
        }

        public void encode(ByteBuf byteBuf, Map<String, List<String>> blockPos) {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(jsonMap);
            byte[] bytes = json.getBytes(Charsets.UTF_8);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    };

    public static Item getIngredientOfLevel(Enchantment enchantment, int enchantmentLevel){

        //BetterEnchanting.LOGGER.info("Custom enchant size : "+customIngredientsDataMap.size());
        Item ingredient = null;

        if(customIngredientsDataMap.containsKey(enchantment))
            ingredient= customIngredientsDataMap.get(enchantment).getIngredientForLevel(enchantmentLevel);

        if(ingredient == null)
            if(ENCHANTMENT_INGREDIENTS_MAP.containsKey(enchantment))
                if(ENCHANTMENT_INGREDIENTS_MAP.get(enchantment).size()>enchantmentLevel)
                    ingredient = ENCHANTMENT_INGREDIENTS_MAP.get(enchantment).get(enchantmentLevel);

        return ingredient;
    }

    public static int getCostOfLevel(Enchantment enchantment, int level){

        int cost = 0;
        if(customIngredientsDataMap.containsKey(enchantment))
            cost= customIngredientsDataMap.get(enchantment).getIngredientCostForLevel(level);

        return cost;
    }

    public static void loadNeoEnchantConfig(){

        defaultMap.put("enchantplus:bow/accuracy_shot", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_ARROWS)));
        defaultMap.put("enchantplus:boots/agility", listOfIdentifiers(List.of(Items.SUGAR,Items.RABBIT_STEW,Items.REDSTONE_BLOCK,Items.WIND_CHARGE,ModItems.ESSENCE_OF_AGILITY)));
        defaultMap.put("enchantplus:elytra/armored", listOfIdentifiers(List.of(Items.IRON_BARS,Items.IRON_BLOCK,Items.OBSIDIAN,ModItems.ESSENCE_OF_PROTECTION)));
        defaultMap.put("enchantplus:sword/attack_speed", listOfIdentifiers(List.of(Items.GOLDEN_APPLE,ModItems.ESSENCE_OF_COMBAT)));
        defaultMap.put("enchantplus:helmet/auto_feed", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_FOOD)));
        defaultMap.put("enchantplus:tools/auto_smelt", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SMELTING)));
        defaultMap.put("enchantplus:bow/breezing_arrow", listOfIdentifiers(List.of(Items.CROSSBOW,Items.WIND_CHARGE,ModItems.ESSENCE_OF_ARROWS)));
        defaultMap.put("enchantplus:helmet/bright_vision", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SIGHT)));
        defaultMap.put("enchantplus:chestplate/builder_arm", listOfIdentifiers(List.of(Items.CRAFTING_TABLE,Items.COBBLED_DEEPSLATE,Items.GRASS_BLOCK,Items.CRAFTER,ModItems.ESSENCE_OF_BUILDING)));
        defaultMap.put("enchantplus:bow/echo_shot", listOfIdentifiers(List.of(Items.ECHO_SHARD,Items.SCULK_SENSOR,ModItems.ESSENCE_OF_ARROWS)));
        defaultMap.put("enchantplus:pickaxe/experimental_bedrock_breaker", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_MINING)));
        defaultMap.put("enchantplus:bow/explosive_arrow", listOfIdentifiers(List.of(Items.TNT,Items.CREEPER_HEAD,ModItems.ESSENCE_OF_ARROWS)));
        defaultMap.put("enchantplus:leggings/fast_swim", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SEA)));
        defaultMap.put("enchantplus:sword/fear", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_FEAR)));
        defaultMap.put("enchantplus:armor/fury", listOfIdentifiers(List.of(Items.SUGAR,Items.IRON_SWORD,Items.DIAMOND_AXE,ModItems.ESSENCE_OF_COMBAT)));
        defaultMap.put("enchantplus:boots/lava_walker", listOfIdentifiers(List.of(Items.MAGMA_BLOCK,Items.LAVA_BUCKET,ModItems.ESSENCE_OF_FIRE)));
        defaultMap.put("enchantplus:leggings/leaping", listOfIdentifiers(List.of(Items.PISTON,ModItems.ESSENCE_OF_AGILITY)));
        defaultMap.put("enchantplus:sword/life_steal", listOfIdentifiers(List.of(Items.SWEET_BERRIES,Items.IRON_SWORD,ModItems.ESSENCE_OF_VAMPIRISM)));
        defaultMap.put("enchantplus:armor/lifeplus", listOfIdentifiers(List.of(Items.APPLE,Items.RABBIT_STEW,Items.NETHER_WART_BLOCK,Items.DRAGON_HEAD,ModItems.ESSENCE_OF_HEALTH)));
        defaultMap.put("enchantplus:trident/magical_water", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SEA)));
        defaultMap.put("enchantplus:tools/miningplus", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_MINING)));
        defaultMap.put("enchantplus:sword/poison_aspect", listOfIdentifiers(List.of(Items.SPIDER_EYE,Items.PUFFERFISH_BUCKET,Items.POISONOUS_POTATO,ModItems.ESSENCE_OF_POISON)));
        defaultMap.put("enchantplus:sword/pull", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_PULLING)));
        defaultMap.put("enchantplus:sword/reach", listOfIdentifiers(List.of(Items.STICK,Items.LIGHTNING_ROD,ModItems.ESSENCE_OF_REACH)));
        defaultMap.put("enchantplus:hoe/scyther", listOfIdentifiers(List.of(Items.HAY_BLOCK,Items.GOLDEN_HOE,ModItems.ESSENCE_OF_FORAGING)));
        defaultMap.put("enchantplus:boots/sky_walk", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_WIND)));
        defaultMap.put("enchantplus:pickaxe/spawner_touch", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SILK_TOUCH)));
        defaultMap.put("enchantplus:boots/step_assist", listOfIdentifiers(List.of(Items.SMOOTH_STONE_SLAB,Items.RABBIT_FOOT,ModItems.ESSENCE_OF_AGILITY)));
        defaultMap.put("enchantplus:bow/storm_arrow", listOfIdentifiers(List.of(Items.COPPER_BLOCK,ModItems.ESSENCE_OF_ARROWS)));
        defaultMap.put("enchantplus:mace/striker", listOfIdentifiers(List.of(Items.LIGHTNING_ROD,ModItems.ESSENCE_OF_STRIKE)));
        defaultMap.put("enchantplus:axe/timber", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_FORAGING)));
        defaultMap.put("enchantplus:pickaxe/vein_miner", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_MINING)));
        defaultMap.put("enchantplus:armor/venom_protection", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_POISON_PROTECTION)));
        defaultMap.put("enchantplus:helmet/voidless", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_LEVITATION)));
        defaultMap.put("enchantplus:mace/wind_propulsion", listOfIdentifiers(List.of(Items.TNT,Items.WIND_CHARGE,ModItems.ESSENCE_OF_WIND)));
        defaultMap.put("enchantplus:sword/xp_boost", listOfIdentifiers(List.of(Items.ENDER_EYE,ModItems.MAGIC_SHARD_DULL,ModItems.ESSENCE_OF_EXPERIENCE)));

    }

    /*public static void loadBumblezoneConfig(){
        defaultMap.put(BzEnchantments.NEUROTOXINS.toString(), listOfIdentifiers(List.of(BzItems.BEE_SOUP.get(),ModItems.ESSENCE_OF_NEUROTOXIN)));
        defaultMap.put(BzEnchantments.POTENT_POISON.toString(), listOfIdentifiers(List.of(Items.FERMENTED_SPIDER_EYE,Items.PUFFERFISH_BUCKET,ModItems.ESSENCE_OF_POISON)));
        defaultMap.put(BzEnchantments.COMB_CUTTER.toString(), listOfIdentifiers(List.of(BzItems.POROUS_HONEYCOMB.get(),ModItems.ESSENCE_OF_COMB_CUTTER)));

    }*/

    public static void loadDungeonsAndTavernsConfig() {
        defaultMap.put("nova_structures:antidote", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_POISON_PROTECTION)));
        defaultMap.put("nova_structures:ghasted", listOfIdentifiers(List.of(Items.GHAST_TEAR, Items.FIRE_CHARGE, ModItems.ESSENCE_OF_ARROWS)));
        defaultMap.put("nova_structures:gravity", listOfIdentifiers(List.of(Items.OBSIDIAN, Items.LODESTONE, ModItems.ESSENCE_OF_GRAVITY)));
        defaultMap.put("nova_structures:illagers_bane", listOfIdentifiers(List.of(Items.CACTUS, Items.IRON_SWORD, Items.IRON_BLOCK, Items.OMINOUS_BOTTLE, ModItems.ESSENCE_OF_COMBAT)));
        defaultMap.put("nova_structures:traveler", listOfIdentifiers(List.of(Items.GOLDEN_CARROT, Items.RABBIT_FOOT, ModItems.ESSENCE_OF_AGILITY)));
        defaultMap.put("nova_structures:outreach", listOfIdentifiers(List.of(Items.SUGAR_CANE, Items.WIND_CHARGE, Items.SUGAR_CANE, ModItems.ESSENCE_OF_REACH)));
        defaultMap.put("nova_structures:photosynthesis", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_PHOTOSYNTHESIS)));
        defaultMap.put("nova_structures:wax_wings", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_WINGS)));
        defaultMap.put("nova_structures:wither_coated", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_BUILDING)));
        defaultMap.put("nova_structures:multishot", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_MULTISHOT)));
        defaultMap.put("nova_structures:piercing", listOfIdentifiers(List.of(Items.FLINT, Items.ARROW, Items.IRON_SWORD, Items.TRIDENT,ModItems.ESSENCE_OF_PIERCING)));
        defaultMap.put("nova_structures:power", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_POWER)));
    }

    public static void loadReplantmentConfig(){
        defaultMap.put("replantment:replant", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_FORAGING)));
    }

    public static void loadDiversityConfig(){
        defaultMap.put("diversity:capacity",listOfIdentifiers(List.of(Items.CHEST,Items.ENDER_CHEST,ModItems.ESSENCE_OF_CAPACITY)));
        defaultMap.put("diversity:refill",listOfIdentifiers(List.of(ModItems.ESSENCE_OF_REFILL)));
    }

    private static Map<Enchantment, List<Item>> getEnchantIngredientsMap(){
        return ENCHANTMENT_INGREDIENTS_MAP;
    }

    public static List<Item> getIngredientsOfEnchantment(Enchantment enchantment) {
        return ENCHANTMENT_INGREDIENTS_MAP.get(enchantment);
    }

    public static void loadTossUpConfig() {
        defaultMap.put("toss_up:toss_up",listOfIdentifiers(List.of(Items.TNT,Items.FIRE_CHARGE,ModItems.ESSENCE_OF_EXPLOSION)));
    }

    public static void loadSpellPowerConfig() {
        defaultMap.put("spell_power:critical_chance",listOfIdentifiers(List.of(Items.REDSTONE,Items.GLOWSTONE_DUST,Items.FIRE_CHARGE,Items.GHAST_TEAR,ModItems.ESSENCE_OF_CRITICAL_SPELL)));
        defaultMap.put("spell_power:critical_damage",listOfIdentifiers(List.of(Items.GUNPOWDER,Items.LAPIS_LAZULI,Items.TNT,Items.FIRE_CHARGE,ModItems.ESSENCE_OF_CRITICAL_SPELL)));
        defaultMap.put("spell_power:energize",listOfIdentifiers(List.of(Items.REDSTONE,Items.END_ROD,Items.COPPER_BLOCK,Items.TRIDENT,ModItems.ESSENCE_OF_ENERGY)));
        defaultMap.put("spell_power:haste",listOfIdentifiers(List.of(Items.SUGAR,Items.GLOW_BERRIES,Items.EMERALD,Items.GLOWSTONE,ModItems.ESSENCE_OF_HASTE)));
        defaultMap.put("spell_power:magic_protection",listOfIdentifiers(List.of(Items.IRON_INGOT,Items.COPPER_GRATE,Items.CRYING_OBSIDIAN,ModItems.ESSENCE_OF_MAGIC_PROTECTION)));
        defaultMap.put("spell_power:soulfrost",listOfIdentifiers(List.of(Items.SNOWBALL,Items.POWDER_SNOW_BUCKET,Items.PACKED_ICE,Items.BLUE_ICE,ModItems.ESSENCE_OF_SOULFROST)));
        defaultMap.put("spell_power:spell_power",listOfIdentifiers(List.of(Items.DIAMOND,Items.REDSTONE_BLOCK,Items.EMERALD_BLOCK,Items.DIAMOND_BLOCK,ModItems.ESSENCE_OF_SPELL_POWER)));
        defaultMap.put("spell_power:sunfire",listOfIdentifiers(List.of(Items.COAL,Items.SOUL_CAMPFIRE,Items.SOUL_CAMPFIRE,Items.BLAZE_POWDER,ModItems.ESSENCE_OF_SUNFIRE)));
        defaultMap.put("spell_engine:spell_infinity",listOfIdentifiers(List.of(ModItems.ESSENCE_OF_SPELL_INFINITY)));
    }
    public static void loadCombatRollConfig() {
        defaultMap.put("combat_roll:multi_roll",listOfIdentifiers(List.of(Items.LEATHER_BOOTS,Items.PISTON,ModItems.ESSENCE_OF_MULTI_ROLL)));
        defaultMap.put("combat_roll:longfooted",listOfIdentifiers(List.of(Items.IRON_BOOTS,Items.LIGHTNING_ROD,Items.END_ROD,ModItems.ESSENCE_OF_LONGFOOT)));
        defaultMap.put("combat_roll:acrobat",listOfIdentifiers(List.of(Items.SUGAR,Items.REDSTONE,Items.DIAMOND_LEGGINGS,Items.CROSSBOW,ModItems.ESSENCE_OF_AGILITY)));
    }

    public static void loadEnderscapeConfig(){
        defaultMap.put("enderscape:bundling", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_CAPACITY)));
        defaultMap.put("enderscape:lightspeed", listOfIdentifiers(List.of(Items.ENDER_PEARL, EnderscapeItems.NEBULITE,ModItems.ESSENCE_OF_ENERGY)));
        defaultMap.put("enderscape:transdimensional", listOfIdentifiers(List.of(ModItems.ESSENCE_OF_BUILDING)));
    }

}
