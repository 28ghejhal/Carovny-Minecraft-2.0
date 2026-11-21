package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureCountTracker {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LoadingCache<ServerLevel, LevelData> data;

   public static void chunkDecorated(ServerLevel p_190882_) {
      try {
         ((LevelData)data.get(p_190882_)).chunksWithFeatures().increment();
      } catch (Exception $$1) {
         LOGGER.error("Failed to increment chunk count", $$1);
      }

   }

   public static void featurePlaced(ServerLevel p_190884_, ConfiguredFeature<?, ?> p_190885_, Optional<PlacedFeature> p_190886_) {
      try {
         ((LevelData)data.get(p_190884_)).featureData().computeInt(new FeatureData(p_190885_, p_190886_), (p_190891_, p_190892_) -> p_190892_ == null ? 1 : p_190892_ + 1);
      } catch (Exception $$3) {
         LOGGER.error("Failed to increment feature count", $$3);
      }

   }

   public static void clearCounts() {
      data.invalidateAll();
      LOGGER.debug("Cleared feature counts");
   }

   public static void logCounts() {
      LOGGER.debug("Logging feature counts:");
      data.asMap().forEach((p_190888_, p_190889_) -> {
         String $$2 = p_190888_.dimension().location().toString();
         boolean $$3 = p_190888_.getServer().isRunning();
         Registry<PlacedFeature> $$4 = p_190888_.registryAccess().<PlacedFeature>registryOrThrow(Registries.PLACED_FEATURE);
         String $$5 = ($$3 ? "running" : "dead") + " " + $$2;
         Integer $$6 = p_190889_.chunksWithFeatures().getValue();
         LOGGER.debug($$5 + " total_chunks: " + $$6);
         p_190889_.featureData().forEach((p_190897_, p_190898_) -> {
            Logger var10000 = LOGGER;
            String var10002 = String.format(Locale.ROOT, "%10d ", p_190898_);
            String var10003 = String.format(Locale.ROOT, "%10f ", (double)p_190898_ / (double)$$6);
            Optional var10004 = p_190897_.topFeature();
            Objects.requireNonNull($$4);
            var10000.debug($$5 + " " + var10002 + var10003 + var10004.flatMap($$4::getResourceKey).map(ResourceKey::location) + " " + p_190897_.feature().feature() + " " + p_190897_.feature());
         });
      });
   }

   static {
      data = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader<ServerLevel, LevelData>() {
         public LevelData load(ServerLevel p_190902_) {
            return new LevelData(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((ServerLevel)var1);
         }
      });
   }

   static record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
      FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
         this.feature = feature;
         this.topFeature = topFeature;
      }
   }

   static record LevelData(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
      LevelData(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
         this.featureData = featureData;
         this.chunksWithFeatures = chunksWithFeatures;
      }
   }
}
