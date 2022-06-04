package shagejack.lostgrace.registries.tile;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarRenderer;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarTileEntity;
import shagejack.lostgrace.contents.block.dreamPool.DreamPoolRenderer;
import shagejack.lostgrace.contents.block.dreamPool.DreamPoolTileEntity;
import shagejack.lostgrace.contents.block.grace.GraceRenderer;
import shagejack.lostgrace.contents.block.grace.GraceTileEntity;
import shagejack.lostgrace.registries.block.AllBlocks;

public class AllTileEntities {

    public static final RegistryObject<BlockEntityType<?>> grace
            = new TileEntityBuilder<GraceTileEntity>()
            .name("grace")
            .tileEntity(GraceTileEntity::new)
            .validBlocks(AllBlocks.grace)
            .renderer(() -> GraceRenderer::new)
            .build();

    public static final RegistryObject<BlockEntityType<?>> bloodAltar
            = new TileEntityBuilder<BloodAltarTileEntity>()
            .name("blood_altar")
            .tileEntity(BloodAltarTileEntity::new)
            .validBlocks(AllBlocks.bloodAltar)
            .renderer(() -> BloodAltarRenderer::new)
            .build();

    public static final RegistryObject<BlockEntityType<?>> dreamPool
            = new TileEntityBuilder<DreamPoolTileEntity>()
            .name("dream_pool")
            .tileEntity(DreamPoolTileEntity::new)
            .validBlocks(AllBlocks.dreamPool)
            .renderer(() -> DreamPoolRenderer::new)
            .build();

}
