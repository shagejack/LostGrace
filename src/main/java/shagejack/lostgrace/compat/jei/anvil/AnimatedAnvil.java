package shagejack.lostgrace.compat.jei.anvil;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.gui.TickTimer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnimatedAnvil implements IDrawable {

    private final IDrawable anvil;
    private final ITickTimer tickTimer;

    public AnimatedAnvil(IGuiHelper guiHelper) {
        this.anvil = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.ANVIL));
        this.tickTimer = new TickTimer(30, 80, false);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void draw(PoseStack poseStack, int xOffset, int yOffset) {
        poseStack.pushPose();
        poseStack.translate(xOffset, yOffset + Math.min(32, tickTimer.getValue()), 0);
        poseStack.scale(2.0f, 2.0f, 2.0f);
        anvil.draw(poseStack);
        poseStack.popPose();
    }
}
