package shagejack.lostgrace.registries.recipe;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.recipe.anvil.AnvilRecipe;
import shagejack.lostgrace.foundation.recipe.IRecipeTypeInfo;
import shagejack.lostgrace.foundation.utility.Lang;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public enum AllRecipeTypes implements IRecipeTypeInfo {

    ANVIL(AnvilRecipe.Serializer::new)

    ;

    private ResourceLocation id;
    private Supplier<RecipeSerializer<?>> serializerSupplier;
    private Supplier<RecipeType<?>> typeSupplier;
    private RecipeSerializer<?> serializer;
    private RecipeType<?> type;

    AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier) {
        this.id = LostGrace.asResource(Lang.asId(name()));
        this.serializerSupplier = serializerSupplier;
        this.typeSupplier = typeSupplier;
    }

    AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, RecipeType<?> existingType) {
        this(serializerSupplier, () -> existingType);
    }

    AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        this.id = LostGrace.asResource(Lang.asId(name()));
        this.serializerSupplier = serializerSupplier;
        this.typeSupplier = () -> simpleType(id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type;
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }

    public <C extends Container, T extends Recipe<C>> List<T> getAllRecipes(Level world) {
        return world.getRecipeManager().getAllRecipesFor(getType());
    }

    public static void register(RegistryEvent.Register<RecipeSerializer<?>> event) {
        ShapedRecipe.setCraftingSize(9, 9);

        for (AllRecipeTypes r : AllRecipeTypes.values()) {
            r.serializer = r.serializerSupplier.get();
            r.type = r.typeSupplier.get();
            r.serializer.setRegistryName(r.id);
            event.getRegistry().register(r.serializer);
        }
    }

    public static <T extends Recipe<?>> RecipeType<T> simpleType(ResourceLocation id) {
        String stringId = id.toString();
        return Registry.register(Registry.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return stringId;
            }
        });
    }

    public static boolean isManualRecipe(Recipe<?> recipe) {
        return recipe.getId()
                .getPath()
                .endsWith("_manual_only");
    }
}
