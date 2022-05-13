package shagejack.lostgrace.foundation.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class RenderingUtils {

    private RenderingUtils() {
        throw new IllegalStateException(this.getClass().toString() + "should not be instantiated as it's a utility class.");
    }

    public static void draw(VertexFormat.Mode drawMode, VertexFormat format, Consumer<BufferBuilder> renderFun) {
        draw(drawMode, format, bufferBuilder -> {
            renderFun.accept(bufferBuilder);
            return null;
        });
    }

    public static <R> R draw(VertexFormat.Mode drawMode, VertexFormat format, Function<BufferBuilder, R> renderFun) {
        BufferBuilder buf = Tesselator.getInstance().getBuilder();
        buf.begin(drawMode, format);
        R result = renderFun.apply(buf);
        finishDrawing(buf);
        return result;
    }

    public static void finishDrawing(BufferBuilder buf) {
        finishDrawing(buf, null);
    }

    public static void finishDrawing(BufferBuilder buf, @Nullable RenderType type) {
        if (buf.building()) {
            if (type != null) {
                type.end(buf, 0, 0, 0);
            } else {
                buf.end();
                BufferUploader.end(buf);
            }
        }
    }
}
