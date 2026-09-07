package com.echoesofvoid;

import com.echoesofvoid.effect.ModEffects;
import com.echoesofvoid.entity.SmilingVariantEntity;
import com.echoesofvoid.entity.VoidStalkerEntity;
import com.echoesofvoid.registry.ModEntities;
import com.echoesofvoid.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoesOfTheVoid implements ModInitializer {
    public static final String MOD_ID = "echoes_of_void";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("=== Echoes of the Void — awakening... ===");

        // Register entities
        ModEntities.register();
        ModSounds.register();
        ModEffects.register();

        // Register entity attributes
        FabricDefaultAttributeRegistry.register(ModEntities.SMILING_VARIANT, SmilingVariantEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.VOID_STALKER, VoidStalkerEntity.createAttributes());

        // Spawn restrictions
        SpawnRestriction.register(ModEntities.SMILING_VARIANT,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                SmilingVariantEntity::checkSmilingSpawn);

        SpawnRestriction.register(ModEntities.VOID_STALKER,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                VoidStalkerEntity::checkVoidSpawn);

        // Add natural spawning to biomes
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER,
                ModEntities.SMILING_VARIANT,
                15,  // weight
                1,   // min group size
                1    // max group size
        );

        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER,
                ModEntities.VOID_STALKER,
                10,
                1,
                1
        );

        LOGGER.info("=== The darkness is watching. ===");
    }
}
