package net.corfaction.ancientartifacts.network;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CleanArchaeologicalPixelPayload(int pixelX, int pixelY)
        implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(
            AncientArtifacts.MOD_ID,
            "clean_archaeological_pixel"
    );

    public static final Type<CleanArchaeologicalPixelPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, CleanArchaeologicalPixelPayload>
            CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CleanArchaeologicalPixelPayload::pixelX,
            ByteBufCodecs.VAR_INT,
            CleanArchaeologicalPixelPayload::pixelY,
            CleanArchaeologicalPixelPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}