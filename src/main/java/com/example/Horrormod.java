package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class Horrormod implements ModInitializer {
    public static final String MOD_ID = "horrormod";

    public static final RegistryKey<DimensionOptions> HIGHWAY_OPTIONS = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier(MOD_ID, "highway"));
    public static final RegistryKey<DimensionType> HIGHWAY_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(MOD_ID, "highway_type"));

    @Override
    public void onInitialize() {
        System.out.println("=== ХОРРОР МОД ЗАГРУЖЕН. ТЬМА ПРИБЛИЖАЕТСЯ... ===");
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "highway_generator"), HighwayChunkGenerator.CODEC);
    }
}
