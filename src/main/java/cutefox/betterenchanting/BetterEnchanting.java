package cutefox.betterenchanting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cutefox.betterenchanting.Util.EnchantingIngredientMapPayload;
import cutefox.betterenchanting.Util.IngredientData;
import cutefox.betterenchanting.Util.Utils;
import cutefox.betterenchanting.conditions.ModConfigConditions;
import cutefox.betterenchanting.config.GlobalConfig;
import cutefox.betterenchanting.datagen.ModEnchantIngredientMap;
import cutefox.betterenchanting.registry.*;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class BetterEnchanting implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("better-enchanting");
	public static final String MOD_ID = "BetterEnchanting";
	public static boolean NEO_ENCHANT_PRESENT = false;
	public static boolean BUMBLEZONE_PRESENT = false;
	public static boolean REPLANTMENT_PRESENT = false;
	public static boolean DUNGEONS_AND_TAVERNS_PRESENT = false;
	public static boolean HORSESHOES_PRESENT = false;
	public static boolean ENDERSCAPE_PRESENT = false;
	public static boolean DIVERSITY_PRESENT = false;
	public static boolean TOSS_UP_PRESENT = false;
	public static boolean SPELL_POWER_PRESENT = false;
	public static boolean COMBAT_ROLL_PRESENT = false;
	private List<IngredientData> customIngredientData;

	@Override
	public void onInitialize() {

		ModConfigConditions.registerConditions();

		checkForCompat();

		customIngredientData = new ArrayList<>();

		MidnightConfig.init("better-enchanting/betterEnchanting", GlobalConfig.class);
		ModItems.registerModItems();
		ModScreenHandlerType.registerModScreenHandlers();
		ModEnchantmentTags.registerModTags();
		ModEnchantIngredientMap.createMap();
		ModTradeOffers.removeEnchantedBooks();
		ModItemTags.registerModTags();
		ModLootTables.registerLootTables();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return Utils.id("enchanting");
			}

			@Override
			public void reload(ResourceManager manager) {
				// Clear Caches Here
				Gson gson = new GsonBuilder().setPrettyPrinting().create();

				for(Identifier id : manager.findResources("enchanting", path -> path.toString().endsWith(".json")).keySet()) {
					try(InputStream stream = manager.getResource(id).get().getInputStream()) {
						Reader reader = new InputStreamReader(stream);
						IngredientData data = gson.fromJson(reader, IngredientData.class);
//						LOGGER.info(data.getEnchantment_id().toString());
//						LOGGER.info("Count : " + data.getIngredients().get(0).getCount());

						customIngredientData.add(data);

					} catch(Exception e) {
						LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
					}
				}
			}
		});

		//Registry.register(Registries.ITEM_GROUP, Utils.id("item_group"), ITEM_GROUP);
		Registry.register(Registries.ITEM_GROUP, Utils.id("item_group"), generateItemGroup());
		PayloadTypeRegistry.playS2C().register(EnchantingIngredientMapPayload.ID, EnchantingIngredientMapPayload.CODEC);

		ModLootTableModifiers.modifyLootTables();

		addEventListner();

	}

	private void addEventListner(){
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
		server.execute(() -> {
			ServerPlayNetworking.send(handler.player,
					new EnchantingIngredientMapPayload(ModEnchantIngredientMap.jsonMap));
			});
		});
		ServerLifecycleEvents.SERVER_STARTED.register(e -> {

			if(e.getResourceManager().getAllNamespaces().contains("enchantplus")){
				NEO_ENCHANT_PRESENT = true;
				ModEnchantIngredientMap.loadNeoEnchantConfig();
			}

			if(e.getResourceManager().getAllNamespaces().contains("replantment")){
				REPLANTMENT_PRESENT = true;
				ModEnchantIngredientMap.loadReplantmentConfig();
			}

			if(e.getResourceManager().getAllNamespaces().contains("nova_structures")){
				DUNGEONS_AND_TAVERNS_PRESENT = true;
				LOGGER.info("Mod nova_structures (Dungeon and Taverns) is loaded ! ");
				ModEnchantIngredientMap.loadDungeonsAndTavernsConfig();
			}

			if(e.getResourceManager().getAllNamespaces().contains("toss_up") && !TOSS_UP_PRESENT){
				LOGGER.info("Datapack Toss Up is present and loaded ; Building compat for " + BetterEnchanting.MOD_ID);
				TOSS_UP_PRESENT = true;
				ModEnchantIngredientMap.loadTossUpConfig();
			}

			ModEnchantIngredientMap.genMapFromJson(e.getWorld(ServerWorld.OVERWORLD));
			ModEnchantIngredientMap.buildCustomIngredientDataMap(e.getWorld(ServerWorld.OVERWORLD), customIngredientData);
		});

		ServerLifecycleEvents.SERVER_STARTING.register(e -> {
			Utils.setRegistryManager(e.getRegistryManager());
		});

	}

	private ItemGroup generateItemGroup(){

		// inlined, changed lambda to method reference
        return FabricItemGroup.builder()
				.icon(() -> new ItemStack(ModItems.ESSENCE_OF_PROTECTION))
				.displayName(Text.translatable("itemGroup.betterenchanting.item_group"))
				.entries((context, entries) -> {
					entries.addAll(ModItems.MOD_ITEM_LIST.stream().map(Item::getDefaultStack).toList());

					if(BetterEnchanting.BUMBLEZONE_PRESENT)
						entries.addAll(ModItems.MOD_ITEM_LIST_BUMBLEZONE_COMPAT.stream().map(Item::getDefaultStack).toList());
				})
				.build();
	}

	private void checkForCompat(){
		if(FabricLoader.getInstance().isModLoaded("the_bumblezone")){
			LOGGER.info("Mod Bumblezone is present and loaded ; Building compat for "+BetterEnchanting.MOD_ID);
			BUMBLEZONE_PRESENT = true;
			//ModEnchantIngredientMap.loadBumblezoneConfig();
		}

		if(FabricLoader.getInstance().isModLoaded("incantationem")){
			LOGGER.info("Mod incantationem is present and loaded ; Building compat for "+BetterEnchanting.MOD_ID);
		}

		if(FabricLoader.getInstance().isModLoaded("horseshoes")){
			LOGGER.info("Mod horseshoes is present ; Building compat for "+BetterEnchanting.MOD_ID);
			HORSESHOES_PRESENT = true;
		}

		if(FabricLoader.getInstance().isModLoaded("mr_dungeons_andtaverns")){
			LOGGER.info("Mod nova_structures (Dungeon and Taverns) is present and loaded ; Building compat for "+BetterEnchanting.MOD_ID);
			DUNGEONS_AND_TAVERNS_PRESENT = true;
		}

		if(FabricLoader.getInstance().isModLoaded("toss_up")){
			LOGGER.info("Mod Toss Up is present and loaded ; Building compat for "+BetterEnchanting.MOD_ID);
			TOSS_UP_PRESENT = true;
			ModEnchantIngredientMap.loadTossUpConfig();
		}

		if(FabricLoader.getInstance().isModLoaded("diversity")){
			LOGGER.info("Mod Diversity is present and loaded; Building compat for "+BetterEnchanting.MOD_ID);
			DIVERSITY_PRESENT = true;
			ModEnchantIngredientMap.loadDiversityConfig();
		}

		if(FabricLoader.getInstance().isModLoaded("spell_power")){
			LOGGER.info("Mod Spell Power is present and loaded; Building compat for "+BetterEnchanting.MOD_ID);
			SPELL_POWER_PRESENT = true;
			ModEnchantIngredientMap.loadSpellPowerConfig();
		}

		if(FabricLoader.getInstance().isModLoaded("combat_roll")){
			LOGGER.info("Mod Combat Roll is present and loaded; Building compat for "+BetterEnchanting.MOD_ID);
			COMBAT_ROLL_PRESENT = true;
			ModEnchantIngredientMap.loadCombatRollConfig();
		}

		if(FabricLoader.getInstance().isModLoaded("enderscape")){
			LOGGER.info("Mod Enderscape is present and loaded; Building compat for "+BetterEnchanting.MOD_ID);
			ENDERSCAPE_PRESENT = true;
			//ModEnchantIngredientMap.loadCombatRollConfig();
		}
	}
}