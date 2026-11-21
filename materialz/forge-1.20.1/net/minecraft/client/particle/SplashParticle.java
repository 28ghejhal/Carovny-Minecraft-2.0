package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SplashParticle extends WaterDropParticle {
   SplashParticle(ClientLevel p_107929_, double p_107930_, double p_107931_, double p_107932_, double p_107933_, double p_107934_, double p_107935_) {
      super(p_107929_, p_107930_, p_107931_, p_107932_);
      this.gravity = 0.04F;
      if (p_107934_ == (double)0.0F && (p_107933_ != (double)0.0F || p_107935_ != (double)0.0F)) {
         this.xd = p_107933_;
         this.yd = 0.1;
         this.zd = p_107935_;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107947_) {
         this.sprite = p_107947_;
      }

      public Particle createParticle(SimpleParticleType p_107958_, ClientLevel p_107959_, double p_107960_, double p_107961_, double p_107962_, double p_107963_, double p_107964_, double p_107965_) {
         SplashParticle $$8 = new SplashParticle(p_107959_, p_107960_, p_107961_, p_107962_, p_107963_, p_107964_, p_107965_);
         $$8.pickSprite(this.sprite);
         return $$8;
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
