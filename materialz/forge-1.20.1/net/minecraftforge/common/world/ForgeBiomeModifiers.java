package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeBiomeModifiers {
   private ForgeBiomeModifiers() {
   }

   public static record AddFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier {
      public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
         if (phase == BiomeModifier.Phase.ADD && this.biomes.contains(biome)) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.features.forEach((holder) -> generationSettings.addFeature(this.step, holder));
         }

      }

      public Codec<? extends BiomeModifier> codec() {
         return ForgeMod.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
      }
   }

   public static record RemoveFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<GenerationStep.Decoration> steps) implements BiomeModifier {
      public static RemoveFeaturesBiomeModifier allSteps(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) {
         return new RemoveFeaturesBiomeModifier(biomes, features, EnumSet.allOf(GenerationStep.Decoration.class));
      }

      public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
         if (phase == BiomeModifier.Phase.REMOVE && this.biomes.contains(biome)) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();

            for(GenerationStep.Decoration step : this.steps) {
               List var10000 = generationSettings.getFeatures(step);
               HolderSet var10001 = this.features;
               Objects.requireNonNull(var10001);
               var10000.removeIf(var10001::contains);
            }
         }

      }

      public Codec<? extends BiomeModifier> codec() {
         return ForgeMod.REMOVE_FEATURES_BIOME_MODIFIER_TYPE.get();
      }
   }

   public static record AddSpawnsBiomeModifier(HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier {
      public static AddSpawnsBiomeModifier singleSpawn(HolderSet<Biome> biomes, MobSpawnSettings.SpawnerData spawner) {
         return new AddSpawnsBiomeModifier(biomes, List.of(spawner));
      }

      public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
         if (phase == BiomeModifier.Phase.ADD && this.biomes.contains(biome)) {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();

            for(MobSpawnSettings.SpawnerData spawner : this.spawners) {
               EntityType<?> type = spawner.type;
               spawns.addSpawn(type.getCategory(), spawner);
            }
         }

      }

      public Codec<? extends BiomeModifier> codec() {
         return ForgeMod.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
      }
   }

   public static record RemoveSpawnsBiomeModifier(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements BiomeModifier {
      public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
         if (phase == BiomeModifier.Phase.REMOVE && this.biomes.contains(biome)) {
            MobSpawnSettingsBuilder spawnBuilder = builder.getMobSpawnSettings();

            for(MobCategory category : MobCategory.values()) {
               List<MobSpawnSettings.SpawnerData> spawns = spawnBuilder.getSpawner(category);
               spawns.removeIf((spawnerData) -> this.entityTypes.contains((Holder)ForgeRegistries.ENTITY_TYPES.getHolder(spawnerData.type).get()));
            }
         }

      }

      public Codec<? extends BiomeModifier> codec() {
         return ForgeMod.REMOVE_SPAWNS_BIOME_MODIFIER_TYPE.get();
      }
   }
}
