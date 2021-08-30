package lunaglaxe7.random.traceon.mixin;

import lunaglaxe7.random.traceon.config.TraceOnConfig;
import lunaglaxe7.random.traceon.util.TraceItemTimer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/Entity;tick()V",shift = At.Shift.AFTER))
    private void onTick(CallbackInfo info){
        if (TraceOnConfig.enableTraceItemLife){
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            try {
                ItemStack stack = itemEntity.getStack();
                if (stack.getOrCreateNbt().contains("TraceItem")) {
                    String id = stack.getNbt().getString("TraceItem");
                    Integer timer = TraceItemTimer.timerManager.get(id);
                    if (timer != null) {
                        if (timer == 0) {
                            itemEntity.discard();
                            TraceItemTimer.remove(id, itemEntity);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Inject(method = "writeCustomDataToNbt",at = @At("HEAD"))
    private void writeToNbt(NbtCompound nbt,CallbackInfo info){
        if (TraceOnConfig.enableTraceItemLife){
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            ItemStack item = itemEntity.getStack();
            if (item.getOrCreateNbt().contains("TraceItem")) {
                String uuid = item.getNbt().getString("TraceItem");
                Integer life = TraceItemTimer.timerManager.get(uuid);
                if (life != null) {
                    item.getNbt().putInt("TraceItemLife", life);
                }
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    private void readFromNbt(NbtCompound nbt,CallbackInfo info){
        if (TraceOnConfig.enableTraceItemLife){
            ItemEntity itemEntity = (ItemEntity) (Object) this;
            ItemStack item = itemEntity.getStack();
            if (item.getOrCreateNbt().contains("TraceItem")) {
                String uuid = item.getNbt().getString("TraceItem");
                int life = item.getNbt().getInt("TraceItemLife");
                if (uuid != null) {
                    if (life == 0) {
                        itemEntity.discard();
                    } else {
                        TraceItemTimer.put(item, uuid, life);
                    }
                }
            }
        }
    }
}
