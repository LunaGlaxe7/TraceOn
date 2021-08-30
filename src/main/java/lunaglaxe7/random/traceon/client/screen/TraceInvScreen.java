package lunaglaxe7.random.traceon.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import lunaglaxe7.random.traceon.TraceOnMain;
import lunaglaxe7.random.traceon.screen.TraceInvScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TraceInvScreen extends HandledScreen<TraceInvScreenHandler> {

    public static final Identifier BACKGROUND = new Identifier(TraceOnMain.MODID,"textures/gui/traceinv.png");

    private Slot selectedSlot;

    private final List<ButtonWidget> buttons = new ArrayList<>();


    public TraceInvScreen(TraceInvScreenHandler handler, Inventory inventory, Text title) {
        super(handler, (PlayerInventory) inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.y /= 2;
        this.backgroundHeight = 171;

        buttons.clear();
        buttons.add(new ButtonWidget(this.x + this.backgroundWidth/2 - 20,this.y + 150,40,16,new TranslatableText("traceon.button.trace"),
                (button)->{
            Slot slot = this.selectedSlot;
            if (slot != null) {
                this.handler.traceOperation(this.selectedSlot);
                this.onClose();
            }

                }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        int i = this.x;
        int j = this.y;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0,BACKGROUND);

        this.drawTexture(matrices,this.x,this.y,0,0,this.backgroundWidth,this.backgroundHeight);

        RenderSystem.disableDepthTest();
        super.render(matrices, mouseX, mouseY, delta);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate((double)i, (double)j, 0.0D);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.focusedSlot = null;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int p;
        int q;
        for(int k = 0; k < this.handler.slots.size(); ++k) {
            Slot slot = this.handler.slots.get(k);
            if (slot.isEnabled()) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                this.drawSlot(matrices, slot);
            }

            if (this.isPointOverSlot(slot, (double)mouseX, (double)mouseY) && slot.isEnabled()) {
                this.focusedSlot = slot;
                p = slot.x;
                q = slot.y;
                drawSlotHighlight(matrices, p, q, this.getZOffset());
            }

            if (selectedSlot == slot){
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1,1,1,1);
                RenderSystem.setShaderTexture(0,BACKGROUND);

                this.drawTexture(matrices,slot.x -3 ,slot.y - 3,0,178,22,22);
            }

        }

        this.drawMouseoverTooltip(matrices,mouseX - 7 * 18,mouseY - 2 * 18);

        this.drawForeground(matrices, mouseX, mouseY);

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();

        Iterator<ButtonWidget> it = buttons.iterator();
        while (it.hasNext()){
            it.next().render(matrices, mouseX, mouseY, delta);
        }


    }

    public boolean getClicked(double mouseX, double mouseY, int button){
        Iterator it = this.buttons.iterator();

        Element element;
        do {
            if (!it.hasNext()) {
                return false;
            }

            element = (Element)it.next();
        } while(!element.mouseClicked(mouseX, mouseY, button));

        this.setFocused(element);
        return true;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (getClicked(mouseX,mouseY,button)){
            return true;
        }

        if (this.focusedSlot != null){
            return selectSlot(this.focusedSlot);
        }
        return false;

    }

    private boolean selectSlot(Slot slot){

        if (this.selectedSlot == null ){
            selectedSlot = slot;
        }else{
            this.handler.swapKnowledgeIndex(selectedSlot, slot);
            selectedSlot = null;
        }
        return true;

    }

    public boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }



    public void drawSlot(MatrixStack matrices, Slot slot){

        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getStack();

        boolean bl = false;
        this.setZOffset(100);
        this.itemRenderer.zOffset = 100.0F;

        //if (itemStack.isEmpty() && slot.isEnabled()) {
        Pair<Identifier, Identifier> pair = slot.getBackgroundSprite();
        if (pair != null) {
            Sprite sprite = this.client.getSpriteAtlas(pair.getFirst()).apply(pair.getSecond());
            RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
            drawSprite(matrices, i, j, this.getZOffset(), 16, 16, sprite);
        }
        //}


            if (bl) {
                fill(matrices, i, j, i + 16, j + 16, -2130706433);
            }

            RenderSystem.enableDepthTest();
            this.itemRenderer.renderInGuiWithOverrides(this.client.player, itemStack, i, j, slot.x + slot.y * this.backgroundWidth);


        this.itemRenderer.zOffset = 0.0F;
        this.setZOffset(0);

    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)(this.titleY ), 4210752);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /*protected TraceButton getButtonSelected(){
        for (TraceButton button : buttons){
            if (button.isSelected()){
                return button;
            }
        }
        return null;
    }*/

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    }

}
