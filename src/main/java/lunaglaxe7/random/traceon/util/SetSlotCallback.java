package lunaglaxe7.random.traceon.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

/**
 * Called when open a gui with inventory (except for player inventory),
 * But this can't modify data in the source. Only modify data has been sent here.
 * May be useless...
 */
public interface SetSlotCallback {
    Event<SetSlotCallback> EVENT =
            EventFactory.createArrayBacked(SetSlotCallback.class,(listeners)->
                    (stack)->{
                for (SetSlotCallback listener : listeners){
                    ActionResult result = listener.setSlotStack(stack);

                    if (result != ActionResult.PASS){
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult setSlotStack(ItemStack itemStack);
}
