package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;

public class VanillaPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BuiltInMetadata metadata;
   private final Set<String> namespaces;
   private final List<Path> rootPaths;
   private final Map<PackType, List<Path>> pathsForType;

   VanillaPackResources(BuiltInMetadata p_249743_, Set<String> p_250468_, List<Path> p_248798_, Map<PackType, List<Path>> p_251106_) {
      this.metadata = p_249743_;
      this.namespaces = p_250468_;
      this.rootPaths = p_248798_;
      this.pathsForType = p_251106_;
   }

   @Nullable
   public IoSupplier<InputStream> getRootResource(String... p_250530_) {
      FileUtil.validatePath(p_250530_);
      List<String> $$1 = List.of(p_250530_);

      for(Path $$2 : this.rootPaths) {
         Path $$3 = FileUtil.resolvePath($$2, $$1);
         if (Files.exists($$3, new LinkOption[0]) && PathPackResources.validatePath($$3)) {
            return IoSupplier.create($$3);
         }
      }

      return null;
   }

   public void listRawPaths(PackType p_252103_, ResourceLocation p_250441_, Consumer<Path> p_251968_) {
      FileUtil.decomposePath(p_250441_.getPath()).get().ifLeft((p_248238_) -> {
         String $$4 = p_250441_.getNamespace();

         for(Path $$5 : (List)this.pathsForType.get(p_252103_)) {
            Path $$6 = $$5.resolve($$4);
            p_251968_.accept(FileUtil.resolvePath($$6, p_248238_));
         }

      }).ifRight((p_248232_) -> LOGGER.error("Invalid path {}: {}", p_250441_, p_248232_.message()));
   }

   public void listResources(PackType p_248974_, String p_248703_, String p_250848_, PackResources.ResourceOutput p_249668_) {
      FileUtil.decomposePath(p_250848_).get().ifLeft((p_248228_) -> {
         List<Path> $$4 = (List)this.pathsForType.get(p_248974_);
         int $$5 = $$4.size();
         if ($$5 == 1) {
            getResources(p_249668_, p_248703_, (Path)$$4.get(0), p_248228_);
         } else if ($$5 > 1) {
            Map<ResourceLocation, IoSupplier<InputStream>> $$6 = new HashMap();

            for(int $$7 = 0; $$7 < $$5 - 1; ++$$7) {
               Objects.requireNonNull($$6);
               getResources($$6::putIfAbsent, p_248703_, (Path)$$4.get($$7), p_248228_);
            }

            Path $$8 = (Path)$$4.get($$5 - 1);
            if ($$6.isEmpty()) {
               getResources(p_249668_, p_248703_, $$8, p_248228_);
            } else {
               Objects.requireNonNull($$6);
               getResources($$6::putIfAbsent, p_248703_, $$8, p_248228_);
               $$6.forEach(p_249668_);
            }
         }

      }).ifRight((p_248234_) -> LOGGER.error("Invalid path {}: {}", p_250848_, p_248234_.message()));
   }

   private static void getResources(PackResources.ResourceOutput p_249662_, String p_251249_, Path p_251290_, List<String> p_250451_) {
      Path $$4 = p_251290_.resolve(p_251249_);
      PathPackResources.listPath(p_251249_, $$4, p_250451_, p_249662_);
   }

   @Nullable
   public IoSupplier<InputStream> getResource(PackType p_250512_, ResourceLocation p_251554_) {
      return (IoSupplier)FileUtil.decomposePath(p_251554_.getPath()).get().map((p_248224_) -> {
         String $$3 = p_251554_.getNamespace();

         for(Path $$4 : (List)this.pathsForType.get(p_250512_)) {
            Path $$5 = FileUtil.resolvePath($$4.resolve($$3), p_248224_);
            if (Files.exists($$5, new LinkOption[0]) && PathPackResources.validatePath($$5)) {
               return IoSupplier.create($$5);
            }
         }

         return null;
      }, (p_248230_) -> {
         LOGGER.error("Invalid path {}: {}", p_251554_, p_248230_.message());
         return null;
      });
   }

   public Set<String> getNamespaces(PackType p_10322_) {
      return this.namespaces;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10333_) {
      IoSupplier<InputStream> $$1 = this.getRootResource("pack.mcmeta");
      if ($$1 != null) {
         try {
            InputStream $$2 = $$1.get();

            Object var5;
            label54: {
               try {
                  T $$3 = (T)AbstractPackResources.getMetadataFromStream(p_10333_, $$2);
                  if ($$3 != null) {
                     var5 = $$3;
                     break label54;
                  }
               } catch (Throwable var7) {
                  if ($$2 != null) {
                     try {
                        $$2.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if ($$2 != null) {
                  $$2.close();
               }

               return (T)this.metadata.get(p_10333_);
            }

            if ($$2 != null) {
               $$2.close();
            }

            return (T)var5;
         } catch (IOException var8) {
         }
      }

      return (T)this.metadata.get(p_10333_);
   }

   public String packId() {
      return "vanilla";
   }

   public boolean isBuiltin() {
      return true;
   }

   public void close() {
   }

   public ResourceProvider asProvider() {
      return (p_248239_) -> Optional.ofNullable(this.getResource(PackType.CLIENT_RESOURCES, p_248239_)).map((p_248221_) -> new Resource(this, p_248221_));
   }
}
