package lunaglaxe7.random.traceon.mixin;

import lunaglaxe7.random.traceon.config.TraceOnConfig;
import lunaglaxe7.random.traceon.util.TraceItemTimer;
import lunaglaxe7.random.traceon.util.TraceKnowledgeUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sync trace knowledge data with nbt.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    private void readKnowledgeFromNBT(NbtCompound nbt,CallbackInfo info){
        PlayerEntity self = (PlayerEntity)(Object) this;
        if (nbt.contains("TraceKnowledge"))
            TraceKnowledgeUtil.updateFromNbt(self, nbt.getList("TraceKnowledge", NbtElement.COMPOUND_TYPE));



        if (TraceOnConfig.enableTraceItemLife) {
            for (int i = 0; i < self.getInventory().size(); i++) {
                ItemStack item = self.getInventory().getStack(i);
                try {
                    if (item.getOrCreateNbt().contains("TraceItem")) {
                        String id = item.getNbt().getString("TraceItem");
                        if (id != null) {
                            int timer = item.getNbt().getInt("TraceItemLife");
                            TraceItemTimer.put(item,id,timer);
                        }
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        }

    }

    @Inject(method = "writeCustomDataToNbt",at = @At("HEAD"))
    private void writeKnowledgeToNBT(NbtCompound nbt,CallbackInfo info){
        PlayerEntity self = (PlayerEntity)(Object) this;
        nbt.put("TraceKnowledge", TraceKnowledgeUtil.writeToNbt(self, new NbtList()));


        if (TraceOnConfig.enableTraceItemLife) {
            for (int i = 0; i < self.getInventory().size(); i++) {
                ItemStack item = self.getInventory().getStack(i);
                try {
                    if (item.getOrCreateNbt().contains("TraceItem")) {
                        String id = item.getNbt().getString("TraceItem");
                        if (id != null) {
                            int timer = TraceItemTimer.timerManager.get(id);
                            item.getNbt().putInt("TraceItemLife", timer);
                        }
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    }

}
