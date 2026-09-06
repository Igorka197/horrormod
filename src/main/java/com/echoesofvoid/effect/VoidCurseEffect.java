package com.echoesofvoid.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * Void Curse — slowly drains health over 60 seconds.
 * Deals 0.5 hearts (1 HP) every 2.5 seconds for ~24 ticks of damage.
 */
public class VoidCurseEffect extends StatusEffect {
    public VoidCurseEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Apply damage every 50 ticks (2.5 seconds)
        return duration % 50 == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        DamageSource source = entity.getDamageSources().magic();
        entity.damage(source, 1.0f + amplifier);
    }
}
