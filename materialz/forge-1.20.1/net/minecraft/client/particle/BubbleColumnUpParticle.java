package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubbleColumnUpParticle extends TextureSheetParticle {
   BubbleColumnUpParticle(ClientLevel p_105733_, double p_105734_, double p_105735_, double p_105736_, double p_105737_, double p_105738_, double p_105739_) {
      super(p_105733_, p_105734_, p_105735_, p_105736_);
      this.gravity = -0.125F;
      this.friction = 0.85F;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.xd = p_105737_ * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
      this.yd = p_105738_ * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
      this.zd = p_105739_ * (double)0.2F + (Math.random() * (double)2.0F - (double)1.0F) * (double)0.02F;
      this.lifetime = (int)((double)40.0F / (Math.random() * 0.8 + 0.2));
   }

   public void tick() {
      super.tick();
      if (!this.removed && !this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
         this.remove();
      }

   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_105753_) {
         this.sprite = p_105753_;
      }

      public Particle createParticle(SimpleParticleType p_105764_, ClientLevel p_105765_, double p_105766_, double p_105767_, double p_105768_, double p_105769_, double p_105770_, double p_105771_) {
         BubbleColumnUpParticle $$8 = new BubbleColumnUpParticle(p_105765_, p_105766_, p_105767_, p_105768_, p_105769_, p_105770_, p_105771_);
         $$8.pickSprite(this.sprite);
         return $$8;
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
