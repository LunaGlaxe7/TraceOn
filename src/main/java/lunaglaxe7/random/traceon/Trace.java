package lunaglaxe7.random.traceon;

import lunaglaxe7.random.traceon.config.TraceOnConfig;
import lunaglaxe7.random.traceon.util.TraceEnchantUtil;
import lunaglaxe7.random.traceon.util.TraceItemTimer;
import lunaglaxe7.random.traceon.util.TraceKnowledgeUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trace {

    public static ItemStack trace(PlayerEntity player, int index){

        ItemStack traceItem = ItemStack.EMPTY;
        TraceKnowledge knowledge;
        try {
            knowledge = TraceKnowledgeUtil.getTraceKnowledge(player);

            traceItem = buildTraceItem(knowledge,index, TraceOnConfig.TRACE_ITEM_LIFE_BASE);

            player.giveItemStack(traceItem);

            knowledge.updateProficient(index, TraceKnowledgeUtil.UpdateType.TRACE);
            TraceKnowledgeUtil.traceKnowledge.put(player,knowledge);
        }catch (Exception e){
            e.printStackTrace();
        }

        return traceItem;
    }

    public static ItemStack buildTraceItem(TraceKnowledge knowledge,int index,int itemLife){
        ItemStack traceItem = knowledge.get(index);
        if (traceItem == null){
            return ItemStack.EMPTY;
        }
        traceItem = traceItem.copy();

        if (TraceOnConfig.enableTraceItemLife) {

            String id = UUID.randomUUID().toString();
            traceItem.getOrCreateNbt().putString("TraceItem", id);
            traceItem.getNbt().putInt("TraceItemLife", itemLife);
            traceItem.getNbt().putInt("TraceItemMaxLife",itemLife);
            TraceItemTimer.put(traceItem,id, itemLife);

        }
        if (TraceOnConfig.banMending) {
            if (EnchantmentHelper.getLevel(Enchantments.MENDING, traceItem) > 0) {
                TraceEnchantUtil.removeEnchant(Enchantments.MENDING,traceItem);
            }
        }

        return traceItem;
    }

    /*private static void checkKnowledge(TraceKnowledge toCheck,boolean doRepair){
        for (int i = 0; i < toCheck.size();i++){
            ItemStack itemToCheck = toCheck.get(i);
            if (toCheck.subList(i+1,toCheck.size()).contains(itemToCheck)){
                if (doRepair){

                }
            }
        }
    }*/


    public static class TraceKnowledge extends ArrayList<ItemStack>{

        public static int proficientNumTrace = 5;
        public static int proficientNumMake = 100;

        public TraceKnowledge() {
            this(0);
        }

        public TraceKnowledge(int maxNum){
            super(maxNum);
            for (int i = 0; i < maxNum; i ++){
                this.add(ItemStack.EMPTY);
            }
        }

        public TraceKnowledge(List<ItemStack> knowledgeList){
            this.copyOf(knowledgeList);
        }

        /**
         * Method used only in trace inventory screen.
         */
        public TraceKnowledge swap(int left,int right){

            ItemStack temp = this.get(left);
            this.set(left,this.get(right));
            this.set(right,temp);

            return this;
        }


        /**
         * Return a sub TraceKnowledge depending on index you give. If @param{toIndex} is too big,
         * it will deal with that automatically.
         */
        @Override
        public TraceKnowledge subList(int fromIndex, int toIndex) {
            if (toIndex > this.size()){toIndex = this.size();}
            List<ItemStack> subList = super.subList(fromIndex,toIndex);
            return new TraceKnowledge().copyOf(subList);
        }

        public void updateProficient(int index, TraceKnowledgeUtil.UpdateType type){
            switch (type){
                case TRACE -> {
                    this.updateProficientNum(index,proficientNumTrace);
                }
                case MAKE -> {
                    this.updateProficientNum(index,proficientNumMake);
                }
            }
        }

        private void updateProficientNum(int index,int num){
            ItemStack toUpdate = this.get(index);
            int updateNum = Math.max(toUpdate.getDamage()-num, 1);
            toUpdate.setDamage(updateNum);

        }

        public boolean updateKnowledge(TraceKnowledge toUpdate){
            boolean updateTag = false;

            for (ItemStack knowledgeToUpdate : toUpdate){
                if (TraceOnConfig.banMending) {
                    if (EnchantmentHelper.getLevel(Enchantments.MENDING, knowledgeToUpdate) > 0) {
                        TraceEnchantUtil.removeEnchant(Enchantments.MENDING,knowledgeToUpdate);
                    }
                }

                if(knowledgeToUpdate.getOrCreateNbt().contains("TraceItem")){
                    knowledgeToUpdate.getNbt().remove("TraceItem");
                    if (knowledgeToUpdate.getOrCreateNbt().contains("TraceItemLife")){
                        knowledgeToUpdate.getNbt().remove("TraceItemLife");
                    }
                    if (knowledgeToUpdate.getNbt().contains("TraceItemMaxLife")){
                        knowledgeToUpdate.getNbt().remove("TraceItemMaxLife");
                    }
                }

                if (!this.contains(knowledgeToUpdate)){
                    updateTag = true;
                    knowledgeToUpdate = knowledgeToUpdate.copy();
                    knowledgeToUpdate.setDamage(knowledgeToUpdate.getMaxDamage()-5);
                    this.add(knowledgeToUpdate);
                }
            }

            return updateTag;
        }


        public boolean contains(ItemStack itemStack) {
            return indexOfRange(itemStack,0,this.size()) >= 0;
        }

        /**
         * @return return a TraceKnowledge only contains knowledge from the parameter.
         */
        public TraceKnowledge copyOf(List<ItemStack> knowledgeList){
            this.clear();
            for (ItemStack toAdd : knowledgeList){
                if (!this.contains(toAdd)){
                    this.add(toAdd);
                }
            }
            return this;
        }

        @Override
        public void clear() {
            super.clear();
        }

        int indexOfRange(ItemStack itemStack, int start, int end){
            if (itemStack != null) {
                for (int i = start; i < end; i++) {
                    if (areEqualIgnoreDamage(itemStack,this.get(i))) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * Compare two ItemStack ignore the damage.
         * a and b must be damageable.
         */
        private static boolean areEqualIgnoreDamage(ItemStack a,ItemStack b){
            ItemStack left = a.copy(); ItemStack right = b.copy();
            left.setDamage(0); right.setDamage(0);
            return ItemStack.areEqual(left,right);
        }

    }

}
