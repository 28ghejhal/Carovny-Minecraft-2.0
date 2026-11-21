package net.minecraft.world.level.block.entity;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface Hopper extends Container {
   VoxelShape INSIDE = Block.box((double)2.0F, (double)11.0F, (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F);
   VoxelShape ABOVE = Block.box((double)0.0F, (double)16.0F, (double)0.0F, (double)16.0F, (double)32.0F, (double)16.0F);
   VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

   default VoxelShape getSuckShape() {
      return SUCK;
   }

   double getLevelX();

   double getLevelY();

   double getLevelZ();
}
