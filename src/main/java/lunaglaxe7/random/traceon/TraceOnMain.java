package lunaglaxe7.random.traceon;

import lunaglaxe7.random.traceon.config.TraceOnConfig;
import lunaglaxe7.random.traceon.network.TraceNetWork;
import lunaglaxe7.random.traceon.screen.TraceInvScreenHandler;
import lunaglaxe7.random.traceon.util.TraceEventRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class TraceOnMain implements ModInitializer {

    public static final String MODID = "traceon";
    public static final Identifier SCREEN_HANDLER = new Identifier(TraceOnMain.MODID,"traceinvscreenhandler");
    public static final ScreenHandlerType<TraceInvScreenHandler> TRACE_INV_SCREEN_HANDLER;

    static {
        TRACE_INV_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(SCREEN_HANDLER, TraceInvScreenHandler::new);
    }

    @Override
    public void onInitialize() {
        try {
            TraceOnConfig.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TraceNetWork.registerReceiver();
        TraceEventRegistry.registryServer();
    }
}
