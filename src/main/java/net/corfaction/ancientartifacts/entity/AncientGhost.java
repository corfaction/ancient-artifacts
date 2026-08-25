package net.corfaction.ancientartifacts.entity;

import net.corfaction.ancientartifacts.block.entity.ActivationAltarBlockEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class AncientGhost extends Vex {

    private ActivationAltarBlockEntity activationAltar;

    public AncientGhost(
            EntityType<? extends Vex> type,
            Level level
    ) {
        super(type, level);
    }

    public void setActivationAltar(ActivationAltarBlockEntity altar) {
        activationAltar = altar;
    }

    public ActivationAltarBlockEntity getActivationAltar() {
        return activationAltar;
    }

    @Override
    public void die(@NonNull DamageSource damageSource) {
        if (activationAltar != null
                && level() instanceof ServerLevel serverLevel) {
            spawnDeathParticles(serverLevel);
        }

        super.die(damageSource);

        if (activationAltar != null) {
            activationAltar.onGhostKilled(this);
        }
    }

    public void evaporate() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            discard();
            return;
        }

        spawnEvaporationParticles(serverLevel);
        discard();
    }

    private void spawnDeathParticles(ServerLevel level) {
        double startX = getX();
        double startY = getY() + getBbHeight() * 0.5;
        double startZ = getZ();

        double endX = activationAltar.getBlockPos().getX() + 0.5;
        double endY = activationAltar.getBlockPos().getY() + 1.1;
        double endZ = activationAltar.getBlockPos().getZ() + 0.5;

        int count = 16;

        for (int i = 0; i < count; i++) {
            double progress = (double) i / count;

            double x = startX + (endX - startX) * progress;
            double y = startY + (endY - startY) * progress;
            double z = startZ + (endZ - startZ) * progress;

            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    x,
                    y,
                    z,
                    1,
                    0.02,
                    0.02,
                    0.02,
                    0.0
            );
        }
    }

    private void spawnEvaporationParticles(ServerLevel level) {
        level.sendParticles(
                ParticleTypes.CLOUD,
                getX(),
                getY() + getBbHeight() * 0.5,
                getZ(),
                25,
                0.35,
                0.5,
                0.35,
                0.03
        );
    }
}