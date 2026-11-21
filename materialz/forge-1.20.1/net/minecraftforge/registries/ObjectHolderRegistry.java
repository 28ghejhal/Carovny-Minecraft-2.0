package net.minecraftforge.registries;

import com.google.common.collect.Maps;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

public class ObjectHolderRegistry {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<Consumer<Predicate<ResourceLocation>>> objectHolders = new HashSet();
   private static final Map<ResourceLocation, Set<Runnable>> namedHandlers = new HashMap();
   private static final List<Runnable> validators = new ArrayList();
   private static final Type OBJECT_HOLDER = Type.getType(ObjectHolder.class);
   private static final Type MOD = Type.getType(Mod.class);
   private static final List<VanillaObjectHolderData> VANILLA_OBJECT_HOLDERS = List.of(new VanillaObjectHolderData("net.minecraft.world.level.block.Blocks", "block", "net.minecraft.world.level.block.Block"), new VanillaObjectHolderData("net.minecraft.world.item.Items", "item", "net.minecraft.world.item.Item"), new VanillaObjectHolderData("net.minecraft.world.item.enchantment.Enchantments", "enchantment", "net.minecraft.world.item.enchantment.Enchantment"), new VanillaObjectHolderData("net.minecraft.world.effect.MobEffects", "mob_effect", "net.minecraft.world.effect.MobEffect"), new VanillaObjectHolderData("net.minecraft.core.particles.ParticleTypes", "particle_type", "net.minecraft.core.particles.ParticleType"), new VanillaObjectHolderData("net.minecraft.sounds.SoundEvents", "sound_event", "net.minecraft.sounds.SoundEvent"));
   private static final Predicate<ResourceLocation> ALWAYS_TRUE = (name) -> true;

   /** @deprecated */
   public static synchronized void addHandler(Consumer<Predicate<ResourceLocation>> ref) {
      objectHolders.add(ref);
   }

   public static synchronized void addHandler(ResourceLocation registry, Runnable ref) {
      ((Set)namedHandlers.computeIfAbsent(registry, (id) -> new HashSet())).add(ref);
   }

   public static synchronized boolean removeHandler(Consumer<Predicate<ResourceLocation>> ref) {
      return objectHolders.remove(ref);
   }

   public static synchronized boolean removeHandler(ResourceLocation registry, Runnable ref) {
      Set<Runnable> handlers = (Set)namedHandlers.get(registry);
      return handlers != null && handlers.remove(ref);
   }

   public static synchronized void addValidator(Runnable ref) {
      validators.add(ref);
   }

   public static void findObjectHolders() {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Processing ObjectHolder annotations");
      HashMap<Type, String> classModIds = new HashMap();
      List<ModFileScanData.AnnotationData> annotations = new ArrayList();

      for(ModFileScanData data : ModList.get().getAllScanData()) {
         for(ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
            if (OBJECT_HOLDER.equals(annotation.annotationType())) {
               annotations.add(annotation);
            } else if (MOD.equals(annotation.annotationType())) {
               classModIds.put(annotation.clazz(), (String)annotation.annotationData().get("value"));
            }
         }
      }

      Map<Type, Class<?>> classCache = Maps.newHashMap();

      for(VanillaObjectHolderData data : VANILLA_OBJECT_HOLDERS) {
         try {
            Class<?> holderClass = Class.forName(data.holderClass(), true, ObjectHolderRegistry.class.getClassLoader());
            Class<?> registryClass = Class.forName(data.registryType(), true, ObjectHolderRegistry.class.getClassLoader());
            Type holderType = Type.getType(holderClass);
            classCache.put(holderType, holderClass);
            scanTarget(classModIds, classCache, holderType, (String)null, registryClass, data.registryName(), "minecraft", true, true);
         } catch (ClassNotFoundException e) {
            throw new RuntimeException("Vanilla class not found, should not be possible", e);
         }
      }

      for(ModFileScanData.AnnotationData data : annotations) {
         if (data.targetType() == ElementType.FIELD) {
            String registry = (String)data.annotationData().get("registryName");
            String name = (String)data.annotationData().get("value");
            scanTarget(classModIds, classCache, data.clazz(), data.memberName(), (Class)null, registry, name, false, false);
         }
      }

      int count = objectHolders.size();

      for(Set<Runnable> set : namedHandlers.values()) {
         count += set.size();
      }

      LOGGER.debug(ForgeRegistry.REGISTRIES, "Found {} ObjectHolder annotations", count);
   }

   private static void scanTarget(Map<Type, String> classModIds, Map<Type, Class<?>> classCache, Type type, @Nullable String annotationTarget, @Nullable Class<?> registryClass, String registryName, String value, boolean isClass, boolean extractFromValue) {
      Class<?> clazz;
      if (classCache.containsKey(type)) {
         clazz = (Class)classCache.get(type);
      } else {
         try {
            clazz = Class.forName(type.getClassName(), extractFromValue, ObjectHolderRegistry.class.getClassLoader());
            classCache.put(type, clazz);
         } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
         }
      }

