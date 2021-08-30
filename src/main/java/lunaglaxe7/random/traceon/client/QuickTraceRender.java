package lunaglaxe7.random.traceon.client;

import com.mojang.blaze3d.systems.RenderSystem;
import lunaglaxe7.random.traceon.Trace;
import lunaglaxe7.random.traceon.TraceOnMain;
import lunaglaxe7.random.traceon.config.TraceOnConfig;
import lunaglaxe7.random.traceon.network.TraceNetWork;
import lunaglaxe7.random.traceon.util.TraceEventRegistry;
import lunaglaxe7.random.traceon.util.TraceItemTimer;
import lunaglaxe7.random.traceon.util.TraceKnowledgeUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuickTraceRender extends DrawableHelper {


    public static final Identifier TEXTURE = new Identifier(TraceOnMain.MODID,"textures/gui/quicktrace.png");
    private static final int textureWidth = 180;
    private static final int textureHeight = 180;
    public static float tickRender = 0.0f;
    public static long lastTime = 0L;
    public static long time = 0L;
    private static float alphaMax = 0.3f;
    private static float alphaUnit = alphaMax / 20;
    public static boolean lastState = false;
    public static QuickTraceRender instance = new QuickTraceRender();
    public static int sideNum = 3;
    public static int numLimit = (sideNum-1) * 4;
    private double mouseX = 0.0;
    private double mouseY = 0.0;
    private List<Point> pointList = new ArrayList<>();

    public void render(MatrixStack matrices){

        if (TraceEventRegistry.quickTraceActive || tickRender > 0.0f) {
            MinecraftClient mc = MinecraftClient.getInstance();
            Trace.TraceKnowledge knowledge = TraceKnowledgeUtil.getTraceKnowledge(mc.player);


            if (!TraceEventRegistry.quickTraceActive){

                if (mc.currentScreen == null && lastState) {

                    if (!mc.mouse.isCursorLocked()) {
                        mc.mouse.lockCursor();
                    }

                    int index = -1;
                    for (int i = 0; i < pointList.size();i++){
                        Point point = pointList.get(i);
                        if (mouseX >= point.x && mouseX < point.x + 17 && mouseY >= point.y && mouseY < point.y + 17){
                            index = i;
                            break;
                        }
                    }

                    if (index >= 0){
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(index);
                        ClientPlayNetworking.send(TraceNetWork.traceOnChannel, buf);
                    }

                    lastState = false;
                }
            }else {
                if (mc.currentScreen != null){
                    TraceEventRegistry.quickTraceActive = false;
                    TraceEventRegistry.quickTraceLock = true;
                    mc.mouse.lockCursor();
                    mc.mouse.unlockCursor();
                    return;
                }

                if (knowledge.size() == 0)
                    return;

                if (mc.mouse.isCursorLocked()){
                    mc.mouse.unlockCursor();
                }
            }


            Window window = mc.getWindow();
            time = System.currentTimeMillis();
            int x = (window.getScaledWidth() - textureWidth) / 2;
            int y = (window.getScaledHeight() - textureHeight) / 2;


            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1, 1, 1, tickRender);
            this.drawTexture(matrices, x, y, 0, 0, textureWidth, textureHeight);

            ItemRenderer it = mc.getItemRenderer();
            ItemStack item;
            for (int i = 0; i < Math.min(knowledge.size(),numLimit);i++){
                item = knowledge.get(i);
                it.renderInGuiWithOverrides(item,x + 64 + ((i + i / 4) % sideNum) * 18, y + 64 + ((i + i / 4) / sideNum) * 18);
                pointList.add(new Point(x + 64 + ((i + i / 4) % sideNum) * 18, y + 64 + ((i + i / 4) / sideNum) * 18));
            }

            mouseX = mc.mouse.getX() / window.getScaleFactor();
            mouseY = mc.mouse.getY() / window.getScaleFactor();

            if (time > lastTime){


                if (!TraceEventRegistry.quickTraceActive){
                    tickRender -= alphaUnit;
                }else if(tickRender < alphaMax){
                    tickRender += alphaUnit;
                }

                if (tickRender > alphaMax){
                    tickRender = alphaMax;
                }
                if (tickRender < 0.0f){
                    tickRender = 0.0f;
                    TraceEventRegistry.quickTraceLock = true;
                }


                lastTime = time + 5L;
                lastState = TraceEventRegistry.quickTraceActive;

            }
        }

        if (TraceOnConfig.enableTraceItemLife){
            this.renderTraceLifeHud(matrices);
        }

    }

    public void renderTraceLifeHud(MatrixStack matrices) {

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack item = player.getInventory().getStack(i);
                if (!item.isEmpty() && item.getOrCreateNbt().contains("TraceItem")) {
                    list.add(item);
                }
            }

            int size = list.size();
            if (size > 0) {
                Window window = MinecraftClient.getInstance().getWindow();
                ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                fill(matrices, 0, window.getScaledHeight() - 17 * size, 16, window.getScaledHeight(), 0x7a000000);

                for (int i = 0;i < size;i++){
                    ItemStack item = list.get(i);
                    String id = item.getNbt().getString("TraceItem");
                    if (id != null) {
                        Integer timer = TraceItemTimer.timerManager.get(id);
                        if (timer == null) {
                            timer = item.getNbt().getInt("TraceItemLife");
                            TraceItemTimer.put(item,id,timer);
                        }
                        int maxLife = item.getNbt().getInt("TraceItemMaxLife");
                        itemRenderer.renderInGui(item, 0, window.getScaledHeight() - 17 * (i + 1));

                        fill(matrices, 0, window.getScaledHeight() - 2 - 17 * i, (int) (16 * (timer / (double) maxLife)), window.getScaledHeight() - 17 * i, 0xff0000ff);

                    }
                }

            }

        }
    }
}
