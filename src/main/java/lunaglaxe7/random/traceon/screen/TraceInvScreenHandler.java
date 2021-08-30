package lunaglaxe7.random.traceon.screen;

import lunaglaxe7.random.traceon.TraceInventory;
import lunaglaxe7.random.traceon.client.screen.TraceSlot;
import lunaglaxe7.random.traceon.network.TraceNetWork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static lunaglaxe7.random.traceon.TraceOnMain.TRACE_INV_SCREEN_HANDLER;

public class TraceInvScreenHandler extends ScreenHandler {

    private final PlayerEntity owner;
    public int knowledgeSize;
    public final TraceInventory traceInventory;
    //private int page;
    //private int maxPage;

    public TraceInvScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId,playerInventory,new TraceInventory(TraceNetWork.knowledgeSize));
    }

    public TraceInvScreenHandler(int syncId, PlayerInventory playerInventory,Inventory inventory) {
        super(TRACE_INV_SCREEN_HANDLER, syncId);

        this.owner = playerInventory.player;
        traceInventory = new TraceInventory(owner);
        knowledgeSize = inventory.size();


        //maxPage = knowledgeSize / 63 + 1;
        //page = 1;
        int i = knowledgeSize % 9;
        int j = knowledgeSize / 9;
        int k,l;
        for (k = 0; k < j + 1;k++){
            for (l = 0; l < (k==j ? i : 9);l++){
                this.addSlot(new TraceSlot(traceInventory, l + k * 9, 8 + l * 18, 25 + k * 18));
            }
        }


    }

    public void traceOperation(Slot slot){
        if (slot != null && slot.getStack().getCount() > 0) {
            int index = this.slots.indexOf(slot);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(index);
            ClientPlayNetworking.send(TraceNetWork.traceOnChannel,buf);
        }
    }

    @Override
    public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
        super.updateSlotStacks(revision, stacks, cursorStack);
    }

    public void swapKnowledgeIndex(Slot slot1, Slot slot2){

        int index1 = this.slots.indexOf(slot1);
        int index2 = this.slots.indexOf(slot2);
        this.traceInventory.traceKnowledge.swap(index1,index2);

    }



    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public static class TraceInvScreenHandlerFactory implements NamedScreenHandlerFactory{

        @Override
        public Text getDisplayName() {
            return new TranslatableText("traceon.inv.title");
        }

        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new TraceInvScreenHandler(syncId,inv,new TraceInventory(player));
        }
    }

    /*public static class TraceSyncReceiver implements ClientPlayNetworking.PlayChannelHandler{
        public static final TraceSyncReceiver traceSyncReceiver = new TraceSyncReceiver();
        public NbtCompound nbtRead;
        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            nbtRead = buf.readNbt();
        }
    }*/


    @Override
    public void setPreviousCursorStack(ItemStack stack) {
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return false;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return false;
    }

    @Override
    public void disableSyncing() {
    }

}
