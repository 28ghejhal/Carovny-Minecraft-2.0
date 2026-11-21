package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum PrioritizeChunkUpdates implements OptionEnum {
   NONE(0, "options.prioritizeChunkUpdates.none"),
   PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
   NEARBY(2, "options.prioritizeChunkUpdates.nearby");

   private static final IntFunction<PrioritizeChunkUpdates> BY_ID = ByIdMap.<PrioritizeChunkUpdates>continuous(PrioritizeChunkUpdates::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private PrioritizeChunkUpdates(int p_193784_, String p_193785_) {
      this.id = p_193784_;
      this.key = p_193785_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static PrioritizeChunkUpdates byId(int p_193788_) {
      return (PrioritizeChunkUpdates)BY_ID.apply(p_193788_);
   }

   // $FF: synthetic method
   private static PrioritizeChunkUpdates[] $values() {
      return new PrioritizeChunkUpdates[]{NONE, PLAYER_AFFECTED, NEARBY};
   }
}
