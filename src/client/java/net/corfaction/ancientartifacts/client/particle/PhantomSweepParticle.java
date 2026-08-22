package net.corfaction.ancientartifacts.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public final class PhantomSweepParticle extends SingleQuadParticle {

    private static final float MAX_ALPHA = 0.8F;
    private static final float START_SIZE = 1.5F;
    private static final float END_SIZE = 1.9F;

    private final SpriteSet sprites;

    private PhantomSweepParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            SpriteSet sprites
    ) {
        super(level, x, y, z, xd, yd, zd, sprites.get(0, 1));

        this.sprites = sprites;
        lifetime = 5;
        gravity = 0.0F;
        friction = 1.0F;
        setColor(0.25F, 0.75F, 1.0F);
        setAlpha(MAX_ALPHA);
        quadSize = START_SIZE;
    }

    @Override
    public void tick() {
        super.tick();

        if (removed) {
            return;
        }

        float progress = (float) age / lifetime;

        setAlpha(MAX_ALPHA * (1.0F - progress));
        quadSize = START_SIZE + (END_SIZE - START_SIZE) * progress;
        setSpriteFromAge(sprites);
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_Y;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xAux,
                double yAux,
                double zAux,
                RandomSource random
        ) {
            return new PhantomSweepParticle(
                    level,
                    x,
                    y,
                    z,
                    xAux,
                    yAux,
                    zAux,
                    sprites
            );
        }
    }
}