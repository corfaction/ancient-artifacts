package net.corfaction.ancientartifacts.entity;

public class ModEntity {
    public static void register() {
        ModEntityTypes.registerModEntityTypes();
        ModEntityTypes.registerAttributes();
        ModEntityTypes.registerSpawnPlacements();
    }
}
