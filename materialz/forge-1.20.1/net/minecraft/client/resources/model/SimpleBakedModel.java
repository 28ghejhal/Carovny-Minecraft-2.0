package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements BakedModel {
   protected final List<BakedQuad> unculledFaces;
   protected final Map<Direction, List<BakedQuad>> culledFaces;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;
   protected final ChunkRenderTypeSet blockRenderTypes;
   protected final ChunkRenderTypeSet blockRenderTypesFast;
   protected final List<RenderType> itemRenderTypes;
   protected final List<RenderType> itemRenderTypesFast;
   protected final List<RenderType> fabulousItemRenderTypes;
   protected final boolean isRenderingCutout;
   private static final ChunkRenderTypeSet SOLID_BLOCK = ChunkRenderTypeSet.of(RenderType.solid());
   private static final List<RenderType> SOLID_BLOCK_ITEM = List.of(RenderType.solid());

   /** @deprecated */
   @Deprecated
   public SimpleBakedModel(List<BakedQuad> p_119489_, Map<Direction, List<BakedQuad>> p_119490_, boolean p_119491_, boolean p_119492_, boolean p_119493_, TextureAtlasSprite p_119494_, ItemTransforms p_119495_, ItemOverrides p_119496_) {
      this(p_119489_, p_119490_, p_119491_, p_119492_, p_119493_, p_119494_, p_119495_, p_119496_, RenderTypeGroup.EMPTY);
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true,
      since = "1.21.4"
   )
   public SimpleBakedModel(List<BakedQuad> p_119489_, Map<Direction, List<BakedQuad>> p_119490_, boolean p_119491_, boolean p_119492_, boolean p_119493_, TextureAtlasSprite p_119494_, ItemTransforms p_119495_, ItemOverrides p_119496_, RenderTypeGroup renderTypes) {
      this(p_119489_, p_119490_, p_119491_, p_119492_, p_119493_, p_119494_, p_119495_, p_119496_, renderTypes, RenderTypeGroup.EMPTY);
   }

   public SimpleBakedModel(List<BakedQuad> p_119489_, Map<Direction, List<BakedQuad>> p_119490_, boolean p_119491_, boolean p_119492_, boolean p_119493_, TextureAtlasSprite p_119494_, ItemTransforms p_119495_, ItemOverrides p_119496_, RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast) {
      this.unculledFaces = p_119489_;
      this.culledFaces = p_119490_;
      this.hasAmbientOcclusion = p_119491_;
      this.isGui3d = p_119493_;
      this.usesBlockLight = p_119492_;
      this.particleIcon = p_119494_;
      this.transforms = p_119495_;
      this.overrides = p_119496_;
      boolean hasRenderTypes = renderTypes != null && !renderTypes.isEmpty();
      boolean hasRenderTypesFast = renderTypesFast != null && !renderTypesFast.isEmpty();
      this.blockRenderTypes = hasRenderTypes ? ChunkRenderTypeSet.of(renderTypes.block()) : null;
      this.blockRenderTypesFast = hasRenderTypesFast ? ChunkRenderTypeSet.of(renderTypesFast.block()) : null;
      this.itemRenderTypes = hasRenderTypes ? List.of(renderTypes.entity()) : null;
      this.itemRenderTypesFast = hasRenderTypesFast ? List.of(renderTypesFast.entity()) : null;
      this.fabulousItemRenderTypes = hasRenderTypes ? List.of(renderTypes.entityFabulous()) : null;
      this.isRenderingCutout = hasRenderTypes && (renderTypes.block() == RenderType.cutout() || renderTypes.block() == RenderType.cutoutMipped());
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_235054_, @Nullable Direction p_235055_, RandomSource p_235056_) {
      return p_235055_ == null ? this.unculledFaces : (List)this.culledFaces.get(p_235055_);
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }

   public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
      if (!ItemBlockRenderTypes.isFancy()) {
         if (this.blockRenderTypesFast != null) {
            return this.blockRenderTypesFast;
         }

         if (this.isRenderingCutout && state.getBlock() instanceof LeavesBlock) {
            return SOLID_BLOCK;
         }
      }

      return this.blockRenderTypes != null ? this.blockRenderTypes : BakedModel.super.getRenderTypes(state, rand, data);
   }

   public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
      if (!fabulous) {
         if (!ItemBlockRenderTypes.isFancy()) {
            if (this.itemRenderTypesFast != null) {
               return this.itemRenderTypesFast;
            }

            if (this.isRenderingCutout) {
               Item var4 = itemStack.getItem();
               if (var4 instanceof BlockItem) {
                  BlockItem blockItem = (BlockItem)var4;
                  if (blockItem.getBlock() instanceof LeavesBlock) {
                     return SOLID_BLOCK_ITEM;
                  }
               }
            }
         }

         if (this.itemRenderTypes != null) {
            return this.itemRenderTypes;
         }
      } else if (this.fabulousItemRenderTypes != null) {
         return this.fabulousItemRenderTypes;
      }

      return BakedModel.super.getRenderTypes(itemStack, fabulous);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> unculledFaces;
      private final Map<Direction, List<BakedQuad>> culledFaces;
      private final ItemOverrides overrides;
      private final boolean hasAmbientOcclusion;
      private TextureAtlasSprite particleIcon;
      private final boolean usesBlockLight;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(BlockModel p_119517_, ItemOverrides p_119518_, boolean p_119519_) {
         this(p_119517_.hasAmbientOcclusion(), p_119517_.getGuiLight().lightLikeBlock(), p_119519_, p_119517_.getTransforms(), p_119518_);
      }

      public Builder(boolean p_119521_, boolean p_119522_, boolean p_119523_, ItemTransforms p_119524_, ItemOverrides p_119525_) {
         this.unculledFaces = Lists.newArrayList();
         this.culledFaces = Maps.newEnumMap(Direction.class);

         for(Direction direction : Direction.values()) {
            this.culledFaces.put(direction, Lists.newArrayList());
         }

         this.overrides = p_119525_;
         this.hasAmbientOcclusion = p_119521_;
         this.usesBlockLight = p_119522_;
         this.isGui3d = p_119523_;
         this.transforms = p_119524_;
      }

      public Builder addCulledFace(Direction p_119531_, BakedQuad p_119532_) {
         ((List)this.culledFaces.get(p_119531_)).add(p_119532_);
         return this;
      }

      public Builder addUnculledFace(BakedQuad p_119527_) {
         this.unculledFaces.add(p_119527_);
         return this;
      }

      public Builder particle(TextureAtlasSprite p_119529_) {
         this.particleIcon = p_119529_;
         return this;
      }

      public Builder item() {
         return this;
      }

      /** @deprecated */
      @Deprecated
      public BakedModel build() {
         return this.build(RenderTypeGroup.EMPTY, RenderTypeGroup.EMPTY);
      }

      public BakedModel build(RenderTypeGroup renderTypes) {
         return this.build(renderTypes, RenderTypeGroup.EMPTY);
      }

      public BakedModel build(RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast) {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes, renderTypesFast);
         }
      }
   }
}
