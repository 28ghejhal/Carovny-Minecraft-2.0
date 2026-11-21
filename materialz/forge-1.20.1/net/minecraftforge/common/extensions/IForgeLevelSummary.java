package net.minecraftforge.common.extensions;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelSummary;

public interface IForgeLevelSummary {
   private LevelSummary self() {
      return (LevelSummary)this;
   }

   default boolean isLifecycleExperimental() {
      LevelSettings settings = this.self().getSettings();
      return settings != null && settings.getLifecycle().equals(Lifecycle.experimental());
   }
}
