package lunaglaxe7.random.traceon.client;

import lunaglaxe7.random.traceon.TraceOnMain;
import lunaglaxe7.random.traceon.client.keybinding.TraceKey;
import lunaglaxe7.random.traceon.client.screen.TraceInvScreen;
import lunaglaxe7.random.traceon.network.TraceNetWork;
import lunaglaxe7.random.traceon.util.TraceEventRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class TraceOnClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TraceKey.keyBinding();
        TraceEventRegistry.registryClient();
        TraceNetWork.registerReceiverClient();
        ScreenRegistry.register(TraceOnMain.TRACE_INV_SCREEN_HANDLER, TraceInvScreen::new);

    }
}
