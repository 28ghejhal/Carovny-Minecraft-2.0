package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaRenderer extends MobRenderer<Panda, PandaModel<Panda>> {
   private static final Map<Panda.Gene, ResourceLocation> TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), (p_115647_) -> {
      p_115647_.put(Panda.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
      p_115647_.put(Panda.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
      p_115647_.put(Panda.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
      p_115647_.put(Panda.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
      p_115647_.put(Panda.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
      p_115647_.put(Panda.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
      p_115647_.put(Panda.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRendererProvider.Context p_174334_) {
      super(p_174334_, new PandaModel(p_174334_.bakeLayer(ModelLayers.PANDA)), 0.9F);
      this.addLayer(new PandaHoldsItemLayer(this, p_174334_.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Panda p_115639_) {
      return (ResourceLocation)TEXTURES.getOrDefault(p_115639_.getVariant(), (ResourceLocation)TEXTURES.get(Panda.Gene.NORMAL));
   }

   protected void setupRotations(Panda p_115641_, PoseStack p_115642_, float p_115643_, float p_115644_, float p_115645_) {
      super.setupRotations(p_115641_, p_115642_, p_115643_, p_115644_, p_115645_);
      if (p_115641_.rollCounter > 0) {
         int $$5 = p_115641_.rollCounter;
         int $$6 = $$5 + 1;
         float $$7 = 7.0F;
         float $$8 = p_115641_.isBaby() ? 0.3F : 0.8F;
         if ($$5 < 8) {
            float $$9 = (float)(90 * $$5) / 7.0F;
            float $$10 = (float)(90 * $$6) / 7.0F;
            float $$11 = this.getAngle($$9, $$10, $$6, p_115645_, 8.0F);
            p_115642_.translate(0.0F, ($$8 + 0.2F) * ($$11 / 90.0F), 0.0F);
            p_115642_.mulPose(Axis.XP.rotationDegrees(-$$11));
         } else if ($$5 < 16) {
            float $$12 = ((float)$$5 - 8.0F) / 7.0F;
            float $$13 = 90.0F + 90.0F * $$12;
            float $$14 = 90.0F + 90.0F * ((float)$$6 - 8.0F) / 7.0F;
            float $$15 = this.getAngle($$13, $$14, $$6, p_115645_, 16.0F);
            p_115642_.translate(0.0F, $$8 + 0.2F + ($$8 - 0.2F) * ($$15 - 90.0F) / 90.0F, 0.0F);
            p_115642_.mulPose(Axis.XP.rotationDegrees(-$$15));
         } else if ((float)$$5 < 24.0F) {
            float $$16 = ((float)$$5 - 16.0F) / 7.0F;
            float $$17 = 180.0F + 90.0F * $$16;
            float $$18 = 180.0F + 90.0F * ((float)$$6 - 16.0F) / 7.0F;
            float $$19 = this.getAngle($$17, $$18, $$6, p_115645_, 24.0F);
            p_115642_.translate(0.0F, $$8 + $$8 * (270.0F - $$19) / 90.0F, 0.0F);
            p_115642_.mulPose(Axis.XP.rotationDegrees(-$$19));
         } else if ($$5 < 32) {
            float $$20 = ((float)$$5 - 24.0F) / 7.0F;
            float $$21 = 270.0F + 90.0F * $$20;
            float $$22 = 270.0F + 90.0F * ((float)$$6 - 24.0F) / 7.0F;
            float $$23 = this.getAngle($$21, $$22, $$6, p_115645_, 32.0F);
            p_115642_.translate(0.0F, $$8 * ((360.0F - $$23) / 90.0F), 0.0F);
            p_115642_.mulPose(Axis.XP.rotationDegrees(-$$23));
         }
      }

      float $$24 = p_115641_.getSitAmount(p_115645_);
      if ($$24 > 0.0F) {
         p_115642_.translate(0.0F, 0.8F * $$24, 0.0F);
         p_115642_.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$24, p_115641_.getXRot(), p_115641_.getXRot() + 90.0F)));
         p_115642_.translate(0.0F, -1.0F * $$24, 0.0F);
         if (p_115641_.isScared()) {
            float $$25 = (float)(Math.cos((double)p_115641_.tickCount * (double)1.25F) * Math.PI * (double)0.05F);
            p_115642_.mulPose(Axis.YP.rotationDegrees($$25));
            if (p_115641_.isBaby()) {
               p_115642_.translate(0.0F, 0.8F, 0.55F);
            }
         }
      }

      float $$26 = p_115641_.getLieOnBackAmount(p_115645_);
      if ($$26 > 0.0F) {
         float $$27 = p_115641_.isBaby() ? 0.5F : 1.3F;
         p_115642_.translate(0.0F, $$27 * $$26, 0.0F);
         p_115642_.mulPose(Axis.XP.rotationDegrees(Mth.lerp($$26, p_115641_.getXRot(), p_115641_.getXRot() + 180.0F)));
      }

   }

   private float getAngle(float p_115625_, float p_115626_, int p_115627_, float p_115628_, float p_115629_) {
      return (float)p_115627_ < p_115629_ ? Mth.lerp(p_115628_, p_115625_, p_115626_) : p_115625_;
   }
}
