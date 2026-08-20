package net.corfaction.ancientartifacts.network;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GuardianGhostStatePayload(boolean enabled) implements CustomPacketPayload {

    public static final Identifier ID = AncientArtifacts.id("guardian_ghost_state");

    public static final Type<GuardianGhostStatePayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, GuardianGhostStatePayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeBoolean(payload.enabled()),
                    buf -> new GuardianGhostStatePayload(buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}