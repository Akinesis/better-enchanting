package cutefox.betterenchanting.conditions;

import com.mojang.serialization.MapCodec;
import cutefox.betterenchanting.BetterEnchanting;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

public class NeoEnchantCompatCondition implements ResourceCondition {
    public static final MapCodec<NeoEnchantCompatCondition> CODEC = MapCodec.unit(NeoEnchantCompatCondition::new);

    @Override
    public ResourceConditionType<?> getType() {
        return ModConfigConditions.NEO_ENCHANT;
    }

    @Override
    public boolean test(@Nullable RegistryOps.RegistryInfoGetter registryInfoGetter) {
        return BetterEnchanting.NEO_ENCHANT_PRESENT;
    }

}