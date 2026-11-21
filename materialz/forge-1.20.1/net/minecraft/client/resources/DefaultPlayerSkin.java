package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultPlayerSkin {
   private static final SkinType[] DEFAULT_SKINS;

   public static ResourceLocation getDefaultSkin() {
      return DEFAULT_SKINS[6].texture();
   }

   public static ResourceLocation getDefaultSkin(UUID p_118628_) {
      return getSkinType(p_118628_).texture;
   }

   public static String getSkinModelName(UUID p_118630_) {
      return getSkinType(p_118630_).model.id;
   }

   private static SkinType getSkinType(UUID p_260299_) {
      return DEFAULT_SKINS[Math.floorMod(p_260299_.hashCode(), DEFAULT_SKINS.length)];
   }

   static {
      DEFAULT_SKINS = new SkinType[]{new SkinType("textures/entity/player/slim/alex.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/ari.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/efe.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/kai.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/makena.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/noor.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/steve.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/sunny.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/slim/zuri.png", DefaultPlayerSkin.ModelType.SLIM), new SkinType("textures/entity/player/wide/alex.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/ari.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/efe.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/kai.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/makena.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/noor.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/steve.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/sunny.png", DefaultPlayerSkin.ModelType.WIDE), new SkinType("textures/entity/player/wide/zuri.png", DefaultPlayerSkin.ModelType.WIDE)};
   }

   @OnlyIn(Dist.CLIENT)
   static enum ModelType {
      SLIM("slim"),
      WIDE("default");

      final String id;

      private ModelType(String p_260160_) {
         this.id = p_260160_;
      }

      // $FF: synthetic method
      private static ModelType[] $values() {
         return new ModelType[]{SLIM, WIDE};
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record SkinType(ResourceLocation texture, ModelType model) {
      public SkinType(String p_259984_, ModelType p_259456_) {
         this(new ResourceLocation(p_259984_), p_259456_);
      }

      private SkinType(ResourceLocation texture, ModelType model) {
         this.texture = texture;
         this.model = model;
      }
   }
}
