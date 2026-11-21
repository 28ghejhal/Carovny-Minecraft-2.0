package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public interface IForgePlayer {
   private Player self() {
      return (Player)this;
   }

   default double getEntityReach() {
      double range = this.self().getAttributeValue(ForgeMod.ENTITY_REACH.get());
      return range == (double)0.0F ? (double)0.0F : range + (double)(this.self().isCreative() ? 3 : 0);
   }

   default double getBlockReach() {
      double reach = this.self().getAttributeValue(ForgeMod.BLOCK_REACH.get());
      return reach == (double)0.0F ? (double)0.0F : reach + (this.self().isCreative() ? (double)0.5F : (double)0.0F);
   }

   default boolean canReach(Vec3 entityHitVec, double padding) {
      return this.self().getEyePosition().closerThan(entityHitVec, this.getEntityReach() + padding);
   }

   default boolean canReach(Entity entity, double padding) {
      return this.isCloseEnough(entity, this.getEntityReach() + padding);
   }

   default boolean canReachRaw(Entity entity, double padding) {
      double range = this.self().getAttributeValue(ForgeMod.ENTITY_REACH.get()) + padding;
      return this.isCloseEnough(entity, range);
   }

   default boolean canReach(BlockPos pos, double padding) {
      double reach = this.getBlockReach() + padding;
      return this.self().getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) <= reach * reach;
   }

   default boolean canReachRaw(BlockPos pos, double padding) {
      double reach = this.self().getAttributeValue(ForgeMod.BLOCK_REACH.get()) + padding;
      return this.self().getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) <= reach * reach;
   }

   default boolean isCloseEnough(Entity entity, double dist) {
      Vec3 eye = this.self().getEyePosition();
      AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
      return aabb.distanceToSqr(eye) < dist * dist;
   }
}
