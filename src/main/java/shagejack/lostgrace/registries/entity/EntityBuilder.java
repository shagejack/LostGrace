package shagejack.lostgrace.registries.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.registries.RegisterHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class EntityBuilder<T extends Entity> {

    public static final List<EntityBuilder.AttributesBinder<?>> attributesTasks = new ArrayList<>();
    public static final List<EntityBuilder.RenderBinder<?>> rendererTasks = new ArrayList<>();

    protected String name;
    protected EntityType.Builder<T> builder;
    protected RegistryObject<EntityType<?>> registryObject = null;

    protected EntityBuilder() {}

    public static <E extends Entity> EntityBuilder<E> of(EntityType.EntityFactory<E> factory, MobCategory category) {
        return new EntityBuilder<E>().builder(EntityType.Builder.of(factory, category));
    }

    public EntityBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public EntityBuilder<T> builder(EntityType.Builder<T> builder) {
        this.builder = builder;
        return this;
    }

    public EntityBuilder<T> builder(UnaryOperator<EntityType.Builder<T>> operator) {
        this.builder = operator.apply(this.builder);
        return this;
    }

    // only for living entities
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EntityBuilder<T> attribute(Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier) {
        attributesTasks.add(new EntityBuilder.AttributesBinder(() -> registryObject, attributeSupplierBuilderSupplier));
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public EntityBuilder<T> renderer(NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? extends T>>> rendererProviderSupplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> rendererTasks.add(new EntityBuilder.RenderBinder(() -> registryObject, context -> rendererProviderSupplier.get().apply(context))));
        return this;
    }

    public RegistryObject<EntityType<? extends Entity>> build() {
        registryObject = RegisterHandle.ENTITY_TYPE_REGISTER.register(name, () -> builder.build(name));
        return registryObject;
    }

    public static void bindAttributes(final EntityAttributeCreationEvent event) {
        attributesTasks.forEach(task -> task.register(event));
    }

    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        rendererTasks.forEach(task -> task.register(event));
    }

    private record AttributesBinder<T extends LivingEntity>(Supplier<RegistryObject<EntityType<? extends T>>> entityTypeSupplier, Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier) {
        private void register(final EntityAttributeCreationEvent event) {
            event.put(entityTypeSupplier.get().get(), attributeSupplierBuilderSupplier.get().build());
        }
    }

    private record RenderBinder<T extends Entity>(Supplier<RegistryObject<EntityType<? extends T>>> entityTypeSupplier, EntityRendererProvider<T> renderer) {
        private void register(final EntityRenderersEvent.RegisterRenderers event) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    event.registerEntityRenderer(entityTypeSupplier.get().get(), renderer)
            );
        }
    }

}
