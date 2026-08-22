package net.corfaction.ancientartifacts.particle;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModParticles {

    private ModParticles() {
    }

    public static void register() {
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Identifier.fromNamespaceAndPath(
                        AncientArtifacts.MOD_ID,
                        "phantom_sweep"
                ),
                PhantomSweepParticleType.PHANTOM_SWEEP
        );
    }
}