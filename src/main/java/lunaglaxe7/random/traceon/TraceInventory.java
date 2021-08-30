package lunaglaxe7.random.traceon;

import lunaglaxe7.random.traceon.util.TraceKnowledgeUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;

public class TraceInventory implements Inventory {

    public Trace.TraceKnowledge traceKnowledge;
    private final PlayerEntity owner;

    public static final int TRACE_HOT_BAR_SIZE = 9;

    public TraceInventory(){
        owner = null;
        this.traceKnowledge = new Trace.TraceKnowledge();
    }

    public TraceInventory(int num){
        owner = null;
        this.traceKnowledge = new Trace.TraceKnowledge(num);
    }

    public TraceInventory(PlayerEntity player){
        owner = player;
        this.traceKnowledge = TraceKnowledgeUtil.getTraceKnowledge(player);
    }

    public TraceInventory(PlayerEntity player, NbtList nbtList){
        owner = player;
        this.traceKnowledge = TraceKnowledgeUtil.readFromNbt(nbtList);
    }


    /**
     * To help the quick trace.
     */
    public Trace.TraceKnowledge getHotBar(){
        return traceKnowledge.subList(0,TRACE_HOT_BAR_SIZE);
    }

    public TraceInventory updateKnowledge(){
        this.traceKnowledge = TraceKnowledgeUtil.getTraceKnowledge(owner);
        return this;
    }

    public ItemStack get(int index){
        return this.traceKnowledge.get(index);
    }


    @Override
    public void setStack(int slot, ItemStack stack) {
    }

    @Override
    public int size() {
        return traceKnowledge.size();
    }

    @Override
    public boolean isEmpty() {
        return traceKnowledge.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return traceKnowledge.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
    }

    @Override
    public void markDirty() {
    }
}
