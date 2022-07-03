package shagejack.lostgrace.foundation.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import shagejack.lostgrace.foundation.utility.Color;

public class EntityDataSerializersLG {
    public static final EntityDataSerializer<Double> DOUBLE = new EntityDataSerializer<>() {
        public void write(FriendlyByteBuf buffer, Double value) {
            buffer.writeDouble(value);
        }

        public Double read(FriendlyByteBuf buffer) {
            return buffer.readDouble();
        }

        public Double copy(Double value) {
            return value;
        }
    };

    public static final EntityDataSerializer<Color> COLOR = new EntityDataSerializer<>() {
        public void write(FriendlyByteBuf buffer, Color value) {
            buffer.writeInt(value.getRGB());
        }

        public Color read(FriendlyByteBuf buffer) {
            return new Color(buffer.readInt());
        }

        public Color copy(Color value) {
            return value;
        }
    };

    static {
        EntityDataSerializers.registerSerializer(DOUBLE);
        EntityDataSerializers.registerSerializer(COLOR);
    }
}
