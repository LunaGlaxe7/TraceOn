package lunaglaxe7.random.traceon.util;

import lunaglaxe7.random.traceon.client.screen.TraceInvScreen;
import lunaglaxe7.random.traceon.config.TraceOnConfig;
import lunaglaxe7.random.traceon.network.TraceNetWork;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import static lunaglaxe7.random.traceon.client.keybinding.TraceKey.quickTraceKey;
import static lunaglaxe7.random.traceon.client.keybinding.TraceKey.traceKey;

public class TraceEventRegistry {

    public static boolean quickTraceActive = false;
    public static boolean quickTraceKeyPressed = false;
    public static boolean quickTraceLock = false;
    public static long lastPressedTime = 0L;

    public static void registryClient(){
        //try to open the screen of TraceKnowledge
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            if (traceKey.isPressed()) {
                if (!(client.currentScreen instanceof TraceInvScreen))
                    ClientPlayNetworking.send(TraceNetWork.traceKeyChannel, PacketByteBufs.empty());
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(quickTraceKey.isPressed()){
                if (client.mouse.isCursorLocked()){
                    PlayerEntity player = client.player;

                    if(player != null){
                        if (!quickTraceKeyPressed) {
                            lastPressedTime = System.currentTimeMillis();
                            quickTraceLock = false;
                        }

                        if (!quickTraceLock) {
                            quickTraceActive = true;
                        }
                    }

                    quickTraceKeyPressed = true;
                }
            }else{
                quickTraceActive = false;
                if (quickTraceKeyPressed){
                    lastPressedTime = System.currentTimeMillis();
                }
                quickTraceKeyPressed = false;
            }
        });


    }

    public static void registryServer() {

        if (TraceOnConfig.enableTraceItemLife) {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                server.execute(TraceItemTimer::timer);
            });

            SetSlotCallback.EVENT.register(stack->{
                if (stack.getCount() > 0) {
                    if (stack.getOrCreateNbt().contains("TraceItem")) {
                        String id = stack.getNbt().getString("TraceItem");
                        Integer integer = TraceItemTimer.timerManager.get(id);
                        if (integer != null) {
                            stack.getNbt().putInt("TraceItemLife", integer);
                        } else {
                            integer = stack.getNbt().getInt("TraceItemLife");
                            TraceItemTimer.put(stack, id, integer);
                        }
                    }
                }
                return ActionResult.PASS;
            });
        }

    }
}
