package net.minecraft.world.level.storage.loot;

import net.minecraft.resources.ResourceLocation;

public record LootDataId<T>(LootDataType<T> type, ResourceLocation location) {
   public LootDataId(LootDataType<T> type, ResourceLocation location) {
      this.type = type;
      this.location = location;
   }
}
