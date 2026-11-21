package net.minecraftforge.common.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;

public interface IForgeLivingEntity extends IForgeEntity {
   default LivingEntity self() {
      return (LivingEntity)this;
   }

   default boolean canSwimInFluidType(FluidType type) {
      if (type == ForgeMod.WATER_TYPE.get()) {
         return !this.self().isSensitiveToWater();
      } else {
         return IForgeEntity.super.canSwimInFluidType(type);
      }
   }

   default void jumpInFluid(FluidType type) {
      this.self().setDeltaMovement(this.self().getDeltaMovement().add((double)0.0F, (double)0.04F * this.self().getAttributeValue(ForgeMod.SWIM_SPEED.get()), (double)0.0F));
   }

   default void sinkInFluid(FluidType type) {
      this.self().setDeltaMovement(this.self().getDeltaMovement().add((double)0.0F, (double)-0.04F * this.self().getAttributeValue(ForgeMod.SWIM_SPEED.get()), (double)0.0F));
   }

   default boolean canDrownInFluidType(FluidType type) {
      if (type == ForgeMod.WATER_TYPE.get()) {
         return !this.self().canBreatheUnderwater();
      } else {
         return type.canDrownIn(this.self());
      }
   }

   default boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
      return state.move(this.self(), movementVector, gravity);
   }
}
