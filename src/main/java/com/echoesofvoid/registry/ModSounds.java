package com.echoesofvoid.registry;

import com.echoesofvoid.EchoesOfTheVoid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier SCREAMER_ID = new Identifier(EchoesOfTheVoid.MOD_ID, "screamer");
    public static final SoundEvent SCREAMER = SoundEvent.of(SCREAMER_ID);

    public static final Identifier STATIC_NOISE_ID = new Identifier(EchoesOfTheVoid.MOD_ID, "static_noise");
    public static final SoundEvent STATIC_NOISE = SoundEvent.of(STATIC_NOISE_ID);

    public static final Identifier WHISPER_FOOTSTEPS_ID = new Identifier(EchoesOfTheVoid.MOD_ID, "whisper_footsteps");
    public static final SoundEvent WHISPER_FOOTSTEPS = SoundEvent.of(WHISPER_FOOTSTEPS_ID);

    public static final Identifier VOID_SCREAM_ID = new Identifier(EchoesOfTheVoid.MOD_ID, "void_scream");
    public static final SoundEvent VOID_SCREAM = SoundEvent.of(VOID_SCREAM_ID);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, SCREAMER_ID, SCREAMER);
        Registry.register(Registries.SOUND_EVENT, STATIC_NOISE_ID, STATIC_NOISE);
        Registry.register(Registries.SOUND_EVENT, WHISPER_FOOTSTEPS_ID, WHISPER_FOOTSTEPS);
        Registry.register(Registries.SOUND_EVENT, VOID_SCREAM_ID, VOID_SCREAM);
        EchoesOfTheVoid.LOGGER.info("Sounds registered.");
    }
}
