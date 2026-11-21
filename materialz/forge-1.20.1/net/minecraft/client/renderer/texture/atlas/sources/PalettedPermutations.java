package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PalettedPermutations implements SpriteSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<PalettedPermutations> CODEC = RecordCodecBuilder.create((p_266838_) -> p_266838_.group(Codec.list(ResourceLocation.CODEC).fieldOf("textures").forGetter((p_267300_) -> p_267300_.textures), ResourceLocation.CODEC.fieldOf("palette_key").forGetter((p_266732_) -> p_266732_.paletteKey), Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).fieldOf("permutations").forGetter((p_267234_) -> p_267234_.permutations)).apply(p_266838_, PalettedPermutations::new));
   private final List<ResourceLocation> textures;
   private final Map<String, ResourceLocation> permutations;
   private final ResourceLocation paletteKey;

   private PalettedPermutations(List<ResourceLocation> p_267282_, ResourceLocation p_266681_, Map<String, ResourceLocation> p_266741_) {
      this.textures = p_267282_;
      this.permutations = p_266741_;
      this.paletteKey = p_266681_;
   }

   public void run(ResourceManager p_267219_, SpriteSource.Output p_267250_) {
      Supplier<int[]> $$2 = Suppliers.memoize(() -> loadPaletteEntryFromImage(p_267219_, this.paletteKey));
      Map<String, Supplier<IntUnaryOperator>> $$3 = new HashMap();
      this.permutations.forEach((p_267108_, p_266969_) -> $$3.put(p_267108_, Suppliers.memoize(() -> createPaletteMapping((int[])$$2.get(), loadPaletteEntryFromImage(p_267219_, p_266969_)))));

      for(ResourceLocation $$4 : this.textures) {
         ResourceLocation $$5 = TEXTURE_ID_CONVERTER.idToFile($$4);
         Optional<Resource> $$6 = p_267219_.getResource($$5);
         if ($$6.isEmpty()) {
            LOGGER.warn("Unable to find texture {}", $$5);
         } else {
            LazyLoadedImage $$7 = new LazyLoadedImage($$5, (Resource)$$6.get(), $$3.size());

            for(Map.Entry<String, Supplier<IntUnaryOperator>> $$8 : $$3.entrySet()) {
               ResourceLocation $$9 = $$4.withSuffix("_" + (String)$$8.getKey());
               p_267250_.add($$9, (SpriteSource.SpriteSupplier)(new PalettedSpriteSupplier($$7, (Supplier)$$8.getValue(), $$9)));
            }
         }
      }

   }

   private static IntUnaryOperator createPaletteMapping(int[] p_266839_, int[] p_266776_) {
      if (p_266776_.length != p_266839_.length) {
         LOGGER.warn("Palette mapping has different sizes: {} and {}", p_266839_.length, p_266776_.length);
         throw new IllegalArgumentException();
      } else {
         Int2IntMap $$2 = new Int2IntOpenHashMap(p_266776_.length);

         for(int $$3 = 0; $$3 < p_266839_.length; ++$$3) {
            int $$4 = p_266839_[$$3];
            if (FastColor.ABGR32.alpha($$4) != 0) {
               $$2.put(FastColor.ABGR32.transparent($$4), p_266776_[$$3]);
            }
         }

         return (p_267899_) -> {
            int $$2 = FastColor.ABGR32.alpha(p_267899_);
            if ($$2 == 0) {
               return p_267899_;
            } else {
               int $$3 = FastColor.ABGR32.transparent(p_267899_);
               int $$4 = $$2.getOrDefault($$3, FastColor.ABGR32.opaque($$3));
               int $$5 = FastColor.ABGR32.alpha($$4);
               return FastColor.ABGR32.color($$2 * $$5 / 255, $$4);
            }
         };
      }
   }

   public static int[] loadPaletteEntryFromImage(ResourceManager p_267184_, ResourceLocation p_267059_) {
      Optional<Resource> $$2 = p_267184_.getResource(TEXTURE_ID_CONVERTER.idToFile(p_267059_));
      if ($$2.isEmpty()) {
         LOGGER.error("Failed to load palette image {}", p_267059_);
         throw new IllegalArgumentException();
      } else {
         try {
            InputStream $$3 = ((Resource)$$2.get()).open();

            int[] var5;
            try (NativeImage $$4 = NativeImage.read($$3)) {
               var5 = $$4.getPixelsRGBA();
            } catch (Throwable var10) {
               if ($$3 != null) {
                  try {
                     $$3.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if ($$3 != null) {
               $$3.close();
            }

            return var5;
         } catch (Exception $$5) {
            LOGGER.error("Couldn't load texture {}", p_267059_, $$5);
            throw new IllegalArgumentException();
         }
      }
   }

   public SpriteSourceType type() {
      return SpriteSources.PALETTED_PERMUTATIONS;
   }

   @OnlyIn(Dist.CLIENT)
   static record PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) implements SpriteSource.SpriteSupplier {
      PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) {
         this.baseImage = baseImage;
         this.palette = palette;
         this.permutationLocation = permutationLocation;
      }

      @Nullable
      public SpriteContents get() {
         SpriteContents var2;
         try {
            NativeImage $$0 = this.baseImage.get().mappedCopy((IntUnaryOperator)this.palette.get());
            var2 = new SpriteContents(this.permutationLocation, new FrameSize($$0.getWidth(), $$0.getHeight()), $$0, AnimationMetadataSection.EMPTY);
            return var2;
         } catch (IllegalArgumentException | IOException $$1) {
            PalettedPermutations.LOGGER.error("unable to apply palette to {}", this.permutationLocation, $$1);
            var2 = null;
         } finally {
            this.baseImage.release();
         }

         return var2;
      }

      public void discard() {
         this.baseImage.release();
      }

      // $FF: synthetic method
      @Nullable
      public Object get() {
         return this.get();
      }
   }
}
