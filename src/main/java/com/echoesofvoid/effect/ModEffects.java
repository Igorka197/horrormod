package com.echoesofvoid.effect;

import com.echoesofvoid.EchoesOfTheVoid;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final StatusEffect VOID_CURSE = new VoidCurseEffect(StatusEffectCategory.HARMFUL, 0x1a0033);

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT,
                new Identifier(EchoesOfTheVoid.MOD_ID, "void_curse"), VOID_CURSE);
        EchoesOfTheVoid.LOGGER.info("Effects registered.");
    }
}
