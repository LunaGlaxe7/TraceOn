package lunaglaxe7.random.traceon.util;

import lunaglaxe7.random.traceon.Trace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;

public class TraceKnowledgeUtil {

    public static final Map<PlayerEntity, Trace.TraceKnowledge> traceKnowledge = new HashMap<>();

    public static Trace.TraceKnowledge getTraceKnowledge(PlayerEntity player){

        if (traceKnowledge.get(player) == null){
            traceKnowledge.put(player,new Trace.TraceKnowledge());
        }
        return traceKnowledge.get(player);
    }

    public static NbtList writeToNbt(PlayerEntity player, NbtList nbtList){
        NbtCompound knowledgeNbt;
        ItemStack knowledgeStack;

        Trace.TraceKnowledge traceKnowledge = getTraceKnowledge(player);
        for (int i = 0; i < traceKnowledge.size();i++){
            knowledgeNbt = new NbtCompound();
            knowledgeStack = traceKnowledge.get(i);
            if (knowledgeStack.getItem() instanceof SwordItem){
                knowledgeNbt.putByte("Trace",(byte)i);
                knowledgeStack.writeNbt(knowledgeNbt);
            }

            nbtList.add(knowledgeNbt);
        }

        return nbtList;
    }
    public static NbtList tryWriteToNbt(Trace.TraceKnowledge knowledge,NbtList nbtList){
        if (knowledge.size() == 0){
            return null;
        }
        return writeToNbt(knowledge, nbtList);
    }

    private static NbtList writeToNbt(Trace.TraceKnowledge knowledge,NbtList nbtList){

        NbtCompound knowledgeNbt;
        ItemStack knowledgeStack;

        for (int i = 0; i < knowledge.size();i++){
            knowledgeNbt = new NbtCompound();
            knowledgeStack = knowledge.get(i);
            if (knowledgeStack.getItem() instanceof SwordItem){
                knowledgeNbt.putByte("Trace",(byte)i);
                knowledgeStack.writeNbt(knowledgeNbt);
            }

            nbtList.add(knowledgeNbt);
        }

        return nbtList;
    }

    public static void updateFromNbt(PlayerEntity player, NbtList nbtList){
        traceKnowledge.remove(player);

        Trace.TraceKnowledge knowledge = readFromNbt(nbtList);

        traceKnowledge.put(player,knowledge);
    }

    public static Trace.TraceKnowledge readFromNbt(NbtList nbtList){
        Trace.TraceKnowledge knowledge = new Trace.TraceKnowledge();

        for (int i = 0; i < nbtList.size(); i++){
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Trace") & 255;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);

            if (j == i){
                knowledge.add(itemStack);
            }else {
                knowledge.add(j,itemStack);
            }
        }

        return knowledge;
    }

    public static boolean updateTraceKnowledge(PlayerEntity player){
        return updateTraceFromInventory(player);
    }

    public static boolean updateTraceFromInventory(PlayerEntity player){
        Trace.TraceKnowledge knowledgeItems = getTraceKnowledge(player);

        if (knowledgeItems.size() >= 63){
            return false;
        }


        Trace.TraceKnowledge toUpdate = new Trace.TraceKnowledge();
        for (int i = 0; i < player.getInventory().size();i++){
            ItemStack item = player.getInventory().getStack(i);

            if (item.getCount() > 0){
                if (item.getItem() instanceof SwordItem) {
                    item = item.copy();
                    toUpdate.add(item);
                }
            }
        }

        return knowledgeItems.updateKnowledge(toUpdate);
    }

    public static enum UpdateType{
        TRACE,MAKE
    }
}
