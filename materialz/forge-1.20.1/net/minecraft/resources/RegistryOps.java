package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DelegatingOps<T> {
   private final RegistryInfoLookup lookupProvider;

   private static RegistryInfoLookup memoizeLookup(final RegistryInfoLookup p_255769_) {
      return new RegistryInfoLookup() {
         private final Map<ResourceKey<? extends Registry<?>>, Optional<? extends RegistryInfo<?>>> lookups = new HashMap();

         public <T> Optional<RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> p_256043_) {
            Map var10000 = this.lookups;
            RegistryInfoLookup var10002 = p_255769_;
            Objects.requireNonNull(var10002);
            return (Optional)var10000.computeIfAbsent(p_256043_, var10002::lookup);
         }
      };
   }

   public static <T> RegistryOps<T> create(DynamicOps<T> p_256342_, final HolderLookup.Provider p_255950_) {
      return create(p_256342_, memoizeLookup(new RegistryInfoLookup() {
         public <E> Optional<RegistryInfo<E>> lookup(ResourceKey<? extends Registry<? extends E>> p_256323_) {
            return p_255950_.lookup(p_256323_).map((p_258224_) -> new RegistryInfo(p_258224_, p_258224_, p_258224_.registryLifecycle()));
         }
      }));
   }

   public static <T> RegistryOps<T> create(DynamicOps<T> p_256278_, RegistryInfoLookup p_256479_) {
      return new RegistryOps<T>(p_256278_, p_256479_);
   }

   private RegistryOps(DynamicOps<T> p_256313_, RegistryInfoLookup p_255799_) {
      super(p_256313_);
      this.lookupProvider = p_255799_;
   }

   public <E> Optional<HolderOwner<E>> owner(ResourceKey<? extends Registry<? extends E>> p_255757_) {
      return this.lookupProvider.lookup(p_255757_).map(RegistryInfo::owner);
   }

   public <E> Optional<HolderGetter<E>> getter(ResourceKey<? extends Registry<? extends E>> p_256031_) {
      return this.lookupProvider.lookup(p_256031_).map(RegistryInfo::getter);
   }

   public static <E, O> RecordCodecBuilder<O, HolderGetter<E>> retrieveGetter(ResourceKey<? extends Registry<? extends E>> p_206833_) {
      return ExtraCodecs.retrieveContext((p_274811_) -> {
         if (p_274811_ instanceof RegistryOps<?> registryops) {
            return (DataResult)registryops.lookupProvider.lookup(p_206833_).map((p_255527_) -> DataResult.success(p_255527_.getter(), p_255527_.elementsLifecycle())).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + String.valueOf(p_206833_)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((p_255526_) -> null);
   }

   public static <E> MapCodec<HolderLookup.RegistryLookup<E>> retrieveRegistryLookup(ResourceKey<? extends Registry<? extends E>> resourceKey) {
      return ExtraCodecs.<HolderLookup.RegistryLookup<E>>retrieveContext((ops) -> {
         if (ops instanceof RegistryOps<?> registryOps) {
            return (DataResult)registryOps.lookupProvider.lookup(resourceKey).map((registryInfo) -> {
               HolderOwner patt3774$temp = registryInfo.owner();
               if (patt3774$temp instanceof HolderLookup.RegistryLookup<E> registryLookup) {
                  return DataResult.success(registryLookup, registryInfo.elementsLifecycle());
               } else {
                  return DataResult.error(() -> "Found holder getter but was not a registry lookup for " + String.valueOf(resourceKey));
               }
            }).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + String.valueOf(resourceKey)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      });
   }

   public static <E, O> RecordCodecBuilder<O, Holder.Reference<E>> retrieveElement(ResourceKey<E> p_256347_) {
      ResourceKey<? extends Registry<E>> resourcekey = ResourceKey.createRegistryKey(p_256347_.registry());
      return ExtraCodecs.retrieveContext((p_274808_) -> {
         if (p_274808_ instanceof RegistryOps<?> registryops) {
            return (DataResult)registryops.lookupProvider.lookup(resourcekey).flatMap((p_255518_) -> p_255518_.getter().get(p_256347_)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Can't find value: " + String.valueOf(p_256347_)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((p_255524_) -> null);
   }

   public static record RegistryInfo<T>(HolderOwner<T> owner, HolderGetter<T> getter, Lifecycle elementsLifecycle) {
   }

   public interface RegistryInfoLookup {
      <T> Optional<RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1);
   }
}
