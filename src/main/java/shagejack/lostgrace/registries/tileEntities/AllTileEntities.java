package shagejack.lostgrace.registries.tileEntities;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
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

}
