package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDropParticle extends TextureSheetParticle {
   protected WaterDropParticle(ClientLevel p_108484_, double p_108485_, double p_108486_, double p_108487_) {
      super(p_108484_, p_108485_, p_108486_, p_108487_, (double)0.0F, (double)0.0F, (double)0.0F);
      this.xd *= (double)0.3F;
      this.yd = Math.random() * (double)0.2F + (double)0.1F;
      this.zd *= (double)0.3F;
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.lifetime = (int)((double)8.0F / (Math.random() * 0.8 + 0.2));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.98F;
         this.yd *= (double)0.98F;
         this.zd *= (double)0.98F;
         if (this.onGround) {
            if (Math.random() < (double)0.5F) {
               this.remove();
            }

            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

         BlockPos $$0 = BlockPos.containing(this.x, this.y, this.z);
         double $$1 = Math.max(this.level.getBlockState($$0).getCollisionShape(this.level, $$0).max(Direction.Axis.Y, this.x - (double)$$0.getX(), this.z - (double)$$0.getZ()), (double)this.level.getFluidState($$0).getHeight(this.level, $$0));
         if ($$1 > (double)0.0F && this.y < (double)$$0.getY() + $$1) {
            this.remove();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_108492_) {
         this.sprite = p_108492_;
      }

      public Particle createParticle(SimpleParticleType p_108503_, ClientLevel p_108504_, double p_108505_, double p_108506_, double p_108507_, double p_108508_, double p_108509_, double p_108510_) {
         WaterDropParticle $$8 = new WaterDropParticle(p_108504_, p_108505_, p_108506_, p_108507_);
         $$8.pickSprite(this.sprite);
         return $$8;
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
