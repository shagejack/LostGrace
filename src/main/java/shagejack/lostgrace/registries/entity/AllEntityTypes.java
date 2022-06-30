package shagejack.lostgrace.registries.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.entity.chronophage.Chronophage;
import shagejack.lostgrace.contents.entity.chronophage.ChronophageRenderer;

public class AllEntityTypes {

    public static final RegistryObject<EntityType<? extends Entity>> chronophage = EntityBuilder.<Chronophage>of(Chronophage::new, MobCategory.MONSTER)
            .name("chronophage")
            .builder(builder -> builder.fireImmune().immuneTo(Blocks.WITHER_ROSE))
            .attribute(Chronophage::createAttributes)
            .renderer(() -> ChronophageRenderer::new)
            .build();

}
