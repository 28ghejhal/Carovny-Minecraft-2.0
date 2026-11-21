package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteParticle extends TextureSheetParticle {
   NoteParticle(ClientLevel p_107167_, double p_107168_, double p_107169_, double p_107170_, double p_107171_) {
      super(p_107167_, p_107168_, p_107169_, p_107170_, (double)0.0F, (double)0.0F, (double)0.0F);
      this.friction = 0.66F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.xd *= (double)0.01F;
      this.yd *= (double)0.01F;
      this.zd *= (double)0.01F;
      this.yd += 0.2;
      this.rCol = Math.max(0.0F, Mth.sin(((float)p_107171_ + 0.0F) * ((float)Math.PI * 2F)) * 0.65F + 0.35F);
      this.gCol = Math.max(0.0F, Mth.sin(((float)p_107171_ + 0.33333334F) * ((float)Math.PI * 2F)) * 0.65F + 0.35F);
      this.bCol = Math.max(0.0F, Mth.sin(((float)p_107171_ + 0.6666667F) * ((float)Math.PI * 2F)) * 0.65F + 0.35F);
      this.quadSize *= 1.5F;
      this.lifetime = 6;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float p_107182_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_107182_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107185_) {
         this.sprite = p_107185_;
      }

      public Particle createParticle(SimpleParticleType p_107196_, ClientLevel p_107197_, double p_107198_, double p_107199_, double p_107200_, double p_107201_, double p_107202_, double p_107203_) {
         NoteParticle $$8 = new NoteParticle(p_107197_, p_107198_, p_107199_, p_107200_, p_107201_);
         $$8.pickSprite(this.sprite);
         return $$8;
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
