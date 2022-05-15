package shagejack.lostgrace.foundation.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.function.UnaryOperator;

public class RenderTypeLG extends RenderType {

    // Dummy
    public RenderTypeLG(String name, VertexFormat vertexFormat, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, vertexFormat, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static final RenderType GRACE = RenderTypeBuilder.builder()
            .name("grace")
            .vertexFormat(DefaultVertexFormat.BLOCK)
            .vertexFormatMode(VertexFormat.Mode.QUADS)
            .affectsCrumbling()
            .sortOnUpload()
            .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
            .setTextureState(BLOCK_SHEET_MIPPED)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(TRANSLUCENT_TARGET)
            .enableLightMap()
            .disableDepthTest()
            .build();

    public static final RenderType FOG_SPHERE = RenderTypeBuilder.builder()
            .name("fog_sphere")
            .vertexFormat(DefaultVertexFormat.POSITION_COLOR)
            .vertexFormatMode(VertexFormat.Mode.TRIANGLES)
            .affectsCrumbling()
            .sortOnUpload()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTextureState(NO_TEXTURE)
            .setTransparencyState(NO_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setOutputState(RenderStateShard.MAIN_TARGET)
            .setDepthTestState(NO_DEPTH_TEST)
            .setCullState(NO_CULL)
            .build();


    public static class RenderTypeBuilder {

        protected String name;
        protected VertexFormat vertexFormat;
        protected VertexFormat.Mode vertexFormatMode;
        protected int pBufferSize;
        protected boolean pAffectsCrumbling;
        protected boolean pSortOnUpload;
        protected boolean pOutline;

        protected RenderType.CompositeState.CompositeStateBuilder stateBuilder;

        private RenderTypeBuilder(RenderType.CompositeState.CompositeStateBuilder compositeStateBuilder) {
            this.stateBuilder = compositeStateBuilder;
            this.vertexFormat = DefaultVertexFormat.BLOCK;
            this.vertexFormatMode = VertexFormat.Mode.QUADS;
            this.pBufferSize = 32768;
            this.pAffectsCrumbling = false;
            this.pSortOnUpload = false;
            this.pOutline = true;
        }

        public static RenderTypeBuilder builder() {
            return new RenderTypeBuilder(RenderType.CompositeState.builder());
        }

        public RenderTypeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RenderTypeBuilder enableLightMap() {
            this.stateBuilder.setLightmapState(RenderStateShard.LIGHTMAP);
            return this;
        }

        public RenderTypeBuilder disableDepthTest() {
            this.stateBuilder.setDepthTestState(DepthTestStateShard.NO_DEPTH_TEST);
            return this;
        }

        public RenderTypeBuilder enableOverlay() {
            this.stateBuilder.setOverlayState(RenderStateShard.OVERLAY);
            return this;
        }

        public RenderTypeBuilder vertexFormat(VertexFormat vertexFormat) {
            this.vertexFormat = vertexFormat;
            return this;
        }

        public RenderTypeBuilder vertexFormatMode(VertexFormat.Mode vertexFormatMode) {
            this.vertexFormatMode = vertexFormatMode;
            return this;
        }

        public RenderTypeBuilder bufferSize(int pBufferSize) {
            this.pBufferSize = pBufferSize;
            return this;
        }

        public RenderTypeBuilder affectsCrumbling() {
            this.pAffectsCrumbling = true;
            return this;
        }

        public RenderTypeBuilder sortOnUpload() {
            this.pSortOnUpload = true;
            return this;
        }

        public RenderTypeBuilder noOutline() {
            this.pOutline = false;
            return this;
        }

        public RenderType build() {
            return RenderType.create(name, vertexFormat, vertexFormatMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, stateBuilder.createCompositeState(pOutline));
        }

        public RenderTypeBuilder setCompositeState(UnaryOperator<CompositeState.CompositeStateBuilder> factory) {
            this.stateBuilder = factory.apply(this.stateBuilder);
            return this;
        }

        public RenderTypeBuilder setTextureState(RenderStateShard.EmptyTextureStateShard textureState) {
            this.stateBuilder.setTextureState(textureState);
            return this;
        }

        public RenderTypeBuilder setShaderState(RenderStateShard.ShaderStateShard shaderState) {
            this.stateBuilder.setShaderState(shaderState);
            return this;
        }

        public RenderTypeBuilder setTransparencyState(RenderStateShard.TransparencyStateShard transparencyState) {
            this.stateBuilder.setTransparencyState(transparencyState);
            return this;
        }

        public RenderTypeBuilder setDepthTestState(RenderStateShard.DepthTestStateShard depthTestState) {
            this.stateBuilder.setDepthTestState(depthTestState);
            return this;
        }

        public RenderTypeBuilder setCullState(RenderStateShard.CullStateShard cullState) {
            this.stateBuilder.setCullState(cullState);
            return this;
        }

        public RenderTypeBuilder setLightmapState(RenderStateShard.LightmapStateShard lightmapState) {
            this.stateBuilder.setLightmapState(lightmapState);
            return this;
        }

        public RenderTypeBuilder setOverlayState(RenderStateShard.OverlayStateShard overlayState) {
            this.stateBuilder.setOverlayState(overlayState);
            return this;
        }

        public RenderTypeBuilder setLayeringState(RenderStateShard.LayeringStateShard layeringState) {
            this.stateBuilder.setLayeringState(layeringState);
            return this;
        }

        public RenderTypeBuilder setOutputState(RenderStateShard.OutputStateShard outputState) {
            this.stateBuilder.setOutputState(outputState);
            return this;
        }

        public RenderTypeBuilder setTexturingState(RenderStateShard.TexturingStateShard texturingState) {
            this.stateBuilder.setTexturingState(texturingState);
            return this;
        }

        public RenderTypeBuilder setWriteMaskState(RenderStateShard.WriteMaskStateShard writeMaskState) {
            this.stateBuilder.setWriteMaskState(writeMaskState);
            return this;
        }

        public RenderTypeBuilder setLineState(RenderStateShard.LineStateShard lineState) {
            this.stateBuilder.setLineState(lineState);
            return this;
        }

    }
}