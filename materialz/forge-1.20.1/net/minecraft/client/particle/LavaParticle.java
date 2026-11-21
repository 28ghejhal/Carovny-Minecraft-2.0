package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaParticle extends TextureSheetParticle {
   LavaParticle(ClientLevel p_107074_, double p_107075_, double p_107076_, double p_107077_) {
      super(p_107074_, p_107075_, p_107076_, p_107077_, (double)0.0F, (double)0.0F, (double)0.0F);
      this.gravity = 0.75F;
      this.friction = 0.999F;
      this.xd *= (double)0.8F;
      this.yd *= (double)0.8F;
      this.zd *= (double)0.8F;
      this.yd = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
      this.lifetime = (int)((double)16.0F / (Math.random() * 0.8 + 0.2));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float p_107086_) {
      int $$1 = super.getLightColor(p_107086_);
      int $$2 = 240;
      int $$3 = $$1 >> 16 & 255;
      return 240 | $$3 << 16;
   }

   public float getQuadSize(float p_107089_) {
      float $$1 = ((float)this.age + p_107089_) / (float)this.lifetime;
      return this.quadSize * (1.0F - $$1 * $$1);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         float $$0 = (float)this.age / (float)this.lifetime;
         if (this.random.nextFloat() > $$0) {
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107092_) {
         this.sprite = p_107092_;
      }

      public Particle createParticle(SimpleParticleType p_107103_, ClientLevel p_107104_, double p_107105_, double p_107106_, double p_107107_, double p_107108_, double p_107109_, double p_107110_) {
         LavaParticle $$8 = new LavaParticle(p_107104_, p_107105_, p_107106_, p_107107_);
         $$8.pickSprite(this.sprite);
         return $$8;
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
