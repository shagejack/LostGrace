package shagejack.lostgrace.contents.recipe.anvil;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import shagejack.lostgrace.foundation.recipe.IRecipeTypeInfo;
import shagejack.lostgrace.foundation.utility.JsonUtils;
import shagejack.lostgrace.registries.recipe.AllRecipeTypes;

public class AnvilRecipe implements Recipe<Container> {

    private RecipeType<?> type;
    private RecipeSerializer<?> serializer;
    private IRecipeTypeInfo typeInfo;

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int extraCount;

    public AnvilRecipe(ResourceLocation id, Ingredient input, ItemStack output, int extraCount) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.extraCount = extraCount;

        this.typeInfo = AllRecipeTypes.ANVIL;
        this.type = typeInfo.getType();
        this.serializer = typeInfo.getSerializer();
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container) {
        return output.copy();
    }

    public int getExtraCount() {
        return extraCount;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    public ItemStack getExtraResultItem() {
        return new ItemStack(output.getItem(), extraCount);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AnvilRecipe> {

        @Override
        public AnvilRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            Ingredient input = Ingredient.fromJson(serializedRecipe.get("ingredient"));
            ItemStack output = JsonUtils.getItemStack(serializedRecipe, "result");
            int extraCount = serializedRecipe.get("extra_count").getAsInt();
            return new AnvilRecipe(recipeId, input, output, extraCount);
        }

        @Nullable
        @Override
        public AnvilRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            int extraCount = buffer.readInt();
            return new AnvilRecipe(recipeId, input, output, extraCount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AnvilRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.extraCount);
        }
    }
}
