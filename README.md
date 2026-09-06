# Echoes of the Void

> *«The darkness is watching. It always was.»*

A horror mod for Minecraft **Fabric 1.20.1** that brings existential dread into your game.
Two terrifying entities stalk the player using unique psychological horror mechanics.

---

## 🔦 Monsters

### 1. Smiling Variant (3D — Улыбающийся Вариант)

- **Model**: Steve-based 3D model with unnaturally elongated limbs, completely black skin, and a giant white smile with human teeth.
- **Spawns**: Behind the player at 15-20 blocks distance, when light level < 4.
- **Behavior**:
  - Freezes instantly when you look at it.
  - If stared at for >3 seconds → camera shake + static noise fills the audio.
  - If you approach within 5 blocks → **SCREAMER**: full-screen face flash (0.5s), loud scream, **12 damage (6 hearts)**, Blindness for 10 seconds.
- **Invulnerable** — cannot be killed.

### 2. Void Stalker (2D — Тень из Вакуума)

- **Render**: 2D billboard sprite (always faces camera), silhouette with glowing blurry eyes.
- **Spawns**: Caves (below Y=50) and during thunder weather.
- **Behavior**:
  - Slowly follows the player from peripheral vision (doesn't attack directly).
  - Plays creepy footsteps sounds behind you.
  - As it approaches → Darkness effect applied.
  - If you look directly at it → **GLITCH SCREAMER**: sprite scales to fullscreen with glitch effect, then disappears, applying **Void Curse** (health drain over 60 seconds).
- **Invulnerable** — cannot be killed.

---

## 🛠 Building from Source

### Prerequisites

- **Java 17** (JDK)
- **Gradle 8.3** (or use the wrapper)

### Quick Build

```bash
# 1. Generate the Gradle wrapper (if gradle-wrapper.jar is missing)
gradle wrapper --gradle-version 8.3

# 2. Build the mod
./gradlew build
```

The compiled JAR will be in `build/libs/echoes-of-the-void-1.0.0.jar`.

### Install

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.20.1
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) mod
3. Copy the JAR file to your `.minecraft/mods/` directory
4. Launch Minecraft with the Fabric profile

---

## 📁 Project Structure

```
src/main/java/com/echoesofvoid/
├── EchoesOfTheVoid.java          # Main mod initializer
├── client/
│   ├── EchoesClient.java         # Client-side setup & effects state
│   ├── model/
│   │   └── SmilingVariantModel.java   # 3D model (elongated Steve)
│   ├── renderer/
│   │   ├── SmilingVariantRenderer.java      # 3D entity renderer
│   │   ├── SmilingVariantEyesFeatureRenderer.java  # Glowing eyes overlay
│   │   └── VoidStalkerRenderer.java         # 2D billboard sprite renderer
│   └── screen/
│       └── ScreamerOverlay.java    # Full-screen screamer HUD overlay
├── effect/
│   ├── ModEffects.java            # Effect registration
│   └── VoidCurseEffect.java       # Void Curse status effect
├── entity/
│   ├── SmilingVariantEntity.java  # 3D monster logic
│   ├── VoidStalkerEntity.java     # 2D monster logic
│   └── ai/
│       ├── SmilingVariantLookGoal.java   # AI: face player, detect stare
│       └── VoidStalkerFollowGoal.java    # AI: peripheral pursuit
├── mixin/
│   ├── GameRendererMixin.java     # Camera shake injection
│   └── ClientPlayerEntityMixin.java  # Client tick hooks
└── registry/
    ├── ModEntities.java           # Entity type registration
    └── ModSounds.java             # Sound event registration

src/main/resources/
├── fabric.mod.json                # Mod metadata
├── echoes_of_void.mixins.json     # Mixin config
└── assets/echoes_of_void/
    ├── textures/
    │   ├── entity/                # Entity textures & screamer images
    │   └── icon.png               # Mod icon
    ├── sounds/                    # .ogg sound files
    ├── sounds.json                # Sound definitions
    └── lang/                      # Language files (en_us, ru_ru)
```

---

## 🎵 Sound Assets

All sounds are procedurally generated OGG Vorbis files:
- `screamer.ogg` — harsh distorted scream
- `static_noise.ogg` — TV static/white noise
- `whisper_footsteps.ogg` — creepy footsteps
- `void_scream.ogg` — deep echoing void wail

To replace with custom sounds, drop `.ogg` files into `src/main/resources/assets/echoes_of_void/sounds/`.

---

## 📜 License

MIT

---

*Crafted with dread. Sleep well.* 🌑
