package net.corfaction.ancientartifacts.client.entity;

import net.corfaction.ancientartifacts.network.PhantomSweepPayload;
import net.corfaction.ancientartifacts.particle.PhantomSweepParticleType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientPhantomSweep {

    private ClientPhantomSweep() {
    }

    public static void register() {
        ParticleProviderRegistry.getInstance().register(
                PhantomSweepParticleType.PHANTOM_SWEEP,
                net.corfaction.ancientartifacts.client.particle.PhantomSweepParticle.Provider::new
        );

        ClientPlayNetworking.registerGlobalReceiver(
                PhantomSweepPayload.TYPE,
                (payload, context) -> context.client().execute(() -> {
                    ClientLevel level = context.client().level;

                    if (level != null) {
                        spawnSweep(level, payload);
                    }
                })
        );
    }

    private static void spawnSweep(ClientLevel level, PhantomSweepPayload payload) {
        float radians = payload.yRot() * ((float) Math.PI / 180.0F);
        double dx = -Math.sin(radians);
        double dz = Math.cos(radians);

        level.addParticle(
                PhantomSweepParticleType.PHANTOM_SWEEP,
                payload.x() + dx * 0.8D,
                payload.y() + 0.7D,
                payload.z() + dz * 0.8D,
                dx,
                0.0D,
                dz
        );
    }
}