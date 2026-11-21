package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HugeExplosionSeedParticle extends NoRenderParticle {
   private int life;
   private final int lifeTime = 8;

   HugeExplosionSeedParticle(ClientLevel p_106947_, double p_106948_, double p_106949_, double p_106950_) {
      super(p_106947_, p_106948_, p_106949_, p_106950_, (double)0.0F, (double)0.0F, (double)0.0F);
   }

   public void tick() {
      for(int $$0 = 0; $$0 < 6; ++$$0) {
         double $$1 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * (double)4.0F;
         double $$2 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * (double)4.0F;
         double $$3 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * (double)4.0F;
         this.level.addParticle(ParticleTypes.EXPLOSION, $$1, $$2, $$3, (double)((float)this.life / (float)this.lifeTime), (double)0.0F, (double)0.0F);
      }

      ++this.life;
      if (this.life == this.lifeTime) {
         this.remove();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType p_106969_, ClientLevel p_106970_, double p_106971_, double p_106972_, double p_106973_, double p_106974_, double p_106975_, double p_106976_) {
         return new HugeExplosionSeedParticle(p_106970_, p_106971_, p_106972_, p_106973_);
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
