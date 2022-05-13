package shagejack.lostgrace.foundation.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import shagejack.lostgrace.LostGrace;

import java.util.Locale;

public enum Blending {

    DEFAULT(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    ALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.SRC_ALPHA),
    PREALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    MULTIPLY(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    ADDITIVE(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE),
    ADDITIVEDARK(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR),
    OVERLAYDARK(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE),
    ADDITIVE_ALPHA(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE),
    CONSTANT_ALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA),
    INVERTEDADD(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR)

    ;

    private final GlStateManager.SourceFactor colorSrcFactor, alphaSrcFactor;
    private final GlStateManager.DestFactor colorDstFactor, alphaDstFactor;

    Blending(GlStateManager.SourceFactor src, GlStateManager.DestFactor dst) {
        this(src, dst, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    Blending(GlStateManager.SourceFactor src, GlStateManager.DestFactor dst, GlStateManager.SourceFactor srcAlpha, GlStateManager.DestFactor dstAlpha) {
        this.colorSrcFactor = src;
        this.colorDstFactor = dst;
        this.alphaSrcFactor = srcAlpha;
        this.alphaDstFactor = dstAlpha;
    }

    public void apply() {
        RenderSystem.blendFuncSeparate(this.colorSrcFactor, this.colorDstFactor, this.alphaSrcFactor, this.alphaDstFactor);
    }

    public RenderStateShard.TransparencyStateShard asState() {
        return new RenderStateShard.TransparencyStateShard(LostGrace.asResource("blending_" + this.name().toLowerCase(Locale.ROOT)).toString(), () -> {
            RenderSystem.enableBlend();
            this.apply();
        }, () -> {
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        });
    }


}
