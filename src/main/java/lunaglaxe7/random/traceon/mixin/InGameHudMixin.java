package lunaglaxe7.random.traceon.mixin;

import lunaglaxe7.random.traceon.client.QuickTraceRender;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render",at = @At(value = "TAIL"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo info){
        QuickTraceRender.instance.render(matrices);
    }
}
