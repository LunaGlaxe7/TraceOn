package lunaglaxe7.random.traceon.client.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class TraceSlot extends Slot {

    public TraceSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }


    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return false;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onQuickTransfer(ItemStack newItem, ItemStack original) {
    }
}
