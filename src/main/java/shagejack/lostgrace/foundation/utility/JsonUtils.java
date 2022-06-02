package shagejack.lostgrace.foundation.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static ItemStack getItemStack(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);
        if (element instanceof JsonObject stackJson) {
            ResourceLocation outputItem = new ResourceLocation(stackJson.get("item").getAsString());
            return new ItemStack(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(outputItem)).orElseThrow(() -> new IllegalStateException("Item: " + outputItem + " does not exist")), stackJson.get("count").getAsInt());
        } else {
            ResourceLocation outputItem = new ResourceLocation(element.getAsString());
            return new ItemStack(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(outputItem)).orElseThrow(() -> new IllegalStateException("Item: " + outputItem + " does not exist")));
        }
    }
}