      ResourceLocation regName = ResourceLocation.parse(registryName);
      if (isClass) {
         scanClassForFields(classModIds, type, regName, registryClass, value, clazz, extractFromValue);
      } else {
         if (value.indexOf(58) == -1) {
            String prefix = (String)classModIds.get(type);
            if (prefix == null) {
               LOGGER.warn(ForgeRegistry.REGISTRIES, "Found an unqualified ObjectHolder annotation ({}) without a modid context at {}.{}, ignoring", value, type, annotationTarget);
               throw new IllegalStateException("Unqualified reference to ObjectHolder");
            }

            value = prefix + ":" + value;
         }

         try {
            Field f = clazz.getDeclaredField(annotationTarget);
            ObjectHolderRef ref = ObjectHolderRef.create(regName, f, value, extractFromValue);
            if (ref != null) {
               addHandler(regName, ref);
            }
         } catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
         }
      }

   }

   private static void scanClassForFields(Map<Type, String> classModIds, Type targetClass, ResourceLocation registryName, Class<?> registryClass, String value, Class<?> clazz, boolean extractFromExistingValues) {
      classModIds.put(targetClass, value);
      int flags = 4105;

      for(Field f : clazz.getFields()) {
         if ((f.getModifiers() & 4105) == 4105 && !f.isAnnotationPresent(ObjectHolder.class) && registryClass.isAssignableFrom(f.getType())) {
            ObjectHolderRef ref = ObjectHolderRef.create(registryName, f, value + ":" + f.getName().toLowerCase(Locale.ENGLISH), extractFromExistingValues);
            if (ref != null) {
               addHandler(registryName, ref);
            }
         }
      }

   }

   private static ResourceLocation getRegistryName(Map<Type, ResourceLocation> classRegistryNames, @Nullable String registryName, Type targetClass, Object declaration) {
      if (registryName != null) {
         return new ResourceLocation(registryName);
      } else if (classRegistryNames.containsKey(targetClass)) {
         return (ResourceLocation)classRegistryNames.get(targetClass);
      } else {
         throw new IllegalStateException("No registry name was declared for " + String.valueOf(declaration));
      }
   }

   public static void applyObjectHolders() {
      try {
         LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups");
         ArrayList<Exception> exceptions = new ArrayList();
         validate(exceptions);

         for(Set<Runnable> handlers : namedHandlers.values()) {
            apply(handlers, exceptions);
         }

         if (!objectHolders.isEmpty()) {
            apply(ALWAYS_TRUE, objectHolders, exceptions);
         }

         throwIfNeeded(exceptions);
         LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
      } catch (RuntimeException e) {
         LOGGER.error("", e);
      }

   }

   /** @deprecated */
   public static void applyObjectHolders(Predicate<ResourceLocation> filter) {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Apply filtered holder lookups");
      ArrayList<Exception> exceptions = new ArrayList();
      validate(exceptions);

      for(Map.Entry<ResourceLocation, Set<Runnable>> entry : namedHandlers.entrySet()) {
         if (filter.test((ResourceLocation)entry.getKey())) {
            apply((Set)entry.getValue(), exceptions);
         }
      }

      if (!objectHolders.isEmpty()) {
         apply(filter, objectHolders, exceptions);
      }

      throwIfNeeded(exceptions);
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
   }

   public static void applyObjectHolders(ResourceLocation registry) {
      ArrayList<Exception> exceptions = new ArrayList();
      validate(exceptions);
      Set<Runnable> handlers = (Set)namedHandlers.get(registry);
      if (handlers != null) {
         apply(handlers, exceptions);
      }

      if (!objectHolders.isEmpty()) {
         Objects.requireNonNull(registry);
         apply(registry::equals, objectHolders, exceptions);
      }

      throwIfNeeded(exceptions);
   }

   private static void validate(List<Exception> exceptions) {
      List<Runnable> pending;
      synchronized(ObjectHolderRegistry.class) {
         if (validators.isEmpty()) {
            return;
         }

         pending = new ArrayList(validators);
         validators.clear();
      }

      for(Runnable validator : pending) {
         try {
            validator.run();
         } catch (Exception e) {
            exceptions.add(e);
         }
      }

   }

   private static void apply(Set<Runnable> handlers, List<Exception> exceptions) {
      for(Runnable handler : handlers) {
         try {
            handler.run();
         } catch (Exception e) {
            exceptions.add(e);
         }
      }

   }

   private static void apply(Predicate<ResourceLocation> filter, Set<Consumer<Predicate<ResourceLocation>>> handlers, List<Exception> exceptions) {
      for(Consumer<Predicate<ResourceLocation>> handler : handlers) {
         try {
            handler.accept(filter);
         } catch (Exception e) {
            exceptions.add(e);
         }
      }

   }

   private static void throwIfNeeded(List<Exception> exceptions) {
      if (!exceptions.isEmpty()) {
         RuntimeException aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");

         for(Exception e : exceptions) {
            aggregate.addSuppressed(e);
         }

         throw aggregate;
      }
   }

   private static record VanillaObjectHolderData(String holderClass, String registryName, String registryType) {
   }
}
