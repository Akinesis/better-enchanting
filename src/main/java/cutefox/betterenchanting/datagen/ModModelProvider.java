package cutefox.betterenchanting.datagen;

import cutefox.betterenchanting.BetterEnchanting;
import cutefox.betterenchanting.registry.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BetterEnchanting.LOGGER.info("Generating block model data for : "+BetterEnchanting.MOD_ID);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        BetterEnchanting.LOGGER.info("Generating item model data for : "+BetterEnchanting.MOD_ID);

        //region MISC
        //itemModelGenerator.register(ModItems.STEEL_BLEND, Models.GENERATED);
        //itemModelGenerator.register(ModItems.STEEL_INGOT, Models.GENERATED);
        //endregion

        //region ENCHANTMENT INGREDIENTS
        ModItems.MOD_ITEM_LIST.stream().forEach(item -> {
            itemModelGenerator.register(item, Models.GENERATED);
        });
        //endregion

    }
}
