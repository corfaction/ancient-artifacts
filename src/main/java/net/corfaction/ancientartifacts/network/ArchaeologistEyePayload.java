package net.corfaction.ancientartifacts.network;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ArchaeologistEyePayload(List<BlockPos> positions, int duration)
        implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(
            AncientArtifacts.MOD_ID,
            "archaeologist_eye"
    );

    public static final Type<ArchaeologistEyePayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ArchaeologistEyePayload>
            CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ArchaeologistEyePayload::positions,
            ByteBufCodecs.VAR_INT,
            ArchaeologistEyePayload::duration,
            ArchaeologistEyePayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(
            ServerPlayer player,
            List<BlockPos> positions,
            int duration
    ) {
        ServerPlayNetworking.send(
                player,
                new ArchaeologistEyePayload(positions, duration)
        );
    }
}