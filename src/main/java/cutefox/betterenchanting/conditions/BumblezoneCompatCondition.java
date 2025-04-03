package cutefox.betterenchanting.conditions;

import com.mojang.serialization.MapCodec;
import cutefox.betterenchanting.BetterEnchanting;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

public class BumblezoneCompatCondition implements ResourceCondition {
    public static final MapCodec<BumblezoneCompatCondition> CODEC = MapCodec.unit(BumblezoneCompatCondition::new);

    @Override
    public ResourceConditionType<?> getType() {
        return ModConfigConditions.BUMBLEZONE;
    }

    @Override
    public boolean test(@Nullable RegistryOps.RegistryInfoGetter registryInfoGetter) {
        return BetterEnchanting.BUMBLEZONE_PRESENT;
    }
}