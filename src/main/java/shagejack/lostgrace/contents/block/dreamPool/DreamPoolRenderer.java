package shagejack.lostgrace.contents.block.dreamPool;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.registries.ForgeRegistries;
import shagejack.lostgrace.foundation.render.DrawUtils;
import shagejack.lostgrace.foundation.render.FluidRenderer;
import shagejack.lostgrace.foundation.tile.renderer.SafeTileEntityRenderer;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.fluid.AllFluids;

import java.awt.*;
import java.util.Collection;
import java.util.Random;

public class DreamPoolRenderer extends SafeTileEntityRenderer<DreamPoolTileEntity> {

    public DreamPoolRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(DreamPoolTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderDream(te, partialTicks, ms, buffer, light, overlay);
        renderDreamEffect(te, partialTicks, ms, buffer, light, overlay);
        renderProcessedDream(te, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderDream(DreamPoolTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (te.tank.isEmpty() && te.tank.getFluidAmount() <= 0)
            return;

        int amount = te.tank.getFluidAmount();

        final float xMin, xMax, yMin, yMax, zMin, zMax;

        xMin = 0.126F;
        xMax = 0.874F;
        zMin = 0.126F;
        zMax = 0.875F;
        yMin = 0.1205F;
        yMax = yMin + (float) amount / te.getCapacity() / 4.016f;

        ms.pushPose();

        // render top only
        FluidRenderer.renderFluidStack(te.tank.getFluid(), te, ms, bufferSource, xMin, xMax, yMin, yMax, zMin, zMax, true, true);

        ms.popPose();
    }

    protected void renderDreamEffect(DreamPoolTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (te.tank.isEmpty())
            return;

        if (te.getLevel() == null)
            return;

        int amount = te.tank.getFluidAmount();
        int count = amount / 50;
        int changeDelay = Math.max(1, 4000 * (1000 - amount) / 1000);
        double range = te.getDreamRadius();

        long mill = System.currentTimeMillis();
        Random random = new Random(mill / changeDelay);
        int moveTimeCost = Math.max(changeDelay, 200);
        double moveMultiplier = mill % moveTimeCost / (double) moveTimeCost;

        for (int i = 0; i < count; i++) {

            switch(random.nextInt(0, 3)) {
                case 0 -> {
                    ms.pushPose();

                    Collection<Item> items = ForgeRegistries.ITEMS.getValues();

                    ItemStack stack = new ItemStack(items.stream().skip(random.nextInt(items.size())).findFirst().orElse(null));

                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

                    Vector3 randomScale = Vector3.random(random).multiply(5);
                    Vector3 moveVector = Vector3.random(random).multiply(moveMultiplier * range / 3.0);
                    Vector3 randomOffset = Vector3.random(random).multiply(range).add(moveVector);

                    ms.translate(randomOffset.x(), range + randomOffset.y(), randomOffset.z());
                    ms.scale(randomScale.xF(), randomScale.yF(), randomScale.zF());
                    ms.mulPose(Vector3.random(random).asRotateAxis(random.nextDouble(2 * Math.PI)));

                    itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, ms, bufferSource, 0);

                    ms.popPose();
                }
                case 1 -> {
                    ms.pushPose();

                    Collection<Block> blocks = ForgeRegistries.BLOCKS.getValues();

                    BlockState state = blocks.stream().skip(random.nextInt(blocks.size())).findFirst().orElse(Blocks.GRASS).defaultBlockState();

                    BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

                    Vector3 randomScale = Vector3.random(random).multiply(5);
                    Vector3 moveVector = Vector3.random(random).multiply(moveMultiplier * range / 3.0);
                    Vector3 randomOffset = Vector3.random(random).multiply(range).add(moveVector);

                    ms.translate(randomOffset.x(), range + randomOffset.y(), randomOffset.z());
                    ms.scale(randomScale.xF(), randomScale.yF(), randomScale.zF());
                    ms.mulPose(Vector3.random(random).asRotateAxis(random.nextDouble(2 * Math.PI)));

                    blockRenderer.renderSingleBlock(state, ms, bufferSource, light, overlay, te.getModelData());

                    ms.popPose();
                }
                case 2 -> {
                    Collection<EntityType<?>> entities = ForgeRegistries.ENTITIES.getValues();

                    Entity entity = entities.stream().skip(random.nextInt(entities.size())).findFirst().orElse(EntityType.ZOMBIE).create(te.getLevel());

                    if (entity == null) {
                        i--;
                        continue;
                    }

                    EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();

                    Vector3 randomScale = Vector3.random(random).multiply(5);
                    Vector3 moveVector = Vector3.random(random).multiply(moveMultiplier * range / 3.0);
                    Vector3 randomOffset = Vector3.random(random).multiply(range).add(moveVector);

                    ms.pushPose();

                    ms.scale(randomScale.xF(), randomScale.yF(), randomScale.zF());
                    ms.mulPose(Vector3.random(random).asRotateAxis(random.nextDouble(2 * Math.PI)));

                    double d0 = Mth.lerp(partialTicks, entity.xOld, entity.getX()) + randomOffset.x();
                    double d1 = Mth.lerp(partialTicks, entity.yOld, entity.getY()) + range + randomOffset.y();
                    double d2 = Mth.lerp(partialTicks, entity.zOld, entity.getZ()) + randomOffset.z();
                    float f = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());

                    entityRenderer.render(entity, d0, d1, d2, f, partialTicks, ms, bufferSource, light);

                    ms.popPose();
                }
            }

        }
    }

    protected void renderProcessedDream(DreamPoolTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        int processDreamAmount = te.getProcessedDreamAmount();

        if (processDreamAmount <= 0)
            return;

        TextureAtlasSprite sprite = FluidRenderer.getFluidTextureSprite(AllFluids.dream.asFluid());

        ms.pushPose();

        ms.translate(.5, .5, .5);

        DrawUtils.renderSphereWithTexture(ms, processDreamAmount / 2400.0, sprite, Color.WHITE, 255);

        ms.popPose();
    }


}
