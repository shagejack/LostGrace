package shagejack.lostgrace.registries.tile;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;
import shagejack.lostgrace.registries.RegisterHandle;
import shagejack.lostgrace.registries.record.ItemBlock;

import java.util.*;
import java.util.function.Supplier;

public class TileEntityBuilder<T extends BlockEntity> {

    private RegistryObject<BlockEntityType<?>> registryObject;
    public static final List<Binder<?>> tasks = new ArrayList<>();

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    public TileEntityBuilder<T> tileEntity(BlockEntityFactory<T> factory) {
        this.factory = factory;
        return this;
    }

    private String name;
    private BlockEntityFactory<T> factory = null;
    private final Set<RegistryObject<Block>> validBlocks = new HashSet<>();

    public RegistryObject<BlockEntityType<?>> build() {
        BlockEntityFactory<T> factory = this.factory;
        registryObject = RegisterHandle.BLOCK_ENTITY_TYPE_REGISTER.register(name, ()
                -> BlockEntityType.Builder.of(factory::create,
                        validBlocks.stream().map(RegistryObject::get).toArray(Block[]::new))
                .build(null));
        return registryObject;
    }

    public TileEntityBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public TileEntityBuilder<T> renderer(NonNullSupplier<NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T>>> renderer) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                tasks.add(new Binder(() -> registryObject, context -> renderer.get().apply(context))));
        return this;
    }

    public TileEntityBuilder<T> validBlocks(ItemBlock... block) {
        validBlocks.addAll(Arrays.stream(block).map(ItemBlock::block).toList());
        return this;
    }

    public TileEntityBuilder<T> validBlocks(RegistryObject<Block>... block) {
        validBlocks.addAll(Arrays.stream(block).toList());
        return this;
    }

    public static void bind(final EntityRenderersEvent.RegisterRenderers event) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> tasks.forEach(task -> task.register(event)));
    }

    private record Binder<T extends BlockEntity>(Supplier<RegistryObject<BlockEntityType<? extends T>>> blockEntityTypeSupplier, BlockEntityRendererProvider<T> render) {
        private void register(final EntityRenderersEvent.RegisterRenderers event) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    event.registerBlockEntityRenderer(blockEntityTypeSupplier.get().get(), render)
            );
        }
    }

}
