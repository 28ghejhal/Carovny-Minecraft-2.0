package net.minecraftforge.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityProvider {
   <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> var1, @Nullable Direction var2);

   default <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
      return this.<T>getCapability(cap, (Direction)null);
   }
}
