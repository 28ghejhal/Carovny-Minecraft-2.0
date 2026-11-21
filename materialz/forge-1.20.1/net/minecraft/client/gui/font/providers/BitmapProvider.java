package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BitmapProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private final NativeImage image;
   private final CodepointMap<Glyph> glyphs;

   BitmapProvider(NativeImage p_285380_, CodepointMap<Glyph> p_285445_) {
      this.image = p_285380_;
      this.glyphs = p_285445_;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public GlyphInfo getGlyph(int p_232638_) {
      return this.glyphs.get(p_232638_);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   @OnlyIn(Dist.CLIENT)
   public static record Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) implements GlyphProviderDefinition {
      private static final Codec<int[][]> CODEPOINT_GRID_CODEC;
      public static final MapCodec<Definition> CODEC;

      public Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) {
         this.file = file;
         this.height = height;
         this.ascent = ascent;
         this.codepointGrid = codepointGrid;
      }

      private static DataResult<int[][]> validateDimensions(int[][] p_286348_) {
         int $$1 = p_286348_.length;
         if ($$1 == 0) {
            return DataResult.error(() -> "Expected to find data in codepoint grid");
         } else {
            int[] $$2 = p_286348_[0];
            int $$3 = $$2.length;
            if ($$3 == 0) {
               return DataResult.error(() -> "Expected to find data in codepoint grid");
            } else {
               for(int $$4 = 1; $$4 < $$1; ++$$4) {
                  int[] $$5 = p_286348_[$$4];
                  if ($$5.length != $$3) {
                     return DataResult.error(() -> "Lines in codepoint grid have to be the same length (found: " + $$5.length + " codepoints, expected: " + $$3 + "), pad with \\u0000");
                  }
               }

               return DataResult.success(p_286348_);
            }
         }
      }

      private static DataResult<Definition> validate(Definition p_286662_) {
         return p_286662_.ascent > p_286662_.height ? DataResult.error(() -> "Ascent " + p_286662_.ascent + " higher than height " + p_286662_.height) : DataResult.success(p_286662_);
      }

      public GlyphProviderType type() {
         return GlyphProviderType.BITMAP;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(ResourceManager p_286694_) throws IOException {
         ResourceLocation $$1 = this.file.withPrefix("textures/");
         InputStream $$2 = p_286694_.open($$1);

         BitmapProvider var22;
         try {
            NativeImage $$3 = NativeImage.read(NativeImage.Format.RGBA, $$2);
            int $$4 = $$3.getWidth();
            int $$5 = $$3.getHeight();
            int $$6 = $$4 / this.codepointGrid[0].length;
            int $$7 = $$5 / this.codepointGrid.length;
            float $$8 = (float)this.height / (float)$$7;
            CodepointMap<Glyph> $$9 = new CodepointMap<Glyph>((p_286343_) -> new Glyph[p_286343_], (p_286759_) -> new Glyph[p_286759_][]);

            for(int $$10 = 0; $$10 < this.codepointGrid.length; ++$$10) {
               int $$11 = 0;

               for(int $$12 : this.codepointGrid[$$10]) {
                  int $$13 = $$11++;
                  if ($$12 != 0) {
                     int $$14 = this.getActualGlyphWidth($$3, $$6, $$7, $$13, $$10);
                     Glyph $$15 = $$9.put($$12, new Glyph($$8, $$3, $$13 * $$6, $$10 * $$7, $$6, $$7, (int)((double)0.5F + (double)((float)$$14 * $$8)) + 1, this.ascent));
                     if ($$15 != null) {
                        BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString($$12), $$1);
                     }
                  }
               }
            }

            var22 = new BitmapProvider($$3, $$9);
         } catch (Throwable var21) {
            if ($$2 != null) {
               try {
                  $$2.close();
               } catch (Throwable var20) {
                  var21.addSuppressed(var20);
               }
            }

            throw var21;
         }

         if ($$2 != null) {
            $$2.close();
         }

         return var22;
      }

      private int getActualGlyphWidth(NativeImage p_286449_, int p_286656_, int p_286554_, int p_286657_, int p_286307_) {
         int $$5;
         for($$5 = p_286656_ - 1; $$5 >= 0; --$$5) {
            int $$6 = p_286657_ * p_286656_ + $$5;

            for(int $$7 = 0; $$7 < p_286554_; ++$$7) {
               int $$8 = p_286307_ * p_286554_ + $$7;
               if (p_286449_.getLuminanceOrAlpha($$6, $$8) != 0) {
                  return $$5 + 1;
               }
            }
         }

         return $$5 + 1;
      }

      static {
         CODEPOINT_GRID_CODEC = ExtraCodecs.validate(Codec.STRING.listOf().xmap((p_286900_) -> {
            int $$1 = p_286900_.size();
            int[][] $$2 = new int[$$1][];

            for(int $$3 = 0; $$3 < $$1; ++$$3) {
               $$2[$$3] = ((String)p_286900_.get($$3)).codePoints().toArray();
            }

            return $$2;
         }, (p_286828_) -> {
            List<String> $$1 = new ArrayList(p_286828_.length);

            for(int[] $$2 : p_286828_) {
               $$1.add(new String($$2, 0, $$2.length));
            }

            return $$1;
         }), Definition::validateDimensions);
         CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec((p_286905_) -> p_286905_.group(ResourceLocation.CODEC.fieldOf("file").forGetter(Definition::file), Codec.INT.optionalFieldOf("height", 8).forGetter(Definition::height), Codec.INT.fieldOf("ascent").forGetter(Definition::ascent), CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(Definition::codepointGrid)).apply(p_286905_, Definition::new)), Definition::validate);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) implements GlyphInfo {
      Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) {
         this.scale = scale;
         this.image = image;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.width = width;
         this.height = height;
         this.advance = advance;
         this.ascent = ascent;
      }

      public float getAdvance() {
         return (float)this.advance;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_232640_) {
         return (BakedGlyph)p_232640_.apply(new SheetGlyphInfo() {
            // $FF: synthetic field
            final BitmapProvider.Glyph this$0;

            {
               this.this$0 = p_232655_;
            }

            public float getOversample() {
               return 1.0F / this.this$0.scale;
            }

            public int getPixelWidth() {
               return this.this$0.width;
            }

            public int getPixelHeight() {
               return this.this$0.height;
            }

            public float getBearingY() {
               return SheetGlyphInfo.super.getBearingY() + 7.0F - (float)this.this$0.ascent;
            }

            public void upload(int p_232658_, int p_232659_) {
               this.this$0.image.upload(0, p_232658_, p_232659_, this.this$0.offsetX, this.this$0.offsetY, this.this$0.width, this.this$0.height, false, false);
            }

            public boolean isColored() {
               return this.this$0.image.format().components() > 1;
            }
         });
      }
   }
}
