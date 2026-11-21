package net.minecraftforge.fluids.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCapability
public interface IFluidHandler {
   int getTanks();

   @NotNull FluidStack getFluidInTank(int var1);

   int getTankCapacity(int var1);

   boolean isFluidValid(int var1, @NotNull FluidStack var2);

   int fill(FluidStack var1, FluidAction var2);

   @NotNull FluidStack drain(FluidStack var1, FluidAction var2);

   @NotNull FluidStack drain(int var1, FluidAction var2);

   public static enum FluidAction {
      EXECUTE,
      SIMULATE;

      public boolean execute() {
         return this == EXECUTE;
      }

      public boolean simulate() {
         return this == SIMULATE;
      }

      // $FF: synthetic method
      private static FluidAction[] $values() {
         return new FluidAction[]{EXECUTE, SIMULATE};
      }
   }
}
