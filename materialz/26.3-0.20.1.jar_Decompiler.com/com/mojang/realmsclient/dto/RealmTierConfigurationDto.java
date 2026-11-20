package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record RealmTierConfigurationDto(RealmTierRangeDto renderDistance, RealmTierRangeDto simDistance) implements ReflectionBasedSerialization {
   @SerializedName("renderDistance")
   public RealmTierRangeDto renderDistance() {
      return this.renderDistance;
   }

   @SerializedName("simDistance")
   public RealmTierRangeDto simDistance() {
      return this.simDistance;
   }

   public static record RealmTierRangeDto(int min, int max, int defaultValue, @Nullable Integer current) implements ReflectionBasedSerialization {
      @SerializedName("min")
      public int min() {
         return this.min;
      }

      @SerializedName("max")
      public int max() {
         return this.max;
      }

      @SerializedName("defaultValue")
      public int defaultValue() {
         return this.defaultValue;
      }

      @SerializedName("current")
      public @Nullable Integer current() {
         return this.current;
      }
   }
}
