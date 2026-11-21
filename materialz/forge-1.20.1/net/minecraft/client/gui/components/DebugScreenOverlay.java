package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugScreenOverlay {
   private static final int COLOR_GREY = 14737632;
   private static final int MARGIN_RIGHT = 2;
   private static final int MARGIN_LEFT = 2;
   private static final int MARGIN_TOP = 2;
   private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = (Map)Util.make(new EnumMap(Heightmap.Types.class), (p_94070_) -> {
      p_94070_.put(Heightmap.Types.WORLD_SURFACE_WG, "SW");
      p_94070_.put(Heightmap.Types.WORLD_SURFACE, "S");
      p_94070_.put(Heightmap.Types.OCEAN_FLOOR_WG, "OW");
      p_94070_.put(Heightmap.Types.OCEAN_FLOOR, "O");
      p_94070_.put(Heightmap.Types.MOTION_BLOCKING, "M");
      p_94070_.put(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML");
   });
   private final Minecraft minecraft;
   private final AllocationRateCalculator allocationRateCalculator;
   private final Font font;
   protected HitResult block;
   protected HitResult liquid;
   @Nullable
   private ChunkPos lastPos;
   @Nullable
   private LevelChunk clientChunk;
   @Nullable
   private CompletableFuture<LevelChunk> serverChunk;
   private static final int RED = -65536;
   private static final int YELLOW = -256;
   private static final int GREEN = -16711936;

   public DebugScreenOverlay(Minecraft p_94039_) {
      this.minecraft = p_94039_;
      this.allocationRateCalculator = new AllocationRateCalculator();
      this.font = p_94039_.font;
   }

   public void clearChunkCache() {
      this.serverChunk = null;
      this.clientChunk = null;
   }

   public void render(GuiGraphics p_281427_) {
      this.minecraft.getProfiler().push("debug");
      Entity entity = this.minecraft.getCameraEntity();
      this.block = entity.pick((double)20.0F, 0.0F, false);
      this.liquid = entity.pick((double)20.0F, 0.0F, true);
      p_281427_.drawManaged(() -> {
         this.drawGameInformation(p_281427_);
         this.drawSystemInformation(p_281427_);
         if (this.minecraft.options.renderFpsChart) {
            int i = p_281427_.guiWidth();
            this.drawChart(p_281427_, this.minecraft.getFrameTimer(), 0, i / 2, true);
            IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
            if (integratedserver != null) {
               this.drawChart(p_281427_, integratedserver.getFrameTimer(), i - Math.min(i / 2, 240), i / 2, false);
            }
         }

      });
      this.minecraft.getProfiler().pop();
   }

   protected void drawGameInformation(GuiGraphics p_281525_) {
      List<String> list = this.getGameInformation();
      list.add("");
      boolean flag = this.minecraft.getSingleplayerServer() != null;
      String var10001 = this.minecraft.options.renderDebugCharts ? "visible" : "hidden";
      list.add("Debug: Pie [shift]: " + var10001 + (flag ? " FPS + TPS" : " FPS") + " [alt]: " + (this.minecraft.options.renderFpsChart ? "visible" : "hidden"));
      list.add("For help: press F3 + Q");
      this.renderLines(p_281525_, list, true);
   }

   protected void drawSystemInformation(GuiGraphics p_281261_) {
      List<String> list = this.getSystemInformation();
      this.renderLines(p_281261_, list, false);
   }

   private void renderLines(GuiGraphics p_286519_, List<String> p_286665_, boolean p_286644_) {
      int i = 9;

      for(int j = 0; j < p_286665_.size(); ++j) {
         String s = (String)p_286665_.get(j);
         if (!Strings.isNullOrEmpty(s)) {
            int k = this.font.width(s);
            int l = p_286644_ ? 2 : p_286519_.guiWidth() - 2 - k;
            int i1 = 2 + i * j;
            p_286519_.fill(l - 1, i1 - 1, l + k + 1, i1 + i - 1, -1873784752);
         }
      }

      for(int j1 = 0; j1 < p_286665_.size(); ++j1) {
         String s1 = (String)p_286665_.get(j1);
         if (!Strings.isNullOrEmpty(s1)) {
            int k1 = this.font.width(s1);
            int l1 = p_286644_ ? 2 : p_286519_.guiWidth() - 2 - k1;
            int i2 = 2 + i * j1;
            p_286519_.drawString(this.font, s1, l1, i2, 14737632, false);
         }
      }

   }

   protected List<String> getGameInformation() {
      IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
      Connection connection = this.minecraft.getConnection().getConnection();
      float f = connection.getAverageSentPackets();
      float f1 = connection.getAverageReceivedPackets();
      String s;
      if (integratedserver != null) {
         s = String.format(Locale.ROOT, "Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedserver.getAverageTickTime(), f, f1);
      } else {
         s = String.format(Locale.ROOT, "\"%s\" server, %.0f tx, %.0f rx", this.minecraft.player.getServerBrand(), f, f1);
      }

      BlockPos blockpos = this.minecraft.getCameraEntity().blockPosition();
      if (this.minecraft.showOnlyReducedInfo()) {
         String[] var42 = new String[9];
         String var46 = SharedConstants.getCurrentVersion().getName();
         var42[0] = "Minecraft " + var46 + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")";
         var42[1] = this.minecraft.fpsString;
         var42[2] = s;
         var42[3] = this.minecraft.levelRenderer.getChunkStatistics();
         var42[4] = this.minecraft.levelRenderer.getEntityStatistics();
         var46 = this.minecraft.particleEngine.countParticles();
         var42[5] = "P: " + var46 + ". T: " + this.minecraft.level.getEntityCount();
         var42[6] = this.minecraft.level.gatherChunkSourceStats();
         var42[7] = "";
         var42[8] = String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15);
         return Lists.newArrayList(var42);
      } else {
         Entity entity = this.minecraft.getCameraEntity();
         Direction direction = entity.getDirection();
         String s1;
         switch (direction) {
            case NORTH -> s1 = "Towards negative Z";
            case SOUTH -> s1 = "Towards positive Z";
            case WEST -> s1 = "Towards negative X";
            case EAST -> s1 = "Towards positive X";
            default -> s1 = "Invalid";
         }

         ChunkPos chunkpos = new ChunkPos(blockpos);
         if (!Objects.equals(this.lastPos, chunkpos)) {
            this.lastPos = chunkpos;
            this.clearChunkCache();
         }

         Level level = this.getLevel();
         LongSet longset = (LongSet)(level instanceof ServerLevel ? ((ServerLevel)level).getForcedChunks() : LongSets.EMPTY_SET);
         String[] var10000 = new String[7];
         String var10003 = SharedConstants.getCurrentVersion().getName();
         var10000[0] = "Minecraft " + var10003 + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + ")";
         var10000[1] = this.minecraft.fpsString;
         var10000[2] = s;
         var10000[3] = this.minecraft.levelRenderer.getChunkStatistics();
         var10000[4] = this.minecraft.levelRenderer.getEntityStatistics();
         var10003 = this.minecraft.particleEngine.countParticles();
         var10000[5] = "P: " + var10003 + ". T: " + this.minecraft.level.getEntityCount();
         var10000[6] = this.minecraft.level.gatherChunkSourceStats();
         List<String> list = Lists.newArrayList(var10000);
         String s2 = this.getServerChunkStats();
         if (s2 != null) {
            list.add(s2);
         }

         String var10001 = String.valueOf(this.minecraft.level.dimension().location());
         list.add(var10001 + " FC: " + longset.size());
         list.add("");
         list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()));
         list.add(String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
         list.add(String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkpos.x, SectionPos.blockToSectionCoord(blockpos.getY()), chunkpos.z, chunkpos.getRegionLocalX(), chunkpos.getRegionLocalZ(), chunkpos.getRegionX(), chunkpos.getRegionZ()));
         list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, s1, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot())));
         LevelChunk levelchunk = this.getClientChunk();
         if (levelchunk.isEmpty()) {
            list.add("Waiting for chunk...");
         } else {
            int i = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockpos, 0);
            int j = this.minecraft.level.getBrightness(LightLayer.SKY, blockpos);
            int k = this.minecraft.level.getBrightness(LightLayer.BLOCK, blockpos);
            list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
            LevelChunk levelchunk1 = this.getServerChunk();
            StringBuilder stringbuilder = new StringBuilder("CH");

            for(Heightmap.Types heightmap$types : Heightmap.Types.values()) {
               if (heightmap$types.sendToClient()) {
                  stringbuilder.append(" ").append((String)HEIGHTMAP_NAMES.get(heightmap$types)).append(": ").append(levelchunk.getHeight(heightmap$types, blockpos.getX(), blockpos.getZ()));
               }
            }

            list.add(stringbuilder.toString());
            stringbuilder.setLength(0);
            stringbuilder.append("SH");

            for(Heightmap.Types heightmap$types1 : Heightmap.Types.values()) {
               if (heightmap$types1.keepAfterWorldgen()) {
                  stringbuilder.append(" ").append((String)HEIGHTMAP_NAMES.get(heightmap$types1)).append(": ");
                  if (levelchunk1 != null) {
                     stringbuilder.append(levelchunk1.getHeight(heightmap$types1, blockpos.getX(), blockpos.getZ()));
                  } else {
                     stringbuilder.append("??");
                  }
               }
            }

            list.add(stringbuilder.toString());
            if (blockpos.getY() >= this.minecraft.level.getMinBuildHeight() && blockpos.getY() < this.minecraft.level.getMaxBuildHeight()) {
               Holder var43 = this.minecraft.level.getBiome(blockpos);
               list.add("Biome: " + printBiome(var43));
               long l = 0L;
               float f2 = 0.0F;
               if (levelchunk1 != null) {
                  f2 = level.getMoonBrightness();
                  l = levelchunk1.getInhabitedTime();
               }

               DifficultyInstance difficultyinstance = new DifficultyInstance(level.getDifficulty(), level.getDayTime(), l, f2);
               list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", difficultyinstance.getEffectiveDifficulty(), difficultyinstance.getSpecialMultiplier(), this.minecraft.level.getDayTime() / 24000L));
            }

            if (levelchunk1 != null && levelchunk1.isOldNoiseGeneration()) {
               list.add("Blending: Old");
            }
         }

         ServerLevel serverlevel = this.getServerLevel();
         if (serverlevel != null) {
            ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
            ChunkGenerator chunkgenerator = serverchunkcache.getGenerator();
            RandomState randomstate = serverchunkcache.randomState();
            chunkgenerator.addDebugScreenInfo(list, randomstate, blockpos);
            Climate.Sampler climate$sampler = randomstate.sampler();
            BiomeSource biomesource = chunkgenerator.getBiomeSource();
            biomesource.addDebugInfo(list, blockpos, climate$sampler);
            NaturalSpawner.SpawnState naturalspawner$spawnstate = serverchunkcache.getLastSpawnState();
            if (naturalspawner$spawnstate != null) {
               Object2IntMap<MobCategory> object2intmap = naturalspawner$spawnstate.getMobCategoryCounts();
               int i1 = naturalspawner$spawnstate.getSpawnableChunkCount();
               list.add("SC: " + i1 + ", " + (String)Stream.of(MobCategory.values()).map((p_94068_) -> {
                  char var10000 = Character.toUpperCase(p_94068_.getName().charAt(0));
                  return var10000 + ": " + object2intmap.getInt(p_94068_);
               }).collect(Collectors.joining(", ")));
            } else {
               list.add("SC: N/A");
            }
         }

         PostChain postchain = this.minecraft.gameRenderer.currentEffect();
         if (postchain != null) {
            list.add("Shader: " + postchain.getName());
         }

         var10001 = this.minecraft.getSoundManager().getDebugString();
         list.add(var10001 + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F)));
         return list;
      }
   }

   private static String printBiome(Holder<Biome> p_205375_) {
      return (String)p_205375_.unwrap().map((p_205377_) -> p_205377_.location().toString(), (p_205367_) -> "[unregistered " + String.valueOf(p_205367_) + "]");
   }

   @Nullable
   private ServerLevel getServerLevel() {
      IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
      return integratedserver != null ? integratedserver.getLevel(this.minecraft.level.dimension()) : null;
   }

   @Nullable
   private String getServerChunkStats() {
      ServerLevel serverlevel = this.getServerLevel();
      return serverlevel != null ? serverlevel.gatherChunkSourceStats() : null;
   }

   private Level getLevel() {
      return (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap((p_288242_) -> Optional.ofNullable(p_288242_.getLevel(this.minecraft.level.dimension()))), this.minecraft.level);
   }

   @Nullable
   private LevelChunk getServerChunk() {
      if (this.serverChunk == null) {
         ServerLevel serverlevel = this.getServerLevel();
         if (serverlevel != null) {
            this.serverChunk = serverlevel.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply((p_205369_) -> (LevelChunk)p_205369_.map((p_205371_) -> (LevelChunk)p_205371_, (p_205363_) -> null));
         }

         if (this.serverChunk == null) {
            this.serverChunk = CompletableFuture.completedFuture(this.getClientChunk());
         }
      }

      return (LevelChunk)this.serverChunk.getNow((LevelChunk)null);
   }

   private LevelChunk getClientChunk() {
      if (this.clientChunk == null) {
         this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
      }

      return this.clientChunk;
   }

   protected List<String> getSystemInformation() {
      long i = Runtime.getRuntime().maxMemory();
      long j = Runtime.getRuntime().totalMemory();
      long k = Runtime.getRuntime().freeMemory();
      long l = j - k;
      List<String> list = Lists.newArrayList(new String[]{String.format(Locale.ROOT, "Java: %s %dbit", System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32), String.format(Locale.ROOT, "Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMegabytes(l), bytesToMegabytes(i)), String.format(Locale.ROOT, "Allocation rate: %03dMB /s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(l))), String.format(Locale.ROOT, "Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMegabytes(j)), "", String.format(Locale.ROOT, "CPU: %s", GlUtil.getCpuInfo()), "", String.format(Locale.ROOT, "Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()), GlUtil.getRenderer(), GlUtil.getOpenGLVersion()});
      if (this.minecraft.showOnlyReducedInfo()) {
         return list;
      } else {
         if (this.block.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)this.block).getBlockPos();
            BlockState blockstate = this.minecraft.level.getBlockState(blockpos);
            list.add("");
            String var10001 = String.valueOf(ChatFormatting.UNDERLINE);
            list.add(var10001 + "Targeted Block: " + blockpos.getX() + ", " + blockpos.getY() + ", " + blockpos.getZ());
            list.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(blockstate.getBlock())));
            UnmodifiableIterator var12 = blockstate.getValues().entrySet().iterator();

            while(var12.hasNext()) {
               Map.Entry<Property<?>, Comparable<?>> entry = (Map.Entry)var12.next();
               list.add(this.getPropertyValueString(entry));
            }

            Stream var10000 = blockstate.getTags().map((p_205365_) -> "#" + String.valueOf(p_205365_.location()));
            Objects.requireNonNull(list);
            var10000.forEach(list::add);
         }

         if (this.liquid.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos1 = ((BlockHitResult)this.liquid).getBlockPos();
            FluidState fluidstate = this.minecraft.level.getFluidState(blockpos1);
            list.add("");
            String var20 = String.valueOf(ChatFormatting.UNDERLINE);
            list.add(var20 + "Targeted Fluid: " + blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ());
            list.add(String.valueOf(BuiltInRegistries.FLUID.getKey(fluidstate.getType())));
            UnmodifiableIterator var17 = fluidstate.getValues().entrySet().iterator();

            while(var17.hasNext()) {
               Map.Entry<Property<?>, Comparable<?>> entry1 = (Map.Entry)var17.next();
               list.add(this.getPropertyValueString(entry1));
            }

            Stream var19 = fluidstate.getTags().map((p_205379_) -> "#" + String.valueOf(p_205379_.location()));
            Objects.requireNonNull(list);
            var19.forEach(list::add);
         }

         Entity entity = this.minecraft.crosshairPickEntity;
         if (entity != null) {
            list.add("");
            list.add(String.valueOf(ChatFormatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())));
            entity.getType().builtInRegistryHolder().tags().forEach((t) -> list.add("#" + String.valueOf(t.location())));
         }

         return list;
      }
   }

   private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> p_94072_) {
      Property<?> property = (Property)p_94072_.getKey();
      Comparable<?> comparable = (Comparable)p_94072_.getValue();
      String s = Util.getPropertyName(property, comparable);
      if (Boolean.TRUE.equals(comparable)) {
         String var10000 = String.valueOf(ChatFormatting.GREEN);
         s = var10000 + s;
      } else if (Boolean.FALSE.equals(comparable)) {
         String var5 = String.valueOf(ChatFormatting.RED);
         s = var5 + s;
      }

      String var6 = property.getName();
      return var6 + ": " + s;
   }

   private void drawChart(GuiGraphics p_282273_, FrameTimer p_281838_, int p_283392_, int p_282726_, boolean p_282824_) {
      int i = p_281838_.getLogStart();
      int j = p_281838_.getLogEnd();
      long[] along = p_281838_.getLog();
      int l = p_283392_;
      int i1 = Math.max(0, along.length - p_282726_);
      int j1 = along.length - i1;
      int $$8 = p_281838_.wrapIndex(i + i1);
      long k1 = 0L;
      int l1 = Integer.MAX_VALUE;
      int i2 = Integer.MIN_VALUE;

      for(int j2 = 0; j2 < j1; ++j2) {
         int k2 = (int)(along[p_281838_.wrapIndex($$8 + j2)] / 1000000L);
         l1 = Math.min(l1, k2);
         i2 = Math.max(i2, k2);
         k1 += (long)k2;
      }

      int j3 = p_282273_.guiHeight();
      p_282273_.fill(RenderType.guiOverlay(), p_283392_, j3 - 60, p_283392_ + j1, j3, -1873784752);

      while($$8 != j) {
         int k3 = p_281838_.scaleSampleTo(along[$$8], p_282824_ ? 30 : 60, p_282824_ ? 60 : 20);
         int l2 = p_282824_ ? 100 : 60;
         int i3 = this.getSampleColor(Mth.clamp(k3, 0, l2), 0, l2 / 2, l2);
         p_282273_.fill(RenderType.guiOverlay(), l, j3 - k3, l + 1, j3, i3);
         ++l;
         $$8 = p_281838_.wrapIndex($$8 + 1);
      }

      if (p_282824_) {
         p_282273_.fill(RenderType.guiOverlay(), p_283392_ + 1, j3 - 30 + 1, p_283392_ + 14, j3 - 30 + 10, -1873784752);
         p_282273_.drawString(this.font, "60 FPS", p_283392_ + 2, j3 - 30 + 2, 14737632, false);
         p_282273_.hLine(RenderType.guiOverlay(), p_283392_, p_283392_ + j1 - 1, j3 - 30, -1);
         p_282273_.fill(RenderType.guiOverlay(), p_283392_ + 1, j3 - 60 + 1, p_283392_ + 14, j3 - 60 + 10, -1873784752);
         p_282273_.drawString(this.font, "30 FPS", p_283392_ + 2, j3 - 60 + 2, 14737632, false);
         p_282273_.hLine(RenderType.guiOverlay(), p_283392_, p_283392_ + j1 - 1, j3 - 60, -1);
      } else {
         p_282273_.fill(RenderType.guiOverlay(), p_283392_ + 1, j3 - 60 + 1, p_283392_ + 14, j3 - 60 + 10, -1873784752);
         p_282273_.drawString(this.font, "20 TPS", p_283392_ + 2, j3 - 60 + 2, 14737632, false);
         p_282273_.hLine(RenderType.guiOverlay(), p_283392_, p_283392_ + j1 - 1, j3 - 60, -1);
      }

      p_282273_.hLine(RenderType.guiOverlay(), p_283392_, p_283392_ + j1 - 1, j3 - 1, -1);
      p_282273_.vLine(RenderType.guiOverlay(), p_283392_, j3 - 60, j3, -1);
      p_282273_.vLine(RenderType.guiOverlay(), p_283392_ + j1 - 1, j3 - 60, j3, -1);
      int l3 = (Integer)this.minecraft.options.framerateLimit().get();
      if (p_282824_ && l3 > 0 && l3 <= 250) {
         p_282273_.hLine(RenderType.guiOverlay(), p_283392_, p_283392_ + j1 - 1, j3 - 1 - (int)((double)1800.0F / (double)l3), -16711681);
      }

      String s1 = l1 + " ms min";
      String s2 = k1 / (long)j1 + " ms avg";
      String s = i2 + " ms max";
      p_282273_.drawString(this.font, s1, p_283392_ + 2, j3 - 60 - 9, 14737632);
      p_282273_.drawCenteredString(this.font, s2, p_283392_ + j1 / 2, j3 - 60 - 9, 14737632);
      p_282273_.drawString(this.font, s, p_283392_ + j1 - this.font.width(s), j3 - 60 - 9, 14737632);
   }

   private int getSampleColor(int p_94046_, int p_94047_, int p_94048_, int p_94049_) {
      return p_94046_ < p_94048_ ? this.colorLerp(-16711936, -256, (float)p_94046_ / (float)p_94048_) : this.colorLerp(-256, -65536, (float)(p_94046_ - p_94048_) / (float)(p_94049_ - p_94048_));
   }

   private int colorLerp(int p_94042_, int p_94043_, float p_94044_) {
      int i = p_94042_ >> 24 & 255;
      int j = p_94042_ >> 16 & 255;
      int k = p_94042_ >> 8 & 255;
      int l = p_94042_ & 255;
      int i1 = p_94043_ >> 24 & 255;
      int j1 = p_94043_ >> 16 & 255;
      int k1 = p_94043_ >> 8 & 255;
      int l1 = p_94043_ & 255;
      int i2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)i, (float)i1), 0, 255);
      int j2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)j, (float)j1), 0, 255);
      int k2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)k, (float)k1), 0, 255);
      int l2 = Mth.clamp((int)Mth.lerp(p_94044_, (float)l, (float)l1), 0, 255);
      return i2 << 24 | j2 << 16 | k2 << 8 | l2;
   }

   private static long bytesToMegabytes(long p_94051_) {
      return p_94051_ / 1024L / 1024L;
   }

   @OnlyIn(Dist.CLIENT)
   static class AllocationRateCalculator {
      private static final int UPDATE_INTERVAL_MS = 500;
      private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
      private long lastTime = 0L;
      private long lastHeapUsage = -1L;
      private long lastGcCounts = -1L;
      private long lastRate = 0L;

      long bytesAllocatedPerSecond(long p_232517_) {
         long i = System.currentTimeMillis();
         if (i - this.lastTime < 500L) {
            return this.lastRate;
         } else {
            long j = gcCounts();
            if (this.lastTime != 0L && j == this.lastGcCounts) {
               double d0 = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(i - this.lastTime);
               long k = p_232517_ - this.lastHeapUsage;
               this.lastRate = Math.round((double)k * d0);
            }

            this.lastTime = i;
            this.lastHeapUsage = p_232517_;
            this.lastGcCounts = j;
            return this.lastRate;
         }
      }

      private static long gcCounts() {
         long i = 0L;

         for(GarbageCollectorMXBean garbagecollectormxbean : GC_MBEANS) {
            i += garbagecollectormxbean.getCollectionCount();
         }

         return i;
      }
   }
}
