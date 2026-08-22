package net.corfaction.ancientartifacts.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PhantomSweepPayload(
        double x,
        double y,
        double z,
        float yRot
) implements CustomPacketPayload {

    public static final Identifier ID =
            Identifier.fromNamespaceAndPath(
                    "ancient-artifacts",
                    "phantom_sweep"
            );

    public static final Type<PhantomSweepPayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            PhantomSweepPayload
            > CODEC = StreamCodec.of(
            PhantomSweepPayload::write,
            PhantomSweepPayload::read
    );

    private static void write(
            RegistryFriendlyByteBuf buf,
            PhantomSweepPayload payload
    ) {
        buf.writeDouble(payload.x);
        buf.writeDouble(payload.y);
        buf.writeDouble(payload.z);
        buf.writeFloat(payload.yRot);
    }

    private static PhantomSweepPayload read(
            RegistryFriendlyByteBuf buf
    ) {
        return new PhantomSweepPayload(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}