package lunaglaxe7.random.traceon.mixin;

import lunaglaxe7.random.traceon.util.SetSlotCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * trace items can't be used to crafting, repairing ...
 */

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "canInsert",at = @At("HEAD"),cancellable = true)
    private void preventInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info){
        if(stack.getOrCreateNbt().contains("TraceItem")){
            if (!(((Slot)(Object)this).inventory instanceof PlayerInventory)) {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "setStack",at = @At("HEAD"), cancellable = true)
    private void onSetStack(ItemStack stack, CallbackInfo info){
        ActionResult result = SetSlotCallback.EVENT.invoker().setSlotStack(stack);

        if (result == ActionResult.FAIL){
            info.cancel();
        }
    }

}
