package net.minecraft.world.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity extends HangingEntity {
   public static final double OFFSET_Y = (double)0.375F;

   public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> p_31828_, Level p_31829_) {
      super(p_31828_, p_31829_);
   }

   public LeashFenceKnotEntity(Level p_31831_, BlockPos p_31832_) {
      super(EntityType.LEASH_KNOT, p_31831_, p_31832_);
      this.setPos((double)p_31832_.getX(), (double)p_31832_.getY(), (double)p_31832_.getZ());
   }

   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + (double)0.5F, (double)this.pos.getY() + (double)0.375F, (double)this.pos.getZ() + (double)0.5F);
      double $$0 = (double)this.getType().getWidth() / (double)2.0F;
      double $$1 = (double)this.getType().getHeight();
      this.setBoundingBox(new AABB(this.getX() - $$0, this.getY(), this.getZ() - $$0, this.getX() + $$0, this.getY() + $$1, this.getZ() + $$0));
   }

   public void setDirection(Direction p_31848_) {
   }

   public int getWidth() {
      return 9;
   }

   public int getHeight() {
      return 9;
   }

   protected float getEyeHeight(Pose p_31839_, EntityDimensions p_31840_) {
      return 0.0625F;
   }

   public boolean shouldRenderAtSqrDistance(double p_31835_) {
      return p_31835_ < (double)1024.0F;
   }

   public void dropItem(@Nullable Entity p_31837_) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag p_31852_) {
   }

   public void readAdditionalSaveData(CompoundTag p_31850_) {
   }

   public InteractionResult interact(Player p_31842_, InteractionHand p_31843_) {
      if (this.level().isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         boolean $$2 = false;
         double $$3 = (double)7.0F;
         List<Mob> $$4 = this.level().getEntitiesOfClass(Mob.class, new AABB(this.getX() - (double)7.0F, this.getY() - (double)7.0F, this.getZ() - (double)7.0F, this.getX() + (double)7.0F, this.getY() + (double)7.0F, this.getZ() + (double)7.0F));

         for(Mob $$5 : $$4) {
            if ($$5.getLeashHolder() == p_31842_) {
               $$5.setLeashedTo(this, true);
               $$2 = true;
            }
         }

         boolean $$6 = false;
         if (!$$2) {
            this.discard();
            if (p_31842_.getAbilities().instabuild) {
               for(Mob $$7 : $$4) {
                  if ($$7.isLeashed() && $$7.getLeashHolder() == this) {
                     $$7.dropLeash(true, false);
                     $$6 = true;
                  }
               }
            }
         }

         if ($$2 || $$6) {
            this.gameEvent(GameEvent.BLOCK_ATTACH, p_31842_);
         }

         return InteractionResult.CONSUME;
      }
   }

   public boolean survives() {
      return this.level().getBlockState(this.pos).is(BlockTags.FENCES);
   }

   public static LeashFenceKnotEntity getOrCreateKnot(Level p_31845_, BlockPos p_31846_) {
      int $$2 = p_31846_.getX();
      int $$3 = p_31846_.getY();
      int $$4 = p_31846_.getZ();

      for(LeashFenceKnotEntity $$6 : p_31845_.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)$$2 - (double)1.0F, (double)$$3 - (double)1.0F, (double)$$4 - (double)1.0F, (double)$$2 + (double)1.0F, (double)$$3 + (double)1.0F, (double)$$4 + (double)1.0F))) {
         if ($$6.getPos().equals(p_31846_)) {
            return $$6;
         }
      }

      LeashFenceKnotEntity $$7 = new LeashFenceKnotEntity(p_31845_, p_31846_);
      p_31845_.addFreshEntity($$7);
      return $$7;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, 0, this.getPos());
   }

   public Vec3 getRopeHoldPosition(float p_31863_) {
      return this.getPosition(p_31863_).add((double)0.0F, 0.2, (double)0.0F);
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.LEAD);
   }
}
