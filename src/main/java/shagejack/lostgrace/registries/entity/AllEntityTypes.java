package shagejack.lostgrace.registries.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.contents.entity.blackKnifeAssassin.BlackKnifeAssassin;
import shagejack.lostgrace.contents.entity.blackKnifeAssassin.BlackKnifeAssassinRenderer;

public class AllEntityTypes {

    public static final RegistryObject<EntityType<? extends Entity>> blackKnifeAssassin = EntityBuilder.<BlackKnifeAssassin>of(BlackKnifeAssassin::new, MobCategory.MONSTER)
            .name("black_knife_assassin")
            .builder(builder -> builder.fireImmune().immuneTo(Blocks.WITHER_ROSE))
            .attribute(BlackKnifeAssassin.createAttributes())
            .renderer(BlackKnifeAssassinRenderer::new)
            .build();

}
