package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.MultipliedFloats;
import net.minecraft.util.valueproviders.SampledFloat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundManager extends SimplePreparableReloadListener<Preparations> {
   public static final Sound EMPTY_SOUND;
   public static final ResourceLocation INTENTIONALLY_EMPTY_SOUND_LOCATION;
   public static final WeighedSoundEvents INTENTIONALLY_EMPTY_SOUND_EVENT;
   public static final Sound INTENTIONALLY_EMPTY_SOUND;
   static final Logger LOGGER;
   private static final String SOUNDS_PATH = "sounds.json";
   private static final Gson GSON;
   private static final TypeToken<Map<String, SoundEventRegistration>> SOUND_EVENT_REGISTRATION_TYPE;
   private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
   private final SoundEngine soundEngine;
   private final Map<ResourceLocation, Resource> soundCache = new HashMap();

   public SoundManager(Options p_250027_) {
      this.soundEngine = new SoundEngine(this, p_250027_, ResourceProvider.fromMap(this.soundCache));
   }

   protected Preparations prepare(ResourceManager p_120356_, ProfilerFiller p_120357_) {
      Preparations $$2 = new Preparations();
      p_120357_.startTick();
      p_120357_.push("list");
      $$2.listResources(p_120356_);
      p_120357_.pop();

      for(String $$3 : p_120356_.getNamespaces()) {
         p_120357_.push($$3);

         try {
            for(Resource $$5 : p_120356_.getResourceStack(new ResourceLocation($$3, "sounds.json"))) {
               p_120357_.push($$5.sourcePackId());

               try {
                  Reader $$6 = $$5.openAsReader();

                  try {
                     p_120357_.push("parse");
                     Map<String, SoundEventRegistration> $$7 = (Map)GsonHelper.fromJson(GSON, $$6, SOUND_EVENT_REGISTRATION_TYPE);
                     p_120357_.popPush("register");

                     for(Map.Entry<String, SoundEventRegistration> $$8 : $$7.entrySet()) {
                        $$2.handleRegistration(new ResourceLocation($$3, (String)$$8.getKey()), (SoundEventRegistration)$$8.getValue());
                     }

                     p_120357_.pop();
                  } catch (Throwable var14) {
                     if ($$6 != null) {
                        try {
                           $$6.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
                  }

                  if ($$6 != null) {
                     $$6.close();
                  }
               } catch (RuntimeException $$9) {
                  LOGGER.warn("Invalid {} in resourcepack: '{}'", new Object[]{"sounds.json", $$5.sourcePackId(), $$9});
               }

               p_120357_.pop();
            }
         } catch (IOException var16) {
         }

         p_120357_.pop();
      }

      p_120357_.endTick();
      return $$2;
   }

   protected void apply(Preparations p_120377_, ResourceManager p_120378_, ProfilerFiller p_120379_) {
      p_120377_.apply(this.registry, this.soundCache, this.soundEngine);
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         for(ResourceLocation $$3 : this.registry.keySet()) {
            WeighedSoundEvents $$4 = (WeighedSoundEvents)this.registry.get($$3);
            if (!ComponentUtils.isTranslationResolvable($$4.getSubtitle()) && BuiltInRegistries.SOUND_EVENT.containsKey($$3)) {
               LOGGER.error("Missing subtitle {} for sound event: {}", $$4.getSubtitle(), $$3);
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         for(ResourceLocation $$5 : this.registry.keySet()) {
            if (!BuiltInRegistries.SOUND_EVENT.containsKey($$5)) {
               LOGGER.debug("Not having sound event for: {}", $$5);
            }
         }
      }

      this.soundEngine.reload();
   }

   public List<String> getAvailableSoundDevices() {
      return this.soundEngine.getAvailableSoundDevices();
   }

   static boolean validateSoundResource(Sound p_250396_, ResourceLocation p_250879_, ResourceProvider p_248737_) {
      ResourceLocation $$3 = p_250396_.getPath();
      if (p_248737_.getResource($$3).isEmpty()) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", $$3, p_250879_);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public WeighedSoundEvents getSoundEvent(ResourceLocation p_120385_) {
      return (WeighedSoundEvents)this.registry.get(p_120385_);
   }

   public Collection<ResourceLocation> getAvailableSounds() {
      return this.registry.keySet();
   }

   public void queueTickingSound(TickableSoundInstance p_120373_) {
      this.soundEngine.queueTickingSound(p_120373_);
   }

   public void play(SoundInstance p_120368_) {
      this.soundEngine.play(p_120368_);
   }

   public void playDelayed(SoundInstance p_120370_, int p_120371_) {
      this.soundEngine.playDelayed(p_120370_, p_120371_);
   }

   public void updateSource(Camera p_120362_) {
      this.soundEngine.updateSource(p_120362_);
   }

   public void pause() {
      this.soundEngine.pause();
   }

   public void stop() {
      this.soundEngine.stopAll();
   }

   public void destroy() {
      this.soundEngine.destroy();
   }

   public void tick(boolean p_120390_) {
      this.soundEngine.tick(p_120390_);
   }

   public void resume() {
      this.soundEngine.resume();
   }

   public void updateSourceVolume(SoundSource p_120359_, float p_120360_) {
      if (p_120359_ == SoundSource.MASTER && p_120360_ <= 0.0F) {
         this.stop();
      }

      this.soundEngine.updateCategoryVolume(p_120359_, p_120360_);
   }

   public void stop(SoundInstance p_120400_) {
      this.soundEngine.stop(p_120400_);
   }

   public boolean isActive(SoundInstance p_120404_) {
      return this.soundEngine.isActive(p_120404_);
   }

   public void addListener(SoundEventListener p_120375_) {
      this.soundEngine.addEventListener(p_120375_);
   }

   public void removeListener(SoundEventListener p_120402_) {
      this.soundEngine.removeEventListener(p_120402_);
   }

   public void stop(@Nullable ResourceLocation p_120387_, @Nullable SoundSource p_120388_) {
      this.soundEngine.stop(p_120387_, p_120388_);
   }

   public String getDebugString() {
      return this.soundEngine.getDebugString();
   }

   public void reload() {
      this.soundEngine.reload();
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   static {
      EMPTY_SOUND = new Sound("minecraft:empty", ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Sound.Type.FILE, false, false, 16);
      INTENTIONALLY_EMPTY_SOUND_LOCATION = new ResourceLocation("minecraft", "intentionally_empty");
      INTENTIONALLY_EMPTY_SOUND_EVENT = new WeighedSoundEvents(INTENTIONALLY_EMPTY_SOUND_LOCATION, (String)null);
      INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_SOUND_LOCATION.toString(), ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Sound.Type.FILE, false, false, 16);
      LOGGER = LogUtils.getLogger();
      GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
      SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundEventRegistration>>() {
      };
   }

   @OnlyIn(Dist.CLIENT)
   protected static class Preparations {
      final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
      private Map<ResourceLocation, Resource> soundCache = Map.of();

      void listResources(ResourceManager p_249271_) {
         this.soundCache = Sound.SOUND_LISTER.listMatchingResources(p_249271_);
      }

      void handleRegistration(ResourceLocation p_250806_, SoundEventRegistration p_249632_) {
         WeighedSoundEvents $$2 = (WeighedSoundEvents)this.registry.get(p_250806_);
         boolean $$3 = $$2 == null;
         if ($$3 || p_249632_.isReplace()) {
            if (!$$3) {
               SoundManager.LOGGER.debug("Replaced sound event location {}", p_250806_);
            }

            $$2 = new WeighedSoundEvents(p_250806_, p_249632_.getSubtitle());
            this.registry.put(p_250806_, $$2);
         }

         ResourceProvider $$4 = ResourceProvider.fromMap(this.soundCache);

         for(Sound $$5 : p_249632_.getSounds()) {
            ResourceLocation $$6 = $$5.getLocation();
            Weighted<Sound> $$8;
            switch ($$5.getType()) {
               case FILE:
                  if (!SoundManager.validateSoundResource($$5, p_250806_, $$4)) {
                     continue;
                  }

                  $$8 = $$5;
                  break;
               case SOUND_EVENT:
                  $$8 = new Weighted<Sound>($$6, $$5) {
                     // $FF: synthetic field
                     final ResourceLocation val$soundLocation;
                     // $FF: synthetic field
                     final Sound val$sound;
                     // $FF: synthetic field
                     final SoundManager.Preparations this$0;

                     {
                        this.this$0 = p_120433_;
                        this.val$soundLocation = var2;
                        this.val$sound = var3;
                     }

                     public int getWeight() {
                        WeighedSoundEvents $$0 = (WeighedSoundEvents)this.this$0.registry.get(this.val$soundLocation);
                        return $$0 == null ? 0 : $$0.getWeight();
                     }

                     public Sound getSound(RandomSource p_235261_) {
                        WeighedSoundEvents $$1 = (WeighedSoundEvents)this.this$0.registry.get(this.val$soundLocation);
                        if ($$1 == null) {
                           return SoundManager.EMPTY_SOUND;
                        } else {
                           Sound $$2 = $$1.getSound(p_235261_);
                           return new Sound($$2.getLocation().toString(), new MultipliedFloats(new SampledFloat[]{$$2.getVolume(), this.val$sound.getVolume()}), new MultipliedFloats(new SampledFloat[]{$$2.getPitch(), this.val$sound.getPitch()}), this.val$sound.getWeight(), Sound.Type.FILE, $$2.shouldStream() || this.val$sound.shouldStream(), $$2.shouldPreload(), $$2.getAttenuationDistance());
                        }
                     }

                     public void preloadIfRequired(SoundEngine p_120438_) {
                        WeighedSoundEvents $$1 = (WeighedSoundEvents)this.this$0.registry.get(this.val$soundLocation);
                        if ($$1 != null) {
                           $$1.preloadIfRequired(p_120438_);
                        }
                     }

                     // $FF: synthetic method
                     public Object getSound(RandomSource var1) {
                        return this.getSound(var1);
                     }
                  };
                  break;
               default:
                  throw new IllegalStateException("Unknown SoundEventRegistration type: " + $$5.getType());
            }

            $$2.addSound($$8);
         }

      }

      public void apply(Map<ResourceLocation, WeighedSoundEvents> p_251229_, Map<ResourceLocation, Resource> p_251045_, SoundEngine p_250302_) {
         p_251229_.clear();
         p_251045_.clear();
         p_251045_.putAll(this.soundCache);

         for(Map.Entry<ResourceLocation, WeighedSoundEvents> $$3 : this.registry.entrySet()) {
            p_251229_.put((ResourceLocation)$$3.getKey(), (WeighedSoundEvents)$$3.getValue());
            ((WeighedSoundEvents)$$3.getValue()).preloadIfRequired(p_250302_);
         }

      }
   }
}
