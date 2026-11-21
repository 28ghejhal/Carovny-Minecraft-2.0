package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.WorldVersion;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class HashCache {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String HEADER_MARKER = "// ";
   private final Path rootDir;
   private final Path cacheDir;
   private final String versionId;
   private final Map<String, ProviderCache> caches;
   private final Map<String, ProviderCache> originalCaches;
   private final Set<String> cachesToWrite = new HashSet();
   private final Set<Path> cachePaths = new HashSet();
   private final int initialCount;
   private int writes;

   private Path getProviderCachePath(String p_254395_) {
      return this.cacheDir.resolve(Hashing.sha1().hashString(p_254395_, StandardCharsets.UTF_8).toString());
   }

   public HashCache(Path p_236087_, Collection<String> p_253748_, WorldVersion p_236089_) throws IOException {
      this.versionId = p_236089_.getName();
      this.rootDir = p_236087_;
      this.cacheDir = p_236087_.resolve(".cache");
      Files.createDirectories(this.cacheDir);
      Map<String, ProviderCache> map = new HashMap();
      int i = 0;

      for(String s : p_253748_) {
         Path path = this.getProviderCachePath(s);
         this.cachePaths.add(path);
         ProviderCache hashcache$providercache = readCache(p_236087_, path);
         map.put(s, hashcache$providercache);
         i += hashcache$providercache.count();
      }

      this.caches = map;
      this.originalCaches = Map.copyOf(this.caches);
      this.initialCount = i;
   }

   private static ProviderCache readCache(Path p_236093_, Path p_236094_) {
      if (Files.isReadable(p_236094_)) {
         try {
            return HashCache.ProviderCache.load(p_236093_, p_236094_);
         } catch (Exception exception) {
            LOGGER.warn("Failed to parse cache {}, discarding", p_236094_, exception);
         }
      }

      return new ProviderCache("unknown", ImmutableMap.of());
   }

   public boolean shouldRunInThisVersion(String p_254319_) {
      ProviderCache hashcache$providercache = (ProviderCache)this.caches.get(p_254319_);
      return hashcache$providercache == null || !hashcache$providercache.version.equals(this.versionId);
   }

   public CompletableFuture<UpdateResult> generateUpdate(String p_253944_, UpdateFunction p_254321_) {
      ProviderCache hashcache$providercache = (ProviderCache)this.caches.get(p_253944_);
      if (hashcache$providercache == null) {
         throw new IllegalStateException("Provider not registered: " + p_253944_);
      } else {
         CacheUpdater hashcache$cacheupdater = new CacheUpdater(p_253944_, this.versionId, hashcache$providercache);
         return p_254321_.update(hashcache$cacheupdater).thenApply((p_253376_) -> hashcache$cacheupdater.close());
      }
   }

   public void applyUpdate(UpdateResult p_253725_) {
      this.caches.put(p_253725_.providerId(), p_253725_.cache());
      this.cachesToWrite.add(p_253725_.providerId());
      this.writes += p_253725_.writes();
   }

   public void purgeStaleAndWrite() throws IOException {
      Set<Path> set = new HashSet();
      this.caches.forEach((p_253378_, p_253379_) -> {
         if (this.cachesToWrite.contains(p_253378_)) {
            Path path = this.getProviderCachePath(p_253378_);
            if (!p_253379_.equals(this.originalCaches.get(p_253378_)) || !Files.exists(path, new LinkOption[0])) {
               Path var10001 = this.rootDir;
               String var10003 = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
               p_253379_.save(var10001, path, var10003 + "\t" + p_253378_);
            }
         }

         set.addAll(p_253379_.data().keySet());
      });
      set.add(this.rootDir.resolve("version.json"));
      MutableInt mutableint = new MutableInt();
      MutableInt mutableint1 = new MutableInt();
      Stream<Path> stream = Files.walk(this.rootDir);

      try {
         stream.forEach((p_236106_) -> {
            if (!Files.isDirectory(p_236106_, new LinkOption[0]) && !this.cachePaths.contains(p_236106_)) {
               mutableint.increment();
               if (!set.contains(p_236106_)) {
                  try {
                     Files.delete(p_236106_);
                  } catch (IOException ioexception) {
                     LOGGER.warn("Failed to delete file {}", p_236106_, ioexception);
                  }

                  mutableint1.increment();
               }
            }

         });
      } catch (Throwable var8) {
         if (stream != null) {
            try {
               stream.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stream != null) {
         stream.close();
      }

      LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{mutableint, this.initialCount, set.size(), mutableint1, this.writes});
   }

   class CacheUpdater implements CachedOutput {
      private final String provider;
      private final ProviderCache oldCache;
      private final ProviderCacheBuilder newCache;
      private final AtomicInteger writes = new AtomicInteger();
      private volatile boolean closed;

      CacheUpdater(String p_253971_, String p_254002_, ProviderCache p_254244_) {
         this.provider = p_253971_;
         this.oldCache = p_254244_;
         this.newCache = new ProviderCacheBuilder(p_254002_);
      }

      private boolean shouldWrite(Path p_236120_, HashCode p_236121_) {
         return !Objects.equals(this.oldCache.get(p_236120_), p_236121_) || !Files.exists(p_236120_, new LinkOption[0]);
      }

      public void writeIfNeeded(Path p_236123_, byte[] p_236124_, HashCode p_236125_) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
         } else {
            if (this.shouldWrite(p_236123_, p_236125_)) {
               this.writes.incrementAndGet();
               Files.createDirectories(p_236123_.getParent());
               Files.write(p_236123_, p_236124_, new OpenOption[0]);
            }

            this.newCache.put(p_236123_, p_236125_);
         }
      }

      public UpdateResult close() {
         this.closed = true;
         return new UpdateResult(this.provider, this.newCache.build(), this.writes.get());
      }
   }

   static record ProviderCache(String version, ImmutableMap<Path, HashCode> data) {
      @Nullable
      public HashCode get(Path p_236135_) {
         return (HashCode)this.data.get(p_236135_);
      }

      public int count() {
         return this.data.size();
      }

      public static ProviderCache load(Path p_236140_, Path p_236141_) throws IOException {
         BufferedReader bufferedreader = Files.newBufferedReader(p_236141_, StandardCharsets.UTF_8);

         ProviderCache var7;
         try {
            String s = bufferedreader.readLine();
            if (!s.startsWith("// ")) {
               throw new IllegalStateException("Missing cache file header");
            }

            String[] astring = s.substring("// ".length()).split("\t", 2);
            String s1 = astring[0];
            ImmutableMap.Builder<Path, HashCode> builder = ImmutableMap.builder();
            bufferedreader.lines().forEach((p_253382_) -> {
               int i = p_253382_.indexOf(32);
               builder.put(p_236140_.resolve(p_253382_.substring(i + 1)), HashCode.fromString(p_253382_.substring(0, i)));
            });
            var7 = new ProviderCache(s1, builder.build());
         } catch (Throwable var9) {
            if (bufferedreader != null) {
               try {
                  bufferedreader.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (bufferedreader != null) {
            bufferedreader.close();
         }

         return var7;
      }

      public void save(Path p_236143_, Path p_236144_, String p_236145_) {
         try {
            BufferedWriter bufferedwriter = Files.newBufferedWriter(p_236144_, StandardCharsets.UTF_8);

            try {
               bufferedwriter.write("// ");
               bufferedwriter.write(this.version);
               bufferedwriter.write(9);
               bufferedwriter.write(p_236145_);
               bufferedwriter.newLine();

               for(Map.Entry<Path, HashCode> entry : this.data.entrySet().stream().sorted(Entry.comparingByKey()).toList()) {
                  bufferedwriter.write(((HashCode)entry.getValue()).toString());
                  bufferedwriter.write(32);
                  bufferedwriter.write(p_236143_.relativize((Path)entry.getKey()).toString().replace("\\", "/"));
                  bufferedwriter.newLine();
               }
            } catch (Throwable var8) {
               if (bufferedwriter != null) {
                  try {
                     bufferedwriter.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (bufferedwriter != null) {
               bufferedwriter.close();
            }
         } catch (IOException ioexception) {
            HashCache.LOGGER.warn("Unable write cachefile {}: {}", p_236144_, ioexception);
         }

      }
   }

   static record ProviderCacheBuilder(String version, ConcurrentMap<Path, HashCode> data) {
      ProviderCacheBuilder(String p_254186_) {
         this(p_254186_, new ConcurrentHashMap());
      }

      public void put(Path p_254121_, HashCode p_254288_) {
         this.data.put(p_254121_, p_254288_);
      }

      public ProviderCache build() {
         return new ProviderCache(this.version, ImmutableMap.copyOf(this.data));
      }
   }

   public static record UpdateResult(String providerId, ProviderCache cache, int writes) {
   }

   @FunctionalInterface
   public interface UpdateFunction {
      CompletableFuture<?> update(CachedOutput var1);
   }
}
