package net.minecraftforge.common.world;

import java.util.List;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ModifiableStructureInfo {
   private final @NotNull StructureInfo originalStructureInfo;
   private @Nullable StructureInfo modifiedStructureInfo = null;

   public ModifiableStructureInfo(@NotNull StructureInfo originalStructureInfo) {
      this.originalStructureInfo = originalStructureInfo;
   }

   public @NotNull StructureInfo get() {
      return this.modifiedStructureInfo == null ? this.originalStructureInfo : this.modifiedStructureInfo;
   }

   public @NotNull StructureInfo getOriginalStructureInfo() {
      return this.originalStructureInfo;
   }

   public @Nullable StructureInfo getModifiedStructureInfo() {
      return this.modifiedStructureInfo;
   }

   @Internal
   public void applyStructureModifiers(Holder<Structure> structure, List<StructureModifier> structureModifiers) {
      if (this.modifiedStructureInfo != null) {
         throw new IllegalStateException(String.format(Locale.ENGLISH, "Structure %s already modified", structure));
      } else {
         StructureInfo original = this.getOriginalStructureInfo();
         StructureInfo.Builder builder = ModifiableStructureInfo.StructureInfo.Builder.copyOf(original);

         for(StructureModifier.Phase phase : StructureModifier.Phase.values()) {
            for(StructureModifier modifier : structureModifiers) {
               modifier.modify(structure, phase, builder);
            }
         }

         this.modifiedStructureInfo = builder.build();
      }
   }

   public static record StructureInfo(Structure.StructureSettings structureSettings) {
      public static class Builder {
         private StructureSettingsBuilder structureSettings;

         public static Builder copyOf(StructureInfo original) {
            StructureSettingsBuilder structureBuilder = StructureSettingsBuilder.copyOf(original.structureSettings());
            return new Builder(structureBuilder);
         }

         private Builder(StructureSettingsBuilder structureSettings) {
            this.structureSettings = structureSettings;
         }

         public StructureInfo build() {
            return new StructureInfo(this.structureSettings.build());
         }

         public StructureSettingsBuilder getStructureSettings() {
            return this.structureSettings;
         }
      }
   }
}
