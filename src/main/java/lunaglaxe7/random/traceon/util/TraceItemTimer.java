package lunaglaxe7.random.traceon.util;

import lunaglaxe7.random.traceon.config.TraceOnConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TraceItemTimer {

    public static final Map<String,Integer> timerManager = new HashMap<>();

    public static void timer(){
        timerManager.forEach((id,integer)->{
            int timer = --integer;
            if(timer < 0){
                timer = 0;
            }
            timerManager.replace(id,timer);
        });
    }

    public static void remove(String id, Entity operator){
        if (!operator.world.isClient()){
            timerManager.remove(id);
        }
    }

    public static void put(ItemStack toPut,String id,Integer putInteger){
        if (toPut.getCount() > 0){
            timerManager.put(id,putInteger);
        }
    }


    public static void updateInventory(PlayerEntity player){

        if (TraceOnConfig.enableTraceItemLife){
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack item = player.getInventory().getStack(i);
                if (item.getCount() > 0){
                    if (item.getOrCreateNbt().contains("TraceItem")) {
                        String id = item.getNbt().getString("TraceItem");
                        Integer life = timerManager.get(id);
                        if (life != null) {
                            if (life == 0) {
                                item.decrement(1);
                                item.setNbt(null);
                                item.setDamage(0);
                                remove(id, player);
                            }
                        } else {
                            try {
                                int remainTime = item.getNbt().getInt("TraceItemLife");

                                put(item,id, remainTime);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }


}
