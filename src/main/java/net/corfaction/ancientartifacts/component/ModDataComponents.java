package net.corfaction.ancientartifacts.component;

import com.mojang.serialization.Codec;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModDataComponents {

    public static final DataComponentType<Boolean> ACTIVATED = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            AncientArtifacts.id("activated"),
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build()
    );

    public static void register() {
    }

}