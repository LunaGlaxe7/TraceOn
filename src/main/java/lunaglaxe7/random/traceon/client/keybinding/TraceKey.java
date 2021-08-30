package lunaglaxe7.random.traceon.client.keybinding;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TraceKey {

    public static KeyBinding traceKey = new KeyBinding("key.trace", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_T, KeyBinding.MISC_CATEGORY);
    public static KeyBinding quickTraceKey = new KeyBinding("key.quicktrace",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_TAB,KeyBinding.MISC_CATEGORY);

    public static void keyBinding(){
        KeyBindingHelper.registerKeyBinding(traceKey);
        KeyBindingHelper.registerKeyBinding(quickTraceKey);
    }
}
