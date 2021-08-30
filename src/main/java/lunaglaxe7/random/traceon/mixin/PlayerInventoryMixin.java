package lunaglaxe7.random.traceon.mixin;

import lunaglaxe7.random.traceon.util.TraceItemTimer;
import lunaglaxe7.random.traceon.util.TraceKnowledgeUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *  Update trace knowledge from Inventory.
 *  And update trace item life if enabled.
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow
    @Final
    PlayerEntity player;

    @Inject(method = "updateItems",at = @At("TAIL"))
    private void updateInventory(CallbackInfo info){
        if(TraceKnowledgeUtil.updateTraceFromInventory(player)){
            player.sendMessage(new LiteralText("Trace knowledge has been updated!"), true);
        }

        TraceItemTimer.updateInventory(player);

    }

}
