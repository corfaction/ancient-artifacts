package net.corfaction.ancientartifacts.block.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.corfaction.ancientartifacts.component.ModDataComponents;
import net.corfaction.ancientartifacts.entity.AncientGhost;
import net.corfaction.ancientartifacts.entity.ModEntityTypes;
import net.corfaction.ancientartifacts.item.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class ActivationAltarBlockEntity extends BlockEntity {

    private static final String ITEM_KEY = "Item";
    private static final String ACTIVATING_KEY = "Activating";
    private static final String KILLED_GHOSTS_KEY = "KilledGhosts";
    private static final String SPAWNED_GHOSTS_KEY = "SpawnedGhosts";
    private static final String GHOST_IDS_KEY = "GhostIds";

    private static final int GHOST_SPAWN_INTERVAL = 40;
    private static final int KILL_TARGET = 20;
    private static final int MAX_SPAWNED_GHOSTS = 40;

    private ItemStack item = ItemStack.EMPTY;

    private boolean activating;
    private int spawnTicks;
    private int killedGhosts;
    private int spawnedGhosts;

    private final Set<AncientGhost> ghosts = new HashSet<>();
    private final Set<UUID> ghostIds = new HashSet<>();

    public ActivationAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ACTIVATION_ALTAR, pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            ActivationAltarBlockEntity altar
    ) {
        if (!altar.activating || altar.isEmpty()) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        altar.restoreGhosts(serverLevel);
        altar.removeDeadGhosts();

        if (altar.killedGhosts >= KILL_TARGET) {
            altar.successfulActivation(serverLevel);
            return;
        }

        if (altar.spawnedGhosts >= MAX_SPAWNED_GHOSTS) {
            altar.failedActivation(serverLevel);
            return;
        }

        if (altar.killedGhosts > 0) {
            altar.spawnSoulParticles(serverLevel);
        }

        altar.spawnTicks++;

        if (altar.spawnTicks >= GHOST_SPAWN_INTERVAL) {
            altar.spawnTicks = 0;
            altar.spawnGhost(serverLevel);
        }
    }

    private void spawnGhost(ServerLevel level) {
        AncientGhost ghost = ModEntityTypes.ANCIENT_GHOST.create(
                level,
                EntitySpawnReason.EVENT
        );

        if (ghost == null) {
            return;
        }

        double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
        double distance = 3.0 + level.getRandom().nextDouble() * 2.0;

        double x = worldPosition.getX()
                + 0.5
                + Math.cos(angle) * distance;

        double y = worldPosition.getY()
                + 1.0
                + level.getRandom().nextDouble() * 2.0;

        double z = worldPosition.getZ()
                + 0.5
                + Math.sin(angle) * distance;

        ghost.setPos(x, y, z);
        ghost.setHealth(0.5F);
        ghost.setActivationAltar(this);

        level.addFreshEntity(ghost);

        ghosts.add(ghost);
        ghostIds.add(ghost.getUUID());
        spawnedGhosts++;

        setChanged();
        sync();
    }

    public void onGhostKilled(AncientGhost ghost) {
        if (!activating) {
            return;
        }

        if (!ghosts.remove(ghost)) {
            return;
        }

        ghostIds.remove(ghost.getUUID());
        killedGhosts++;

        if (level instanceof ServerLevel serverLevel) {
            spawnGhostDeathParticles(serverLevel, ghost);
        }

        setChanged();
        sync();

        if (killedGhosts >= KILL_TARGET
                && level instanceof ServerLevel serverLevel) {
            successfulActivation(serverLevel);
        }
    }

    private void restoreGhosts(ServerLevel level) {
        ghosts.clear();

        for (UUID uuid : ghostIds) {
            if (level.getEntity(uuid) instanceof AncientGhost ghost
                    && ghost.isAlive()) {
                ghost.setActivationAltar(this);
                ghosts.add(ghost);
            }
        }
    }

    private void spawnGhostDeathParticles(
            ServerLevel level,
            AncientGhost ghost
    ) {
        double x = ghost.getX();
        double y = ghost.getY() + 0.5;
        double z = ghost.getZ();

        double targetX = worldPosition.getX() + 0.5;
        double targetY = worldPosition.getY() + 1.5;
        double targetZ = worldPosition.getZ() + 0.5;

        for (int i = 0; i < 12; i++) {
            double progress = i / 12.0;

            double px = x + (targetX - x) * progress;
            double py = y + (targetY - y) * progress;
            double pz = z + (targetZ - z) * progress;

            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    px,
                    py,
                    pz,
                    2,
                    0.04,
                    0.04,
                    0.04,
                    0.01
            );
        }
    }

    private void spawnSoulParticles(ServerLevel level) {
        if (level.getGameTime() % 5 != 0) {
            return;
        }

        int targetCount = Math.min(killedGhosts, KILL_TARGET);

        for (int i = 0; i < targetCount; i++) {
            double centerX = worldPosition.getX() + 0.5;
            double centerY = worldPosition.getY() + 1.5;
            double centerZ = worldPosition.getZ() + 0.5;

            double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
            double distance = 0.7
                    + level.getRandom().nextDouble() * 1.3;

            double x = centerX + Math.cos(angle) * distance;
            double y = centerY
                    + level.getRandom().nextDouble() * 1.5;
            double z = centerZ + Math.sin(angle) * distance;

            level.sendParticles(
                    ParticleTypes.SOUL,
                    x,
                    y,
                    z,
                    1,
                    0.0,
                    0.02,
                    0.0,
                    0.01
            );
        }
    }

    private void removeDeadGhosts() {
        ghosts.removeIf(ghost -> !ghost.isAlive());

        if (level == null) {
            return;
        }

        ghostIds.removeIf(uuid -> {
            var entity = level.getEntity(uuid);
            return entity instanceof AncientGhost ghost && !ghost.isAlive();
        });
    }

    private void successfulActivation(ServerLevel level) {
        spawnSuccessParticles(level);

        level.playSound(
                null,
                worldPosition,
                SoundEvents.PLAYER_LEVELUP,
                SoundSource.BLOCKS,
                1.0F,
                1.2F
        );

        activating = false;
        spawnTicks = 0;

        removeRemainingGhosts();

        activateItem();

        if (!item.isEmpty()) {
            Block.popResource(
                    level,
                    worldPosition,
                    item
            );

            item = ItemStack.EMPTY;
        }

        killedGhosts = 0;
        spawnedGhosts = 0;

        setChanged();
        sync();
    }

    private void failedActivation(ServerLevel level) {
        spawnFailureParticles(level);

        level.playSound(
                null,
                worldPosition,
                SoundEvents.VEX_DEATH,
                SoundSource.BLOCKS,
                1.0F,
                0.7F
        );

        activating = false;
        spawnTicks = 0;

        removeRemainingGhosts();

        item = ItemStack.EMPTY;

        killedGhosts = 0;
        spawnedGhosts = 0;

        setChanged();
        sync();
    }

    private void activateItem() {
        if (item.isEmpty()) {
            return;
        }

        item.set(
                ModDataComponents.ACTIVATED,
                true
        );
    }

    private void removeRemainingGhosts() {
        for (AncientGhost ghost : ghosts) {
            if (ghost.isAlive()) {
                ghost.evaporate();
            }
        }

        if (level != null) {
            for (UUID uuid : ghostIds) {
                if (level.getEntity(uuid) instanceof AncientGhost ghost
                        && ghost.isAlive()) {
                    ghost.evaporate();
                }
            }
        }

        ghosts.clear();
        ghostIds.clear();
    }

    private void spawnSuccessParticles(ServerLevel level) {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.0;
        double z = worldPosition.getZ() + 0.5;

        level.sendParticles(
                ParticleTypes.END_ROD,
                x,
                y,
                z,
                60,
                0.35,
                0.1,
                0.35,
                0.12
        );

        level.sendParticles(
                ParticleTypes.ENCHANT,
                x,
                y + 0.2,
                z,
                80,
                0.5,
                0.15,
                0.5,
                0.8
        );

        for (int i = 0; i < 24; i++) {
            double angle = Math.PI * 2.0 * i / 24.0;

            double px = x + Math.cos(angle) * 0.8;
            double pz = z + Math.sin(angle) * 0.8;

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    px,
                    y + 0.05,
                    pz,
                    1,
                    0,
                    0.08,
                    0,
                    0.08
            );
        }
    }

    private void spawnFailureParticles(ServerLevel level) {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.0;
        double z = worldPosition.getZ() + 0.5;

        level.sendParticles(
                ParticleTypes.SMOKE,
                x,
                y,
                z,
                40,
                0.4,
                0.2,
                0.4,
                0.05
        );

        level.sendParticles(
                ParticleTypes.EXPLOSION,
                x,
                y,
                z,
                3,
                0.2,
                0.2,
                0.2,
                0.0
        );

        for (int i = 0; i < 8; i++) {
            spawnLightningBurst(level, x, y, z);
        }
    }

    private void spawnLightningBurst(
            ServerLevel level,
            double x,
            double y,
            double z
    ) {
        double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
        double distance = 0.3
                + level.getRandom().nextDouble() * 0.8;

        double startX = x + Math.cos(angle) * distance;
        double startZ = z + Math.sin(angle) * distance;

        for (int i = 0; i < 8; i++) {
            double progress = i / 8.0;

            double px = startX + (x - startX) * progress;
            double py = y + progress * 1.2;
            double pz = startZ + (z - startZ) * progress;

            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    px,
                    py,
                    pz,
                    2,
                    0.04,
                    0.04,
                    0.04,
                    0.02
            );
        }
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean canAcceptItem(ItemStack stack) {
        return !activating
                && stack.is(ModItemTags.ARTIFACT)
                && !stack.getOrDefault(
                ModDataComponents.ACTIVATED,
                false
        );
    }

    public boolean isActivating() {
        return activating;
    }

    public int getKilledGhosts() {
        return killedGhosts;
    }

    public int getSpawnedGhosts() {
        return spawnedGhosts;
    }

    public void setItem(ItemStack stack) {
        if (!canAcceptItem(stack)) {
            return;
        }

        item = stack.copyWithCount(1);

        activating = true;
        spawnTicks = 0;
        killedGhosts = 0;
        spawnedGhosts = 0;

        ghosts.clear();
        ghostIds.clear();

        setChanged();
        sync();
    }

    public ItemStack removeItem() {
        if (item.isEmpty() || activating) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = item;
        item = ItemStack.EMPTY;

        spawnTicks = 0;
        killedGhosts = 0;
        spawnedGhosts = 0;

        ghosts.clear();
        ghostIds.clear();

        setChanged();
        sync();

        return stack;
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    private void sync() {
        if (level != null) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        item = input.read(
                ITEM_KEY,
                ItemStack.CODEC
        ).orElse(ItemStack.EMPTY);

        activating = input.getInt(
                ACTIVATING_KEY
        ).orElse(0) != 0;

        killedGhosts = input.getInt(
                KILLED_GHOSTS_KEY
        ).orElse(0);

        spawnedGhosts = input.getInt(
                SPAWNED_GHOSTS_KEY
        ).orElse(0);

        spawnTicks = 0;

        ghosts.clear();
        ghostIds.clear();

        input.read(
                GHOST_IDS_KEY,
                UUIDUtil.CODEC_SET
        ).ifPresent(ghostIds::addAll);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        if (!item.isEmpty()) {
            output.store(
                    ITEM_KEY,
                    ItemStack.CODEC,
                    item
            );
        }

        if (activating) {
            output.putInt(
                    ACTIVATING_KEY,
                    1
            );
        }

        if (killedGhosts > 0) {
            output.putInt(
                    KILLED_GHOSTS_KEY,
                    killedGhosts
            );
        }

        if (spawnedGhosts > 0) {
            output.putInt(
                    SPAWNED_GHOSTS_KEY,
                    spawnedGhosts
            );
        }

        if (!ghostIds.isEmpty()) {
            output.store(
                    GHOST_IDS_KEY,
                    UUIDUtil.CODEC_SET,
                    ghostIds
            );
        }
    }

    @Override
    public void preRemoveSideEffects(
            @NonNull BlockPos pos,
            @NonNull BlockState state
    ) {
        if (level instanceof ServerLevel serverLevel) {
            removeRemainingGhosts();
        } else {
            ghosts.clear();
            ghostIds.clear();
        }

        item = ItemStack.EMPTY;
        activating = false;
        spawnTicks = 0;
        killedGhosts = 0;
        spawnedGhosts = 0;

        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(
            HolderLookup.@NonNull Provider registries
    ) {
        return saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}