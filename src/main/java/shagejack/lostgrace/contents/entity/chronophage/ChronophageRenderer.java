package shagejack.lostgrace.contents.entity.chronophage;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import shagejack.lostgrace.foundation.render.DrawUtils;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ChronophageRenderer extends EntityRenderer<Chronophage> {

    public ChronophageRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    public void render(Chronophage entity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        double w = entity.getW();
        double radius3D = entity.getRadiusIn3D();
        Color color = entity.getColor();

        if (radius3D > 0) {
            DrawUtils.renderSphere(pMatrixStack, radius3D, color, 255);
            super.render(entity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Chronophage entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
