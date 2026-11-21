package net.minecraftforge.registries;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class DataPackRegistryEvent extends Event implements IModBusEvent {
   public static final class NewRegistry extends DataPackRegistryEvent {
      private final List<DataPackRegistryData<?>> registryDataList = new ArrayList();

      public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
         this.dataPackRegistry(registryKey, codec, (Codec)null);
      }

      public <T> void dataPackRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
         this.registryDataList.add(new DataPackRegistryData(new RegistryDataLoader.RegistryData(registryKey, codec), networkCodec));
      }

      void process() {
         for(DataPackRegistryData<?> registryData : this.registryDataList) {
            DataPackRegistriesHooks.addRegistryCodec(registryData);
         }

      }
   }

   static record DataPackRegistryData<T>(RegistryDataLoader.RegistryData<T> loaderData, @Nullable Codec<T> networkCodec) {
   }
}
