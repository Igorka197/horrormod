package com.echoesofvoid.registry;

import com.echoesofvoid.EchoesOfTheVoid;
import com.echoesofvoid.entity.SmilingVariantEntity;
import com.echoesofvoid.entity.VoidStalkerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<SmilingVariantEntity> SMILING_VARIANT =
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SmilingVariantEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 2.6f))
                    .trackRangeChunks(8)
                    .build();

    public static final EntityType<VoidStalkerEntity> VOID_STALKER =
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, VoidStalkerEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 2.0f))
                    .trackRangeChunks(10)
                    .build();

    public static void register() {
        Registry.register(Registries.ENTITY_TYPE,
                new Identifier(EchoesOfTheVoid.MOD_ID, "smiling_variant"), SMILING_VARIANT);
        Registry.register(Registries.ENTITY_TYPE,
                new Identifier(EchoesOfTheVoid.MOD_ID, "void_stalker"), VOID_STALKER);
        EchoesOfTheVoid.LOGGER.info("Entities registered.");
    }
}
