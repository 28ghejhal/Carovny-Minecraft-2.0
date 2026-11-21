package net.minecraftforge.registries;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegistryObject<T> implements Supplier<T> {
   private final @Nullable ResourceLocation name;
   private final @Nullable ResourceKey<T> key;
   private @Nullable T value;
   private @Nullable Holder<T> holder;
   private static final RegistryObject<?> EMPTY = new RegistryObject();

   public static <T, U extends T> RegistryObject<U> create(ResourceLocation name, IForgeRegistry<T> registry) {
      return new RegistryObject<U>(name, registry);
   }

   public static <T, U extends T> RegistryObject<U> create(ResourceLocation name, ResourceKey<? extends Registry<T>> registryKey, String modid) {
      return new RegistryObject<U>(name, registryKey.location(), modid, false);
   }

   public static <T, U extends T> RegistryObject<U> createOptional(ResourceLocation name, ResourceKey<? extends Registry<T>> registryKey, String modid) {
      return new RegistryObject<U>(name, registryKey.location(), modid, true);
   }

   public static <T, U extends T> RegistryObject<U> create(ResourceLocation name, ResourceLocation registryName, String modid) {
      return new RegistryObject<U>(name, registryName, modid, false);
   }

   public static <T, U extends T> RegistryObject<U> createOptional(ResourceLocation name, ResourceLocation registryName, String modid) {
      return new RegistryObject<U>(name, registryName, modid, true);
   }

   private static <T> RegistryObject<T> empty() {
      RegistryObject<T> t = EMPTY;
      return t;
   }

   private RegistryObject() {
      this.name = null;
      this.key = null;
   }

   private RegistryObject(ResourceLocation name, IForgeRegistry<?> registry) {
      if (registry == null) {
         throw new IllegalArgumentException("Invalid registry argument, must not be null");
      } else {
         this.name = name;
         this.key = ResourceKey.create(registry.getRegistryKey(), name);
         ObjectHolderRegistry.addHandler(registry.getRegistryName(), () -> this.updateReference(registry));
         this.updateReference(registry);
      }
   }

   private RegistryObject(ResourceLocation name, ResourceLocation registryName, String modid, boolean optionalRegistry) {
      this.name = name;
      this.key = ResourceKey.create(ResourceKey.createRegistryKey(registryName), name);
      if (!optionalRegistry) {
         Throwable callerStack = new Throwable("Calling Site from mod: " + modid);
         ObjectHolderRegistry.addValidator(() -> {
            if (!registryExists(registryName)) {
               throw new IllegalStateException("Unable to find registry with key " + String.valueOf(registryName) + " for mod \"" + modid + "\". Check the 'caused by' to see further stack.", callerStack);
            }
         });
      }

      ObjectHolderRegistry.addHandler(registryName, () -> this.updateReference(registryName));
      this.updateReference(registryName);
   }

   public @NotNull T get() {
      T ret = this.value;
      Objects.requireNonNull(ret, () -> "Registry Object not present: " + String.valueOf(this.name));
      return ret;
   }

   void updateReference(IForgeRegistry<? extends T> registry) {
      if (this.name != null && this.key != null) {
         if (registry.containsKey(this.name)) {
            this.value = registry.getValue(this.name);
            this.holder = (Holder)registry.getHolder(this.name).orElse((Object)null);
         } else {
            this.value = null;
            this.holder = null;
         }

      }
   }

   void updateReference(Registry<? extends T> registry) {
      if (this.name != null && this.key != null) {
         if (registry.containsKey(this.name)) {
            this.value = (T)registry.get(this.name);
            this.holder = (Holder)registry.getHolder(this.key).orElse((Object)null);
         } else {
            this.value = null;
            this.holder = null;
         }

      }
   }

   void updateReference(ResourceLocation registryName) {
      if (this.name != null) {
         IForgeRegistry<? extends T> forgeRegistry = RegistryManager.ACTIVE.getRegistry(registryName);
         if (forgeRegistry != null) {
            this.updateReference(forgeRegistry);
         } else {
            Registry<? extends T> vanillaRegistry = (Registry)BuiltInRegistries.REGISTRY.get(registryName);
            if (vanillaRegistry != null) {
               this.updateReference(vanillaRegistry);
            } else {
               this.value = null;
               this.holder = null;
            }
         }
      }
   }

   void updateReference(RegisterEvent event) {
      IForgeRegistry<? extends T> forgeRegistry = event.<T>getForgeRegistry();
      if (forgeRegistry != null) {
         this.updateReference(forgeRegistry);
      } else {
         Registry<? extends T> vanillaRegistry = event.<T>getVanillaRegistry();
         if (vanillaRegistry != null) {
            this.updateReference(vanillaRegistry);
         } else {
            this.value = null;
         }

      }
   }

   private static boolean registryExists(ResourceLocation registryName) {
      return RegistryManager.ACTIVE.getRegistry(registryName) != null || BuiltInRegistries.REGISTRY.containsKey(registryName);
   }

   public ResourceLocation getId() {
      return this.name;
   }

   public @Nullable ResourceKey<T> getKey() {
      return this.key;
   }

   public Stream<T> stream() {
      return this.isPresent() ? Stream.of(this.get()) : Stream.of();
   }

   public boolean isPresent() {
      return this.value != null;
   }

   public void ifPresent(Consumer<? super T> consumer) {
      if (this.isPresent()) {
         consumer.accept(this.get());
      }

   }

   public RegistryObject<T> filter(Predicate<? super T> predicate) {
      Objects.requireNonNull(predicate);
      if (!this.isPresent()) {
         return this;
      } else {
         return predicate.test(this.get()) ? this : empty();
      }
   }

   public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? Optional.empty() : Optional.ofNullable(mapper.apply(this.get()));
   }

   public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? Optional.empty() : (Optional)Objects.requireNonNull((Optional)mapper.apply(this.get()));
   }

   public <U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return () -> this.isPresent() ? mapper.apply(this.get()) : null;
   }

   public T orElse(T other) {
      return (T)(this.isPresent() ? this.get() : other);
   }

   public T orElseGet(Supplier<? extends T> other) {
      return (T)(this.isPresent() ? this.get() : other.get());
   }

   public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
      if (this.isPresent()) {
         return (T)this.get();
      } else {
         throw (Throwable)exceptionSupplier.get();
      }
   }

   public @NotNull Optional<Holder<T>> getHolder() {
      return Optional.ofNullable(this.holder);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof RegistryObject)) {
         return false;
      } else {
         RegistryObject o = (RegistryObject)obj;
         return o.key == this.key && Objects.equals(o.name, this.name);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.name);
   }
}
