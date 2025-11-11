package cutefox.betterenchanting.mixin;

import cutefox.betterenchanting.screen.CustomEnchantmentScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableMixin {

    @Inject(method = "createScreenHandlerFactory",
            at = @At(value = "RETURN", ordinal = 0),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void betterEnchanting$createCustomEnchantScreen(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<NamedScreenHandlerFactory> cir, BlockEntity blockEntity, EnchantingTableBlockEntity enchantingTableBlockEntity, Text text){

        //Return custom factory
        NamedScreenHandlerFactory retrunFactory = null;
        retrunFactory = new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
            return new CustomEnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
        }, text);

        cir.setReturnValue(retrunFactory);
    }
}
