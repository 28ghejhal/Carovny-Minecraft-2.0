package net.minecraftforge.common.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public interface NonNullLazy<T> extends NonNullSupplier<T> {
   static <T> NonNullLazy<T> of(@NotNull NonNullSupplier<T> supplier) {
      Objects.requireNonNull(supplier);
      Lazy<T> lazy = Lazy.<T>of(supplier::get);
      return () -> Objects.requireNonNull(lazy.get());
   }

   static <T> NonNullLazy<T> concurrentOf(@NotNull NonNullSupplier<T> supplier) {
      Objects.requireNonNull(supplier);
      Lazy<T> lazy = Lazy.<T>concurrentOf(supplier::get);
      return () -> Objects.requireNonNull(lazy.get());
   }
}
